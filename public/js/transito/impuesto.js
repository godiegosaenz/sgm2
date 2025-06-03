globalThis.FormAccionRango=""
globalThis.IdEditarRango=
$('#modalEditarRangos').on('shown.bs.modal', function () {
    // Tu acción aquí
    llenarTabla()
    FormAccionRango='R'
});


function llenarTabla(){
    
    $("#tablaRangos tbody").html('');
    $('#tablaRangos tbody').empty(); 
    var num_col = $("#tablaRangos thead tr th").length; //obtenemos el numero de columnas de la tabla
    vistacargando("m", "Espere por favor")
    $.get('llenar-tabla-rango', function(data){
        console.log(data)
       
        vistacargando("")
        if(data.error==true){
			$("#tablaRangos tbody").html('');
			$("#tablaRangos tbody").html(`<tr><td colspan="${num_col}" style="text-align:center>No existen registros</td></tr>`);
			alertNotificar(data.mensaje,"error");
            // cancelar()
			return;   
		}
		if(data.error==false){
			if(data.resultado.length==0){
				$("#tablaRangos tbody").html('');
				$("#tablaRangos tbody").html(`<tr><td colspan="${num_col}" style="text-align:center">No existen registros</td></tr>`);
				alertNotificar("No se encontró información","error");
                // cancelar()
				return;
			}
			
			$("#tablaRangos tbody").html('');
         
			$.each(data.resultado,function(i, item){
                let valor_hasta="En Adelante"
                if(item.hasta!=null){
                    valor_hasta=item.hasta
                }
				$('#tablaRangos').append(`<tr>
                                                <td style="width:30%; text-align:center; vertical-align:middle">
                                                    ${item.desde} 
                                                    
                                                </td>

                                                <td style="width:30%;  text-align:left; vertical-align:middle">
                                                    ${valor_hasta} 
                                                </td>
                                               
                                                <td style="width:30%; text-align:center; vertical-align:middle">
                                                    ${item.valor}
                                                </td>

                                               
                                                <td style="width:10%; text-align:right; vertical-align:middle">
                                                    <button class="btn btn-sm btn-warning" onclick="editarRango('${item.id}','${item.desde}','${item.hasta}','${item.valor}')"><i class="bi bi-pencil-square"></i></button>
                                                </td>
                                                
											
										</tr>`);
			})
          

            
		}
    
    }).fail(function(){
        vistacargando("")
        alertNotificar("Se produjo un error, por favor intentelo más tarde","error");  
        $("#tablaRangos tbody").html('');
		$("#tablaRangos tbody").html(`<tr><td colspan="${num_col}" style="text-align:center">Se produjo un error, por favor intentelo más tarde</td></tr>`);
    });
}

function editarRango(id, desde, hasta, rango){
    $('#desde_base').val(desde)
    $('#hasta_base').val(hasta)
    $('#valor_base').val(rango)
    $('#id_base').val(id)
    $('#btn_base').html('Actualizar')
    FormAccionRango='A'
    IdEditarRango=id
}

function cancelarRango(){
    $('#desde_base').val('')
    $('#hasta_base').val('')
    $('#valor_base').val('')
    $('#id_base').val('')
    $('#btn_base').html('Guardar')
    FormAccionRango='R'
}


$("#form_base").submit(function(e){
    e.preventDefault();
    
    //validamos los campos obligatorios
    let desde_base=$('#desde_base').val()
    let hasta_base=$('#hasta_base').val()
    let valor_base=$('#valor_base').val()
   
    if(desde_base=="" || desde_base==null){
        alertNotificar("Debe ingresar desde","error")
        $('#desde_base').focus()
        return
    } 

    if (isNaN(desde_base) || desde_base.trim() === "") {
        alertNotificar('Debe ingresar un número válido',"error");
        $('#desde_base').focus()
        return
    }

    // if(hasta_base=="" || hasta_base==null){
    //     alertNotificar("Debe ingresar hasta","error")
    //     $('#hasta_base').focus()
    //     return
    // } 

    // if (isNaN(hasta_base) || hasta_base.trim() === "") {
    //     alertNotificar('Debe ingresar un número válido',"error");
    //     $('#hasta_base').focus()
    //     return
    // }

    if(valor_base=="" || valor_base==null){
        alertNotificar("Debe ingresar valor","error")
        $('#valor_base').focus()
        return
    } 

    if (isNaN(valor_base) || valor_base.trim() === "") {
        alertNotificar('Debe ingresar un número válido',"error");
        $('#valor_base').focus()
        return
    }

    vistacargando("m","Espere por favor")
    $.ajaxSetup({
        headers: {
            'X-CSRF-TOKEN': $('meta[name="csrf-token"]').attr('content')
        }
    });

    //comprobamos si es registro o edicion
    let tipo=""
    let url_form=""
    if(FormAccionRango=="R"){
        tipo="POST"
        url_form="guardar-rango"
    }else{
        tipo="PUT"
        url_form="actualizar-rango/"+IdEditarRango
    }
  
    var FrmData=$("#form_base").serialize();

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
            cancelarRango()
            alertNotificar(data.mensaje,"success");
            llenarTabla()
                            
        }, error:function (data) {
            console.log(data)

            vistacargando("");
            alertNotificar('Ocurrió un error','error');
        }
    });
})

