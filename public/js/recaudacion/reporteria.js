function cambioData(){
    $('#btn_descargar').hide()
    //$('#grafico_urbano').hide()    
    $('#btn_consultar').show()
    $('#titulo_recaudacion').html('Reporte Recaudacion')

    var num_col = $("#tabla_ingreso thead tr th").length;
    $("#tabla_ingreso tbody").html('');

	$('#tabla_ingreso').DataTable().destroy();
	$('#tabla_ingreso tbody').empty(); 
    $("#tabla_ingreso tbody").html('');
    $("#tabla_ingreso tbody").html(`<tr><td colspan="${num_col}"><center>No hay Datos Disponibles</center></td></tr>`);
}


function mostrarData(){
    $('#btn_descargar').hide()
    $('#btn_consultar').show()
    let filtroDesde=$('#filtroDesde').val()
    let area=$('#filtroArea').val()
    let filtroTipo=$('#filtroTipo').val()

    if(filtroDesde==""){
        alertNotificar("Debe seleccionar la fecha inicial","error")
        $('#filtroDesde').focus()       
        return
    }

    if(filtroArea==""){
        alertNotificar("Debe seleccionar el tipo","error")
        $('#filtroDesde').focus()       
        return
    }
   
    $("#formReporteria").submit()
}



$("#formReporteria").submit(function(e){
    $('#total_recaudado').html('')
    let tota_recaudado=0
    var num_col = $("#tabla_ingreso thead tr th").length;
    $("#tabla_ingreso tbody").html('');

	$('#tabla_ingreso').DataTable().destroy();
	$('#tabla_ingreso tbody').empty(); 
    
    e.preventDefault();
    // var ruta="pago-consulta-transito"
    var ruta="valores-recaudados"
        
    vistacargando("m","Espere por favor")
    $.ajaxSetup({
        headers: {
            'X-CSRF-TOKEN': $('meta[name="csrf-token"]').attr('content')
        }
    });

    var tipo="POST"
  
    let filtroDesde=$('#filtroDesde').val()
    let area=$('#filtroArea').val()
    let filtroTipo=$('#filtroTipo').val()

    $.ajax({
            
        type: tipo,
        url: ruta,
        method: tipo,             
        data: {
            filtroDesde: filtroDesde,
            area: area,
            filtroTipo: filtroTipo
        },  
		
        // processData:false, 

        success: function(response) {
            console.log(response)
            vistacargando("")

            if(response.error==true){
                $("#tabla_ingreso tbody").html('');
                $("#tabla_ingreso tbody").html(`<tr><td colspan="${num_col}">No existen registros</td></tr>`);
                alertNotificar(response.mensaje)
                return;   
            }
         
            if(response.error==false){
                // alert(response.data.length)
                if(response.data.length==0){
                    $("#tabla_ingreso tbody").html('');
                    $("#tabla_ingreso tbody").html(`<tr><td colspan="${num_col}">No existen registros</td></tr>`);
                    alertNotificar("No se encontró información","error");
                    // cancelar()
                    return;
                }

                $("#tabla_ingreso tbody").html('');

                $.each(response.data,function(i, item){
                    tota_recaudado=tota_recaudado + Number(item.total_pago_anteriores) + Number(item.total_pago_anio_actual)
                    let total_codigo=0;
                    total_codigo= Number(item.total_pago_anteriores) + Number(item.total_pago_anio_actual)
                    let total_pago_anio_act='.00'
                    if(item.total_pago_anio_actual!=null){
                        total_pago_anio_act=item.total_pago_anio_actual
                    }

                    let total_pago_anio_ant='.00'
                    if(item.total_pago_anteriores!=null){
                        total_pago_anio_ant=item.total_pago_anteriores
                    }

                    $('#tabla_ingreso').append(`<tr>
                                                    <td style="width:10%; text-align:left; vertical-align:middle">
                                                       ${item.codigo} 
                                                       
                                                    </td>
    
                                                  
                                                    <td style="width:60%;  text-align:left; vertical-align:middle">
                                                        ${item.detalle_imp} 
                                                    </td>
    
                                                     <td style="width:10%; text-align:right; vertical-align:middle">
                                                       ${total_pago_anio_ant == '.00' ? '0.00' : total_pago_anio_ant
} 
                                                       
                                                    </td>
    
                                                   
                                                    <td style="width:10%; text-align:right;vertical-align:middle">
                                                       ${total_pago_anio_act == '.00' ? '0.00' : total_pago_anio_act
} 
                                                       
                                                    </td>
    
                                                    
                                                   
                                                    <td style="width:10%; text-align:right; vertical-align:middle">
    
                                                     
                                                        ${total_codigo == '0' ? '0.00' : total_codigo.toFixed(2)}
                                                       
                                                    </td>
    
                                                    
                                                
                                            </tr>`);
                })
                cargar_estilos_datatable1('tabla_ingreso');
                $('#btn_descargar').show()
                $('#btn_consultar').hide()

                $('#total_recaudado').html(tota_recaudado.toFixed(2))
            }

            
        },
        error: function(xhr, status, error) {
            vistacargando("")
            console.error("Error al obtener los datos:", error);
        }
    });
})

function cargar_estilos_datatable1(idtabla){
	$("#"+idtabla).DataTable({
		'paging'      : true,
		'searching'   : true,
		'ordering'    : true,
		'info'        : true,
		'autoWidth'   : true,
		"destroy":true,
        order: [[ 0, "asc" ]],
		pageLength: 10,
		sInfoFiltered:false,
		language: {
			url: 'json/datatables/spanish.json',
		},
        
	}); 
	$('.collapse-link').click();
	$('.datatable_wrapper').children('.row').css('overflow','inherit !important');

	$('.table-responsive').css({'padding-top':'12px','padding-bottom':'12px', 'border':'0', 'overflow-x':'inherit'});	
}

function cargar_estilos_datatable(idtabla, fecha){
	tablaPredio = $("#"+idtabla).DataTable({
		'paging'      : true,
		'searching'   : true,
		'ordering'    : true,
		'info'        : true,
		'autoWidth'   : true,
		"destroy":true,
        order: [[ 0, "asc" ]],
		pageLength: 10,
		sInfoFiltered:false,
		language: {
			url: 'json/datatables/spanish.json',
		},
         buttons: [
                {
                    extend: 'excelHtml5',
                    text: '📥 Descargar Excel',
                    title: 'RECAUDACION RURAL DEL DIA '+fecha,
                    exportOptions: {
                        columns: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9] // columnas a exportar
                    }
                }
            ]
	}); 
	$('.collapse-link').click();
	$('.datatable_wrapper').children('.row').css('overflow','inherit !important');

	$('.table-responsive').css({'padding-top':'12px','padding-bottom':'12px', 'border':'0', 'overflow-x':'inherit'});	
}

function cargar_estilos_datatable_urb(idtabla, fecha){
	tablaPredio = $("#"+idtabla).DataTable({
		'paging'      : true,
		'searching'   : true,
		'ordering'    : true,
		'info'        : true,
		'autoWidth'   : true,
		"destroy":true,
        order: [[ 0, "asc" ]],
		pageLength: 10,
		sInfoFiltered:false,
		language: {
			url: 'json/datatables/spanish.json',
		},
         buttons: [
                {
                    extend: 'excelHtml5',
                    text: '📥 Descargar Excel',
                    title: 'RECAUDACION PREDIAL URBANA EN EFCTIVO DEL DIA '+fecha,
                    exportOptions: {
                        columns: [0, 1, 2, 3, 4, 5, 6] // columnas a exportar
                    }
                }
            ]
	}); 
	$('.collapse-link').click();
	$('.datatable_wrapper').children('.row').css('overflow','inherit !important');

	$('.table-responsive').css({'padding-top':'12px','padding-bottom':'12px', 'border':'0', 'overflow-x':'inherit'});	
}

function cargar_estilos_datatable_urbNC(idtabla, fecha){
	tablaPredioNC = $("#"+idtabla).DataTable({
		'paging'      : true,
		'searching'   : true,
		'ordering'    : true,
		'info'        : true,
		'autoWidth'   : true,
		"destroy":true,
        order: [[ 0, "asc" ]],
		pageLength: 10,
		sInfoFiltered:false,
		language: {
			url: 'json/datatables/spanish.json',
		},
         buttons: [
                {
                    extend: 'excelHtml5',
                    text: '📥 Descargar Excel',
                    title: 'RECAUDACION PREDIAL URBANA EN NOTA CREDITO DEL DIA '+fecha,
                    exportOptions: {
                        columns: [0, 1, 2, 3, 4, 5, 6] // columnas a exportar
                    }
                }
            ]
	}); 
	$('.collapse-link').click();
	$('.datatable_wrapper').children('.row').css('overflow','inherit !important');

	$('.table-responsive').css({'padding-top':'12px','padding-bottom':'12px', 'border':'0', 'overflow-x':'inherit'});	
}

