function llenarTabla(){
  
    let tipo = $('#tipo').val();
   
    if (tipo === '') {
        $('#tipo').addClass('is-invalid');
        alertNotificar('Ingrese la cedula','error')
        return
        
    }
   
    $("#tablaRepetidos tbody").html('');
    // $('#tablaRepetidos tbody').empty(); 

    $('#tablaRepetidos').DataTable().destroy();
	$('#tablaRepetidos tbody').empty(); 
    var num_col = $("#tablaRepetidos thead tr th").length; //obtenemos el numero de columnas de la tabla
    vistacargando("m", "Espere por favor")
    $.get('buscar-repetidas/'+tipo, function(data){
      
        vistacargando("")
        if(data.error==true){
			$("#tablaRepetidos tbody").html('');
			$("#tablaRepetidos tbody").html(`<tr><td colspan="${num_col}" style="text-align:center>No existen registros</td></tr>`);
			alertNotificar(data.mensaje,"error");
            // cancelar()
			return;   
		}
		if(data.error==false){
			if(data.resultado.length==0){
				$("#tablaRepetidos tbody").html('');
				$("#tablaRepetidos tbody").html(`<tr><td colspan="${num_col}" style="text-align:center">No existen registros</td></tr>`);
				alertNotificar("No se encontró información","error");
                // cancelar()
				return;
			}
			
			$("#tablaRepetidos tbody").html('');
         
			$.each(data.resultado,function(i, item){
                // let anio=item.CarVe_NumTitulo               
                // anio=anio.split("-");
                let txt="Por Pagar"
                let icono=`<i class="bi bi-circle-fill" style="color:red;"></i>`;
                if(item.CarVe_Estado=='C'){
                    icono=`<i class="bi bi-circle-fill" style="color:green;"></i>`;
                    txt="Cancelado"
                }
                let nombre=item.CarVe_Nombres
                if(i.Ciu_Nombres!=""){
                    nombre=item.Ciu_Nombres+" "+item.Ciu_Apellidos
                }
				$('#tablaRepetidos').append(`<tr>
                                               

                                                <td style="width:10%;  text-align:left; vertical-align:middle">
                                                    ${item.CarVe_CI}
                                                </td>
                                               
                                                <td style="width:40%; text-align:center; vertical-align:middle">
                                                    ${item.CarVe_Nombres}
                                                </td>

                                                <td style="width:15%; text-align:center; vertical-align:middle">
                                                    ${item.CarVe_FechaEmision}
                                                </td>

                                                <td style="width:15%; text-align:center; vertical-align:middle">
                                                    ${item.CarVe_NumTitulo}
                                                </td>

                                                <td style="width:15%; text-align:center; vertical-align:middle">
                                                    ${icono}  ${txt}
                                                </td>

										</tr>`);
			})
          
            cargar_estilos_datatable('tablaRepetidos');
            
		}
    
    }).fail(function(){
        vistacargando("")
        alertNotificar("Se produjo un error, por favor intentelo más tarde","error");  
        $("#tablaRepetidos tbody").html('');
		$("#tablaRepetidos tbody").html(`<tr><td colspan="${num_col}" style="text-align:center">Se produjo un error, por favor intentelo más tarde</td></tr>`);
    });
}

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
			// url: 'json/datatables/spanish.json',
		},
	}); 
	$('.collapse-link').click();
	$('.datatable_wrapper').children('.row').css('overflow','inherit !important');

	$('.table-responsive').css({'padding-top':'12px','padding-bottom':'12px', 'border':'0', 'overflow-x':'inherit'});	
}

function quitarDuplicados(){
  
    let tipo = $('#tipo').val();
   
    if (tipo === '') {
        $('#tipo').addClass('is-invalid');
        alertNotificar('Ingrese la cedula','error')
        return
        
    }

    vistacargando("m", "Espere por favor")
    $.get('quitar-repetidos/'+tipo, function(data){
        console.log(data)
        vistacargando("")
        if(data.error==true){
			alertNotificar(data.mensaje,"error");
			return;   
		}
        // alert("descargar-txt/"+data.txt )
        alertNotificar(data.mensaje,"success");
        window.location.href="descargar-txt/"+data.txt  
        // llenarTabla()
    }).fail(function(){
        vistacargando("")
        alertNotificar("Se produjo un error, por favor intentelo más tarde","error");  
        $("#tablaRepetidos tbody").html('');
        $("#tablaRepetidos tbody").html(`<tr><td colspan="${num_col}" style="text-align:center">Se produjo un error, por favor intentelo más tarde</td></tr>`);
    });
   
}
