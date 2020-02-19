const reactiveWebSocket = new WebSocket("ws://localhost:8080/web-socket/reactive");

reactiveWebSocket.onopen = () => {
    console.debug('Connected');
    let json = {
        message: 'Hello-Reactive',
        recipient: 'vova@reactive-admin.com'
    };

    reactiveWebSocket.send(JSON.stringify(json));
};

reactiveWebSocket.onmessage = event => {
    console.log('Message received: ', event.data);
};