function cargar_estilos_datatable_urbCH(idtabla, fecha){
	tablaPredioCH = $("#"+idtabla).DataTable({
		'paging'      : true,
		'searching'   : true,
		'ordering'    : true,
		'info'        : true,
		'autoWidth'   : true,
		"destroy":true,
        order: [[ 0, "asc" ]],
		pageLength: 10,
		sInfoFiltered:false,
		language: {
			url: 'json/datatables/spanish.json',
		},
         buttons: [
                {
                    extend: 'excelHtml5',
                    text: '📥 Descargar Excel',
                    title: 'RECAUDACION PREDIAL URBANA EN CHEQUE DEL DIA '+fecha,
                    exportOptions: {
                        columns: [0, 1, 2, 3, 4, 5, 6] // columnas a exportar
                    }
                }
            ]
	}); 
	$('.collapse-link').click();
	$('.datatable_wrapper').children('.row').css('overflow','inherit !important');

	$('.table-responsive').css({'padding-top':'12px','padding-bottom':'12px', 'border':'0', 'overflow-x':'inherit'});	
}

function cargar_estilos_datatable_urbTR(idtabla, fecha){
	tablaPredioTR = $("#"+idtabla).DataTable({
		'paging'      : true,
		'searching'   : true,
		'ordering'    : true,
		'info'        : true,
		'autoWidth'   : true,
		"destroy":true,
        order: [[ 0, "asc" ]],
		pageLength: 10,
		sInfoFiltered:false,
		language: {
			url: 'json/datatables/spanish.json',
		},
         buttons: [
                {
                    extend: 'excelHtml5',
                    text: '📥 Descargar Excel',
                    title: 'RECAUDACION PREDIAL URBANA EN TRANSFERENCIA DEL DIA '+fecha,
                    exportOptions: {
                        columns: [0, 1, 2, 3, 4, 5, 6] // columnas a exportar
                    }
                }
            ]
	}); 
	$('.collapse-link').click();
	$('.datatable_wrapper').children('.row').css('overflow','inherit !important');

	$('.table-responsive').css({'padding-top':'12px','padding-bottom':'12px', 'border':'0', 'overflow-x':'inherit'});	
}

function cargar_estilos_datatable_urbCOM(idtabla, fecha){
	tablaPredioCOM = $("#"+idtabla).DataTable({
		'paging'      : true,
		'searching'   : true,
		'ordering'    : true,
		'info'        : true,
		'autoWidth'   : true,
		"destroy":true,
        order: [[ 0, "asc" ]],
		pageLength: 10,
		sInfoFiltered:false,
		language: {
			url: 'json/datatables/spanish.json',
		},
         buttons: [
                {
                    extend: 'excelHtml5',
                    text: '📥 Descargar Excel',
                    title: 'RECAUDACION PREDIAL URBANA EN COMPENSACION DEL DIA '+fecha,
                    exportOptions: {
                        columns: [0, 1, 2, 3, 4, 5, 6] // columnas a exportar
                    }
                }
            ]
	}); 
	$('.collapse-link').click();
	$('.datatable_wrapper').children('.row').css('overflow','inherit !important');

	$('.table-responsive').css({'padding-top':'12px','padding-bottom':'12px', 'border':'0', 'overflow-x':'inherit'});	
}


function cargar_estilos_datatable_urb_cem(idtabla, fecha){
	tablaPredioCem = $("#"+idtabla).DataTable({
		'paging'      : true,
		'searching'   : true,
		'ordering'    : true,
		'info'        : true,
		'autoWidth'   : true,
		"destroy":true,
        scrollX: true,
        order: [[ 0, "asc" ]],
		pageLength: 10,
		sInfoFiltered:false,
		language: {
			url: 'json/datatables/spanish.json',
		},
         buttons: [
                {
                    extend: 'excelHtml5',
                    text: '📥 Descargar Excel',
                    title: 'RECAUDACION DE CEM URBANO EN EFECTIVO DEL DIA '+fecha,
                    exportOptions: {
                        columns: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10] // columnas a exportar
                    }
                }
            ]
	}); 
	$('.collapse-link').click();
	$('.datatable_wrapper').children('.row').css('overflow','inherit !important');

	$('.table-responsive').css({'padding-top':'12px','padding-bottom':'12px', 'border':'0', 'overflow-x':'inherit'});	
}

function cargar_estilos_datatable_urb_cemNC(idtabla, fecha){
	tablaPredioCemNC = $("#"+idtabla).DataTable({
		'paging'      : true,
		'searching'   : true,
		'ordering'    : true,
		'info'        : true,
		'autoWidth'   : true,
		"destroy":true,
        scrollX: true,
        order: [[ 0, "asc" ]],
		pageLength: 10,
		sInfoFiltered:false,
		language: {
			url: 'json/datatables/spanish.json',
		},
         buttons: [
                {
                    extend: 'excelHtml5',
                    text: '📥 Descargar Excel',
                    title: 'RECAUDACION DE CEM URBANO EN NOTA CREDITO DEL DIA '+fecha,
                    exportOptions: {
                        columns: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10] // columnas a exportar
                    }
                }
            ]
	}); 
	$('.collapse-link').click();
	$('.datatable_wrapper').children('.row').css('overflow','inherit !important');

	$('.table-responsive').css({'padding-top':'12px','padding-bottom':'12px', 'border':'0', 'overflow-x':'inherit'});	
}

function cargar_estilos_datatable_urb_cemCH(idtabla, fecha){
	tablaPredioCemCH = $("#"+idtabla).DataTable({
		'paging'      : true,
		'searching'   : true,
		'ordering'    : true,
		'info'        : true,
		'autoWidth'   : true,
		"destroy":true,
        scrollX: true,
        order: [[ 0, "asc" ]],
		pageLength: 10,
		sInfoFiltered:false,
		language: {
			url: 'json/datatables/spanish.json',
		},
         buttons: [
                {
                    extend: 'excelHtml5',
                    text: '📥 Descargar Excel',
                    title: 'RECAUDACION DE CEM URBANO EN CHEQUE DEL DIA '+fecha,
                    exportOptions: {
                        columns: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10] // columnas a exportar
                    }
                }
            ]
	}); 
	$('.collapse-link').click();
	$('.datatable_wrapper').children('.row').css('overflow','inherit !important');

	$('.table-responsive').css({'padding-top':'12px','padding-bottom':'12px', 'border':'0', 'overflow-x':'inherit'});	
}

function cargar_estilos_datatable_urb_cemTC(idtabla, fecha){
	tablaPredioCemTC = $("#"+idtabla).DataTable({
		'paging'      : true,
		'searching'   : true,
		'ordering'    : true,
		'info'        : true,
		'autoWidth'   : true,
		"destroy":true,
        scrollX: true,
        order: [[ 0, "asc" ]],
		pageLength: 10,
		sInfoFiltered:false,
		language: {
			url: 'json/datatables/spanish.json',
		},
         buttons: [
                {
                    extend: 'excelHtml5',
                    text: '📥 Descargar Excel',
                    title: 'RECAUDACION DE CEM URBANO EN TARJETA CREDITO DEL DIA '+fecha,
                    exportOptions: {
                        columns: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10] // columnas a exportar
                    }
                }
            ]
	}); 
	$('.collapse-link').click();
	$('.datatable_wrapper').children('.row').css('overflow','inherit !important');

	$('.table-responsive').css({'padding-top':'12px','padding-bottom':'12px', 'border':'0', 'overflow-x':'inherit'});	
}

function cargar_estilos_datatable_urb_cemTR(idtabla, fecha){
	tablaPredioCemTR = $("#"+idtabla).DataTable({
		'paging'      : true,
		'searching'   : true,
		'ordering'    : true,
		'info'        : true,
		'autoWidth'   : true,
		"destroy":true,
        scrollX: true,
        order: [[ 0, "asc" ]],
		pageLength: 10,
		sInfoFiltered:false,
		language: {
			url: 'json/datatables/spanish.json',
		},
         buttons: [
                {
                    extend: 'excelHtml5',
                    text: '📥 Descargar Excel',
                    title: 'RECAUDACION DE CEM URBANO EN TRANSFERENCIA DEL DIA '+fecha,
                    exportOptions: {
                        columns: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10] // columnas a exportar
                    }
                }
            ]
	}); 
	$('.collapse-link').click();
	$('.datatable_wrapper').children('.row').css('overflow','inherit !important');

	$('.table-responsive').css({'padding-top':'12px','padding-bottom':'12px', 'border':'0', 'overflow-x':'inherit'});	
}

function cargar_estilos_datatable_urb_cemCOM(idtabla, fecha){
	tablaPredioCemCOM = $("#"+idtabla).DataTable({
		'paging'      : true,
		'searching'   : true,
		'ordering'    : true,
		'info'        : true,
		'autoWidth'   : true,
		"destroy":true,
        scrollX: true,
        order: [[ 0, "asc" ]],
		pageLength: 10,
		sInfoFiltered:false,
		language: {
			url: 'json/datatables/spanish.json',
		},
         buttons: [
                {
                    extend: 'excelHtml5',
                    text: '📥 Descargar Excel',
                    title: 'RECAUDACION DE CEM URBANO EN COMPENSACIONES DEL DIA '+fecha,
                    exportOptions: {
                        columns: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10] // columnas a exportar
                    }
                }
            ]
	}); 
	$('.collapse-link').click();
	$('.datatable_wrapper').children('.row').css('overflow','inherit !important');

	$('.table-responsive').css({'padding-top':'12px','padding-bottom':'12px', 'border':'0', 'overflow-x':'inherit'});	
}


