var socket;
var sender;
var receiver;

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
    //$('#message-block').prepend("<div id='divlabel'>" + data.name + "<br />" + data.time + "</div><div class='talk-bubble tri-left round right-in'><div class='talktext'><p>" + data.body +  "</p></div></div><br /></div>");
    var possession;
    if (sender === data.name)
    {
        possession = "talk-bubble-mine";
    }
    else
    {
        possession = "talk-bubble-theirs";
    }
    var element = "<div>" +
                    "<div class='divlabel'>" +
                        data.name + "<br/>" +
                        data.time +
                    "</div>" +
                    "<div class='" + possession + "'>" +
                        "<div class='talktext'>" +
                            "<p>" +
                               data.body +
                            "</p>" +
                        "</div>" +
                    "</div>" +
                "</div>" +
                "<br />";

    $('#message-block').prepend(element);

    if (mess === undefined)
    {
        return;
    }
}

function sendMessage() {
    sender = $('#name').val();
    receiver = $('#receiverid').val();
    var t = timeNow();
    var s = JSON.stringify({body: $('#body').val(), time: t, itemid: $('#itemid').val(), conversation: $('#conversation').val(), receiverid: $('#receiverid').val(), name: $('#name').val()});
    socket.send("/topic/chat/"+$('#conversation').val(), {}, s);
}

function timeNow() {
  var d = new Date(),
      mo = ((d.getMonth()+1)<10?'0':'') + d.getMonth(),
      day = (d.getDate()<10?'0':'') + d.getDate(),
      h = (d.getHours()<10?'0':'') + d.getHours(),
      m = (d.getMinutes()<10?'0':'') + d.getMinutes();
  return mo + '/' + day + '-' + h + ':' + m;
}

start();