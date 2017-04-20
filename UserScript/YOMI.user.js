// ==UserScript==
// @name        YOMI
// @namespace   yomi
// @author      Eugene Lin
// @description Enable with f2. Highlight text to read yomigata.
// @include     *
// @grant       GM_xmlhttpRequest
// @version     1
// ==/UserScript==


//constants or globals
var YomiEnabled = false;
var YomiNotificationZIndex = 10001;
var YomiMaxLength = 300;
var YomiPreloaderData = "data:image/gif;base64,R0lGODlhEAAFAMIAAMzOzNze3Nza3Ozq7NTS1OTi5O7u7gAAACH/C05FVFNDQVBFMi4wAwEAAAAh+QQJDQAGACwAAAAAEAAFAAADHxhEELOADMUUiKDcazG7ROB10cdoG8qEq/AsRGERQgIAIfkECQ0ADAAsAAAAABAABQCDjIqMzM7MtLK03N7clJKU3NrcvLq87OrsjI6M1NLUtLa05OLk7u7uAAAAAAAAAAAABCfQIILUSSmkM3AYBCACQ6B92YmMwGKa7pusY5kuWa6MwnEmi46mEAEAIfkECQ0ADAAsAAAAABAABQCDjIqMvL681NLUlJaU3N7clJKUzM7MjI6MxMLE3NrcnJqc5OLk7u7uAAAAAAAAAAAABCSQCGHIGuUoIqk0BoUAJBBQYQoKYxmAKyqWJmV/QkIc5KGniQgAIfkECQ0ADAAsAAAAABAABQCDjIqMzM7MtLK03N7clJKU3NrcvLq87OrsjI6M1NLUtLa05OLk7u7uAAAAAAAAAAAABChwpBTGmSEdgwhSVBYsQVkRQAqE2SAmA6KuJUWai6xKWXFNiYVCJYgAADtyVVlhZDEvYlZkQS9VMkE4ZGhITXZaVG5HWFhQOTk0a0hKOVJkMFMydmhSVWlOc3JWLzBRU0RlVzYrc3o0YmlV";

// ADd hotkey. script is disable by default,
//because we want it to just work with every site,
//but not bother you when you are doing other things
function YomiCheckHotKey(e) {
    if (e.keyCode == 113) {
        YomiEnabled = !YomiEnabled;
        var sMessage = "";
        if (YomiEnabled){
            sMessage = "YOMI <span style='color:#008800;'>Enabled</span>";
        }else{
            sMessage = "YOMI <span style='color:#880000;'>Disabled</span>";
        }
        YomiShowNotification(sMessage);
    }
}
function YomiShowNotification(sMessageHTML){
    var YomiNotification = document.createElement("div");
    YomiNotification.innerHTML = sMessageHTML;
    YomiNotification.className += " YomiGenericDiv YomiNotificationDiv ";
    YomiNotification.style.zIndex = YomiNotificationZIndex;
    YomiNotificationZIndex += 1;
    document.body.appendChild(YomiNotification);
    setTimeout(function(){
        YomiFadeOutNotification(YomiNotification);
    }, 800);
}
function YomiFadeOutNotification(oDiv){
    try{
        oDiv.style.opacity = "0.0";
        setTimeout(function(){
            YomiRemoveNotification(oDiv);
        }, 500);
    }catch(ex){
        oDiv.remove();
    }
}
function YomiRemoveNotification(oDiv){
    oDiv.remove();
}
// register hotkey handler
document.addEventListener('keyup', YomiCheckHotKey, false);

var oStyleElement = document.createElement('style');
document.documentElement.firstChild.appendChild(oStyleElement);

oStyleElement.innerHTML += " #YomiResultDiv { ";
oStyleElement.innerHTML += "  z-index:10000;";
oStyleElement.innerHTML += " }";

oStyleElement.innerHTML += " .YomiGenericDiv { ";
oStyleElement.innerHTML += "  font-family:verdana; font-size:16px; background-color:#f5f5f5;";
oStyleElement.innerHTML += "  border-width:1px; border-color:#cccccc; border-style:solid; ";
oStyleElement.innerHTML += "  position:absolute; box-shadow:1px 1px 3px rgba(0,0,0,.4);";
oStyleElement.innerHTML += "  background-color:white; background-color:rgba(255,255,255,.9);";
oStyleElement.innerHTML += "  min-width:100px; max-width:600px; padding:5px; user-select: none; ";
oStyleElement.innerHTML += "  -ms-user-select: none; -moz-user-select: none;";
oStyleElement.innerHTML += "  -khtml-user-select: none; -webkit-user-select: none; ";
oStyleElement.innerHTML += "  -webkit-touch-callout: none; border-radius:4px;";
oStyleElement.innerHTML += " }";

