
function llenar_tabla_notificacion(){
    var periodo=$('#periodo').val()
    if(periodo==""){return}

    $("#tableNotificacion tbody").html('');
    $('#tableNotificacion tbody').empty(); 
    var num_col = $("#tableNotificacion thead tr th").length; //obtenemos el numero de columnas de la tabla
    vistacargando("m", "Espere por favor")
    $.get('pago-notificaciones/'+periodo, function(data){
        console.log(data)
       
        vistacargando("")
        if(data.error==true){
			$("#tableNotificacion tbody").html('');
			$("#tableNotificacion tbody").html(`<tr><td colspan="${num_col}" style="text-align:center>No existen registros</td></tr>`);
			alertNotificar(data.mensaje,"error");
            // cancelar()
			return;   
		}
		if(data.error==false){
			if(data.resultado.length==0){
				$("#tableNotificacion tbody").html('');
				$("#tableNotificacion tbody").html(`<tr><td colspan="${num_col}" style="text-align:center">No existen registros</td></tr>`);
				alertNotificar("No se encontró información","error");
                // cancelar()
				return;
			}
			
			$("#tableNotificacion tbody").html('');
         
			$.each(data.resultado,function(i, item){
             
                let icono=`<span class="badge-notificado"> 🔔 Notificado</span>`;
               
                if(item.estado=='Pagado'){
                    icono=`<span class="badge-pagado"> 💵 Pagado </span>`;
                }
                
                let icono2=""
                if(item.dias_transcurridos>=10){
                    icono2=`<span class="badge-expirado">${item.dias_transcurridos} </span>`;
                }else{
                    icono2=`<span class="badge-sin-expirar">${item.dias_transcurridos} </span>`;
                }

                let nombre=item.CarVe_Nombres
                if(i.Ciu_Nombres!=""){
                    nombre=item.Ciu_Nombres+" "+item.Ciu_Apellidos
                }
				$('#tableNotificacion').append(`<tr>
                                                <td style="width:5%; text-align:center; vertical-align:middle">
                                                   <button type="button" class="btn btn-success btn-sm" onclick="detalleNot('${item.id}')">
                                                        <i class="fa fa-eye"></i>
                                                    </button>                                               
                                                </td>

                                                <td style="width:5%; text-align:center; vertical-align:middle">
                                                    ${item.ente.ci_ruc}                                                     
                                                </td>
                                                <td style="width:40%; text-align:center; vertical-align:middle">
                                                    ${item.ente.apellidos} ${item.ente.nombres}                                                         
                                                </td>
                                                <td style="width:10%; text-align:center; vertical-align:middle">
                                                    ${item.fecha_registra}                                                     
                                                </td>
                                                <td style="width:10%; text-align:center; vertical-align:middle">
                                                    ${item.total_notificado}                                                     
                                                </td>
                                                <td style="width:10%; text-align:center; vertical-align:middle">
                                                    ${icono}                                                     
                                                </td>

                                                <td style="width:10%; text-align:center; vertical-align:middle">
                                                    ${icono2}                                                     
                                                </td>
                                                <td style="width:10%; text-align:center; vertical-align:middle">
                                                                                                   
                                                </td>

                                               
                                                
											
										</tr>`);
			})
          

            
		}
    
    }).fail(function(){
        vistacargando("")
        alertNotificar("Se produjo un error, por favor intentelo más tarde","error");  
        $("#tableNotificacion tbody").html('');
		$("#tableNotificacion tbody").html(`<tr><td colspan="${num_col}" style="text-align:center">Se produjo un error, por favor intentelo más tarde</td></tr>`);
    });
}

