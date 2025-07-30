function cambiaTipo(){
    limpiarBusqueda()
    var tipo=$('#tipo').val()
    $('#div_matricula').show()
    $('#div_cedula').hide()
    $('#div_clave').hide()
    $('#div_nombres').hide()
    if(tipo==""){return}
    else if(tipo=="0"){
        $('#div_matricula').show()
        $('#div_cedula').hide()
        $('#div_nombres').hide()
        $('#div_clave').hide()
    }
    else if(tipo=="1"){
        $('#div_cedula').show()
        $('#div_matricula').hide()
        $('#div_clave').hide()
        $('#div_nombres').hide()
    }else if(tipo=="2"){
        $('#div_matricula').hide()
        $('#div_cedula').hide()
        $('#div_nombres').hide()
        $('#div_clave').show()
    }else{
        $('#div_matricula').hide()
        $('#div_cedula').hide()
        $('#div_nombres').show()
        $('#div_clave').hide()
    }
}

function limpiarBusqueda(){
    $('#cedula').val('')
    $('#matricula').val('')
    $('#clave').val('')
    $('#cmb_nombres').val('')
}


$('#formConsulta').on('submit', function(event) {
    let tipo = $('#tipo').val();
    let matricula = $('#matricula').val();
    let cedula = ""
    let clave =""
    // let cedula = $('#cedula').val().trim();
    // let clave = $('#clave').val().trim();
    let nombres = $('#cmb_nombres').val();
    let valido = true;

    // Oculta mensajes de error previos si los hay
    $('.is-invalid').removeClass('is-invalid');

    if (tipo == '0') {
        if (matricula === '') {
            $('#matricula').addClass('is-invalid');
            alertNotificar('Ingrese la matricula','error')
            valido = false;
        }
    }else if (tipo == '1') {
        if (cedula === '') {
            $('#cedula').addClass('is-invalid');
            alertNotificar('Ingrese la cedula','error')
            valido = false;
        }
    } else if (tipo == '2') {
        if (clave === '') {
            $('#clave').addClass('is-invalid');
            alertNotificar('Ingrese la clave','error')
            valido = false;
        }
    }else if (tipo == '3') {
        if (nombres === '') {
            $('#cmb_nombres').addClass('is-invalid');
            alertNotificar('Seleccione el nombre','error')
            return
            
        }
        valor=nombres
    }

    if (!valido) {
        event.preventDefault(); // 🚫 Detiene el envío si hay error
    }
});

