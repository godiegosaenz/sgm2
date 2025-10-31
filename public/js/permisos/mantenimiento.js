globalThis.abrirGlobal=0
globalThis.rolSelecc=""

$("#form_registro_user").submit(function(e){
    e.preventDefault();
    
    //validamos los campos obligatorios
    let departamento=$('#departamento').val()
    let rol=$('#rol').val()
      
    if(departamento=="" || departamento==null){
        alertNotificar("Debe seleccionar un departamento","error")
        return
    } 

    if(rol=="" || rol==null){
        alertNotificar("Debe seleccionar un rol","error")
        return
    } 
    
    $.ajaxSetup({
        headers: {
            'X-CSRF-TOKEN': $('meta[name="csrf-token"]').attr('content')
        }
    });

    //comprobamos si es registro o edicion
    let tipo=""
    let url_form=""
    if(AccionForm=="R"){
        tipo="POST"
        url_form="guardar-usuario"
    }else{
        tipo="PUT"
        url_form="actualizar-usuario/"+idUserEditar
    }
    
    vistacargando("m","Espere por favor")
    var FrmData=$("#form_registro_user").serialize();
   
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
            limpiarCampos()
            alertNotificar(data.mensaje,"success");
            $('#form_ing').hide(200)
            $('#listado_user').show(200)
            llenar_tabla_rol()
                            
        }, error:function (data) {
            console.log(data)

            vistacargando("");
            alertNotificar('Ocurrió un error','error');
        }
    });
})

function limpiarCampos(){
    // $('#departamento').prop('disabled', false)
    $('#rol').val('').trigger('change.select2')
    $('#departamento').val('').trigger('change.select2')
    
}

function llenar_tabla_rol(){
    var num_col = $("#tabla_usuario thead tr th").length; //obtenemos el numero de columnas de la tabla
	$("#tabla_usuario tbody").html(`<tr><td colspan="${num_col}" style="padding:40px; 0px; font-size:20px;"><center><span class="spinner-border" role="status" aria-hidden="true"></span><b> Obteniendo información</b></center></td></tr>`);
   
    
    $.get("listado-rol/", function(data){
       
        console.log(data)
        if(data.error==true){
            alertNotificar(data.mensaje,"error");
            $("#tabla_usuario tbody").html(`<tr><td colspan="${num_col}" style="padding:40px; 0px; font-size:20px;"><center>No se encontraron datos</center></td></tr>`);
            return;   
        }
        if(data.error==false){
            
            if(data.resultado.length <= 0){
                $("#tabla_usuario tbody").html(`<tr><td colspan="${num_col}" style="padding:40px; 0px; font-size:20px;"><center>No se encontraron datos</center></td></tr>`);
                alertNotificar("No se encontró datos","error");
                return;  
            }
         
            $('#tabla_usuario').DataTable({
                "destroy":true,
                pageLength: 10,
                autoWidth : true,
                order: [[ 1, "desc" ]],
                sInfoFiltered:false,
                language: {
                    url: 'json/datatables/spanish.json',
                },
                columnDefs: [
                    { "width": "10%", "targets": 0 },
                    { "width": "25%", "targets": 1 },
                    { "width": "25%", "targets": 2 },
                    { "width": "20%", "targets": 3 },
                               
                ],
                data: data.resultado,
                columns:[
                        {data: "name"},
                        {data: "name"},
                       
                        // {data: "name.permiso_rol.permiso.name"},
                        {
                            data: "permiso_rol",
                            render: function(data, type, row) {
                                if (!data || data.length === 0) return '-';
                                // Mapear y mostrar solo los nombres de permisos
                                return data.map(function(p) {
                                    return p.permiso.name;
                                }).join("<br>");
                            }
                        },
                         {data: "name"},
                        
                       
                ],    
                "rowCallback": function( row, data, index ) {
                    $('td', row).eq(0).html(index+1)
                    $('td', row).eq(3).html(`
                                  
                                            <button type="button" class="btn btn-success btn-xs" onclick="mantenimientoPermiso('${data.id}','${data.name}')">Mantenimiento</button>

                                           

                                    
                    `); 
                }             
            });
        }
    }).fail(function(){
        $("#tabla_usuario tbody").html(`<tr><td colspan="${num_col}" style="padding:40px; 0px; font-size:20px;"><center>No se encontraron datos</center></td></tr>`);
        alertNotificar("Se produjo un error, por favor intentelo más tarde","error");  
    });


}

$('.collapse-link').click();
$('.datatable_wrapper').children('.row').css('overflow','inherit !important');

$('.table-responsive').css({'padding-top':'12px','padding-bottom':'12px', 'border':'0', 'overflow-x':'inherit'});




