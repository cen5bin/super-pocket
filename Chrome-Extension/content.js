var getHTML = function() {
    alert('zz');
};


//获取网页正文
var get_content = function() {
    var page_h = $(document).height();
    var page_w = $(document).width();
    var gap = 10;
    var content_h = $('#article_details').height() + gap;
    var content_w = $('#article_details').width() + gap;
    var content_x = $('#article_details').offset().left - gap / 2;
    var content_y = $('#article_details').offset().top - gap / 2;
    console.log(content_x);
    var div = '<div id="super-pocket-bg" style="border-width: ' + content_y + 'px ' + (page_w-content_w-content_x) +'px '
   + (page_h-content_h-content_y) + 'px ' + content_x + 'px;">';
    console.log(div);
    $('html').append(div);
    $('#super-pocket-bg').css('width', content_w+'px');
    $('#super-pocket-bg').css('height', content_h+'px');
    $('#super-pocket-bg').append('<div id="super-pocket-container"></div>');
};



chrome.extension.onRequest.addListener(
        function(request, sender, sendRensponse){
            if (request.method == 'getHTML') {
                var x = $('#article_details').offset().top;
                console.log('zz');
               // var y = $('#article_details').offset().left;
                sendRensponse({data:'x:'+x, method : 'getHTML'});
                //$('#article_details').css('border', '1px solid #333333');
                get_content();

            }
        });

