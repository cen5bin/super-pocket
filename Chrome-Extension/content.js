var keyDown = null;

//获取网页正文
var get_content = function() {
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

var show_super_pocket_panel = function() {
    $('html').append('<iframe id="super-pocket-panel" src="'+ chrome.extension.getURL('panel.html') +'"></iframe>')
}

//退出剪藏功能，恢复原始页面
var recover_page = function() {
    $('#super-pocket-bg').remove();
    $('#super-pocket-panel').remove();
};

//监听键盘事件
var add_keyboard_listener = function() {
    document.onkeydown = function (event) {
        console.log(event.keyCode);
        //按Esc，应该恢复原始页面
        if (event.keyCode == 27) {
            recover_page();
            document.onkeydown = keyDown;
        }
    };
};


chrome.extension.onRequest.addListener(
        function(request, sender, sendRensponse){
            if (request.method == 'getHTML') {
                if (keyDown == null) keyDown = document.onkeydown;
                //add_keyboard_listener();
                get_content();
                show_super_pocket_panel();
               // var y = $('#article_details').offset().left;
                sendRensponse({data:'x:'+x, method : 'getHTML'});
                //$('#article_details').css('border', '1px solid #333333');
            }
            else if (request.method == 'exit') {
                recover_page();
            }
        });

