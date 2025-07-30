function generarPdf(id){
    vistacargando("m","Espere por favor")
    $.get("../transito-imprimir/"+id, function(data){
        vistacargando("")
        if(data.error==true){
            alertNotificar(data.mensaje,"error");
            return;   
        }
        verpdf(data.pdf)
        console.log(data)
        
        
    }).fail(function(){
        vistacargando("")
        alertNotificar("Se produjo un error, por favor intentelo más tarde","error");  
    });
}

function verpdf(ruta){

    var iframe=$('#iframePdf');
    iframe.attr("src", "../patente/documento/"+ruta);   
    $("#vinculo").attr("href", '../patente/descargar-documento/'+ruta);
    $("#documentopdf").modal("show");
}

function eliminarTitulo(id){
    $('#tituloEliminaModal').modal('show')
    $('#id_impuesto').val(id)
}
function procesaEliminacion(id){
    vistacargando("m","Espere por favor")
    $.get("../elimina-titulo-transito/"+id, function(data){
        vistacargando("")
        if(data.error==true){
            alertNotificar(data.mensaje,"error");
            return;   
        }
        verpdf(data.pdf)
        console.log(data)
        
        
    }).fail(function(){
        vistacargando("")
        alertNotificar("Se produjo un error, por favor intentelo más tarde","error");  
    });
}

$("#form_jefe_area").submit(function(e){
    e.preventDefault();
    
    //validamos los campos obligatorios
    let motivo_baja=$('#motivo_baja').val()
    let id_impuesto=$('#id_impuesto').val()

    if(motivo_baja=="" || motivo_baja==null){
        alertNotificar("Debe ingresar el motivo","error")
        $('#motivo_baja').focus()
        return
    } 

    if(confirm('¿Estas seguro que quieres dar de baja al titulo?'))
    {
        vistacargando("m","Espere por favor")
        $.ajaxSetup({
            headers: {
                'X-CSRF-TOKEN': $('meta[name="csrf-token"]').attr('content')
            }
        });

        //comprobamos si es registro o edicion
        let tipo="POST"
        let url_form="../baja-titulo-transito"
        var FrmData=$("#form_jefe_area").serialize();

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
            
                alertNotificar(data.mensaje,"success");
                cancelarBaja()
                setTimeout(function() {
                    location.href = location.href;
                }, 1000);
                                
            }, error:function (data) {
                console.log(data)

                vistacargando("");
                alertNotificar('Ocurrió un error','error');
            }
        });
    }
})

function cancelarBaja(){
    $('#motivo_baja').val('')
    $('#id_impuesto').val('')
    $('#tituloEliminaModal').modal('hide')
}

function nuevoArea(){
    alert("aa")
}

function nuevoJefeArea(){
    limpiarCampos()
    $('#NuevoJefeAreaModal').modal('show')
}

function cancelarJefeArea(){
    $('#NuevoJefeAreaModal').modal('hide')
    limpiarCampos()

}

function limpiarCampos(){
    $('#user_id').val('')
    $('#area_id').val('')
    $('#jefe_area_id').val('')
    $('#btn_tipo').html('Guardar')
}

$("#form_jefe_area").submit(function(e){
    e.preventDefault();
    
    //validamos los campos obligatorios
    let jefe_area_id=$('#jefe_area_id').val()
    let area_id=$('#area_id').val()
   
    let user_id=$('#user_id').val()

    if(area_id=="" || area_id==null){
        alertNotificar("Seleccione el area","error")
        return
    } 

    if(user_id=="" || user_id==null){
        alertNotificar("Seleccione el usuario","error")
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
        let url_form="mantenimiento-jefe-area"
        var FrmData=$("#form_jefe_area").serialize();

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
            
                alertNotificar(data.mensaje,"success");
                
                setTimeout(function() {
                    cancelarJefeArea()
                    location.href = location.href;
                }, 1000);
                                
            }, error:function (data) {
                console.log(data)

                vistacargando("");
                alertNotificar('Ocurrió un error','error');
            }
        });
    
})

function editar(id){
    $('#btn_tipo').html('Actualizar')
    vistacargando("m","Espere por favor")
    $.get("editar-jefe-area/"+id, function(data){
        console.log(data)
        vistacargando("")
        if(data.error==true){
            alertNotificar(data.mensaje,"error");
            return;   
        }
        $('#NuevoJefeAreaModal').modal('show')
        $('#area_id').val(data.resultado.id_area)
        $('#user_id').val(data.resultado.id_usuario)
        $('#jefe_area_id').val(id)
        
        
    }).fail(function(){
        vistacargando("")
        alertNotificar("Se produjo un error, por favor intentelo más tarde","error");  
    });
}

function eliminar(id){
    if(confirm('¿Quiere eliminar el registro?')){
        vistacargando("m","Espere por favor")
        $.get("eliminar-jefe-area/"+id, function(data){
            vistacargando("")
            if(data.error==true){
                alertNotificar(data.mensaje,"error");
                return;   
            }
    
            alertNotificar(data.mensaje,"success");
            setTimeout(function() {
                cancelarJefeArea()
                location.href = location.href;
            }, 1000);
           
        }).fail(function(){
            vistacargando("")
            alertNotificar("Se produjo un error, por favor intentelo más tarde","error");  
        });
    }
}