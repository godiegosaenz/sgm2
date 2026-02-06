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
                    <button type="button" class="btn btn-success btn-sm" onclick="buscarPredios('${cedula}', '${item.nombres}','${correos}')">
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
function buscarPredios(cedula, nombres, correos){
    CedulaGlobal=cedula
    $('#total_deuda').html('')
    $('#total_seleccionado').html('')
    $('#exon_contr').html('')

    $('#correos_notifica').val('')
    $('#archivo_notifica').val('')
    $('#nombres_notifica').val('')
    $('#ci_ruc_notifica').val('')
    
    AplicaRemiGlobal=0
    $('#selectAllPredios').prop('checked',false)

    let ruta_busqueda='buscar-liquidacion-rurales/'+cedula+'/'
    let ubicacion=$('#lugar').val()
    if(ubicacion==1){
        // ruta_busqueda='buscar-liquidacion-urbanos/'+cedula+'/'
        ruta_busqueda='buscar-predios-urbanos-aseo/'+cedula+'/'
    }
   
    $("#tbodyPrediosDetalle").html('');
    $('#tbodyPrediosDetalle').empty(); 
    var num_col = $("#tablePredios thead tr th").length;
    vistacargando("m", "Espere por favor")
    $.get(ruta_busqueda, function(data){
        console.log(data)
       
        vistacargando("")
        if(data.error==true){
			$("#tbodyPrediosDetalle").html('');
			$("#tbodyPrediosDetalle").html(`<tr><td colspan="${num_col}" style="text-align:center>No existen registros</td></tr>`);
			alertNotificar(data.mensaje,"error");
            // cancelar()
			return;   
		}
        if(data.resultado.length==0){
            $("#tbodyPrediosDetalle").html('');
			$("#tbodyPrediosDetalle").append(`<tr><td colspan="${num_col}" style="text-align:center>No existen registros</td></tr>`);
            alertNotificar('No existen deuda para esta persona/empresa',"error");
            return
        }
       
        $.each(data.resultado,function(i, item){
           
            let num_matricula=""
            let codigo=item.num_titulo
            if(ubicacion==1){
                num_matricula = item.num_predio;
                codigo=item.id
            }
            $('#tbodyPrediosDetalle').append(`<tr>
                   <td style="width:5%; text-align:center; vertical-align:middle">
                        <input type="checkbox" name="checkNumPredio[]" id="predio_valor" value="${item.idpredio}" >                
                    </td>
                    <td style="width:10%; text-align:center; vertical-align:middle">
                        ${num_matricula}                      
                    </td>
                    <td style="width:20%; text-align:center; vertical-align:middle">
                        ${item.clave_cat}                     
                    </td>
                    <td style="width:25%; text-align:center; vertical-align:middle">
                        ${item.ciudadela}                      
                    </td>
                    <td style="width:30%; text-align:center; vertical-align:middle">
                        ${item.tipoconj !=null ? item.tipoconj : ''}                   
                    </td>
                    <td style="width:25%; text-align:center; vertical-align:middle">
                        ${item.uso_suelo !=null ? item.uso_suelo : ''}                     
                    </td>
                   
                
            </tr>`);
        })
        const firstTab = document.querySelector('#tab-actual-btn');
        //const tab = new bootstrap.Tab(firstTab);
        //tab.show();
        $('#modalContri').modal('show')
        $('#total_deuda').html(data.total_valor);
        let tamanio=data.resultado.length
        $('#nombre_contr').html(nombres)
        $('#num_ident_contr').html(cedula)
        $('#direccion_contr').html(data.resultado[tamanio-1].direcc_cont)
        $('#clave_contr').html(data.resultado[tamanio-1].clave)

        $('#nombres_notifica').val(nombres)
        $('#ci_ruc_notifica').val(cedula)
        $('#correos_notifica').val(correos)
       
        const lugarTexto = (ubicacion == 1) ? 'Urbano' : 'Rural';
        $("#lugar_not").val(lugarTexto)


        ejecuta()
    }).fail(function(){
        vistacargando("")
        alertNotificar("Se produjo un error, por favor intentelo más tarde","error");  
        $("#tbodyRuralDetalle").html('');
		$("#tbodyRuralDetalle").html(`<tr><td colspan="${num_col}" style="text-align:center">Se produjo un error, por favor intentelo más tarde</td></tr>`);
    });
}

