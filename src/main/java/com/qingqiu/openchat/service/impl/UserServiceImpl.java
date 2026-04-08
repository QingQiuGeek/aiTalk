package com.qingqiu.openchat.service.impl;


import static com.qingqiu.openchat.constant.Common.REGISTER_CAPTCHA_TTL;
import static com.qingqiu.openchat.constant.Common.USER_REGISTER_CAPTCHA_KEY;
import static com.qingqiu.openchat.util.RegularUtil.checkMail;
import static com.qingqiu.openchat.util.RegularUtil.checkPassword;

import cn.dev33.satoken.stp.StpUtil;
import com.mybatisflex.core.query.QueryWrapper;
import com.qingqiu.openchat.mapper.UserMapper;
import com.qingqiu.openchat.enums.BizExceptionEnum;
import com.qingqiu.openchat.exception.BizException;
import com.qingqiu.openchat.domain.dto.UserLoginDTO;
import com.qingqiu.openchat.domain.dto.UserRegisterDTO;
import com.qingqiu.openchat.domain.dto.UserRegisterMailDTO;
import com.qingqiu.openchat.domain.entity.User;
import com.qingqiu.openchat.domain.vo.LoginUserVO;
import com.qingqiu.openchat.service.UserService;
import com.qingqiu.openchat.util.MailUtil;
import com.qingqiu.openchat.util.UserContext;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

/**
 * @author Qing Qiu
 * @description
 * @createDate
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

  @Resource
  UserMapper userMapper;

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
      throw new BizException(BizExceptionEnum.PARAMS_ERROR.getCode(), BizExceptionEnum.PARAMS_ERROR.getMessage());
    }

    //2.根据邮箱从数据库查询用户
    QueryWrapper queryWrapper = new QueryWrapper();
    queryWrapper.eq(User::getMail, encrypt(loginUserMail));
    User queryUser = userMapper.selectOneByQuery(queryWrapper);
    if (queryUser == null) {
      throw new BizException(BizExceptionEnum.NOT_REGISTER_ERROR.getCode(), BizExceptionEnum.NOT_REGISTER_ERROR.getMessage());
    }
    if (queryUser.getStatus() == 0) {
      throw new BizException(BizExceptionEnum.FORBIDDEN_ERROR.getCode(), BizExceptionEnum.FORBIDDEN_ERROR.getMessage());
    }

    //3.核对密码
      if (!encrypt(loginPassword).equals(queryUser.getPassword())) {
        throw new BizException(BizExceptionEnum.LOGIN_PARAMS_ERROR.getCode(), BizExceptionEnum.LOGIN_PARAMS_ERROR.getMessage());
      }

    return loginSuccess(queryUser.getRole(), queryUser.getUserId());
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
    loginUserVO.setUserId(userId);
    UserContext.saveUser(userId);
    StpUtil.login(userId);
    return loginUserVO;
  }

  @Override
  public Boolean sendRegisterCode(UserRegisterMailDTO userRegister) {
    String mail = userRegister.getMail();
    //检查该邮箱是否已注册
    checkMailIsRegistered(mail);
    //发送验证码
    log.info("尝试发送邮箱验证码给用户：{}进行注册操作", mail);
    log.info("开始发送邮件...获取的到邮件发送对象为:{}", mailSender);
    MailUtil mailUtil = new MailUtil(mailSender, fromEmail);
    String code = mailUtil.sendCode(mail);
    //验证码存入redis，有效期1min,用注册的邮箱区分验证码
    stringRedisTemplate.opsForValue()
        .set(USER_REGISTER_CAPTCHA_KEY + encrypt(mail), code, REGISTER_CAPTCHA_TTL, TimeUnit.MINUTES);
    log.info("发送邮箱验证码给用户：{}成功 : {}", mail, code);
    return StringUtils.isNotBlank(code);
  }

  /**
   * @return
   */
  @Override
  public LoginUserVO register(UserRegisterDTO userRegister) {
    String mail = userRegister.getMail();
    String password = userRegister.getPassword();
    String rePassword = userRegister.getRePassword();
    String registerCode = userRegister.getCode();

    //检查注册参数是否为空
    if (StringUtils.isAnyBlank(mail, password, rePassword, registerCode)) {
      throw new BizException(BizExceptionEnum.PARAMS_ERROR.getCode(), BizExceptionEnum.PARAMS_ERROR.getMessage());
    }

    // 检查参数是否合法
    if (!password.equals(rePassword) || checkMail(mail) || checkPassword(password)) {
      throw new BizException(BizExceptionEnum.PARAMS_ERROR.getCode(),  BizExceptionEnum.PARAMS_ERROR.getMessage());
    }

    //检查邮箱是否被注册
    checkMailIsRegistered(mail);

    //从redis获取验证码
    String rightCode = stringRedisTemplate.opsForValue().get(USER_REGISTER_CAPTCHA_KEY + encrypt(mail));

    //检查验证码是否存在
    if (StringUtils.isBlank(rightCode)) {
      throw new BizException(BizExceptionEnum.CAPTCHA_ERROR.getCode(),  BizExceptionEnum.CAPTCHA_ERROR.getMessage());
    }

    //核验验证码是否正确
    if (!rightCode.equals(registerCode)) {
      throw new BizException(BizExceptionEnum.CAPTCHA_ERROR.getCode(),   BizExceptionEnum.CAPTCHA_ERROR.getMessage());
    }
    //4.密码盐值加密，写入数据库，注册成功
    User user = User.builder().password(encrypt(password)).mail(encrypt(mail)).build();
    int insert = userMapper.insert(user);
    if (insert <= 0) {
      throw new BizException(BizExceptionEnum.REGISTER_ERROR.getCode(), BizExceptionEnum.REGISTER_ERROR.getMessage());
    }
    return loginSuccess(user.getRole(), user.getUserId());
  }

  private void checkMailIsRegistered(String mail) {
    //发送验证码时已检查该邮箱是否注册，这里再检查一次
    QueryWrapper queryWrapper = new QueryWrapper();
    queryWrapper.eq(User::getMail,encrypt(mail));
    User user = userMapper.selectOneByQuery(queryWrapper);
    if (Objects.isNull(user)) {
      throw new BizException(
          BizExceptionEnum.ACCOUNT_EXISTED_ERROR.getCode(), BizExceptionEnum.ACCOUNT_EXISTED_ERROR.getMessage());
    }
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
      throw new BizException(BizExceptionEnum.NOT_LOGIN_ERROR.getCode(), BizExceptionEnum.NOT_LOGIN_ERROR.getMessage());
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




