function llenar_tabla_notificacion(){
    var periodo=$('#periodo').val()
    if(periodo==""){return}

    $("#tableCoactiva tbody").html('');
    $('#tableCoactiva').DataTable().destroy();
	$('#tableCoactiva tbody').empty(); 
    var num_col = $("#tableCoactiva thead tr th").length; //obtenemos el numero de columnas de la tabla
    vistacargando("m", "Espere por favor")
    $.get('pago-coactivas/'+periodo, function(data){
        console.log(data)
       
        vistacargando("")
        if(data.error==true){
			$("#tableCoactiva tbody").html('');
			$("#tableCoactiva tbody").html(`<tr><td colspan="${num_col}" style="text-align:center>No existen registros</td></tr>`);
			alertNotificar(data.mensaje,"error");
            // cancelar()
			return;   
		}
		if(data.error==false){
			if(data.resultado.length==0){
				$("#tableCoactiva tbody").html('');
				$("#tableCoactiva tbody").html(`<tr><td colspan="${num_col}" style="text-align:center">No existen registros</td></tr>`);
				alertNotificar("No se encontró información","error");
                // cancelar()
				return;
			}
			
			$("#tableCoactiva tbody").html('');
         
			$.each(data.resultado,function(i, item){
             
                let icono=`<span class="badge-notificado"> 🔔 Notificado</span>`;
               
                if(item.estado_proceso==1){
                    icono=`<span class="badge-orden"> 💵 ORDEN DE PAGO INMEDIATO </span>`;
                }else if(item.estado_proceso==2){
                   icono=`<span class="badge-medidas"> <i class="fa fa-balance-scale" style="font-size: 16px;"></i> EN EJECUCIÓN CON MEDIDAS </span>`;
                }else if(item.estado_proceso==3){
                    icono=`<span class="badge-cancelado"> <i class="fa fa-archive" style="font-size: 16px;"></i> CANCELADO \ ARCHIVADO </span>`;
                }else if(item.estado_proceso==4){
                    icono=`<span class="badge-acuerdo"> <i class="fa fa-handshake" style="font-size: 16px;"></i> ACUERDO DE PAGO </span>`;
                }
                
                

                let nombre=""
                let num_ident=""
                if(item.notificacion.id_persona!=null){
                    nombre=item.notificacion.ente.apellidos+" "+item.notificacion.ente.nombres
                    num_ident=item.notificacion.ente.ci_ruc
                }else{
                    nombre=item.notificacion.contribuyente
                    num_ident=item.notificacion.num_ident
                }
				$('#tableCoactiva').append(`<tr>
                                                <td style="width:3%; text-align:center; vertical-align:middle">
                                                   <button type="button" class="btn btn-success btn-sm" onclick="detalleNot('${item.notificacion.id}')">
                                                        <i class="fa fa-eye"></i>
                                                    </button>                                               
                                                </td>

                                                <td style="width:38%; text-align:letf; vertical-align:middle">
                                                    <span><b>C.I.: </b>${num_ident}</span><br>   
                                                    <span><b>Nombres: </b>${nombre}</span>   

                                                </td>
                                                <td style="width:8%; text-align:center; vertical-align:middle">
                                                    ${item.fecha_registra}                                                            
                                                </td>
                                                <td style="width:9%; text-align:center; vertical-align:middle">
                                                    ${item.notificacion.total_notificado}                                                     
                                                </td>
                                                <td style="width:9%; text-align:center; vertical-align:middle">
                                                    ${item.valor_pago_inmediato}                                                     
                                                </td>
                                                <td style="width:7%; text-align:center; vertical-align:middle">
                                                    ${item.valor_cancelado === null ? '0.00' : item.valor_cancelado}                                                  
                                                </td>

                                                <td style="width:6%; text-align:center; vertical-align:middle">
                                                    ${item.estado_pago}                                                  
                                                </td>
                                                <td style="width:20%; text-align:center; vertical-align:middle">
                                                    ${icono}                                              
                                                </td>

                                               
                                                
											
										</tr>`);
			})
          
            cargar_estilos_datatable('tableCoactiva')
            
		}
    
    }).fail(function(){
        vistacargando("")
        alertNotificar("Se produjo un error, por favor intentelo más tarde","error");  
        $("#tableCoactiva tbody").html('');
		$("#tableCoactiva tbody").html(`<tr><td colspan="${num_col}" style="text-align:center">Se produjo un error, por favor intentelo más tarde</td></tr>`);
    });
}

