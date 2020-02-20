let stompClient = StompJs.Stomp.client('ws://localhost:8080/web-socket');

stompClient.connect({}, () => {
    servlet();
    setTimeout(reactive, 5000);
});

servlet = () => {
    stompClient.subscribe('/get-data', message => {
        let jsonResult = JSON.parse(message.body);
        jsonResult.forEach(result => console.debug(`Response from server: ${result.message}'`));
    });

    let json = {
        message: 'Hello back-end!'
    };

    stompClient.send('/send-data', {}, JSON.stringify(json));
};

reactive = () => {
    stompClient.subscribe('/get-data/reactive', message => {
        let jsonResult = JSON.parse(message.body);
        jsonResult.forEach(result => console.debug(`Response from server (reactive): ${result.message}'`));
    });

    let json = {
        message: 'Hello reactive back-end!'
    };

    stompClient.send('/send-data/reactive', {}, JSON.stringify(json));
};