function mantenimientoPermiso(idrol, rol, abrirGlobal=0){

    
    rolSelecc=rol
    var num_col = $("#tabla_permisos thead tr th").length; //obtenemos el numero de columnas de la tabla
	$("#tabla_permisos tbody").html(`<tr><td colspan="${num_col}" style="padding:40px; 0px; font-size:20px;"><center><span class="spinner-border" role="status" aria-hidden="true"></span><b> Obteniendo información</b></center></td></tr>`);

    vistacargando("m","Espere por favor")
    $.get("ver-permisos-roles/"+idrol, function(data){
        console.log(data)
        
       
        vistacargando("")
        if(data.error==true){
            alertNotificar(data.mensaje,"error");
            return;   
        }
      

         if(data.resultado.length <= 0){
            $("#tabla_permisos tbody").html(`<tr><td colspan="${num_col}" style="padding:40px; 0px; font-size:20px;"><center>No se encontraron datos</center></td></tr>`);
            alertNotificar("No se encontró datos","error");
            return;  
        }
      
        $('#id_rol_selecc').val(idrol)
        $('#rol_selecc').val(rol)
      
        $('#tabla_permisos').DataTable({
            "destroy":true,
            pageLength: 10,
            autoWidth : true,
            order: [[ 1, "desc" ]],
            sInfoFiltered:false,
            language: {
                url: '/json/datatables/spanish.json',
            },
            columnDefs: [
                { "width": "20%", "targets": 0 },
                { "width": "35%", "targets": 1 },
                { "width": "25%", "targets": 2 },
            
                
            ],
            data: data.resultado.permiso_rol,
            columns:[
                    {data: "permiso.name"},
                    {data: "permiso.name"},
                    
                    {data: "permiso.name"},
            ],    
            "rowCallback": function( row, data, index ) {
                console.log("ss")
                console.log(data)
                $('td', row).eq(0).html(index+1)
                $('td', row).eq(2).html(`
                                
                                        <button type="button" class="btn btn-danger btn-xs" onclick="eliminarPermiso('${data.permission_id}','${idrol}')">Borrar</button>

                                        

                                
                `); 
            }
               
            
        })
        // alert(abrirGlobal)
        if(abrirGlobal==0){
            $('#modal_Permisos').modal('show')
        }
       
       
    }).fail(function(){
        vistacargando("")
        alertNotificar("Se produjo un error, por favor intentelo más tarde","error");  
    });
}

function visualizarForm(tipo){
    $('#form_ing').show(200)
    $('#listado_user').hide(200)
    globalThis.AccionForm="";
    if(tipo=='N'){
        $('#titulo_form').html("Registro Usuario")
        $('#nombre_btn_form').html('Registrar')
        AccionForm="R"
    }else{
        $('#titulo_form').html("Actualizar Usuario")
        $('#nombre_btn_form').html('Actualizar')
        AccionForm="E"
    }
}

function visualizarListado(){
    $('#form_ing').hide(200)
    $('#listado_user').show(200)
    limpiarCampos()
}

function eliminarPermiso(idpermiso,idrol){
    vistacargando("m","Espere por favor")
    if(confirm('¿Quiere eliminar el registro?')){
        $.get("eliminar-permiso/"+idpermiso+"/"+idrol, function(data){
            vistacargando("")
            if(data.error==true){
                alertNotificar(data.mensaje,"error");
                return;   
            }
    
            alertNotificar(data.mensaje,"success");     
            abrirGlobal=1       
            mantenimientoPermiso(idrol, rolSelecc, abrirGlobal)
            llenar_tabla_rol()            
           
        }).fail(function(){
            vistacargando("")
            alertNotificar("Se produjo un error, por favor intentelo más tarde","error");  
        });
    }
   
}

function cancelarPermiso(){
    $('#modal_Permisos').modal('hide')
}



$("#form_registro_permisos").submit(function(e){
    e.preventDefault();
    
    //validamos los campos obligatorios
    let permisos=$('#permisos').val()
    let rol_selecc = $('#rol_selecc').val();
    let idrol= $('#id_rol_selecc').val()
     
    if(permisos=="" || permisos==null){
        alertNotificar("Debe seleccionar un permiso","error")
        return
    } 
    
    $.ajaxSetup({
        headers: {
            'X-CSRF-TOKEN': $('meta[name="csrf-token"]').attr('content')
        }
    });

    let tipo="POST"
    let url_form="guardar-permiso"
    
    vistacargando("m","Espere por favor")
    var FrmData=$("#form_registro_permisos").serialize();
   
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
            $('#permisos').val('').trigger('change.select2')
            alertNotificar(data.mensaje,"success");
            
            abrirGlobal=1       
            mantenimientoPermiso(idrol, rol_selecc, abrirGlobal)
            llenar_tabla_rol()
                            
        }, error:function (data) {
            console.log(data)

            vistacargando("");
            alertNotificar('Ocurrió un error','error');
        }
    });
})