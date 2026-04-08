CREATE DATABASE openchat;
\connect   openchat;
CREATE EXTENSION IF NOT EXISTS vector;
CREATE TABLE "user" (
  user_id BIGINT not null PRIMARY KEY,
   role varchar(20) NOT NULL,                     -- 角色（admin/user）
   mail varchar(50) NOT NULL UNIQUE,          -- 邮箱
   phone varchar(20) UNIQUE,                     -- 电话（可选）
   password varchar(20) NOT NULL,           -- 密码（实际项目中应加密存储）
  user_name varchar(20) NOT NULL,
  status int NOT NULL default 0,               -- 账户状态（0-正常，1-禁用）
  created_at TIMESTAMP DEFAULT NOW(),
  updated_at TIMESTAMP DEFAULT NOW(),
   is_deleted int DEFAULT 0
);

CREATE TABLE agent (
    agent_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id BIGINT not null ,
    name varchar(20) NOT NULL,                    -- Agent 名称
    description varchar(50),                      -- 描述（用户可见）
    system_prompt TEXT,                    -- 系统指令
    model_id bigint,                            -- 默认使用的模型
    allowed_tools JSONB,                   -- 允许使用的工具列表
    allowed_kbs JSONB,                     -- 允许访问的知识库
    chat_options JSONB,                    -- 其它配置项（温度、top_p、最大token）
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    is_deleted int DEFAULT 0

);

CREATE TABLE model_provider (
   model_id bigint PRIMARY KEY ,
   user_id BIGINT NOT NULL,             -- 所属用户
   model_name VARCHAR(20),             -- 实际模型标识 (如 "gpt-3.5-turbo", "qwen-turbo")
   provider_type VARCHAR(20),           -- 厂商类型 (如 "openai", "anthropic", "ollama", "custom")
   base_url VARCHAR(255) NOT NULL,      -- API 地址 (如 https://api.openai.com/v1)
   api_key VARCHAR(255) NOT NULL,       -- API Key (生产环境建议加密存储)
   max_tokens INTEGER,                  -- 该模型的最大上下文窗口
   created_at TIMESTAMP DEFAULT NOW(),
   updated_at TIMESTAMP  DEFAULT NOW(),
    is_deleted int DEFAULT 0
);

-- 普通会话
CREATE TABLE chat_session (
    chat_session_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    agent_id UUID ,  -- 所属 Agent会话，可为空
    title varchar(20),                          -- 自动生成的标题
    metadata JSONB,                      -- 扩展（例如输入语言、设备类型）
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    is_deleted int DEFAULT 0

);

CREATE TABLE chat_message (
    chat_message_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    chat_session_id UUID NOT NULL ,
    role varchar(20) NOT NULL,                      -- user / assistant / system / tool
    content TEXT,                            -- 主体内容
    metadata JSONB,                          -- 工具调用、RAG 片段、模型参数等
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    is_deleted int DEFAULT 0
);

CREATE TABLE knowledge_base (
#     TODO 加agentID或者userID
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name varchar(20) NOT NULL,
    description varchar(50),
    metadata JSONB,                         -- 业务属性，如行业/标签
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    is_deleted int DEFAULT 0
);

CREATE TABLE document (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    kb_id UUID NOT NULL REFERENCES knowledge_base(id) ON DELETE CASCADE,
    filename varchar(20) NOT NULL,
    filetype varchar(10),                          -- pdf / md / txt 等
    size BIGINT,                            -- 文件大小
    metadata JSONB,                         -- 页数、上传方式、解析参数等
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    is_deleted int DEFAULT 0
);

CREATE TABLE chunk_bge_m3 (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    kb_id UUID NOT NULL REFERENCES knowledge_base(id) ON DELETE CASCADE,
    doc_id UUID NOT NULL REFERENCES document(id) ON DELETE CASCADE,
    content TEXT NOT NULL,                  -- 切片后的文本内容
    metadata JSONB,                         -- 页码、段落号、chunk index 等
    embedding VECTOR(1024) NOT NULL,        -- bge_m3 模型是 1024 维的向量
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    is_deleted int DEFAULT 0
);

-- 给向量加索引
CREATE INDEX idx_chunk_embedding
ON chunk_bge_m3
USING ivfflat (embedding vector_l2_ops)
WITH (lists = 100);
