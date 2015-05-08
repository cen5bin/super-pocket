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


//获取网页正文
function clip_content() {
    chrome.tabs.query({active: true, currentWindow: true}, function(tabs){
        chrome.tabs.sendMessage(tabs[0].id, {method: 'clip'}, function(response){
            console.log(response);
        });
    });
}


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