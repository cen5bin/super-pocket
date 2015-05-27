/**
 * Created by wubincen on 15/5/10.
 */

function getUrlVars(){
    var vars = [], hash;
    var hashes = window.location.href.slice(window.location.href.indexOf('?') + 1).split('&');
    for(var i = 0; i < hashes.length; i++)
    {
        hash = hashes[i].split('=');
        vars.push(hash[0]);
        vars[hash[0]] = hash[1];
    }
    return vars;
}

function getUrlVar(name){
    return getUrlVars()[name];
}

//获取分类结果
function getLabels() {
    var ret = [];
    $('#recommend-content-labels > input[type=checkbox]').each(function(){
        if (this.checked) ret.push($(this).val());
    });

    var custom_labels = $('#custom-labels').val();
    if (custom_labels.length == 0) return ret;
    console.log(custom_labels);
    custom_labels = custom_labels.split(',');
    for (var i = 0; i < custom_labels.length; ++i)
        ret.push(custom_labels[i]);
    return ret;
}

$(document).ready(function(){
    //var url = window.location.href;
    var title = decodeURI(getUrlVar('title'));
    $('#super-pocket-panel-title').val(title);

    $('#super-pocket-panel-close-button').click(function(){
        parent.postMessage({name:'close-panel'}, '*');
    });

    $('#super-pocket-panel-logout-button').click(function(){
        parent.postMessage({name:'logout'}, '*');
    });

    $('#super-pocket-panel-save-button').click(function(){
        //getLabels();
        parent.postMessage({name:'save-result', data:getLabels()}, '*');
    });

    document.onkeydown = function(event) {
        if (event.keyCode == 27) parent.postMessage({name:'close-panel'}, '*');
    };

    window.addEventListener('message', function(event){
        console.log(event.data);
        var labels = event.data.labels;
        for (var i = 0; i < labels.length; ++i)
        $('#recommend-content-labels').append('<input type="checkbox" value="' + labels[i] + '">' + labels[i] +'<br>');
    });

});