function cargar_estilos_datatable_urbTC(idtabla, fecha){
	tablaPredioTC = $("#"+idtabla).DataTable({
		'paging'      : true,
		'searching'   : true,
		'ordering'    : true,
		'info'        : true,
		'autoWidth'   : true,
		"destroy":true,
        order: [[ 0, "asc" ]],
		pageLength: 10,
		sInfoFiltered:false,
		language: {
			url: 'json/datatables/spanish.json',
		},
         buttons: [
                {
                    extend: 'excelHtml5',
                    text: '📥 Descargar Excel',
                    title: 'RECAUDACION DE CEM URBANO EN TARJETA CREDITO DEL DIA '+fecha,
                    exportOptions: {
                        columns: [0, 1, 2, 3, 4, 5, 6] // columnas a exportar
                    }
                }
            ]
	}); 
	$('.collapse-link').click();
	$('.datatable_wrapper').children('.row').css('overflow','inherit !important');

	$('.table-responsive').css({'padding-top':'12px','padding-bottom':'12px', 'border':'0', 'overflow-x':'inherit'});	
}

function cargar_estilos_datatable_urb_otros(idtabla, fecha){
	tablaPredioOtros = $("#"+idtabla).DataTable({
		'paging'      : true,
		'searching'   : true,
		'ordering'    : true,
		'info'        : true,
		'autoWidth'   : true,
		"destroy":true,
        scrollX: true,
        order: [[ 0, "asc" ]],
		pageLength: 10,
		sInfoFiltered:false,
		language: {
			url: 'json/datatables/spanish.json',
		},
         buttons: [
                {
                    extend: 'excelHtml5',
                    text: '📥 Descargar Excel',
                    title: 'RECAUDACION DE OTROS URBANO EN EFECTIVO DEL DIA '+fecha,
                    exportOptions: {
                        columns: [0, 1, 2, 3, 4, 5] // columnas a exportar
                    }
                }
            ]
	}); 
	$('.collapse-link').click();
	$('.datatable_wrapper').children('.row').css('overflow','inherit !important');

	$('.table-responsive').css({'padding-top':'12px','padding-bottom':'12px', 'border':'0', 'overflow-x':'inherit'});	
}


function cargar_estilos_datatable_urb_otrosNC(idtabla, fecha){

     if ($.fn.DataTable.isDataTable("#"+idtabla)) {
        $("#"+idtabla).DataTable().clear().destroy();
    }

    
	tablaPredioOtrosNC = $("#"+idtabla).DataTable({
		'paging'      : true,
		'searching'   : true,
		'ordering'    : true,
		'info'        : true,
		'autoWidth'   : true,
		"destroy":true,
        scrollX: true,
        order: [[ 0, "asc" ]],
		pageLength: 10,
		sInfoFiltered:false,
		language: {
			url: 'json/datatables/spanish.json',
		},
         buttons: [
                {
                    extend: 'excelHtml5',
                    text: '📥 Descargar Excel',
                    title: 'RECAUDACION DE OTROS URBANO EN NOTA CREDITO DEL DIA '+fecha,
                    exportOptions: {
                        columns: [0, 1, 2, 3, 4, 5] // columnas a exportar
                    }
                }
            ]
	}); 
	$('.collapse-link').click();
	$('.datatable_wrapper').children('.row').css('overflow','inherit !important');

	$('.table-responsive').css({'padding-top':'12px','padding-bottom':'12px', 'border':'0', 'overflow-x':'inherit'});
    // 🔥 fuerza ajuste del thead
    setTimeout(() => {
        tablaPredioOtrosNC.columns.adjust();
    }, 200);
}

function cargar_estilos_datatable_urb_otrosCH(idtabla, fecha){

     if ($.fn.DataTable.isDataTable("#"+idtabla)) {
        $("#"+idtabla).DataTable().clear().destroy();
    }

    
	tablaPredioOtrosCH = $("#"+idtabla).DataTable({
		'paging'      : true,
		'searching'   : true,
		'ordering'    : true,
		'info'        : true,
		'autoWidth'   : true,
		"destroy":true,
        scrollX: true,
        order: [[ 0, "asc" ]],
		pageLength: 10,
		sInfoFiltered:false,
		language: {
			url: 'json/datatables/spanish.json',
		},
         buttons: [
                {
                    extend: 'excelHtml5',
                    text: '📥 Descargar Excel',
                    title: 'RECAUDACION DE OTROS URBANO EN CHEQUE DEL DIA'+fecha,
                    exportOptions: {
                        columns: [0, 1, 2, 3, 4, 5] // columnas a exportar
                    }
                }
            ]
	}); 
	$('.collapse-link').click();
	$('.datatable_wrapper').children('.row').css('overflow','inherit !important');

	$('.table-responsive').css({'padding-top':'12px','padding-bottom':'12px', 'border':'0', 'overflow-x':'inherit'});
    // 🔥 fuerza ajuste del thead
    setTimeout(() => {
        tablaPredioOtrosCH.columns.adjust();
    }, 200);
}

function cargar_estilos_datatable_urb_otrosTC(idtabla, fecha){

     if ($.fn.DataTable.isDataTable("#"+idtabla)) {
        $("#"+idtabla).DataTable().clear().destroy();
    }

    
	tablaPredioOtrosTC = $("#"+idtabla).DataTable({
		'paging'      : true,
		'searching'   : true,
		'ordering'    : true,
		'info'        : true,
		'autoWidth'   : true,
		"destroy":true,
        scrollX: true,
        order: [[ 0, "asc" ]],
		pageLength: 10,
		sInfoFiltered:false,
		language: {
			url: 'json/datatables/spanish.json',
		},
         buttons: [
                {
                    extend: 'excelHtml5',
                    text: '📥 Descargar Excel',
                    title: 'RECAUDACION DE OTROS URBANO EN TARJETA CREDITO DEL DIA '+fecha,
                    exportOptions: {
                        columns: [0, 1, 2, 3, 4, 5] // columnas a exportar
                    }
                }
            ]
	}); 
	$('.collapse-link').click();
	$('.datatable_wrapper').children('.row').css('overflow','inherit !important');

	$('.table-responsive').css({'padding-top':'12px','padding-bottom':'12px', 'border':'0', 'overflow-x':'inherit'});
    // 🔥 fuerza ajuste del thead
    setTimeout(() => {
        tablaPredioOtrosTC.columns.adjust();
    }, 200);
}

function cargar_estilos_datatable_urb_otrosCOM(idtabla, fecha){

     if ($.fn.DataTable.isDataTable("#"+idtabla)) {
        $("#"+idtabla).DataTable().clear().destroy();
    }

    
	tablaPredioOtrosCOM = $("#"+idtabla).DataTable({
		'paging'      : true,
		'searching'   : true,
		'ordering'    : true,
		'info'        : true,
		'autoWidth'   : true,
		"destroy":true,
        scrollX: true,
        order: [[ 0, "asc" ]],
		pageLength: 10,
		sInfoFiltered:false,
		language: {
			url: 'json/datatables/spanish.json',
		},
         buttons: [
                {
                    extend: 'excelHtml5',
                    text: '📥 Descargar Excel',
                    title: 'RECAUDACION DE OTROS URBANO EN COMPENSACION DEL DIA '+fecha,
                    exportOptions: {
                        columns: [0, 1, 2, 3, 4, 5] // columnas a exportar
                    }
                }
            ]
	}); 
	$('.collapse-link').click();
	$('.datatable_wrapper').children('.row').css('overflow','inherit !important');

	$('.table-responsive').css({'padding-top':'12px','padding-bottom':'12px', 'border':'0', 'overflow-x':'inherit'});
    // 🔥 fuerza ajuste del thead
    setTimeout(() => {
        tablaPredioOtrosCOM.columns.adjust();
    }, 200);
}

function cargar_estilos_datatable_urb_otrosTR(idtabla, fecha){

     if ($.fn.DataTable.isDataTable("#"+idtabla)) {
        $("#"+idtabla).DataTable().clear().destroy();
    }

    
	tablaPredioOtrosTR = $("#"+idtabla).DataTable({
		'paging'      : true,
		'searching'   : true,
		'ordering'    : true,
		'info'        : true,
		'autoWidth'   : true,
		"destroy":true,
        scrollX: true,
        order: [[ 0, "asc" ]],
		pageLength: 10,
		sInfoFiltered:false,
		language: {
			url: 'json/datatables/spanish.json',
		},
         buttons: [
                {
                    extend: 'excelHtml5',
                    text: '📥 Descargar Excel',
                    title: 'RECAUDACION DE OTROS URBANO EN TRANSFERENCIA DEL DIA '+fecha,
                    exportOptions: {
                        columns: [0, 1, 2, 3, 4, 5] // columnas a exportar
                    }
                }
            ]
	}); 
	$('.collapse-link').click();
	$('.datatable_wrapper').children('.row').css('overflow','inherit !important');

	$('.table-responsive').css({'padding-top':'12px','padding-bottom':'12px', 'border':'0', 'overflow-x':'inherit'});
    // 🔥 fuerza ajuste del thead
    setTimeout(() => {
        tablaPredioOtrosTR.columns.adjust();
    }, 200);
}


