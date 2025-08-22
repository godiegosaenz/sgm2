function cambiaContrasenia(){
    let actual=$('#password_act').val()
    let nueva=$('#password_new').val()
    let repite=$('#password_rep').val()

    if(actual==""){
        alertNotificar("Ingrese la contraseña actual","error")
        $('#password_act').focus()
        return
    }

    if(nueva==""){
        alertNotificar("Ingrese la nueva contraseña","error")
        $('#password_new').focus()
        return
    }

    if(repite==""){
        alertNotificar("Repita la nueva contraseña","error")
        $('#password_rep').focus()
        return
    }

    if(nueva != repite){
        alertNotificar("Las nuevas contraseñas no coinciden","error")
        return
    }

    if(nueva.length<8){
        alertNotificar("La nueva contraseña debe tener al menos 8 caracteres","error")
        return
    }

    $("#form_contrasenia").submit()
}

function limpiarCampos(){
    $('#password_act').val('')
    $('#password_new').val('')
    $('#password_rep').val('')
}


$("#form_contrasenia").submit(function(e){
    e.preventDefault();
    
   
    vistacargando("m","Espere por favor")
    $.ajaxSetup({
        headers: {
            'X-CSRF-TOKEN': $('meta[name="csrf-token"]').attr('content')
        }
    });

    //comprobamos si es registro o edicion
    let tipo="POST"
    let url_form="guardar-contrasenia"
    // var FrmData=$("#form_contrasenia").serialize();
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
            limpiarCampos()     
        }, error:function (data) {
            console.log(data)

            vistacargando("");
            alertNotificar('Ocurrió un error','error');
        }
    });
    
})