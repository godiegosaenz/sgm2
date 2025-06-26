
function verPatentePdf1(ruta){
    var iframe=$('#iframePdf');
    iframe.attr("src", "documento/"+ruta);   
    $("#vinculo").attr("href", 'descargar-documento/'+ruta);
    $("#documentopdf").modal("show");
}

function verPatentePdf(id){
    vistacargando("m","Espere por favor")
    $.get('reporte/'+id, function(data){
        vistacargando("")
        if(data.error==true){
            // alertNotificar(data.mensaje,"error");
            alert(data.mensaje);
            return;   
        }

        alertNotificar("El documento se descargara en unos segundos","success")
        verpdf(data.pdf)

    }).fail(function(){
        vistacargando("")
        alertNotificar('Ocurrió un error','error');
    });
}

function verpdf(ruta){
    var iframe=$('#iframePdf');
    iframe.attr("src", "documento/"+ruta);   
    $("#vinculo").attr("href", 'descargar-documento/'+ruta);
    $("#documentopdf").modal("show");
}

function eliminarTitulo(id){
    $('#tituloEliminaModal').modal('show')
    $('#id_impuesto').val(id)
}
function procesaEliminacion(id){
    vistacargando("m","Espere por favor")
    $.get("../elimina-titulo-patente/"+id, function(data){
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

function cancelarBaja(){
    $('#motivo_baja').val('')
    $('#id_impuesto').val('')
    $('#tituloEliminaModal').modal('hide')
}

$("#form_baja").submit(function(e){
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
        let url_form="baja-titulo-patente"
        var FrmData=$("#form_baja").serialize();

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