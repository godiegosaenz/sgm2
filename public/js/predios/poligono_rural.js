function consultarPredioR(){
    let poligono=$('#poligono').val()
    // $("#tablaPoligono").html('');
    // $('#tablaPoligono').empty(); 

    $("#tablaPredio tbody").html('');
    $('#tablaPredio').DataTable().destroy();
	$('#tablaPredio tbody').empty(); 
    vistacargando("m","Espere por favor")
    $.get("llenar-tabla-poligono/"+poligono, function(data){
        vistacargando("")
        if(data.error==true){
            alertNotificar(data.mensaje,"error");
            // return;   
        }

        if(data.error==true){
            $('#tablaPoligono').append(`<tr>
            <td colspan="2" style="text-align:center">No hay datos disponibles</td>`);
            alertNotificar(data.mensaje,'error');
            return;                      
        }
        console.log(data)
        if(data.resultado.length==0){
            $('#tablaPoligono').append(`<tr>
            <td colspan="2" style="text-align:center">No hay datos disponibles</td>`);
            return; 
        }
        $("#tablaPredio tbody").html('');
        $.each(data.resultado,function(i, item){
            $('#tablaPredio').append(`<tr>
                
                <td style="width:30%; text-align:center; vertical-align:middle">
                    ${item.Pre_CodigoCatastral}                    
                </td>
                <td style="width:30%; text-align:center; vertical-align:middle">
                    ${item.nombre}                     
                </td>
               
            
            </tr>`);
        })
        cargar_estilos_datatable('tablaPredio',poligono)

    }).fail(function(){
        vistacargando("")
        $('#tablaPoligono').append(`<tr>
        <td colspan="2" style="text-align:center">No hay datos disponibles</td>`);
        alertNotificar("Se produjo un error, por favor intentelo más tarde","error");  
    });
}

function cargar_estilos_datatable(idtabla, poligono){
	tablaPredioNC = $("#"+idtabla).DataTable({
		'paging'      : true,
		'searching'   : true,
		'ordering'    : true,
		'info'        : true,
		'autoWidth'   : true,
		"destroy":true,
        order: [[ 0, "asc" ]],
		pageLength: 10,
		sInfoFiltered:false,
		language: {
			url: 'json/datatables/spanish.json',
		},
         buttons: [
                {
                    extend: 'excelHtml5',
                    text: '📥 Descargar Excel',
                    title: 'LISTADO DE PREDIOS RURALES CON POLIGONO '+poligono,
                    exportOptions: {
                        columns: [0, 1] // columnas a exportar
                    }
                }
            ]
	}); 
	$('.collapse-link').click();
	$('.datatable_wrapper').children('.row').css('overflow','inherit !important');

	$('.table-responsive').css({'padding-top':'12px','padding-bottom':'12px', 'border':'0', 'overflow-x':'inherit'});	
}

$('#btnExcel').on('click', function () {
    tablaPredioNC.button('.buttons-excel').trigger();
});
