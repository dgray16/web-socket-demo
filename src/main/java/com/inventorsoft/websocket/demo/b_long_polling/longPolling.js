setInterval(
    () => {
        fetch('http://localhost:8080/users').then(data => {
            console.debug('Blocking back-end');
            console.debug(data);
        });

        fetch('http://localhost:8080/users/reactive').then(data => {
            console.debug('Reactive back-end');
            console.debug(data);
        });
    },
    15000
);