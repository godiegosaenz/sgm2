
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