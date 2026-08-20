# 安卓自动记账应用

当前仓库已经进入 Android 原型开发阶段。第一版的目标不是“偷偷记录所有支付”，而是：

1. 在用户明确授权后读取支付类通知；
2. 在本机解析金额、商户、账户和交易类型；
3. 去重并生成待确认记录；
4. 高置信度记录可按用户规则自动入账；
5. 所有结果可追溯、可撤销、可导出。

项目文档：

- [软件架构](docs/ARCHITECTURE.md)
- [vivo 自动记账调研与参考项目](docs/AUTO_BOOKKEEPING_RESEARCH.md)
- [GitHub 项目管理办法](docs/PROJECT_MANAGEMENT.md)

## 建议的第一阶段

- Kotlin + Jetpack Compose 原生 Android 应用
- 本地优先，不要求注册账号
- 手动记账、账户和分类管理
- 通知捕获与规则解析
- 待确认箱、去重、撤销
- 月度收支与分类统计
- CSV/JSON 备份导出

暂不在第一版加入云同步、家庭账本、银行密码登录、无障碍抓取和大模型联网分析。

## 项目状态

当前处于 `M0：需求与原型` 阶段，已经建立 Kotlin + Jetpack Compose 工程骨架。每项开发工作通过 GitHub Issue 跟踪，通过 Pull Request 合入 `main`，详细流程见项目管理文档。

当前原型包含：

- 首页月度收支概览；
- 明细、统计和设置页面；
- 可实际新增收入或支出的“记一笔”表单；
- 金额以分为单位保存的基础业务模型；
- 金额解析单元测试。

自动通知识别和 Room 数据库将在后续里程碑接入。

## 开发环境

- Android Studio Quail 3 或兼容版本
- JDK 17 或更高版本
- Android SDK 37

首次构建：

```powershell
.\gradlew.bat testDebugUnitTest assembleDebug
```

应用 ID 暂定为 `com.jicmyk.accounting`，正式发布前可以调整。
