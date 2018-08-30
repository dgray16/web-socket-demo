var webSocket = new WebSocket("ws://localhost:8080/web-socket");

webSocket.onopen = function () {
    console.log('Connected');
    let json = {
        message: 'hello',
        recipient: 'vova@admin.com'
    };

    webSocket.send(JSON.stringify(json));
};

webSocket.onmessage = function (message) {
    console.log('Message received: ', message.data);
};

webSocket.onclose = function (event) {
    console.log('Connection closed');
};

webSocket.onerror = function (event) {
    console.log('Error happened');
};
