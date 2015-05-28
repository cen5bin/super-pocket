/**
 * Created by wubincen on 15/5/28.
 */


$(document).ready(function(){

    $('#super-pocket-panel-save-button').click(function () {
        //alert('zz');
        var sel=document.getElementById("sp-setting-select");
        var val=sel.options[sel.selectedIndex].value;
        parent.postMessage({name:'choose-method', method_id: val}, '*');
    });
});