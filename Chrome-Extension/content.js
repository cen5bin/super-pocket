//获取网页正文，剪藏功能
var clip_content = function() {
    //计算坐标
    var page_h = $(document).height();
    var page_w = $(document).width();
    var gap = 6;
    var content_h = $('#article_details').height() + gap;
    var content_w = $('#article_details').width() + gap;
    var content_x = $('#article_details').offset().left - gap / 2;
    var content_y = $('#article_details').offset().top - gap / 2;

    //创建剪藏功能的那个大的div，要覆盖到原来的页面上去
    var div = '<div id="super-pocket-bg" style="border-width: ' + content_y + 'px ' + (page_w-content_w-content_x) +'px '
   + (page_h-content_h-content_y) + 'px ' + content_x + 'px;">';
    console.log(div);
    $('html').append(div);
    $('#super-pocket-bg').css('width', content_w+'px');
    $('#super-pocket-bg').css('height', content_h+'px');
    $('#super-pocket-bg').append('<div id="super-pocket-container"></div>');
};

//显示剪藏功能的控制面板
var show_super_pocket_panel = function() {
    $('html').append('<iframe id="super-pocket-panel" src="'+ chrome.extension.getURL('panel.html') +'"></iframe>')
}

var is_clipping = false; //当前页面是否处于剪藏状态
//退出剪藏功能，恢复原始页面
var recover_page = function() {
    $('#super-pocket-bg').remove();
    $('#super-pocket-panel').remove();
    document.onkeydown = key_down;
    is_clipping = false;
};

var key_down = null;  //保存原来页面中的按键事件
//监听键盘事件，按Esc要退出剪藏功能
var add_keyboard_listener = function() {
    if (key_down == null) key_down = document.onkeydown;
    document.onkeydown = function (event) {
        console.log(event.keyCode);
        //按Esc，应该恢复原始页面
        if (event.keyCode == 27) {
            recover_page();
        }
    };
};

//消息监听
chrome.runtime.onMessage.addListener(
    function(request, sender, sendResponse) {
        console.log(request);
        //从event.js发来了clip消息，开始剪藏
        if (request.method == 'clip') {
            if (!is_clipping) {
                add_keyboard_listener();
                clip_content();
                show_super_pocket_panel();
                sendResponse('Good Job!');
                is_clipping = true;
            }
            else recover_page();
        }
    }
);