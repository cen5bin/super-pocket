chrome.browserAction.onClicked.addListener(function(tab){
    alert(tab.url);
});
