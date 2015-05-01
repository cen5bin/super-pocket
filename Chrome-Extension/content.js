var getHTML = function() {
    alert('zz');
};

chrome.extension.onRequest.addListener(
        function(request, sender, sendRensponse){
            if (request.method == 'getHTML') {
                sendRensponse({data:'asd', method : 'getHTML'});
            }
        });

