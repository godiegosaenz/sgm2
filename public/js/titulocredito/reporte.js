function cambiaTipo(){
    limpiarBusqueda()
    var tipo=$('#tipo').val()
    $('#div_matricula').show()
    $('#div_clave').hide()
    $('#div_nombres').hide()
    if(tipo==""){return}
    else if(tipo=="1"){
        $('#div_matricula').show()
        $('#div_clave').hide()
         $('#div_nombres').hide()
    }else if(tipo=="2"){
        $('#div_matricula').hide()
        $('#div_nombres').hide()
        $('#div_clave').show()
    }else{
        $('#div_matricula').hide()
        $('#div_nombres').show()
        $('#div_clave').hide()
    }
}

function limpiarBusqueda(){
    $('#num_predio').val('')
    $('#clave').val('')
    $('#cmb_nombres').val('')
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

$('#clave').select2({
    // dropdownParent: $('#actividadLocal'),
    ajax: {
        url: 'buscarClaveCatastralUrbano',
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
                    id: item.clave_cat,
                    text: item.clave_cat + " - " + item.nombre
                }))
            };
        }
    },
    minimumInputLength: 1
});

function actualizarContribuyente(){
    if ($('input[name="checkLiquidacion[]"]:checked').length == 0) {
        alertNotificar("Debe seleccionar al menos uno de la lista","error")
    } else if(($('input[name="checkLiquidacion[]"]:checked').length > 1000000) ) {
        // Ninguno está seleccionado
        // alertNotificar("Debe seleccionar solo uno de la lista","error")
    }else{
        $('#cedula_ruc_cont').val('')
        $('#nombre_cont').val('')
        $('#apellido_cont').val('')
        $('#direccion_cont').val('')
        $('#id_contribuyente').val('')
        $('#id_liquidacion').val('')

        // var id_selecc= $('input[name="checkLiquidacion[]"]:checked').val();
        
        // vistacargando("m","Espere por favor")
        // $.get("tituloscoactiva/buscar-contribuyente/"+id_selecc, function(data){
        //     vistacargando("")
        //     if(data.error==true){
        //         alertNotificar(data.mensaje,"error");
        //         return;   
        //     }

        //     if(data.data==null){
        //         alertNotificar('No se encontro informacion del contribuyente',"error");
        //         return;   
        //     }
          
        //     $('#cedula_ruc_cont').val(data.data.ci_ruc)
        //     $('#nombre_cont').val(data.data.nombres)
        //     $('#apellido_cont').val(data.data.apellidos)
        //     $('#direccion_cont').val(data.data.direccion)
        //     $('#id_contribuyente').val(data.data.id)
        //     $('#id_liquidacion').val(id_selecc)
        //     $('#modalContri').modal('show')
           
        // }).fail(function(){
        //     vistacargando("")
        //     alertNotificar("Se produjo un error, por favor intentelo más tarde","error");  
        // }); 
        $('#modalContri').modal('show')
    }
}


function actualizaContribuyente(){
    var cedula= $('#cedula_ruc_cont').val()
    var nombres= $('#nombre_cont').val()
    var apellidos= $('#apellido_cont').val()
    var direccion= $('#direccion_cont').val()
    var id= $('#id_contribuyente').val()
    // var id_liquidacion= $('#id_liquidacion').val()
    // var id_liquidacion= $('#id_liquidacion').val()
    let valoresSeleccionados = [];

    $('input[name="checkLiquidacion[]"]:checked').each(function() {
        valoresSeleccionados.push($(this).val());
    });


    if(cedula=="" || cedula==null){
        alertNotificar("Ingrese la celula o ruc","error")
        return
    }
    if (isNaN(cedula)) {
        alertNotificar("La cédula o RUC debe contener solo números", "error");
        return;
    }
    
    if (cedula.length > 13) {
        alertNotificar("La cédula o RUC no debe tener más de 13 dígitos", "error");
        return;
    }
    if(nombres=="" || nombres==null){
        alertNotificar("Ingrese los nombres","error")
        return
    }
    if(apellidos=="" || apellidos==null){
        alertNotificar("Ingrese los apellidos","error")
        return
    }
    // if(direccion=="" || direccion==null){
    //     alertNotificar("Ingrese la direccion2","error")
    //     return
    // }
    
    swal({
        title: '¿Desea actualizar el registro?',
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

            vistacargando("m","Espere por favor");           

            $.ajaxSetup({
                headers: {
                    'X-CSRF-TOKEN': $('meta[name="csrf-token"]').attr('content')
                }
            });
            
            $.ajax({
                type:'POST',
                url: "tituloscoactiva/actualiza-contribuyente",
                data: { _token: $('meta[name="csrf-token"]').attr('content'),
                    cedula:cedula,
                    nombres:nombres,
                    apellidos:apellidos,
                    direccion:direccion,
                    id:id,
                    id_liquidacion:valoresSeleccionados

                },
                success: function(data){
                    console.log(data)
                    vistacargando("");                
                    if(data.error==true){
                       
                        alertNotificar(data.mensaje,'error');
                        return;                      
                    }
                    alertNotificar(data.mensaje,"success");                                   
                    limpiarCamposContribuyente()
                    $('#modalContri').modal('hide')
                }, error:function (data) {
                    vistacargando("");
                    alertNotificar('Ocurrió un error','error');
                }
            });

        }
        sweetAlert.close();   // ocultamos la ventana de pregunta
    });

}

function limpiarCamposContribuyente(){
    $('#cedula_ruc_cont').val('')
    $('#nombre_cont').val('')
    $('#apellido_cont').val('')
    $('#direccion_cont').val('')
    $('#id_contribuyente').val('')
}


function BuscaContribuyente(){
    let cedula=$('#cedula_ruc_cont').val();
    if(cedula==""){return}
    if(cedula.length<10){
        alertNotificar("El numero de identificacion debe tener mas de 9 digitos")
        return
    }
    vistacargando("m","Espere por favor")
    $.get("carga-info-persona/"+cedula, function(data){
        console.log(data)
        vistacargando("")
        if(data.error==true){
            alertNotificar(data.mensaje,"error");
            return;   
        }

        if(data.data==null){
            alertNotificar('No se encontro informacion del contribuyente',"error");
            return;   
        }
        
        // $('#cedula_ruc_cont').val(data.data.ci_ruc)
        $('#nombre_cont').val(data.data[0].nombre)
        $('#apellido_cont').val(data.data[0].apellido)
        // $('#direccion_cont').val(data.data.direccion)
        // $('#id_contribuyente').val(data.data.id)
        // $('#id_liquidacion').val(id_selecc)
        // $('#modalContri').modal('show')
        
    }).fail(function(){
        vistacargando("")
        alertNotificar("Se produjo un error, por favor intentelo más tarde","error");  
    }); 
}