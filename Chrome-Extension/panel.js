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
});