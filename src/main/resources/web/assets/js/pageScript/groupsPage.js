var groupsTable = $("#groupsTable").DataTable({
    "paging": false,
    "ordering": true,
    "info": true,
    "columnDefs": [
        {
            "render": function (data, type, row) {
                return '<button type="button" class="btn btn-warning btn-sm editBtn">Edit</button> <button type="button" class="btn btn-danger btn-sm deleteBtn">Delete</button>';
            },
            "targets": 2
        }
    ]
});

$("#groupsTable_wrapper .col-md-6:eq(0)").append("<button type=\"button\" id=\"newGroup\" class=\"btn btn-outline-dark btn-sm editBtn\">New Group</button>")

$('#groupsTable tbody').on('click', '.editBtn', function () {
    var parent = $(this).parent().parent();
    var data = groupsTable.row(parent).data();
    console.log(data);
    $("#editId").val(data[0]);
    $("#editName").val(data[1]);
    $("#editGroupModal").modal('show');
});

$('#groupsTable tbody').on('click', '.deleteBtn', function () {
    var parent = $(this).parent().parent();
    var data = groupsTable.row(parent).data();
    console.log(data);
    $("#deleteConfirmSpan").html(data[1]);
    $("#deleteConfirmSpan").attr('data', data[0]);
    $("#deleteConfirmModal").modal('show');
});

var renewGroupsList = function (event) {
    var groupsList = JSON.parse(event.data);
    console.log(groupsList);
    groupsTable.clear();
    groupsList.forEach(function (data) {
        groupsTable.row.add([data.id, data.name]).draw();
    });
};

function getGroups(){
    websocket.send("getGroups", "all");
}

websocket = new WatchdogWebsocket(6085, "/groups", {
    onload: function (event) {
        getGroups();
    },
    groupsList: function (event) {
        renewGroupsList(event)
    }
});

$("#newGroup").on('click', function () {
    $("#editId").val(0);
    $("#editName").val("");
    $("#editGroupModal").modal('show');
});

$("#editGroupSave").on('click', function () {
    var editGroup = {
        id: $("#editId").val(),
        name: $("#editName").val()
    };
    websocket.send("saveGroup", JSON.stringify(editGroup));
    $("#editGroupModal").modal('hide');
    getGroups();
});

$("#deleteConfirm").on('click', function () {
    var deleteId = $("#deleteConfirmSpan").attr("data");
    websocket.send("deleteGroup", deleteId);
    $("#deleteConfirmModal").modal('hide');
    getGroups();
});