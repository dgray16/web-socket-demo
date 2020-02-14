setInterval(
    () => {
        fetch('http://localhost:8080/patient/profile').then(data => {
            console.debug('Blocking back-end');
            console.debug(data);
        });

        fetch('http://localhost:8080/patient/profile/reactive').then(data => {
            console.debug('Reactive back-end');
            console.debug(data);
        });
    },
    5000
);

