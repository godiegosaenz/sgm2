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


 // Inicializa Select2 cuando se abra el modal
    $('#modal_Permisos').on('shown.bs.modal', function () {
        //alert("ss")
        $('#permisos').select2({
            dropdownParent: $('#modal_Permisos'), // 👈 Importante para que funcione dentro del modal
            width: '100%',
            placeholder: 'Seleccione un permiso o escriba...',
            allowClear: true,
            // Si quieres permitir agregar nuevos valores:
            // tags: true  
        });
    });


const isDarkMode = window.matchMedia('(prefers-color-scheme: dark)').matches;
if(isDarkMode){
    applyDarkModeStyles('D')
}else{
    applyDarkModeStyles('L')
}


function applyDarkModeStyles(enable) {
    let styleTag = document.getElementById('dark-mode-styles');

    if (enable=='D' ) {
        // Si el estilo ya existe, no lo agregamos de nuevo
        if (!styleTag) {
            styleTag = document.createElement('style');
            styleTag.id = 'dark-mode-styles';
            styleTag.innerHTML = `
                .select2-container--default .select2-results__option {
                    background-color: #212529 !important;
                    color: #fff !important;
                }
                .select2-container--default .select2-results__option--highlighted {
                    background-color: #343a40 !important;
                    color: #fff !important;
                }
                .select2-container--default .select2-results__option[aria-selected="true"] {
                    background-color: #555 !important;
                    color: #fff !important;
                }
                .select2-container--default .select2-selection--single {
                    background-color: #212529 !important;
                    color: #fff !important;
                    border: 1px solid #555 !important;
                }
                .select2-container--default .select2-selection__arrow b {
                    border-color: #fff transparent transparent transparent !important;
                }
                .select2-container--default .select2-search--dropdown .select2-search__field {
                    background-color: #333 !important;
                    color: #fff !important;
                    border: 1px solid #555 !important;
                }
                .select2-container--default .select2-results__message {
                    background-color: #212529 !important;
                    color: #bbb !important;
                }
                .select2-container--default .select2-selection__rendered {
                    color: white !important;
                }
            `;
            document.head.appendChild(styleTag);
        }
    } else {           
        // Si el usuario cambia a modo claro, eliminamos la etiqueta de estilos oscuros
        if (styleTag) {
            styleTag.remove();
        }
    }
}
// Llamar a la función cuando cambie el tema
function cambiaTema(isDark) {
    applyDarkModeStyles(isDark);       
}
function setTheme(theme) {
    localStorage.setItem('theme', theme);  // Guardar preferencia en localStorage
    applyTheme(theme);
}

// Cuando la página carga, recuperamos la preferencia
window.addEventListener('DOMContentLoaded', (event) => {
    const savedTheme = localStorage.getItem('theme');
    if (savedTheme) {
        applyTheme(savedTheme);  // Aplica el tema guardado
    }
});

// Función para aplicar el tema (claro u oscuro)
function applyTheme(theme) {
    // alert(theme)
    if(theme=='dark'){
        applyDarkModeStyles('D')
    }else if(theme=='light'){
        applyDarkModeStyles('L')
    }
}
function getSystemTheme() {
    return window.matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light';
}