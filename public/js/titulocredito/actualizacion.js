function cambiaTipo(){
    limpiaTabla()
    limpiarBusqueda()
    var tipo=$('#tipo').val()
    // $('#div_cedula').show()
    $('#div_clave').hide()
    $('#div_nombres').hide()
    if(tipo==""){return}
    else if(tipo=="1"){
        $('#div_cedula').show()
        $('#div_clave').hide()
        $('#div_nombres').hide()
    }else if(tipo=="2"){
        $('#div_cedula').hide()
        $('#div_nombres').hide()
        $('#div_clave').show()
    }else{
        $('#div_cedula').hide()
        $('#div_nombres').show()
        $('#div_clave').hide()
    }
}
function limpiaTabla(){
    $("#tbodyRural").html('');
	$("#tbodyRural").append(`<tr><td colspan="6" style="text-align:center">No existen registros</td></tr>`);
}
function limpiarBusqueda(){
    $('#cedula').val('')
    $('#clave').val('')
    $('#cmb_nombres').val('')
}

function cambiaLugar(){
    limpiaTabla()
    var lugar=$('#lugar').val()
    if(lugar==1){
        $("#tipo").val(1)
        $('.class_urb').show()
        $('.class_rural').hide()

        $('#div_cedula').hide()
        $('#div_clave').hide()
        $('#div_nombres').hide()
    }else{
        $("#tipo_urb").val(1)
        $('.class_urb').hide()
        $('.class_rural').show()

        $('#div_matricula_urb').hide()
        $('#div_clave_urb').hide()
        $('#div_nombres_urb').hide()
    }
}

function limpiarBusquedaU(){
    $('#matricula_urb').val('')
    $('#clave_urb').val('')
    $('#nombre_urb').val('')

    
}

function cambiaTipoU(){
    limpiaTabla()
    limpiarBusquedaU()
    var tipo=$('#tipo_urb').val()
    $('#div_matricula_urb').show()
    $('#div_clave_urb').hide()
    $('#div_nombres_urb').hide()
    if(tipo==""){return}
    else if(tipo=="1"){
        $('#div_matricula_urb').show()
        $('#div_clave_urb').hide()
        $('#div_nombres_urb').hide()
    }else if(tipo=="2"){
        $('#div_matricula_urb').hide()
        $('#div_nombres_urb').hide()
        $('#div_clave_urb').show()
    }else{
        $('#div_matricula_urb').hide()
        $('#div_nombres_urb').show()
        $('#div_clave_urb').hide()
    }
}


