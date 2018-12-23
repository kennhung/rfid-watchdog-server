var doorsTable = $("#doorsTable").DataTable({
    "paging": false,
    "ordering": true,
    "info": true,
    "columnDefs": [
        {
            "render": function (data, type, row) {
               return JSON.parse(data).length;
            },
            "targets": 3
        },
        {
            "render": function (data, type, row) {
                return '<button type="button" class="btn btn-warning btn-sm editBtn">Edit</button> <button type="button" class="btn btn-danger btn-sm deleteBtn">Delete</button>';
            },
            "targets": 4
        }
    ]
});

$("#doorsTable_wrapper .col-md-6:eq(0)").append("<button type=\"button\" id=\"newDoor\" class=\"btn btn-outline-dark btn-sm editBtn\">New Door</button>")

$('#doorsTable tbody').on('click', '.editBtn', function () {
    var parent = $(this).parent().parent();
    var data = doorsTable.row(parent).data();
    console.log(data);
    $("#editId").val(data[0]);
    $("#editName").val(data[1]);
    $("#editAuth_token").val(data[2]);
    $("#editPermission_Blocks").val(data[3]);
    $("#editDoorModal").modal('show');
});

$('#doorsTable tbody').on('click', '.deleteBtn', function () {
    var parent = $(this).parent().parent();
    var data = doorsTable.row(parent).data();
    console.log(data);
    $("#deleteConfirmSpan").html(data[1]);
    $("#deleteConfirmSpan").attr('data', data[0]);
    $("#deleteConfirmModal").modal('show');
});

var renewDoorsList = function (event) {
    var doorsList = JSON.parse(event.data);
    console.log(doorsList);
    doorsTable.clear();
    doorsList.forEach(function (data) {
        doorsTable.row.add([data.id, data.name, data.auth_token, JSON.stringify(data.permission_blocks)]).draw();
    });
};

function getDoors(){
    websocket.send("getDoors", "all");
}

websocket = new WatchdogWebsocket(6085, "/doors", {
    onload: function (event) {
        getDoors();
    },
    doorsList: function (event) {
        renewDoorsList(event);
    }
});

$("#newDoor").on('click', function () {
    $("#editId").val(0);
    $("#editName").val("");
    $("#editAuth_token").val("");
    $("#editPermission_Blocks").val(JSON.stringify([]));
    $("#editDoorModal").modal('show');
});

$("#editDoorSave").on('click', function () {
    var editDoor = {
        id: $("#editId").val(),
        name: $("#editName").val(),
        auth_token: $("#editAuth_token").val(),
        permission_blocks: JSON.parse($("#editPermission_Blocks").val())
    };
    websocket.send("saveDoor", JSON.stringify(editDoor));
    $("#editDoorModal").modal('hide');
    getDoors();
});

$("#deleteConfirm").on('click', function () {
    var deleteId = $("#deleteConfirmSpan").attr("data");
    websocket.send("deleteDoor", deleteId);
    $("#deleteConfirmModal").modal('hide');
    getDoors();
});