globalThis.AplicaRemiGlobal=0
globalThis.CedulaGlobal=""
function buscarTitulos(cedula, nombres, correos){
    CedulaGlobal=cedula
    $('#total_deuda').html('')
    $('#total_seleccionado').html('')
    $('#exon_contr').html('')

    $('#correos_notifica').val('')
    $('#archivo_notifica').val('')
    $('#nombres_notifica').val('')
    $('#ci_ruc_notifica').val('')
    
    AplicaRemiGlobal=0
    $('#selectAll').prop('checked',false)

    let ruta_busqueda='buscar-liquidacion-rurales/'+cedula+'/'
    let ubicacion=$('#lugar').val()
    if(ubicacion==1){
        // ruta_busqueda='buscar-liquidacion-urbanos/'+cedula+'/'
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
            let recar = item.recargo !=null ? item.recargo : '0.00';
            let descu = item.descuento !=null ? item.descuento : '0.00';
            let intereses = item.intereses !=null ? item.intereses : '0.00';
            let num_matricula=""
            let codigo=item.num_titulo
            if(ubicacion==1){
                num_matricula = item.num_predio;
                codigo=item.id
            }
            $('#tbodyRuralDetalle').append(`<tr>
                   <td style="width:5%; text-align:center; vertical-align:middle">
                        <input type="checkbox" name="checkLiquidacion[]" id="predio_valor" value="${codigo}" >                
                    </td>
                    <td style="width:10%; text-align:center; vertical-align:middle">
                        ${num_matricula}                      
                    </td>
                    <td style="width:20%; text-align:center; vertical-align:middle">
                        ${item.num_titulo}                     
                    </td>
                    <td style="width:25%; text-align:center; vertical-align:middle">
                        ${item.clave}                      
                    </td>
                    <td style="width:30%; text-align:center; vertical-align:middle">
                        ${item.direcc_cont !=null ? item.direcc_cont : '*'}                   
                    </td>
                    <td style="width:15%; text-align:center; vertical-align:middle">
                        ${item.subtotal_emi}                     
                    </td>
                    <td style="width:15%; text-align:center; vertical-align:middle">
                        ${descu}                     
                    </td>
                    <td style="width:15%; text-align:center; vertical-align:middle">
                        ${recar}                     
                    </td>
                     <td style="width:15%; text-align:center; vertical-align:middle">
                        ${intereses}                     
                    </td>
                    <td style="width:15%; text-align:center; vertical-align:middle">
                        ${item.total_pagar}                     
                    </td>
                
            </tr>`);
        })
        const firstTab = document.querySelector('#tab-actual-btn');
        //const tab = new bootstrap.Tab(firstTab);
        //tab.show();
        $('#modalContri').modal('show')
        $('#total_deuda').html(data.total_valor);
        let tamanio=data.resultado.length
        $('#nombre_contr').html(nombres)
        $('#num_ident_contr').html(cedula)
        $('#direccion_contr').html(data.resultado[tamanio-1].direcc_cont)
        $('#clave_contr').html(data.resultado[tamanio-1].clave)

        $('#nombres_notifica').val(nombres)
        $('#ci_ruc_notifica').val(cedula)
        $('#correos_notifica').val(correos)
       
        const lugarTexto = (ubicacion == 1) ? 'Urbano' : 'Rural';
        $("#lugar_not").val(lugarTexto)


        llenarTablaNotificacion(data.notificaciones)
        ejecuta()
    }).fail(function(){
        vistacargando("")
        alertNotificar("Se produjo un error, por favor intentelo más tarde","error");  
        $("#tbodyRuralDetalle").html('');
		$("#tbodyRuralDetalle").html(`<tr><td colspan="${num_col}" style="text-align:center">Se produjo un error, por favor intentelo más tarde</td></tr>`);
    });
}

function ejecuta(){
   $('#modalContri button[data-bs-target="#tab-actual"]').tab('show');
}



$(document).on('change', '#selectAll', function() {
    // Obtener el estado del checkbox (marcado o desmarcado)
    var isChecked = $(this).prop('checked');
    
    // Cambiar el estado de todos los checkboxes en el cuerpo de la tabla
    $('#tbodyRuralDetalle input[type="checkbox"]').each(function() {
        $(this).prop('checked', isChecked);
    });
    
   
});

