package com.qingqiu.aigeek.service.impl;


import static com.qingqiu.aigeek.constant.Common.REGISTER_CAPTCHA_TTL;
import static com.qingqiu.aigeek.constant.Common.USER_REGISTER_CAPTCHA_KEY;
import static com.qingqiu.aigeek.util.RegularUtil.checkMail;
import static com.qingqiu.aigeek.util.RegularUtil.checkPassword;
import static com.qingqiu.aigeek.util.RegularUtil.checkUserName;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.qingqiu.aigeek.enums.BusinessExceptionEnum;
import com.qingqiu.aigeek.exception.BusinessException;
import com.qingqiu.aigeek.domain.dto.UserLoginDTO;
import com.qingqiu.aigeek.domain.dto.UserRegisterDTO;
import com.qingqiu.aigeek.domain.dto.UserRegisterMailDTO;
import com.qingqiu.aigeek.domain.entity.User;
import com.qingqiu.aigeek.domain.vo.LoginUserVO;
import com.qingqiu.aigeek.service.UserService;
import com.qingqiu.aigeek.util.IPUtil;
import com.qingqiu.aigeek.util.MailUtil;
import com.qingqiu.aigeek.util.UserContext;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

/**
 * @author 懒大王Smile
 * @description 针对表【user(用户表)】的数据库操作Service实现
 * @createDate 2024-09-12 22:19:13
 */
@Service
@Slf4j
public class UserServiceImpl implements UserService {

  @Resource
  private JavaMailSenderImpl mailSender;
  
  @Resource
  private StringRedisTemplate stringRedisTemplate;

  @Value("${spring.mail.username}")
  private String fromEmail;
  
  /**
   * @return
   */
  @Override
  public LoginUserVO login(UserLoginDTO userLogin) {
    //1.判断邮箱和密码是否为空,邮箱格式校验，密码长度校验
    String loginUserMail = userLogin.getMail();
    String loginPassword = userLogin.getPassword();
    if (StringUtils.isAnyBlank(loginUserMail, loginPassword) || checkMail(loginUserMail)
        || checkPassword(loginPassword)) {
      throw new BusinessException(BusinessExceptionEnum.PARAMS_ERROR.getCode(), BusinessExceptionEnum.PARAMS_ERROR.getMessage());
    }

    //2.根据邮箱从数据库查询用户
//    User queryUser = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getMail, encrypt(loginUserMail)));
//    if (queryUser == null) {
//      throw new BusinessException(BusinessExceptionEnum.NOT_REGISTER_ERROR.getCode(),BusinessExceptionEnum.NOT_REGISTER_ERROR.getMessage());
//    }
//    if (queryUser.getStatus() == 0) {
//      throw new BusinessException(BusinessExceptionEnum.FORBIDDEN_ERROR.getCode(), BusinessExceptionEnum.FORBIDDEN_ERROR.getMessage());
//    }

    //3.核对密码
//    {
//      if (!encrypt(loginPassword).equals(queryUser.getPassword())) {
//        throw new BusinessException(BusinessExceptionEnum.LOGIN_PARAMS_ERROR.getCode(), BusinessExceptionEnum.LOGIN_PARAMS_ERROR.getMessage());
//      }
//    }

//    log.info("用户登录，userID:{},ip:{}",queryUser.getUserId(),IPUtil.getIpAddr());
//    if (!queryUser.getIpAddress().equals(ipAddr)) {
      //如果用户id地址变化，那么更新数据库
//      userMapper.updateIpAddress(ipAddr, queryUser.getUserId());
//    }
//    return loginSuccess(queryUser.getRole(), queryUser.getUserId());
    return null;
  }

  /**
   * 传入参数，使用参数本身作为盐值 进行加密
   *
   * @param str
   * @return
   */
  public String encrypt(String str) {
    return DigestUtils.md5DigestAsHex((str + str).getBytes());
  }

  /**
   * @param role
   */
  private LoginUserVO loginSuccess(String role, Long userId) {
    LoginUserVO loginUserVO = new LoginUserVO();
//    loginUserVO.setUserId(userId);
//    loginUserVO.setAvatarUrl(userMapper.getUserAvatar(userId));
//    UserContext.saveUser(userId);
    StpUtil.login(userId);
    return loginUserVO;
  }

  @Async("taskExecutor")
  public Boolean sendCodeForRegister(String email) {
    log.info("尝试发送邮箱验证码给用户：" + email + "进行注册操作");
    log.info("开始发送邮件..." + "获取的到邮件发送对象为:" + mailSender);
    MailUtil mailUtil = new MailUtil(mailSender, fromEmail);
    String code = mailUtil.sendCode(email);
    //验证码存入redis，有效期1min,用注册的邮箱区分验证码
    stringRedisTemplate.opsForValue()
        .set(USER_REGISTER_CAPTCHA_KEY + encrypt(email), code, REGISTER_CAPTCHA_TTL, TimeUnit.MINUTES);
    log.info("发送邮箱验证码给用户：" + email + "成功 : " + code);
    return StringUtils.isNotBlank(code);
  }

