#super-pocket

###项目结构
- 服务端(JavaEE servlet)
    - `com.superpocket.servlet`: 所有服务的servlet
        - `SignIn`: 登录
        - `SignUp`: 注册
        - `Classify`: 分类
    - `com.superpocket.kit`: 工具类
        - `DataKit`: 数据处理。如接收客户端传来的json数据
        - `SecureKit`: 安全，对用户信息加密等等
        - `SettingKit`: 用户设置
    - `com.superpocket.logic`: 逻辑层代码，所有业务逻辑代码都放在这里
        - `ContentLogic`: 内容相关，如保存网页正文，分类等等
        - `UserLogic`：用户相关，登录，注册等等
        - `NetLogic`：网络相关，向客户端返回数据，写cookie等等
    - `com.superpocket.classifier`: 分类器
        - `ClassifierInterface`：分类器接口，所有分类器必须implement这个接口
        - `ClassifierSample`：分类器示例，写分类器时可以参考这个模板
    - `com.superpocket.dao`: 数据库相关  
- 客户端(Chrome extension html+js+css)
	- `manifest.json`：chrome extension配置文件
	- `content.js`：注入到web page里运行的js
	- `popup.html`,`popup.js`：点击按钮弹出的界面以及相关函数
	- `event.js`：chrome extension的background
	- `panel.html panel.js`：super-pocket前端正文抽取功能的控制面板，这个页面会被`content.js`加载到当前web page的dom树里
	- `icon.png`：chrome extension图标
	- `logo.png`：应用logo，会放在`popup.html`里显示
	- `style.css`：所有需要的css
	- `jquery.js`,`json2.js`：需要的第三方js库

###开发日志
- 2015-05-08 自动登录，客户端cookie，popup UI全新设计，UI基调确定  
- 2015-05-07 登录和注册加密，前端完善
- 2015-05-06 重新加入popup，客户端加入了ajax，服务端加入登录和注册的servlet
- 2015-05-05 去掉了popup，改成了background模式，完善了前端的交互，开始处理工具面板
- 2015-05-04 弹出剪藏功能的控制面板
- 2015-05-03 完成剪藏功能的demo
- 2015-05-01 工程建立
  - `Server`: 服务端程序，java servlet
  - `Chrome-Extension`: 谷歌浏览器插件程序，javascript+css+html
