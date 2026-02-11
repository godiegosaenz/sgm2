function cambiaTipo(){
    limpiaTabla()
    limpiarBusqueda()
    var tipo=$('#tipo_busq').val()
    $('#div_cedula').show()
   
    $('#div_nombres').hide()
    if(tipo==""){return}
    else if(tipo=="1"){
        $('#div_cedula').show()
       
        $('#div_nombres').hide()
    }else{
        $('#div_cedula').hide()
        $('#div_nombres').show()
       
    }
}

function cancelaAtencion(){
    if(confirm('¿Quiere cancelar la atencion?')){
        $('#busqueda_paciente').show(200)
        $('#atencion_paciente').hide(200)
    }
}

function validaSeccionI(){
    let tamanio_act_extra=$('#tbodyActividadExtra tr').length;
    if(tamanio_act_extra==0){
        alertNotificar("Debe ingresar al menos una actividad extra")
        return false
    }
    return true
}
globalThis.CantDiag=0
function validaSeccionK(){

    let ttamanio_diagnostico=$('#tbodyDiagnostico tr').length;
    if(ttamanio_diagnostico==0){
        alertNotificar("Debe ingresar al menos un diagnostico","error")
        return false
    }

    if(CantDiag==0){
        alertNotificar("Debe ingresar al menos un diagnostico","error")
        return false
    }
    return true
}

function guardaVitales1() {
    alert("ss");
    return Promise.resolve();
}



async function guardarHistoriaCompleta() {
    try {
                  
        await guardarSeccionA();    // A
        await guardarMotivo();   // B
        await guardarAntecedentes();                // C
        await guardarEnfermedad();                // D
        await guardaVitales();  // E
        await guardarExamenFisico();                // F
        await guardarObservacionResultados();                // J
        await guardarAptitudesMedi();            // L
        await guardarRecomendaciones();          // M
        await guardarRetiro();                   // N

        $('#busqueda_paciente').show(200)
        $('#atencion_paciente').hide(200)

        alertNotificar("Historia guardada correctamente", "success");

    } catch (error) {
        console.error(error);
        alertNotificar("Proceso detenido por error", "error");
    }
}


function finalizarAtencion(){
    if(confirm('¿Deseas finalizar la atencion?')){
        $('.accordion-collapse').collapse('hide');
        setTimeout(function() {
        
            if(!validaSeccionA()){
                abrirAcordion('collapseA');
                return; // ⛔ se detiene aquí
            }
            

            if(!validaSeccionB()){
                abrirAcordion('collapseB');
                return; // ⛔ se detiene aquí
            }
        

            if(!validaSeccionC()){
                abrirAcordion('collapseC');
                return; // ⛔ se detiene aquí
            }

        
            if(!validaSeccionD()){
                abrirAcordion('collapseD');
                return; // ⛔ se detiene aquí
            }

            

            if(!validaSeccionE()){
                abrirAcordion('collapseE');
                return; // ⛔ se detiene aquí
            }

            

            if(!validaSeccionF()){
                abrirAcordion('collapseF');
                return; // ⛔ se detiene aquí
            } 

        

            if(!validaSeccionJ()){
                abrirAcordion('collapseJ');
                return; // ⛔ se detiene aquí
            }

            

            
            if(!validaSeccionK()){
                abrirAcordion('collapseK');
                return; // ⛔ se detiene aquí
            }
        
            if(!validaSeccionL()){
                abrirAcordion('collapseL');
                return; // ⛔ se detiene aquí
            }

        

            if(!validaSeccionM()){
                abrirAcordion('collapseM');
                return; // ⛔ se detiene aquí
            }      

            if(!validaSeccionN()){
                abrirAcordion('collapseN');
                return; // ⛔ se detiene aquí
            }

            // $("#formActualizaSeccionA").submit(); //seccion A
            // $("#formMotivo").submit() //seccion B
            // $("#formAntecedentes").submit() //seccion C
            // $("#formEnfermedad").submit() // seccion D
            // $("#formConstanteVitales").submit() //seccion E
            // $("#formExamenFisico").submit() //seccion F
            // $("#formObservacionResultados").submit() //seccion J
            // guardarAptitudesMedi() //secccion L
            // guardarRecomendaciones() //seccion M
            // guardarRetiro() //seccion N

            guardarHistoriaCompleta()

        }, 500)
    }  
}

function abrirAcordion(idCollapse){
    // cerrar todos (opcional)
    // $('.accordion-collapse').collapse('hide');

    // abrir el que falló
    $('#' + idCollapse).collapse('show');

    // scroll suave hacia la sección
    $('html, body').animate({
        scrollTop: $('#' + idCollapse).offset().top - 100
    }, 500);
}


function limpiarBusqueda(){
    $('#cedula').val('')
    $('#clave').val('')
    $('#cmb_nombres').val('')
}


function limpiarBusquedaU(){
    $('#cedula_emp').val('')
    $('#nombre_empl').val('')

    
}
function limpiaTabla(){
    $("#tbodyPaciente").html('');
	$("#tbodyPaciente").append(`<tr><td colspan="6" style="text-align:center">No existen registros</td></tr>`);
}
function cambiaTipoU(){
    limpiaTabla()
    limpiarBusquedaU()
    var tipo=$('#tipo_busq').val()
    $('#div_cedula_emp').show()
   
    $('#div_nombres_urb').hide()
    if(tipo==""){return}
    else if(tipo=="1"){
        $('#div_cedula_emp').show()
       
        $('#div_nombres').hide()
    }else if(tipo=="2"){
        $('#div_cedula_emp').hide()
        $('#div_nombres').show()
       
    }
}

function abrirModalPaciente(){
    $('#modalCrearPaciente').modal('show')
}

