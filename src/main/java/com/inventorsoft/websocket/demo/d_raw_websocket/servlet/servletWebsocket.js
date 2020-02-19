const servletWebSocket = new WebSocket("ws://localhost:8080/web-socket");

servletWebSocket.onopen = () => {
    console.debug('Connected');
    let json = {
        message: 'hello',
        recipient: 'vova@admin.com'
    };

    servletWebSocket.send(JSON.stringify(json));
};

servletWebSocket.onmessage = event => {
    console.log('Message received: ', event.data);
};