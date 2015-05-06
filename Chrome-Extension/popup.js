/**
 * Created by wubincen on 15/5/5.
 */
//window.close();

//chrome.runtime.getBackgroundPage().extension_button_clicked();

chrome.tabs.query({active: true, currentWindow: true}, function(tabs){
    chrome.tabs.sendMessage(tabs[0].id, {method: 'clip'}, function(response){
        console.log(response);
    });
});


//获取email和password
function get_user_data() {
    var ret = {};
    ret['email'] = $('#email').val();
    ret['password'] = $('#password').val();
    return ret;
}


//登录功能
function sign_in() {
    chrome.extension.getBackgroundPage().send_request_post('SignIn', get_user_data(), function(data){
        console.log(data.zz);
    });
}

//注册功能
function sign_up() {
    chrome.extension.getBackgroundPage().send_request_post('SignUp', get_user_data(), function(data){
        console.log(data);
    });
}


$(document).ready(function(){
    $('#signin_button').click(sign_in);
    $('#signup_button').click(sign_up);
});

