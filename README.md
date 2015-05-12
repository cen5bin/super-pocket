#super-pocket

###项目结构
- 服务端(JavaEE servlet)
    - `com.superpocket.servlet`: 所有服务的servlet
        - `SignIn`: 登录
        - `SignUp`: 注册
        - `Classify`: 分类

- 2015-05-08 自动登录，客户端cookie，popup UI全新设计，UI基调确定  
- 2015-05-07 登录和注册加密，前端完善
- 2015-05-06 重新加入popup，客户端加入了ajax，服务端加入登录和注册的servlet
- 2015-05-05 去掉了popup，改成了background模式，完善了前端的交互，开始处理工具面板
- 2015-05-04 弹出剪藏功能的控制面板
- 2015-05-03 完成剪藏功能的demo
- 2015-05-01 工程建立
  - `Server`: 服务端程序，java servlet
  - `Chrome-Extension`: 谷歌浏览器插件程序，javascript+css+html