function cargar_estilos_datatable_con(idtabla){
	$("#"+idtabla).DataTable({
		'paging'      : true,
		'searching'   : true,
		'ordering'    : true,
		'info'        : true,
		'autoWidth'   : true,
		"destroy":true,
        order: [[ 1, "desc" ]],
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

function cargar_estilos_datatable(idtabla){
	$("#"+idtabla).DataTable({
		'paging'      : true,
		'searching'   : true,
		'ordering'    : true,
		'info'        : true,
		'autoWidth'   : true,
		"destroy":true,
        order: [[ 2, "desc" ]],
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
globalThis.IdNotificaSele=""
function detalleNot(id){
   
    $('#id_notifica').val('')
    $('.label_not').html('')
    $("#tableDetNot tbody").html('');
    $('#tableDetNot tbody').empty(); 
    var num_col = $("#tableDetNot thead tr th").length; //obtenemos el numero de columnas de la tabla
    vistacargando("m", "Espere por favor")
    IdNotificaSele=id
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

                let clave_matricula=""
                let anio=""
                
                if(data.resultado.predio=='Urbano'){
                    clave_matr.push(item.clave_cat);
                    clave_matricula=item.liquidacion.predio
                    anio=item.liquidacion.anio
                    cont=data.resultado.ente.apellidos +" "+data.resultado.ente.nombres
                   
                }else{
                    clave_matr.push(item.clave_cat);
                    clave_matricula=item.clave_cat
                    anio=item.anio
                    cont=item.contrib
                  
                }
                
                // clave_matr.push(item.liquidacion.predio);
				$('#tableDetNot').append(`<tr>
                                                
                                                <td style="width:20%; text-align:center; vertical-align:middle">
                                                    ${clave_matricula}
                                                     
                                                </td>
                                                <td style="width:10%; text-align:center; vertical-align:middle">
                                                   ${anio}                                                    
                                                </td>
                                                <td style="width:15%; text-align:center; vertical-align:middle">
                                                   ${item.subtotal}                                                    
                                                </td>
                                                <td style="width:15%; text-align:center; vertical-align:middle">
                                                   ${item.interes}                                                    
                                                </td>
                                                <td style="width:15%; text-align:center; vertical-align:middle">
                                                    ${item.descuento === null ? '0.00' : item.descuento}                                             
                                                </td>
                                                <td style="width:15%; text-align:center; vertical-align:middle">
                                                   ${item.recargo === null ? '0.00' : item.recargo}                                              
                                                </td>

                                                <td style="width:10%; text-align:center; vertical-align:middle">
                                                    ${item.total}                                                   
                                                </td>
                                               
											
										</tr>`);
			})
          
            $('#nombre_notificador').html(data.resultado.profesional)
            $('#fecha_notificacion').html(data.resultado.fecha_registra)

             let ced_ruc=""

            if(data.resultado.predio=='Urbano'){
                ced_ruc=data.resultado.ente.ci_ruc
            }else{
                ced_ruc=data.resultado.num_ident
            }

            $('#num_ident_contr').html(cont)
            $('#nombre_contr').html(ced_ruc)
            $('.valor_notificado').html(data.resultado.total_notificado)
            $('#dias_notificado').html(data.resultado.dias_transcurridos)

            $('.titulo_modal').html(ced_ruc+" - "+cont)

            $('#doc_generado').html(`<i class="fa fa-file-pdf" style="color:skyblue" onclick="verpdf('${data.resultado.documento}')"><i>`)
            $('#doc_subido').html(`<i class="fa fa-file-pdf" style="color:skyblue" onclick="verpdf_subido('${data.resultado.documento_subido}','0')"><i>`)

            $('#predio_localizacion').html(data.resultado.predio) 
            let prediosUnicos = [...new Set(clave_matr)];
            let clave_matricula = prediosUnicos.join(', ');

            $('#matr_clave').html(`
                <label>${clave_matricula}</label>
            `);
            $('#idcoa').val('')
            $('#idcoa_conv').val('')
            $('#idcoa_medida').val('')
            $('#idcoa_pago').val('')
            $('.txt_conv').val('')
            if(data.resultado.estado=='Coactivado'){
               
                $("#tableDetCoa tbody").html('');
                $('#tableDetCoa tbody').empty(); 

                $.each(data.datosCoa.data,function(i, item){
                    let predio=""
                    let anio=""
                    if($('#predio_localizacion').html()=="Rural"){
                        predio=item.clave_cat
                        anio=item.anio
                    }else{
                        predio=item.liquidacion.predio
                        anio=item.liquidacion.anio
                    }
				    $('#tableDetCoa').append(`<tr>
                                                
                                                <td style="width:15%; text-align:center; vertical-align:middle">
                                                    ${predio}
                                                     
                                                </td>
                                                <td style="width:10%; text-align:center; vertical-align:middle">
                                                   ${anio}                                                    
                                                </td>
                                                <td style="width:15%; text-align:center; vertical-align:middle">
                                                   ${item.subtotal}                                                    
                                                </td>
                                                <td style="width:15%; text-align:center; vertical-align:middle">
                                                   ${item.interes}                                                    
                                                </td>
                                                <td style="width:15%; text-align:center; vertical-align:middle">
                                                    ${item.descuento === null ? '0.00' : item.descuento}                                          
                                                </td>
                                                <td style="width:10%; text-align:center; vertical-align:middle">
                                                   ${item.recargo === null ? '0.00' : item.recargo}                                              
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
                $('#idcoa_conv').val(data.datosCoa.id)
                $('#idcoa_medida').val(data.datosCoa.id)
                $('#idcoa_pago').val(data.datosCoa.id)
                
                llenar_tabla_cuota(data.datosCoa.id)
                llenar_tabla_medidas(data.datosCoa.id)
                llenar_tabla_pagos(data.datosCoa.id)

                $('.valor_coa').html(data.datosCoa.valor_pago_inmediato)
                $('#valor_adeudado').val(data.datosCoa.valor_pago_inmediato)


                $('#valor_pago_inm').val(data.datosCoa.valor_pago_inmediato)
                $('#medidas_txt').val('MEDIDAS SUPERINTENDENCIA, REGISTRO DE LA PROPIEDAD Y TRANSITO')
                $('#total_valor_deuda').val(data.datosCoa.valor_pago_inmediato)
                // alert(data.datosCoa.valor_pago_inmediato)
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
    e.preventDefault();
    let documento=$('#archivo').val()
    if(documento=="" || documento==null){
        alertNotificar("Debe subir el documento","error")
        return
    }

   
   
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

$("#FormConvenio").submit(function(e){
    e.preventDefault();
    let valor_adeudado=$('#valor_adeudado').val()
    let cuota_inicial=$('#cuota_inicial').val()
    let num_cuotas=$('#num_cuotas').val()
    let f_ini=$('#f_ini').val()
    let f_fin=$('#f_fin').val()
    let valor_coa=$('.valor_coa').html()
    if(valor_adeudado=="" || valor_adeudado==null){
        alertNotificar("Debe ingresar el valor adeudado","error")
        $('#valor_adeudado').focus()
        return
    }
   
    if(parseFloat(valor_adeudado)<parseFloat(valor_coa)){
        alertNotificar("El valor adeudado no debe ser menor a "+valor_coa,"error")
        $('#valor_adeudado').focus()
        return
    }

    if(cuota_inicial=="" || cuota_inicial==null){
        alertNotificar("Debe ingresar la cuota inicial","error")
        $('#cuota_inicial').focus()
        return
    }

    if(num_cuotas=="" || num_cuotas==null){
        alertNotificar("Debe ingresar el numero de cuotas","error")
        $('#num_cuotas').focus()
        return
    }

    if(f_ini=="" || f_ini==null){
        alertNotificar("Debe ingresar la fecha de inicio","error")
        $('#f_ini').focus()
        return
    }
    if(f_fin=="" || f_fin==null){
        alertNotificar("Debe ingresar la fecha de fin","error")
        $('#f_fin').focus()
        return
    }

    let startDate = new Date(f_ini.split('/').reverse().join('/')); // Cambiamos el formato dd/mm/yyyy a yyyy/mm/dd
    let endDate = new Date(f_fin.split('/').reverse().join('/'));

    // Validar si la fecha fin es posterior a la fecha inicio
    if (endDate <= startDate) {
        alertNotificar("La fecha final debe ser posterior a la fecha de inicio.","error");
        return; // Detenemos la ejecución si la validación falla
    }

    
    
    vistacargando("m","Espere por favor")
    $.ajaxSetup({
        headers: {
            'X-CSRF-TOKEN': $('meta[name="csrf-token"]').attr('content')
        }
    });
    let idcoa_conv=$('#idcoa_conv').val()
   
    let tipo="POST"
    let url_form="guardar-cuota-conv"
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
            llenar_tabla_cuota(idcoa_conv)
            llenar_tabla_medidas(idcoa_conv)
            llenar_tabla_pagos(idcoa_conv)
            llenar_tabla_notificacion()
            $('.txt_conv').val('')
                            
        }, error:function (data) {
            console.log(data)

            vistacargando("");
            alertNotificar('Ocurrió un error','error');
        }
    });
})

function llenar_tabla_cuota(id){
   
    $("#tableConvenio tbody").html('');
    $('#tableConvenio').DataTable().destroy();
	$('#tableConvenio tbody').empty(); 
    var num_col = $("#tableCoactiva thead tr th").length; //obtenemos el numero de columnas de la tabla
    vistacargando("m", "Espere por favor")
    $.get('llenar-tabla-convenio/'+id, function(data){
        console.log(data)
       
        vistacargando("")
        if(data.error==true){
			$("#tableConvenio tbody").html('');
			$("#tableConvenio tbody").html(`<tr><td colspan="${num_col}" style="text-align:center>No existen registros</td></tr>`);
			alertNotificar(data.mensaje,"error");
            // cancelar()
			return;   
		}
		if(data.error==false){
			if(data.resultado.length==0){
				$("#tableConvenio tbody").html('');
				$("#tableConvenio tbody").html(`<tr><td colspan="${num_col}" style="text-align:center">No existen registros</td></tr>`);
				// alertNotificar("No se encontró información","error");
                // cancelar()
				return;
			}
			
			$("#tableConvenio tbody").html('');
         
			$.each(data.resultado,function(i, item){

                let disabled=""
                if(item.estado=='Inactivo'){
                    disabled='disabled'
                }
             
				$('#tableConvenio').append(`<tr>
                                                <td style="width:8%; text-align:center; vertical-align:middle">
                                                    <button type="button" ${disabled} class="btn btn-danger btn-sm"  onclick="inactivarConvenio('${item.id}')">
                                                        <i class="fa fa-trash"></i>
                                                    </button>
                                                    <button type="button" ${disabled} class="btn btn-success btn-sm"  onclick="detalleConvenio('${item.id}')">
                                                        <i class="fa fa-eye"></i>
                                                    </button>                                               
                                                </td>

                                                <td style="width:15%; text-align:center; vertical-align:middle">
                                                    ${item.fecha_registra}
                                                </td>

                                               
                                                <td style="width:15%; text-align:center; vertical-align:middle">
                                                    ${item.cuota_inicial}                                                            
                                                </td>
                                                <td style="width:5%; text-align:center; vertical-align:middle">
                                                    ${item.numero_cuotas}                                                  
                                                </td>
                                                <td style="width:15%; text-align:center; vertical-align:middle">
                                                    ${item.f_inicio}                                                     
                                                </td>
                                                <td style="width:15%; text-align:center; vertical-align:middle">
                                                    ${item.f_fin}                                                  
                                                </td>

                                                <td style="width:15%; text-align:center; vertical-align:middle">
                                                    ${item.estado}                                                  
                                                </td>

                                                 <td style="width:15%; text-align:center; vertical-align:middle">
                                                    ${item.estado_pago}                                                  
                                                </td>

                                                <td style="width:15%; text-align:center; vertical-align:middle">
                                                    ${item.valor_adeudado}
                                                </td>

                                                 <td style="width:15%; text-align:center; vertical-align:middle">
                                                    ${item.valor_cancelado}
                                                </td>
                                                
											
										</tr>`);
			})
          
            cargar_estilos_datatable_con('tableConvenio')
            
		}
    
    }).fail(function(){
        vistacargando("")
        alertNotificar("Se produjo un error, por favor intentelo más tarde","error");  
        $("#tableConvenio tbody").html('');
		$("#tableConvenio tbody").html(`<tr><td colspan="${num_col}" style="text-align:center">Se produjo un error, por favor intentelo más tarde</td></tr>`);
    });
}

function detalleConvenio(id){
    $('#tableDetConvenio tbody').html('')
    vistacargando("m","Espere por favor")
    $.get('detalle-convenio/'+id, function(data){
        console.log(data)
    
        vistacargando("")
        if(data.error==true){			
            alertNotificar(data.mensaje,"error");
            return;   
        }

        $.each(data.resultado,function(i, item){

            
			$('#tableDetConvenio').append(`<tr>
                                                
                                                <td style="width:10%; text-align:center; vertical-align:middle">
                                                    ${i+1}
                                                </td>

                                                <td style="width:25%; text-align:center; vertical-align:middle">
                                                    ${item.fecha}
                                                </td>

                                               
                                                <td style="width:20%; text-align:center; vertical-align:middle">
                                                    
                                                    ${item.saldo_abono === null ? '0.00' : item.saldo_abono}                                                          
                                                </td>
                                                <td style="width:20%; text-align:center; vertical-align:middle">
                                                    ${item.valor_cuota}                                                  
                                                </td>
                                                <td style="width:25%; text-align:center; vertical-align:middle">
                                                    ${item.estado}                                                     
                                                </td>
                                             
										</tr>`);
		})

         $('#modalDetalleConvenio').modal('show')
    }).fail(function(){
        vistacargando("")
    
    });
   
}

function inactivarConvenio(id){
    if(confirm('¿Estas seguro que quieres inactivar el convenio?')){
        vistacargando("m","Espere por favor")
        $.get('inactivar-convenio/'+id, function(data){
            console.log(data)
        
            vistacargando("")
            if(data.error==true){			
                alertNotificar(data.mensaje,"error");
                return;   
            }

            alertNotificar(data.mensaje,"success");
            let idcoa_conv=$('#idcoa_conv').val()
            llenar_tabla_cuota(idcoa_conv)
            llenar_tabla_medidas(idcoa_conv)
            llenar_tabla_pagos(idcoa_conv)
            llenar_tabla_notificacion()

        }).fail(function(){
            vistacargando("")
        
        });
    }

}

$("#FormMedidas").submit(function(e){
    e.preventDefault();
    let total_valor_deuda=$('#total_valor_deuda').val()
    let medidas_txt=$('#medidas_txt').val()
    let valor_coa=$('.valor_coa').html()
    let predio=$('#predio_localizacion').html()
    
    if(total_valor_deuda=="" || total_valor_deuda==null){
        alertNotificar("Debe ingresar el total de la deuda","error")
        $('#total_valor_deuda').focus()
        return
    }
   
    if(parseFloat(total_valor_deuda)<parseFloat(valor_coa)){
        alertNotificar("El valor adeudado no debe ser menor a "+valor_coa,"error")
        $('#total_valor_deuda').focus()
        return
    }

    if(medidas_txt=="" || medidas_txt==null){
        alertNotificar("Debe ingresar medidas impuestas","error")
        $('#medidas_txt').focus()
        return
    }

    vistacargando("m","Espere por favor")
    $.ajaxSetup({
        headers: {
            'X-CSRF-TOKEN': $('meta[name="csrf-token"]').attr('content')
        }
    });
    let idcoa_conv=$('#idcoa_conv').val()
   
    let tipo="POST"
    let url_form="guardar-medidas-conv"
    var FrmData = new FormData(this);
    FrmData.append('predio', predio);
    FrmData.append('IdNotificaSele', IdNotificaSele);
   
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
            
            llenar_tabla_medidas(idcoa_conv)
            llenar_tabla_cuota(idcoa_conv)           
            llenar_tabla_pagos(idcoa_conv)

            llenar_tabla_notificacion()
            // $('.txt_conv').val('')
                            
        }, error:function (data) {
            console.log(data)

            vistacargando("");
            alertNotificar('Ocurrió un error','error');
        }
    });
})


function llenar_tabla_medidas(id){
   
    $("#tableMedidas tbody").html('');
    $('#tableMedidas').DataTable().destroy();
	$('#tableMedidas tbody').empty(); 
    var num_col = $("#tableMedidas thead tr th").length; //obtenemos el numero de columnas de la tabla
    vistacargando("m", "Espere por favor")
    $.get('llenar-tabla-medidas/'+id, function(data){
        console.log(data)
       
        vistacargando("")
        if(data.error==true){
			$("#tableMedidas tbody").html('');
			$("#tableMedidas tbody").html(`<tr><td colspan="${num_col}" style="text-align:center>No existen registros</td></tr>`);
			alertNotificar(data.mensaje,"error");
            // cancelar()
			return;   
		}
		if(data.error==false){
			if(data.resultado.length==0){
				$("#tableMedidas tbody").html('');
				$("#tableMedidas tbody").html(`<tr><td colspan="${num_col}" style="text-align:center">No existen registros</td></tr>`);
				// alertNotificar("No se encontró información","error");
                // cancelar()
				return;
			}
			
			$("#tableMedidas tbody").html('');
         
			$.each(data.resultado,function(i, item){

                let disabled=""
                if(item.estado=='Inactivo'){
                    disabled='disabled'
                }
             
				$('#tableMedidas').append(`<tr>
                                                <td style="width:10%; text-align:center; vertical-align:middle">
                                                   <button type="button" ${disabled} class="btn btn-danger btn-sm" onclick="inactivarMedidas('${item.id}')">
                                                        <i class="fa fa-trash"></i>
                                                    </button>   
                                                    <button type="button" ${disabled} class="btn btn-primary btn-sm" onclick="verpdf('${item.documento}')">
                                                        <i class="fa fa-file"></i>
                                                    </button>                                               
                                                </td>

                                                <td style="width:15%; text-align:center; vertical-align:middle">
                                                    ${item.fecha_registra}
                                                </td>

                                                <td style="width:15%; text-align:center; vertical-align:middle">
                                                    ${item.total_deuda}
                                                </td>
                                                <td style="width:50%; text-align:center; vertical-align:middle">
                                                    ${item.medidas}                                                            
                                                </td>
                                                

                                                <td style="width:10%; text-align:center; vertical-align:middle">
                                                    ${item.estado}                                                  
                                                </td>
                                                
											
										</tr>`);
			})
          
            cargar_estilos_datatable_con('tableMedidas')
            
		}
    
    }).fail(function(){
        vistacargando("")
        alertNotificar("Se produjo un error, por favor intentelo más tarde","error");  
        $("#tableMedidas tbody").html('');
		$("#tableMedidas tbody").html(`<tr><td colspan="${num_col}" style="text-align:center">Se produjo un error, por favor intentelo más tarde</td></tr>`);
    });
}

function inactivarMedidas(id){
    if(confirm('¿Estas seguro que quieres inactivar la medida?')){
        vistacargando("m","Espere por favor")
        $.get('inactivar-medidas/'+id, function(data){
            console.log(data)
        
            vistacargando("")
            if(data.error==true){			
                alertNotificar(data.mensaje,"error");
                return;   
            }

            alertNotificar(data.mensaje,"success");
            let idcoa_conv=$('#idcoa_conv').val()
            llenar_tabla_medidas(idcoa_conv)
            llenar_tabla_cuota(idcoa_conv)           
            llenar_tabla_pagos(idcoa_conv)
            llenar_tabla_notificacion()

        }).fail(function(){
            vistacargando("")
        
        });
    }

}

$("#FormCancelado").submit(function(e){
    e.preventDefault();
    let valor_cancelado=$('#valor_cancelado').val()
    let valor_coa=$('.valor_coa').html()
    if(valor_cancelado=="" || valor_cancelado==null){
        alertNotificar("Debe ingresar el valor cancelado","error")
        $('#valor_cancelado').focus()
        return
    }
   
    if(parseFloat(valor_cancelado)<parseFloat(valor_coa)){
        alertNotificar("El valor cancelado no debe ser menor a "+valor_coa,"error")
        $('#valor_cancelado').focus()
        return
    }

    vistacargando("m","Espere por favor")
    $.ajaxSetup({
        headers: {
            'X-CSRF-TOKEN': $('meta[name="csrf-token"]').attr('content')
        }
    });
    let idcoa_conv=$('#idcoa_conv').val()
   
    let tipo="POST"
    let url_form="guardar-pago-conv"
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
            llenar_tabla_pagos(idcoa_conv)
            llenar_tabla_medidas(idcoa_conv)
            llenar_tabla_cuota(idcoa_conv)          

            $('.txt_conv').val('')
            llenar_tabla_notificacion()


                            
        }, error:function (data) {
            console.log(data)

            vistacargando("");
            alertNotificar('Ocurrió un error','error');
        }
    });
})