function llenarTablaAux(){

    let tipo = $('#tipo').val();
    let matricula = $('#matricula').val();
    let cedula=""
    let clave=""
    // let cedula = $('#cedula').val().trim();
    // let clave = $('#clave').val().trim();
    let nombres = $('#cmb_nombres').val();
    let valor=""
    if (tipo == '0') {
        if (matricula === '') {
            $('#matricula').addClass('is-invalid');
            alertNotificar('Ingrese la matricula','error')
            valido = false;
        }
        valor=matricula
    }else if (tipo == '1') {
        if (cedula === '') {
            $('#cedula').addClass('is-invalid');
            alertNotificar('Ingrese la cedula','error')
            return
            
        }
        valor=cedula

    } else if (tipo == '2') {
        if (clave === '') {
            $('#clave').addClass('is-invalid');
            alertNotificar('Ingrese la clave','error')
            return
            
        }
        valor=clave
    }else if (tipo == '3') {
        if (nombres === '') {
            $('#cmb_nombres').addClass('is-invalid');
            alertNotificar('Seleccione el nombre','error')
            return
            
        }
        valor=nombres
    }
    $('.botones').prop('disabled',true)
    $("#tableUrbana tbody").html('');
    $('#tableUrbana tbody').empty(); 
    var num_col = $("#tableUrbana thead tr th").length; //obtenemos el numero de columnas de la tabla
    vistacargando("m", "Espere por favor")
    $.get('buscar-titulo-urbano/'+tipo+'/'+valor, function(data){
        console.log(data)
       
        vistacargando("")
        if(data.error==true){
            
			$("#tableUrbana tbody").html('');
			$("#tableUrbana tbody").html(`<tr><td colspan="${num_col}" style="text-align:center>No existen registros</td></tr>`);
			alertNotificar(data.mensaje,"error");
            // cancelar()
			return;   
		}
		if(data.error==false){
			if(data.resultado.length==0){
				$("#tableUrbana tbody").html('');
				$("#tableUrbana tbody").html(`<tr><td colspan="${num_col}" style="text-align:center">No existen registros</td></tr>`);
				alertNotificar("No se encontró información","error");
                // cancelar()
				return;
			}
			
			$("#tableUrbana tbody").html('');
         
			$.each(data.resultado,function(i, item){
               
                let icono=`<i class="bi bi-circle-fill" style="color:red;"></i>`;
                if(item.CarVe_Estado=='C'){
                    icono=`<i class="bi bi-circle-fill" style="color:green;"></i>`;
                }
                let nombre=item.nombre_comprador
                if(item.nombres!=null){
                    nombre=item.nombres+" "+item.apellidos
                }
				$('#tableUrbana').append(`<tr>
                                                <td style="width:5%; text-align:center; vertical-align:middle">
                                                    ${icono} 
                                                    
                                                </td>

                                                <td style="width:6%;  text-align:left; vertical-align:middle">
                                                    <input class="form-check-input" type="checkbox" value=${item.id} name="checkLiquidacion[]" data-liquidacion="${item.id_liquidacion}" data-id="${item.id}">
                                                </td>
                                               
                                                <td style="width:8%; text-align:center; vertical-align:middle">
                                                    ${item.anio}
                                                </td>

                                                <td style="width:14%; text-align:center; vertical-align:middle">
                                                    ${item.id_liquidacion}
                                                </td>

                                                <td style="width:10%; text-align:center; vertical-align:middle">
                                                    ${item.clave_cat}
                                                </td>

                                                 <td style="width:40%; text-align:left; vertical-align:middle">
                                                    ${nombre}
                                                </td>

                                                <td style="width:10%; text-align:right; vertical-align:middle">
                                                    ${item.total_pago}
                                                </td>

                                                 <td style="width:10%; text-align:right; vertical-align:middle">
                                                    ${item.interes}
                                                </td>

                                                 <td style="width:10%; text-align:right; vertical-align:middle">
                                                    ${item.recargos}
                                                </td>


                                                 <td style="width:10%; text-align:right; vertical-align:middle">
                                                    ${item.total_complemento}
                                                </td>
                                                
                                                
											
										</tr>`);
			})
          

            $('.botones').prop('disabled',false)
		}
    
    }).fail(function(){
        vistacargando("")
        alertNotificar("Se produjo un error, por favor intentelo más tarde","error");  
        $("#tableUrbana tbody").html('');
		$("#tableUrbana tbody").html(`<tr><td colspan="${num_col}" style="text-align:center">Se produjo un error, por favor intentelo más tarde</td></tr>`);
    });
}

