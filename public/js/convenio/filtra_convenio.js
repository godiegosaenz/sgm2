function llenar_tabla_notificacion(){
    var contrib=$('#contrib').val()
    if(contrib==""){return}
    

    $("#tableCoactiva tbody").html('');
    $('#tableCoactiva').DataTable().destroy();
	$('#tableCoactiva tbody').empty(); 
    var num_col = $("#tableCoactiva thead tr th").length; //obtenemos el numero de columnas de la tabla
    vistacargando("m", "Espere por favor")

    $.ajax({
        url: 'pago-convenios-filtra' ,
        type: 'POST',
        data: { data: contrib },
        headers: {
            'X-CSRF-TOKEN': $('meta[name="csrf-token"]').attr('content')
        },
        success: function(data) {
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
                // Obtener el año actual
                var year = new Date().getFullYear();
                
                $("#tableCoactiva tbody").html('');
            
                $.each(data.resultado,function(i, item){
                
                    let icono='';
                
                    if(item.estado_pago=="Pagado"){
                        icono=`<span class="badge-orden"> 💵 PAGADO </span>`;
                    }else if(item.estado_pago=="Debe"){
                    icono=`<span class="badge-medidas"> <i class="fa fa-credit-card" style="font-size: 16px;"></i> DEBE</span>`;
                    }else if(item.estado_pago=="En pago"){
                        icono=`<span class="badge-cancelado"> <i class="fa fa-archive" style="font-size: 16px;"></i> PAGANDO </span>`;
                    }
                    let nombre=""
                    let num_ident=""
                    let num_proceso=""
                    let es_Urbano_Rural=""
                    let es_voluntario_coact=""
                    if(item.coactiva==null){
                        
                        if(item.notificacion.id_persona!=null){
                            nombre=item.notificacion.ente.apellidos+" "+item.notificacion.ente.nombres
                            num_ident=item.notificacion.ente.ci_ruc
                        }else{
                            nombre=item.notificacion.contribuyente
                            num_ident=item.notificacion.num_ident
                        }

                        if(item.notificacion.predio=="Urbano"){
                            es_Urbano_Rural="Urbano"
                        }else{
                            es_Urbano_Rural="Rural"
                        }
                        es_voluntario_coact="Voluntario"

                    }else{
                        if(item.coactiva.notificacion.id_persona!=null){
                            nombre=item.coactiva.notificacion.ente.apellidos+" "+item.coactiva.notificacion.ente.nombres
                            num_ident=item.coactiva.notificacion.ente.ci_ruc
                        }else{
                            nombre=item.coactiva.notificacion.contribuyente
                            num_ident=item.coactiva.notificacion.num_ident
                        }

                        if(item.coactiva.notificacion.predio=="Urbano"){
                            es_Urbano_Rural="Urbano"
                        }else{
                            es_Urbano_Rural="Rural"
                        }
                        es_voluntario_coact="Proceso"

                        // Agregar ceros a la izquierda y concatenar el año
                        num_proceso = strPad(item.coactiva.num_proceso, 3, '0') + '-' + year
                        
                    }

                    

                    $('#tableCoactiva').append(`<tr>
                                                    <td style="width:3%; text-align:center; vertical-align:middle">
                                                    <button type="button" class="btn btn-success btn-sm" onclick="detalleConvenio('${item.id}','${es_Urbano_Rural}','${es_voluntario_coact}')">
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
                                                    
                                                    <td style="width:6%; text-align:center; vertical-align:middle">
                                                        ${num_proceso}                                                  
                                                    </td>
                                                    
                                                    <td style="width:9%; text-align:center; vertical-align:middle">
                                                        ${item.f_inicio}                                                     
                                                    </td>
                                                    <td style="width:9%; text-align:center; vertical-align:middle">
                                                        ${item.f_fin}                                                     
                                                    </td>
                                                    <td style="width:7%; text-align:center; vertical-align:middle">
                                                        ${item.valor_adeudado === null ? '0.00' : item.valor_adeudado}                                                  
                                                    </td>

                                                   
                                                    <td style="width:10%; text-align:center; vertical-align:middle">
                                                        ${icono}                                              
                                                    </td>

                                                    
                                                    <td style="width:20%; text-align:center; vertical-align:middle">
                                                                                                   
                                                    </td>
                                                
                                                    
                                                
                                            </tr>`);
                })
            
                cargar_estilos_datatable('tableCoactiva')
                
            }
        },
        error: function(jqXHR, textStatus, errorThrown) {
             vistacargando("")
            console.log("Error: " + textStatus);
        }
    });
  
}