$(document).on('change', '#selectAllPredios', function() {
    // Obtener el estado del checkbox (marcado o desmarcado)
    var isChecked = $(this).prop('checked');
    
    // Cambiar el estado de todos los checkboxes en el cuerpo de la tabla
    $('#tbodyPrediosDetalle input[type="checkbox"]').each(function() {
        $(this).prop('checked', isChecked);
    });
    
   
});

$(document).on('change', '#selectAllListadoParcial', function() {
    // Obtener el estado del checkbox (marcado o desmarcado)
    var isChecked = $(this).prop('checked');
    
    // Cambiar el estado de todos los checkboxes en el cuerpo de la tabla
    $('#tbodyDetalleAseoParcial input[type="checkbox"]').each(function() {
        $(this).prop('checked', isChecked);
    });

   
    var table = $('#tableListadoDeudaAseoParcial').DataTable(); // Obtener la instancia de DataTable
    if (isChecked) {
        // Seleccionar todas las filas en todas las páginas
        table.rows().select();
    } else {
        // Deseleccionar todas las filas en todas las páginas
        table.rows().deselect();
    }
    
   
});

$(document).on('change', '#selectAllDeudaAseo', function() {
    // Obtener el estado del checkbox (marcado o desmarcado)
    var isChecked = $(this).prop('checked');
    
    // Cambiar el estado de todos los checkboxes en el cuerpo de la tabla
    $('#tbodyDetalleAseo input[type="checkbox"]').each(function() {
        $(this).prop('checked', isChecked);
    });

    
    
   
});


function cerrarModalPago(){
    $('#modalContri').modal('hide')
}


function prediosSeleccionados(){
    $("#tableListadoDeudaAseo tbody").html('');
    $('#tableListadoDeudaAseo tbody').empty(); 
    let urbano_rural=$('#lugar').val()

    $('#selectAllDeudaAseo').prop('checked',false)
    // alert(urbano_rural)
    if ($('input[name="checkNumPredio[]"]:checked').length === 0) {
        alertNotificar("Debe seleccionar al menos un predio.","error");
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
        ruta="obtener-deuda-aseo-urb"
    }
    var FrmData=$("#formAseo").serialize();

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
            if(data.resultado.length==0){
                alertNotificar('No existe deudas pendientes','success');
                return;  
            }
            $('#predios_totales_detalle').hide()
            $('#listar_liquidaciones').show()
            $("#tableListadoDeudaAseo tbody").html('');
            $.each(data.resultado,function(i, item){
              
				$('#tbodyDetalleAseo').append(`<tr>
                                               
                                                <td style="width:10%;  text-align:left; vertical-align:middle">
                                                    <input class="form-check-input" type="checkbox" value=${item.idpredio} name="checkLiquidacion[]">
                                                </td>
                                               
                                                <td style="width:10%; text-align:center; vertical-align:middle">
                                                    ${item.num_predio}
                                                </td>

                                                <td style="width:10%; text-align:center; vertical-align:middle">
                                                    ${item.clave_cat}
                                                </td>

                                                <td style="width:10%; text-align:center; vertical-align:middle">
                                                    ${item.uso_suelo}
                                                </td>

                                                <td style="width:40%; text-align:left; vertical-align:middle">
                                                    ${item.tipoconj}
                                                </td>

                                                <td style="width:10%; text-align:right; vertical-align:middle">
                                                    ${item.uso_suelo}
                                                </td>

                                                <td style="width:10%; text-align:center; vertical-align:middle">
                                                    ${item.lvalor}
                                                </td>

                                                <td style="width:10%; text-align:center; vertical-align:middle">
                                                    ${item.ldescuento !=null ? item.ldescuento : '0.00'}
                                                </td>

                                                <td style="width:40%; text-align:left; vertical-align:middle">
                                                    ${item.lrecargo !=null ? item.lrecargo : '0.00'}
                                                </td>

                                                <td style="width:10%; text-align:right; vertical-align:middle">
                                                    ${item.linteres !=null ? item.linteres : '0.00'}
                                                </td>
                                                <td style="width:10%; text-align:right; vertical-align:middle">
                                                    ${item.total_cob !=null ? item.total_cob : '0.00'}
                                                </td>
                                                
											
										</tr>`);
			})
                            
        }, error:function (data) {
            console.log(data)

            vistacargando("");
            alertNotificar('Ocurrió un error','error');
        }
    });
}

