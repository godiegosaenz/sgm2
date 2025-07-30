$("#form_firma").submit(function(e){
    e.preventDefault();
    
    //validamos los campos obligatorios
    let p12=$('#p12').val()
    let password=$('#password').val()
   
    if(p12=="" || p12==null){
        alertNotificar("Seleccione el archivo","error")
        $('#p12').focus()
        return
    } 

    if(password=="" || password==null){
        alertNotificar("Ingrese la contraseña de su firma electronica","error")
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
    let url_form="mantenimiento-firma"
    // var FrmData=$("#form_firma").serialize();
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
            
            setTimeout(function() {
                cancelarFirma()
                location.href = location.href;
            }, 1000);
                            
        }, error:function (data) {
            console.log(data)

            vistacargando("");
            alertNotificar('Ocurrió un error','error');
        }
    });
    
})

function cancelarFirma(){
    $('#NuevaFirmaModal').modal('hide')
    limpiarCampos()

}

function limpiarCampos(){
    $('#p12').val('')
    $('#password').val('')
    $('#btn_tipo').html('Guardar')
}

function nuevaFirma(){
    limpiarCampos()
    $('#NuevaFirmaModal').modal('show')
}

function eliminar(id){
    if(confirm('¿Quiere eliminar el registro?')){
        vistacargando("m","Espere por favor")
        $.get("eliminar-firma/"+id, function(data){
            vistacargando("")
            if(data.error==true){
                alertNotificar(data.mensaje,"error");
                return;   
            }
    
            alertNotificar(data.mensaje,"success");
            setTimeout(function() {
                location.href = location.href;
            }, 1000);
           
        }).fail(function(){
            vistacargando("")
            alertNotificar("Se produjo un error, por favor intentelo más tarde","error");  
        });
    }
}
