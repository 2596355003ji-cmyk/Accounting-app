# GitHub 项目管理办法（新手版）

## 1. 只记住一条主流程

```text
想法/问题 → GitHub Issue → 开发分支 → Pull Request → 自动测试 → 合入 main → 版本发布
```

`main` 始终代表当前可构建版本。日常开发不直接改 `main`，每次只完成一个小目标。

## 2. GitHub 中每种东西的用途

| 名称 | 可以理解为 | 本项目用途 |
|---|---|---|
| Repository | 项目文件柜 | 保存代码、文档和版本历史 |
| Issue | 任务单/问题单 | 一个功能、Bug 或调研对应一个 Issue |
| Branch | 安全的工作副本 | 修改不会立刻影响主版本 |
| Pull Request | 合并申请 | 展示改了什么、测试是否通过 |
| Project | 看板 | 查看待办、进行中和已完成 |
| Milestone | 阶段目标 | 把 Issues 归到 M0、M1、M2 等版本 |
| Actions | 自动检查员 | 自动构建和运行测试 |
| Release | 可下载版本 | 发布 APK、更新说明和版本号 |

## 3. 项目阶段

### M0：需求与可运行原型

- 确定页面结构和产品范围；
- 创建 Android 工程；
- 能在模拟器或真机打开；
- 手动记账原型可操作。

### M1：本地账本

- Room 数据库；
- 账户、分类、交易增删改查；
- 首页、明细和基础统计；
- 数据持久化测试。

### M2：通知自动记账

- 通知权限引导；
- 微信、支付宝脱敏测试样本；
- 解析、待确认、去重和撤销；
- 真机验证与失败诊断。

### M3：可测试版本

- 导入和导出；
- 隐私说明和权限说明；
- 数据库升级测试；
- 生成首个可下载 APK。

### M4：公开测试

- 多品牌手机兼容测试；
- 崩溃和性能修复；
- 商店政策复核；
- `v0.1.0` Release。

## 4. 看板设置

建议创建一个 GitHub Project，使用五列：

1. `Backlog`：以后可能做；
2. `Ready`：需求清楚，可以开始；
3. `In progress`：正在做，每次最多 1～2 项；
4. `Review`：代码完成，等待检查；
5. `Done`：验收完成。

对个人项目来说，限制“进行中”的数量比建立复杂流程更重要。

## 5. Issue 规则

- 一个 Issue 只描述一个可验收目标；
- 标题写结果，例如“支持新增一笔支出”，不要写“优化一下”；
- Issue 必须写清楚用户场景和验收标准；
- 新想法先放 `Backlog`，不要中断当前任务；
- Bug 附复现步骤，但不得上传真实账单和敏感通知。

建议标签：

| 标签 | 含义 |
|---|---|
| `type:feature` | 新功能 |
| `type:bug` | 缺陷 |
| `type:research` | 调研或技术验证 |
| `area:ui` | 页面和交互 |
| `area:ledger` | 账本和统计 |
| `area:autobook` | 自动识别 |
| `area:privacy` | 权限与隐私 |
| `priority:p0` | 阻塞发布或严重数据错误 |
| `priority:p1` | 当前里程碑必须完成 |
| `priority:p2` | 可以稍后完成 |
| `good first issue` | 适合新手理解项目 |

## 6. 分支、提交和 PR

分支命名：

```text
feat/12-manual-expense
fix/31-duplicate-transaction
docs/8-privacy-policy
```

其中数字是 Issue 编号。提交信息使用简单英文前缀：

```text
feat: add manual expense form
fix: prevent duplicate notification records
test: add Alipay parser samples
docs: explain notification permission
```

Pull Request 默认先建为 Draft。满足以下条件后再合入：

- 对应一个 Issue；
- 验收标准完成；
- 本地测试通过；
- GitHub Actions 通过；
- 没有提交密钥、真实账单、数据库或构建产物；
- 使用 Squash merge，保持主分支历史简洁。

## 7. 每周维护节奏

- 周初：从 `Ready` 选择一个最小任务；
- 开发中：所有新想法记成 Issue，不临时扩展当前范围；
- PR 前：运行格式检查、单元测试和 Debug 构建；
- 周末：更新看板，关闭完成项，记录下周唯一主目标；
- 每个里程碑结束：生成一次 APK，写清已知限制并打版本标签。

## 8. 自动化检查

Android 工程创建后再启用 GitHub Actions，避免空仓库阶段产生失败记录。首批检查：

- `testDebugUnitTest`：业务逻辑和解析器单元测试；
- `assembleDebug`：确保 APK 能构建；
- `lintDebug`：Android 静态检查；
- Secret scanning / Dependabot：检查密钥和依赖风险。

真机通知识别无法完全依靠云端 CI，仍需要维护一份脱敏样本测试和人工真机验收记录。

## 9. 永远不要上传的文件

- `local.properties`；
- `.jks` / `.keystore` 签名文件及密码；
- 真实通知、短信、账单 CSV、数据库；
- 包含姓名、手机号、卡号、订单号的截图；
- Debug/Release APK 构建目录；
- API 密钥、访问令牌和云服务配置明文。

这些内容需要由 `.gitignore`、代码审查和自动扫描共同保护。

## 10. 与 Codex 协作的方式

以后可以直接用这种格式提出任务：

> 请处理 Issue #12：实现手动新增支出。先创建分支，完成后运行测试并给我看变更，不要推送，等我确认。

涉及 GitHub 外部操作时，创建分支、提交、推送和创建 PR 是不同动作。Codex 会分别取得授权，不会因为允许修改代码就自动公开或合并代码。