function abrirModalMantenimiento(){
  
    $('#modalMantenimiento').modal('show')
    llenarTablaMarca()
    llenarTablaTipo()
    llenarTablaConcepto()
}

function llenarTablaMarca(){
    
    $("#tablaMarca tbody").html('');
    $('#tablaMarca tbody').empty(); 
    var num_col = $("#tablaMarca thead tr th").length; //obtenemos el numero de columnas de la tabla
    vistacargando("m", "Espere por favor")
    $.get('llenar-tabla-marca', function(data){
        console.log(data)
       
        vistacargando("")
        if(data.error==true){
			$("#tablaMarca tbody").html('');
			$("#tablaMarca tbody").html(`<tr><td colspan="${num_col}" style="text-align:center>No existen registros</td></tr>`);
			alertNotificar(data.mensaje,"error");
            // cancelar()
			return;   
		}
		if(data.error==false){
			if(data.resultado.length==0){
				$("#tablaMarca tbody").html('');
				$("#tablaMarca tbody").html(`<tr><td colspan="${num_col}" style="text-align:center">No existen registros</td></tr>`);
				alertNotificar("No se encontró información","error");
                // cancelar()
				return;
			}
			
			$("#tablaMarca tbody").html('');
         
			$.each(data.resultado,function(i, item){

              
                
				$('#tablaMarca').append(`<tr>
                                                <td style="width:10%; text-align:center; vertical-align:middle">
                                                    ${i+1} 
                                                    
                                                </td>

                                                <td style="width:50%;  text-align:left; vertical-align:middle">
                                                    ${item.descripcion} 
                                                </td>

                                             
                                                <td style="width:10%; text-align:right; vertical-align:middle">
                                                    <button class="btn btn-sm btn-warning" onclick="editarMarca('${item.id}','${item.descripcion}')"><i class="bi bi-pencil-square"></i></button>

                                                     <button class="btn btn-sm btn-danger" onclick="eliminarMarca('${item.id}')"><i class="fa fa-trash"></i></button>
                                                </td>


                                                
											
										</tr>`);
			})
          

            
		}
    
    }).fail(function(){
        vistacargando("")
        alertNotificar("Se produjo un error, por favor intentelo más tarde","error");  
        $("#tablaMarca tbody").html('');
		$("#tablaMarca tbody").html(`<tr><td colspan="${num_col}" style="text-align:center">Se produjo un error, por favor intentelo más tarde</td></tr>`);
    });
}
globalThis.FormaAccionMarca="R"
globalThis.IdEditarMarca=""
function editarMarca(id, marca_vehi){
    $('#marca_vehi').val(marca_vehi)
    $('#id_marca_vehi').val(id)
    $('#btn_marca').html('Actualizar')
    FormaAccionMarca='A'
    IdEditarMarca=id
}

function cancelarMarca(){
    $('#marca_vehi').val('')
    $('#id_marca_vehi').val('')
    $('#btn_marca').html('Guardar')
    FormaAccionMarca='R'
}

