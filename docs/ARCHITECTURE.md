# 自动记账 Android 软件框架（v0.1）

更新日期：2026-08-20

## 1. 产品定位

这是一款本地优先的个人记账应用。核心体验是“系统帮助用户发现交易并完成大部分填写，用户只处理少量不确定项”。

第一版成功标准：

- 常见支付通知能在数秒内转成待确认记录；
- 金额、收支方向和来源识别准确，错账不会静默进入账本；
- 同一笔交易不会因为多个通知被重复记账；
- 用户能看懂每笔账为什么被创建，并能撤销或修正；
- 没有网络也能完整使用；
- 不收集支付密码、银行卡密码或完整短信历史。

## 2. 自动记账的能力边界

### 2.1 首选数据源：支付通知

使用 Android `NotificationListenerService`，由用户在系统设置中主动授予“通知使用权”。仅处理用户开启的应用白名单，例如支付工具和银行应用。

优点：实时、无需登录金融账户、比无障碍抓屏更克制。局限：不同应用和版本的通知文案会变化；部分通知会隐藏金额；用户关闭通知后无法捕获。

### 2.2 补充数据源

- 用户分享文本或截图到本应用：显式触发，适合电子回单和账单截图；OCR 放到第二阶段。
- CSV/账单文件导入：适合补账和月末核对，放到第二阶段。
- 手动快速记账：永远保留，作为兜底路径。
- 短信：不作为 MVP 的依赖。Google Play 对 SMS 权限有严格审核；“短信类资金管理”可能申请例外，但并非默认获批。

### 2.3 明确不采用的路径

- 不用 `AccessibilityService` 抓取其他应用界面。Android 将它定位为帮助残障用户，商店政策和隐私风险都很高。
- 不模拟点击支付宝、微信或银行应用。
- 不要求用户提交支付密码、网银密码或验证码。
- 不把原始通知全文上传到服务器。

如果未来只在特定企业渠道或国内应用市场分发，也应该把无障碍方案作为单独产品决策和独立构建变体，而不是污染主版本。

## 3. 核心业务流程

```text
支付/银行通知
      │
      ▼
NotificationCaptureService
      │  包名白名单、字段清洗、生成指纹
      ▼
RawEvent（短期、最小化保存）
      │
      ▼
ParserRegistry ── 按来源选择解析器
      │
      ▼
ParsedTransactionCandidate
      │
      ├─ 无金额/无法判断方向 ───────────► 忽略或提示补全
      │
      ▼
DeduplicationEngine ── 时间、金额、来源、商户、外部单号
      │
      ├─ 重复 ─────────────────────────► 关联已有记录
      │
      ▼
ClassificationEngine ── 商户别名、用户规则、历史选择
      │
      ▼
ConfidencePolicy
      │
      ├─ 高置信度且用户允许 ───────────► 自动入账 + 可撤销通知
      └─ 其他 ─────────────────────────► 待确认箱
```

必须保证整个流水线幂等：同一个通知重复送达、应用重启或后台任务重试，都不能生成第二笔账。

## 4. 功能框架

### 4.1 MVP 功能

1. 引导与权限
   - 解释用途后跳转系统通知使用权页面；
   - 选择允许识别的应用；
   - 提供“发送测试通知/查看识别结果”的诊断页。
2. 账户
   - 现金、储蓄卡、信用卡、支付宝、微信钱包及自定义账户；
   - 期初余额、隐藏/归档账户。
3. 交易
   - 支出、收入、转账、退款、信用卡还款；
   - 手动新增、编辑、删除、撤销；
   - 标签、备注、交易时间、来源追踪。
4. 自动记账
   - 通知来源白名单；
   - 来源解析器；
   - 去重；
   - 待确认箱；
   - 用户规则（商户 → 分类/账户）；
   - 置信度阈值和“始终需要确认”开关。
5. 统计
   - 本月收入、支出、结余；
   - 分类占比、日趋势；
   - 不把转账和信用卡还款计入消费。
