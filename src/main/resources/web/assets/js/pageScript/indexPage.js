var UpdateBasicInfo = function (event) {
    var data = JSON.parse(event.data);
    $("#version").html(data.sysVersion);
    var day = Math.floor(data.uptime/(3600*24));
    var hr = Math.floor(data.uptime%(3600*24)/3600);
    var min = Math.floor(data.uptime%(3600)/60);
    var uptimeStr = "";
    if(day != 0) uptimeStr += day+" day ";
    if(hr != 0) uptimeStr += hr+" hr ";
    if(min != 0) uptimeStr += min+" min ";
    uptimeStr += Math.floor(data.uptime%60)+" sec";
    $("#uptime").html(uptimeStr);
    console.log(JSON.parse(data.network));
    resolveNetworkInfo(JSON.parse(data.network));
}

websocket = new WatchdogWebsocket(6085, "/index", {
    onload: function(event){
        websocket.send("getBasicInfo", "all");
    },
    UpdateBasicInfo: function (event) {
        UpdateBasicInfo(event);
    }
});

function resolveNetworkInfo(arr) {
    $("#networkInfoList").html("");
    arr.forEach(function (data) {
        var name = data.displayName;
        var mac = data.mac;
        var out = "<li class=\"list-group-item\"><h6 class=\"mb-1\">"+name+"</h6>";
        out += "<span class='badge badge-secondary'>Mac: </span> "+mac+"<br>";
        var address = "";
        var mask = "";
        data.address.forEach(function (addr) {
            address += addr.address+" ";
            mask += addr.maskAddress+" ";
        })
        out += address+"<br>";
        out += mask+"<br>";
        out += "</li>";
        $("#networkInfoList").append(out);
    })
}