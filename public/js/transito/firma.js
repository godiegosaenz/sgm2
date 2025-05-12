function firmar(){
    $('#btn_descargar').hide()
    $('#btn_consultar').show()
    let archivo=$('#archivo').val()
    let clave=$('#clave').val()
    let documento=$('#documento').val()

    if(archivo==""){
        alertNotificar("Debe seleccionar el archivo","error")
        $('#archivo').focus()       
        return
    }

    if(clave==""){
        alertNotificar("Debe ingresar la clave","error")
        $('#clave').focus()       
        return
    }
    
    if(documento==""){
        alertNotificar("Debe seleccionar el documento","error")
        $('#documento').focus()       
        return
    }

    $("#formReporteria").submit()
}

$("#formReporteria").submit(function(e){
    
    e.preventDefault();
    var ruta="pago-consulta-transito"
        
    vistacargando("m","Espere por favor")
    $.ajaxSetup({
        headers: {
            'X-CSRF-TOKEN': $('meta[name="csrf-token"]').attr('content')
        }
    });

    var tipo="POST"
  
    let archivo=$('#archivo').val()
    let clave=$('#clave').val()
    let documento=$('#documento').val()

    $.ajax({
            
        type: tipo,
        url: ruta,
        method: tipo,             
        data: {
            archivo: archivo,
            clave: clave,
            documento: documento
        },  
		
        // processData:false, 

        success: function(response) {
            console.log(response)
            vistacargando("")

            if(response.error==true){
                alertNotificar(response.mensaje,"error")
                return;   
            }
         
            if(response.error==false){
                alertNotificar("El documento se descargara en unos segundos")
            }

            
        },
        error: function(xhr, status, error) {
            vistacargando("")
            console.error("Error al obtener los datos:", error);
        }
    });
})