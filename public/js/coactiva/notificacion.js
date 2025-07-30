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
    let cedula = $('#cedula').val().trim();
    let clave = $('#clave').val().trim();
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

function llenarTabla(){

    let tipo = $('#tipo').val();
    let matricula = $('#matricula').val();
    let cedula = $('#cedula').val().trim();
    let clave = $('#clave').val().trim();
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
            const texto_aux=data.correo
            const texto = texto_aux.join(',');

            // Poner el texto en el input
            // document.getElementById('miInput').value = texto;
            $('#email_selecc').val(texto)
            $('#contr_selecc').val(data.resultado[0].nombres +" "+data.resultado[0].apellidos)
            $('#idcontr_selecc').val(data.resultado[0].id_cont)


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


globalThis.FormaAccionMarca='R'
$("#formNotificar").submit(function(e){
    e.preventDefault();
    
    let observacion=$('#observacion').val()  
    let archivo=$('#archivo').val()  
    if(observacion=="" || observacion==null){
        alertNotificar("Debe ingresar la observacion","error")
        $('#observacion').focus()
        return
    } 

    if(archivo=="" || archivo==null){
        alertNotificar("Debe subir la documentacion","error")
        $('#archivo').focus()
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
        url_form="guardar-notificacion"
    }else{
        tipo="PUT"
        url_form="actualizar-coactiva/"+IdEditarMarca
    }
  
    // var FrmData=$("#formNotificar").serialize();
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