  @Override
  public Boolean sendRegisterCode(UserRegisterMailDTO userRegister) {
    String mail = userRegister.getMail();
    //检查该邮箱是否已注册
    checkMailIsRegistered(mail);
    //发送验证码
    return sendCodeForRegister(mail);
  }

  /**
   * @return
   */
  @Override
  public LoginUserVO register(UserRegisterDTO userRegister) {

    String mail = userRegister.getMail();
    String password = userRegister.getPassword();
    String rePassword = userRegister.getRePassword();
    String userName = userRegister.getUserName();
    String registerCode = userRegister.getCode();

    //检查注册参数是否为空
    if (StringUtils.isAnyBlank(mail, password, rePassword, userName, registerCode)) {
      throw new BusinessException(BusinessExceptionEnum.PARAMS_ERROR.getCode(), BusinessExceptionEnum.PARAMS_ERROR.getMessage());
    }

    // 检查参数是否合法
    if (!password.equals(rePassword) || checkMail(mail) || checkUserName(userName) || checkPassword(
        password)) {
      throw new BusinessException(BusinessExceptionEnum.PARAMS_ERROR.getCode(),  BusinessExceptionEnum.PARAMS_ERROR.getMessage());
    }

    //检查邮箱是否被注册
    checkMailIsRegistered(mail);

    //从redis获取验证码
    String rightCode = stringRedisTemplate.opsForValue().get(USER_REGISTER_CAPTCHA_KEY + encrypt(mail));

    //检查验证码是否存在
    if (StringUtils.isBlank(rightCode)) {
      throw new BusinessException(BusinessExceptionEnum.CAPTCHA_ERROR.getCode(),  BusinessExceptionEnum.CAPTCHA_ERROR.getMessage());
    }

    //核验验证码是否正确
    if (!rightCode.equals(registerCode)) {
      throw new BusinessException(BusinessExceptionEnum.CAPTCHA_ERROR.getCode(),   BusinessExceptionEnum.CAPTCHA_ERROR.getMessage());
    }
    //4.密码盐值加密，写入数据库，注册成功
    log.info("用户注册，ip地址：{}", IPUtil.getIpAddr());
//    User user = User.builder().userName(userName).password(encrypt(password)).mail(encrypt(mail)).ipAddress(ipAddr)
//        .build();
//    int insert = userMapper.insert(user);
//    if (insert <= 0) {
//      throw new BusinessException(BusinessExceptionEnum.REGISTER_ERROR.getCode(), BusinessExceptionEnum.REGISTER_ERROR.getMessage());
//    }
//    return loginSuccess(userMapper.getUserRole(user.getUserId()), user.getUserId());
    return null;
  }

  private void checkMailIsRegistered(String mail) {
    //发送验证码时已检查该邮箱是否注册，这里再检查一次
    LambdaUpdateWrapper<User> userLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
    userLambdaUpdateWrapper.eq(User::getMail, encrypt(mail));
//    if (userMapper.exists(userLambdaUpdateWrapper)) {
//      throw new BusinessException(BusinessExceptionEnum.ACCOUNT_EXISTED_ERROR.getCode(), BusinessExceptionEnum.ACCOUNT_EXISTED_ERROR.getMessage());
//    }
  }


  /**
   * @return
   */
  @Override
  public Boolean logout(HttpServletRequest httpServletRequest) {
    try {
      StpUtil.logout();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
//    //获取请求头中的token，这是用户登录时生成的uuid
//    String token = httpServletRequest.getHeader(AUTHORIZATION);
//    if (StringUtils.isBlank(token)) {
//      throw new BusinessException(ErrorCode.PARAMS_ERROR, "无效的token");
//    }
//    String tokenKey = Common.LOGIN_TOKEN_KEY + token;
//    return stringRedisTemplate.delete(tokenKey);
    return true;
  }


  @Override
  public LoginUserVO getLoginUser() {
    Long userId = UserContext.getUser();
    if (userId == null) {
      throw new BusinessException(BusinessExceptionEnum.NOT_LOGIN_ERROR.getCode(),BusinessExceptionEnum.NOT_LOGIN_ERROR.getMessage());
    }
    //获取数据库最新的数据，防止用户更新完个人信息后拿到的还是老数据
//    User user = userMapper.selectById(userId);
//    LoginUserVO loginUserVO = new LoginUserVO();
//    BeanUtils.copyProperties(user, loginUserVO);
//    String ipAddress = user.getIpAddress();
//    String ipRegion = IPUtil.getIpRegion(ipAddress);
//    loginUserVO.setIpAddress(ipRegion);
    return null;
  }
}