function abrirModalMedidasPreventivas(){
    $('#modalMedida').modal('show')
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


$('#modalCrearPaciente').on('hidden.bs.modal', function (e) {
    $('#cedula_empleado').val('')
    $('#primer_apellido').val('')
    $('#segundo_apellido').val('')
    $('#primer_nombre').val('')
    $('#segundo_nombre').val('')
    $('#fecha_nacimiento').val('')
    $('#correo').val('')
    $('#telefono').val('')
    $('#fecha_nacimiento').val('')
    $('#correo').val('')
    $('#sexo').val('')
    $('#grupo_sanguineo').val('')
    $('#lateridad').val('')

    $('.check_atencion_prio').prop('checked',false)

    $('#nombre_btn_persona').html('Guardar')

});

$('#modalCrearPaciente').on('shown.bs.modal', function (e) {
   $('#nombre_btn_persona').html('Guardar')
   $('#btn_guarda_act_persona').removeClass('btn-warning');
   $('#btn_guarda_act_persona').addClass('btn-primary');
});

function guardarPersona(){
    let cedula=$('#cedula_empleado').val()
    let primer_apellido=$('#primer_apellido').val()
    let segundo_apellido=$('#segundo_apellido').val()
    let primer_nombre=$('#primer_nombre').val()
    let segundo_nombre=$('#segundo_nombre').val()
    let fecha_nacimiento=$('#fecha_nacimiento').val()
    let correo=$('#correo').val()
    let telefono=$('#telefono').val()
    let sexo=$('#sexo').val()
    let grupo_sanguineo=$('#grupo_sanguineo').val()
    let lateridad=$('#lateridad').val()

    if(cedula=="" || cedula==null){
        alertNotificar("Debe ingresar la cedula","error")
        $('#cedula').focus()
        return
    } 

    if(primer_apellido=="" || primer_apellido==null){
        alertNotificar("Debe ingresar el primer apellido","error")
        $('#primer_apellido').focus()
        return
    } 

    if(segundo_apellido=="" || segundo_apellido==null){
        alertNotificar("Debe ingresar el segundo apellido","error")
        $('#segundo_apellido').focus()
        return
    } 

    if(primer_nombre=="" || primer_nombre==null){
        alertNotificar("Debe ingresar el primer nombre","error")
        $('#primer_nombre').focus()
        return
    } 

    if(segundo_nombre=="" || segundo_nombre==null){
        alertNotificar("Debe ingresar el segundo nombre","error")
        $('#segundo_nombre').focus()
        return
    } 

    if(fecha_nacimiento=="" || fecha_nacimiento==null){
        alertNotificar("Debe ingresar la fecha nacimiento","error")
        $('#fecha_nacimiento').focus()
        return
    } 

    if(sexo=="" || sexo==null){
        alertNotificar("Debe seleccionar el  sexo","error")
        return
    } 

    if(grupo_sanguineo=="" || grupo_sanguineo==null){
        alertNotificar("Debe ingresar grupo_sanguineo","error")
        $('#grupo_sanguineo').focus()
        return
    } 
    
    if(lateridad=="" || lateridad==null){
        alertNotificar("Debe ingresar lateridad","error")
        $('#lateridad').focus()
        return
    } 

    if(telefono=="" || telefono==null){
        alertNotificar("Debe ingresar el telefono","error")
        $('#telefono').focus()
        return
    } 


    vistacargando("m","Espere por favor")
    $.ajaxSetup({
        headers: {
            'X-CSRF-TOKEN': $('meta[name="csrf-token"]').attr('content')
        }
    });

    //comprobamos si es registro o edicion
    let tipo="POST"
    let url_form="guardar-empleado-paciente"
    
    var FrmData=$("#formPersona").serialize();

    $.ajax({
            
        type: tipo,
        url: url_form,
        method: tipo,             
		data: FrmData,      
		
        processData:false, 

        success: function(data){
            vistacargando("");                
            if(data.error==true){
                alertNotificar(data.mensaje,'error');
                return;                      
            }
            alertNotificar(data.mensaje,'success');
            setTimeout(function() {
                $('#modalCrearPaciente').modal('hide')
            }, 2000)
                            
        }, error:function (data) {
            console.log(data)

            vistacargando("");
            alertNotificar('Ocurrió un error','error');
        }
    });
}

globalThis.PersonaIdEditar=0
function capturaInfoPersona(){
    var ci_ruc=$('#cedula_empleado').val()
    if(ci_ruc==""){return}
    
    $('#primer_apellido').val('')
    $('#segundo_apellido').val('')
    $('#primer_nombre').val('')
    $('#segundo_nombre').val('')
    $('#fecha_nacimiento').val('')
    $('#correo').val('')
    $('#telefono').val('')
    $('#sexo').val('')
    $('#grupo_sanguineo').val('')
    $('#lateridad').val('')
    $('#nombre_btn_persona').html('')
    $('#btn_guarda_act_persona').prop('disabled',false)

    // vistacargando("m","Espere por favor")
    $.get("carga-info-paciente/"+ci_ruc, function(data){
        vistacargando("")
        
       
        if(data.error==true){
            // alertNotificar(data.mensaje,"error");
            $('#primer_apellido').val(data.resultado.primer_apellido)
            $('#segundo_apellido').val(data.resultado.segundo_apellido)
            $('#primer_nombre').val(data.resultado.primer_nombre)
            $('#segundo_nombre').val(data.resultado.segundo_nombre)
            $('#fecha_nacimiento').val(data.resultado.fecha_nacimiento)
            $('#correo').val(data.resultado.correo)
            $('#telefono').val(data.resultado.telefono)
            $('#sexo').val(data.resultado.sexo)
            $('#grupo_sanguineo').val(data.resultado.grupo_sanguinedad)
            $('#lateridad').val(data.resultado.lateridad)
            
            if(data.resultado.embarazada){
                $('#embarazada').prop('checked',true)
            }

            if(data.resultado.discapacidad){
                $('#discapacidad').prop('checked',true)
            }

            if(data.resultado.ecatastrofica){
                $('#ecatastrofica').prop('checked',true)
            }

            if(data.resultado.lactancia){
                $('#lactancia').prop('checked',true)
            }

            if(data.resultado.mayor_edad){
                $('#mayor_edad').prop('checked',true)
            }
          
            return;   
        }
        if(data.resultado!=null){
            PersonaIdEditar=data.resultado.id
        }
           
        
        if(PersonaIdEditar>0){
            $('#nombre_btn_persona').html('Actualizar')
            $('#btn_guarda_act_persona').removeClass('btn-primary');
            $('#btn_guarda_act_persona').addClass('btn-warning');
        }else{
            $('#nombre_btn_persona').html('Guardar')
            $('#btn_guarda_act_persona').removeClass('btn-warning');
            $('#btn_guarda_act_persona').addClass('btn-primary');
        }

        $('#primer_apellido').val(data.resultado.primer_apellido)
        $('#segundo_apellido').val(data.resultado.segundo_apellido)
        $('#primer_nombre').val(data.resultado.primer_nombre)
        $('#segundo_nombre').val(data.resultado.segundo_nombre)
        $('#fecha_nacimiento').val(data.resultado.fecha_nacimiento)
        $('#correo').val(data.resultado.correo)
        $('#telefono').val(data.resultado.telefono)
        $('#sexo').val(data.resultado.sexo)
        $('#grupo_sanguineo').val(data.resultado.grupo_sanguinedad)
        $('#lateridad').val(data.resultado.lateralidad)

        if(data.resultado.embarazada){
            $('#embarazada').prop('checked',true)
        }

        if(data.resultado.discapacidad){
            $('#discapacidad').prop('checked',true)
        }

        if(data.resultado.ecatastrofica){
            $('#ecatastrofica').prop('checked',true)
        }

        if(data.resultado.lactancia){
            $('#lactancia').prop('checked',true)
        }

        if(data.resultado.mayor_edad){
            $('#mayor_edad').prop('checked',true)
        }
        console.log(data)
       
         
    }).fail(function(){
        // vistacargando("")
        alertNotificar("Se produjo un error, por favor intentelo más tarde","error");  
    });
}

function bloqueaInpust(input){
    $('#btn_guarda_act_persona').prop('disabled',true)
}


function buscaContribuyente(){
    
    $("#tbodyPaciente").html('');
    $('#tbodyPaciente').empty();     
    var valor=""
    var tipo_busq=$('#tipo_busq').val()
    var tipo_=$('#tipo').val()
    
    if(tipo_busq==""){
        alertNotificar("Debe seleccionar el tipo persona","error")
        return
    }

    if(tipo_busq==1){
        valor=$('#cedula_emp').val()
        if(valor==""){
            alertNotificar("Debe ingresar el numero de cedula del empleado","error")
            return
        }
    }

    if(tipo_busq==2){
        valor=$('#nombre_empl').val()
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

  
    url_form="buscar-info-empleado"

    let FrmData = {
        // tipo_per:tipo_per,
        // tipo: tipo_,
        tipo_busq:tipo_busq,
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
                $('#tbodyPaciente').append(`<tr>
                <td colspan="6" style="text-align:center">No hay datos disponibles</td>`);
                alertNotificar(data.mensaje,'error');
                return;                      
            }
            console.log(data)
            if(data.resultado.length==0){
                $('#tbodyPaciente').append(`<tr>
                <td colspan="6" style="text-align:center">No hay datos disponibles</td>`);
                // alertNotificar('No se encontro informacion','error');
                return; 
            }
        
            // llenarData(data.data)
            $.each(data.resultado,function(i, item){
                $('#tbodyPaciente').append(`<tr>
                    <td style="width:9%; text-align:center; vertical-align:middle">
                        <button type="button" class="btn btn-success btn-sm" onclick="atencionPaciente('${item.id}')">
                            <i class="fa fa-user-md"></i>
                        </button> 
                        <button type="button" class="btn btn-primary btn-sm" onclick="verHistorial('${item.id}')">
                            <i class="fa fa-medkit"></i>
                        </button>                    
                    </td>
                    <td style="width:10%; text-align:center; vertical-align:middle">
                        ${item.cedula}                    
                    </td>
                    <td style="width:30%; text-align:center; vertical-align:middle">
                        ${item.nombre}                     
                    </td>
                    <td style="width:10%; text-align:center; vertical-align:middle">
                        ${item.telefono}            
                    </td>
                    <td style="width:10%; text-align:center; vertical-align:middle">
                        ${item.correo}                 
                    </td>
                   
                
                </tr>`);
            })
                            
        }, error:function (data) {
            console.log(data)

            vistacargando("");
            alertNotificar('Ocurrió un error','error');
            $('#tbodyPaciente').append(`<tr>
                <td colspan="6" style="text-align:center">No hay datos disponibles</td>`);
        }
    });
}

