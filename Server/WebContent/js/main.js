/**
 * Created by wubincen on 15/5/19.
 */

function classes_is_showing() {
    var display = $('#sp-class-list').css('display');
    //console.log(display);
    if (display == 'block') return true;
    return false;
}

function hide_classes() {
    if (!classes_is_showing()) return;
    var side_bar_width = parseInt($('#sp-side-bar').css('width'));
    var class_list_width = parseInt($('#sp-class-list').css('width'));
    $('#sp-class-list').animate({left: (side_bar_width - class_list_width) + 'px', opacity:'0'}, 500, function(){
        $('#sp-class-list').css('display', 'none');
    });
}

function show_classes() {
    //console.log('zz');
    if (classes_is_showing()) {
        hide_classes();
        return;
    }
    var side_bar_width = parseInt($('#sp-side-bar').css('width'));
    var class_list_width = parseInt($('#sp-class-list').css('width'));
    var src = $('#sp-class-list').attr('src');
    $('#sp-class-list').attr('src', src);
    $('#sp-class-list').css('display', 'block')
        .css('left', (side_bar_width - class_list_width) + 'px')
        .css('opacity', '1');

    //$('#sp-side-bar').css('z-index', 10000000);
    $('#sp-class-list').animate({left: $('#sp-side-bar').outerWidth() + 'px'}, 500, function(){
        //$('#sp-class-list').css('z-index', '0');
    });
}


function show_all_post(){
    hide_classes();
    $('#sp-admin-content').attr('src', 'postlist.jsp?uid=' + $('#sp-admin-uid').val());
}


function show_posts_in(tag) {
	hide_classes();
	$('#sp-admin-content').attr('src', encodeURI('postlist.jsp?tag='+tag+'&uid=' + $('#sp-admin-uid').val()));
}

function show_post_content(pid) {
	hide_classes();
	$('#sp-admin-content').attr('src', encodeURI('content.jsp?pid='+pid));
}