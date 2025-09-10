# DiffLight - 交通流量数据分析平台

DiffLight 是一个基于 Vue 3 + Spring Boot 的前后端分离交通流量数据分析平台，提供数据集管理、参数配置、结果展示等功能。

## 🎯 功能特性

### 用户功能
- **用户认证**：注册、登录、个人信息管理
- **数据集管理**：上传、下载、管理个人数据集
- **参数设置**：配置分析参数，查看结果图表
- **结果展示**：轮播图展示填补结果图、加噪过程图、去噪过程图

### 管理员功能
- **数据集管理**：管理所有用户数据集，上传共享数据集
- **用户管理**：查看、编辑用户信息，重置密码
- **参数管理**：维护参数选项，管理参数组合与结果图片映射

## 🏗️ 技术架构

### 前端技术栈
- **Vue 3** - 渐进式 JavaScript 框架
- **Vite** - 现代化构建工具
- **Element Plus** - Vue 3 UI 组件库
- **Vue Router** - 官方路由管理器
- **Pinia** - Vue 3 状态管理
- **Axios** - HTTP 客户端

### 后端技术栈
- **Spring Boot 3** - Java 企业级应用框架
- **Spring Security** - 安全框架，JWT 鉴权
- **Spring Data JPA** - 数据访问层
- **MySQL 8** - 关系型数据库
- **Swagger/OpenAPI** - API 文档生成
- **Lombok** - Java 代码简化

### 部署技术
- **Docker & Docker Compose** - 容器化部署
- **Nginx** - 反向代理和静态文件服务

## 🚀 快速开始

### 环境要求
- Java 17+
- Node.js 18+
- MySQL 8.0+
- Docker & Docker Compose (可选)

### 使用 Docker 部署 (推荐)

1. **克隆项目**
```bash
git clone https://github.com/20040120lxs/traffic.git
cd traffic
```

2. **构建后端**
```bash
cd backend
mvn clean package -DskipTests
cd ..
```

3. **构建前端**
```bash
cd frontend
npm install
npm run build
cd ..
```

4. **启动服务**
```bash
docker-compose up -d
```

5. **访问应用**
- 前端：http://localhost:3000
- 后端 API：http://localhost:8080
- Swagger 文档：http://localhost:8080/swagger-ui.html

### 本地开发部署

#### 1. 数据库设置
```sql
CREATE DATABASE traffic_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

#### 2. 后端启动
```bash
cd backend

# 配置环境变量 (可选，使用 .env 文件)
cp ../.env.example .env

# 启动应用
mvn spring-boot:run
```

#### 3. 前端启动
```bash
cd frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

#### 4. 访问应用
- 前端开发服务器：http://localhost:3000
- 后端 API：http://localhost:8080
- Swagger 文档：http://localhost:8080/swagger-ui.html

## 📋 默认账号

### 管理员账号
- **用户名**：admin
- **密码**：Admin@123
- **手机号**：13800000000

### 参数选项
系统预置了以下参数选项：
- **交通文件**：hangzhou, jinan
- **路网文件**：roadnet_3_4, roadnet_4_4
- **交叉口数量**：16, 12
- **缺失模式**：random_missing, kriging missing
- **缺失率**：10%, 30%, 50%, 6.25%, 12.5%, 18.75%, 8.33%, 16.67%, 25%

## 🗂️ 项目结构

```
traffic/
├── backend/                    # Spring Boot 后端
│   ├── src/main/java/
│   │   └── com/traffic/difflight/
│   │       ├── config/         # 配置类
│   │       ├── controller/     # 控制器
│   │       ├── dto/           # 数据传输对象
│   │       ├── entity/        # 实体类
│   │       ├── repository/    # 数据访问层
│   │       ├── security/      # 安全配置
│   │       └── service/       # 业务逻辑层
│   ├── src/main/resources/
│   │   └── application.yml    # 应用配置
│   ├── Dockerfile
│   └── pom.xml
├── frontend/                   # Vue 3 前端
│   ├── src/
│   │   ├── components/        # 可复用组件
│   │   ├── router/           # 路由配置
│   │   ├── stores/           # Pinia 状态管理
│   │   ├── utils/            # 工具类
│   │   ├── views/            # 页面组件
│   │   │   └── admin/        # 管理员页面
│   │   └── App.vue
│   ├── Dockerfile
│   ├── nginx.conf
│   ├── package.json
│   └── vite.config.js
├── uploads/                    # 文件上传目录
│   ├── datasets/              # 数据集文件
│   └── results/               # 结果图片
├── docker-compose.yml          # Docker 编排文件
├── init.sql                   # 数据库初始化脚本
├── .env.example               # 环境变量示例
└── README.md
```

