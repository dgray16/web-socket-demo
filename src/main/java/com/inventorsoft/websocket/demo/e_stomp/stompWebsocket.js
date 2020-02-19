let stompClient = StompJs.Stomp.client('ws://localhost:8080/web-socket');

stompClient.connect({}, () => {
    stompClient.subscribe('/get-data', message => {
        let json = JSON.parse(message.body);
        json.forEach(object => console.debug(`Response from server: ${object.message}'`));
    });

    let json = {
        message: 'Hello back-end!'
    };

    stompClient.send('/send-data', {}, JSON.stringify(json));
});