function verHistorial(id){
   

    $("#tablaHistorialPaciente tbody").html('');
    $('.detalle_hist').html('')

    $('#tablaHistorialPaciente').DataTable().destroy();
	$('#tablaHistorialPaciente tbody').empty(); 

    vistacargando("m","Espere por favor")
    $.get("historial-paciente-evolucion/"+id, function(data){
        vistacargando("")
               
        if(data.error==true){
            alertNotificar(data.mensaje,"error");
            return;   
        }

        $('#cedula_historial').html(data.resultado.cedula)
        $('#paciente_historial').html(data.resultado.primer_apellido +" "+data.resultado.segundo_apellido+" "+data.resultado.primer_nombre+" "+data.resultado.segundo_nombre)
        $('#fnacimiento_historial').html(data.resultado.fecha_nacimiento)
        $('#edad_historial').html(data.resultado.edad)
        $('#gsanguineo_historial').html(data.resultado.grupo_sanguinedad)
        $('#lateralidad_historial').html(data.resultado.lateralidad)

        $.each(data.motivo,function(i, item){

            // Construir los diagnósticos
            let diagnosticos = '';

            if(item.diagnosticos && item.diagnosticos.length > 0){
                diagnosticos = item.diagnosticos.map(d => d.nombre).join('<br>');
            }else{
                diagnosticos = 'Sin diagnóstico';
            }

            $('#tbodyHistorialPaciente').append(`<tr>
                <td style="width:9%; text-align:center; vertical-align:middle">
                    <button type="button" class="btn btn-success btn-sm" onclick="atencionPacientse('${item.id}')">
                        <i class="fa fa-file-pdf"></i>
                    </button> 
                                     
                </td>
                <td style="width:20%; text-align:center; vertical-align:middle">
                    ${item.motivo}                    
                </td>
                <td style="width:40%; text-align:center; vertical-align:middle">
                    ${diagnosticos}               
                </td>
                <td style="width:10%; text-align:center; vertical-align:middle">
                    ${item.fecha_registro}            
                </td>
                <td style="width:20%; text-align:center; vertical-align:middle">
                    ${item.profesional}                 
                </td>
                
            
            </tr>`);
        })

        cargar_estilos_datatable('tablaHistorialPaciente');
        $('#busqueda_paciente').hide(200)
        $('#historial_paciente').show(200)

    }).fail(function(){
        vistacargando("")
        alertNotificar("Se produjo un error, por favor intentelo más tarde","error");  
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

function regresarHistorial(){
    $('#historial_paciente').hide(200)
    $('#busqueda_paciente').show(200)
}

function atencionPaciente(id){
    $('.check_atencion_prio').prop('checked',false)
    $('.check_riesgo').prop('checked',false)
    $('.check_mujer').prop('checked',false)
    
    $('.form-check-input').prop('checked',false)
    $('.seccion_a').val('')
    $('.seccion_c').val('')
    $('.seccion_d').val('')
    $('.seccion_e').val('')
    $('.seccion_f').val('')
    $('.seccion_j').val('')
    $('.seccion_l').val('')
    $('.seccion_m').val('')
    $('.seccion_n').val('')

    $('#examenes-container .examen-item').not(':first').remove();
    $('#examenes-container-masculino .examen-item-masculino').not(':first').remove();
    limpiarSeccionMotivo()
    
    
    vistacargando("m","Espere por favor")
    $.get("carga-info-paciente-evolucion/"+id, function(data){
        vistacargando("")
        
       
        if(data.error==true){
            // alertNotificar(data.mensaje,"error");
            return;   
        }
        $('.persona_evaluada').html('')
        $('.persona_evaluada').html('Evaluacion Ocupacional -- '+data.resultado.primer_apellido+ " "+data.resultado.segundo_apellido
            +" "+data.resultado.primer_nombre+" "+data.resultado.segundo_nombre
        )
      
        $('#id_empleado').val(id)
        $('#num_cedula_empleado').val(data.resultado.cedula)
        $('#primer_apellido_empleado').val(data.resultado.primer_apellido)
        $('#segundo_apellido_empleado').val(data.resultado.segundo_apellido)
        $('#primer_nombre_empleado').val(data.resultado.primer_nombre)
        $('#segundo_nombre_empleado').val(data.resultado.segundo_nombre)
        $('#fecha_nacimiento_empleado').val(data.resultado.fecha_nacimiento)
        $('#edad_empleado').val(data.resultado.edad)
        $('#sexo_empleado').val(data.resultado.sexo)
        $('#grupo_sanguineo_empleado').val(data.resultado.grupo_sanguinedad)
        $('#lateralidad_empleado').val(data.resultado.lateralidad)

        // $('#puesto_cmb').val(data.motivo.id_puesto)
        $('#fecha_atencion').val(data.fecha_atencion)

        if(data.motivo!=null){
            $('#fecha_ingreso').val(data.motivo.fecha_ingreso_trabajo)
            $('#fecha_reingreso').val(data.motivo.fecha_reintegro)
            $('#fecha_ultimo_dia').val(data.motivo.fecha_ultimo_dia_laboral)
            cargaComboPuesto(data.motivo.id_puesto)
        }else{
            cargaComboPuesto()
        }

        if(data.resultado.embarazada){
            $('#embarazada_e').prop('checked',true)
        }

        if(data.resultado.discapacidad){
            $('#discapacidad_e').prop('checked',true)
        }

        if(data.resultado.ecatastrofica){
            $('#ecatastrofica_e').prop('checked',true)
        }

        if(data.resultado.lactancia){
            $('#lactancia_e').prop('checked',true)
        }

        if(data.resultado.mayor_edad){
            $('#mayor_edad_e').prop('checked',true)
        }
        

        console.log(data)

        $('#busqueda_paciente').hide(200)
        $('#atencion_paciente').show(200)
        seleccionSexo()

        // $.each(data.factores_riesgo, function(i,item){
        //   	$('#'+item.codigo).prop('checked',true)
        // })

        llenarTablaMedida(id)
        llenarTablaActividad(id)
        llenarTablaActividadExtras(id)
        llenarTablaResultadoExamen(id)
        llenarTablaDiagnostico(id)
         
    }).fail(function(){
        vistacargando("")
        alertNotificar("Se produjo un error, por favor intentelo más tarde","error");  
    });
}

function volverBusqueda(){
    $('#atencion_paciente').hide(200)
    $('#busqueda_paciente').show(200)
}


document.addEventListener('click', function (e) {

  /* AGREGAR */
  if (e.target.classList.contains('btnAgregar')) {

    const container = document.getElementById('examenes-container');
    const item = e.target.closest('.examen-item');
    const clone = item.cloneNode(true);

    // Limpiar valores
    clone.querySelectorAll('input, textarea').forEach(el => el.value = '');

    // Quitar botón agregar del clon
    clone.querySelector('.btnAgregar').remove();

    // Crear botón eliminar
    const btnEliminar = document.createElement('button');
    btnEliminar.type = 'button';
    btnEliminar.className = 'btn btn-danger btnEliminar';
    btnEliminar.innerHTML = '🗑';

    // Insertar botón eliminar
    clone.querySelector('.d-flex').appendChild(btnEliminar);

    container.appendChild(clone);
  }

  /* ELIMINAR */
  if (e.target.classList.contains('btnEliminar')) {
    e.target.closest('.examen-item').remove();
  }



  if (e.target.classList.contains('btnAgregarMasculino')) {

    const container = document.getElementById('examenes-container-masculino');
    const item = e.target.closest('.examen-item-masculino');
    const clone = item.cloneNode(true);

    // Limpiar valores
    clone.querySelectorAll('input, textarea').forEach(el => el.value = '');

    // Quitar botón agregar del clon
    clone.querySelector('.btnAgregarMasculino').remove();

    // Crear botón eliminar
    const btnEliminarMasculino = document.createElement('button');
    btnEliminarMasculino.type = 'button';
    btnEliminarMasculino.className = 'btn btn-danger btnEliminarMasculino';
    btnEliminarMasculino.innerHTML = '🗑';

    // Insertar botón eliminar
    clone.querySelector('.d-flex').appendChild(btnEliminarMasculino);

    container.appendChild(clone);
  }

  /* ELIMINAR */
  if (e.target.classList.contains('btnEliminarMasculino')) {
    e.target.closest('.examen-item-masculino').remove();
  }

  if (e.target.classList.contains('btnAgregarConsumo')) {

    const container = document.getElementById('examenes-container-consumo_sustancias');
    const item = e.target.closest('.examen-item-consumo');
    const clone = item.cloneNode(true);

    // Limpiar valores
    clone.querySelectorAll('input, textarea').forEach(el => el.value = '');

    // Quitar botón agregar del clon
    clone.querySelector('.btnAgregarConsumo').remove();

    // Crear botón eliminar
    const btnEliminarConsumo = document.createElement('button');
    btnEliminarConsumo.type = 'button';
    btnEliminarConsumo.className = 'btn btn-danger btnEliminarConsumo';
    btnEliminarConsumo.innerHTML = '🗑';

    // Insertar botón eliminar
    clone.querySelector('.d-flex').appendChild(btnEliminarConsumo);

    container.appendChild(clone);
  }

  /* ELIMINAR */
    if (e.target.classList.contains('btnEliminarConsumo')) {
        e.target.closest('.examen-item-consumo').remove();
    }

    if (e.target.classList.contains('tipo-consumo')) {

        const valor = e.target.value;
        const bloque = e.target.closest('.examen-item-consumo');

        if (!bloque) return;

        const seccion = bloque.querySelector('.seccion-otra-sustancia');
        if (!seccion) return;

        if (valor === 'Otras') {
        seccion.style.display = 'flex'; // porque es row
        } else {
        seccion.style.display = 'none';

        // Limpiar textarea
        const textarea = seccion.querySelector('textarea');
        if (textarea) textarea.value = '';
        }
    }

});
function resetearTxtTratamientoOrmonal(){
    $('#seccion_txt_tratamiento_ormonal').hide()
    $('#txt_tratamiento_ormonal').val('')
}
function seleccionTratamientoOrmonal(){
    resetearTxtTratamientoOrmonal()
    var valor=$('#tratamiento_ormonal').val()
    if(valor==""){return}
    else if(valor=='Si'){$('#seccion_txt_tratamiento_ormonal').show()}
}

function resetearTxtMetodoPlanificacionfamiliar(){
    $('#seccion_txt_metodo_planificcion_familiar').hide()
    $('#txt_metodo_planificcion_familiar').val('')
}
function seleccionMetodoPlanificacionFamiliar(){
    resetearTxtMetodoPlanificacionfamiliar()
    var valor=$('#metodo_planificacion').val()
    if(valor==""){return}
    else if(valor=='Si'){$('#seccion_txt_metodo_planificcion_familiar').show()}
}

function resetearTxtMetodoPlanificacionfamiliarMasculino(){
    $('#seccion_txt_metodo_planificcion_familiar_masculino').hide()
    $('#txt_metodo_planificcion_familiar_masculino').val('')
}
function seleccionMetodoPlanificacionFamiliarMasculino(){
    resetearTxtMetodoPlanificacionfamiliarMasculino()
    var valor=$('#metodo_planificacion_masculino').val()
    if(valor==""){return}
    else if(valor=='Si'){$('#seccion_txt_metodo_planificcion_familiar_masculino').show()}
}

function cambioTipo(select) {
  const valor = select.value;
  console.log('Valor seleccionado:', valor);

  if (valor === 'si') {
    console.log('Mostrar campos de consumo');
  } else if (valor === 'no') {
    console.log('Ocultar campos de consumo');
  }
}

function validaSeccionA(){
    let cedula=$('#num_cedula_empleado').val()
    let primer_apellido=$('#primer_apellido_empleado').val()
    let segundo_apellido=$('#segundo_apellido_empleado').val()
    let primer_nombre=$('#primer_nombre_empleado').val()
    let segundo_nombre=$('#segundo_nombre_empleado').val()
    let fecha_nacimiento=$('#fecha_nacimiento_empleado').val()
    let sexo=$('#sexo_empleado').val()
    let grupo_sanguineo=$('#grupo_sanguineo_empleado').val()
    let lateridad=$('#lateralidad_empleado').val()

    if(cedula=="" || cedula==null){
        alertNotificar("Debe ingresar la cedula","error")
        $('#num_cedula_empleado').focus()
        return fasel
    } 

    if(primer_apellido=="" || primer_apellido==null){
        alertNotificar("Debe ingresar el primer apellido","error")
        $('#primer_apellido_empleado').focus()
        return false
    } 

    if(segundo_apellido=="" || segundo_apellido==null){
        alertNotificar("Debe ingresar el segundo apellido","error")
        $('#segundo_apellido_empleado').focus()
        return false
    } 

    if(primer_nombre=="" || primer_nombre==null){
        alertNotificar("Debe ingresar el primer nombre","error")
        $('#primer_nombre_empleado').focus()
        return false
    } 

    if(segundo_nombre=="" || segundo_nombre==null){
        alertNotificar("Debe ingresar el segundo nombre","error")
        $('#segundo_nombre_empleado').focus()
        return false
    } 

    if(fecha_nacimiento=="" || fecha_nacimiento==null){
        alertNotificar("Debe ingresar la fecha nacimiento","error")
        $('#fecha_nacimiento_empleado').focus()
        return false
    } 

    if(sexo=="" || sexo==null){
        alertNotificar("Debe seleccionar el  sexo","error")
        return false
    } 

    if(grupo_sanguineo=="" || grupo_sanguineo==null){
        alertNotificar("Debe ingresar grupo_sanguineo","error")
        $('#grupo_sanguineo_empleado').focus()
        return false
    } 
    
    if(lateridad=="" || lateridad==null){
        alertNotificar("Debe ingresar lateridad","error")
        $('#lateralidad_empleado').focus()
        return false
    } 
    return true
}

// $("#formActualizaSeccionA").submit(function(e){
//     e.preventDefault();

//     // if(!validaSeccionA()){
//     //     return; // ⛔ se detiene aquí
//     // }

//     vistacargando("m","Actualizando...")
//     $.ajaxSetup({
//         headers: {
//             'X-CSRF-TOKEN': $('meta[name="csrf-token"]').attr('content')
//         }
//     });

//     //comprobamos si es registro o edicion
//     let tipo="POST"
//     let url_form="actualiza-seccion-a"
    
//     var FrmData=$("#formActualizaSeccionA").serialize();

//     $.ajax({
            
//         type: tipo,
//         url: url_form,
//         method: tipo,             
// 		data: FrmData,      
		
//         processData:false, 

//         success: function(data){
//             vistacargando("");                
//             if(data.error==true){
//                 alertNotificar(data.mensaje,'error');
//                 return;                      
//             }
//             alertNotificar(data.mensaje,'success');
           
                            
//         }, error:function (data) {
//             console.log(data)

//             vistacargando("");
//             alertNotificar('Ocurrió un error','error');
//         }
//     });
// })

function guardarSeccionA() {
    return new Promise((resolve, reject) => {

        // if (!validaSeccionA()) {
        //     reject("Validación Sección A");
        //     return;
        // }

        vistacargando("m","Actualizando...");

        $.ajax({
            type: "POST",
            url: "actualiza-seccion-a",
            data: $("#formActualizaSeccionA").serialize(),

            success: function (data) {
                vistacargando("");

                if (data.error === true) {
                    alertNotificar(data.mensaje, 'error');
                    reject(data.mensaje);
                    return;
                }

                alertNotificar(data.mensaje, 'success');
                resolve(data); // ✅ TERMINÓ BIEN
            },
            error: function (err) {
                vistacargando("");
                alertNotificar('Ocurrió un error', 'error');
                reject(err);
            }
        });
    });
}


function calculaEdad(){
    let fecha_nacimiento=$('#fecha_nacimiento_empleado').val()
   
    $.get("calcula-edad/"+fecha_nacimiento, function(data){
        vistacargando("")
        if(data.error==true){
            alertNotificar(data.mensaje,"error");          
            return;   
        }
        
        $('#edad_empleado').val(data.edad)
       
    }).fail(function(){
        // vistacargando("")
        alertNotificar("Se produjo un error, por favor intentelo más tarde","error");  
    });
}

function abrirModalPuesto(){
    $('#modalCrearPuesto').modal('show')
}

function guardarPuesto(){
    let nombre_puesto=$('#nombre_puesto').val()
    
    if(nombre_puesto=="" || nombre_puesto==null){
        alertNotificar("Debe ingresar el nombre_puesto","error")
        $('#nombre_puesto').focus()
        return
    } 

    vistacargando("m","Espere por favor")
    $.ajaxSetup({
        headers: {
            'X-CSRF-TOKEN': $('meta[name="csrf-token"]').attr('content')
        }
    });

    //comprobamos si es registro o edicion
    let tipo="POST"
    let url_form="guardar-puesto"
    
    var FrmData=$("#formPuesto").serialize();

    $.ajax({
            
        type: tipo,
        url: url_form,
        method: tipo,             
		data: FrmData,      
		
        processData:false, 

        success: function(data){
            vistacargando("");                
            if(data.error==true){
                alertNotificar(data.mensaje,'error');
                return;                      
            }
            alertNotificar(data.mensaje,'success');
            $('#nombre_puesto').val('')
            cargaComboPuesto()               
        }, error:function (data) {
            console.log(data)

            vistacargando("");
            alertNotificar('Ocurrió un error','error');
        }
    });
}

$('#modalCrearPuesto').on('hidden.bs.modal', function (e) {
    $('#nombre_puesto').val('')
    
});

function cargaComboPuesto(seleccionado=null){
    vistacargando("m","");
    $.get('carga-combo-puesto', function(data){
        console.log(data)
        vistacargando("")
        if(data.error==true){
			alertNotificar(data.mensaje,"error");
			return;   
		}
        $('#puesto_cmb').html('');	
        $('#puesto_cmb').find('option').remove().end();
        $('#puesto_cmb').append('<option value="">Selecccione un puesto</option>');
        $.each(data.resultado, function(i,item){
          	if(seleccionado != null && seleccionado == item.id){
                $('#puesto_cmb').append('<option value="'+item.id+'" selected>'+item.descripcion+'</option>');
            } else {
                // Si 'seleccionado' es null, añadimos todas las opciones sin seleccionar
                $('#puesto_cmb').append('<option value="'+item.id+'">'+item.descripcion+'</option>');
            }
        })
         $("#puesto_cmb").trigger("chosen:updated"); // actualizamos el combo 
          
        
    }).fail(function(){
        alertNotificar("Ocurrio un error","error");
        vistacargando("")
       
    }); 
}

function seleccionSexo(){
    var seleccion=$('#sexo_empleado').val()
    if(seleccion=='Hombre'){
        $('#seccion_masculino').show()
        $('#seccion_femenino').hide()
    }else{
        $('#seccion_femenino').show()
        $('#seccion_masculino').hide()
    }
}
function limpiarSeccionMotivo(){
    $('#id_empleado').val('')
    $('#puesto_cmb').val('')
    $('#fecha_atencion').val('')
    $('#fecha_ingreso').val('')
    $('#fecha_reingreso').val('')
    $('#fecha_ultimo_dia').val('')
    $('#tipo_atencion').val('')
    $('#motivo').val('')
}

function validaSeccionB(){
    let puesto_cmb=$('#puesto_cmb').val()
    let fecha_atencion=$('#fecha_atencion').val()
    let tipo_atencion=$('#tipo_atencion').val()
    let motivi_atencion=$('#motivo').val()
 
    if(puesto_cmb=="" || puesto_cmb==null){
        alertNotificar("Debe seleccionar el puesto_cmb","error")
        return false
    } 

    if(fecha_atencion=="" || fecha_atencion==null){
        alertNotificar("Debe ingresar la fecha_atencion","error")
        $('#fecha_atencion').focus()
        return false
    } 

    if(tipo_atencion=="" || tipo_atencion==null){
        alertNotificar("Debe seleccionar el tipo_atencion","error")
        return false
    } 

    if(motivi_atencion=="" || motivi_atencion==null){
        alertNotificar("Debe ingresar el motivo","error")
        $('#motivi_atencion').focus()
        return false
    } 
    
    return true
} 
globalThis.IdCabeceraAtencion=0
// $("#formMotivo").submit(function(e){
//     e.preventDefault();
//     let id_empleado=$('#id_empleado').val()
    
//     // if(!validaSeccionB()){
//     //     return; // ⛔ se detiene aquí
//     // }
    
//     vistacargando("m","Registrando...")
//     $.ajaxSetup({
//         headers: {
//             'X-CSRF-TOKEN': $('meta[name="csrf-token"]').attr('content')
//         }
//     });

//     //comprobamos si es registro o edicion
//     let tipo="POST"
//     let url_form="guardar-motivo"
    
//     var FrmData=$("#formMotivo").serialize();
//     FrmData += "&id_empleado=" + id_empleado;

//     $.ajax({
            
//         type: tipo,
//         url: url_form,
//         method: tipo,             
// 		data: FrmData,      
		
//         processData:false, 

//         success: function(data){
//             vistacargando("");                
//             if(data.error==true){
//                 alertNotificar(data.mensaje,'error');
//                 return;                      
//             }
//             alertNotificar(data.mensaje,'success');
//             IdCabeceraAtencion=data.idcabecera
//             alert("formMotivo "+IdCabeceraAtencion)
                            
//         }, error:function (data) {
//             console.log(data)

//             vistacargando("");
//             alertNotificar('Ocurrió un error','error');
//         }
//     });
// })

function guardarMotivo() {
    return new Promise((resolve, reject) => {

        let id_empleado = $('#id_empleado').val();

        // if (!validaSeccionB()) {
        //     reject("Validación Sección B");
        //     return;
        // }

        vistacargando("m","Registrando...");

        let FrmData = $("#formMotivo").serialize();
        FrmData += "&id_empleado=" + id_empleado;

        $.ajax({
            type: "POST",
            url: "guardar-motivo",
            data: FrmData,

            success: function (data) {
                vistacargando("");

                if (data.error === true) {
                    alertNotificar(data.mensaje, 'error');
                    reject(data.mensaje);
                    return;
                }

                alertNotificar(data.mensaje, 'success');

                // 🔥 ESTE ES EL DATO CLAVE
                IdCabeceraAtencion = data.idcabecera;

                console.log("formMotivo", IdCabeceraAtencion);

                resolve(data); // ✅ TERMINÓ BIEN
            },
            error: function (err) {
                console.log(err);
                vistacargando("");
                alertNotificar('Ocurrió un error', 'error');
                reject(err);
            }
        });
    });
}


function validaSeccionC(){
    let antecedente_cq=$('#antecedente_cq').val()
    let antecedente_familiares=$('#antecedente_familiares').val()
    let autoriza_transfusion=$('#autoriza_transfusion').val()
    let tratamiento_ormonal=$('#tratamiento_ormonal').val()
    let txt_tratamiento_ormonal=$('#txt_tratamiento_ormonal').val()
    let observacion_antecedentes=$('#observacion_antecedentes').val()
 
    if(antecedente_cq=="" || antecedente_cq==null){
        alertNotificar("Debe ingresar el antecedentes clinicos quirurgicos","error")
        $('#antecedente_cq').focus()
        return false
    } 

    if(antecedente_familiares=="" || antecedente_familiares==null){
        alertNotificar("Debe ingresar la antecedente_familiares","error")
        $('#antecedente_familiares').focus()
        return false
    } 

    if(autoriza_transfusion=="" || autoriza_transfusion==null){
        alertNotificar("Debe seleccionar autoriza_transfusion","error")
        return false
    }
    
    if(tratamiento_ormonal=="" || tratamiento_ormonal==null){
        alertNotificar("Debe seleccionar si se encuentra bajo un tratamiento_ormonal","error")
        return false
    }

    if(tratamiento_ormonal=='Si'){
        if(txt_tratamiento_ormonal=="" || txt_tratamiento_ormonal==null){
            alertNotificar("Debe ingresar cual tratamiento hormonal","error")
            $('#txt_tratamiento_ormonal').focus()
            return false
        }
    }


    var seleccion=$('#sexo_empleado').val()
    
    if(seleccion=='Hombre'){
        var metodo_planificacion_masculino=$('#metodo_planificacion_masculino').val()
        var cual_metodo=$('#txt_metodo_planificcion_familiar_masculino').val()
        if(metodo_planificacion_masculino=="" || metodo_planificacion_masculino==null){
            alertNotificar("Debe seleccionar metodo_planificacion_masculino","error")
            return false
        }
        if(metodo_planificacion_masculino=='Si'){
            if(cual_metodo=="" || cual_metodo==null){
                alertNotificar("Debe ingresar cual_metodo","error")
                $('#txt_metodo_planificcion_familiar_masculino').focus()
                return false
            }
        }
    }else{
        var metodo_planificacion=$('#metodo_planificacion').val()
        var cual_metodo=$('#txt_metodo_planificcion_familiar').val()
        var fecha_ultima_menstruacion=$('#fecha_ultima_menstruacion').val() 
        if(fecha_ultima_menstruacion=="" || fecha_ultima_menstruacion==null){
            alertNotificar("Debe seleccionar fecha_ultima_menstruacion","error")
            return false
        }  
        if(metodo_planificacion=="" || metodo_planificacion==null){
            alertNotificar("Debe seleccionar metodo_planificacion","error")
            return false
        }
        if(metodo_planificacion=='Si'){
            if(cual_metodo=="" || cual_metodo==null){
                alertNotificar("Debe ingresar cual_metodo","error")
                $('#txt_metodo_planificcion_familiar').focus()
                return false
            }
        }

        if(observacion_antecedentes=="" || observacion_antecedentes==null){
            alertNotificar("Debe ingresar observacion_antecedentes","error")
            $('#observacion_antecedentes').focus()
            return false
        }
       
    }

    return true

}

globalThis.IdAntecedentesRegistrado=0
$("#formAntecedentes").submit(function(e){
    e.preventDefault();
    let id_empleado=$('#id_empleado').val()
    var seleccion=$('#sexo_empleado').val()

    // if(!validaSeccionC()){
    //     return; // ⛔ se detiene aquí
    // }
    alert("forante "+IdAntecedentesRegistrado)
    return
    vistacargando("m","Registrando...")
    $.ajaxSetup({
        headers: {
            'X-CSRF-TOKEN': $('meta[name="csrf-token"]').attr('content')
        }
    });

    //comprobamos si es registro o edicion
    let tipo="POST"
    let url_form="guardar-antecedentes"
    
    var FrmData=$("#formAntecedentes").serialize();
    FrmData += "&id_empleado=" + id_empleado;
    FrmData += "&hombre_mujer=" + seleccion;
    FrmData += "&IdCabeceraAtencion=" +IdCabeceraAtencion;

    $.ajax({
            
        type: tipo,
        url: url_form,
        method: tipo,             
		data: FrmData,      
		
        processData:false, 

        success: function(data){
            vistacargando("");                
            if(data.error==true){
                alertNotificar(data.mensaje,'error');
                return;                      
            }
            alertNotificar(data.mensaje,'success');
            
            // IdAntecedentesRegistrado=data.id_antecedentes
                            
        }, error:function (data) {
            console.log(data)

            vistacargando("");
            alertNotificar('Ocurrió un error','error');
        }
    });
})

function guardarAntecedentes() {
    return new Promise((resolve, reject) => {

        let id_empleado = $('#id_empleado').val();
        let seleccion = $('#sexo_empleado').val();

        if (!IdCabeceraAtencion) {
            reject("No existe IdCabeceraAtencion");
            return;
        }

        // if (!validaSeccionC()) {
        //     reject("Validación Sección C");
        //     return;
        // }

        vistacargando("m","Registrando...");

        let FrmData = $("#formAntecedentes").serialize();
        FrmData += "&id_empleado=" + id_empleado;
        FrmData += "&hombre_mujer=" + seleccion;
        FrmData += "&IdCabeceraAtencion=" + IdCabeceraAtencion;

        $.ajax({
            type: "POST",
            url: "guardar-antecedentes",
            data: FrmData,

            success: function (data) {
                vistacargando("");

                if (data.error === true) {
                    alertNotificar(data.mensaje,'error');
                    reject(data.mensaje);
                    return;
                }

                alertNotificar(data.mensaje,'success');

                // 🔑 si luego lo necesitas
                IdAntecedentesRegistrado = data.id_antecedentes;

                console.log("Antecedentes OK", IdAntecedentesRegistrado);

                resolve(data); // ✅ LISTO
            },
            error: function (err) {
                console.log(err);
                vistacargando("");
                alertNotificar('Ocurrió un error','error');
                reject(err);
            }
        });
    });
}

function validaSeccionD(){
    let enfermedad_problema_actual=$('#enfermedad_problema_actual').val()
    if(enfermedad_problema_actual=="" || enfermedad_problema_actual==null){
        alertNotificar("Debe ingresar enfermedad_problema_actual","error")
        $('#enfermedad_problema_actual').focus()
        return false
    } 
    return true
} 

globalThis.IdEnfermedadregistrada=0
// $("#formEnfermedad").submit(function(e){
//     e.preventDefault();
    
//     let id_empleado=$('#id_empleado').val()

//     //  if(!validaSeccionD()){
//     //     return; // ⛔ se detiene aquí
//     // }
   

//     vistacargando("m","Registrando...")
//     $.ajaxSetup({
//         headers: {
//             'X-CSRF-TOKEN': $('meta[name="csrf-token"]').attr('content')
//         }
//     });

//     //comprobamos si es registro o edicion
//     let tipo="POST"
//     let url_form="guardar-enfermedad"
    
//     var FrmData=$("#formEnfermedad").serialize();
//     FrmData += "&id_empleado=" + id_empleado;
//     FrmData += "&IdCabeceraAtencion=" + IdCabeceraAtencion;
    
   
//     $.ajax({
            
//         type: tipo,
//         url: url_form,
//         method: tipo,             
// 		data: FrmData,      
		
//         processData:false, 

//         success: function(data){
//             vistacargando("");                
//             if(data.error==true){
//                 alertNotificar(data.mensaje,'error');
//                 return;                      
//             }
//             alertNotificar(data.mensaje,'success');
//             // IdEnfermedadregistrada=data.idenfermedad
                            
//         }, error:function (data) {
//             console.log(data)

//             vistacargando("");
//             alertNotificar('Ocurrió un error','error');
//         }
//     });
// })

function guardarEnfermedad() {
    return new Promise((resolve, reject) => {

        let id_empleado = $('#id_empleado').val();

        if (!IdCabeceraAtencion) {
            reject("No existe IdCabeceraAtencion");
            return;
        }

        // if (!validaSeccionD()) {
        //     reject("Validación Sección D");
        //     return;
        // }

        vistacargando("m","Registrando...");

        let FrmData = $("#formEnfermedad").serialize();
        FrmData += "&id_empleado=" + id_empleado;
        FrmData += "&IdCabeceraAtencion=" + IdCabeceraAtencion;

        $.ajax({
            type: "POST",
            url: "guardar-enfermedad",
            data: FrmData,

            success: function (data) {
                vistacargando("");

                if (data.error === true) {
                    alertNotificar(data.mensaje,'error');
                    reject(data.mensaje);
                    return;
                }

                alertNotificar(data.mensaje,'success');

                // 🔑 si luego lo necesitas
                IdEnfermedadregistrada = data.idenfermedad;

                console.log("Enfermedad OK", IdEnfermedadregistrada);

                resolve(data); // ✅ TERMINÓ BIEN
            },
            error: function (err) {
                console.log(err);
                vistacargando("");
                alertNotificar('Ocurrió un error','error');
                reject(err);
            }
        });
    });
}
function guardaVitales() {
   
    return new Promise((resolve, reject) => {

        let id_empleado = $('#id_empleado').val();

        if (!IdCabeceraAtencion) {
            alert("No existe IdCabeceraAtencion");
            return;
        }

      
        vistacargando("m","Registrando...");

        let FrmData = $("#formConstanteVitales").serialize();
        FrmData += "&id_empleado=" + id_empleado;
        FrmData += "&IdCabeceraAtencion=" + IdCabeceraAtencion;

        // Si luego dependes de la enfermedad
        // FrmData += "&IdEnfermedadregistrada=" + IdEnfermedadregistrada;

        $.ajax({
            type: "POST",
            url: "guardar-constantes",
            data: FrmData,

            success: function (data) {
                vistacargando("");

                if (data.error === true) {
                    alertNotificar(data.mensaje,'error');
                    reject(data.mensaje);
                    return;
                }

                alertNotificar(data.mensaje,'success');


                resolve(data); // ✅ LISTO
            },
            error: function (err) {
                console.log(err);
                vistacargando("");
                alertNotificar('Ocurrió un error','error');
                reject(err);
            }
        });
    });
}



function validaSeccionE(){
    let temperatura=$('#temperatura').val()
    let presion_arterial=$('#presion_arterial').val()
    let frecuencia_cardiaca=$('#frecuencia_cardiaca').val()
    let frecuencia_respiratoria=$('#frecuencia_respiratoria').val()
    let saturacion=$('#saturacion').val()
    let peso=$('#peso').val()
    let talla=$('#talla').val()
    let imc=$('#imc').val()
    
    if(temperatura=="" || temperatura==null){
        alertNotificar("Debe ingresar temperatura","error")
        $('#temperatura').focus()
        return false
    }
    
    if(presion_arterial=="" || presion_arterial==null){
        alertNotificar("Debe ingresar presion_arterial","error")
        $('#presion_arterial').focus()
        return false
    }

    if(frecuencia_cardiaca=="" || frecuencia_cardiaca==null){
        alertNotificar("Debe ingresar frecuencia_cardiaca","error")
        $('#frecuencia_cardiaca').focus()
        return false
    }

    if(frecuencia_respiratoria=="" || frecuencia_respiratoria==null){
        alertNotificar("Debe ingresar frecuencia_respiratoria","error")
        $('#frecuencia_respiratoria').focus()
        return false
    }

    if(saturacion=="" || saturacion==null){
        alertNotificar("Debe ingresar saturacion","error")
        $('#saturacion').focus()
        return false
    }

    if(peso=="" || peso==null){
        alertNotificar("Debe ingresar peso","error")
        $('#peso').focus()
        return false
    }

    if(talla=="" || talla==null){
        alertNotificar("Debe ingresar talla","error")
        $('#talla').focus()
        return false
    }

    if(imc=="" || imc==null){
        alertNotificar("Debe ingresar el indice de masa corporal","error")
        $('#imc').focus()
        return false
    }

    return true
} 

// $("#formConstanteVitales").submit(function(e){
//     e.preventDefault();

//     let id_empleado=$('#id_empleado').val()

//     // if(!validaSeccionE()){
//     //     return; // ⛔ se detiene aquí
//     // }
    
//     vistacargando("m","Registrando...")
//     $.ajaxSetup({
//         headers: {
//             'X-CSRF-TOKEN': $('meta[name="csrf-token"]').attr('content')
//         }
//     });

//     //comprobamos si es registro o edicion
//     let tipo="POST"
//     let url_form="guardar-constantes"
    
//     var FrmData=$("#formConstanteVitales").serialize();
//     FrmData += "&id_empleado=" + id_empleado;
//     FrmData += "&IdCabeceraAtencion=" + IdCabeceraAtencion;
//     // FrmData += "&IdEnfermedadregistrada=" + IdEnfermedadregistrada;    
   
//     $.ajax({
            
//         type: tipo,
//         url: url_form,
//         method: tipo,             
// 		data: FrmData,      
		
//         processData:false, 

//         success: function(data){
//             vistacargando("");                
//             if(data.error==true){
//                 alertNotificar(data.mensaje,'error');
//                 return;                      
//             }
//             alertNotificar(data.mensaje,'success');
//             // IdConstantesVitale=data.idconstante
                            
//         }, error:function (data) {
//             console.log(data)

//             vistacargando("");
//             alertNotificar('Ocurrió un error','error');
//         }
//     });
// })





function validaSeccionF(){
    let motivo_examen=$('#motivo_examen').val()
   
    if(motivo_examen=="" || motivo_examen==null){
        alertNotificar("Debe ingresar la observacion de los examenes","error")
        $('#motivo_examen').focus()
        return false
    }

    return true
}

// $("#formExamenFisico").submit(function(e){
//     e.preventDefault();
//     let id_empleado=$('#id_empleado').val()

//     // if(!validaSeccionF()){
//     //     return; // ⛔ se detiene aquí
//     // }
        
//     vistacargando("m","Registrando...")
//     $.ajaxSetup({
//         headers: {
//             'X-CSRF-TOKEN': $('meta[name="csrf-token"]').attr('content')
//         }
//     });

//     //comprobamos si es registro o edicion
//     let tipo="POST"
//     let url_form="guardar-examen-fisico"
    
//     var FrmData=$("#formExamenFisico").serialize();
//     FrmData += "&id_empleado=" + id_empleado;
//     FrmData += "&IdCabeceraAtencion=" + IdCabeceraAtencion;
//     // FrmData += "&IdConstantesVitale=" + IdConstantesVitale;    
   
//     $.ajax({
            
//         type: tipo,
//         url: url_form,
//         method: tipo,             
// 		data: FrmData,      
		
//         processData:false, 

//         success: function(data){
//             vistacargando("");                
//             if(data.error==true){
//                 alertNotificar(data.mensaje,'error');
//                 return;                      
//             }
//             alertNotificar(data.mensaje,'success');
           
//         }, error:function (data) {
//             console.log(data)

//             vistacargando("");
//             alertNotificar('Ocurrió un error','error');
//         }
//     });
// })

function guardarExamenFisico() {
    return new Promise((resolve, reject) => {

        let id_empleado = $('#id_empleado').val();

        if (!IdCabeceraAtencion) {
            reject("No existe IdCabeceraAtencion");
            return;
        }

        // if (!validaSeccionF()) {
        //     reject("Validación Sección F");
        //     return;
        // }

        vistacargando("m","Registrando...");

        let FrmData = $("#formExamenFisico").serialize();
        FrmData += "&id_empleado=" + id_empleado;
        FrmData += "&IdCabeceraAtencion=" + IdCabeceraAtencion;

        // Si luego necesitas la relación
        // FrmData += "&IdConstantesVitale=" + IdConstantesVitale;

        $.ajax({
            type: "POST",
            url: "guardar-examen-fisico",
            data: FrmData,

            success: function (data) {
                vistacargando("");

                if (data.error === true) {
                    alertNotificar(data.mensaje,'error');
                    reject(data.mensaje);
                    return;
                }

                alertNotificar(data.mensaje,'success');

                console.log("Examen Físico OK");

                resolve(data); // ✅ LISTO
            },
            error: function (err) {
                console.log(err);
                vistacargando("");
                alertNotificar('Ocurrió un error','error');
                reject(err);
            }
        });
    });
}



$(document).on('change', '.check_riesgo', function () {

    let checkbox = $(this);

    let payload = {
        _token: $('meta[name="csrf-token"]').attr('content'),
        indice: checkbox.val(),
        tipo: checkbox.data('tipo'),
        expo: checkbox.data('fisico'),
        codigo: checkbox.data('codigo'),
        paciente_id:  $('#id_empleado').val(),
        IdCabeceraAtencion:  IdCabeceraAtencion
    };

    if (checkbox.is(':checked')) {
        // ✅ GUARDAR
        $.post('factores-riesgos', payload)
            .done(res => {
                console.log('Guardado', res);
            })
            .fail(() => {
                alert('Error al guardar');
                checkbox.prop('checked', false); // rollback
            });

    } else {
        // ❌ ELIMINAR
        $.post('factores-riesgos-eliminar', payload)
            .done(res => {
                console.log('Eliminado', res);
            })
            .fail(() => {
                alert('Error al eliminar');
                checkbox.prop('checked', true); // rollback
            });
    }
});


function guardarMedidaPreventiva(){
    let charla_salud_empl=$('#charla_salud_empl').val()
    let controles_med_empl=$('#controles_med_empl').val()
    let prenda_proteccion_empl=$('#prenda_proteccion_empl').val()
    let id_empleado=$('#id_empleado').val()
    if(charla_salud_empl=="" || charla_salud_empl==null){
        alertNotificar("Debe ingresar la charla_salud_empl","error")
        $('#charla_salud_empl').focus()
        return
    } 

    if(controles_med_empl=="" || controles_med_empl==null){
        alertNotificar("Debe ingresar la controles_med_empl","error")
        $('#controles_med_empl').focus()
        return
    } 

    if(prenda_proteccion_empl=="" || prenda_proteccion_empl==null){
        alertNotificar("Debe ingresar la prenda_proteccion_empl","error")
        $('#prenda_proteccion_empl').focus()
        return
    } 

    vistacargando("m","Espere por favor")
    $.ajaxSetup({
        headers: {
            'X-CSRF-TOKEN': $('meta[name="csrf-token"]').attr('content')
        }
    });

    //comprobamos si es registro o edicion
    let tipo="POST"
    let url_form="guardar-medidad-preventivas"
    
    var FrmData=$("#formMedidas").serialize();
    FrmData += "&id_empleado=" + id_empleado;
    FrmData += "&IdCabeceraAtencion=" + IdCabeceraAtencion;
    
    $.ajax({
            
        type: tipo,
        url: url_form,
        method: tipo,             
		data: FrmData,      
		
        processData:false, 

        success: function(data){
            vistacargando("");                
            if(data.error==true){
                alertNotificar(data.mensaje,'error');
                return;                      
            }
            alertNotificar(data.mensaje,'success');
            llenarTablaMedida(id_empleado)  
            limpiarCamposMedidas()          
        }, error:function (data) {
            console.log(data)

            vistacargando("");
            alertNotificar('Ocurrió un error','error');
        }
    });
}
function limpiarCamposMedidas(){
    $('#charla_salud_empl').val('')
    $('#controles_med_empl').val('')
    $('#prenda_proteccion_empl').val('')
}
$('#modalCrearPuesto').on('hidden.bs.modal', function (e) {
    limpiarCamposMedidas()
});

function llenarTablaMedida(id_empleado){
    $("#tbodyMedida").html('');
    $('#tbodyMedida').empty(); 
    vistacargando("m","Espere por favor")
    $.get("llenar-tabla-medida/"+id_empleado, function(data){
        vistacargando("")
        if(data.error==true){
            alertNotificar(data.mensaje,"error");
            // return;   
        }

        if(data.error==true){
            $('#tbodyMedida').append(`<tr>
            <td colspan="3" style="text-align:center">No hay datos disponibles</td>`);
            alertNotificar(data.mensaje,'error');
            return;                      
        }
        console.log(data)
        if(data.resultado.length==0){
            $('#tbodyMedida').append(`<tr>
            <td colspan="3" style="text-align:center">No hay datos disponibles</td>`);
            return; 
        }
  
        $.each(data.resultado,function(i, item){
            $('#tbodyMedida').append(`<tr>
                
                <td style="width:30%; text-align:center; vertical-align:middle">
                    ${item.charla_salud}                    
                </td>
                <td style="width:30%; text-align:center; vertical-align:middle">
                    ${item.controles_medicos_rutinarios}                     
                </td>
                <td style="width:30%; text-align:center; vertical-align:middle">
                    ${item.uso_adecuado_prenda_prot}            
                </td>
                
            
            </tr>`);
        })

    }).fail(function(){
        vistacargando("")
        $('#tbodyMedida').append(`<tr>
        <td colspan="3" style="text-align:center">No hay datos disponibles</td>`);
        alertNotificar("Se produjo un error, por favor intentelo más tarde","error");  
    });
}

function abrirModalActividades(){
    $('#modalActividad').modal('show')
}

function limpiarModalAntecedentes(){
    $('.modal_act').val('')
}
function guardarActividad(){
    
    let id_empleado=$('#id_empleado').val()
   
    vistacargando("m","Espere por favor")
    $.ajaxSetup({
        headers: {
            'X-CSRF-TOKEN': $('meta[name="csrf-token"]').attr('content')
        }
    });

    //comprobamos si es registro o edicion
    let tipo="POST"
    let url_form="guardar-actividad-laboral"
    
    var FrmData=$("#formActividad").serialize();
    FrmData += "&id_empleado=" + id_empleado;
    FrmData += "&IdCabeceraAtencion=" + IdCabeceraAtencion;

    $.ajax({
            
        type: tipo,
        url: url_form,
        method: tipo,             
		data: FrmData,     
        processData:false, 

        success: function(data){
            vistacargando("");                
            if(data.error==true){
                alertNotificar(data.mensaje,'error');
                return;                      
            }
            alertNotificar(data.mensaje,'success');
            llenarTablaActividad(id_empleado)  
            limpiarModalAntecedentes()          
        }, error:function (data) {
            console.log(data)

            vistacargando("");
            alertNotificar('Ocurrió un error','error');
        }
    });
}

function llenarTablaActividad(id_empleado){
    $("#tbodyActividadLaboral").html('');
    $('#tbodyActividadLaboral').empty(); 
    vistacargando("m","Espere por favor")
    $.get("llenar-tabla-actividad/"+id_empleado, function(data){
        vistacargando("")
        if(data.error==true){
            alertNotificar(data.mensaje,"error");
            // return;   
        }

        if(data.error==true){
            $('#tbodyActividadLaboral').append(`<tr>
            <td colspan="13" style="text-align:center">No hay datos disponibles</td>`);
            alertNotificar(data.mensaje,'error');
            return;                      
        }
        console.log(data)
        if(data.resultado.length==0){
            $('#tbodyActividadLaboral').append(`<tr>
            <td colspan="13" style="text-align:center">No hay datos disponibles</td>`);
            return; 
        }
  
        $.each(data.resultado,function(i, item){
            let actual=""
            let anterior=""
            if(item.trabajo=="Anterior"){
                anterior="X"
            }else if(item.trabajo=="Actual"){
                actual="X"
            }

            let si_califica=""
            let no_califica=""
            if(item.calificado_iess=="Si"){
                si_califica="X"
            }else if(item.calificado_iess=="No"){
                no_califica="X"
            }

            $('#tbodyActividadLaboral').append(`<tr>
                
                <td style="text-align:center; vertical-align:middle">
                    ${item.centro_trabajo ? item.centro_trabajo : ''}
                   
                </td>
                <td style="text-align:center; vertical-align:middle">
                    ${item.actividad_desempenia ? item.actividad_desempenia : ''}
                    
                </td>
                <td style="text-align:center; vertical-align:middle">
                    ${anterior}            
                </td>

                <td style="text-align:center; vertical-align:middle">
                    ${actual}                    
                </td>
                <td style="text-align:center; vertical-align:middle">
                    ${item.tiempo_trabajo ? item.tiempo_trabajo : ''}                    
                </td>
                <td style="text-align:center; vertical-align:middle">
                   ${item.incidente ? item.incidente : ''}                  
                </td>

                <td style="text-align:center; vertical-align:middle">
                    ${item.accidente ? item.accidente : ''}         
                </td>

                <td style="text-align:center; vertical-align:middle">
                    ${item.enfermedad_profesional ? item.enfermedad_profesional : ''}         
                </td>

                <td style="text-align:center; vertical-align:middle">
                    ${si_califica}            
                </td>
                <td style="text-align:center; vertical-align:middle">
                    ${no_califica}            
                </td>

                <td style="text-align:center; vertical-align:middle">
                   ${item.fecha_calificacion ? item.fecha_calificacion : ''}    
                </td>

                <td style="text-align:center; vertical-align:middle">
                   ${item.especificar ? item.especificar : ''}         
                </td>

                <td style="text-align:center; vertical-align:middle">
                    ${item.observaciones ? item.observaciones : ''}    
                </td>
                
            
            </tr>`);
        })

    }).fail(function(){
        vistacargando("")
        $('#tbodyActividadLaboral').append(`<tr>
        <td colspan="13" style="text-align:center">No hay datos disponibles</td>`);
        alertNotificar("Se produjo un error, por favor intentelo más tarde","error");  
    });
}


function abrirModalActividadesExtras(){
    $('#modalActividadExtra').modal('show')
}

function limpiarModalActExtras(){
    $('.modal_ext').val('')
}
function guardarActividadExtras(){
    
    let id_empleado=$('#id_empleado').val()
    let act_extra=$('#act_extra').val()
    let fecha_act_extra=$('#fecha_act_extra').val()

    if(act_extra=="" || act_extra==null){
        alertNotificar("Debe ingresar la actividad","error")
        $('#act_extra').focus()
        return
    } 

    // if(fecha_act_extra=="" || fecha_act_extra==null){
    //     alertNotificar("Debe seleccionar la fecha","error")
    //     $('#fecha_act_extra').focus()
    //     return
    // } 
   
    vistacargando("m","Espere por favor")
    $.ajaxSetup({
        headers: {
            'X-CSRF-TOKEN': $('meta[name="csrf-token"]').attr('content')
        }
    });

    //comprobamos si es registro o edicion
    let tipo="POST"
    let url_form="guardar-actividad-extra"
    
    var FrmData=$("#formActividadExtra").serialize();
    FrmData += "&id_empleado=" + id_empleado;
    FrmData += "&IdCabeceraAtencion=" + IdCabeceraAtencion;

    $.ajax({
            
        type: tipo,
        url: url_form,
        method: tipo,             
		data: FrmData,     
        processData:false, 

        success: function(data){
            vistacargando("");                
            if(data.error==true){
                alertNotificar(data.mensaje,'error');
                return;                      
            }
            alertNotificar(data.mensaje,'success');
            llenarTablaActividadExtras(id_empleado)  
            limpiarModalActExtras()          
        }, error:function (data) {
            console.log(data)

            vistacargando("");
            alertNotificar('Ocurrió un error','error');
        }
    });
}

function llenarTablaActividadExtras(id_empleado){
    $("#tbodyActividadExtra").html('');
    $('#tbodyActividadExtra').empty(); 
    vistacargando("m","Espere por favor")
    $.get("llenar-tabla-actividad-extra/"+id_empleado, function(data){
        vistacargando("")
        if(data.error==true){
            alertNotificar(data.mensaje,"error");
            // return;   
        }

        if(data.error==true){
            $('#tbodyActividadExtra').append(`<tr>
            <td colspan="2" style="text-align:center">No hay datos disponibles</td>`);
            alertNotificar(data.mensaje,'error');
            return;                      
        }
        console.log(data)
        if(data.resultado.length==0){
            $('#tbodyActividadExtra').append(`<tr>
            <td colspan="2" style="text-align:center">No hay datos disponibles</td>`);
            return; 
        }
  
        $.each(data.resultado,function(i, item){
            

            $('#tbodyActividadExtra').append(`<tr>
                
                <td style="text-align:left; vertical-align:middle">
                    ${item.actividad ? item.actividad : ''}
                   
                </td>
                <td style="text-align:left; vertical-align:middle">
                    ${item.fecha ? item.fecha : ''}
                    
                </td>
               
            
            </tr>`);
        })

    }).fail(function(){
        vistacargando("")
        $('#tbodyActividadExtra').append(`<tr>
        <td colspan="2" style="text-align:center">No hay datos disponibles</td>`);
        alertNotificar("Se produjo un error, por favor intentelo más tarde","error");  
    });
}



function abrirModalResultadosExam(){
    $('#modalResultadoExamen').modal('show')
}

function limpiarModalresultadoExam(){
    $('.modal_exam').val('')
}
function guardarResultadoExamen(){
    
    let id_empleado=$('#id_empleado').val()
    let nombre_examen=$('#nombre_examen').val()
    let fecha_examen=$('#fecha_examen').val()
    let resultados_exam=$('#resultados_exam').val()

    if(nombre_examen=="" || nombre_examen==null){
        alertNotificar("Debe ingresar el nombre del examen","error")
        $('#nombre_examen').focus()
        return
    } 

    if(fecha_examen=="" || fecha_examen==null){
        alertNotificar("Debe ingresar la fecha","error")
        $('#fecha_examen').focus()
        return
    } 

    if(resultados_exam=="" || resultados_exam==null){
        alertNotificar("Debe ingresar los resultados","error")
        $('#resultados_exam').focus()
        return
    } 
   
    vistacargando("m","Espere por favor")
    $.ajaxSetup({
        headers: {
            'X-CSRF-TOKEN': $('meta[name="csrf-token"]').attr('content')
        }
    });

    //comprobamos si es registro o edicion
    let tipo="POST"
    let url_form="guardar-resultados-examen"
    
    var FrmData=$("#formResultadoExamen").serialize();
    FrmData += "&id_empleado=" + id_empleado;
    FrmData += "&IdCabeceraAtencion=" + IdCabeceraAtencion;

    $.ajax({
            
        type: tipo,
        url: url_form,
        method: tipo,             
		data: FrmData,     
        processData:false, 

        success: function(data){
            vistacargando("");                
            if(data.error==true){
                alertNotificar(data.mensaje,'error');
                return;                      
            }
            alertNotificar(data.mensaje,'success');
            llenarTablaResultadoExamen(id_empleado)  
            limpiarModalresultadoExam()          
        }, error:function (data) {
            console.log(data)

            vistacargando("");
            alertNotificar('Ocurrió un error','error');
        }
    });
}

function llenarTablaResultadoExamen(id_empleado){
    $("#tbodyResultadoExamen").html('');
    $('#tbodyResultadoExamen').empty(); 
    vistacargando("m","Espere por favor")
    $.get("llenar-tabla-resultados-examen/"+id_empleado, function(data){
        vistacargando("")
        if(data.error==true){
            alertNotificar(data.mensaje,"error");
            // return;   
        }

        if(data.error==true){
            $('#tbodyResultadoExamen').append(`<tr>
            <td colspan="3" style="text-align:center">No hay datos disponibles</td>`);
            alertNotificar(data.mensaje,'error');
            return;                      
        }
        console.log(data)
        if(data.resultado.length==0){
            $('#tbodyResultadoExamen').append(`<tr>
            <td colspan="3" style="text-align:center">No hay datos disponibles</td>`);
            return; 
        }
  
        $.each(data.resultado,function(i, item){
            

            $('#tbodyResultadoExamen').append(`<tr>
                
                <td style="text-align:left; vertical-align:middle">
                    ${item.examen ? item.examen : ''}
                   
                </td>
                <td style="text-align:left; vertical-align:middle">
                    ${item.fecha ? item.fecha : ''}
                    
                </td>
                <td style="text-align:left; vertical-align:middle">
                    ${item.resultado ? item.resultado : ''}
                    
                </td>
               
            
            </tr>`);
        })

    }).fail(function(){
        vistacargando("")
        $('#tbodyResultadoExamen').append(`<tr>
        <td colspan="3" style="text-align:center">No hay datos disponibles</td>`);
        alertNotificar("Se produjo un error, por favor intentelo más tarde","error");  
    });
}

function validaSeccionJ(){
    let observacion_resultados=$('#observacion_resultados').val()
  
    if(observacion_resultados=="" || observacion_resultados==null){
        alertNotificar("Debe ingresar la observacion de los resultados de los examenes generales y especificos","error")
        $('#observacion_resultados').focus()
        return false
    }
    return true
    // let tamanio_diagnostico=$('#tbodyDiagnostico tr').length;
    // alert(tamanio_diagnostico)    
}

// $("#formObservacionResultados").submit(function(e){
//     e.preventDefault();
//     let id_empleado=$('#id_empleado').val()

//     // if(!validaSeccionJ()){
//     //     return; // ⛔ se detiene aquí
//     // }
    
//     vistacargando("m","Registrando...")
//     $.ajaxSetup({
//         headers: {
//             'X-CSRF-TOKEN': $('meta[name="csrf-token"]').attr('content')
//         }
//     });

//     //comprobamos si es registro o edicion
//     let tipo="POST"
//     let url_form="guardar-observacion-resultados"
    
//     var FrmData=$("#formObservacionResultados").serialize();
//     FrmData += "&id_empleado=" + id_empleado;
//     FrmData += "&IdCabeceraAtencion=" + IdCabeceraAtencion;
     
//     $.ajax({
            
//         type: tipo,
//         url: url_form,
//         method: tipo,             
// 		data: FrmData,      
		
//         processData:false, 

//         success: function(data){
//             vistacargando("");                
//             if(data.error==true){
//                 alertNotificar(data.mensaje,'error');
//                 return;                      
//             }
//             alertNotificar(data.mensaje,'success');
           
                            
//         }, error:function (data) {
//             console.log(data)

//             vistacargando("");
//             alertNotificar('Ocurrió un error','error');
//         }
//     });
// })

function guardarObservacionResultados() {
    return new Promise((resolve, reject) => {

        let id_empleado = $('#id_empleado').val();

        if (!IdCabeceraAtencion) {
            reject("No existe IdCabeceraAtencion");
            return;
        }

        // if (!validaSeccionJ()) {
        //     reject("Validación Sección J");
        //     return;
        // }

        vistacargando("m","Registrando...");

        let FrmData = $("#formObservacionResultados").serialize();
        FrmData += "&id_empleado=" + id_empleado;
        FrmData += "&IdCabeceraAtencion=" + IdCabeceraAtencion;

        $.ajax({
            type: "POST",
            url: "guardar-observacion-resultados",
            data: FrmData,

            success: function (data) {
                vistacargando("");

                if (data.error === true) {
                    alertNotificar(data.mensaje,'error');
                    reject(data.mensaje);
                    return;
                }

                alertNotificar(data.mensaje,'success');

                console.log("Observaciones / Resultados OK");

                resolve(data); // ✅ LISTO
            },
            error: function (err) {
                console.log(err);
                vistacargando("");
                alertNotificar('Ocurrió un error','error');
                reject(err);
            }
        });
    });
}



function abrirModalDiagnostico(){
    limpiarModalDiagnostico()
    $('#modalDiagnostico').modal('show')
}

$('#modalDiagnostico').on('shown.bs.modal', function () {

    // Evita doble inicialización
    if ($.fn.select2 && $('#cmb_diagnostico').hasClass('select2-hidden-accessible')) {
        $('#cmb_diagnostico').select2('destroy');
    }

    $('#cmb_diagnostico').select2({
        dropdownParent: $('#modalDiagnostico'), // 🔑 CLAVE
        width: '100%',
        placeholder: 'Buscar diagnóstico CIE-10',
        allowClear: true,
        minimumInputLength: 2,
        ajax: {
            url: 'buscaCie10',
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
                        id: item.id,
                        text: item.nombre
                    }))
                };
            },
            cache: true
        }
    });
});

