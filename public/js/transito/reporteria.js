function cambioData(){
    $('#btn_descargar').hide()
    $('#grafico_urbano').hide()    
    $('#btn_consultar').show()
}
function mostrarData(){
    $('#btn_descargar').hide()
    $('#btn_consultar').show()
    let filtroDesde=$('#filtroDesde').val()
    let filtroHasta=$('#filtroHasta').val()
    let filtroTipo=$('#filtroTipo').val()

    if(filtroDesde==""){
        alertNotificar("Debe seleccionar la fecha inicial","error")
        $('#filtroDesde').focus()       
        return
    }

    if(filtroHasta==""){
        alertNotificar("Debe seleccionar la fecha final","error")
        $('#filtroDesde').focus()       
        return
    }
    
    if(filtroHasta<filtroDesde){        
        alertNotificar("La fecha final debe ser mayor a la inicial","error")
        $('#filtroHasta').focus()
        return
    }

    $("#formReporteria").submit()
}

$("#formReporteria").submit(function(e){
    var num_col = $("#tabla_ingreso thead tr th").length;
    $("#tabla_ingreso tbody").html('');

	$('#tabla_ingreso').DataTable().destroy();
	$('#tabla_ingreso tbody').empty(); 
    
    e.preventDefault();
    var ruta="pago-consulta-transito"
        
    vistacargando("m","Espere por favor")
    $.ajaxSetup({
        headers: {
            'X-CSRF-TOKEN': $('meta[name="csrf-token"]').attr('content')
        }
    });

    var tipo="POST"
  
    let filtroDesde=$('#filtroDesde').val()
    let filtroHasta=$('#filtroHasta').val()
    let filtroTipo=$('#filtroTipo').val()

    $.ajax({
            
        type: tipo,
        url: ruta,
        method: tipo,             
        data: {
            filtroDesde: filtroDesde,
            filtroHasta: filtroHasta,
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
                    cancelar()
                    return;
                }

                $("#tabla_ingreso tbody").html('');

                $.each(response.data,function(i, item){
                    console.log("ssss")
                    console.log(item)
                   
                    $('#tabla_ingreso').append(`<tr>
                                                    <td style="width:25%; text-align:left; vertical-align:middle">
                                                        <li><b>Descripcion:</b> ${item.clase}</li>
                                                        <li><b>Marca-Modelo:</b> ${item.marca_veh}-${item.year}</li>
                                                        <li><b>Placa:</b> ${item.placa}</li> 
                                                        <li><b>Avaluo:</b> ${item.avaluo}</li> 
                                                        
                                                    </td>
    
                                                  
                                                    <td style="width:30%;  text-align:left; vertical-align:middle">
                                                        <li><b>C.I:</b> ${item.identificacion_propietario}</li>
                                                        <li><b>Nombres:</b> ${item.nombre_propietario} ${item.apellido_propietario}</li>
                                                    </td>
    
                                                     <td style="width:30%; text-align:left; vertical-align:middle">
                                                        <li><b>Usuario:</b> ${item.nombre_usuario}</li>
                                                        <li><b>Fecha Pago:</b> ${item.created_at}</li>
                                                        <li><b>Año Impuesto:</b> ${item.year_impuesto}</li>
                                                    </td>
    
                                                   
                                                    <td style="width:10%; text-align:right;vertical-align:middle">
                                                        ${item.total_pagar}
                                                       
                                                    </td>
    
                                                    
                                                   
                                                    <td style="width:5%; text-align:center; vertical-align:middle">
    
                                                        <button type="button" class="btn btn-xs btn-primary" onclick="descargarReporte('${item.identificador}')"> <i class="fa fa-file-pdf-o"></i></button>
    
                                                       
                                                    </td>
    
                                                    
                                                
                                            </tr>`);
                })
                cargar_estilos_datatable('tabla_ingreso');
                $('#btn_descargar').show()
                $('#btn_consultar').hide()
            }

            
        },
        error: function(xhr, status, error) {
            vistacargando("")
            console.error("Error al obtener los datos:", error);
        }
    });
})

function cargar_estilos_datatable(idtabla){
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

function descargarPdf(){
   var ruta="reporte-diario"
    vistacargando("m","Espere por favor")
    $.ajaxSetup({
        headers: {
            'X-CSRF-TOKEN': $('meta[name="csrf-token"]').attr('content')
        }
    });

    var tipo="POST"
  
    let filtroDesde=$('#filtroDesde').val()
    let filtroHasta=$('#filtroHasta').val()
    let filtroTipo=$('#filtroTipo').val()

    $.ajax({
            
        type: tipo,
        url: ruta,
        method: tipo,             
        data: {
            filtroDesde: filtroDesde,
            filtroHasta: filtroHasta,
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
            window.location.href='../analitica/descargar-reporte/'+response.pdf
        },
        error: function(xhr, status, error) {
            vistacargando("")
            console.error("Error al obtener los datos:", error);
        }
    });
} 

function descargarReporte(id){
    vistacargando("m","Espere por favor")
    $.get("../transito-imprimir/"+id, function(data){
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