$('#btnExcel').on('click', function () {
    tablaPredio.button('.buttons-excel').trigger();
});

$('#btnExcelUrb').on('click', function () {
    tablaPredio.button('.buttons-excel').trigger();
});

$('#btnExcelUrbNC').on('click', function () {
    tablaPredioNC.button('.buttons-excel').trigger();
});

$('#btnExcelUrbCH').on('click', function () {
    tablaPredioCH.button('.buttons-excel').trigger();
});

$('#btnExcelUrbTC').on('click', function () {
    tablaPredioTC.button('.buttons-excel').trigger();
});

$('#btnExcelUrbTR').on('click', function () {
    tablaPredioTR.button('.buttons-excel').trigger();
});

$('#btnExcelUrbCOM').on('click', function () {
    tablaPredioCOM.button('.buttons-excel').trigger();
});

$('#btnExcelUrbCem').on('click', function () {
    tablaPredioCem.button('.buttons-excel').trigger();
});

$('#btnExcelUrbCemNC').on('click', function () {
    tablaPredioCemNC.button('.buttons-excel').trigger();
});

$('#btnExcelUrbCemCH').on('click', function () {
    tablaPredioCemCH.button('.buttons-excel').trigger();
})
$('#btnExcelUrbCemTC').on('click', function () {
    tablaPredioCemTC.button('.buttons-excel').trigger();
})

$('#btnExcelUrbCemTR').on('click', function () {
    tablaPredioCemTR.button('.buttons-excel').trigger();
})

$('#btnExcelUrbCemCOM').on('click', function () {
    tablaPredioCemCOM.button('.buttons-excel').trigger();
})

$('#btnExcelUrbOtros').on('click', function () {
    tablaPredioOtros.button('.buttons-excel').trigger();
});

$('#btnExcelUrbOtrosNC').on('click', function () {
    tablaPredioOtrosNC.button('.buttons-excel').trigger();
});

$('#btnExcelUrbOtrosCH').on('click', function () {
    tablaPredioOtrosCH.button('.buttons-excel').trigger();
});

$('#btnExcelUrbOtrosTC').on('click', function () {
    tablaPredioOtrosTC.button('.buttons-excel').trigger();
});

$('#btnExcelUrbOtrosCOM').on('click', function () {
    tablaPredioOtrosCOM.button('.buttons-excel').trigger();
});

$('#btnExcelUrbOtrosTR').on('click', function () {
    tablaPredioOtrosTR.button('.buttons-excel').trigger();
});

