function actualizarContribuyente(){
    if ($('input[name="checkLiquidacion[]"]:checked').length == 0) {
        alertNotificar("Debe seleccionar al menos uno de la lista","error")
    } else if(($('input[name="checkLiquidacion[]"]:checked').length > 1) ) {
        // Ninguno está seleccionado
        alertNotificar("Debe seleccionar solo uno de la lista","error")
    }else{
        $('#cedula_ruc_cont').val('')
        $('#nombre_cont').val('')
        $('#apellido_cont').val('')
        $('#direccion_cont').val('')
        $('#id_contribuyente').val('')
        $('#id_liquidacion').val('')

        var id_selecc= $('input[name="checkLiquidacion[]"]:checked').val();
        
        vistacargando("m","Espere por favor")
        $.get("tituloscoactiva/buscar-contribuyente/"+id_selecc, function(data){
            vistacargando("")
            if(data.error==true){
                alertNotificar(data.mensaje,"error");
                return;   
            }

            if(data.data==null){
                alertNotificar('No se encontro informacion del contribuyente',"error");
                return;   
            }
          
            $('#cedula_ruc_cont').val(data.data.ci_ruc)
            $('#nombre_cont').val(data.data.nombres)
            $('#apellido_cont').val(data.data.apellidos)
            $('#direccion_cont').val(data.data.direccion)
            $('#id_contribuyente').val(data.data.id)
            $('#id_liquidacion').val(id_selecc)
            $('#modalContri').modal('show')
           
        }).fail(function(){
            vistacargando("")
            alertNotificar("Se produjo un error, por favor intentelo más tarde","error");  
        });
        
    }
}


function actualizaContribuyente(){
    var cedula= $('#cedula_ruc_cont').val()
    var nombres= $('#nombre_cont').val()
    var apellidos= $('#apellido_cont').val()
    var direccion= $('#direccion_cont').val()
    var id= $('#id_contribuyente').val()
    var id_liquidacion= $('#id_liquidacion').val()

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
    if(direccion=="" || direccion==null){
        alertNotificar("Ingrese la direccion","error")
        return
    }
    
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
                    id_liquidacion:id_liquidacion

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
   