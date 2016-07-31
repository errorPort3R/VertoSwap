var socket;

function start() {
    var ws = new SockJS("/socket")
    socket = Stomp.over(ws)

    socket.connect({}, onSocketConnected)
}

function onSocketConnected() {
    socket.subscribe("/topic/chat", onReceiveMessage)
}

function onReceiveMessage(mess) {
    data = JSON.parse(mess.body);
    $('#divlist').append("<div id='divlabel'>" + data.user + "<br />" + data.time + "</div>   <div id='divmsg'>" + data.msg +  "</div><br />");
    if (mess === undefined)
    {
        return;
    }
}

function sendMessage() {
    var t = timeNow();
    var s = JSON.stringify({msg: $('#msg').val(), time: t, user: "Mike"});
    socket.send("/topic/chat", {}, s);
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