function llenar_tabla_pagos(id){
   
    $("#tableCancelado tbody").html('');
    $('#tableCancelado').DataTable().destroy();
	$('#tableCancelado tbody').empty(); 
    var num_col = $("#tableCancelado thead tr th").length; //obtenemos el numero de columnas de la tabla
    vistacargando("m", "Espere por favor")
    $.get('llenar-tabla-pago/'+id, function(data){
        console.log(data)
       
        vistacargando("")
        if(data.error==true){
			$("#tableCancelado tbody").html('');
			$("#tableCancelado tbody").html(`<tr><td colspan="${num_col}" style="text-align:center>No existen registros</td></tr>`);
			alertNotificar(data.mensaje,"error");
            // cancelar()
			return;   
		}
		if(data.error==false){
			if(data.resultado.length==0){
				$("#tableCancelado tbody").html('');
				$("#tableCancelado tbody").html(`<tr><td colspan="${num_col}" style="text-align:center">No existen registros</td></tr>`);
				// alertNotificar("No se encontró información","error");
                // cancelar()
				return;
			}
			
			$("#tableCancelado tbody").html('');
         
			$.each(data.resultado,function(i, item){

                let disabled=""
                if(item.estado=='Inactivo'){
                    disabled='disabled'
                }
             
				$('#tableCancelado').append(`<tr>
                                                <td style="width:10%; text-align:center; vertical-align:middle">
                                                   <button type="button" ${disabled} class="btn btn-danger btn-sm" onclick="inactivarPago('${item.id}')">
                                                        <i class="fa fa-trash"></i>
                                                    </button>                                               
                                                </td>

                                                <td style="width:40%; text-align:center; vertical-align:middle">
                                                    ${item.fecha_registra}
                                                </td>

                                                <td style="width:35%; text-align:center; vertical-align:middle">
                                                    ${item.valor_cancelado}
                                                </td>
                                               
                                                <td style="width:15%; text-align:center; vertical-align:middle">
                                                    ${item.estado}                                                  
                                                </td>
                                                
											
										</tr>`);
			})
          
            cargar_estilos_datatable_con('tableCancelado')
            
		}
    
    }).fail(function(){
        vistacargando("")
        alertNotificar("Se produjo un error, por favor intentelo más tarde","error");  
        $("#tableCancelado tbody").html('');
		$("#tableCancelado tbody").html(`<tr><td colspan="${num_col}" style="text-align:center">Se produjo un error, por favor intentelo más tarde</td></tr>`);
    });
}

