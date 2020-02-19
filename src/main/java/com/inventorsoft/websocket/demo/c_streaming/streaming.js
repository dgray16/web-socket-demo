const blockingEventSource = new EventSource('http://localhost:8080/users-stream');
let blockingIterator = 0;

blockingEventSource.addEventListener('sse', event => {
    console.debug(event);
    blockingIterator++;
    if (blockingIterator >= 10) {
        blockingEventSource.close();
    }
});

const reactiveEventSource = new EventSource('http://localhost:8080/users-stream/reactive');
let reactiveIterator = 0;

reactiveEventSource.addEventListener('sse-reactive', event => {
    console.info(event);
    reactiveIterator++;
    if (reactiveIterator >= 10) {
        reactiveEventSource.close();
    }
});
