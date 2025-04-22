function cambioData(){
    $('#btn_descargar').hide()
    $('#grafico_urbano').hide()
    $('#grafico_rural').hide()
    $('#btn_consultar').show()
}
function mostrarData(){
    $('#btn_descargar').hide()
    $('#btn_consultar').show()
    let filtroDesde=$('#filtroDesde').val()
    let filtroHasta=$('#filtroHasta').val()
    let filtroRango=$('#filtroRango').val()
    let filtroTipo=$('#filtroTipo').val()

    if(filtroDesde<0 ){
        alertNotificar("El valor desde debe ser mayor a cero","error")
        $('#filtroDesde').focus()
       
        return
    }
    
    if(Number(filtroHasta)<Number(filtroDesde)){        
        alertNotificar("El valor hasta "+filtroHasta +" debe ser mayor a cero y mayor a desde "+filtroDesde +"","error")
        $('#filtroHasta').focus()
        return
    }

    if(filtroRango<=0){
        alertNotificar("El valor rango debe ser mayor a cero","error")
        $('#filtroRango').focus()
        return
    }

    if(filtroTipo=="Urbano"){
        $('#grafico_urbano').show()
        $('#grafico_rural').hide()
    }else if(filtroTipo=="Rural"){
        $('#grafico_urbano').hide()
        $('#grafico_rural').show()
    }else{
        $('#grafico_urbano').show()
        $('#grafico_rural').show()
    }

    $("#formAnaliticaPredio").submit()
}

function descargarPdf(){
    
    vistacargando("m","Espere por favor")
    $.ajaxSetup({
        headers: {
            'X-CSRF-TOKEN': $('meta[name="csrf-token"]').attr('content')
        }
    });

    var tipo="POST"
  
    let filtroDesde=$('#filtroDesde').val()
    let filtroHasta=$('#filtroHasta').val()
    let filtroRango=$('#filtroRango').val()
    let filtroTipo=$('#filtroTipo').val()

    $.ajax({
            
        type: tipo,
        url: "reporte-predio-rango",
        method: tipo,             
        data: {
            filtroDesde: filtroDesde,
            filtroHasta: filtroHasta,
            filtroRango: filtroRango,
            filtroTipo: filtroTipo
        },  
		
        // processData:false, 

        success: function(response) {
            vistacargando("")
            if(response.error==true){
                alertNotificar(response.mensaje,"error")
                return
            }
            alertNotificar("El documento se descargara en unos segundos","success")
            window.location.href="descargar-reporte/"+response.pdf
        },
        error: function(xhr, status, error) {
            vistacargando("")
            console.error("Error al obtener los datos:", error);
        }
    });
}

function verpdf(ruta){
    var iframe=$('#iframePdf');
    iframe.attr("src", "patente/documento/"+ruta);   
    $("#vinculo").attr("href", 'patente/descargar-documento/'+ruta);
    $("#documentopdf").modal("show");
}

let myChart = null; // Variable global
let myChartRural=null;

