/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
PrimeFaces.widget.ColumnToggler.prototype.render = function () {
    //variable for creating id referance for each checkbox in ColumnToggler
    var id = 0;
    this.columns = this.thead.find("> tr > th:visible:not(.ui-static-column)");
    this.panel = $("<div></div>").attr("id", this.cfg.id).addClass("ui-columntoggler ui-widget ui-widget-content ui-shadow ui-corner-all").append('<ul class="ui-columntoggler-items"></ul').appendTo(document.body);
    this.itemContainer = this.panel.children("ul");
    for (var a = 0; a < this.columns.length; a++) {
        id++;
        var b = this.columns.eq(a);
        $('<li class="ui-columntoggler-item"><div class="ui-chkbox ui-widget"><div id="cb' + id /*creating id for each checkbox for accessing later*/ + '" class="ui-chkbox-box ui-widget ui-corner-all ui-state-default ui-state-active"><span class="ui-chkbox-icon ui-icon ui-icon-check"></span></div></div><label>' + b.children(".ui-column-title").text() + "</label></li>").data("column", b.attr("id")).appendTo(this.itemContainer);

        //access clumns using class reference(not-show-at-start) created in jsf page
        if (b.hasClass("not-show-at-start")) {
            //access checkbox using id attribute created above and uncheck it
            //this will hide columns that have "not-show-at-start" class
            this.uncheck($('#cb' + id));
        }
    }

    this.hide();
}


