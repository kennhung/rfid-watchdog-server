var usersTable = $("#usersTable").DataTable({
    "paging": false,
    "ordering": true,
    "info": true,
    "columnDefs": [
        {
            "render": function (data, type, row) {
                var out = "";
                var jdata = JSON.parse(data);
                for (var i = 0; i < jdata.length; i++) {
                    var group = getGroupById(groups,jdata[i]);
                    if(group != undefined){
                        out += group.name;
                        out += ", ";
                    }
                }
                return out.substr(0,out.length-2);
            },
            "targets": 3
        },
        {
            "render": function (data, type, row) {
                return '<button type="button" class="btn btn-warning btn-sm editBtn">Edit</button> <button type="button" class="btn btn-danger btn-sm deleteBtn">Delete</button>';
            },
            "targets": 8
        }
    ]
});

$("#usersTable_wrapper .col-md-6:eq(0)").append("<button type=\"button\" id=\"newUser\" class=\"btn btn-outline-dark btn-sm editBtn\">New User</button>")

$('#usersTable tbody').on('click', '.editBtn', function () {
    var parent = $(this).parent().parent();
    var data = usersTable.row(parent).data();
    console.log(data);
    $("#editId").val(data[0]);
    $("#editUID").val(data[1]);
    $("#editName").val(data[2]);
    $("#editGroups").val(data[3]);
    $("#editMeta").val(data[4]);
    $("#editValidate").val(data[5]);
    $("#editEnable").prop("checked", (data[6]?true:false));
    $("#editPassword").val(data[7]);
    $("#editUserModal").modal('show');
});

$('#usersTable tbody').on('click', '.deleteBtn', function () {
    var parent = $(this).parent().parent();
    var data = usersTable.row(parent).data();
    console.log(data);
    $("#deleteConfirmSpan").html(data[2] + "(" + data[1] + ")");
    $("#deleteConfirmSpan").attr('data', data[0]);
    $("#deleteConfirmModal").modal('show');
});

var renewUsersList = function (event) {
    var userList = JSON.parse(event.data);
    console.log(userList);
    usersTable.clear();
    userList.forEach(function (data) {
        usersTable.row.add([data.id, data.uid, data.name, data.groups, data.metadata, data.validate, data.enable, data.password]).draw();
    });
}

var groups = [];


websocket = new WatchdogWebsocket(6085, "/users", {
    onload: function (event) {
        getGroups();
        getUsers();
    },
    usersList: function (event) {
        renewUsersList(event);
    },
    groupsList: function (event) {
        groups = JSON.parse(event.data);
    }
});

function getUsers() {
    websocket.send("getUsers", "all");
}

function getGroups(){
    websocket.send("getGroups", "all");
}

$("#newUser").on('click', function () {
    $("#editId").val(0);
    $("#editUID").val("");
    $("#editName").val("");
    $("#editMeta").val(JSON.stringify({}));
    $("#editGroups").val(JSON.stringify([]));
    $("#editValidate").val(Math.floor(Date.now() / 1000));
    $("#editEnable").prop("checked", true);
    $("#editPassword").val("");
    $("#editUserModal").modal('show');
})

$("#editUserSave").on('click', function () {
    var editUser = {
        id: $("#editId").val(),
        uid: $("#editUID").val(),
        name: $("#editName").val(),
        metadata: $("#editMeta").val(),
        groups: $("#editGroups").val(),
        validate: $("#editValidate").val(),
        enable: $("#editEnable").prop("checked"),
        password: $("#editPassword").val()
    };
    websocket.send("saveUser", JSON.stringify(editUser));
    $("#editUserModal").modal('hide');
    getUsers();
})

$("#deleteConfirm").on('click', function () {
    var deleteId = $("#deleteConfirmSpan").attr("data");
    websocket.send("deleteUser", deleteId);
    $("#deleteConfirmModal").modal('hide');
    getUsers();
})