$("#form_marca").submit(function(e){
    e.preventDefault();
    
    //validamos los campos obligatorios
    let marca_vehi=$('#marca_vehi').val()
   
    if(marca_vehi=="" || marca_vehi==null){
        alertNotificar("Debe ingresar la marca","error")
        $('#marca_vehi').focus()
        return
    } 

    vistacargando("m","Espere por favor")
    $.ajaxSetup({
        headers: {
            'X-CSRF-TOKEN': $('meta[name="csrf-token"]').attr('content')
        }
    });

    //comprobamos si es registro o edicion
    let tipo=""
    let url_form=""
    if(FormaAccionMarca=="R"){
        tipo="POST"
        url_form="guardar-marca"
    }else{
        tipo="PUT"
        url_form="actualizar-marca/"+IdEditarMarca
    }
  
    var FrmData=$("#form_marca").serialize();

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
            cancelarMarca()
            alertNotificar(data.mensaje,"success");
            llenarTablaMarca()
                            
        }, error:function (data) {
            console.log(data)

            vistacargando("");
            alertNotificar('Ocurrió un error','error');
        }
    });
})

function eliminarMarca(id){
    if(confirm('¿Quiere eliminar el registro?')){
        vistacargando("m","Espere por favor")
        $.get("eliminar-marca/"+id, function(data){
            vistacargando("")
            if(data.error==true){
                alertNotificar(data.mensaje,"error");
                return;   
            }
    
            cancelarMarca()
            alertNotificar(data.mensaje,"success");
            llenarTablaMarca()
           
        }).fail(function(){
            vistacargando("")
            alertNotificar("Se produjo un error, por favor intentelo más tarde","error");  
        });
    }
}

function llenarTablaTipo(){
    
    $("#tablaTipoVehiculo tbody").html('');
    $('#tablaTipoVehiculo tbody').empty(); 
    var num_col = $("#tablaTipoVehiculo thead tr th").length; //obtenemos el numero de columnas de la tabla
    vistacargando("m", "Espere por favor")
    $.get('llenar-tabla-tipo', function(data){
        console.log(data)
       
        vistacargando("")
        if(data.error==true){
			$("#tablaTipoVehiculo tbody").html('');
			$("#tablaTipoVehiculo tbody").html(`<tr><td colspan="${num_col}" style="text-align:center>No existen registros</td></tr>`);
			alertNotificar(data.mensaje,"error");
            // cancelar()
			return;   
		}
		if(data.error==false){
			if(data.resultado.length==0){
				$("#tablaTipoVehiculo tbody").html('');
				$("#tablaTipoVehiculo tbody").html(`<tr><td colspan="${num_col}" style="text-align:center">No existen registros</td></tr>`);
				alertNotificar("No se encontró información","error");
                // cancelar()
				return;
			}
			
			$("#tablaTipoVehiculo tbody").html('');
         
			$.each(data.resultado,function(i, item){

              
                
				$('#tablaTipoVehiculo').append(`<tr>
                                                <td style="width:10%; text-align:center; vertical-align:middle">
                                                    ${i+1} 
                                                    
                                                </td>

                                                <td style="width:45%;  text-align:left; vertical-align:middle">
                                                    ${item.descripcion} 
                                                </td>

                                                 <td style="width:10%;  text-align:left; vertical-align:middle">
                                                    ${item.valor} 
                                                </td>

                                             
                                                <td style="width:20%; text-align:right; vertical-align:middle">
                                                    <button class="btn btn-sm btn-warning" onclick="editarTipo('${item.id}','${item.descripcion}','${item.valor}')"><i class="bi bi-pencil-square"></i></button>

                                                     <button class="btn btn-sm btn-danger" onclick="eliminarTipo('${item.id}')"><i class="fa fa-trash"></i></button>
                                                </td>


                                                
											
										</tr>`);
			})
          

            
		}
    
    }).fail(function(){
        vistacargando("")
        alertNotificar("Se produjo un error, por favor intentelo más tarde","error");  
        $("#tablaTipoVehiculo tbody").html('');
		$("#tablaTipoVehiculo tbody").html(`<tr><td colspan="${num_col}" style="text-align:center">Se produjo un error, por favor intentelo más tarde</td></tr>`);
    });
}

globalThis.FormaAccionTipo="R"
globalThis.IdEditarTipo=""
function editarTipo(id, tipo_vehi, valor){
    $('#tipo_vehi').val(tipo_vehi)
    $('#tipo_valor').val(valor)
    $('#id_tipo_vehi').val(id)
    $('#btn_tipo').html('Actualizar')
    FormaAccionTipo='A'
    IdEditarTipo=id
}

