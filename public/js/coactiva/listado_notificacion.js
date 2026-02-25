
function llenar_tabla_notificacion(){
    var periodo=$('#periodo').val()
    if(periodo==""){return}

    // $("#tableNotificacion tbody").html('');
    // $('#tableNotificacion tbody').empty(); 
    $("#tableNotificacion tbody").html('');
    $('#tableNotificacion').DataTable().destroy();
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
                }else if(item.estado=='Coactivado'){
                    icono=`<span class="badge-coactivado">
                            <i class="fas fa-exclamation-circle" style="margin-right: 5px;"></i> Coactivado
                            </span>`;
                }
                
                let icono2=""
                if(item.dias_transcurridos>=10){
                    icono2=`<span class="badge-expirado">${item.dias_transcurridos} </span>`;
                }else{
                    icono2=`<span class="badge-sin-expirar">${item.dias_transcurridos} </span>`;
                }

                let nombre=""
                let num_ident=""
                if(item.id_persona!=null){
                    nombre=item.ente.apellidos+" "+item.ente.nombres
                    num_ident=item.ente.ci_ruc
                }else{
                    nombre=item.contribuyente
                    num_ident=item.num_ident
                }
				$('#tableNotificacion').append(`<tr>
                                                <td style="width:5%; text-align:center; vertical-align:middle">
                                                   <button type="button" class="btn btn-success btn-sm" onclick="detalleNot('${item.id}')">
                                                        <i class="fa fa-eye"></i>
                                                    </button>                                               
                                                </td>

                                                <td style="width:5%; text-align:center; vertical-align:middle">
                                                    ${num_ident}                                                     
                                                </td>
                                                <td style="width:40%; text-align:center; vertical-align:middle">
                                                    ${nombre}                                                         
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
          
            cargar_estilos_datatable('tableNotificacion')
            
		}
    
    }).fail(function(){
        vistacargando("")
        alertNotificar("Se produjo un error, por favor intentelo más tarde","error");  
        $("#tableNotificacion tbody").html('');
		$("#tableNotificacion tbody").html(`<tr><td colspan="${num_col}" style="text-align:center">Se produjo un error, por favor intentelo más tarde</td></tr>`);
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
        order: [[ 3, "desc" ]],
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
          
            $('#total_deuda_proceso').html('$'+data.total)
		}
    
    }).fail(function(){
        vistacargando("")
        alertNotificar("Se produjo un error, por favor intentelo más tarde","error");  
        $("#tableProcesoNot tbody").html('');
		$("#tableProcesoNot tbody").html(`<tr><td colspan="${num_col}" style="text-align:center">Se produjo un error, por favor intentelo más tarde</td></tr>`);
    });
}

globalThis.EstadoCoactivadoGlobal=0
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
            $('.seccion_detalle_coa').hide()
            $('.botone_inicia_proceso').show()
			$('#modalDetalleNot').modal('show')
			$("#tableDetNot tbody").html('');
            
            let clave_matr = [];
			$.each(data.resultado.data,function(i, item){
                
                clave_matr.push(item.liquidacion.predio);
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
            $('.valor_notificado').html(data.resultado.total_notificado)
            $('#dias_notificado').html(data.resultado.dias_transcurridos)

            $('#doc_generado').html(`<i class="fa fa-file-pdf" style="color:skyblue" onclick="verpdf('${data.resultado.documento}')"><i>`)
            $('#doc_subido').html(`<i class="fa fa-file-pdf" style="color:skyblue" onclick="verpdf_subido('${data.resultado.documento_subido}','0')"><i>`)

            $('#predio_localizacion').html(data.resultado.predio) 
            let prediosUnicos = [...new Set(clave_matr)];
            let clave_matricula = prediosUnicos.join(', ');

            $('#matr_clave').html(`
                <label>${clave_matricula}</label>
            `);
            $('#idcoa').val('')
            if(data.resultado.estado=='Coactivado'){
               
                $("#tableDetCoa tbody").html('');
                $('#tableDetCoa tbody').empty(); 

                $.each(data.datosCoa.data,function(i, item){
                
				$('#tableDetCoa').append(`<tr>
                                                
                                                <td style="width:15%; text-align:center; vertical-align:middle">
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
                                                <td style="width:10%; text-align:center; vertical-align:middle">
                                                   ${item.recargo}                                             
                                                </td>

                                                 <td style="width:10%; text-align:center; vertical-align:middle">
                                                   ${item.coactiva === null ? '0.00' : item.coactiva}                                             
                                                </td>

                                                <td style="width:10%; text-align:center; vertical-align:middle">
                                                    ${item.total}                                                   
                                                </td>
                                               
											
										</tr>`);
			    })
          

                $('.seccion_detalle_coa').show()
                $('.botone_inicia_proceso').hide()

                $('#nombre_coactivador').html(data.datosCoa.profesional)
                $('#fecha_coactivador').html(data.datosCoa.fecha_registra)

                $('#doc_generado_coa').html(`<i class="fa fa-file-pdf" style="color:skyblue" onclick="verpdf('${data.datosCoa.documento}')"><i>`)
                $('#doc_subido_coa').html(`<i class="fa fa-file-pdf" style="color:skyblue" onclick="verpdf_subido('${data.datosCoa.documento_subido}','1')"><i>`)

                $('#idcoa').val(data.datosCoa.id)
            }
            
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

function verpdf(ruta){

    var iframe=$('#iframePdf');
    iframe.attr("src", "coactiva/documento/"+ruta);   
    $("#vinculo").attr("href", 'coactiva/documento-descargar/'+ruta);
    $("#documentopdf").modal("show");
}

function verpdf_subido(ruta,valor){
    $('#es_coact').val(valor)
    var iframe=$('#iframePdfSubido');
    iframe.attr("src", "coactiva/documento/"+ruta);   
    $("#vinculoSubido").attr("href", 'coactiva/documento-descargar/'+ruta);
    $("#documentopdf_subido").modal("show");
}


function abrirModalSubir(){
    $('#archivo').val('')
    $("#documentopdf_subido").modal("hide");
    $("#subir_documento").modal("show");
    
    $('#idnoti').val($('#id_notifica').val())
}

$("#formArchivoFirmado").submit(function(e){
    
    let documento=$('#archivo').val()
    if(documento=="" || documento==null){
        alertNotificar("Debe subir el documento","error")
        return
    }

    e.preventDefault();
   
    vistacargando("m","Espere por favor")
    $.ajaxSetup({
        headers: {
            'X-CSRF-TOKEN': $('meta[name="csrf-token"]').attr('content')
        }
    });
    let valor=$('#es_coact').val()
   
    let tipo="POST"
    let url_form="guardar-documento-firmado-noti"
    if($('#es_coact').val()==1){
        url_form='guardar-documento-firmado-coact'
    }
    var FrmData = new FormData(this);
   
    $.ajax({
            
        type: tipo,
        url: url_form,
        method: tipo,             
		data: FrmData,      
		
        contentType:false,
        cache:false,
        processData:false, 

        success: function(data){
            vistacargando("");                
            if(data.error==true){
                alertNotificar(data.mensaje,'error');
                return;                      
            }
           
            alertNotificar(data.mensaje,"success");
            if(valor==0){
                $('#doc_subido').html(`<i class="fa fa-file-pdf" style="color:skyblue" onclick="verpdf_subido('${data.archivo}','${valor}')"><i>`)
            }else{
                $('#doc_subido_coa').html(`<i class="fa fa-file-pdf" style="color:skyblue" onclick="verpdf_subido('${data.archivo}','${valor}')"><i>`)
            }
            $('#subir_documento').modal('hide')
                            
        }, error:function (data) {
            console.log(data)

            vistacargando("");
            alertNotificar('Ocurrió un error','error');
        }
    });
})

function iniciarProcesoCoact(){
    let id_not=$('#id_notifica').val()
    swal({
        title: '¿Desea iniciar el proceso de coativa?',
        type: "warning",
        showCancelButton: true,
        confirmButtonClass: "btn-danger",
        confirmButtonText: "Si, continuar",
        cancelButtonText: "No, cancelar",
        closeOnConfirm: false,
        closeOnCancel: false
    },
    function(isConfirm) {
        if (isConfirm) { 
            vistacargando("m", "Espere por favor")
            $.get('inicia-proceso-coactiva/'+id_not, function(data){
                console.log(data)
            
                vistacargando("")
                if(data.error==true){
                    alertNotificar(data.mensaje,"error");
                    // cancelar()
                    return;   
                }
                alertNotificar(data.mensaje,"success");
                $('#modalDetalleNot').modal('hide')

                setTimeout(function() {
                    verpdf(data.pdf)
                }, 1000);
                llenar_tabla_notificacion()
            
            }).fail(function(){
                vistacargando("")
                alertNotificar("Se produjo un error, por favor intentelo más tarde","error");  
            
            });
        }
        sweetAlert.close();   // ocultamos la ventana de pregunta
    });   
}

function verpdfCoa(ruta){
    console.log('coactiva/documento-descargar/'+ruta)
    var iframe=$('#iframePdfCoa');
    iframe.attr("src", "coactiva/documento/"+ruta);   
    $("#vinculoCoa").attr("href", 'coactiva/documento-descargar/'+ruta);
    $("#documentopdf").modal("show");
}

$('#documentopdfcoa').on('hidden.bs.modal', function () {
    llenar_tabla_notificacion()
   
   
});