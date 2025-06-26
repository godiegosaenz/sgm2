globalThis.TieneDeuda=0
$('#cedula').on('keydown', function(e) {
    if (e.key === "Enter") {
        e.preventDefault();
        llenarTabla()
        // Tu lógica aquí
    }
});

globalThis.CedulaCont=""
globalThis.TipoSelecc=""
function llenarTabla(){
    $('#btn_genera').prop("disabled",true)
    let cedula = $('#cedula').val().trim();
   
    if (cedula === '') {
        $('#cedula').addClass('is-invalid');
        alertNotificar('Ingrese la cedula','error')
        return
        
    }
      
    $("#tablaDeudas tbody").html('');
    $('#tablaDeudas tbody').empty(); 
    var num_col = $("#tablaDeudas thead tr th").length; //obtenemos el numero de columnas de la tabla
    vistacargando("m", "Espere por favor")
    $.get('buscar-deudas/'+cedula, function(data){
        console.log(data)
       
        vistacargando("")
        if(data.error==true){
			$("#tablaDeudas tbody").html('');
			$("#tablaDeudas tbody").html(`<tr><td colspan="${num_col}" style="text-align:center>No existen registros</td></tr>`);
			alertNotificar(data.mensaje,"error");
            // cancelar()
			return;   
		}
		if(data.error==false){
			if(data.resultado.length==0){

                // CedulaCont=cedula
                // TipoSelecc="SD"
				$("#tablaDeudas tbody").html('');
				$("#tablaDeudas tbody").html(`<tr><td colspan="${num_col}" style="text-align:center">No existen registros</td></tr>`);
				// alertNotificar("No se encontró información","error");
                // cancelar()
				return;
			}

            $('#btn_genera').prop("disabled",false)
			
			$("#tablaDeudas tbody").html('');
            let total_adeudado=0
            let tipo_aux=""
			$.each(data.resultado,function(i, item){
                tipo_aux=item.tipo
                // let anio=item.CarVe_NumTitulo               
                // anio=anio.split("-");
                // let icono=`<i class="bi bi-circle-fill" style="color:red;"></i>`;
                // if(item.CarVe_Estado=='C'){
                //     icono=`<i class="bi bi-circle-fill" style="color:green;"></i>`;
                // }
                let tipo=""
                if(item.tipo!="SD"){
                   tipo=item.tipo
                }
				$('#tablaDeudas').append(`<tr>
                                               

                                                <td style="width:40%;  text-align:left; vertical-align:middle">
                                                    ${item.nombres}
                                                </td>
                                               
                                                <td style="width:15%; text-align:center; vertical-align:middle">
                                                    ${tipo}
                                                </td>

                                                <td style="width:15%; text-align:center; vertical-align:middle">
                                                    ${item.total}
                                                </td>

                                                <td style="width:10%; text-align:center; vertical-align:middle">
                                                   <button type="button" class="btn btn-primary btn-sm" onclick="verDetalle('${cedula}','${item.tipo}')">Detalle</button>                                                    
                                                </td>

                                                
											
										</tr>`);

                total_adeudado=Number(total_adeudado)+Number(item.total)
			})
          
            TieneDeuda=total_adeudado

            CedulaCont=cedula
            TipoSelecc=tipo_aux
          
            // alert(TipoSelecc)
		}
        
    
    }).fail(function(){
        vistacargando("")
        alertNotificar("Se produjo un error, por favor intentelo más tarde","error");  
        $("#tablaDeudas tbody").html('');
		$("#tablaDeudas tbody").html(`<tr><td colspan="${num_col}" style="text-align:center">Se produjo un error, por favor intentelo más tarde</td></tr>`);
    });
}

function verDetalle(cedula, tipo) {
    
    $("#tablaDetalleDeudas tbody").html('');
    $('#tablaDetalleDeudas tbody').empty(); 
    var num_col = $("#tablaDetalleDeudas thead tr th").length; //obtenemos el numero de columnas de la tabla
    vistacargando("m", "Espere por favor")
    $.get('buscar-detalle-deudas/'+cedula+'/'+tipo, function(data){
        console.log(data)
       
        vistacargando("")
        if(data.error==true){
			$("#tablaDetalleDeudas tbody").html('');
			$("#tablaDetalleDeudas tbody").html(`<tr><td colspan="${num_col}" style="text-align:center>No existen registros</td></tr>`);
			alertNotificar(data.mensaje,"error");
            // cancelar()
			return;   
		}
		if(data.error==false){
			if(data.resultado.length==0){
				$("#tablaDetalleDeudas tbody").html('');
				$("#tablaDetalleDeudas tbody").html(`<tr><td colspan="${num_col}" style="text-align:center">No existen registros</td></tr>`);
				alertNotificar("No se encontró información","error");
                // cancelar()
				return;
			}
			$('#modalDetalle').modal('show')

			$("#tablaDetalleDeudas tbody").html('');
            
            let total=0
			$.each(data.resultado,function(i, item){
                total=total + Number(item.total_complemento)
              
				$('#tablaDetalleDeudas').append(`<tr>
                                               
                                                <td style="width:20%;  text-align:left; vertical-align:middle">
                                                    ${item.clave_cat}
                                                </td>
                                                <td style="width:10%;  text-align:left; vertical-align:middle">
                                                    ${item.anio}
                                                </td>
                                               
                                                <td style="width:15%; text-align:right; vertical-align:middle">
                                                    ${item.saldo}
                                                </td>

                                                <td style="width:15%; text-align:right; vertical-align:middle">
                                                    ${item.desc}
                                                </td>

                                                 <td style="width:15%;  text-align:right; vertical-align:middle">
                                                    ${item.recargos}
                                                </td>
                                               
                                                <td style="width:15%; text-align:right; vertical-align:middle">
                                                    ${item.interes}
                                                </td>

                                                <td style="width:15%; text-align:right; vertical-align:middle">
                                                    ${item.total_complemento}
                                                </td>


                                               
                                                
											
										</tr>`);
			})
          
            $('#ci_ruc_deudor').html(cedula)
            $('#nombre_deudor').html(data.resultado[0].nombres)
            $('#tipo_deudor').html(tipo)
            $('#total_deudor').html(total)
          
            
		}
    
    }).fail(function(){
        vistacargando("")
        alertNotificar("Se produjo un error, por favor intentelo más tarde","error");  
        $("#tablaDetalleDeudas tbody").html('');
		$("#tablaDetalleDeudas tbody").html(`<tr><td colspan="${num_col}" style="text-align:center">Se produjo un error, por favor intentelo más tarde</td></tr>`);
    })
    
}

