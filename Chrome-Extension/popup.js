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



$(document).ready(function(){
    $('#signin_button').click(function(){
        console.log('sign in');
        var tmp = {
            email : 'asd@asd.com',
            password : 'aaaaaa'
        };

        $.ajax({
            type : 'post',
            contentType : 'application/json',
            url : 'http://10.211.55.8:8080/Server/SignIn',
            data : JSON.stringify(tmp),
            dataType : 'json',
            success : function(data) {
                console.log('zz');
                console.log(data.zz);
            }
        });
    });
});