$("#formAnaliticaPredio").submit(function(e){
    e.preventDefault();
    var ruta="carga-data"
        
    vistacargando("m","Espere por favor")
    $.ajaxSetup({
        headers: {
            'X-CSRF-TOKEN': $('meta[name="csrf-token"]').attr('content')
        }
    });

    var tipo="POST"
  
    var FrmData=$("#formAnaliticaPredio").serialize();
    let filtroDesde=$('#filtroDesde').val()
    let filtroHasta=$('#filtroHasta').val()
    let filtroRango=$('#filtroRango').val()
    let filtroTipo=$('#filtroTipo').val()

    $.ajax({
            
        type: tipo,
        url: ruta,
        method: tipo,             
        data: {
            filtroDesde: filtroDesde,
            filtroHasta: filtroHasta,
            filtroRango: filtroRango,
            filtroTipo: filtroTipo
        },  
		
        // processData:false, 

        success: function(response) {
            vistacargando("")

            if(response.tipo_busqueda=="U"){
                let labels = [];
                let data = [];
                let backgroundColors = [];
                let borderColors = [];
        
                response.resultados_urbano.forEach(item => {
                    labels.push(item.rango);
                    data.push(item.cantidad);
        
                    // Generar color aleatorio
                    let r = Math.floor(Math.random() * 255);
                    let g = Math.floor(Math.random() * 255);
                    let b = Math.floor(Math.random() * 255);
        
                    backgroundColors.push(`rgba(${r}, ${g}, ${b}, 0.2)`);
                    borderColors.push(`rgba(${r}, ${g}, ${b}, 1)`);
                });
                
                // ⚠️ Destruir gráfico anterior si ya existe
                if (myChart !== null) {
                    myChart.destroy();
                }

                const ctx = document.getElementById('myChart').getContext('2d');
                myChart = new Chart(ctx, {
                    type: 'bar',
                    data: {
                        labels: labels,
                        datasets: [{
                            label: '# de Predios por Rango Urbano',
                            data: data,
                            backgroundColor: backgroundColors,
                            borderColor: borderColors,
                            borderWidth: 1
                        }]
                    },
                    options: {
                        responsive: true,
                        scales: {
                            y: {
                                beginAtZero: true,
                                ticks: {
                                    precision: 0 // evita decimales si no los necesitas
                                }
                            }
                        }
                    }
                });
            }else if(response.tipo_busqueda=="R"){
                let labels_rural = [];
                let data_rural = [];
                let backgroundColors_rural = [];
                let borderColors_rural = [];
        
                response.resultados_rural.forEach(item => {
                    labels_rural.push(item.rango);
                    data_rural.push(item.cantidad);
        
                    // Generar color aleatorio
                    let r = Math.floor(Math.random() * 255);
                    let g = Math.floor(Math.random() * 255);
                    let b = Math.floor(Math.random() * 255);
        
                    backgroundColors_rural.push(`rgba(${r}, ${g}, ${b}, 0.2)`);
                    borderColors_rural.push(`rgba(${r}, ${g}, ${b}, 1)`);
                });
                
                // ⚠️ Destruir gráfico anterior si ya existe
                if (myChartRural !== null) {
                    myChartRural.destroy();
                }

                const ctx = document.getElementById('myChartRural').getContext('2d');
                myChartRural = new Chart(ctx, {
                    type: 'bar',
                    data: {
                        labels: labels_rural,
                        datasets: [{
                            label: '# de Predios por Rango Rural',
                            data: data_rural,
                            backgroundColor: backgroundColors_rural,
                            borderColor: borderColors_rural,
                            borderWidth: 1
                        }]
                    },
                    options: {
                        responsive: true,
                        scales: {
                            y: {
                                beginAtZero: true,
                                ticks: {
                                    precision: 0 // evita decimales si no los necesitas
                                }
                            }
                        }
                    }
                });
            }else{

                let labels = [];
                let data = [];
                let backgroundColors = [];
                let borderColors = [];
        
                response.resultados_urbano.forEach(item => {
                    labels.push(item.rango);
                    data.push(item.cantidad);
        
                    // Generar color aleatorio
                    let r = Math.floor(Math.random() * 255);
                    let g = Math.floor(Math.random() * 255);
                    let b = Math.floor(Math.random() * 255);
        
                    backgroundColors.push(`rgba(${r}, ${g}, ${b}, 0.2)`);
                    borderColors.push(`rgba(${r}, ${g}, ${b}, 1)`);
                });
                
                // ⚠️ Destruir gráfico anterior si ya existe
                if (myChart !== null) {
                    myChart.destroy();
                }

                const ctx = document.getElementById('myChart').getContext('2d');
                myChart = new Chart(ctx, {
                    type: 'bar',
                    data: {
                        labels: labels,
                        datasets: [{
                            label: '# de Predios por Rango Urbano',
                            data: data,
                            backgroundColor: backgroundColors,
                            borderColor: borderColors,
                            borderWidth: 1
                        }]
                    },
                    options: {
                        responsive: true,
                        scales: {
                            y: {
                                beginAtZero: true,
                                ticks: {
                                    precision: 0 // evita decimales si no los necesitas
                                }
                            }
                        }
                    }
                });

                let labels_rural = [];
                let data_rural = [];
                let backgroundColors_rural = [];
                let borderColors_rural = [];
        
                response.resultados_rural.forEach(item => {
                    labels_rural.push(item.rango);
                    data_rural.push(item.cantidad);
        
                    // Generar color aleatorio
                    let r = Math.floor(Math.random() * 255);
                    let g = Math.floor(Math.random() * 255);
                    let b = Math.floor(Math.random() * 255);
        
                    backgroundColors_rural.push(`rgba(${r}, ${g}, ${b}, 0.2)`);
                    borderColors_rural.push(`rgba(${r}, ${g}, ${b}, 1)`);
                });
                
                // ⚠️ Destruir gráfico anterior si ya existe
                if (myChartRural !== null) {
                    myChartRural.destroy();
                }

                const ctxRural = document.getElementById('myChartRural').getContext('2d');
                myChartRural = new Chart(ctxRural, {
                    type: 'bar',
                    data: {
                        labels: labels_rural,
                        datasets: [{
                            label: '# de Predios por Rango Rural',
                            data: data_rural,
                            backgroundColor: backgroundColors_rural,
                            borderColor: borderColors_rural,
                            borderWidth: 1
                        }]
                    },
                    options: {
                        responsive: true,
                        scales: {
                            y: {
                                beginAtZero: true,
                                ticks: {
                                    precision: 0 // evita decimales si no los necesitas
                                }
                            }
                        }
                    }
                });

            }
            $('#btn_descargar').show()
            $('#btn_consultar').hide()
        },
        error: function(xhr, status, error) {
            vistacargando("")
            console.error("Error al obtener los datos:", error);
        }
    });
})