6. 数据管理
   - 本地备份；
   - CSV/JSON 导出；
   - 清除捕获历史；
   - 应用锁和生物识别（可在 MVP 后半段加入）。

### 4.2 后续版本

- V1.1：账单 CSV 导入、退款关联、预算、周期账单；
- V1.2：本地 OCR、商户别名学习、跨来源对账；
- V2：端到端加密云同步、多设备、家庭账本；
- V3：在用户明确选择后提供本地模型或脱敏后的智能分类。

## 5. Android 技术选型

| 领域 | 选择 | 原因 |
|---|---|---|
| 语言 | Kotlin | Android 原生主路径，协程和类型安全适合流水线处理 |
| UI | Jetpack Compose + Material 3 | 现代声明式 UI，适合状态驱动页面 |
| 架构 | UI / Domain / Data 分层 + 单向数据流 | 状态可预测，解析、记账和 UI 可分别测试 |
| 状态 | ViewModel + StateFlow | 生命周期安全，页面只有一个可观察状态来源 |
| 本地数据库 | Room | SQL 编译期校验、迁移能力和 Flow 支持 |
| 设置 | DataStore | 保存权限引导状态、主题和用户开关 |
| 依赖注入 | Hilt | Service、Worker、Repository 和 ViewModel 共享依赖较多 |
| 后台任务 | WorkManager | 可靠执行备份、同步、清理和重算等持久任务 |
| 实时捕获 | NotificationListenerService | 系统提供的通知监听入口 |
| 导航 | Navigation Compose | 单 Activity、多页面 |
| 序列化 | kotlinx.serialization | 规则、导入导出和测试样本共用模型 |
| 日志 | 结构化本地日志，Release 脱敏 | 便于诊断解析失败，不泄漏金额和通知正文 |

依赖版本统一放入 Gradle Version Catalog。`minSdk` 可先设为 26；`compileSdk` 和 `targetSdk` 在初始化项目时选当时稳定且符合分发市场要求的版本，不在架构文档中写死。

## 6. 代码组织

MVP 先使用单 `app` 模块，按职责严格分包，避免为了“架构感”过早创建几十个 Gradle 模块。当解析器或团队规模增长后，再把 `ledger`、`autobook` 和 `database` 拆成独立模块。

```text
app/src/main/java/<package>/
├── app/                         # Application、Activity、导航、依赖注入
├── core/
│   ├── model/                   # 纯 Kotlin 业务模型
│   ├── database/                # Room Entity、DAO、迁移
│   ├── data/                    # Repository、数据源实现
│   ├── ledger/                  # 入账、转账、退款、余额规则
│   ├── autobook/
│   │   ├── capture/             # 通知事件接入
│   │   ├── parser/              # 来源解析器与注册表
│   │   ├── dedup/               # 去重与事件关联
│   │   ├── classify/            # 分类规则和置信度
│   │   └── pipeline/            # 编排，不直接依赖 UI
│   ├── security/                # Keystore、脱敏、应用锁
│   └── ui/                      # 主题和通用组件
├── feature/
│   ├── onboarding/
│   ├── home/
│   ├── inbox/                   # 待确认箱
│   ├── transaction/
│   ├── account/
│   ├── rule/
│   ├── report/
│   └── settings/
└── worker/                      # 备份、清理、同步任务
```

依赖方向固定为：`UI → Domain → Data abstraction`，Android 系统 API 只出现在 capture、database、security 等边缘实现中。解析器和账本引擎保持纯 Kotlin，以便在 JVM 上快速测试。

## 7. 数据模型

### 7.1 主要实体

