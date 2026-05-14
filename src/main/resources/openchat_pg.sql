CREATE DATABASE openchat;
\connect   openchat;
CREATE EXTENSION IF NOT EXISTS vector;
CREATE TABLE "user" (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
   role varchar(10) NOT NULL,                     -- 角色（admin/user）
   mail varchar(100) NOT NULL UNIQUE,          -- 邮箱
   phone varchar(100) UNIQUE,                     -- 电话（可选）
   password varchar(100) ,           -- 密码（实际项目中应加密存储）
  user_name varchar(20) ,
  status int NOT NULL default 0,               -- 账户状态（0-正常，1-禁用）
  created_at TIMESTAMP not null DEFAULT NOW(),
  updated_at TIMESTAMP not null DEFAULT NOW(),
   is_deleted int DEFAULT 0
);

CREATE TABLE agent (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY ,
    user_id BIGINT not null ,
    name varchar(20) NOT NULL,                    -- Agent 名称
    description varchar(50),                      -- 描述（用户可见）
    system_prompt TEXT,                    -- 系统指令
    model_provider_id bigint,                            -- 默认使用的模型
    allowed_tools JSONB,                   -- 允许使用的工具列表
    allowed_kbs JSONB,                     -- 允许访问的知识库
    chat_options JSONB,                    -- 其它配置项（温度、top_p、最大token）
    created_at TIMESTAMP not null DEFAULT NOW(),
    updated_at TIMESTAMP not null DEFAULT NOW(),
    is_deleted int DEFAULT 0
);

CREATE TABLE model_provider (
   id bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY ,
   user_id BIGINT NOT NULL,             -- 所属用户
   model_name VARCHAR(20) NOT NULL,             -- 实际模型标识 (如 "gpt-3.5-turbo", "qwen-turbo")
   provider_type VARCHAR(20) ,           -- 厂商类型 (如 "openai", "anthropic", "ollama", "custom")
   base_url VARCHAR(255) NOT NULL,      -- API 地址 (如 https://api.openai.com/v1)
   api_key VARCHAR(255) NOT NULL,       -- API Key (生产环境建议加密存储)
   max_tokens INTEGER,                  -- 该模型的最大上下文窗口
   created_at TIMESTAMP NOT NULL DEFAULT NOW(),
   updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    is_deleted int DEFAULT 0
);

-- 普通会话
CREATE TABLE chat_session (
    id varchar(100) PRIMARY KEY , --uuid字符串
    agent_id bigint ,  -- 所属 Agent会话，可为空
    title varchar(20) not null ,                          -- 自动生成的标题
    metadata JSONB,                      -- 扩展（例如输入语言、设备类型）
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    is_deleted int DEFAULT 0
);

CREATE TABLE chat_message (
    id varchar(100) PRIMARY KEY, --uuid字符串
    chat_session_id varchar(50) NOT NULL ,
    role varchar(10) NOT NULL,                      -- user / assistant / system / tool
    content TEXT not null ,                            -- 主体内容
    metadata JSONB,                          -- 工具调用、RAG 片段、模型参数等
    created_at TIMESTAMP not null  DEFAULT NOW(),
    updated_at TIMESTAMP not null  DEFAULT NOW(),
    is_deleted int DEFAULT 0
);

CREATE TABLE knowledge_base (
    id bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY ,
    user_id bigint NOT NULL,
    name varchar(20) NOT NULL,
    description varchar(50) not null ,
    metadata JSONB,                         -- 业务属性，如行业/标签
    created_at TIMESTAMP not null  DEFAULT NOW(),
    updated_at TIMESTAMP not null  DEFAULT NOW(),
    is_deleted int DEFAULT 0
);

CREATE TABLE document (
    id bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY ,
    kb_id bigint NOT NULL ,
    filename varchar(20) NOT NULL,
    filetype varchar(10),                          -- pdf / md / txt 等
    size BIGINT,                            -- 文件大小，存字节
    metadata JSONB,                         -- 页数、上传方式、解析参数等
    created_at TIMESTAMP not null DEFAULT NOW(),
    updated_at TIMESTAMP not null DEFAULT NOW(),
    is_deleted int DEFAULT 0
);

CREATE TABLE chunk_bge_m3 (
    id bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY ,
    kb_id bigint NOT NULL ,
    doc_id bigint NOT NULL ,
    content TEXT NOT NULL,                  -- 切片后的文本内容
    metadata JSONB,                         -- 页码、段落号、chunk index 等
    embedding VECTOR(1024) NOT NULL,        -- bge_m3 模型是 1024 维的向量
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    is_deleted int DEFAULT 0
);

-- 给向量加索引
CREATE INDEX idx_chunk_embedding
ON chunk_bge_m3
USING ivfflat (embedding vector_l2_ops)
WITH (lists = 100);
