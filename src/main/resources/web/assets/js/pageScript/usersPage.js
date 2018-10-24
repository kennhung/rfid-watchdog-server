var usersTable = $("#usersTable").DataTable({
    "paging": false,
    "ordering": true,
    "info": true
});

$('#usersTable tbody').on('click', 'tr', function () {
    var data = usersTable.row(this).data();
    console.log(data);
    $("#editId").val(data[0]);
    $("#editUID").val(data[1]);
    $("#editName").val(data[2]);
    $("#editMeta").val(data[3]);
    $("#editGroups").val(data[4]);
    $("#editUserModal").modal('show');
});


websocket = new WatchdogWebsocket(6085, "/users", {
    onload: function (event) {
        getUsers();
    },
    usersList: function (event) {
        var userList = JSON.parse(event.data);
        console.log(userList);
        usersTable.clear();
        userList.forEach(function (data) {
            usersTable.row.add([data.id, data.uid, data.name, data.metadata, data.groups]).draw();
        });
    }
});

function getUsers(){
    websocket.send("getUsers", "all");
}


$("#editUserSave").on('click',function () {
    var editUser = {
        id: $("#editId").val(),
        uid: $("#editUID").val(),
        name: $("#editName").val(),
        metadata: $("#editMeta").val(),
        groups: $("#editGroups").val()
    };
    websocket.send("saveUser", JSON.stringify(editUser));
    $("#editUserModal").modal('hide');
    getUsers();
})