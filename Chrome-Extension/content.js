//加载动画
var spinner = null;
function start_spin() {
    $('html').append('<div id="super-pocket-loading"></div>')

    var window_height = $(window).height();
    console.log(window_height);
    var window_width = $(window).width();
    var container_len = 150;
    $('#super-pocket-loading').css('width', container_len + 'px')
        .css('height', container_len + 'px')
        .css('left', (window_width/2-container_len/2)+'px')
        .css('top', (window_height/2-container_len/2)+'px');

    var opts = {
        lines: 13, // The number of lines to draw
        length: 15, // The length of each line
        width: 6, // The line thickness
        radius: 20, // The radius of the inner circle
        corners: 1, // Corner roundness (0..1)
        rotate: 19, // The rotation offset
        color: '#ffffff', // #rgb or #rrggbb
        speed: 1.4, // Rounds per second
        trail: 71, // Afterglow percentage
        shadow: false, // Whether to render a shadow
        hwaccel: false, // Whether to use hardware acceleration
        className: 'spinner', // The CSS class to assign to the spinner
        zIndex: 2e9, // The z-index (defaults to 2000000000)
        top: container_len/2+'px', // Top position relative to parent in px
        left: container_len/2+'px' // Left position relative to parent in px
    };



    var target = document.getElementById('super-pocket-loading');
    spinner = new Spinner(opts).spin(target);
}

//停止加载动画
function stop_spin() {
    if (!spinner) return;
    spinner.stop();
    $('#super-pocket-loading').remove();
    spinner = null;
}

//去除字符串前后的空白字符
function trim(str){
    str = str.replace(/^(\s|\u00A0)+/,'');
    for(var i=str.length-1; i>=0; i--){
        if(/\S/.test(str.charAt(i))){
            str = str.substring(0, i+1);
            break;
        }
    }
    return str;
}

//抽取文章标题，目前直接返回网页title
function extract_title(post_div) {
    return document.title;
    console.log($($('#'+post_div+' h1')[0]).html().replace(/<[^>]+>/g,''));
    return trim($($('#'+post_div+' h1')[0]).html().replace(/<[^>]+>/g,''));
}

//获取网页正文，剪藏功能
function clip_content(post_div) {
    //计算坐标
    var page_h = $(document).height();
    var page_w = $(document).width();
    var gap = 6;
    var content_h = $('#'+post_div).height() + gap;
    var content_w = $('#'+post_div).width() + gap;
    var content_x = $('#'+post_div).offset().left - gap / 2;
    var content_y = $('#'+post_div).offset().top - gap / 2;

    if (content_x < 0) content_x = 0;
    console.log($('#'+post_div).offset());

    //创建剪藏功能的那个大的div，要覆盖到原来的页面上去
    var div = '<div id="super-pocket-bg" style="border-width: ' + content_y + 'px ' + (page_w-content_w-content_x) +'px '
   + (page_h-content_h-content_y) + 'px ' + content_x + 'px;">';
    console.log(div);
    $('html').append(div);
    $('#super-pocket-bg').css('width', content_w+'px');
    $('#super-pocket-bg').css('height', content_h+'px');
    $('#super-pocket-bg').append('<div id="super-pocket-container"></div>');
}

//显示剪藏功能的控制面板
function show_super_pocket_panel(post_id, labels) {
    var url = chrome.extension.getURL('panel.html') + '?title=' + extract_title(post_id);
    $('html').append('<iframe id="super-pocket-panel" scrolling="no" src="'+ encodeURI(url) + '"></iframe>');
    $('#super-pocket-panel').load(function(){
        $('#super-pocket-panel')[0].contentWindow.postMessage(labels, '*');
    });

}

var is_clipping = false; //当前页面是否处于剪藏状态
//退出剪藏功能，恢复原始页面
function recover_page() {
    $('#super-pocket-bg').remove();
    $('#super-pocket-panel').remove();
    document.onkeydown = key_down;
    is_clipping = false;
    stop_spin();
}

var key_down = null;  //保存原来页面中的按键事件
//监听键盘事件，按Esc要退出剪藏功能
function add_keyboard_listener() {
    if (key_down == null) key_down = document.onkeydown;
    document.onkeydown = function (event) {
        console.log(event.keyCode);
        //按Esc，应该恢复原始页面
        if (event.keyCode == 27) {
            recover_page();
        }
    };
}

//获取文章的div的id
function get_post_id() {
    //return 'content';
    return 'topics';
    //return 'mainContent';
    return 'article_details';
}

var pid = 0;  //服务器上临时存储的此文的pid

//消息监听
chrome.runtime.onMessage.addListener(
    function(request, sender, sendResponse) {
        console.log(request);
        //从event.js发来了clip消息，开始剪藏
        if (request.method == 'clip') {
            if (!is_clipping) {
                add_keyboard_listener();
                var post_id = get_post_id();
                start_spin();
                console.log(extract_title(post_id));
                sendResponse({title: extract_title(post_id), content:$('#'+post_id).prop('outerHTML'), url: window.location.href});
                is_clipping = true;
            }
            else recover_page();
        }
        else if (request.method == 'show_result') {  //显示分类结果，调出super-pocket-panel
            var post_id = get_post_id();
            pid = request.data.post_id;
            clip_content(post_id);
            show_super_pocket_panel(post_id, request.data);
            stop_spin();
            sendResponse('zzz');
        }
        else if (request.method == 'response_save') {
            stop_spin();
            if (request.data.success == 'yes') {
                recover_page();
                alert('保存成功');
            }
            else alert('保存失败');
        }
    }
);

window.addEventListener('message', function(event){

    //这个事件由点击super-pocket-panel的关闭按钮触发
    if (event.data.name == 'close-panel')
        recover_page();
    else if (event.data.name == 'save-result') { //点击super-pocket-panel保存按钮
        console.log(event.data.data);
        if (pid == 0) return;
        //将结果发回给event.js，让他提交服务器
        start_spin();
        chrome.runtime.sendMessage({method:'save_to_server', data:{post_id: pid, labels: event.data.data}}, function(response){

        });
    }
});