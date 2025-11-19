function generarParte(){
    var fecha=$('#fecha').val()
    vistacargando("m","Espere por favor")
    $.get("generar-parte-diario/"+fecha, function(data){
        console.log(data)
        vistacargando("")
        if(data.error==true){
            alertNotificar(data.mensaje,"error");
            return;   
        }

        // alertNotificar(data.mensaje,"success");
        alertNotificar('El documento se descargara en unos segundos...',"success");

        window.location.href="descargar-parte/"+data.pdf

        // setTimeout(function() {
        //     location.href = location.href;
        // }, 1000);
        
    }).fail(function(){
        vistacargando("")
        alertNotificar("Se produjo un error, por favor intentelo más tarde","error");  
    });
    
}