function strPad(num, length, padChar) {
    var str = num.toString();
    while (str.length < length) {
        str = padChar + str;
    }
    return str;
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
   
    if(ruta=='null'){
        alertNotificar("No se ha subido ningun documento","warning")
        return
    }
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

                let disabled="disabled"
                let disabled2=""
                if(item.estado=='Inactivo'){
                    disabled='disabled'
                    disabled2='disabled'
                }
                
             
				$('#tableConvenio').append(`<tr>
                                                <td style="width:5%; text-align:center; vertical-align:middle">
                                                   <button type="button" ${disabled} class="btn btn-danger btn-sm" onclick="inactivarConvenio('${item.id}')">
                                                        <i class="fa fa-trash"></i>
                                                    </button>       
                                                    
                                                     <button type="button" ${disabled2} class="btn btn-success btn-sm"  onclick="detalleConvenio('${item.id}')">
                                                        <i class="fa fa-eye"></i>
                                                    </button> 

                                                </td>

                                               <td style="width:15%; text-align:center; vertical-align:middle">
                                                    ${item.fecha_registra}
                                                </td>

                                               
                                                <td style="width:10%; text-align:center; vertical-align:middle">
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
globalThis.IdConvenio=0
globalThis.Urbano_Rural=""
globalThis.Noti_o_Proceso=""
function detalleConvenio(id, predio, not_proceso){
    IdConvenio=id
    Urbano_Rural=predio
    Noti_o_Proceso=not_proceso
    $('#boton_titulos').html('')
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
                                                    
                                                    ${item.cuota_inicial === true ? 'Inicial': i} 
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
        $('.detalle_principal').show()
        $('.listado_titulo').hide()
        volverDetalle()
        $('#convenio_generado').html(data.resultado[0].convenio.usuario_registra)
        $('#fecha_conv_generado').html(data.resultado[0].convenio.fecha_registra)
        $('#convenio_contr').html($('.titulo_modal').html())
        $('#convenio_deuda').html(data.resultado[0].convenio.valor_adeudado)

        verTitulos('N')

        $('#boton_titulos').append(`<i class="fa fa-file-pdf" style="color:blue" onclick="verTitulos('S')"></i>`)

        // <i class="fa fa-file-pdf" style="color:blue" onclick="verTitulos()"></i>


    }).fail(function(){
        vistacargando("")
    
    });
   
}

function inactivarConvenio(id){
    alertNotificar("Usted no puede realizar dicha accion", "warning")
    return
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

                let disabled="disabled"
                // if(item.estado=='Inactivo'){
                //     disabled='disabled'
                // }
             
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
    alertNotificar("Usted no puede realizar dicha accion", "warning")
    return
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

                let disabled="disabled"
                // if(item.estado=='Inactivo'){
                //     disabled='disabled'
                // }
             
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
    alertNotificar("Usted no puede realizar dicha accion", "warning")
    return
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

function verTitulos(mostrar){
    
    if(mostrar=="N"){

        $('#valor_final').html('')
        $('#valor_intereses').html('')

        $("#tableTitulo tbody").html('');
        $('#tableTitulo').DataTable().destroy();
        $('#tableTitulo tbody').empty(); 
    
        vistacargando("m","Espere por favor")
        $.get('ver-titulos-convenio/'+IdConvenio+'/'+Urbano_Rural+'/'+Noti_o_Proceso, function(data){
            console.log(data)
        
            vistacargando("")
            if(data.error==true){			
                alertNotificar(data.mensaje,"error");
                return;   
            }

            // alertNotificar(data.mensaje,"success");
            $("#tableTitulo tbody").html('');
            let total_final=0
            let total_cancelado=0
            $.each(data.resultado,function(i, item){
                let num_titulo=""
                let subtotal=""
                let int=""
                let recargos=""
                let descuento=""
                let total_a_pagar=""
                let estado=""
                if(Urbano_Rural=="Urbano"){
                    num_titulo=item[0].id_liquidacion
                    subtotal=item[0].total_pago
                    int=item[0].interes
                    recargos=item[0].recargos
                    descuento=item[0].desc
                    total_a_pagar=item[0].total_complemento
                    estado=item[0].estado_liquidacion
                    if(estado==1){
                        total_cancelado=parseFloat(total_cancelado) + parseFloat(total_a_pagar);
                    }
                }else{
                    num_titulo=item[0].CarVe_NumTitulo
                    subtotal=item[0].CarVe_ValorEmitido
                    int=item[0].intereses
                    recargos=item[0].recargo
                    descuento="0.00"
                    total_a_pagar=item[0].total_pagar
                    estado=item[0].CarVe_Estado
                    if(estado=="C"){
                        estado=1
                        total_cancelado=parseFloat(total_cancelado) + parseFloat(total_a_pagar);
                    }else{
                        estado=0
                    }
                }
                total_final=parseFloat(total_final) + parseFloat(total_a_pagar);
                $('#tableTitulo').append(`<tr>
                                                <td style="width:15%; text-align:center; vertical-align:middle">
                                                    ${num_titulo}                                              
                                                </td>

                                                <td style="width:10%; text-align:center; vertical-align:middle">
                                                    ${subtotal}
                                                </td>

                                                <td style="width:10%; text-align:center; vertical-align:middle">
                                                    ${int}
                                                </td>
                                                
                                                <td style="width:10%; text-align:center; vertical-align:middle">
                                                    0.00                                               
                                                </td>

                                                <td style="width:10%; text-align:center; vertical-align:middle">
                                                    ${recargos == null ? '0.00' : recargos}
                                                </td>

                                                <td style="width:10%; text-align:center; vertical-align:middle">
                                                    ${descuento}
                                                </td>
                                                
                                                <td style="width:10%; text-align:center; vertical-align:middle">
                                                    ${total_a_pagar}                                               
                                                </td>

                                                <td style="width:10%; text-align:center; vertical-align:middle">
                                                    ${estado == 1 ? 'Pagado' : 'Por pagar'}                                            
                                                </td>
                                                
                                            
                                        </tr>`);
            })
            
            cargar_estilos_datatable_con('tableTitulo')
            $('#valor_final').html(total_final.toFixed(2))

            let valor_int= parseFloat(total_final) - parseFloat($('#convenio_deuda').html()) 
            $('#valor_intereses').html(valor_int.toFixed(2))

            $('#valor_cancelado').html(total_cancelado.toFixed(2))

            let valor_pendiente= parseFloat(total_final) - parseFloat(total_cancelado) 
            $('#valor_pendiente').html(valor_pendiente.toFixed(2))
            
        }).fail(function(){
            vistacargando("")
            $("#tableTitulo tbody").html('');
            $("#tableTitulo tbody").html(`<tr><td colspan="${num_col}" style="text-align:center">Se produjo un error, por favor intentelo más tarde</td></tr>`);
        
        });
    }else{
        $('.detalle_principal').hide()
        $('.listado_titulo').show()
        $('.salir').hide()
        $('.detalle_btn').show()
    }
}

function volverDetalle(){
    $('.detalle_btn').hide()
    $('.salir').show()

    $('.detalle_principal').show()
    $('.listado_titulo').hide()
}
