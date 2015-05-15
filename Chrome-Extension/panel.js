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

$(document).ready(function(){
    //var url = window.location.href;
    var title = decodeURI(getUrlVar('title'));
    $('#super-pocket-panel-title').val(title);

    $('#super-pocket-panel-close-button').click(function(){
        parent.postMessage({name:'close-panel'}, '*');
    });

    $('#super-pocket-panel-save-button').click(function(){
        parent.postMessage({name:'save-result', data:'asd'}, '*');
    });

    window.addEventListener('message', function(event){
        console.log(event.data);
        var labels = event.data.labels;
        for (var i = 0; i < labels.length; ++i)
        $('#recommend-content-labels').append('<input type="checkbox">' + labels[i]+'<br>');


    });


    //console.log(window.parent.document);
});