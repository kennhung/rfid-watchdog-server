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

    $("#editId").val(data[0]);
    $("#editName").val(data[1]);
    $("#editAuthToken").val(data[2]);
    $("#editPermissionBlocks").val(data[3]);
    $("#editDoorModal").modal('show');
});

$('#doorsTable tbody').on('click', '.deleteBtn', function () {
    var parent = $(this).parent().parent();
    var data = doorsTable.row(parent).data();
    $("#deleteConfirmSpan").html(data[1]);
    $("#deleteConfirmSpan").attr('data', data[0]);
    $("#deleteConfirmModal").modal('show');
});

var renewDoorsList = function (event) {
    var doorsList = JSON.parse(event.data);
    doorsTable.clear();
    doorsList.forEach(function (data) {
        doorsTable.row.add([data.id, data.name, data.auth_token, JSON.stringify(data.permission_blocks)]).draw();
    });
};

var websocket;

function getDoors() {
    websocket.send("getDoors", "all");
}

function renewPBGroups(data){
    var pbGroupsSelect = $("#pbGroupsSelect");
    data.forEach(function (d) {
        pbGroupsSelect.append('<a class="dropdown-item" onclick="putPbTargetId('+d.id+')">'+d.name+'</a>');
    });
}

websocket = new WatchdogWebsocket(6085, "/doors", {
    onload: function (event) {
        getDoors();
        websocket.send("getGroups", "all");
    },
    doorsList: function (event) {
        renewDoorsList(event);
    },
    groupsList: function (event) {
        renewPBGroups(JSON.parse(event.data));
    }
});

$("#newDoor").on('click', function () {
    $("#editId").val(0);
    $("#editName").val("");
    $("#editAuthToken").val("");
    $("#editPermissionBlocks").val(JSON.stringify([]));
    $("#editDoorModal").modal('show');
});

$("#editDoorSave").on('click', function () {
    var editDoor = {
        id: $("#editId").val(),
        name: $("#editName").val(),
        auth_token: $("#editAuthToken").val(),
        permission_blocks: JSON.parse($("#editPermissionBlocks").val())
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

    lockPBInput();

    $("#pbEditId").val(data[0]);
    $("#pbEditId").html(data[1] + "(" + data[0] + ")");

    editPermissionBlocksTable.clear().draw();
    var pbs = JSON.parse(data[3]);

    pbs.forEach(function (p) {
        editPermissionBlocksTable.row.add([p.targetId, p.validate, JSON.stringify(p.permission)]).draw();
    });
    $("#pbJson").val(data[3]);

    $("#pbEditModal").modal('show');
});

function lockPBInput() {
    $("#pb_targetId").attr("disabled", true).val("");
    $("#pb_validate").attr("disabled", true).val("");
    $("#pbUpdate").attr("disabled", true);
    $("#pbDeleteSelected").attr("disabled", true);
    $("#pbGroupsDrop").attr("disabled", true);
}

function unlockPBInput() {
    $("#pb_targetId").removeAttr("disabled");
    $("#pb_validate").removeAttr("disabled");
    $("#pbUpdate").removeAttr("disabled");
    $("#pbDeleteSelected").removeAttr("disabled");
    $("#pbGroupsDrop").removeAttr("disabled");
    pbDateTimePicker.setDate();
}


$('#editPermissionBlocksTable tbody').on('click', 'tr', function () {
    if (editPermissionBlocksTable.rows().data().length > 0) {
        if ($(this).hasClass('table-info')) {
            $(this).removeClass('table-info');
            lockPBInput();
        } else {
            editPermissionBlocksTable.$('tr.table-info').removeClass('table-info');
            $(this).addClass('table-info');

            var data = editPermissionBlocksTable.row(this).data();

            unlockPBInput();
            $("#pb_targetId").val(data[0]);
            pbDateTimePicker.setDate(data[1].toString());

            var permissions = JSON.parse(data[2]);
            if(permissions.open){
                $("#openCheckBox").prop('checked',true);
            }
            else{
                $("#openCheckBox").prop('checked',false);
            }

            if(permissions.admin){
                $("#adminCheckBox").prop('checked',true);
            }
            else{
                $("#adminCheckBox").prop('checked',false);
            }
        }
    }
});

var pbDateTimePicker = flatpickr("#pb_validate", {
    enableTime: true,
    dateFormat: "U",
    time_24hr: true,
    plugins: [new confirmDatePlugin({})],
    static: false
});

$("#newPermissionBlock").on('click', function () {
    $("#pb_targetId").val("");
    $("#pb_validate").val("");
    unlockPBInput();
});

$("#pbUpdate").on('click', function () {
    var targetId = $("#pb_targetId").val();

    var pbRow = $('#editPermissionBlocksTable').dataTable().fnFindCellRowIndexes(targetId, 0);
    if (pbRow.length <= 0) {
        editPermissionBlocksTable.row.add([targetId, $("#pb_validate").val(), JSON.stringify({
            open: $("#openCheckBox").prop('checked'),
            admin: $("#adminCheckBox").prop('checked')
        })]).draw();
    } else {
        editPermissionBlocksTable.row(pbRow).data([targetId, $("#pb_validate").val(), JSON.stringify({
            open: $("#openCheckBox").prop('checked'),
            admin: $("#adminCheckBox").prop('checked')
        })]).draw();
    }
    lockPBInput();
    $("#editPermissionBlocksTable tbody tr").removeClass('table-info');
});

$("#pbDeleteSelected").on('click', function () {
    var targetId = $("#pb_targetId").val();

    var pbRow = $('#editPermissionBlocksTable').dataTable().fnFindCellRowIndexes(targetId, 0);
    if (pbRow.length > 0) {
        editPermissionBlocksTable.row(pbRow).remove();
    }
    lockPBInput();
    editPermissionBlocksTable.draw();
});

$("#editPbSave").on('click', function () {
    var pbData = editPermissionBlocksTable.rows().data();
    var pbRows = [];

    editPermissionBlocksTable.rows().every(function (value) {
        var data = this.data();

        var pb = {
            targetId: data[0],
            validate: data[1],
            permission: JSON.parse(data[2])
        };

        pbRows.push(pb);
    });

    websocket.send("updatePermissionBlocks", JSON.stringify({
        target: parseInt($("#pbEditId").val(), 10),
        permissionBlocks: pbRows
    }));
    getDoors();
    console.log(pbRows)
    $("#pbEditModal").modal('hide');
});