function verDetalle(){
    $('#titulo_recaudacion').html('Reporte Recaudacion')
    let area=$('#filtroArea').val()
    if(area=='Rural'){
        var num_col = $("#tabla_detalle thead tr th").length;
        $("#tabla_detalle tbody").html('');
        $('#tabla_detalle').DataTable().destroy();
        $('#tabla_detalle tbody').empty(); 
    }else if(area=='Urbano'){
        var num_col = $("#tabla_detalle_urb thead tr th").length;
        $("#tabla_detalle_urb tbody").html('');
        $('#tabla_detalle_urb').DataTable().destroy();
        $('#tabla_detalle_urb tbody').empty(); 

        var num_col = $("#tabla_detalle_urb_cem thead tr th").length;
        $("#tabla_detalle_urb_cem tbody").html('');
        $('#tabla_detalle_urb_cem').DataTable().destroy();
        $('#tabla_detalle_urb_cem tbody').empty(); 

        var num_col = $("#tabla_detalle_urb_cemNC thead tr th").length;
        $("#tabla_detalle_urb_cemNC tbody").html('');
        $('#tabla_detalle_urb_cemNC').DataTable().destroy();
        $('#tabla_detalle_urb_cemNC tbody').empty(); 

        var num_col = $("#tabla_detalle_urb_otros thead tr th").length;
        $("#tabla_detalle_urb_otros tbody").html('');
        $('#tabla_detalle_urb_otros').DataTable().destroy();
        $('#tabla_detalle_urb_otros tbody').empty();
        
        
        var num_col = $("#tabla_detalle_urbNC thead tr th").length;
        $("#tabla_detalle_urbNC tbody").html('');
        $('#tabla_detalle_urbNC').DataTable().destroy();
        $('#tabla_detalle_urbNC tbody').empty(); 

        var num_col = $("#tabla_detalle_urb_cemCH thead tr th").length;
        $("#tabla_detalle_urb_cemCH tbody").html('');
        $('#tabla_detalle_urb_cemCH').DataTable().destroy();
        $('#tabla_detalle_urb_cemCH tbody').empty(); 

        
        var num_col = $("#tabla_detalle_urb_cemTC thead tr th").length;
        $("#tabla_detalle_urb_cemTC tbody").html('');
        $('#tabla_detalle_urb_cemTC').DataTable().destroy();
        $('#tabla_detalle_urb_cemTC tbody').empty(); 

        var num_col = $("#tabla_detalle_urb_cemTR thead tr th").length;
        $("#tabla_detalle_urb_cemTR tbody").html('');
        $('#tabla_detalle_urb_cemTR').DataTable().destroy();
        $('#tabla_detalle_urb_cemTR tbody').empty(); 

         var num_col = $("#tabla_detalle_urb_cemCOM thead tr th").length;
        $("#tabla_detalle_urb_cemCOM tbody").html('');
        $('#tabla_detalle_urb_cemCOM').DataTable().destroy();
        $('#tabla_detalle_urb_cemCOM tbody').empty();


        var num_col = $("#tabla_detalle_urb_otrosNC thead tr th").length;
        $("#tabla_detalle_urb_otrosNC tbody").html('');
        $('#tabla_detalle_urb_otrosNC').DataTable().destroy();
        $('#tabla_detalle_urb_otrosNC tbody').empty();

        var num_col = $("#tabla_detalle_urb_otrosCH thead tr th").length;
        $("#tabla_detalle_urb_otrosCH tbody").html('');
        $('#tabla_detalle_urb_otrosCH').DataTable().destroy();
        $('#tabla_detalle_urb_otrosCH tbody').empty();

        var num_col = $("#tabla_detalle_urb_otrosTC thead tr th").length;
        $("#tabla_detalle_urb_otrosTC tbody").html('');
        $('#tabla_detalle_urb_otrosTC').DataTable().destroy();
        $('#tabla_detalle_urb_otrosTC tbody').empty();

        var num_col = $("#tabla_detalle_urb_otrosCOM thead tr th").length;
        $("#tabla_detalle_urb_otrosCOM tbody").html('');
        $('#tabla_detalle_urb_otrosCOM').DataTable().destroy();
        $('#tabla_detalle_urb_otrosCOM tbody').empty();

        var num_col = $("#tabla_detalle_urb_otrosTR thead tr th").length;
        $("#tabla_detalle_urb_otrosTR tbody").html('');
        $('#tabla_detalle_urb_otrosTR').DataTable().destroy();
        $('#tabla_detalle_urb_otrosTR tbody').empty();


        var num_col = $("#tabla_detalle_urbCH thead tr th").length;
        $("#tabla_detalle_urbCH tbody").html('');
        $('#tabla_detalle_urbCH').DataTable().destroy();
        $('#tabla_detalle_urbCH tbody').empty(); 

        var num_col = $("#tabla_detalle_urbTC thead tr th").length;
        $("#tabla_detalle_urbTC tbody").html('');
        $('#tabla_detalle_urbTC').DataTable().destroy();
        $('#tabla_detalle_urbTC tbody').empty(); 

        var num_col = $("#tabla_detalle_urbCOM thead tr th").length;
        $("#tabla_detalle_urbCOM tbody").html('');
        $('#tabla_detalle_urbCOM').DataTable().destroy();
        $('#tabla_detalle_urbCOM tbody').empty(); 

        var num_col = $("#tabla_detalle_urbTR thead tr th").length;
        $("#tabla_detalle_urbTR tbody").html('');
        $('#tabla_detalle_urbTR').DataTable().destroy();
        $('#tabla_detalle_urbTR tbody').empty(); 

        var num_col = $("#tabla_detalle_urbCOM thead tr th").length;
        $("#tabla_detalle_urbCOM tbody").html('');
        $('#tabla_detalle_urbCOM').DataTable().destroy();
        $('#tabla_detalle_urbCOM tbody').empty(); 
    }
        


    var ruta="reporte-recaudacion"
    vistacargando("m","Espere por favor")
    $.ajaxSetup({
        headers: {
            'X-CSRF-TOKEN': $('meta[name="csrf-token"]').attr('content')
        }
    });

    var tipo="POST" 

    let filtroDesde=$('#filtroDesde').val()
   
    let filtroTipo=$('#filtroTipo').val()

    $.ajax({
            
        type: tipo,
        url: ruta,
        method: tipo,             
        data: {
            filtroDesde: filtroDesde,
            area: area,
            filtroTipo: filtroTipo
        },  
		
        success: function(response) {
            vistacargando("")
            console.log(response)
            if(response.error==true){
                alertNotificar("Ocurrio un error","error")
                return
            }
            if(area=='Rural'){
                $('#titulo_recaudacion').html('Reporte Recaudacion Rural del '+response.fecha)
                $("#tabla_detalle tbody").html('');
                $.each(response.data,function(i, item){                
                    $('#tabla_detalle').append(`<tr>
                                                    <td style="width:5%; text-align:left; vertical-align:middle">
                                                        ${item.tipo_anio}                                                     
                                                    </td>
                                                    
                                                    <td style="width:10%;  text-align:left; vertical-align:middle">
                                                        ${item.num_titulo} 
                                                    </td>
                                                        <td style="width:10%; text-align:right; vertical-align:middle">
                                                        ${Number(item.total_pago_anio_actual_ipr || 0).toFixed(2)}
                                                    </td>

                                                    
                                                    <td style="width:10%; text-align:right;vertical-align:middle">                                                   
                                                        ${Number(item.total_pago_anio_actual_int || 0).toFixed(2)}                                                    
                                                    </td>

                                                    <td style="width:10%; text-align:right;vertical-align:middle">                                                  
                                                        ${Number(item.total_pago_anio_actual_desc || 0).toFixed(2)}                                                    
                                                    </td>

                                                    <td style="width:10%; text-align:right;vertical-align:middle">                                                    
                                                        ${Number(item.total_pago_anio_actual_rec || 0).toFixed(2)}                                                    
                                                    </td>

                                                    <td style="width:10%; text-align:right;vertical-align:middle">                                                    
                                                        ${Number(item.total_pago_anio_actual_bombero || 0).toFixed(2)}                                                    
                                                    </td>

                                                    <td style="width:10%; text-align:right;vertical-align:middle">                                                  
                                                        ${Number(item.total_pago_anio_actual_seguridad || 0).toFixed(2)}                                                    
                                                    </td>

                                                    <td style="width:10%; text-align:right;vertical-align:middle">                                                   
                                                        ${Number(item.total_pago_anio_actual_tasa || 0).toFixed(2)}                                                    
                                                    </td>

                                                    <td style="width:10%; text-align:right;vertical-align:middle">                                                   
                                                        ${Number(item.total_cobrado || 0).toFixed(2)}                                                   
                                                    </td>
                                                
                                            </tr>`);
                })
                cargar_estilos_datatable('tabla_detalle', response.fecha);
                $('#vista_buscador').hide(200)
                $('#vista_detalle').show(200)
            }else if(area=='Urbano'){
                $('#titulo_recaudacion').html('Reporte Recaudacion Urbana del '+response.fecha)
                $("#tabla_detalle_urb tbody").html('');
                $("#tabla_detalle_urbNC tbody").html('');
                $("#tabla_detalle_urbCH tbody").html('');
                $("#tabla_detalle_urbTC tbody").html('');
                $("#tabla_detalle_urbTR tbody").html('');
                $("#tabla_detalle_urbCOM tbody").html('');

                $.each(response.data,function(i, item){ 
                    if(item.tipo_pago==1){               
                        $('#tabla_detalle_urb').append(`<tr>
                                                    <td style="width:5%; text-align:left; vertical-align:middle">
                                                        ${item.tipo_anio}                                                     
                                                    </td>
                                                    
                                                    <td style="width:10%;  text-align:left; vertical-align:middle">
                                                        ${item.num_titulo} 
                                                    </td>
                                                        <td style="width:10%; text-align:right; vertical-align:middle">
                                                        ${Number(item.total_pago_anio_actual_ipr || 0).toFixed(2)}
                                                    </td>

                                                    
                                                    <td style="width:10%; text-align:right;vertical-align:middle">                                                   
                                                        ${Number(item.total_pago_anio_actual_int || 0).toFixed(2)}                                                    
                                                    </td>

                                                    <td style="width:10%; text-align:right;vertical-align:middle">                                                  
                                                        ${Number(item.total_pago_anio_actual_desc || 0).toFixed(2)}                                                    
                                                    </td>

                                                    <td style="width:10%; text-align:right;vertical-align:middle">                                                    
                                                        ${Number(item.total_pago_anio_actual_rec || 0).toFixed(2)}                                                    
                                                    </td>

                                                 

                                                    <td style="width:10%; text-align:right;vertical-align:middle">                                                   
                                                        ${Number(item.total_cobrado || 0).toFixed(2)}                                                   
                                                    </td>
                                                
                                            </tr>`);
                    }else if(item.tipo_pago==2){
                        $('#tabla_detalle_urbNC').append(`<tr>
                                                    <td style="width:5%; text-align:left; vertical-align:middle">
                                                        ${item.tipo_anio}                                                     
                                                    </td>
                                                    
                                                    <td style="width:10%;  text-align:left; vertical-align:middle">
                                                        ${item.num_titulo} 
                                                    </td>
                                                        <td style="width:10%; text-align:right; vertical-align:middle">
                                                        ${Number(item.total_pago_anio_actual_ipr || 0).toFixed(2)}
                                                    </td>

                                                    
                                                    <td style="width:10%; text-align:right;vertical-align:middle">                                                   
                                                        ${Number(item.total_pago_anio_actual_int || 0).toFixed(2)}                                                    
                                                    </td>

                                                    <td style="width:10%; text-align:right;vertical-align:middle">                                                  
                                                        ${Number(item.total_pago_anio_actual_desc || 0).toFixed(2)}                                                    
                                                    </td>

                                                    <td style="width:10%; text-align:right;vertical-align:middle">                                                    
                                                        ${Number(item.total_pago_anio_actual_rec || 0).toFixed(2)}                                                    
                                                    </td>

                                                 

                                                    <td style="width:10%; text-align:right;vertical-align:middle">                                                   
                                                        ${Number(item.total_cobrado || 0).toFixed(2)}                                                   
                                                    </td>
                                                
                                            </tr>`);
                    }else if(item.tipo_pago==3){
                        $('#tabla_detalle_urbCH').append(`<tr>
                                                    <td style="width:5%; text-align:left; vertical-align:middle">
                                                        ${item.tipo_anio}                                                     
                                                    </td>
                                                    
                                                    <td style="width:10%;  text-align:left; vertical-align:middle">
                                                        ${item.num_titulo} 
                                                    </td>
                                                        <td style="width:10%; text-align:right; vertical-align:middle">
                                                        ${Number(item.total_pago_anio_actual_ipr || 0).toFixed(2)}
                                                    </td>

                                                    
                                                    <td style="width:10%; text-align:right;vertical-align:middle">                                                   
                                                        ${Number(item.total_pago_anio_actual_int || 0).toFixed(2)}                                                    
                                                    </td>

                                                    <td style="width:10%; text-align:right;vertical-align:middle">                                                  
                                                        ${Number(item.total_pago_anio_actual_desc || 0).toFixed(2)}                                                    
                                                    </td>

                                                    <td style="width:10%; text-align:right;vertical-align:middle">                                                    
                                                        ${Number(item.total_pago_anio_actual_rec || 0).toFixed(2)}                                                    
                                                    </td>

                                                 

                                                    <td style="width:10%; text-align:right;vertical-align:middle">                                                   
                                                        ${Number(item.total_cobrado || 0).toFixed(2)}                                                   
                                                    </td>
                                                
                                            </tr>`);
                    }else if(item.tipo_pago==4){
                        $('#tabla_detalle_urbTC').append(`<tr>
                                                    <td style="width:5%; text-align:left; vertical-align:middle">
                                                        ${item.tipo_anio}                                                     
                                                    </td>
                                                    
                                                    <td style="width:10%;  text-align:left; vertical-align:middle">
                                                        ${item.num_titulo} 
                                                    </td>
                                                        <td style="width:10%; text-align:right; vertical-align:middle">
                                                        ${Number(item.total_pago_anio_actual_ipr || 0).toFixed(2)}
                                                    </td>

                                                    
                                                    <td style="width:10%; text-align:right;vertical-align:middle">                                                   
                                                        ${Number(item.total_pago_anio_actual_int || 0).toFixed(2)}                                                    
                                                    </td>

                                                    <td style="width:10%; text-align:right;vertical-align:middle">                                                  
                                                        ${Number(item.total_pago_anio_actual_desc || 0).toFixed(2)}                                                    
                                                    </td>

                                                    <td style="width:10%; text-align:right;vertical-align:middle">                                                    
                                                        ${Number(item.total_pago_anio_actual_rec || 0).toFixed(2)}                                                    
                                                    </td>

                                                 

                                                    <td style="width:10%; text-align:right;vertical-align:middle">                                                   
                                                        ${Number(item.total_cobrado || 0).toFixed(2)}                                                   
                                                    </td>
                                                
                                            </tr>`);
                    }else if(item.tipo_pago==5){
                        $('#tabla_detalle_urbTR').append(`<tr>
                                                    <td style="width:5%; text-align:left; vertical-align:middle">
                                                        ${item.tipo_anio}                                                     
                                                    </td>
                                                    
                                                    <td style="width:10%;  text-align:left; vertical-align:middle">
                                                        ${item.num_titulo} 
                                                    </td>
                                                        <td style="width:10%; text-align:right; vertical-align:middle">
                                                        ${Number(item.total_pago_anio_actual_ipr || 0).toFixed(2)}
                                                    </td>

                                                    
                                                    <td style="width:10%; text-align:right;vertical-align:middle">                                                   
                                                        ${Number(item.total_pago_anio_actual_int || 0).toFixed(2)}                                                    
                                                    </td>

                                                    <td style="width:10%; text-align:right;vertical-align:middle">                                                  
                                                        ${Number(item.total_pago_anio_actual_desc || 0).toFixed(2)}                                                    
                                                    </td>

                                                    <td style="width:10%; text-align:right;vertical-align:middle">                                                    
                                                        ${Number(item.total_pago_anio_actual_rec || 0).toFixed(2)}                                                    
                                                    </td>

                                                 

                                                    <td style="width:10%; text-align:right;vertical-align:middle">                                                   
                                                        ${Number(item.total_cobrado || 0).toFixed(2)}                                                   
                                                    </td>
                                                
                                            </tr>`);
                    }else if(item.tipo_pago==6){
                        $('#tabla_detalle_urbCOM').append(`<tr>
                                                    <td style="width:5%; text-align:left; vertical-align:middle">
                                                        ${item.tipo_anio}                                                     
                                                    </td>
                                                    
                                                    <td style="width:10%;  text-align:left; vertical-align:middle">
                                                        ${item.num_titulo} 
                                                    </td>
                                                        <td style="width:10%; text-align:right; vertical-align:middle">
                                                        ${Number(item.total_pago_anio_actual_ipr || 0).toFixed(2)}
                                                    </td>

                                                    
                                                    <td style="width:10%; text-align:right;vertical-align:middle">                                                   
                                                        ${Number(item.total_pago_anio_actual_int || 0).toFixed(2)}                                                    
                                                    </td>

                                                    <td style="width:10%; text-align:right;vertical-align:middle">                                                  
                                                        ${Number(item.total_pago_anio_actual_desc || 0).toFixed(2)}                                                    
                                                    </td>

                                                    <td style="width:10%; text-align:right;vertical-align:middle">                                                    
                                                        ${Number(item.total_pago_anio_actual_rec || 0).toFixed(2)}                                                    
                                                    </td>

                                                 

                                                    <td style="width:10%; text-align:right;vertical-align:middle">                                                   
                                                        ${Number(item.total_cobrado || 0).toFixed(2)}                                                   
                                                    </td>
                                                
                                            </tr>`);
                    }
                })
                cargar_estilos_datatable_urb('tabla_detalle_urb', response.fecha);
                cargar_estilos_datatable_urbNC('tabla_detalle_urbNC', response.fecha);
                cargar_estilos_datatable_urbCH('tabla_detalle_urbCH', response.fecha);
                cargar_estilos_datatable_urbTC('tabla_detalle_urbTC', response.fecha);
                cargar_estilos_datatable_urbTR('tabla_detalle_urbTR', response.fecha);
                cargar_estilos_datatable_urbCOM('tabla_detalle_urbCOM', response.fecha);
                


                $("#tabla_detalle_urb_cem tbody").html('');
                $("#tabla_detalle_urb_cemNC tbody").html('');
                $("#tabla_detalle_urb_cemCH tbody").html('');
                $("#tabla_detalle_urb_cemTC tbody").html('');
                $("#tabla_detalle_urb_cemTR tbody").html('');
                $("#tabla_detalle_urb_cemCOM tbody").html('');
                
                $.each(response.cem,function(i, item){       
                    if(item.tipo_pago==1){          
                        $('#tabla_detalle_urb_cem').append(`<tr>
                                                    <td style="width:5%; text-align:left; vertical-align:middle">
                                                        ${item.tipo_anio}                                                     
                                                    </td>
                                                    
                                                    <td style="width:10%;  text-align:left; vertical-align:middle">
                                                        ${item.num_titulo} 
                                                    </td>
                                                        <td style="width:10%; text-align:right; vertical-align:middle">
                                                        ${Number(item.cem_alcantarillado_sani || 0).toFixed(2)}
                                                    </td>

                                                    
                                                    <td style="width:10%; text-align:right;vertical-align:middle">                                                   
                                                        ${Number(item.cem_alcant_sta_marta || 0).toFixed(2)}                                                    
                                                    </td>

                                                    <td style="width:10%; text-align:right;vertical-align:middle">                                                  
                                                        ${Number(item.cem_alcant_vias || 0).toFixed(2)}                                                    
                                                    </td>

                                                    <td style="width:10%; text-align:right;vertical-align:middle">                                                    
                                                        ${Number(item.cem_area_recreacional || 0).toFixed(2)}                                                    
                                                    </td>


                                                     <td style="width:10%; text-align:right;vertical-align:middle">                                                   
                                                        ${Number(item.cem_mercado_muni || 0).toFixed(2)}                                                    
                                                    </td>

                                                    <td style="width:10%; text-align:right;vertical-align:middle">                                                  
                                                        ${Number(item.cem_parque_plaza || 0).toFixed(2)}                                                    
                                                    </td>

                                                    <td style="width:10%; text-align:right;vertical-align:middle">                                                    
                                                        ${Number(item.cem_pavimentacion_malla || 0).toFixed(2)}                                                    
                                                    </td>

                                                    <td style="width:10%; text-align:right;vertical-align:middle">                                                    
                                                        ${Number(item.cem_regeneracion || 0).toFixed(2)}                                                    
                                                    </td>

                                                 

                                                    <td style="width:10%; text-align:right;vertical-align:middle">                                                   
                                                        ${Number(item.total_cobrado || 0).toFixed(2)}                                                   
                                                    </td>
                                                
                                            </tr>`);
                    }else if(item.tipo_pago==2){
                        $('#tabla_detalle_urb_cemNC').append(`<tr>
                                                    <td style="width:5%; text-align:left; vertical-align:middle">
                                                        ${item.tipo_anio}                                                     
                                                    </td>
                                                    
                                                    <td style="width:10%;  text-align:left; vertical-align:middle">
                                                        ${item.num_titulo} 
                                                    </td>
                                                        <td style="width:10%; text-align:right; vertical-align:middle">
                                                        ${Number(item.cem_alcantarillado_sani || 0).toFixed(2)}
                                                    </td>

                                                    
                                                    <td style="width:10%; text-align:right;vertical-align:middle">                                                   
                                                        ${Number(item.cem_alcant_sta_marta || 0).toFixed(2)}                                                    
                                                    </td>

                                                    <td style="width:10%; text-align:right;vertical-align:middle">                                                  
                                                        ${Number(item.cem_alcant_vias || 0).toFixed(2)}                                                    
                                                    </td>

                                                    <td style="width:10%; text-align:right;vertical-align:middle">                                                    
                                                        ${Number(item.cem_area_recreacional || 0).toFixed(2)}                                                    
                                                    </td>


                                                     <td style="width:10%; text-align:right;vertical-align:middle">                                                   
                                                        ${Number(item.cem_mercado_muni || 0).toFixed(2)}                                                    
                                                    </td>

                                                    <td style="width:10%; text-align:right;vertical-align:middle">                                                  
                                                        ${Number(item.cem_parque_plaza || 0).toFixed(2)}                                                    
                                                    </td>

                                                    <td style="width:10%; text-align:right;vertical-align:middle">                                                    
                                                        ${Number(item.cem_pavimentacion_malla || 0).toFixed(2)}                                                    
                                                    </td>

                                                    <td style="width:10%; text-align:right;vertical-align:middle">                                                    
                                                        ${Number(item.cem_regeneracion || 0).toFixed(2)}                                                    
                                                    </td>

                                                 

                                                    <td style="width:10%; text-align:right;vertical-align:middle">                                                   
                                                        ${Number(item.total_cobrado || 0).toFixed(2)}                                                   
                                                    </td>
                                                
                                            </tr>`);
                    }else if(item.tipo_pago==3){
                        $('#tabla_detalle_urb_cemCH').append(`<tr>
                                                    <td style="width:5%; text-align:left; vertical-align:middle">
                                                        ${item.tipo_anio}                                                     
                                                    </td>
                                                    
                                                    <td style="width:10%;  text-align:left; vertical-align:middle">
                                                        ${item.num_titulo} 
                                                    </td>
                                                        <td style="width:10%; text-align:right; vertical-align:middle">
                                                        ${Number(item.cem_alcantarillado_sani || 0).toFixed(2)}
                                                    </td>

                                                    
                                                    <td style="width:10%; text-align:right;vertical-align:middle">                                                   
                                                        ${Number(item.cem_alcant_sta_marta || 0).toFixed(2)}                                                    
                                                    </td>

                                                    <td style="width:10%; text-align:right;vertical-align:middle">                                                  
                                                        ${Number(item.cem_alcant_vias || 0).toFixed(2)}                                                    
                                                    </td>

                                                    <td style="width:10%; text-align:right;vertical-align:middle">                                                    
                                                        ${Number(item.cem_area_recreacional || 0).toFixed(2)}                                                    
                                                    </td>


                                                     <td style="width:10%; text-align:right;vertical-align:middle">                                                   
                                                        ${Number(item.cem_mercado_muni || 0).toFixed(2)}                                                    
                                                    </td>

                                                    <td style="width:10%; text-align:right;vertical-align:middle">                                                  
                                                        ${Number(item.cem_parque_plaza || 0).toFixed(2)}                                                    
                                                    </td>

                                                    <td style="width:10%; text-align:right;vertical-align:middle">                                                    
                                                        ${Number(item.cem_pavimentacion_malla || 0).toFixed(2)}                                                    
                                                    </td>

                                                    <td style="width:10%; text-align:right;vertical-align:middle">                                                    
                                                        ${Number(item.cem_regeneracion || 0).toFixed(2)}                                                    
                                                    </td>

                                                 

                                                    <td style="width:10%; text-align:right;vertical-align:middle">                                                   
                                                        ${Number(item.total_cobrado || 0).toFixed(2)}                                                   
                                                    </td>
                                                
                                            </tr>`);
                    }else if(item.tipo_pago==4){
                        $('#tabla_detalle_urb_cemTC').append(`<tr>
                                                    <td style="width:5%; text-align:left; vertical-align:middle">
                                                        ${item.tipo_anio}                                                     
                                                    </td>
                                                    
                                                    <td style="width:10%;  text-align:left; vertical-align:middle">
                                                        ${item.num_titulo} 
                                                    </td>
                                                        <td style="width:10%; text-align:right; vertical-align:middle">
                                                        ${Number(item.cem_alcantarillado_sani || 0).toFixed(2)}
                                                    </td>

                                                    
                                                    <td style="width:10%; text-align:right;vertical-align:middle">                                                   
                                                        ${Number(item.cem_alcant_sta_marta || 0).toFixed(2)}                                                    
                                                    </td>

                                                    <td style="width:10%; text-align:right;vertical-align:middle">                                                  
                                                        ${Number(item.cem_alcant_vias || 0).toFixed(2)}                                                    
                                                    </td>

                                                    <td style="width:10%; text-align:right;vertical-align:middle">                                                    
                                                        ${Number(item.cem_area_recreacional || 0).toFixed(2)}                                                    
                                                    </td>


                                                     <td style="width:10%; text-align:right;vertical-align:middle">                                                   
                                                        ${Number(item.cem_mercado_muni || 0).toFixed(2)}                                                    
                                                    </td>

                                                    <td style="width:10%; text-align:right;vertical-align:middle">                                                  
                                                        ${Number(item.cem_parque_plaza || 0).toFixed(2)}                                                    
                                                    </td>

                                                    <td style="width:10%; text-align:right;vertical-align:middle">                                                    
                                                        ${Number(item.cem_pavimentacion_malla || 0).toFixed(2)}                                                    
                                                    </td>

                                                    <td style="width:10%; text-align:right;vertical-align:middle">                                                    
                                                        ${Number(item.cem_regeneracion || 0).toFixed(2)}                                                    
                                                    </td>

                                                 

                                                    <td style="width:10%; text-align:right;vertical-align:middle">                                                   
                                                        ${Number(item.total_cobrado || 0).toFixed(2)}                                                   
                                                    </td>
                                                
                                            </tr>`);
                    }else if(item.tipo_pago==5){
                        $('#tabla_detalle_urb_cemTR').append(`<tr>
                                                    <td style="width:5%; text-align:left; vertical-align:middle">
                                                        ${item.tipo_anio}                                                     
                                                    </td>
                                                    
                                                    <td style="width:10%;  text-align:left; vertical-align:middle">
                                                        ${item.num_titulo} 
                                                    </td>
                                                        <td style="width:10%; text-align:right; vertical-align:middle">
                                                        ${Number(item.cem_alcantarillado_sani || 0).toFixed(2)}
                                                    </td>

                                                    
                                                    <td style="width:10%; text-align:right;vertical-align:middle">                                                   
                                                        ${Number(item.cem_alcant_sta_marta || 0).toFixed(2)}                                                    
                                                    </td>

                                                    <td style="width:10%; text-align:right;vertical-align:middle">                                                  
                                                        ${Number(item.cem_alcant_vias || 0).toFixed(2)}                                                    
                                                    </td>

                                                    <td style="width:10%; text-align:right;vertical-align:middle">                                                    
                                                        ${Number(item.cem_area_recreacional || 0).toFixed(2)}                                                    
                                                    </td>


                                                     <td style="width:10%; text-align:right;vertical-align:middle">                                                   
                                                        ${Number(item.cem_mercado_muni || 0).toFixed(2)}                                                    
                                                    </td>

                                                    <td style="width:10%; text-align:right;vertical-align:middle">                                                  
                                                        ${Number(item.cem_parque_plaza || 0).toFixed(2)}                                                    
                                                    </td>

                                                    <td style="width:10%; text-align:right;vertical-align:middle">                                                    
                                                        ${Number(item.cem_pavimentacion_malla || 0).toFixed(2)}                                                    
                                                    </td>

                                                    <td style="width:10%; text-align:right;vertical-align:middle">                                                    
                                                        ${Number(item.cem_regeneracion || 0).toFixed(2)}                                                    
                                                    </td>

                                                 

                                                    <td style="width:10%; text-align:right;vertical-align:middle">                                                   
                                                        ${Number(item.total_cobrado || 0).toFixed(2)}                                                   
                                                    </td>
                                                
                                            </tr>`);
                    }else if(item.tipo_pago==6){
                        $('#tabla_detalle_urb_cemCOM').append(`<tr>
                                                    <td style="width:5%; text-align:left; vertical-align:middle">
                                                        ${item.tipo_anio}                                                     
                                                    </td>
                                                    
                                                    <td style="width:10%;  text-align:left; vertical-align:middle">
                                                        ${item.num_titulo} 
                                                    </td>
                                                        <td style="width:10%; text-align:right; vertical-align:middle">
                                                        ${Number(item.cem_alcantarillado_sani || 0).toFixed(2)}
                                                    </td>

                                                    
                                                    <td style="width:10%; text-align:right;vertical-align:middle">                                                   
                                                        ${Number(item.cem_alcant_sta_marta || 0).toFixed(2)}                                                    
                                                    </td>

                                                    <td style="width:10%; text-align:right;vertical-align:middle">                                                  
                                                        ${Number(item.cem_alcant_vias || 0).toFixed(2)}                                                    
                                                    </td>

                                                    <td style="width:10%; text-align:right;vertical-align:middle">                                                    
                                                        ${Number(item.cem_area_recreacional || 0).toFixed(2)}                                                    
                                                    </td>


                                                     <td style="width:10%; text-align:right;vertical-align:middle">                                                   
                                                        ${Number(item.cem_mercado_muni || 0).toFixed(2)}                                                    
                                                    </td>

                                                    <td style="width:10%; text-align:right;vertical-align:middle">                                                  
                                                        ${Number(item.cem_parque_plaza || 0).toFixed(2)}                                                    
                                                    </td>

                                                    <td style="width:10%; text-align:right;vertical-align:middle">                                                    
                                                        ${Number(item.cem_pavimentacion_malla || 0).toFixed(2)}                                                    
                                                    </td>

                                                    <td style="width:10%; text-align:right;vertical-align:middle">                                                    
                                                        ${Number(item.cem_regeneracion || 0).toFixed(2)}                                                    
                                                    </td>

                                                 

                                                    <td style="width:10%; text-align:right;vertical-align:middle">                                                   
                                                        ${Number(item.total_cobrado || 0).toFixed(2)}                                                   
                                                    </td>
                                                
                                            </tr>`);
                    }
                })
                cargar_estilos_datatable_urb_cem('tabla_detalle_urb_cem', response.fecha);
                cargar_estilos_datatable_urb_cemNC('tabla_detalle_urb_cemNC', response.fecha);
                cargar_estilos_datatable_urb_cemCH('tabla_detalle_urb_cemCH', response.fecha);
                cargar_estilos_datatable_urb_cemTC('tabla_detalle_urb_cemTC', response.fecha);
                cargar_estilos_datatable_urb_cemTR('tabla_detalle_urb_cemTR', response.fecha);
                cargar_estilos_datatable_urb_cemCOM('tabla_detalle_urb_cemCOM', response.fecha);


                $("#tabla_detalle_urb_otros tbody").html('');
                $("#tabla_detalle_urb_otrosNC tbody").html('');
                $("#tabla_detalle_urb_otrosCH tbody").html('');
                $("#tabla_detalle_urb_otrosTC tbody").html('');
                $("#tabla_detalle_urb_otrosCOM tbody").html('');
                $("#tabla_detalle_urb_otrosTR tbody").html('');
                $.each(response.otros,function(i, item){  
                    if(item.tipo_pago==1){              
                        $('#tabla_detalle_urb_otros').append(`<tr>
                                                    <td style="width:5%; text-align:left; vertical-align:middle">
                                                        ${item.tipo_anio}                                                     
                                                    </td>
                                                    
                                                    <td style="width:10%;  text-align:left; vertical-align:middle">
                                                        ${item.num_titulo} 
                                                    </td>
                                                        <td style="width:10%; text-align:right; vertical-align:middle">
                                                        ${Number(item.total_seguridad || 0).toFixed(2)}
                                                    </td>

                                                    
                                                    <td style="width:10%; text-align:right;vertical-align:middle">                                                   
                                                        ${Number(item.total_bomberos || 0).toFixed(2)}                                                    
                                                    </td>

                                                    <td style="width:10%; text-align:right;vertical-align:middle">                                                  
                                                        ${Number(item.total_adm || 0).toFixed(2)}                                                    
                                                    </td>

                                                   

                                                    <td style="width:10%; text-align:right;vertical-align:middle">                                                   
                                                        ${Number(item.total_cobrado || 0).toFixed(2)}                                                   
                                                    </td>
                                                
                                            </tr>`);
                    }else if(item.tipo_pago==2){ 
                        $('#tabla_detalle_urb_otrosNC').append(`<tr>
                                                    <td style="width:5%; text-align:left; vertical-align:middle">
                                                        ${item.tipo_anio}                                                     
                                                    </td>
                                                    
                                                    <td style="width:10%;  text-align:left; vertical-align:middle">
                                                        ${item.num_titulo} 
                                                    </td>
                                                        <td style="width:20%; text-align:right; vertical-align:middle">
                                                        ${Number(item.total_seguridad || 0).toFixed(2)}
                                                    </td>

                                                    
                                                    <td style="width:20%; text-align:right;vertical-align:middle">                                                   
                                                        ${Number(item.total_bomberos || 0).toFixed(2)}                                                    
                                                    </td>

                                                    <td style="width:20%; text-align:right;vertical-align:middle">                                                  
                                                        ${Number(item.total_adm || 0).toFixed(2)}                                                    
                                                    </td>

                                                   

                                                    <td style="width:35%; text-align:right;vertical-align:middle">                                                   
                                                        ${Number(item.total_cobrado || 0).toFixed(2)}                                                   
                                                    </td>
                                                
                                            </tr>`);
                    }else if(item.tipo_pago==3){ 
                        $('#tabla_detalle_urb_otrosCH').append(`<tr>
                                                    <td style="width:5%; text-align:left; vertical-align:middle">
                                                        ${item.tipo_anio}                                                     
                                                    </td>
                                                    
                                                    <td style="width:10%;  text-align:left; vertical-align:middle">
                                                        ${item.num_titulo} 
                                                    </td>
                                                        <td style="width:20%; text-align:right; vertical-align:middle">
                                                        ${Number(item.total_seguridad || 0).toFixed(2)}
                                                    </td>

                                                    
                                                    <td style="width:20%; text-align:right;vertical-align:middle">                                                   
                                                        ${Number(item.total_bomberos || 0).toFixed(2)}                                                    
                                                    </td>

                                                    <td style="width:20%; text-align:right;vertical-align:middle">                                                  
                                                        ${Number(item.total_adm || 0).toFixed(2)}                                                    
                                                    </td>

                                                   

                                                    <td style="width:35%; text-align:right;vertical-align:middle">                                                   
                                                        ${Number(item.total_cobrado || 0).toFixed(2)}                                                   
                                                    </td>
                                                
                                            </tr>`);
                    }else if(item.tipo_pago==4){ 
                        $('#tabla_detalle_urb_otrosTC').append(`<tr>
                                                    <td style="width:5%; text-align:left; vertical-align:middle">
                                                        ${item.tipo_anio}                                                     
                                                    </td>
                                                    
                                                    <td style="width:10%;  text-align:left; vertical-align:middle">
                                                        ${item.num_titulo} 
                                                    </td>
                                                        <td style="width:20%; text-align:right; vertical-align:middle">
                                                        ${Number(item.total_seguridad || 0).toFixed(2)}
                                                    </td>

                                                    
                                                    <td style="width:20%; text-align:right;vertical-align:middle">                                                   
                                                        ${Number(item.total_bomberos || 0).toFixed(2)}                                                    
                                                    </td>

                                                    <td style="width:20%; text-align:right;vertical-align:middle">                                                  
                                                        ${Number(item.total_adm || 0).toFixed(2)}                                                    
                                                    </td>

                                                   

                                                    <td style="width:35%; text-align:right;vertical-align:middle">                                                   
                                                        ${Number(item.total_cobrado || 0).toFixed(2)}                                                   
                                                    </td>
                                                
                                            </tr>`);
                    }else if(item.tipo_pago==5){ 
                        $('#tabla_detalle_urb_otrosCTR').append(`<tr>
                                                    <td style="width:5%; text-align:left; vertical-align:middle">
                                                        ${item.tipo_anio}                                                     
                                                    </td>
                                                    
                                                    <td style="width:10%;  text-align:left; vertical-align:middle">
                                                        ${item.num_titulo} 
                                                    </td>
                                                        <td style="width:20%; text-align:right; vertical-align:middle">
                                                        ${Number(item.total_seguridad || 0).toFixed(2)}
                                                    </td>

                                                    
                                                    <td style="width:20%; text-align:right;vertical-align:middle">                                                   
                                                        ${Number(item.total_bomberos || 0).toFixed(2)}                                                    
                                                    </td>

                                                    <td style="width:20%; text-align:right;vertical-align:middle">                                                  
                                                        ${Number(item.total_adm || 0).toFixed(2)}                                                    
                                                    </td>

                                                   

                                                    <td style="width:35%; text-align:right;vertical-align:middle">                                                   
                                                        ${Number(item.total_cobrado || 0).toFixed(2)}                                                   
                                                    </td>
                                                
                                            </tr>`);
                    } else if(item.tipo_pago==6){ 
                        $('#tabla_detalle_urb_otrosCOM').append(`<tr>
                                                    <td style="width:5%; text-align:left; vertical-align:middle">
                                                        ${item.tipo_anio}                                                     
                                                    </td>
                                                    
                                                    <td style="width:10%;  text-align:left; vertical-align:middle">
                                                        ${item.num_titulo} 
                                                    </td>
                                                        <td style="width:20%; text-align:right; vertical-align:middle">
                                                        ${Number(item.total_seguridad || 0).toFixed(2)}
                                                    </td>

                                                    
                                                    <td style="width:20%; text-align:right;vertical-align:middle">                                                   
                                                        ${Number(item.total_bomberos || 0).toFixed(2)}                                                    
                                                    </td>

                                                    <td style="width:20%; text-align:right;vertical-align:middle">                                                  
                                                        ${Number(item.total_adm || 0).toFixed(2)}                                                    
                                                    </td>

                                                   

                                                    <td style="width:35%; text-align:right;vertical-align:middle">                                                   
                                                        ${Number(item.total_cobrado || 0).toFixed(2)}                                                   
                                                    </td>
                                                
                                            </tr>`);
                    }     
                })
                cargar_estilos_datatable_urb_otros('tabla_detalle_urb_otros', response.fecha);
                cargar_estilos_datatable_urb_otrosNC('tabla_detalle_urb_otrosNC', response.fecha);
                cargar_estilos_datatable_urb_otrosCH('tabla_detalle_urb_otrosCH', response.fecha);
                cargar_estilos_datatable_urb_otrosTC('tabla_detalle_urb_otrosTC', response.fecha);
                cargar_estilos_datatable_urb_otrosTR('tabla_detalle_urb_otrosTR', response.fecha);
                cargar_estilos_datatable_urb_otrosCOM('tabla_detalle_urb_otrosCOM', response.fecha);

               

                setTimeout(() => {
                     $('#vista_buscador').hide()
                $('#vista_detalle_urb').show()
                }, 200);
            }
        },
        error: function(xhr, status, error) {
            vistacargando("")
            console.error("Error al obtener los datos:", error);
        }
    });
} 




function regresar(){
    $('#vista_buscador').show(200)
    $('#vista_detalle').hide(200)
    $('#vista_detalle_urb').hide(200)
}

function descargarReporte(id){
    vistacargando("m","Espere por favor")
    $.get("../transito-imprimir/"+id+'/G', function(data){
        vistacargando("")
        if(data.error==true){
            alertNotificar(data.mensaje,"error");
            return;   
        }
        verpdf(data.pdf)
        console.log(data)
        
        
    }).fail(function(){
        vistacargando("")
        alertNotificar("Se produjo un error, por favor intentelo más tarde","error");  
    });
}

function verpdf(ruta){

    var iframe=$('#iframePdf');
    iframe.attr("src", "../patente/documento/"+ruta);   
    $("#vinculo").attr("href", '../patente/descargar-documento/'+ruta);
    $("#documentopdf").modal("show");
}