function limpiarModalDiagnostico(){
    $('.modal_diag').val('')
    $('#cmb_diagnostico').val('')
}
function guardarDiagnostico(){
    
    let id_empleado=$('#id_empleado').val()
    let cmb_diagnostico=$('#cmb_diagnostico').val()
    let prevent_defin=$('#prevent_defin').val()
  
    if(cmb_diagnostico=="" || cmb_diagnostico==null){
        alertNotificar("Debe ingresar y seleccionar el diagnostico","error")
        return
    } 

    if(prevent_defin=="" || prevent_defin==null){
        alertNotificar("Debe seleccionar un tipo","error")
        return
    } 

    vistacargando("m","Espere por favor")
    $.ajaxSetup({
        headers: {
            'X-CSRF-TOKEN': $('meta[name="csrf-token"]').attr('content')
        }
    });

    //comprobamos si es registro o edicion
    let tipo="POST"
    let url_form="guardar-diagnostico"
    
    var FrmData=$("#formDiagnostico").serialize();
    FrmData += "&id_empleado=" + id_empleado;
    FrmData += "&IdCabeceraAtencion=" + IdCabeceraAtencion;

    $.ajax({
            
        type: tipo,
        url: url_form,
        method: tipo,             
		data: FrmData,     
        processData:false, 

        success: function(data){
            vistacargando("");                
            if(data.error==true){
                alertNotificar(data.mensaje,'error');
                return;                      
            }
            alertNotificar(data.mensaje,'success');
            llenarTablaDiagnostico(id_empleado)  
            limpiarModalDiagnostico()          
        }, error:function (data) {
            console.log(data)

            vistacargando("");
            alertNotificar('Ocurrió un error','error');
        }
    });
}