| 实体 | 关键字段 | 说明 |
|---|---|---|
| `Book` | id, name, baseCurrency | 账本，MVP 只有默认账本但保留扩展能力 |
| `Account` | id, type, name, currency, openingBalance, archivedAt | 资金账户 |
| `Category` | id, parentId, direction, name | 收入/支出分类，支持两级分类 |
| `Transaction` | id, bookId, type, occurredAt, status, note, source | 用户看到的一笔交易 |
| `Posting` | id, transactionId, accountId/categoryId, amountMinor, currency | 交易分录；一笔交易至少两条 |
| `SourceEvent` | id, sourceType, packageName, eventTime, fingerprint, payloadCiphertext, expiresAt | 捕获事件；原始载荷短期保存 |
| `Candidate` | id, sourceEventId, amountMinor, merchant, direction, confidence, state | 解析后待处理记录 |
| `Rule` | id, priority, conditionsJson, actionsJson, enabled | 用户可解释的分类规则 |
| `MerchantAlias` | normalizedName, displayName, categoryId | 商户归一化和历史选择 |
| `AuditLog` | id, action, targetId, createdAt, reversiblePayload | 自动入账和撤销依据 |

### 7.2 金额规则

- 金额一律用最小货币单位的 `Long` 保存，例如 12.34 元保存为 1234；禁止用 `Float`/`Double`。
- 每条 `Posting` 带币种；MVP 不做自动汇率换算。
- 每笔交易的分录合计必须平衡。
- 转账、信用卡还款只在账户之间移动，不计入收入或支出。
- 退款关联原支出并冲销，不简单记为普通收入。

采用内部双分录、外部简单表单：用户仍只填写“金额、账户、分类”，系统负责生成分录。这会显著减少转账、退款、还款导致的统计错误。

## 8. 自动解析设计

### 8.1 标准输入

```kotlin
data class CapturedEvent(
    val sourceType: SourceType,
    val packageName: String,
    val postedAt: Instant,
    val title: String?,
    val text: String?,
    val extras: Map<String, String>,
)
```

进入数据库前先做字段长度限制、Unicode 归一化和敏感字段清洗。只有白名单包名能进入解析流程。

### 8.2 解析器契约

```kotlin
interface TransactionParser {
    fun supports(event: CapturedEvent): Boolean
    fun parse(event: CapturedEvent): ParseResult
}
```

每个来源单独实现解析器，规则由测试样本保护。通用正则只做兜底，不能把所有应用文案塞进一个巨型解析类。

`ParseResult` 应包含：金额、币种、收支方向、商户、账户提示、外部单号、交易时间、字段级置信度、解析器版本和失败原因。

### 8.3 置信度策略

- 金额或方向缺失：不得自动入账；
- 疑似转账、退款、还款：默认需要确认；
- 金额与方向确定、命中用户规则、去重无冲突：可达到高置信度；
- 用户必须主动开启“高置信度自动入账”；
- 自动入账后显示可直接撤销的应用内提示或通知；
- 阈值应由字段条件决定，不只依赖一个难以解释的分数。

### 8.4 去重指纹

优先级从高到低：

1. 来源包名 + 外部交易号；
2. 来源包名 + 金额 + 币种 + 交易时间窗口 + 归一化商户；
3. 通知 key + postedAt；
4. 原始事件稳定字段的哈希。

同一事件可关联多个原始通知，但最终只能对应一个候选或一个已入账交易。用户手动记账后，自动候选也应尝试与其合并。

## 9. 页面与导航

底部导航建议保持四项：

- 首页：本月概览、最近交易、待确认数量；
- 明细：筛选、搜索、编辑交易；
- 统计：趋势和分类；
- 设置：账户、分类、自动记账规则、数据与隐私。

“待确认箱”从首页高优先级入口进入；“记一笔”使用全局浮动按钮。权限诊断页应显示：监听是否开启、白名单来源、最近一次捕获时间、最近一次解析结果和失败原因。

## 10. 隐私与安全

- 默认本地处理，核心功能不依赖账号和服务器；
- 权限申请前用独立页面说明捕获内容、处理目的、保存期限和关闭方式；
- 只监听用户选择的包名；
- `SourceEvent` 原文加密且设置短保留期，例如 7 天；确认入账后可立即删除原文；
- 密钥由 Android Keystore 管理；备份文件由用户密码派生的密钥加密；
- Release 日志不得记录完整通知、账号、卡号、金额或商户组合；
- 截屏保护做成用户选项，导出操作需要二次确认；
- 云同步若上线，必须采用独立威胁模型和端到端加密设计，不能直接复用明文 REST 同步。

