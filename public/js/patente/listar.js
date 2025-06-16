
function verPatentePdf(ruta){
    var iframe=$('#iframePdf');
    iframe.attr("src", "documento/"+ruta);   
    $("#vinculo").attr("href", 'descargar-documento/'+ruta);
    $("#documentopdf").modal("show");
}