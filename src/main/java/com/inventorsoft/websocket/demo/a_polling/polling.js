setInterval(function () {
    $.ajax({
        url: "http://localhost:8080/patient/profile",
        dataType: "json",
        success: function (data) {
            console.log(data);
        }
    });
}, 5000);