function llenarTabla(){

    let tipo = $('#tipo').val();
    let matricula = $('#matricula').val();
    // let cedula = $('#cedula').val().trim();
    // let clave = $('#clave').val().trim();
    let cedula=""
    let clave=""
    let nombres = $('#cmb_nombres').val();
    let valor=""
    if (tipo == '0') {
        if (matricula === '') {
            $('#matricula').addClass('is-invalid');
            alertNotificar('Ingrese la matricula','error')
            valido = false;
        }
        valor=matricula
    }else if (tipo == '1') {
        if (cedula === '') {
            $('#cedula').addClass('is-invalid');
            alertNotificar('Ingrese la cedula','error')
            return
            
        }
        valor=cedula

    } else if (tipo == '2') {
        if (clave === '') {
            $('#clave').addClass('is-invalid');
            alertNotificar('Ingrese la clave','error')
            return
            
        }
        valor=clave
    }else if (tipo == '3') {
        if (nombres === '') {
            $('#cmb_nombres').addClass('is-invalid');
            alertNotificar('Seleccione el nombre','error')
            return
            
        }
        valor=nombres
    }
    $('.botones').prop('disabled',true)
    $("#tableUrbana tbody").html('');
    $('#tableUrbana tbody').empty(); 
    var num_col = $("#tableUrbana thead tr th").length; //obtenemos el numero de columnas de la tabla
    vistacargando("m", "Espere por favor")
    $.get('buscar-notificados/'+tipo+'/'+valor, function(data){
        console.log(data)
       
        vistacargando("")
        if(data.error==true){
            
			$("#tableUrbana tbody").html('');
			$("#tableUrbana tbody").html(`<tr><td colspan="${num_col}" style="text-align:center>No existen registros</td></tr>`);
			alertNotificar(data.mensaje,"error");
            // cancelar()
			return;   
		}
		if(data.error==false){
			if(data.resultado.length==0){
				$("#tableUrbana tbody").html('');
				$("#tableUrbana tbody").html(`<tr><td colspan="${num_col}" style="text-align:center">No existen registros</td></tr>`);
				// alertNotificar("No se encontró información","error");
                // cancelar()
				return;
			}
			
			$("#tableUrbana tbody").html('');
         
			$.each(data.resultado,function(i, item){
               
                let icono=`<i class="bi bi-circle-fill" style="color:orange;"></i>`;
              
                let nombre=item.nombre_comprador
                if(item.nombres!=null){
                    nombre=item.nombres+" "+item.apellidos
                }
                let titulo=[]
               let titulo_ = `<ul>`;
                $.each(item.titulos, function(i2, item2) {
                    console.log(item2)
                    titulo_ += `<li>${item2}</li>`;
                });
                titulo_ += `</ul>`;

				$('#tableUrbana').append(`<tr>
                                                <td style="width:3%; text-align:center; vertical-align:middle">
                                                    ${icono} 
                                                    
                                                </td>

                                                <td style="width:5%;  text-align:left; vertical-align:middle">
                                                    <input class="form-check-input" type="checkbox" value=${item.id} name="checkLiquidacion[]" >
                                                </td>
                                               
                                                <td style="width:13%; text-align:left; vertical-align:middle">
                                                    ${titulo_}
                                                </td>

                                                <td style="width:36%; text-align:left; vertical-align:middle">
                                                    <b>C.I.:</b> ${item.ci_ruc}<br>
                                                    <b>Contribuyente:</b> ${nombre}
                                                </td>

                                                <td style="width:20%; text-align:left; vertical-align:middle">
                                                   ${item.observacion_notificacion}
                                                </td>

                                                 <td style="width:25%; text-align:left; vertical-align:middle">
                                                    <b>Fecha:</b> ${item.fecha_registro_notif}<br>
                                                    <b>Usuario:</b> ${item.nombre_usuario}
                                                </td>

                                               
										</tr>`);
			})
          

            $('.botones').prop('disabled',false)
           
            $('#fecha_notifica').html(data.resultado[0].fecha_registro_notif)
		}
    
    }).fail(function(){
        vistacargando("")
        alertNotificar("Se produjo un error, por favor intentelo más tarde","error");  
        $("#tableUrbana tbody").html('');
		$("#tableUrbana tbody").html(`<tr><td colspan="${num_col}" style="text-align:center">Se produjo un error, por favor intentelo más tarde</td></tr>`);
    });
}



