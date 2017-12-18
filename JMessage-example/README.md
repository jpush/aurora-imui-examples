# JMessage-example

这个 example 使用  [Aurora IMUI](https://github.com/jpush/aurora-imui)  和  [JMessage SDK](https://docs.jiguang.cn/jmessage/guideline/jmessage_guide/) 实现聊天功能，同时支持 Android 和 iOS 平台，如果需要集成本项目的功能，需要可以参照下列操作。

## 集成

### iOS：

集成到自己项目：

- 安装 JMessage
- 添加 [aurora-imui](./JMessage-example/iOS/aurora-imui) 目录文件到自己工程，并且完成相关[配置](https://github.com/jpush/aurora-imui/blob/master/docs/iOS/IMUIInputView_usage_zh.md#%E6%89%8B%E5%8A%A8%E9%9B%86%E6%88%90)。
- 添加 [JMessageBridge](./JMessage-example/iOS/JMessageBridge) 目录文件到自己工程。
- 添加 `ConversationViewController.h` `ConversationViewController.m` `ConversationViewController.xib` 到自己工程。

