/**
 * Created by wubincen on 15/5/4.
 */


function extension_button_clicked() {
    chrome.tabs.query({active: true, currentWindow: true}, function(tabs){
        chrome.tabs.sendMessage(tabs[0].id, {method: 'clip'}, function(response){
            console.log(response);
        });
    });
}

//点击extension按钮触发的事件
chrome.browserAction.onClicked.addListener(extension_button_clicked);