function detalleProcesoIniciaCoa(){
    $("#tableProcesoNot tbody").html('');
    $('#tableProcesoNot tbody').empty(); 
    var num_col = $("#tableProcesoNot thead tr th").length; //obtenemos el numero de columnas de la tabla
    var id=$('#id_notifica').val()
    vistacargando("m","Espere por favor")
    $.get('pago-notificaciones-detalle-proceso-coac/'+id, function(data){
        console.log(data)
       
        vistacargando("")
        if(data.error==true){
			$("#tableProcesoNot tbody").html('');
			$("#tableProcesoNot tbody").html(`<tr><td colspan="${num_col}" style="text-align:center>No existen registros</td></tr>`);
			alertNotificar(data.mensaje,"error");
            // cancelar()
			return;   
		}
		if(data.error==false){
			if(data.resultado.length==0){
				$("#tableProcesoNot tbody").html('');
				$("#tableProcesoNot tbody").html(`<tr><td colspan="${num_col}" style="text-align:center">No existen registros</td></tr>`);
				alertNotificar("No se encontró información","error");
                // cancelar()
				return;
			}

            $('#seccion_detalle').hide()
            $('#seccion_inicia_proceso').show()
			$("#tableProcesoNot tbody").html('');
         
			$.each(data.resultado,function(i, item){

              
				$('#tableProcesoNot').append(`<tr>
                                                
                                                <td style="width:20%; text-align:center; vertical-align:middle">
                                                    ${item.predio}
                                                     
                                                </td>
                                                <td style="width:10%; text-align:center; vertical-align:middle">
                                                   ${item.anio}                                                    
                                                </td>
                                                <td style="width:15%; text-align:center; vertical-align:middle">
                                                   ${item.subtotal_emi}                                                    
                                                </td>
                                                <td style="width:15%; text-align:center; vertical-align:middle">
                                                   ${item.intereses}                                                    
                                                </td>
                                                <td style="width:15%; text-align:center; vertical-align:middle">
                                                    ${item.descuento}                                            
                                                </td>
                                                <td style="width:15%; text-align:center; vertical-align:middle">
                                                   ${item.recargo}                                             
                                                </td>

                                                <td style="width:10%; text-align:center; vertical-align:middle">
                                                    ${item.total_pagar}                                                   
                                                </td>
                                               
											
										</tr>`);
			})
          
            $('#total_deuda_proceso').html('$ '+data.total)
		}
    
    }).fail(function(){
        vistacargando("")
        alertNotificar("Se produjo un error, por favor intentelo más tarde","error");  
        $("#tableProcesoNot tbody").html('');
		$("#tableProcesoNot tbody").html(`<tr><td colspan="${num_col}" style="text-align:center">Se produjo un error, por favor intentelo más tarde</td></tr>`);
    });
}

function detalleNot(id){
    $('#id_notifica').val('')
    $('.label_not').html('')
    $("#tableDetNot tbody").html('');
    $('#tableDetNot tbody').empty(); 
    var num_col = $("#tableDetNot thead tr th").length; //obtenemos el numero de columnas de la tabla
    vistacargando("m", "Espere por favor")
    $.get('pago-notificaciones-detalle/'+id, function(data){
        console.log(data)
        $('#id_notifica').val(id)
        vistacargando("")
        if(data.error==true){
			$("#tableDetNot tbody").html('');
			$("#tableDetNot tbody").html(`<tr><td colspan="${num_col}" style="text-align:center>No existen registros</td></tr>`);
			alertNotificar(data.mensaje,"error");
            // cancelar()
			return;   
		}
		if(data.error==false){
			if(data.resultado.data.length==0){
				$("#tableDetNot tbody").html('');
				$("#tableDetNot tbody").html(`<tr><td colspan="${num_col}" style="text-align:center">No existen registros</td></tr>`);
				alertNotificar("No se encontró información","error");
                // cancelar()
				return;
			}

            $('#seccion_detalle').show()
            $('#seccion_inicia_proceso').hide()

			$('#modalDetalleNot').modal('show')
			$("#tableDetNot tbody").html('');
         
			$.each(data.resultado.data,function(i, item){

              
				$('#tableDetNot').append(`<tr>
                                                
                                                <td style="width:20%; text-align:center; vertical-align:middle">
                                                    ${item.liquidacion.predio}
                                                     
                                                </td>
                                                <td style="width:10%; text-align:center; vertical-align:middle">
                                                   ${item.liquidacion.anio}                                                    
                                                </td>
                                                <td style="width:15%; text-align:center; vertical-align:middle">
                                                   ${item.subtotal}                                                    
                                                </td>
                                                <td style="width:15%; text-align:center; vertical-align:middle">
                                                   ${item.interes}                                                    
                                                </td>
                                                <td style="width:15%; text-align:center; vertical-align:middle">
                                                    ${item.descuento}                                            
                                                </td>
                                                <td style="width:15%; text-align:center; vertical-align:middle">
                                                   ${item.recargo}                                             
                                                </td>

                                                <td style="width:10%; text-align:center; vertical-align:middle">
                                                    ${item.total}                                                   
                                                </td>
                                               
											
										</tr>`);
			})
          
            $('#nombre_notificador').html(data.resultado.profesional)
            $('#fecha_notificacion').html(data.resultado.fecha_registra)

            $('#num_ident_contr').html(data.resultado.ente.apellidos +" "+data.resultado.ente.nombres)
            $('#nombre_contr').html(data.resultado.ente.ci_ruc)
            $('#valor_notificado').html(data.resultado.total_notificado)
            $('#dias_notificado').html(data.resultado.dias_transcurridos)
            
		}
    
    }).fail(function(){
        vistacargando("")
        alertNotificar("Se produjo un error, por favor intentelo más tarde","error");  
        $("#tableDetNot tbody").html('');
		$("#tableDetNot tbody").html(`<tr><td colspan="${num_col}" style="text-align:center">Se produjo un error, por favor intentelo más tarde</td></tr>`);
    });

}

function cerrarModalNot(){
    $('#modalDetalleNot').modal('hide')
    
}