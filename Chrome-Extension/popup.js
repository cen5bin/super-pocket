/**
 * Created by wubincen on 15/5/5.
 */

//利用cookie自动登录
function auto_sign_in() {
    chrome.runtime.getBackgroundPage(function(background){
        background.send_request_post('Secure/SignIn', '{}', function(data){
            console.log(data);
            if (data.success == 'yes') {
                background.clip_content();
            }
            else alert('链接服务器出错');
        }, true);
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

function sign_in_failed() {
    clear_error_tip();
    $($('.input-container')[0]).append('<p>邮箱或者密码错误</p>');
}

//服务端登录和注册的url前缀，需要根据实际情况配置
var server_host = '10.211.55.8';
var cookie_url = 'https://'+server_host+':8443/Server/Secure/';
var cookie_url1 = 'http://'+server_host+':8080/Server/';

/**
 * 将cookie_url对应的那些cookie在cookie_url1中也写一份
 * @param callback
 */
function set_cookie(callback) {
    console.log('set_cookie');
    chrome.cookies.get({url:cookie_url, name:'email'}, function(cookie){
        console.log(cookie);
        chrome.cookies.set({url:cookie_url1, name:'email', value: cookie.value, expirationDate: 3578902847.485571}, function(cookie){
            console.log(cookie);
            chrome.cookies.get({url:cookie_url, name:'token'}, function(cookie){
                chrome.cookies.set({url:cookie_url1, name:'token', value: cookie.value, expirationDate: 3578902847.485571}, function(cookie){
                    chrome.cookies.get({url:cookie_url, name:'uid'}, function(cookie){
                        chrome.cookies.set({url:cookie_url1, name:'uid', value: cookie.value, expirationDate: 3578902847.485571}, function(cookie){
                            callback();
                        });
                    });
                });
            });
        });
    });
}

//登录功能
function sign_in() {
    if (!judge_user_data_valid()) return;
    console.log('begin to sign in');
    chrome.runtime.getBackgroundPage(function(background){
        background.send_request_post('Secure/SignIn', get_user_data(), function(data){
            if (data.success == 'yes') {
                set_cookie(function(){
                    //window.close();
                    background.clip_content();
                });
            }
            else sign_in_failed();
        }, true);
    });
}

//注册功能
function sign_up() {
    if (!judge_user_data_valid()) return;
    console.log('begin to sign up');
    chrome.runtime.getBackgroundPage(function(background){
        background.send_request_post('Secure/SignUp', get_user_data(), function(data){
            if (data.success == 'yes') {
                set_cookie(function(){
                    window.close();
                    background.clip_content();
                });
            }
        }, true);
    });
}



//注销功能
function sign_out() {
    console.log('sign out');
    chrome.cookies.remove({url:cookie_url, name:'email'});
    chrome.cookies.remove({url:cookie_url, name:'token'});
}

$(document).ready(function(){
    console.log('document ready');
    //sign_out();
    $('#signin-button').click(sign_in);
    $('#signup-button').click(sign_up);

    //如果cookie已经存在，则自动登录
    chrome.cookies.get({url:cookie_url, name:"token"}, function(cookie){
        if (cookie) {
            console.log(cookie);
            window.close();
            //console.log(document.head);
            auto_sign_in();
        }
    });

});

