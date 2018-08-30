setInterval(function () {
    $.ajax({
        url: "http://localhost:8080/users",
        dataType: "json",
        success: function (data) {
            console.log(data);
        }
    });
}, 5000);