## 📖 API 文档

### 认证相关
- `POST /api/auth/register` - 用户注册
- `POST /api/auth/login` - 用户登录
- `POST /api/admin/login` - 管理员登录
- `GET /api/auth/me` - 获取当前用户信息
- `PUT /api/auth/me` - 更新用户信息

### 数据集管理
- `POST /api/datasets/upload` - 上传数据集
- `GET /api/datasets/my` - 获取我的数据集
- `GET /api/datasets/shared` - 获取共享数据集
- `GET /api/datasets/{id}/download` - 下载数据集
- `DELETE /api/datasets/{id}` - 删除数据集

### 参数设置
- `GET /api/params/options` - 获取参数选项
- `POST /api/params/query` - 查询参数结果

### 管理员功能
- `GET /api/admin/datasets` - 获取所有数据集
- `POST /api/admin/datasets/upload` - 上传共享数据集
- `DELETE /api/admin/datasets/{id}` - 删除任意数据集
- `POST /api/admin/params/options` - 添加参数选项
- `DELETE /api/admin/params/options` - 删除参数选项
- `POST /api/admin/results` - 上传结果图片
- `GET /api/admin/results` - 获取所有结果
- `DELETE /api/admin/results/{id}` - 删除结果

完整的 API 文档可访问：http://localhost:8080/swagger-ui.html

## 🔧 配置说明

### 环境变量

主要的环境变量配置项：

| 变量名 | 说明 | 默认值 |
|--------|------|--------|
| DB_HOST | 数据库主机 | localhost |
| DB_PORT | 数据库端口 | 3306 |
| DB_NAME | 数据库名称 | traffic_db |
| DB_USERNAME | 数据库用户名 | root |
| DB_PASSWORD | 数据库密码 | root123 |
| JWT_SECRET | JWT 密钥 | mySecretKey... |
| JWT_EXPIRATION | JWT 过期时间(毫秒) | 86400000 |
| FILE_UPLOAD_DIR | 文件上传目录 | uploads |
| SERVER_PORT | 服务器端口 | 8080 |

### 参数组合映射规则

根据需求，系统支持以下有效的参数组合：

1. **hangzhou + roadnet_4_4 + 16**
   - random_missing: 10%, 30%, 50%
   - kriging missing: 6.25%, 12.5%, 18.75%

2. **jinan + roadnet_3_4 + 16**
   - random_missing: 10%, 30%, 50%
   - kriging missing: 8.33%, 16.67%, 25%

每种有效组合对应 3 张图片：填补结果图、加噪过程图、去噪过程图。

## 🛠️ 开发指南

### 添加新的参数选项
1. 使用管理员账号登录
2. 通过 API 或数据库直接添加到 `param_options` 表
3. 前端会自动获取最新的参数选项

### 上传结果图片
1. 使用管理员账号登录
2. 访问管理员功能（需要完善后端接口）
3. 选择参数组合并上传对应的三张图片

### 扩展功能
- 修改 `/backend/src/main/java/com/traffic/difflight/` 下的相应类
- 修改 `/frontend/src/` 下的对应组件
- 更新 API 文档

## 🔍 故障排除

### 常见问题

1. **数据库连接失败**
   - 检查 MySQL 服务是否启动
   - 验证数据库连接配置
   - 确认数据库用户权限

2. **文件上传失败**
   - 检查 `uploads` 目录权限
   - 确认文件大小不超过 100MB
   - 验证文件格式是否支持

3. **JWT 认证失败**
   - 检查 JWT 密钥配置
   - 确认 token 未过期
   - 验证请求头格式

4. **前端代理失败**
   - 检查后端服务是否启动在 8080 端口
   - 验证 vite.config.js 中的代理配置
   - 确认 CORS 配置正确

### 日志查看

- **后端日志**：查看 Spring Boot 控制台输出
- **前端日志**：查看浏览器开发者工具 Console
- **Docker 日志**：`docker-compose logs [service_name]`

## 📄 许可证

[MIT License](LICENSE)

## 🤝 贡献

欢迎提交 Issue 和 Pull Request 来改进项目。

## 📞 联系方式

如有问题，请通过 GitHub Issues 联系我们。