function inactivarPago(id){
    if(confirm('¿Estas seguro que quieres inactivar la medida?')){
        vistacargando("m","Espere por favor")
        $.get('inactivar-pago/'+id, function(data){
            console.log(data)
        
            vistacargando("")
            if(data.error==true){			
                alertNotificar(data.mensaje,"error");
                return;   
            }

            alertNotificar(data.mensaje,"success");
            let idcoa_conv=$('#idcoa_conv').val()
        
            llenar_tabla_pagos(idcoa_conv)
            llenar_tabla_medidas(idcoa_conv)
            llenar_tabla_cuota(idcoa_conv)         
            llenar_tabla_notificacion()

        }).fail(function(){
            vistacargando("")
        
        });
    }

}


function calcularCuotaInicial(input){
    let valor = $(input).val();
    
    let cuota_inicial=valor * 0.20
    cuota_inicial=cuota_inicial.toFixed(2)
    $('#cuota_inicial').val(cuota_inicial)

    // $('.cal_mensual').val('')
    calculaValorMensual()

}

function calculaValorMensual(){
    // let cant_cuota= Number($(input).val());
    let cant_cuota= Number($('#num_cuotas').val());
    if(cant_cuota==""){return}
    let valor_adeudado = parseFloat($('#valor_adeudado').val()) || 0;
    valor_adeudado = Math.round(valor_adeudado * 100) / 100;
    let cuota_inicial=parseFloat($('#cuota_inicial').val()) || 0;
    cuota_inicial=Math.round(cuota_inicial * 100) / 100;

    
    let restante=Number(valor_adeudado) - Number(cuota_inicial)
    console.log(restante)

    let valor_mensual=restante / cant_cuota;
    valor_mensual=valor_mensual.toFixed(2)

    $('#valor_cuotas').val(valor_mensual)

    let fechaInicio = $('#f_ini').val();

    if(!fechaInicio || !cant_cuota) return;

    let partes = fechaInicio.split('-');
    console.log(partes)

    let fecha = new Date(partes[0], partes[1]-1, partes[2]);
    console.log(fecha.getMonth())
    console.log(fecha.getMonth() + cant_cuota)
    // año, mes-1, día

    fecha.setMonth(fecha.getMonth() + cant_cuota);

    let dia = String(fecha.getDate()).padStart(2,'0');
    let mes = String(fecha.getMonth()+1).padStart(2,'0');
    let anio = fecha.getFullYear();

    console.log('anio' +anio)
    console.log('mes' +mes)
    console.log('dia' +dia)

    let fechaFinal = `${anio}-${mes}-${dia}`;

    $('#f_fin').val(fechaFinal);

}