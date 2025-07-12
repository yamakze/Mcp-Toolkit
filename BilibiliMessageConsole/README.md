# 📬 Bilibili 消息控制台

**BilibiliMessageConsole** 是一个基于 [Model Context Protocol (MCP)](https://github.com/modelcontextprotocol/spec) 构建的消息控制服务，  
用于获取并批量回复 Bilibili 用户消息，旨在为 AI 智能体提供稳定、可控、自动化的消息处理能力。

---

## 🧰 工具列表（MCP Tools）

### 🔍 获取待回复消息 - `getReviews`

- **名称**：`getReviews`  
- **描述**：Get the latest messages that need to be replied to.

#### ✅ 响应结构

```json
[
  {
    "messageId": 12345678,
    "messageContent": "你好，请问你的视频会更新吗？"
  }
]
````

#### ⚙️ 内部逻辑

* 从已收集消息列表中提取前 K 条未处理评论
* 自动清洗内容：去除 `@用户名` / `回复 @xxx:` 等无意义信息

---

### 💬 批量回复消息 - `replyToMessage`

* **名称**：`replyToMessage`
* **描述**：Batch send a reply to a specific message by ID.

#### 📝 请求结构

```json
[
  {
    "messageId": 12345678,
    "replyContent": "会的哦，感谢支持！"
  }
]
```

#### ⚙️ 内部逻辑

* 使用 `@Async` 注解，异步执行回复流程
* 每条消息发送后延迟 500ms，避免限流触发
* 成功回复后自动从待回复列表中移除该消息

---

## 🚀 快速接入指南

请将以下内容添加到你的 MCP 客户端配置中，以便通过 Stdio 启动：

> 以下两项 **二选一** 必填：
>
> * `--bilibili.api.cookie-file-path`：传入一个 JSON 格式的 Cookie 文件路径
> * `--bilibili.api.cookie-value`：直接传入字符串形式的 Cookie 值

```jsonc
{
  "mcpServers": {
    "mcp-server-bilibili": {
      "command": "java",
      "args": [
        "-Dspring.ai.mcp.server.stdio=true",
        "-Dspring.main.web-application-type=none",
        "-jar",
        "{ABSOLUTE_PATH}/BilibiliMessageConsole-1.0.jar",
        "--bilibili.api.cookie-file-path={your_cookie.json}",
        "--bilibili.api.cookie-value={your_cookie_value}"
      ]
    }
  }
}
```

---

## ⚙️ 可选参数说明

| 参数名                    | 默认值            | 描述                             |
| ---------------------- |----------------| ------------------------------ |
| `--cursor-timestamp`   | `0`            | 基准时间戳（单位：秒），仅收集时间晚于该值的消息，并自动更新 |
| `--collect-cron`       | `0/10 * * * * *` | 消息收集的 Cron 表达式（建议不要低于 10 秒）    |
| `--max-retrieve-count` | `5`            | 每次 `getReviews` 返回的最大消息数量      |

---

## 📜  许可证

本项目采用 [MIT License](https://opensource.org/licenses/MIT) 开源协议。
可自由使用、修改、发布，但须遵守 MIT 许可证的条款和条件！