function cancelarTipo(){
    $('#tipo_vehi').val('')
    $('#tipo_valor').val('')
    $('#id_tipo_vehi').val('')
    $('#btn_tipo').html('Guardar')
    FormaAccionTipo='R'
}

$("#form_tipo").submit(function(e){
    e.preventDefault();
    
    //validamos los campos obligatorios
    let tipo_vehi=$('#tipo_vehi').val()
    let valor_vehi=$('#tipo_valor').val()
   
    if(tipo_vehi=="" || tipo_vehi==null){
        alertNotificar("Debe ingresar el tipo","error")
        $('#tipo_vehi').focus()
        return
    } 

     if(valor_vehi=="" || valor_vehi==null){
        alertNotificar("Debe ingresar el valor","error")
        $('#valor_vehi').focus()
        return
    } 

     if (isNaN(valor_vehi) || valor_vehi.trim() === "") {
        alertNotificar('Debe ingresar un número válido',"error");
        $('#valor_vehi').focus()
        return
    }


    vistacargando("m","Espere por favor")
    $.ajaxSetup({
        headers: {
            'X-CSRF-TOKEN': $('meta[name="csrf-token"]').attr('content')
        }
    });

    //comprobamos si es registro o edicion
    let tipo=""
    let url_form=""
    if(FormaAccionTipo=="R"){
        tipo="POST"
        url_form="guardar-tipo"
    }else{
        tipo="PUT"
        url_form="actualizar-tipo/"+IdEditarTipo
    }
  
    var FrmData=$("#form_tipo").serialize();

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
            cancelarTipo()
            alertNotificar(data.mensaje,"success");
            llenarTablaTipo()
                            
        }, error:function (data) {
            console.log(data)

            vistacargando("");
            alertNotificar('Ocurrió un error','error');
        }
    });
})


function eliminarTipo(id){
    if(confirm('¿Quiere eliminar el registro?')){
        vistacargando("m","Espere por favor")
        $.get("eliminar-tipo/"+id, function(data){
            vistacargando("")
            if(data.error==true){
                alertNotificar(data.mensaje,"error");
                return;   
            }
    
            cancelarTipo()
            alertNotificar(data.mensaje,"success");
            llenarTablaTipo()
           
        }).fail(function(){
            vistacargando("")
            alertNotificar("Se produjo un error, por favor intentelo más tarde","error");  
        });
    }
}

function capturaInfoPersona(){
    var ci_ruc=$('#ci_ruc').val()
    var tipo=$('#es_persona').val()

    $('#nombres').val('')
    $('#apellidos').val('')

    vistacargando("m","Espere por favor")
    $.get("carga-info-persona/"+ci_ruc, function(data){
        vistacargando("")
        if(data.error==true){
            alertNotificar(data.mensaje,"error");
            return;   
        }

        $('#nombres').val(data.data[0].nombre)
        $('#apellidos').val(data.data[0].apellido)
        console.log(data)
        
         
    }).fail(function(){
        vistacargando("")
        alertNotificar("Se produjo un error, por favor intentelo más tarde","error");  
    });
}

function verpdf(ruta){
   
    var iframe=$('#iframePdf');
    iframe.attr("src", "patente/documento/"+ruta);   
    $("#vinculo").attr("href", 'patente/descargar-documento/'+ruta);
    $("#documentopdf").modal("show");
}