function llenarTablaDiagnostico(id_empleado){
    $("#tbodyDiagnostico").html('');
    $('#tbodyDiagnostico').empty(); 
    vistacargando("m","Espere por favor")
    $.get("llenar-tabla-diagnostico/"+id_empleado, function(data){
        vistacargando("")
        if(data.error==true){
            alertNotificar(data.mensaje,"error");
            // return;   
        }

        if(data.error==true){
            $('#tbodyDiagnostico').append(`<tr>
            <td colspan="4" style="text-align:center">No hay datos disponibles</td>`);
            alertNotificar(data.mensaje,'error');
            return;                      
        }
        console.log(data)
        if(data.resultado.length==0){
            $('#tbodyDiagnostico').append(`<tr>
            <td colspan="4" style="text-align:center">No hay datos disponibles</td>`);
            return; 
        }
        CantDiag=data.resultado.length
        $.each(data.resultado,function(i, item){
            
            let def=""
            let prev=""
            if(item.prev_def=="Preventiva"){
                prev="X"
                def=""
                
            }else{
                def="X"
                prev=""
            }

            $('#tbodyDiagnostico').append(`<tr>
                
                <td style="text-align:left; vertical-align:middle">
                    ${item.cie10 ? item.cie10.codigo : ''}
                   
                </td>
                <td style="text-align:left; vertical-align:middle">
                    ${item.cie10 ? item.cie10.descripcion : ''}
                    
                </td>
                <td style="text-align:left; vertical-align:middle">
                    ${prev}
                    
                </td>
                <td style="text-align:left; vertical-align:middle">
                    ${def}
                    
                </td>
               
            
            </tr>`);
        })

    }).fail(function(){
        vistacargando("")
        $('#tbodyDiagnostico').append(`<tr>
        <td colspan="4" style="text-align:center">No hay datos disponibles</td>`);
        alertNotificar("Se produjo un error, por favor intentelo más tarde","error");  
    });
}

