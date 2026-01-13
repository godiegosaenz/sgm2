function cambioData(){
    $('#btn_descargar').hide()
    $('#grafico_urbano').hide()    
    $('#btn_consultar').show()
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
                    $('#tabla_ingreso').append(`<tr>
                                                    <td style="width:10%; text-align:left; vertical-align:middle">
                                                       ${item.codigo} 
                                                       
                                                    </td>
    
                                                  
                                                    <td style="width:60%;  text-align:left; vertical-align:middle">
                                                        ${item.detalle_imp} 
                                                    </td>
    
                                                     <td style="width:10%; text-align:right; vertical-align:middle">
                                                       ${item.total_pago_anteriores == '.00' ? '0.00' : item.total_pago_anteriores
} 
                                                       
                                                    </td>
    
                                                   
                                                    <td style="width:10%; text-align:right;vertical-align:middle">
                                                       ${item.total_pago_anio_actual == '.00' ? '0.00' : item.total_pago_anio_actual
} 
                                                       
                                                    </td>
    
                                                    
                                                   
                                                    <td style="width:10%; text-align:right; vertical-align:middle">
    
                                                     
                                                        ${total_codigo == '0' ? '0.00' : total_codigo}
                                                       
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
$('#btnExcel').on('click', function () {
    tablaPredio.button('.buttons-excel').trigger();
});
function verDetalle(){

    var num_col = $("#tabla_detalle thead tr th").length;
    $("#tabla_detalle tbody").html('');

	$('#tabla_detalle').DataTable().destroy();
	$('#tabla_detalle tbody').empty(); 
    var ruta="reporte-recaudacion"
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
            vistacargando("")
            console.log(response)
            if(response.error==true){
                alertNotificar("Ocurrio un error","error")
                return
            }
            // window.location.href='../analitica/descargar-reporte/'+response.pdf

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