function generarPdf(id){
    vistacargando("m","Espere por favor")
    $.get("transito-imprimir/"+id, function(data){
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

function calcularImpuesto(){
    document.getElementById('btn-calcular').click();
}

document.querySelectorAll('.concepto-check').forEach(function(checkbox) {
    checkbox.addEventListener('change', function(event) {
        const isChecked = this.checked; // true o false
        const conceptoId = this.getAttribute('data-id'); // obtener el data-id

        if (isChecked) {
            console.log('Marcado el checkbox con ID:', conceptoId);
            calcularImpuesto()
        } else {
            console.log('Desmarcado el checkbox con ID:', conceptoId);
            calcularImpuesto()
        }
    });
});


function llenarTablaConcepto(){
    
    $("#tablaConceptoVehiculo tbody").html('');
    $('#tablaConceptoVehiculo tbody').empty(); 
    var num_col = $("#tablaConceptoVehiculo thead tr th").length; //obtenemos el numero de columnas de la tabla
    vistacargando("m", "Espere por favor")
    $.get('llenar-tabla-concepto', function(data){
        console.log(data)
       
        vistacargando("")
        if(data.error==true){
			$("#tablaConceptoVehiculo tbody").html('');
			$("#tablaConceptoVehiculo tbody").html(`<tr><td colspan="${num_col}" style="text-align:center>No existen registros</td></tr>`);
			alertNotificar(data.mensaje,"error");
            // cancelar()
			return;   
		}
		if(data.error==false){
			if(data.resultado.length==0){
				$("#tablaConceptoVehiculo tbody").html('');
				$("#tablaConceptoVehiculo tbody").html(`<tr><td colspan="${num_col}" style="text-align:center">No existen registros</td></tr>`);
				alertNotificar("No se encontró información","error");
                // cancelar()
				return;
			}
			
			$("#tablaConceptoVehiculo tbody").html('');
         
			$.each(data.resultado,function(i, item){

              
                
				$('#tablaConceptoVehiculo').append(`<tr>
                                                <td style="width:10%; text-align:center; vertical-align:middle">
                                                    ${i+1} 
                                                    
                                                </td>

                                                <td style="width:65%;  text-align:left; vertical-align:middle">
                                                    ${item.concepto} 
                                                </td>

                                                 <td style="width:10%;  text-align:left; vertical-align:middle">
                                                    ${item.valor} 
                                                </td>

                                             
                                                <td style="width:10%; text-align:right; vertical-align:middle">
                                                    <button class="btn btn-sm btn-warning" onclick="editarConcepto('${item.id}','${item.concepto}','${item.valor}')"><i class="bi bi-pencil-square"></i></button>

                                                    
                                                </td>


                                                
											
										</tr>`);
			})
          

            
		}
    
    }).fail(function(){
        vistacargando("")
        alertNotificar("Se produjo un error, por favor intentelo más tarde","error");  
        $("#tablaConceptoVehiculo tbody").html('');
		$("#tablaConceptoVehiculo tbody").html(`<tr><td colspan="${num_col}" style="text-align:center">Se produjo un error, por favor intentelo más tarde</td></tr>`);
    });
}

globalThis.FormaAccionConcepto="R"
globalThis.IdEditarConcepto=""
function editarConcepto(id, concepto, valor){
    $('#txt_concepto').val(concepto)
    $('#valor_concepto').val(valor)
    $('#id_concepto').val(id)
    $('#btn_concepto').html('Actualizar')
    FormaAccionConcepto='A'
    IdEditarConcepto=id
}

function cancelarConcepto(){
    $('#txt_concepto').val('')
    $('#valor_concepto').val('')
    $('#id_concepto').val('')
    $('#btn_concepto').html('Guardar')
    FormaAccionConcepto='R'
}

$("#form_concepto").submit(function(e){
    e.preventDefault();
    
    //validamos los campos obligatorios
    let txt_concepto=$('#txt_concepto').val()
    let valor_concepto=$('#valor_concepto').val()
   
    if(txt_concepto=="" || txt_concepto==null){
        alertNotificar("Debe ingresar el concepto","error")
        $('#txt_concepto').focus()
        return
    } 

     if(valor_concepto=="" || valor_concepto==null){
        alertNotificar("Debe ingresar el valor","error")
        $('#valor_concepto').focus()
        return
    } 

     if (isNaN(valor_concepto) || valor_concepto.trim() === "") {
        alertNotificar('Debe ingresar un número válido',"error");
        $('#valor_concepto').focus()
        return
    }


    vistacargando("m","Espere por favor")
    $.ajaxSetup({
        headers: {
            'X-CSRF-TOKEN': $('meta[name="csrf-token"]').attr('content')
        }
    });

    //comprobamos si es registro o edicion
    let tipo=""
    let url_form=""
    if(FormaAccionConcepto=="R"){
        tipo="POST"
        url_form="guardar-concepto"
    }else{
        tipo="PUT"
        url_form="actualizar-concepto/"+IdEditarConcepto
    }
  
    var FrmData=$("#form_concepto").serialize();

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
            cancelarConcepto()
            alertNotificar(data.mensaje,"success");
            llenarTablaConcepto()
                            
        }, error:function (data) {
            console.log(data)

            vistacargando("");
            alertNotificar('Ocurrió un error','error');
        }
    });
})