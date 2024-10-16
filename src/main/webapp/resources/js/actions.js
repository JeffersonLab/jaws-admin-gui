var jlab = jlab || {};
jlab.editableRowTable = jlab.editableRowTable || {};
jlab.editableRowTable.entity = 'Action';
jlab.editableRowTable.dialog.width = 800;
jlab.editableRowTable.dialog.height = 600;
jlab.addRow = function () {
    var name = $("#row-name").val(),
        systemId = $("#row-system").val(),
        priorityId = $("#row-priority").val(),
        correctiveAction = $("#corrective-action-textarea").val(),
        rationale = $("#rationale-textarea").val(),
        filterable = $("#row-filterable").is(":checked") ? 'Y' : 'N',
        latchable = $("#row-latchable").is(":checked") ? 'Y' : 'N',
        onDelaySeconds = $("#row-ondelay").val(),
        offDelaySeconds = $("#row-offdelay").val(),
        reloading = false;

    $(".dialog-submit-button")
        .height($(".dialog-submit-button").height())
        .width($(".dialog-submit-button").width())
        .empty().append('<div class="button-indicator"></div>');
    $(".dialog-close-button").attr("disabled", "disabled");
    $(".ui-dialog-titlebar button").attr("disabled", "disabled");

    var request = jQuery.ajax({
        url: "/jaws/ajax/add-action",
        type: "POST",
        data: {
            name: name,
            systemId: systemId,
            priorityId: priorityId,
            correctiveAction: correctiveAction,
            rationale: rationale,
            filterable: filterable,
            latchable: latchable,
            onDelaySeconds: onDelaySeconds,
            offDelaySeconds: offDelaySeconds
        },
        dataType: "json"
    });

    request.done(function (json) {
        if (json.stat === 'ok') {
            reloading = true;
            window.location.reload();
        } else {
            alert(json.error);
        }
    });

    request.fail(function (xhr, textStatus) {
        window.console && console.log('Unable to add action; Text Status: ' + textStatus + ', Ready State: ' + xhr.readyState + ', HTTP Status Code: ' + xhr.status);
        alert('Unable to Save: Server unavailable or unresponsive');
    });

    request.always(function () {
        if (!reloading) {
            $(".dialog-submit-button").empty().text("Save");
            $(".dialog-close-button").removeAttr("disabled");
            $(".ui-dialog-titlebar button").removeAttr("disabled");
        }
    });
};
jlab.editRow = function () {
    var name = $("#row-name").val(),
        systemId = $("#row-system").val(),
        priorityId = $("#row-priority").val(),
        correctiveAction = $("#corrective-action-textarea").val(),
        rationale = $("#rationale-textarea").val(),
        filterable = $("#row-filterable").is(":checked") ? 'Y' : 'N',
        latchable = $("#row-latchable").is(":checked") ? 'Y' : 'N',
        onDelaySeconds = $("#row-ondelay").val(),
        offDelaySeconds = $("#row-offdelay").val(),
        actionId = $(".editable-row-table tr.selected-row").attr("data-id"),
        reloading = false;

    $(".dialog-submit-button")
        .height($(".dialog-submit-button").height())
        .width($(".dialog-submit-button").width())
        .empty().append('<div class="button-indicator"></div>');
    $(".dialog-close-button").attr("disabled", "disabled");
    $(".ui-dialog-titlebar button").attr("disabled", "disabled");

    var request = jQuery.ajax({
        url: "/jaws/ajax/edit-action",
        type: "POST",
        data: {
            actionId: actionId,
            name: name,
            systemId: systemId,
            priorityId: priorityId,
            correctiveAction: correctiveAction,
            rationale: rationale,
            filterable: filterable,
            latchable: latchable,
            onDelaySeconds: onDelaySeconds,
            offDelaySeconds: offDelaySeconds
        },
        dataType: "json"
    });

    request.done(function (json) {
        if (json.stat === 'ok') {
            reloading = true;
            window.location.reload();
        } else {
            alert(json.error);
        }
    });

    request.fail(function (xhr, textStatus) {
        window.console && console.log('Unable to edit action; Text Status: ' + textStatus + ', Ready State: ' + xhr.readyState + ', HTTP Status Code: ' + xhr.status);
        alert('Unable to Save: Server unavailable or unresponsive');
    });

    request.always(function () {
        if (!reloading) {
            $(".dialog-submit-button").empty().text("Save");
            $(".dialog-close-button").removeAttr("disabled");
            $(".ui-dialog-titlebar button").removeAttr("disabled");
        }
    });
};
jlab.removeRow = function () {
    var name = $(".editable-row-table tr.selected-row td:first-child").text(),
        id = $(".editable-row-table tr.selected-row").attr("data-id"),
        reloading = false;

    $("#remove-row-button")
        .height($("#remove-row-button").height())
        .width($("#remove-row-button").width())
        .empty().append('<div class="button-indicator"></div>');

    var request = jQuery.ajax({
        url: "/jaws/ajax/remove-action",
        type: "POST",
        data: {
            id: id
        },
        dataType: "json"
    });

    request.done(function (json) {
        if (json.stat === 'ok') {
            reloading = true;
            window.location.reload();
        } else {
            alert(json.error);
        }
    });

    request.fail(function (xhr, textStatus) {
        window.console && console.log('Unable to remove action; Text Status: ' + textStatus + ', Ready State: ' + xhr.readyState + ', HTTP Status Code: ' + xhr.status);
        alert('Unable to Remove Server unavailable or unresponsive');
    });

    request.always(function () {
        if (!reloading) {
            $("#remove-row-button").empty().text("Remove");
        }
    });
};
jlab.initMarkdownSplitPanes = function() {
    $(".split-pane").each(function (item) {
        let markdown = $(this).find("textarea").val(),
            rendered = DOMPurify.sanitize(marked.parse(markdown), jlab.sanitizeConfig);

        $(this).find(".markdown-html").html(rendered);
    });
};
$(document).on("dialogclose", "#table-row-dialog", function () {
    $("#row-form")[0].reset();

    jlab.initMarkdownSplitPanes();
});
$(document).on("click", "#open-edit-row-dialog-button", function () {
    var $selectedRow = $(".editable-row-table tr.selected-row");
    $("#row-name").val($selectedRow.find("td:first-child a").text());
    $("#row-system").val($selectedRow.attr("data-system-id"));
    $("#row-priority").val($selectedRow.attr("data-priority-id"));

    $("#row-filterable").prop("checked", $selectedRow.attr("data-filterable") === "true");
    $("#row-latchable").prop("checked", $selectedRow.attr("data-latchable") === "true")
    $("#row-ondelay").val($selectedRow.attr("data-ondelay"));
    $("#row-offdelay").val($selectedRow.attr("data-offdelay"));

    $("#corrective-action-textarea").val($selectedRow.attr("data-corrective-action"));
    $("#rationale-textarea").val($selectedRow.attr("data-rationale"));

    jlab.initMarkdownSplitPanes();

});
$(document).on("table-row-add", function () {
    jlab.addRow();
});
$(document).on("table-row-edit", function () {
    jlab.editRow();
});
$(document).on("click", "#remove-row-button", function () {
    var name = $(".editable-row-table tr.selected-row td:first-child").text().trim();
    if (confirm('Are you sure you want to remove ' + name + '?')) {
        jlab.removeRow();
    }
});
$(document).on("click", ".default-clear-panel", function () {
    $("#priority-select").val('');
    $("#team-select").val('');
    $("#action-name").val('');
    $("#system-name").val('');
    return false;
});
$(".left-pane").resizable({
    handleSelector: ".splitter",
    resizeHeight: false
});
$(function () {
    $("#table-row-dialog").dialog("option", "resizable", true);
    $("#table-row-dialog").dialog("option", "minWidth", 800);
    $("#table-row-dialog").dialog("option", "minHeight", 600);

    /* TODO: run marked.parse in web worker?  https://marked.js.org/using_advanced */

    let correctiveTextarea = document.getElementById("corrective-action-textarea"),
        correctiveRenderDiv = document.getElementById('corrective-action-rendered');
    $(document).on("keyup", "#corrective-action-textarea", function() {
        correctiveRenderDiv.innerHTML = DOMPurify.sanitize(marked.parse(correctiveTextarea.value), jlab.sanitizeConfig);
    });

    let rationaleTextarea = document.getElementById("rationale-textarea"),
        rationaleRenderDiv = document.getElementById('rationale-rendered');
    $(document).on("keyup", "#rationale-textarea", function() {
        rationaleRenderDiv.innerHTML = DOMPurify.sanitize(marked.parse(rationaleTextarea.value), jlab.sanitizeConfig);
    });

    const urlParams = new URLSearchParams(window.location.search);
    const alarmName = urlParams.get('actionName');
    const edit = urlParams.get('edit');

    if(edit === 'Y' && alarmName !== undefined) {
        urlParams.delete('edit');
        window.history.replaceState(null, null, 'actions?' + urlParams.toString());
        $(".inner-table tbody tr:first-child").trigger('click');
        $("#open-edit-row-dialog-button").trigger('click');
    }
});