function validaSeccionL(){
    let observ_apt_med=$('#observ_apt_med').val()
   
    if(observ_apt_med=="" || observ_apt_med==null){
        alertNotificar("Debe ingresar las observacion de aptitudes medicas","error")
        $('#observ_apt_med').focus()
        return false
    } 

    return true
}
// function guardarAptitudesMedi(){
    
//     let id_empleado=$('#id_empleado').val()

//     // if(!validaSeccionL()){
//     //     return; // ⛔ se detiene aquí
//     // }
//     vistacargando("m","Espere por favor")
//     $.ajaxSetup({
//         headers: {
//             'X-CSRF-TOKEN': $('meta[name="csrf-token"]').attr('content')
//         }
//     });

//     //comprobamos si es registro o edicion
//     let tipo="POST"
//     let url_form="guardar-aptitudes-medicas"
    
//     var FrmData=$("#formAptitudesMedicas").serialize();
//     FrmData += "&id_empleado=" + id_empleado;
//     FrmData += "&IdCabeceraAtencion=" + IdCabeceraAtencion;

//     $.ajax({
            
//         type: tipo,
//         url: url_form,
//         method: tipo,             
// 		data: FrmData,     
//         processData:false, 

//         success: function(data){
//             vistacargando("");                
//             if(data.error==true){
//                 alertNotificar(data.mensaje,'error');
//                 return;                      
//             }
//             alertNotificar(data.mensaje,'success');
                  