function generarTitulos(){
    if ($('input[name="checkLiquidacion[]"]:checked').length === 0) {
        alert("Debe seleccionar al menos una liquidación.");
        return false; // Previene la acción, si estás dentro de un submit o evento
    }

    vistacargando("m","Espere por favor")
    $.ajaxSetup({
        headers: {
            'X-CSRF-TOKEN': $('meta[name="csrf-token"]').attr('content')
        }
    });

     var FrmData=$("#formExonerar").serialize();

    $.ajax({
            
        type: 'POST',
        url: 'tituloscoactivarural/imprimir',
        method: 'POST',             
		data: FrmData,      
		
        processData:false, 

        success: function(data){
            vistacargando("");                
            if(data.error==true){
                alertNotificar(data.mensaje,'error');
                return;                      
            }
           
            alertNotificar("El documento se descargara en unos segundos...","success");
            window.location.href="descargar-reporte/"+data.pdf
                            
        }, error:function (data) {
            console.log(data)

            vistacargando("");
            alertNotificar('Ocurrió un error','error');
        }
    });
}
$('#cmb_nombres').select2({
    // dropdownParent: $('#actividadLocal'),
    ajax: {
        url: 'buscarContribuyenteUrbano',
        dataType: 'json',
        delay: 250,
        data: function (params) {
            return {
                q: params.term
            };
        },
        processResults: function (data) {
            console.log(data)
            return {
                results: data.map(item => ({
                    id: item.id,
                    text: item.ci_ruc + " - " + item.nombre
                }))
            };
        }
    },
    minimumInputLength: 1
});

 $(document).on('change', 'input[name="checkLiquidacion[]"]', function () {
    // Recolectar todos los seleccionados
    const ids = [];
    const liquidaciones = [];

    $('input[name="checkLiquidacion[]"]:checked').each(function () {
        ids.push($(this).data('id'));
        liquidaciones.push($(this).data('liquidacion'));
    });

    // Mostrar en los textareas
    $('#inputId').val(ids.join(', '));
    $('#titulos_selecc').val(liquidaciones.join(', '));
});

function coactivarTitulos(){

    if ($('input[name="checkLiquidacion[]"]:checked').length === 0) {
        alert("Debe seleccionar al menos una liquidación.");
        return false; // Previene la acción, si estás dentro de un submit o evento
    }



  
    $('.txt_coact').val('')
    $('#modalContri').modal('show')
}

function verDetalle() {
    var id_selecc= $('input[name="checkLiquidacion[]"]:checked').val()
    $("#tablaDetalleDeudas tbody").html('');
    $('#tablaDetalleDeudas tbody').empty(); 
    var num_col = $("#tablaDetalleDeudas thead tr th").length; //obtenemos el numero de columnas de la tabla
    vistacargando("m", "Espere por favor")
    $.get('ver-detalle-deudas/'+id_selecc, function(data){
        console.log(data)
       
        vistacargando("")
        if(data.error==true){
			$("#tablaDetalleDeudas tbody").html('');
			$("#tablaDetalleDeudas tbody").html(`<tr><td colspan="${num_col}" style="text-align:center>No existen registros</td></tr>`);
			alertNotificar(data.mensaje,"error");
            // cancelar()
			return;   
		}
		if(data.error==false){
			if(data.resultado.length==0){
				$("#tablaDetalleDeudas tbody").html('');
				$("#tablaDetalleDeudas tbody").html(`<tr><td colspan="${num_col}" style="text-align:center">No existen registros</td></tr>`);
				alertNotificar("No se encontró información","error");
                // cancelar()
				return;
			}
			$('#modalDetalle').modal('show')

			$("#tablaDetalleDeudas tbody").html('');
            
            let total=0
			$.each(data.resultado,function(i, item){
                let porc=0
                porc=0.05*Number(item.total_complemento)

                let total_parc=0
                total_parc=Number(item.total_complemento)+Number(porc)

                total=total + Number(item.total_complemento) + Number(porc)
              
				$('#tablaDetalleDeudas').append(`<tr>
                                               
                                                <td style="width:20%;  text-align:left; vertical-align:middle">
                                                    ${item.clave_cat}
                                                </td>
                                                <td style="width:10%;  text-align:left; vertical-align:middle">
                                                    ${item.anio}
                                                </td>
                                               
                                                <td style="width:10%; text-align:right; vertical-align:middle">
                                                    ${item.saldo}
                                                </td>

                                                <td style="width:10%; text-align:right; vertical-align:middle">
                                                    ${item.desc}
                                                </td>

                                                 <td style="width:10%;  text-align:right; vertical-align:middle">
                                                    ${item.recargos}
                                                </td>
                                               
                                                <td style="width:10%; text-align:right; vertical-align:middle">
                                                    ${item.interes}
                                                </td>

                                                <td style="width:15%; text-align:right; vertical-align:middle">
                                                    ${item.total_complemento}
                                                </td>

                                                <td style="width:15%; text-align:right; vertical-align:middle">
                                                    ${porc.toFixed(2)}
                                                </td>

                                                <td style="width:15%; text-align:right; vertical-align:middle">
                                                    ${total_parc.toFixed(2)}
                                                </td>


                                               
                                                
											
										</tr>`);
			})

             var nombre_rl=$('#cmb_nombres option:selected').text();
            nombre_rl=nombre_rl.split("-")
            // $('#representante').val(nombre_rl[1])


            // let ci=$('#cmb_nombres').text()
            $('#ci_ruc_deudor').html(data.resultado[0].ci_ruc)
            $('#nombre_deudor').html(data.resultado[0].nombres)
            // $('#fecha_notifica').html(tipo)
            $('#total_deudor').html(total)
            $('#id_coact').val(id_selecc)
          
            
		}
    
    }).fail(function(){
        vistacargando("")
        alertNotificar("Se produjo un error, por favor intentelo más tarde","error");  
        $("#tablaDetalleDeudas tbody").html('');
		$("#tablaDetalleDeudas tbody").html(`<tr><td colspan="${num_col}" style="text-align:center">Se produjo un error, por favor intentelo más tarde</td></tr>`);
    })
    
}

