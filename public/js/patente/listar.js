
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
                }, 3000);
                                
            }, error:function (data) {
                console.log(data)

                vistacargando("");
                alertNotificar('Ocurrió un error','error');
            }
        });
    }
})

function cobrarTitulo(id){
    $('.detalle_cobro').html('')
    $('#id_impuesto').val(id)

    vistacargando("m","Espere por favor")
    $.get("../patente/detalle-titulo-patente/"+id, function(data){
        console.log(data)
        vistacargando("")
        if(data.error==true){
            alertNotificar(data.mensaje,"error");
            return;   
        }

        $('#valor_imp_pat_muni').html(data.resultado[0].valor_impuesto)
        $('#exoneracion_pat_muni').html(data.resultado[0].valor_exoneracion)
        $('#serv_adm_pat_muni').html(data.resultado[0].valor_sta)
        $('#intereses_pat_muni').html(data.resultado[0].valor_intereses)
        $('#recargos_pat_muni').html(data.resultado[0].valor_recargos)
        $('#total_pat_muni').html(data.resultado[0].valor_patente)
        
        $('#tablaImpuestoActivoDetalle').show()
        if(data.resultado[0].valor_impuesto_act==null){
            $('#tablaImpuestoActivoDetalle').hide()
        }
            

        $('#valor_imp_activo').html(data.resultado[0].valor_impuesto_act)
        $('#valor_exoneracion_activo').html(data.resultado[0].valor_exoneracion_act)
        $('#valor_servicioadm_activo').html(data.resultado[0].valor_sta_act)
        $('#valor_intereses_activo').html(data.resultado[0].valor_intereses_act)
        $('#valor_recargo_activo').html(data.resultado[0].valor_recargos_act)
        $('#valor_total_activo').html(data.resultado[0].valor_activo_total)

        $('#persona').html(data.resultado[0].contribuyente.razon_social)
        $('#ruc_cont').html(data.resultado[0].contribuyente.ruc)
        let obligado = data.resultado[0].contribuyente.obligado_contabilidad;
        $('#lleva_conta').html(obligado == 1 ? 'Sí' : 'No')
        $('#regimen').html(data.resultado[0].contribuyente.regimen.nombre)

        let local = data.resultado[0].local;

        let partes = [
            local.calle_principal,
            local.calle_secundaria ? `y ${local.calle_secundaria}` : null,
            local.referencia_ubicacion
        ];

        let direccion = partes.filter(Boolean).join(' ');

        $('#nombre_local').html(data.resultado[0].local.actividad_descripcion)
        $('#direccion_local').html(direccion)
        let local_estado = data.resultado[0].local.local_propio;
        $('#estado_local').html(obligado == 1 ? 'Propio' : 'Arrendado')
        
        $.each(data.resultado[0].actividades,function(i, item){
            console.log(item)
            $('#actividades').append(`<ul>
                    <li>${item.detalle_actividad.ciiu} --  ${item.detalle_actividad.descripcion}</li>
                <ul>`)
        })

        let v1 = parseFloat(data.resultado[0].valor_patente) || 0;
        let v2 = parseFloat(data.resultado[0].valor_activo_total) || 0;

        let total_final = (v1 + v2).toFixed(2);

        $('#valor_totalpago').html(total_final)


        $('#PagoModal').modal('show')
                    
        
    }).fail(function(){
        vistacargando("")
        alertNotificar("Se produjo un error, por favor intentelo más tarde","error");  
    });
}

globalThis.CargarPagina=0
$('#documentopdf').on('hidden.bs.modal', function () {
    if(CargarPagina==1){
        $("#documentopdf").modal("hide");
        location.reload();
    }
});

function registrarCobro(){
    var id=$('#id_impuesto').val()
    
    if(confirm('¿Estas seguro que quieres realizar el cobro?'))
    {
        
        vistacargando("m","Espere por favor")
        $.get("../patente/registrar-cobro-patente/"+id, function(data){
            vistacargando("")
            if(data.error==true){
                alertNotificar(data.mensaje,"error");
                return;   
            }
            cerrarCobro()
            alertNotificar(data.mensaje,"success");
            CargarPagina=1
            verpdf(data.pdf)

            
            // location.reload();
        }).fail(function(){
            vistacargando("")
            alertNotificar("Se produjo un error, por favor intentelo más tarde","error");  
        });
    }
}

function cerrarCobro(){
    $('#PagoModal').modal('hide')
}

function anularCobro(){
    var id=$('#id_impuesto').val()
    
    if(confirm('¿Estas seguro que quieres anular el registro?'))
    {
        
        vistacargando("m","Espere por favor")
        $.get("../patente/anular-cobro-patente/"+id, function(data){
            vistacargando("")
            if(data.error==true){
                alertNotificar(data.mensaje,"error");
                return;   
            }
            alertNotificar(data.mensaje,"success");
            
            setTimeout(() => {
                cerrarCobro()
                location.reload();
            }, 2000);
            
            
        }).fail(function(){
            vistacargando("")
            alertNotificar("Se produjo un error, por favor intentelo más tarde","error");  
        });
    }
}