//         }, error:function (data) {
//             console.log(data)

//             vistacargando("");
//             alertNotificar('Ocurrió un error','error');
//         }
//     });
// }
function guardarAptitudesMedi() {
    return new Promise((resolve, reject) => {

        let id_empleado = $('#id_empleado').val();

        if (!IdCabeceraAtencion) {
            reject("No existe IdCabeceraAtencion");
            return;
        }

        // if (!validaSeccionL()) {
        //     reject("Validación Sección L");
        //     return;
        // }

        vistacargando("m","Espere por favor");

        let FrmData = $("#formAptitudesMedicas").serialize();
        FrmData += "&id_empleado=" + id_empleado;
        FrmData += "&IdCabeceraAtencion=" + IdCabeceraAtencion;

        $.ajax({
            type: "POST",
            url: "guardar-aptitudes-medicas",
            data: FrmData,

            success: function (data) {
                vistacargando("");

                if (data.error === true) {
                    alertNotificar(data.mensaje,'error');
                    reject(data.mensaje);
                    return;
                }

                alertNotificar(data.mensaje,'success');
                console.log("Aptitudes médicas OK");

                resolve(data); // ✅ LISTO
            },
            error: function (err) {
                console.log(err);
                vistacargando("");
                alertNotificar('Ocurrió un error','error');
                reject(err);
            }
        });
    });
}


