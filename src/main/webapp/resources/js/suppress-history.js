var jlab = jlab || {};

$(document).on("click", ".default-clear-panel", function () {
    $("#date-range").val('custom').change();
    $("#start").val('');
    $("#end").val('');
    $("#state-select").val('');
    $("#overridden-select").val('');
    $("#override-select").val('');
    $("#type-select").val('');
    $("#location-select").val(null).trigger('change');
    $("#priority-select").val('');
    $("#team-select").val('');
    $("#registered-select").val('');
    $("#alarm-name").val('');
    $("#action-name").val('');
    $("#system-name").val('');
    return false;
});
jlab.initDialog = function () {
    $(".dialog").dialog({
        autoOpen: false,
        width: 700,
        height: 700,
        modal: true,
        resizable: false
    });
};
function formatLocation(location) {
    return location.text.trim();
}
$(function () {
    jlab.initDialog();

    $("#location-select").select2({
        width: 390,
        templateSelection: formatLocation
    });

    /*Custom time picker*/
    var myControl = {
        create: function (tp_inst, obj, unit, val, min, max, step) {
            $('<input class="ui-timepicker-input" value="' + val + '" style="width:50%">')
                .appendTo(obj)
                .spinner({
                    min: min,
                    max: max,
                    step: step,
                    change: function (e, ui) { // key events
                        // don't call if api was used and not key press
                        if (e.originalEvent !== undefined)
                            tp_inst._onTimeChange();
                        tp_inst._onSelectHandler();
                    },
                    spin: function (e, ui) { // spin events
                        tp_inst.control.value(tp_inst, obj, unit, ui.value);
                        tp_inst._onTimeChange();
                        tp_inst._onSelectHandler();
                    }
                });
            return obj;
        },
        options: function (tp_inst, obj, unit, opts, val) {
            if (typeof (opts) === 'string' && val !== undefined)
                return obj.find('.ui-timepicker-input').spinner(opts, val);
            return obj.find('.ui-timepicker-input').spinner(opts);
        },
        value: function (tp_inst, obj, unit, val) {
            if (val !== undefined)
                return obj.find('.ui-timepicker-input').spinner('value', val);
            return obj.find('.ui-timepicker-input').spinner('value');
        }
    };

    $(".date-time-field").datetimepicker({
        dateFormat: 'dd-M-yy',
        controlType: myControl,
        timeFormat: 'HH:mm'
    }).mask("99-aaa-9999 99:99", {placeholder: " "});
});