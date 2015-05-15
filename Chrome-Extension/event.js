/**
 * Created by wubincen on 15/5/4.
 */

/**
 * post 请求
 * @param servlet 服务端servlet名称
 * @param data  数据，json格式
 * @param callback 回调函数，有一个参数data
 * @param secure 是否需要加密
 */
function send_request_post(servlet, data, callback, secure) {
    var url_prefix = (secure ? 'https://10.211.55.8:8443' : 'http://10.211.55.8:8080') + '/Server/';
    console.log('send request to ' + url_prefix + servlet);
    $.ajax({
        type : 'post',
        contentType : 'application/json; charset=utf-8',
        url : url_prefix + servlet,
        data : JSON.stringify(data),
        dataType : 'json',
        success : callback
        //error : function(xxx, yyy){ console.log('error'); },
        //complete : function(data) {console.log('complete');}
    });
}


//服务端登录和注册的url前缀，需要根据实际情况配置
var server_host = '10.211.55.8';
var cookie_url = 'https://'+server_host+':8443/Server/Secure/';
var cookie_url1 = 'http://'+server_host+':8080/Server/';

/**
 * 向服务端发送待分类的网页正文
 * @param data
 */
function send_data_to_classify(data) {
    console.log(data);
    chrome.cookies.get({url:cookie_url1, name:"token"}, function(cookie){
        console.log(cookie);
        if (cookie) {
            //data = {title: '你好', content: '哈哈'};
            send_request_post('Classify', data, function(response){
                //这里是从服务端传回分类结果后调用的函数
                console.log(response);

                chrome.tabs.query({active:true, currentWindow:true}, function(tabs){
                    chrome.tabs.sendMessage(tabs[0].id, {method: 'show_result', data: response}, function(response){
                        console.log(response);
                    });
                });


            });
        }
    });
}

/**
 * 保存内容
 * @param data
 */
function save_to_server(data) {
    chrome.cookies.get({url:cookie_url1, name:'token'}, function(cookie){
        if (cookie) {
            send_request_post('Save', data, function(response){
                console.log(response);
                //callback(response);
                chrome.tabs.query({active:true, currentWindow:true}, function(tabs){
                    chrome.tabs.sendMessage(tabs[0].id, {method: 'response_save', data: response}, function(response){
                        console.log(response);
                    });
                });

            });
        }
    });
}

//获取网页正文
function clip_content() {
    chrome.tabs.query({active: true, currentWindow: true}, function(tabs){
        chrome.tabs.sendMessage(tabs[0].id, {method: 'clip'}, function(response){
            console.log(response);
            send_data_to_classify(response);

        });
    });
}

//监听消息
chrome.runtime.onMessage.addListener(function(request, sender, sendResponse){
    if (request.method == 'save_to_server')
        //console.log(request);
        save_to_server(request.data);
    //sendResponse('zzz');

});



//点击按钮时，调用这个函数
function extension_button_clicked() {
    chrome.tabs.query({active: true, currentWindow: true}, function(tabs){
        chrome.tabs.sendMessage(tabs[0].id, {method: 'clip'}, function(response){
            console.log(response);
        });
    });
}

//监听点击extension按钮触发的事件
chrome.browserAction.onClicked.addListener(extension_button_clicked);