## 11. 测试策略

自动记账的首要资产不是 UI 截图，而是解析样本和账本不变量。

### 单元测试

- 每个来源建立脱敏后的“黄金样本”：原始字段 → 期望解析结果；
- 金额格式、全角字符、多语言、负数、退款、撤销、缺字段的参数化测试；
- 相同事件重复执行 2 次，数据库结果仍只有 1 笔；
- 所有交易分录平衡；
- 转账和还款不改变收支统计；
- 退款正确冲销原交易；
- 规则优先级和冲突策略可预测。

### 集成测试

- Room migration 测试；
- Repository + 数据库事务测试；
- Notification Service 到 Candidate 的流水线测试；
- Worker 重试、应用重启和进程被杀后的恢复测试。

### UI 测试

- 首次授权、拒绝权限、权限被系统撤销；
- 待确认、批量确认、编辑、撤销；
- 空状态、错误状态和大字体；
- 导出与恢复闭环。

## 12. 可观测性

本地诊断记录仅保存：事件类型、来源包名的稳定代号、解析器版本、成功/失败码、耗时和去重结果。默认不记录业务正文。

用户可以主动导出一份脱敏诊断包。任何远程崩溃上报都应独立征得同意，并在发送前去除通知正文、金额、账户和商户信息。

## 13. 迭代计划

### 里程碑 A：账本底座

- 创建 Android 工程和主题；
- Room schema、Repository、账本引擎；
- 账户/分类/手动记账/明细；
- 单元测试覆盖金额和分录不变量。

验收：离线完成增删改查；转账和还款统计正确；应用重启后数据完整。

### 里程碑 B：自动捕获闭环

- 权限引导与来源白名单；
- NotificationListenerService；
- 第一批来源解析器；
- 候选、去重、待确认、撤销；
- 解析诊断页与黄金样本测试。

验收：测试通知能稳定产生唯一候选；重复通知不重复入账；解析失败可解释。

### 里程碑 C：规则与统计

- 商户归一化；
- 分类规则；
- 高置信度自动入账开关；
- 首页和月度统计；
- CSV/JSON 导出备份。

验收：用户能控制哪些记录自动入账；每笔自动账可追溯、可撤销；导出金额与统计一致。

### 里程碑 D：发布准备

- 权限和隐私文案；
- 数据安全说明；
- 数据库迁移、性能、电量和兼容性测试；
- 内测渠道验证不同厂商系统的通知行为；
- 商店政策复核。

## 14. 当前需要锁定的产品决策

开始写工程前只需锁定以下事项：

1. 首发分发渠道：Google Play、国内应用市场还是仅自用安装；
2. 第一批要支持的支付/银行应用及脱敏通知样例；
3. 是否坚持完全离线，还是第一版就需要多设备同步；
4. 自动入账默认关闭还是默认只进入待确认箱；
5. 是否需要多账本、多币种和家庭共享。

建议默认答案：面向正式上架；先支持 2～3 个高频来源；完全离线；自动入账默认关闭；MVP 单账本、单本位币。

## 15. 相关官方依据

- [Android 推荐应用架构](https://developer.android.com/topic/architecture/recommendations)
- [NotificationListenerService](https://developer.android.com/reference/android/service/notification/NotificationListenerService)
- [Room 本地数据库](https://developer.android.com/training/data-storage/room)
- [WorkManager](https://developer.android.com/reference/androidx/work/WorkManager)
- [Android Keystore](https://developer.android.com/privacy-and-security/keystore)
- [Google Play 的 SMS/通话记录权限政策](https://support.google.com/googleplay/android-developer/answer/10208820)
- [Google Play 的 AccessibilityService 政策](https://support.google.com/googleplay/android-developer/answer/10964491)
