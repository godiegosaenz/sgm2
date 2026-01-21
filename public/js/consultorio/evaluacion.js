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
       alert("a")
    }
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
                        <button type="button" class="btn btn-primary btn-sm" onclick="buscarTitulos('${item.id}')">
                            <i class="fa fa-file-pdf-o"></i>
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
                    <td style="width:20%; text-align:center; vertical-align:middle">
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

function atencionPaciente(id){
   
    vistacargando("m","Espere por favor")
    $.get("carga-info-paciente-evolucion/"+id, function(data){
        vistacargando("")
        
       
        if(data.error==true){
            // alertNotificar(data.mensaje,"error");
            return;   
        }
        
        $('#cedula_empleado').val(data.resultado.cedula)
        $('#primer_apellido_empleado').val(data.resultado.primer_apellido)
        $('#segundo_apellido').val(data.resultado.segundo_apellido)
        $('#primer_nombre_empleado').val(data.resultado.primer_nombre)
        $('#segundo_nombre_empleado').val(data.resultado.segundo_nombre)
        $('#fecha_nacimiento_empleado').val(data.resultado.fecha_nacimiento)
        $('#correo').val(data.resultado.correo)
        $('#telefono').val(data.resultado.telefono)
        $('#sexo').val(data.resultado.sexo)
        $('#grupo_sanguineo').val(data.resultado.grupo_sanguinedad)
        $('#lateridad').val(data.resultado.lateralidad)
        console.log(data)

        $('#busqueda_paciente').hide(200)
        $('#atencion_paciente').show(200)
       
         
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