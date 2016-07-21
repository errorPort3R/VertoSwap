var ws;
var stompClient;

$(document).ready(function(){
    $('modal-trigger').leanModal();

    $('sendMessageButton').click(function() {
        sendForm();

    $('#messageText').val('');
    $('#newMessageModal').closeModal();
    });


    ws = new WebSocket('ws://localhost:8080/messages');

    ws.onmessage = function(data)
    {
        $('#messages').prepend('<div class="row"><div class="col s12">')
    }

});



function sendForm() {
ws..send($('#messageText').val());

};