function generarTitulos(){
    if(TieneDeuda>0){
        // alertNotificar("El contribuyente mantiene una deuda pendiente de "+TieneDeuda, "error")
        swal("El contribuyente mantiene una deuda pendiente de $"+TieneDeuda, "", "error");     
        return  
    }
// descargar-reporte
    vistacargando("m", "Espere por favor")
    $.get('generar-nd/'+CedulaCont+'/'+TipoSelecc, function(data){
        console.log(data)       
        vistacargando("")
        if(data.error==true){
			alertNotificar(data.mensaje,"error");
			return;   
		}
        alertNotificar("El documento se descargara en unos segundos","success")
        // window.location.href="descargar-reporte/"+response.pdf
        verpdf(data.pdf)

    }).fail(function(){
        vistacargando("")
        alertNotificar("Se produjo un error, por favor intentelo más tarde","error");  
        
    })
}

function verpdf(ruta){
    var iframe=$('#iframePdf');
    iframe.attr("src", "nodeudor/documento/"+ruta);   
    $("#vinculo").attr("href", 'descargar-reporte/'+ruta);
    $("#documentopdf").modal("show");
}


 // Detectar si el sistema está en modo oscuro
if (window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches) {
document.body.classList.add('dark-mode');
} else {
document.body.classList.remove('dark-mode');
}

// Escuchar cambios en tiempo real (opcional)
window.matchMedia('(prefers-color-scheme: dark)').addEventListener('change', e => {
    const darkModeOn = e.matches;
    if (darkModeOn) {
        document.body.classList.add('dark-mode');
    } else {
        document.body.classList.remove('dark-mode');
    }
});

function nuevoCliente(){
    $('#modalContri').modal('show')

}


function actualizaContribuyente(){
    var cedula= $('#cedula_ruc_cont').val()
    var nombres= $('#nombre_cont').val()
    var apellidos= $('#apellido_cont').val()
    var direccion= $('#direccion_cont').val()
    var fnacimiento_cont= $('#fnacimiento_cont').val()
   
    if(cedula=="" || cedula==null){
        alertNotificar("Ingrese la celula o ruc","error")
        return
    }
    if (isNaN(cedula)) {
        alertNotificar("La cédula o RUC debe contener solo números", "error");
        return;
    }
    
    if (cedula.length > 13) {
        alertNotificar("La cédula o RUC no debe tener más de 13 dígitos", "error");
        return;
    }
    if(nombres=="" || nombres==null){
        alertNotificar("Ingrese los nombres","error")
        return
    }
    if(apellidos=="" || apellidos==null){
        alertNotificar("Ingrese los apellidos","error")
        return
    }
    if(direccion=="" || direccion==null){
        alertNotificar("Ingrese la direccion","error")
        return
    }

    if(fnacimiento_cont=="" || fnacimiento_cont==null){
        alertNotificar("Ingrese la direccion","error")
        return
    }
    
    vistacargando("m","Espere por favor");           

    $.ajaxSetup({
        headers: {
            'X-CSRF-TOKEN': $('meta[name="csrf-token"]').attr('content')
        }
    });
    
    $.ajax({
        type:'POST',
        url: "nodeudor/guarda-contribuyente",
        data: { _token: $('meta[name="csrf-token"]').attr('content'),
            cedula:cedula,
            nombres:nombres,
            apellidos:apellidos,
            direccion:direccion,
            fnacimiento_cont:fnacimiento_cont,
            
        },
        success: function(data){
            console.log(data)
            vistacargando("");                
            if(data.error==true){
                
                alertNotificar(data.mensaje,'error');
                return;                      
            }
            alertNotificar(data.mensaje,"success");                                   
            limpiarCamposContribuyente()
            $('#modalContri').modal('hide')
        }, error:function (data) {
            vistacargando("");
            alertNotificar('Ocurrió un error','error');
        }
    });

       
}

function limpiarCamposContribuyente(){
    $('#cedula_ruc_cont').val('')
    $('#nombre_cont').val('')
    $('#apellido_cont').val('')
    $('#direccion_cont').val('')
    $('#fnacimiento_cont').val('')
}

// function imprimirTitulos(){
//     vistacargando("m", "Espere por favor")
//     $.get('generar-titulos/'+CedulaCont+'/'+TipoSelecc, function(data){
//         console.log(data)       
//         vistacargando("")
//         if(data.error==true){
// 			alertNotificar(data.mensaje,"error");
// 			return;   
// 		}
//         alertNotificar("El documento se descargara en unos segundos","success")
//         // window.location.href="descargar-reporte/"+response.pdf
//         verpdf(data.pdf)

//     }).fail(function(){
//         vistacargando("")
//         alertNotificar("Se produjo un error, por favor intentelo más tarde","error");  
        
//     })
// }