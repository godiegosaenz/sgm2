function cambiaTipo(){
    limpiarBusqueda()
    var tipo=$('#tipo').val()
    $('#div_cedula').show()
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

function limpiarBusqueda(){
    $('#cedula').val('')
    $('#clave').val('')
    $('#cmb_nombres').val('')
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
    var tipo_per=$('#tipo_per').val()
    var tipo_=$('#tipo').val()
    
    if(tipo_per==""){
        alertNotificar("Debe seleccionar el tipo persona","error")
        return
    }

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

    vistacargando("m","Espere por favor")
    $.ajaxSetup({
        headers: {
            'X-CSRF-TOKEN': $('meta[name="csrf-token"]').attr('content')
        }
    });

  
    url_form="buscar-contribuyente-rural"

    let FrmData = {
        tipo_per:tipo_per,
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

function llenarData(data){
    $.each(data,function(i, item){
        $('#tbodyRural').append(`<tr>
                <td style="width:5%; text-align:center; vertical-align:middle">
                    <button type="button" class="btn btn-success btn-sm" onclick="buscarTitulos('${item.Pre_CodigoCatastral}','${item.Titpr_RUC_CI}')">
                        <i class="fa fa-money"></i>
                    </button>                    
                </td>
                <td style="width:10%; text-align:center; vertical-align:middle">
                    Rural                    
                </td>
                <td style="width:15%; text-align:center; vertical-align:middle">
                    ${item.Pre_CodigoCatastral}                     
                </td>
                <td style="width:30%; text-align:center; vertical-align:middle">
                    ${item.TitPr_DireccionCont}                     
                </td>
                <td style="width:30%; text-align:center; vertical-align:middle">
                    ${item.Ciu_Apellidos} ${item.Ciu_Nombres}                    
                </td>
                <td style="width:15%; text-align:center; vertical-align:middle">
                    ${item.Titpr_RUC_CI}                     
                </td>
            
        </tr>`);
    })
}

globalThis.AplicaRemiGlobal=0
function buscarTitulos(clave, cedula){
    $('#total_cobrado').html('')
    $('#total_seleccionado').html('')
    
    AplicaRemiGlobal=0
    $('#selectAll').prop('checked',false)
    $('#modalContri').modal('show')
    $("#tbodyRuralDetalle").html('');
    $('#tbodyRuralDetalle').empty(); 
    var num_col = $("#tableDetalleRural thead tr th").length;
    vistacargando("m", "Espere por favor")
    $.get('buscar-titulos-rurales-cobrados/'+clave+'/'+cedula, function(data){
        console.log(data)
       
        vistacargando("")
        if(data.error==true){
			$("#tbodyRuralDetalle").html('');
			$("#tbodyRuralDetalle").html(`<tr><td colspan="${num_col}" style="text-align:center>No existen registros</td></tr>`);
			alertNotificar(data.mensaje,"error");
            // cancelar()
			return;   
		}
        let total_cobrado=0;
        $.each(data.resultado,function(i, item){
           
            $('#tbodyRuralDetalle').append(`<tr>
                    <td style="width:5%; text-align:center; vertical-align:middle">
                        <input type="checkbox" name="predio_valor" id="predio_valor" value="${item.total_cobrado}" data-num-titulo="${item.num_titulo}"  data-orden="${i+1}" data-valor-cobrado="${item.total_cobrado}">                
                    </td>
                    <td style="width:5%; text-align:center; vertical-align:middle">
                        Rural                    
                    </td>
                    <td style="width:15%; text-align:center; vertical-align:middle">
                        ${item.num_titulo}                     
                    </td>
                    <td style="width:20%; text-align:center; vertical-align:middle">
                        ${item.clave}                      
                    </td>
                    <td style="width:25%; text-align:center; vertical-align:middle">
                        ${item.direcc_cont !=null ? item.direcc_cont : '*'}                   
                    </td>
                    <td style="width:15%; text-align:center; vertical-align:middle">
                        ${item.fecha_recaudacion}                     
                    </td>
                    <td style="width:15%; text-align:center; vertical-align:middle">
                        ${item.total_cobrado}                     
                    </td>
                    
                
            </tr>`);

            total_cobrado += parseFloat(
                ((item.total_cobrado ?? "0").toString().trim()).replace(",", ".")
            ) || 0;
        })
        console.log(total_cobrado.toFixed(2))
        $('#total_cobrado').html(total_cobrado.toFixed(2));
        let tamanio=data.resultado.length
        $('#nombre_contr').html(data.resultado[tamanio-1].Ciu_Apellidos +" "+ data.resultado[tamanio-1].Ciu_Nombres)
        $('#num_ident_contr').html(data.resultado[tamanio-1].num_ident)
        $('#direccion_contr').html(data.resultado[tamanio-1].direcc_cont)
        $('#clave_contr').html(data.resultado[tamanio-1].clave)

        // $('#selectRemision').prop('checked',false)
        // if(data.aplica_remision==1){
        //     $('#selectRemision').prop('checked',true)
        //     AplicaRemiGlobal=1
        // }

    }).fail(function(){
        vistacargando("")
        alertNotificar("Se produjo un error, por favor intentelo más tarde","error");  
        $("#tbodyRuralDetalle").html('');
		$("#tbodyRuralDetalle").html(`<tr><td colspan="${num_col}" style="text-align:center">Se produjo un error, por favor intentelo más tarde</td></tr>`);
    });
}

globalThis.estaChequeado=""
$(document).on('change', '#selectRemision', function() {
    estaChequeado = $(this).prop('checked');
    if(estaChequeado==true){
        $('#selectRemision').prop('checked',false)
    }else{
        $('#selectRemision').prop('checked',true)
    }
})

// Función para seleccionar/deseleccionar todas las filas
$(document).on('change', '#selectAll', function() {
    // Obtener el estado del checkbox (marcado o desmarcado)
    var isChecked = $(this).prop('checked');
    
    // Cambiar el estado de todos los checkboxes en el cuerpo de la tabla
    $('#tbodyRuralDetalle input[type="checkbox"]').each(function() {
        $(this).prop('checked', isChecked);
    });
    
    // Recalcular el total cuando se seleccionan o deseleccionan las filas
    actualizarTotalGeneral();
});

// Función para calcular el total cuando se marca o desmarca un checkbox individual
$(document).on('change', '#tbodyRuralDetalle input[type="checkbox"]', function() {
    actualizarTotalGeneral();
});
globalThis.numTitulosSeleccionados=[]
globalThis.ordenTitulosSeleccionados=[]
globalThis.valorCobrado=[]
globalThis.valorInteres=[]
globalThis.valorDescuento=[]
globalThis.valorRecarga=[]
// Función para actualizar el total general basado en los checkboxes seleccionados
function actualizarTotalGeneral() {
    let totalGeneral = 0;
    numTitulosSeleccionados = [];
    ordenTitulosSeleccionados=[]
    valorCobrado=[]
    // Iterar sobre cada checkbox marcado y sumar el valor correspondiente
    $('#tbodyRuralDetalle input[type="checkbox"]:checked').each(function() {
        
        // totalGeneral += parseFloat($(this).val());
        //  totalGeneral += parseFloat(
        //         ((parseFloat($(this).val()) ?? "0").toString().trim()).replace(",", ".")
        //     ) || 0
        totalGeneral=totalGeneral+parseFloat($(this).val())
        numTitulosSeleccionados.push($(this).data('num-titulo'));
        ordenTitulosSeleccionados.push($(this).data('orden'));
        valorCobrado.push($(this).data('valor-cobrado'));
        valorInteres.push($(this).data('valor-interes'));
        valorDescuento.push($(this).data('valor-descuento'));
        valorRecarga.push($(this).data('valor-recarga'));
    });
//    (totalGeneral)
    // Actualizar el total general en un lugar específico (ej. un div o campo)
    // $('#total_seleccionado').text(totalGeneral.toFixed(2));  // Actualiza el total con 2 decimales

    console.log('Num Titulos Seleccionados:', numTitulosSeleccionados);
}


function descargarTituloRural(){
    let clave_cat=$('#clave_contr').html()
     // Obtener los checkboxes seleccionados
    let selectedRows = $('#tbodyRuralDetalle input[type="checkbox"]:checked');

    // Validar que al menos un checkbox esté seleccionado
    if (selectedRows.length === 0) {
        alertNotificar('Por favor, seleccione al menos un título de crédito.','error');
        return;  // Detener la ejecución si no hay ninguna selección
    } 
       
    vistacargando("m","Espere por favor");           

    $.ajaxSetup({
        headers: {
            'X-CSRF-TOKEN': $('meta[name="csrf-token"]').attr('content')
        }
    });
    
    $.ajax({
        type:'POST',
        url: "descarga-titulo-rural",
        data: { _token: $('meta[name="csrf-token"]').attr('content'),
            numTitulosSeleccionados:numTitulosSeleccionados,
            ordenTitulosSeleccionados:ordenTitulosSeleccionados,
            clave_cat:clave_cat,
            valorCobrado:valorCobrado
            
        },
        success: function(data){
            console.log(data)
            vistacargando("");                
            if(data.error==true){                       
                alertNotificar(data.mensaje,'error');
                return;                      
            }

            alertNotificar(data.mensaje,'success');
            $('#modalContri').modal('hide')
            window.location.href="descargar-reporte/"+data.pdf
            
        }, error:function (data) {
            vistacargando("");
            alertNotificar('Ocurrió un error','error');
        }
    });

       
}

function validarSeleccionCorrelativa(ordenTitulosSeleccionados) {

    if (ordenTitulosSeleccionados.length === 0) {
        swal("Debe seleccionar al menos un título.", "", "warning");
        return false;
    }

    // Convertir a números y ordenar
    const orden = ordenTitulosSeleccionados.map(Number).sort((a, b) => a - b);

    // El máximo seleccionado debe incluir todos los anteriores
    const max = orden[orden.length - 1];

    let faltantes = [];

    for (let i = 1; i <= max; i++) {
        if (!orden.includes(i)) {
            faltantes.push(i);
        }
    }

    if (faltantes.length > 0) {

        swal({
            title: "Selección inválida",
            // text: "Debe seleccionar también: " + faltantes.join(", "),
             text: "Debe seleccionar los titulos en orden ascedente ",
            type: "warning"
        });

        return false;
    }

    return true;
}

function cerrarModalPago(){
    $('#modalContri').modal('hide')
}

