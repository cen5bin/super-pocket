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
function show_super_pocket_panel(post_id) {
    $('html').append('<iframe id="super-pocket-panel" scrolling="no" src="'+ encodeURI(chrome.extension.getURL('panel.html') +
    '?title=' + extract_title(post_id)) + '"></iframe>');
    //$('#super-pocket-panel').load(function(){
    //    extract_title(post_id);
    //});

}

var is_clipping = false; //当前页面是否处于剪藏状态
//退出剪藏功能，恢复原始页面
function recover_page() {
    $('#super-pocket-bg').remove();
    $('#super-pocket-panel').remove();
    document.onkeydown = key_down;
    is_clipping = false;
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
    //return 'topics';
    //return 'mainContent';
    return 'article_details';
}


//消息监听
chrome.runtime.onMessage.addListener(
    function(request, sender, sendResponse) {
        console.log(request);
        //从event.js发来了clip消息，开始剪藏
        if (request.method == 'clip') {
            if (!is_clipping) {
                add_keyboard_listener();
                var post_id = get_post_id();
                clip_content(post_id);
                show_super_pocket_panel(post_id);
                sendResponse('Good Job!');
                is_clipping = true;
            }
            else recover_page();
        }
    }
);