$("#formCoactivarEmi").submit(function(e){
    e.preventDefault();
    
       
    if(observacion=="" || observacion==null){
        alertNotificar("Debe ingresar la observacion","error")
        $('#observacion').focus()
        return
    } 

    vistacargando("m","Espere por favor")
    $.ajaxSetup({
        headers: {
            'X-CSRF-TOKEN': $('meta[name="csrf-token"]').attr('content')
        }
    });

    //comprobamos si es registro o edicion
    let tipo=""
    let url_form=""
    if(FormaAccionMarca=="R"){
        tipo="POST"
        url_form="guardar-coactiva-emi"
    }else{
        tipo="PUT"
        url_form="actualizar-coactiva/"+IdEditarMarca
    }
  
    // var FrmData=$("#formCoactivarEmi").serialize();
    var FrmData = new FormData(this);
    //FrmData.append('id_ren_liquidacion',arrayInt);

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
            llenarTabla()
            $('#modalDetalle').modal('hide')
                            
        }, error:function (data) {
            console.log(data)

            vistacargando("");
            alertNotificar('Ocurrió un error','error');
        }
    });
})



globalThis.FormaAccionMarca='R'
$("#formCoactivar").submit(function(e){
    e.preventDefault();
    
       
    if(observacion=="" || observacion==null){
        alertNotificar("Debe ingresar la observacion","error")
        $('#observacion').focus()
        return
    } 

    vistacargando("m","Espere por favor")
    $.ajaxSetup({
        headers: {
            'X-CSRF-TOKEN': $('meta[name="csrf-token"]').attr('content')
        }
    });

    //comprobamos si es registro o edicion
    let tipo=""
    let url_form=""
    if(FormaAccionMarca=="R"){
        tipo="POST"
        url_form="guardar-coactiva"
    }else{
        tipo="PUT"
        url_form="actualizar-coactiva/"+IdEditarMarca
    }
  
    // var FrmData=$("#formCoactivar").serialize();
    var FrmData = new FormData(this);
    //FrmData.append('id_ren_liquidacion',arrayInt);

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
            llenarTabla()
            $('#modalContri').modal('hide')
                            
        }, error:function (data) {
            console.log(data)

            vistacargando("");
            alertNotificar('Ocurrió un error','error');
        }
    });
})