oStyleElement.innerHTML += " .YomiNotificationDiv { ";
oStyleElement.innerHTML += "  transition: opacity 0.2s linear; position:fixed;";
oStyleElement.innerHTML += "  width: 200px; position: fixed; left: 50%; margin-left: -100px;";
oStyleElement.innerHTML += "  top:10px; text-align:center;";
oStyleElement.innerHTML += " }";

var YomiResultDiv = document.createElement("div");
YomiResultDiv.id = "YomiResultDiv";
YomiResultDiv.innerHTML = "";
YomiResultDiv.className += " YomiGenericDiv ";
YomiResultDiv.style.top = "-100px";
YomiResultDiv.style.left = "-100px";
document.body.appendChild(YomiResultDiv);

var currentSelectionPos = {top:0, left:0, right :0};

function getSelectionText() {
    var text = "";
    if (window.getSelection) {
        var selection = window.getSelection();
        text = selection.toString();
    } else if (document.selection && document.selection.type != "Control") {
        text = document.selection.createRange().text;
    }
    return text;
}

setInterval(ProcessSelection, 250);

var PreviousYomiSelection = "";

var currentMousePos = { x: -1, y: -1 };

function ProcessSelection(){
    var sInput = getSelectionText();
    var oResultDiv = document.getElementById("YomiResultDiv");

    if (sInput === null || sInput === "" || !sInput){
        oResultDiv.innerHTML = "";
        oResultDiv.style.top = "-100px";
        oResultDiv.style.left = "-100px";
        PreviousYomiSelection = "";
        return;
    }

    if (!YomiEnabled){
        return;
    }

    if (sInput.length > YomiMaxLength){
        sInput = sInput.substring(0,YomiMaxLength);
    }

    //do not process if it was the same as earlier
    if (PreviousYomiSelection === sInput){
        return;
    }

    //if there is selection, try to get rectangle locations.
    try
    {
        var selection = window.getSelection();
        text = selection.toString();
        range = selection.getRangeAt(0);
        clientRects = range.getClientRects();
        currentSelectionPos = {top:0, bottom:0, left:0, right :0};
        if (clientRects.length > 0){
            currentSelectionPos.top = clientRects[0].top;
            currentSelectionPos.bottom = clientRects[0].bottom;
            currentSelectionPos.left = clientRects[0].left;
            currentSelectionPos.right = clientRects[0].right;
        }
        for (var n = 1; n < clientRects.length ; n++){
            var clientRect = clientRects[n];
            if (clientRect.top < currentSelectionPos.top){
                currentSelectionPos.top = clientRect.top;
            }
            if (clientRect.left < currentSelectionPos.left){
                currentSelectionPos.left = clientRect.left;
            }
            if (clientRect.right > currentSelectionPos.right){
                currentSelectionPos.right = clientRect.right;
            }
        }
    }catch (ex){
        console.warn(ex.toString());
    }

    if (PreviousYomiSelection === ""){
        oResultDiv.innerHTML = "<div style='text-align:center;'><img class='WhoaIAmLoadingLol' src='" + YomiPreloaderData + "' /></div>";
    }
    PreviousYomiSelection = sInput;

    YomiSetDivPosition(oResultDiv);


    GM_xmlhttpRequest ( {
        method:     "GET",
        url:       "http://djdenpa.com:57380/?color=Y&q=" + sInput,
        headers:    {
            "Content-Type": "application/x-www-form-urlencoded"
        },
        onload:     function (response) {
            oResultDiv.innerHTML = (response.responseText);
            YomiSetDivPosition(oResultDiv);
        }
    } );

}

function YomiSetDivPosition(oDiv){
    var newLeft = currentSelectionPos.left;
    oDiv.style.left = (currentSelectionPos.left) + "px";
    oDiv.style.top = (currentSelectionPos.top - oDiv.clientHeight - 30 + window.scrollY) + "px";
}


