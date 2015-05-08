/**
 * Created by wubincen on 15/5/5.
 */

//利用cookie自动登录
function auto_sign_in() {
    chrome.runtime.getBackgroundPage(function(background){
        background.send_request_post('SignIn', '{}', function(data){
            console.log(data);
            if (data.success == 'yes') {
                background.clip_content();
            }
        }, true);
    });
}

chrome.cookies.get({url:'https://10.211.55.8:8443/Server/', name:"token"}, function(cookie){
    if (cookie) {
        window.close();
        auto_sign_in();
    }
});

//获取网页正文
function clip_content() {
    chrome.tabs.query({active: true, currentWindow: true}, function(tabs){
        chrome.tabs.sendMessage(tabs[0].id, {method: 'clip'}, function(response){
            console.log(response);
        });
    });
}

//获取email和password
function get_user_data() {
    var ret = {};
    ret['email'] = $('#email').val();
    ret['password'] = $('#password').val();
    return ret;
}

//验证邮箱格式
function is_email(str) {
    if (str.charAt(0) == "." || str.charAt(0) == "@" || str.indexOf('@', 0) == -1 ||
        str.indexOf('.', 0) == -1 || str.lastIndexOf("@") == str.length - 1 ||
        str.lastIndexOf(".") == str.length - 1 ||
        str.indexOf('@.') > -1)
        return false;
    else
        return true;
}

//清除错误提示
function clear_error_tip() {
    $('.input-container p').remove();
}

//检查用户输入的数据是否合法
function judge_user_data_valid() {
    clear_error_tip();
    var data = get_user_data();
    if (data['email'].length == 0) {
        $($('.input-container')[0]).append('<p>邮箱不能为空</p>');
        return false;
    }
    if (data['password'].length < 6) {
        $($('.input-container')[1]).append('<p>密码不得少于6位</p>')
        return false;
    }
    if (!is_email(data['email'])) {
        $($('.input-container')[0]).append('<p>非法邮箱</p>');
        return false;
    }
    return true;
}

//登录功能
function sign_in() {
    if (judge_user_data_valid()) return;
    console.log('begin to sign in');
    chrome.runtime.getBackgroundPage(function(background){
        background.send_request_post('SignIn', get_user_data(), function(data){
            chrome.cookies.get({url:'https://10.211.55.8:8443/Server/', name:"email"}, function(cookie){
                console.log(cookie.value);
            });
        }, true);
    });
}


//注册功能
function sign_up() {
    if (!judge_user_data_valid()) return;
    chrome.runtime.getBackgroundPage(function(background){
        background.send_request_post('SignUp', get_user_data(), function(data){
            console.log(data);
        });
    });
}


$(document).ready(function(){
    $('#signin_button').click(sign_in);
    $('#signup_button').click(sign_up);
});

