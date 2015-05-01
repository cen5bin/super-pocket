var url_queue = [];
chrome.tabs.query({'active': true}, function (tabs) {
    var url = tabs[0].url;
    console.log(url);
    console.log(document.body.outerHTML)
    if (url_queue.indexOf(url) == -1) {
        url_queue.push(url);
        
    chrome.tabs.sendRequest(tabs[0].id, {method:'getHTML'}, function(response){
        alert('yes');
        console.log('yes');
        url_queue.splice(url_queue.indexOf(url), 1);
    });
    }
});