function validaSeccionM(){
    let recomendacion_trata=$('#recomendacion_trata').val()
   
    if(recomendacion_trata=="" || recomendacion_trata==null){
        alertNotificar("Debe ingresar la recomendacion tratamiento","error")
        $('#recomendacion_trata').focus()
        return false
    } 

    return true
}


// function guardarRecomendaciones(){
    
//     let id_empleado=$('#id_empleado').val()
    
//     // if(!validaSeccionM()){
//     //     return; // ⛔ se detiene aquí
//     // }
   
//     vistacargando("m","Espere por favor")
//     $.ajaxSetup({
//         headers: {
//             'X-CSRF-TOKEN': $('meta[name="csrf-token"]').attr('content')
//         }
//     });

//     //comprobamos si es registro o edicion
//     let tipo="POST"
//     let url_form="guardar-recomendacion-tratamiento"
    
//     var FrmData=$("#formRecomendacionTrat").serialize();
//     FrmData += "&id_empleado=" + id_empleado;
//     FrmData += "&IdCabeceraAtencion=" + IdCabeceraAtencion;

//     $.ajax({
            
//         type: tipo,
//         url: url_form,
//         method: tipo,             
// 		data: FrmData,     
//         processData:false, 

//         success: function(data){
//             vistacargando("");                
//             if(data.error==true){
//                 alertNotificar(data.mensaje,'error');
//                 return;                      
//             }
//             alertNotificar(data.mensaje,'success');
                  
//         }, error:function (data) {
//             console.log(data)

//             vistacargando("");
//             alertNotificar('Ocurrió un error','error');
//         }
//     });
// }

function guardarRecomendaciones() {
    return new Promise((resolve, reject) => {

        let id_empleado = $('#id_empleado').val();

        if (!IdCabeceraAtencion) {
            reject("No existe IdCabeceraAtencion");
            return;
        }

        // if (!validaSeccionM()) {
        //     reject("Validación Sección M");
        //     return;
        // }

        vistacargando("m","Espere por favor");

        let FrmData = $("#formRecomendacionTrat").serialize();
        FrmData += "&id_empleado=" + id_empleado;
        FrmData += "&IdCabeceraAtencion=" + IdCabeceraAtencion;

        $.ajax({
            type: "POST",
            url: "guardar-recomendacion-tratamiento",
            data: FrmData,

            success: function (data) {
                vistacargando("");

                if (data.error === true) {
                    alertNotificar(data.mensaje,'error');
                    reject(data.mensaje);
                    return;
                }

                alertNotificar(data.mensaje,'success');
                console.log("Recomendaciones OK");

                resolve(data); // ✅ LISTO
            },
            error: function (err) {
                console.log(err);
                vistacargando("");
                alertNotificar('Ocurrió un error','error');
                reject(err);
            }
        });
    });
}


function validaSeccionN(){
    let observ_retiro=$('#observ_retiro').val()
   
    if(observ_retiro=="" || observ_retiro==null){
        alertNotificar("Debe ingresar la observacion retiro","error")
        $('#observ_retiro').focus()
        return false
    } 

    return true
}

// function guardarRetiro(){
    
//     let id_empleado=$('#id_empleado').val()
    
//     // if(!validaSeccionN()){
//     //     return; // ⛔ se detiene aquí
//     // }

//     vistacargando("m","Espere por favor")
//     $.ajaxSetup({
//         headers: {
//             'X-CSRF-TOKEN': $('meta[name="csrf-token"]').attr('content')
//         }
//     });

//     //comprobamos si es registro o edicion
//     let tipo="POST"
//     let url_form="guardar-retiro"
    
//     var FrmData=$("#formRetiro").serialize();
//     FrmData += "&id_empleado=" + id_empleado;
//     FrmData += "&IdCabeceraAtencion=" + IdCabeceraAtencion;

//     $.ajax({
            
//         type: tipo,
//         url: url_form,
//         method: tipo,             
// 		data: FrmData,     
//         processData:false, 

//         success: function(data){
//             vistacargando("");                
//             if(data.error==true){
//                 alertNotificar(data.mensaje,'error');
//                 return;                      
//             }
//             alertNotificar(data.mensaje,'success');
                  
//         }, error:function (data) {
//             console.log(data)

//             vistacargando("");
//             alertNotificar('Ocurrió un error','error');
//         }
//     });
// }

function guardarRetiro() {
    return new Promise((resolve, reject) => {

        let id_empleado = $('#id_empleado').val();

        if (!IdCabeceraAtencion) {
            reject("No existe IdCabeceraAtencion");
            return;
        }

        // if (!validaSeccionN()) {
        //     reject("Validación Sección N");
        //     return;
        // }

        vistacargando("m","Espere por favor");

        let FrmData = $("#formRetiro").serialize();
        FrmData += "&id_empleado=" + id_empleado;
        FrmData += "&IdCabeceraAtencion=" + IdCabeceraAtencion;

        $.ajax({
            type: "POST",
            url: "guardar-retiro",
            data: FrmData,

            success: function (data) {
                vistacargando("");

                if (data.error === true) {
                    alertNotificar(data.mensaje,'error');
                    reject(data.mensaje);
                    return;
                }

                alertNotificar(data.mensaje,'success');
                console.log("Retiro OK");

                resolve(data); // ✅ FIN REAL
            },
            error: function (err) {
                console.log(err);
                vistacargando("");
                alertNotificar('Ocurrió un error','error');
                reject(err);
            }
        });
    });
}
