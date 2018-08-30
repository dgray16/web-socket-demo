$.getScript('stomp.js', function (data) {
    console.log('Stomp has been loaded');
    let stompClient = Stomp.client('ws://localhost:8080/web-socket');

    stompClient.connect({}, function () {
        stompClient.subscribe('/get-data', function (message) {
            var json = JSON.parse(message.body);
            json.forEach(object => console.log(`Response from server: ${object.message}'`));
        });

        let json = {
            message: 'Hello back-end!'
        };

        stompClient.send('/send-data', {}, JSON.stringify(json));
    });
});

