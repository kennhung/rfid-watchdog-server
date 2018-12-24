var doorsTable = $("#doorsTable").DataTable({
    "paging": false,
    "ordering": true,
    "info": true,
    "columnDefs": [
        {
            "render": function (data) {
                return JSON.parse(data).length;
            },
            "targets": 3
        },
        {
            "render": function () {
                return '<button type="button" class="btn btn-warning btn-sm editBtn">Edit</button> <button type="button" class="btn btn-info btn-sm pbEditBtn">Permissions</button> <button type="button" class="btn btn-danger btn-sm deleteBtn">Delete</button>';
            },
            "targets": 4
        }
    ]
});

$("#doorsTable_wrapper .col-md-6:eq(0)").append("<button type=\"button\" id=\"newDoor\" class=\"btn btn-outline-dark btn-sm editBtn\">New Door</button>");

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

function getDoors() {
    websocket.send("getDoors", "all");
}

var groupsList;

websocket = new WatchdogWebsocket(6085, "/doors", {
    onload: function (event) {
        getDoors();
        websocket.send("getGroups", "all");
    },
    doorsList: function (event) {
        renewDoorsList(event);
    },
    groupsList: function (event) {
        groupsList = event.data;
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


// permission_blocks

var editPermissionBlocksTable = $("#editPermissionBlocksTable").DataTable({
    "paging": false,
    "searching": false,
    "bInfo": false,
    "columnDefs": [
        {
            "render": function (data, type, row) {
                return data;
            },
            "targets": 2
        }
    ]
});

$("#editPermissionBlocksTable_wrapper .col-md-6:eq(0)").append("<button type=\"button\" id=\"newPermissionBlock\" class=\"btn btn-outline-primary btn-sm editBtn\">New PermissionBlock</button>");

$('#doorsTable tbody').on('click', '.pbEditBtn', function () {
    var parent = $(this).parent().parent();
    var data = doorsTable.row(parent).data();
    console.log(data);

    editPermissionBlocksTable.clear();
    var pbs = JSON.parse(data[3]);
    console.log(pbs);
    pbs.forEach(function (p) {
        editPermissionBlocksTable.row.add([p.targetId, p.validate, JSON.stringify(p.permission)]).draw();
    });
    $("#pbJson").val(data[3]);

    $("#pbEditModal").modal('show');
});

function lockPBInput(){
    $("#pb_targetId").attr("disabled", true).val("");
    $("#pb_validate").attr("disabled", true).val("");
    $("#pbUpdate").attr("disabled", true);
}

function unlockPBInput(){
    $("#pb_targetId").removeAttr("disabled");
    $("#pb_validate").removeAttr("disabled");
    $("#pbUpdate").removeAttr("disabled");
}


$('#editPermissionBlocksTable tbody').on('click', 'tr', function () {
    if ($(this).hasClass('table-info')) {
        $(this).removeClass('table-info');

        lockPBInput();
    } else {
        editPermissionBlocksTable.$('tr.table-info').removeClass('table-info');
        $(this).addClass('table-info');

        var data = editPermissionBlocksTable.row(this).data();
        console.log(data);

        unlockPBInput();
        $("#pb_targetId").val(data[0]);
        pbDateTimePicker.setDate(data[1].toString());
    }
});

var pbDateTimePicker = flatpickr("#pb_validate", {
    enableTime: true,
    dateFormat: "U",
    time_24hr: true,
    plugins: [new confirmDatePlugin({})],
    static: false
})

$("#newPermissionBlock").on('click', function () {
    $("#pb_targetId").removeAttr("disabled").val("");
    $("#pb_validate").removeAttr("disabled").val("");
    $("#pbUpdate").removeAttr("disabled");
    pbDateTimePicker.setDate();
});

$("#pbUpdate").on('click', function () {
    var pbRow = $('#editPermissionBlocksTable').dataTable().fnFindCellRowIndexes($("#pb_targetId").val(), 0);
    if (pbRow.length <= 0) {
        editPermissionBlocksTable.row.add([$("#pb_targetId").val(), $("#pb_validate").val(), JSON.stringify([])]).draw();
    } else {
        editPermissionBlocksTable.row(pbRow).data([$("#pb_targetId").val(), $("#pb_validate").val(), editPermissionBlocksTable.row(pbRow).data()[2]]).draw();
    }
    lockPBInput();
});