function pagoParcial(){
    // $("#tableListadoDeudaAseoParcial tbody").html('');
    // $('#tableListadoDeudaAseoParcial tbody').empty(); 

    $('#tableListadoDeudaAseoParcial').DataTable().destroy();
	$('#tableListadoDeudaAseoParcial tbody').empty(); 

    $('#selectAllListadoParcial').prop('checked',false)
    let urbano_rural=$('#lugar').val()
    // alert(urbano_rural)
    if ($('input[name="checkLiquidacion[]"]:checked').length === 0) {
        alertNotificar("Debe seleccionar al menos un predio.","error");
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
        ruta="obtener-deuda-aseo-urb-parcial"
    }
    var FrmData=$("#formAseoListado").serialize();

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
            if(data.resultado.length==0){
                alertNotificar('No existe deudas pendientes','success');
                return;  
            }
            $('#listar_liquidaciones').hide()
            $('#listar_liquidaciones_parcial').show()
            $("#tableListadoDeudaAseoParcial tbody").html('');
            $.each(data.resultado,function(i, item){
              
				$('#tbodyDetalleAseoParcial').append(`<tr>
                                               
                                                <td style="width:10%;  text-align:left; vertical-align:middle">
                                                    <input class="form-check-input" type="checkbox" value=${item.idpredio} name="checkLiquidacionDetalle[]">
                                                </td>
                                               
                                                <td style="width:10%; text-align:center; vertical-align:middle">
                                                    ${item.num_predio}
                                                </td>

                                                <td style="width:10%; text-align:center; vertical-align:middle">
                                                    ${item.clave_cat}
                                                </td>

                                                <td style="width:10%; text-align:center; vertical-align:middle">
                                                    ${item.uso_suelo}
                                                </td>

                                                <td style="width:10%; text-align:left; vertical-align:middle">
                                                    ${item.anio}
                                                </td>

                                                <td style="width:10%; text-align:right; vertical-align:middle">
                                                    ${item.mes_nombre}
                                                </td>

                                                <td style="width:10%; text-align:center; vertical-align:middle">
                                                    ${item.lvalor}
                                                </td>

                                                <td style="width:10%; text-align:center; vertical-align:middle">
                                                    ${item.ldescuento !=null ? item.ldescuento : '0.00'}
                                                </td>

                                                <td style="width:10%; text-align:left; vertical-align:middle">
                                                    ${item.lrecargo !=null ? item.lrecargo : '0.00'}
                                                </td>

                                                <td style="width:10%; text-align:right; vertical-align:middle">
                                                    ${item.linteres !=null ? item.linteres : '0.00'}
                                                </td>
                                                <td style="width:10%; text-align:right; vertical-align:middle">
                                                    ${item.total_cob !=null ? item.total_cob : '0.00'}
                                                </td>
                                                
											
										</tr>`);
			})
            cargar_estilos_datatable('tableListadoDeudaAseoParcial');
                            
        }, error:function (data) {
            console.log(data)

            vistacargando("");
            alertNotificar('Ocurrió un error','error');
        }
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
        select: {
            style: 'multi' // Permite la selección múltiple de filas
        },
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

function volverAgrupado(){
    $('#listar_liquidaciones').show()
    $('#listar_liquidaciones_parcial').hide()
}

function volverListaPredio(){
    $('#predios_totales_detalle').show()
    $('#listar_liquidaciones').hide()
}


function cancelarNotifica(){
    //$('#correos_notifica').val('')
    $('#archivo_notifica').val('')
} 

$("#formNotifica").submit(function(e){
    e.preventDefault();
    
    //validamos los campos obligatorios
    let cedula=$('#ci_ruc_notifica').val()
    let archivo_notifica=$('#archivo_notifica').val()
    let correo=$('#correos_notifica').val()
   
    if(archivo_notifica=="" || archivo_notifica==null){
        alertNotificar("Seleccione el archivo","error")
        return
    } 
     var FrmData = new FormData(this);
    swal({
        title: '¿Desea realizar la notificacion?',
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
            let url_form="notifica-contribuyente"
             //var FrmData=$("#formNotifica").serialize();
           

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
                
                    alertNotificar("Notificacion creada exitosamente","success");
                    llenarTablaNotificacion(data.resultado)
                    cancelarNotifica()               
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
