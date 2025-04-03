
/* global PrimeFaces */

function validateIntegerValueAndFocusNext(event, next) {
    var keyCode = event.which || event.keyCode;
    //console.log(keyCode);
    if (keyCode === 13) {
        next = PrimeFaces.escapeClientId(next);
        var element = $(next);
        if (element)
        {
            element.focus();
            element.select();

        }
    }

    if (keyCode < 48) {
        if (keyCode !== 8 && keyCode !== 9 && keyCode !== 37 && keyCode !== 39) {
            event.preventDefault();
            return false;
        }

    }
    if (keyCode > 57) {
        event.preventDefault();
        return false;
    }

    return true;
}
function validateFloatValueAndFocusNext(event, next) {
    var keyCode = event.which || event.keyCode;
    if (keyCode === 13) {
        next = PrimeFaces.escapeClientId(next);
        var element = $(next);
        if (element)
        {
            element.focus();
            element.select();
        }
    }
    if (keyCode > 57) {
        event.preventDefault();
        return false;
    }
    if (keyCode < 48) {
        /*. 46  ,44*/
        if (keyCode !== 8 && keyCode !== 46 && keyCode !== 9 && keyCode !== 37 && keyCode !== 39 && keyCode !== 44) {
            event.preventDefault();
            return false;
        }
    }
    return true;
}

function focusNextOnEnter(event, next) {
    var keyCode = event.which || event.keyCode;
    if (keyCode === 13) {
        next = PrimeFaces.escapeClientId(next);
        var element = $(next);
        if (element)
        {
            element.focus();
            element.select();
            event.preventDefault();
        }
    }
    return true;
}


function wizardTransform() {
    $('ul.ui-wizard-step-titles>li.ui-wizard-step-title').each(function (e) {

        $(this).removeClass('ui-wizard-step-title ui-corner-all ui-state-default');
        $(this).addClass(' origami-wizard');

        $(this).html('<a href="#">' + $(this).html() + '</a>');

    });
}

$(document).ready(function () {

    $(document).on('blur', 'div.compContainer input.compInput', function () {

        var value = $(this).val();
        var id = $(this).attr("id");
        var field = $(this).attr("data-field");
        var defaultValue = $(this).attr("data-default-value");
        var allValues = $(this).attr("data-all-values");
        var valorActualInput = $(this).attr("data-id");

        if (typeof valorActualInput !== "undefined" || value) {
            var idOrden = allValues.split('-');
            var idItem = -1;
            var orden = -1;

            for (var i = 0; i < idOrden.length; i++) {
                var temp = idOrden[i].split(";");
                if (temp[1] === value) {
                    idItem = temp[0];
                    orden = temp[1];
                    break;
                }
            }
            if (orden === -1) {
                value = defaultValue;
                for (var i = 0; i < idOrden.length; i++) {
                    var temp = idOrden[i].split(";");
                    if (temp[1] === value) {
                        idItem = temp[0];
                        orden = temp[1];
                        break;
                    }
                }
            } else {

                value = orden;
            }
            $(this).val(parseInt(value));

            var arr = id.split(':');
            var prefIdSelect = '';

            for (var i = 0; i < arr.length - 1; i++) {
                prefIdSelect += arr[i] + '\\:';
            }
            var idSelect = prefIdSelect + field + '-select_input';
            var idLabel = prefIdSelect + field + '-select_label';


            $("#" + idSelect).val('com.origami.sgm.entities.CtlgItem:' + idItem + ':java.lang.Long');
            $("#" + idLabel).html($("#" + idSelect).find('option:selected').text());
        } else {

            if (value) {
                var idOrden = allValues.split('-');
                var idItem = -1;
                var orden = -1;

                for (var i = 0; i < idOrden.length; i++) {
                    var temp = idOrden[i].split(";");
                    if (temp[1] === value) {
                        idItem = temp[0];
                        orden = temp[1];
                        break;
                    }
                }
                if (orden === -1) {
                    value = defaultValue;
                    for (var i = 0; i < idOrden.length; i++) {
                        var temp = idOrden[i].split(";");
                        if (temp[1] === value) {
                            idItem = temp[0];
                            orden = temp[1];
                            break;
                        }
                    }
                } else {

                    value = orden;
                }
                $(this).val(parseInt(value));

                var arr = id.split(':');
                var prefIdSelect = '';

                for (var i = 0; i < arr.length - 1; i++) {
                    prefIdSelect += arr[i] + '\\:';
                }
                var idSelect = prefIdSelect + field + '-select_input';
                var idLabel = prefIdSelect + field + '-select_label';


                $("#" + idSelect).val('com.origami.sgm.entities.CtlgItem:' + idItem + ':java.lang.Long');
                $("#" + idLabel).html($("#" + idSelect).find('option:selected').text());

            }

        }

    });

    $(document).on('change', 'div.compContainer select', function () {


        var value = $(this).val();
        var id = $(this).attr("id");
        var field = $(this).attr("data-field");

        var arr = id.split(':');
        var prefIdSelect = '';

        for (var i = 0; i < arr.length - 1; i++) {
            prefIdSelect += arr[i] + '\\:';
        }
        var idInput = prefIdSelect + field + '-input';

        var allValues = $(this).attr("data-all-values");

        var idsOrdenItems = allValues.split('-');
        var idItemArray = value.split(':');
        var idItemSeleccionado = idItemArray[1];
        var orden = -1;

        for (var i = 0; i < idsOrdenItems.length; i++) {
            var temp = idsOrdenItems[i].split(";");
            if (temp[0] === idItemSeleccionado) {
                orden = temp[1];
                break;
            }
        }

        $("#" + idInput).val(orden === -1 ? '' : parseInt(orden));

    });
});