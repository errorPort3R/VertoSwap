var socket;

function start() {
    var ws = new SockJS("/socket")
    socket = Stomp.over(ws)

    socket.connect({}, onSocketConnected)
}

function onSocketConnected() {
    socket.subscribe("/topic/chat/"+$('#conversation').val(), onReceiveMessage)
}

function onReceiveMessage(mess) {
    data = JSON.parse(mess.body);
    $('#divlist').prepend("<div id='divlabel'>" + data.name + "<br />" + data.time + "</div>   <div id='divmsg'>" + data.body +  "</div><br />");
    if (mess === undefined)
    {
        return;
    }
}

function sendMessage() {
    var t = timeNow();
    var s = JSON.stringify({body: $('#body').val(), time: t, itemid: $('#itemid').val(), conversation: $('#conversation').val(), receiverid: $('#receiverid').val(), name: $('#name').val()});
    socket.send("/topic/chat/"+$('#conversation').val(), {}, s);
}

function timeNow() {
  var d = new Date(),
      mo = d.getMonth();
      day = d.getDate();
      h = d.getHours(),
      m = (d.getMinutes()<10?'0':'') + d.getMinutes();
  return mo + '/' + day + '-' + h + ':' + m;
}

start();