$('#formConsulta').on('submit', function(event) {
    let tipo = $('#tipo').val();
    let cedula = $('#cedula').val().trim();
    let clave = $('#clave').val().trim();
    let nombres = $('#cmb_nombres').val();
    let valido = true;

    // Oculta mensajes de error previos si los hay
    $('.is-invalid').removeClass('is-invalid');

    if (tipo == '1') {
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
    let cedula = $('#cedula').val().trim();
    let clave = $('#clave').val().trim();
    let nombres = $('#cmb_nombres').val();
    let valor=""
    if (tipo == '1') {
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

    $("#tableRural tbody").html('');
    $('#tableRural tbody').empty(); 
    var num_col = $("#tableRural thead tr th").length; //obtenemos el numero de columnas de la tabla
    vistacargando("m", "Espere por favor")
    $.get('buscar-titulo-rural/'+tipo+'/'+valor, function(data){
        console.log(data)
       
        vistacargando("")
        if(data.error==true){
			$("#tableRural tbody").html('');
			$("#tableRural tbody").html(`<tr><td colspan="${num_col}" style="text-align:center>No existen registros</td></tr>`);
			alertNotificar(data.mensaje,"error");
            // cancelar()
			return;   
		}
		if(data.error==false){
			if(data.resultado.length==0){
				$("#tableRural tbody").html('');
				$("#tableRural tbody").html(`<tr><td colspan="${num_col}" style="text-align:center">No existen registros</td></tr>`);
				alertNotificar("No se encontró información","error");
                // cancelar()
				return;
			}
			
			$("#tableRural tbody").html('');
         
			$.each(data.resultado,function(i, item){
                let anio=item.CarVe_NumTitulo               
                anio=anio.split("-");
                let icono=`<i class="bi bi-circle-fill" style="color:red;"></i>`;
                if(item.CarVe_Estado=='C'){
                    icono=`<i class="bi bi-circle-fill" style="color:green;"></i>`;
                }
                let nombre=item.CarVe_Nombres
                if(i.Ciu_Nombres!=""){
                    nombre=item.Ciu_Nombres+" "+item.Ciu_Apellidos
                }
				$('#tableRural').append(`<tr>
                                                <td style="width:5%; text-align:center; vertical-align:middle">
                                                    ${icono} 
                                                    
                                                </td>

                                                <td style="width:10%;  text-align:left; vertical-align:middle">
                                                    <input class="form-check-input" type="checkbox" value=${item.CarVe_NumTitulo} name="checkLiquidacion[]">
                                                </td>
                                               
                                                <td style="width:10%; text-align:center; vertical-align:middle">
                                                    ${anio[0]}
                                                </td>

                                                <td style="width:10%; text-align:center; vertical-align:middle">
                                                    ${item.CarVe_NumTitulo}
                                                </td>

                                                <td style="width:10%; text-align:center; vertical-align:middle">
                                                    ${item.Pre_CodigoCatastral}
                                                </td>

                                                 <td style="width:40%; text-align:left; vertical-align:middle">
                                                    ${nombre}
                                                </td>

                                                <td style="width:10%; text-align:right; vertical-align:middle">
                                                    ${item.total_pagar}
                                                </td>
                                                
											
										</tr>`);
			})
          

            
		}
    
    }).fail(function(){
        vistacargando("")
        alertNotificar("Se produjo un error, por favor intentelo más tarde","error");  
        $("#tableRural tbody").html('');
		$("#tableRural tbody").html(`<tr><td colspan="${num_col}" style="text-align:center">Se produjo un error, por favor intentelo más tarde</td></tr>`);
    });
}

function generarTitulos(){
    if ($('input[name="checkLiquidacion[]"]:checked').length === 0) {
        alertNotificar("Debe seleccionar al menos una liquidación.","error");
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
        url: 'buscarContribuyenteRural',
        dataType: 'json',
        delay: 250,
        data: function (params) {
            return {
                q: params.term
            };
        },
        processResults: function (data) {
            return {
                results: data.map(item => ({
                    id: item.Pre_CodigoCatastral,
                    text: item.CarVe_CI + " - " + item.CarVe_Nombres
                }))
            };
        }
    },
    minimumInputLength: 1
});

$(".buscarCont").on("keypress", function(e){
    if(e.which === 13){
        buscaContribuyente()
    }
});


function buscaContribuyente(){

    $("#tbodyRural").html('');
    $('#tbodyRural').empty();     
    var valor=""
    var lugar=$('#lugar').val()
    var tipo_=$('#tipo').val()
    
    if(lugar==""){
        alertNotificar("Debe seleccionar el tipo persona","error")
        return
    }
    if(lugar==2){
        if(tipo_==""){
            alertNotificar("Debe seleccionar el tipo","error")
            return
        }

        if(tipo_==1){
            valor=$('#cedula').val()
            if(valor==""){
                alertNotificar("Debe ingresar el numero de cedula o RUC","error")
                return
            }
        }

        if(tipo_==2){
            valor=$('#clave').val()
            if(valor==""){
                alertNotificar("Debe ingresar la clave catastral","error")
                return
            }
        }

        if(tipo_==3){
            valor=$('#nombre').val()
            if(valor==""){
                alertNotificar("Debe ingresar y seleccionar el nombre","error")
                return
            }
        }
    }else{
        var tipo_=$('#tipo_urb').val()
       
        if(tipo_==""){
            alertNotificar("Debe seleccionar el tipo","error")
            return
        }

        if(tipo_==1){
            valor=$('#matricula_urb').val()
            if(valor==""){
                alertNotificar("Debe ingresar el numero de matricula","error")
                return
            }
        }

        if(tipo_==2){
            valor=$('#clave_urb').val()
            if(valor==""){
                alertNotificar("Debe ingresar la clave catastral","error")
                return
            }
        }

        if(tipo_==3){
            valor=$('#nombre_urb').val()
            if(valor==""){
                alertNotificar("Debe ingresar y seleccionar el nombre","error")
                return
            }
        }
    }

    vistacargando("m","Espere por favor")
    $.ajaxSetup({
        headers: {
            'X-CSRF-TOKEN': $('meta[name="csrf-token"]').attr('content')
        }
    });

  
    url_form="buscar-deudas-contribuyente"
    if(lugar==1){
        url_form="buscar-deudas-contribuyente-urb"
    }

    let FrmData = {
        lugar:lugar,
        tipo: tipo_,
        valor:valor
    };

    console.log(FrmData)

    $.ajax({
            
        type: 'POST',
        url: url_form,
        method: 'POST',  
        data: JSON.stringify(FrmData),      
        processData: false,  
        contentType: 'application/json',  
        processData:false, 

        success: function(data){
            vistacargando("");                
            if(data.error==true){
                $('#tbodyRural').append(`<tr>
                <td colspan="6" style="text-align:center">No hay datos disponibles</td>`);
                alertNotificar(data.mensaje,'error');
                return;                      
            }
            console.log(data)
            if(data.data.length==0){
                $('#tbodyRural').append(`<tr>
                <td colspan="6" style="text-align:center">No hay datos disponibles</td>`);
                // alertNotificar('No se encontro informacion','error');
                return; 
            }
        
            llenarData(data.data)
                            
        }, error:function (data) {
            console.log(data)

            vistacargando("");
            alertNotificar('Ocurrió un error','error');
            $('#tbodyRural').append(`<tr>
                <td colspan="6" style="text-align:center">No hay datos disponibles</td>`);
        }
    });
   
}
globalThis.CorreosContr=[""]
function llenarData(data){
    
    $.each(data,function(i, item){
        let cedula=item.Titpr_RUC_CI
        if(item.Titpr_RUC_CI<=0){
            cedula=item.ruc
        }  
        
        let telefono=""
        if (typeof item.telf !== 'undefined') {
            // La variable está definida
            telefono=item.telf
        }

        let correos=""
        if (typeof item.email !== 'undefined') {
            // La variable está definida
            correos=item.email
        }
        
        $('#tbodyRural').append(`<tr>
                <td style="width:5%; text-align:center; vertical-align:middle">
                    <button type="button" class="btn btn-success btn-sm" onclick="buscarTitulos('${cedula}','${correos}','${item.direccion}','${telefono}')">
                        <i class="fa fa-money"></i>
                    </button>                    
                </td>
                <td style="width:10%; text-align:center; vertical-align:middle">
                    ${cedula}                    
                </td>
                <td style="width:30%; text-align:center; vertical-align:middle">
                    ${item.nombres}                      
                </td>
                <td style="width:10%; text-align:center; vertical-align:middle">
                    ${telefono}            
                </td>
                <td style="width:10%; text-align:center; vertical-align:middle">
                    ${correos}              
                </td>
                <td style="width:20%; text-align:center; vertical-align:middle">
                     ${item.direccion}                     
                </td>
            
        </tr>`);
    })
}

globalThis.AplicaRemiGlobal=0
globalThis.CedulaGlobal=""
function buscarTitulos(cedula, correos, direccion, telefono){
    CedulaGlobal=cedula
    $('#total_deuda').html('')
    $('#total_seleccionado').html('')
    $('#exon_contr').html('')

    $('#correos_notifica').val('')
    $('#archivo_notifica').val('')
    $('#nombre_contribuyente').val('')
    $('#ci_ruc_contribuyente').val('')
    
    AplicaRemiGlobal=0
    $('#selectAll').prop('checked',false)

    let ruta_busqueda='buscar-predios-rurales/'+cedula+'/'
    let ubicacion=$('#lugar').val()
    if(ubicacion==1){
        ruta_busqueda='buscar-predios-urbanos/'+cedula+'/'
    }
   
    $("#tbodyRuralDetalle").html('');
    $('#tbodyRuralDetalle').empty(); 
    var num_col = $("#tableDetalleRural thead tr th").length;
    vistacargando("m", "Espere por favor")
    $.get(ruta_busqueda, function(data){
        console.log(data)
       
        vistacargando("")
        if(data.error==true){
			$("#tbodyRuralDetalle").html('');
			$("#tbodyRuralDetalle").html(`<tr><td colspan="${num_col}" style="text-align:center>No existen registros</td></tr>`);
			alertNotificar(data.mensaje,"error");
            // cancelar()
			return;   
		}
        if(data.resultado.length==0){
            $("#tbodyRuralDetalle").html('');
			$("#tbodyRuralDetalle").append(`<tr><td colspan="${num_col}" style="text-align:center>No existen registros</td></tr>`);
            alertNotificar('No existen deuda para esta persona/empresa',"error");
            return
        }
       
        $.each(data.resultado,function(i, item){
            let cedula=item.num_ident
            let codigo=item.num_titulo
            let num_matricula=''
            if(ubicacion==1){
                num_matricula = item.num_predio;
                codigo=item.id
                cedula=item.ci_ruc
            }
            $('#tbodyRuralDetalle').append(`<tr>
                 
                    <td style="width:10%; text-align:center; vertical-align:middle">
                        ${num_matricula}                      
                    </td>
                 
                    <td style="width:25%; text-align:center; vertical-align:middle">
                        ${item.clave}                      
                    </td>
                    <td style="width:30%; text-align:center; vertical-align:middle">
                        ${item.direcc_cont !=null ? item.direcc_cont : '*'}                   
                    </td>

                    <td style="width:10%; text-align:center; vertical-align:middle">
                        ${cedula}                      
                    </td>
                    
                
            </tr>`);
        })
        const firstTab = document.querySelector('#tab-actual-btn');
        //const tab = new bootstrap.Tab(firstTab);
        //tab.show();
        $('#modalContri').modal('show')
       
        let tamanio=data.resultado.length
        
            
        $('#num_ident_contr').html(cedula)

         $('#div_apellidos').show()
        if(ubicacion==2){
            let separa_direccion=direccion.split("-")
            $('#ciudad_contribuyente').val(separa_direccion[0])
            $('#direccion_contribuyente').val(separa_direccion[1])
            if(cedula.length==10){
                let separa_nombre=data.resultado[tamanio-1].nombre_per
                separa_nombre=separa_nombre.split("-")
                console.log(separa_nombre)
                $('#nombre_contr').html(data.resultado[tamanio-1].nombre_per)
                $('#apellido_contribuyente').val(separa_nombre[0])
                $('#nombre_contribuyente').val(separa_nombre[1])
            }else{
                $('#nombre_contr').html(data.resultado[tamanio-1].nombre_per)
                let separa_nombre=data.resultado[tamanio-1].nombre_per
                separa_nombre=separa_nombre.split("-")
                $('#nombre_contribuyente').prop('readonly', true)
                $('#ci_ruc_contribuyente').prop('readonly', true)
                $('#nombre_contribuyente').val(separa_nombre[1])
                $('#div_apellidos').hide()
               
            }
        }else{
            $('#nombre_contr').html(data.resultado[tamanio-1].nombres)
            $('#apellido_contribuyente').val(data.resultado[tamanio-1].apellidos)
            $('#nombre_contribuyente').val(data.resultado[tamanio-1].nombres)

             let separa_direccion=direccion.split("-")
            $('#ciudad_contribuyente').val(separa_direccion[0])
            $('#direccion_contribuyente').val(separa_direccion[1])
        }

       
        $('#clave_contr').html(data.resultado[tamanio-1].clave)

        
        $('#ci_ruc_contribuyente').val(cedula)
        $('#correos_notifica').val(correos)
        $('#id_cont').val(data.id_cont)
       
        const lugarTexto = (ubicacion == 1) ? 'Urbano' : 'Rural';
        $("#lugar_not").val(lugarTexto)
        cargarCorreos(correos)
        cargarTelefonos(telefono)
        // llenarTablaNotificacion(data.notificaciones)
        ejecuta()
    }).fail(function(){
        vistacargando("")
        alertNotificar("Se produjo un error, por favor intentelo más tarde","error");  
        $("#tbodyRuralDetalle").html('');
		$("#tbodyRuralDetalle").html(`<tr><td colspan="${num_col}" style="text-align:center">Se produjo un error, por favor intentelo más tarde</td></tr>`);
    });
}

function cargarTelefonos(telefonos) {

    // 👇 convertir string a array si es necesario
    if (typeof telefonos === 'string') {
        telefonos = telefonos.split(',');
    }

    const container = $('#telefonos-container');
    container.empty();

    telefonos.forEach((telefono, index) => {
        telefono = telefono.trim(); // limpiar espacios

        if (index === 0) {
            container.append(`
                <div class="input-group mb-2">
                    <input type="text" class="form-control" name="telefono[]" value="${telefono}">
                    <button class="btn btn-outline-success" type="button"
                        onclick="agregarInput('telefonos-container', 'text', 'telefono[]')">
                        <i class="bi bi-plus-lg"></i>
                    </button>
                </div>
            `);
        } else {
            agregarInput('telefonos-container', 'text', 'telefono[]');
            container.find('input[name="telefono[]"]').last().val(telefono);
        }
    });
}

function cargarCorreos(correos) {

    if (typeof correos === 'string') {
        correos = correos.split(',');
    }

    const container = $('#correos-container');
    container.empty();

    correos.forEach((correo, index) => {
        correo = correo.trim();

        if (index === 0) {
            container.append(`
                <div class="input-group mb-2">
                    <input type="email" class="form-control" name="correo[]" value="${correo}">
                    <button class="btn btn-outline-success" type="button"
                        onclick="agregarInput('correos-container', 'email', 'correo[]')">
                        <i class="bi bi-plus-lg"></i>
                    </button>
                </div>
            `);
        } else {
            agregarInput('correos-container', 'email', 'correo[]');
            container.find('input[name="correo[]"]').last().val(correo);
        }
    });
}



function ejecuta(){
   $('#modalContri button[data-bs-target="#tab-actual"]').tab('show');
}

function llenarTablaNotificacion(data){
    $("#tbodyNotifica").html('');
    $('#tbodyNotifica').empty(); 
    var num_col = $("#tableNotifica thead tr th").length;
    if(data.length==0){
        
        $("#tbodyNotifica").html('');
        $("#tbodyNotifica").append(`<tr><td colspan="${num_col}" style="text-align:center">No existen registros</td></tr>`);
        return
    }
    $.each(data,function(i, item){

        let archivos = [];

        if (item.archivo) {
            try {
                // decodifica doblemente si es string anidado
                let parsed = JSON.parse(item.archivo);
                if (typeof parsed === 'string') {
                    archivos = JSON.parse(parsed);
                } else {
                    archivos = parsed;
                }
            } catch (e) {
                archivos = [];
            }
        }
        let id_notifica = item.id
        // construir la lista de archivos
        let listaArchivos = '';
        if (archivos.length > 0) {
            listaArchivos = '<ul>';
            $.each(archivos, function (i2, archivo) {
                
                let url = 'notificacion/' + id_notifica + '/' + i2;
                listaArchivos += `
                    <li>
                        <a href="${url}" target="_blank" >
                            ${archivo.nombre}
                        </a>
                    </li>
                `;

               
            });
            listaArchivos += '</ul>';
        } else {
            listaArchivos = '<p><i>No hay archivos adjuntos</i></p>';
        }

        let correos = [];

        if (item.correo) {
            try {
                // decodifica doblemente si es string anidado
                let parsed = JSON.parse(item.correo);
                if (typeof parsed === 'string') {
                    correos = JSON.parse(parsed);
                } else {
                    correos = parsed;
                }
            } catch (e) {
                correos = [];
            }
        }
        
        // construir la lista de archivos
        let listaCorreos = '';
        if (archivos.length > 0) {
            listaCorreos = '<ul>';
            $.each(correos, function (i3, correo) {
                
                
                listaCorreos += `
                    <li>                      
                        ${correo}                      
                    </li>
                `;

               
            });
            listaCorreos += '</ul>';
        } else {
            listaCorreos = '<p><i></i></p>';
        }

        $('#tbodyNotifica').append(`<tr>
                <td style="width:5%; text-align:center; vertical-align:middle">
                    ${i+1}               
                </td>
                <td style="width:10%; text-align:center; vertical-align:middle">
                    ${item.lugar}               
                </td>
                <td style="width:25%; text-align:center; vertical-align:middle">
                    ${item.nombre_usuario}               
                </td>
                <td style="width:10%; text-align:center; vertical-align:middle">
                    ${item.created_at}                        
                </td>
                <td style="width:25%; text-align:center; vertical-align:middle">
                    ${listaCorreos}                     
                </td>
                <td style="width:30%; text-align:center; vertical-align:middle">
                    ${listaArchivos}                       
                </td>
            
        </tr>`);
    })
}

$(document).on('change', '#selectAll', function() {
    // Obtener el estado del checkbox (marcado o desmarcado)
    var isChecked = $(this).prop('checked');
    
    // Cambiar el estado de todos los checkboxes en el cuerpo de la tabla
    $('#tbodyRuralDetalle input[type="checkbox"]').each(function() {
        $(this).prop('checked', isChecked);
    });
    
   
});

function cerrarModalPago(){
    $('#modalContri').modal('hide')
}

function descargarLiquidacion(){
    let ubicacion=$('#lugar').val()
    let rutapdf="pdf-liquidacion-rural-urb"
   
    vistacargando("m", "Espere por favor")
    $.get(rutapdf+'/'+CedulaGlobal+'/'+ubicacion, function(data){
        console.log(data)
       
        vistacargando("")
        if(data.error==true){
			alertNotificar(data.mensaje,"error");
            // cancelar()
			return;   
		}
        alertNotificar("El documento se descargara en unos segundos...","success");
        window.location.href="descargar-reporte/"+data.pdf
       
       
    }).fail(function(){
        vistacargando("")
        alertNotificar("Se produjo un error, por favor intentelo más tarde","error");  
       
    });
}

function descargaPagoVolutario(){
    let ubicacion=$('#lugar').val()
    let rutapdf="pago-voluntario-rural-urb"
   
    vistacargando("m", "Espere por favor")
    $.get(rutapdf+'/'+CedulaGlobal+'/'+ubicacion, function(data){
        console.log(data)
       
        vistacargando("")
        if(data.error==true){
			alertNotificar(data.mensaje,"error");
            // cancelar()
			return;   
		}
        alertNotificar("El documento se descargara en unos segundos...","success");
        window.location.href="descargar-reporte/"+data.pdf
       
       
    }).fail(function(){
        vistacargando("")
        alertNotificar("Se produjo un error, por favor intentelo más tarde","error");  
       
    });
}

function generarTitulos(){
    let urbano_rural=$('#lugar').val()
    // alert(urbano_rural)
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
    ruta="tituloscoactivarural/imprimir"
    if(urbano_rural==1){
        ruta="tituloscoactivaurb/imprimir"
    }
    var FrmData=$("#formExonerar").serialize();

    $.ajax({
            
        type: 'POST',
        url: ruta,
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

function cancelarActualizacion(){
    //$('#correos_notifica').val('')
    $('#archivo_notifica').val('')
    location.reload();
} 

$("#formActualiza").submit(function(e){
    e.preventDefault();
    
    //validamos los campos obligatorios
    let cedula=$('#ci_ruc_contribuyente').val()
    let nombre_contribuyente=$('#nombre_contribuyente').val()
    let apellido_contribuyente=$('#apellido_contribuyente').val()
    let direccion_contribuyente=$('#direccion_contribuyente').val()
    let correo=$('#correos_notifica').val()
   
    if(nombre_contribuyente=="" || nombre_contribuyente==null){
        alertNotificar("Debe ingresar el nombre del contribuyente","error")
        return
    } 
    if(cedula.length==10){
        if(apellido_contribuyente=="" || apellido_contribuyente==null){
            alertNotificar("Debe ingresar el apellido del contribuyente","error")
            return
        }
    }

    if (cedula.length !== 10 && cedula.length !== 13) {
        alertNotificar("La cedula/Ruc debe tener 10 o 13 digitos","error")
        return
    }   
    
    if(direccion_contribuyente=="" || direccion_contribuyente==null){
        alertNotificar("Debe ingresar la direccion del contribuyente","error")
        return
    }

   // ================= VALIDAR CORREOS =================
    let correoValido = false

    $('input[name="correo[]"]').each(function () {
        if ($(this).val().trim() !== "") {
            correoValido = true
        }
    })

    if (!correoValido) {
        alertNotificar("Debe ingresar al menos un correo electrónico", "error")
        return
    }

    // ================= VALIDAR TELÉFONOS =================
    let telefonoValido = false

    $('input[name="telefono[]"]').each(function () {
        if ($(this).val().trim() !== "") {
            telefonoValido = true
        }
    })

    if (!telefonoValido) {
        alertNotificar("Debe ingresar al menos un número de teléfono", "error")
        return
    }

    var FrmData = new FormData(this);
    swal({
        title: '¿Desea realizar la actualizacion?',
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
            $('#btn_enviar').prop('disabled',true)
            $('#btn_cancelar').prop('disabled',true)
            
            vistacargando("m","Espere por favor")
            $.ajaxSetup({
                headers: {
                    'X-CSRF-TOKEN': $('meta[name="csrf-token"]').attr('content')
                }
            });

            //comprobamos si es registro o edicion
            let tipo="POST"
            let url_form="actualiza-contribuyente"
             //var FrmData=$("#formActualiza").serialize();
           

            $.ajax({
                    
                type: tipo,
                url: url_form,
                method: tipo,             
                data: FrmData,      
                contentType:false,
                cache:false,
                processData:false, 

                success: function(data){
                    $('#btn_enviar').prop('disabled',false)
                    $('#btn_cancelar').prop('disabled',false)
                    vistacargando("");                
                    if(data.error==true){
                        alertNotificar(data.mensaje,'error');
                        return;                      
                    }
                
                    alertNotificar(data.mensaje,"success");
                    setTimeout(() => {
                        location.reload();     
                    }, 900);
                           
                }, error:function (data) {
                    console.log(data)

                    vistacargando("");
                    alertNotificar('Ocurrió un error','error');
                    $('#btn_enviar').prop('disabled',false)
                    $('#btn_cancelar').prop('disabled',false)
                }
            });
         }
        sweetAlert.close();   // ocultamos la ventana de pregunta
    });
})

function agregarInput(containerId, tipo, name) {
    const container = document.getElementById(containerId);

    const div = document.createElement('div');
    div.className = 'input-group mb-2';

    div.innerHTML = `
        <input type="${tipo}" class="form-control" name="${name}">
        <button class="btn btn-outline-danger" type="button" onclick="eliminarInput(this)">
            <i class="bi bi-x-lg"></i>
        </button>
    `;

    container.appendChild(div);
}

function eliminarInput(btn) {
    btn.closest('.input-group').remove();
}


