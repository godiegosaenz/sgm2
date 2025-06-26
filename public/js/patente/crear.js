globalThis.LlevaContabilidad=0
function cargaInfoContribuyente(){
    let idcontribuyente=$('#cmb_propietario').val()
    if(idcontribuyente=="" || idcontribuyente==null){return}
    vistacargando("m","Espere por favor")
    $.get('patente/busca-data-contribuyente/'+idcontribuyente, function(data){
        vistacargando("")
        console.log(data)
        if(data.error==true){
            // alertNotificar(data.mensaje,"error");
            alert(data.mensaje);
            return;   
        }
        let tipo_contr=""
        let rl=""
        let cedula_rl=data.data.ruc
        if (data.data.tipo_contribuyente === 1) {
            tipo_contr = "Persona natural";
            rl=data.data.contribuyente
            cedula_rl=data.data.ruc
        } else if (data.data.tipo_contribuyente === 2) {
            tipo_contr = "Sociedades";
            rl=data.data.representante_legal
            cedula_rl=data.data.cedula_rl
        } else {
            tipo_contr = "Desconocido"; // Opcional: texto si el estado no coincide
        }

        let estado_contr=""
        if (data.data.estado_contribuyente_id === 1) {
            estado_contr = "Activo";
        } else if (data.data.estado_contribuyente_id === 2) {
            estado_contr = "Inactivo";
        } else {
            estado_contr = "Desconocido"; // Opcional: texto si el estado no coincide
        }
        $('#lleva_contabilidad').prop('checked', false);
        let obligado_cont="No"
        if(data.data.obligado_contabilidad==true){
            obligado_cont="Si"
            LlevaContabilidad=1
            $('#lleva_contabilidad').prop('checked', true);
        }

       
        let es_artesano="No"
        if(data.data.es_artesano==true){
            es_artesano="Si"
        }
      
        $('#ruc_cedula').val(data.data.ruc)
        $('#razon_social').val(data.data.contribuyente)
        $('#representante_legal').val(rl)
        $('#cedula_representante').val(cedula_rl)
        $('#num_establecimientos').val(data.data.cantidad_locales)
        $('#inicio_actividades').val(data.data.fecha_inicio_actividades)
        $('#tipo_contribuyente').val(tipo_contr)
        $('#estado_contribuyente').val(estado_contr)
        $('#clase_contribuyente').val(data.data.clase_contri)
        $('#edad_cont').val(data.data.edad_contribuyente)
        $('#lleva_cont').val(obligado_cont)
        $('#artesano_cont').val(es_artesano)
        $('#pdf_artesano').val(data.data.archivo_artesano)
        $('#pdf_ruc').val(data.data.archivo_ruc)
        
        
        let actividadesComerciales=data.actividad
        
        limpiarTabla();
        // document.getElementById("lleva_cont").value = LocalPropio ?? "";
        actividadesComerciales.forEach((actividad) => {
            console.log(actividad)
            seleccionarActividad(actividad.idActividad,actividad.descripcion,actividad.ciiu, actividad.id);
        });

        let locales=data.locales
        limpiarTablaLocal();
        locales.forEach((local) => {
            console.log(local)
            let direccion=local.provin+"-"+local.canton_+"-"+local.parroquia_+", Calle "+local.calle_principal+" y "+local.calle_secundaria
            seleccionarLocales(local.id,local.actividad_descripcion,direccion,local.local_propio, local.estado_establecimiento);
        });

        let inputs = document.querySelectorAll(".desabilita_txt");
        inputs.forEach(input => input.disabled = false); // Desbloquea cada input
           
        habilitarCamposObligadoOno(obligado_cont)
        // alertNotificar("El documento se descargara en unos segundos","success")
        // verpdf(data.pdf)

    }).fail(function(){
        vistacargando("")
        alertNotificar('Ocurrió un error','error');
    });
}

function verPdfRuc(){
   
    var nombre_pdf_ruc=$('#pdf_ruc').val()
    var contribuyente= $('#ruc_cedula').val()
    if(contribuyente==""){
        alertNotificar("Seleccione el contribuyente","error")
        return
    }
    if(nombre_pdf_ruc==""){
        alertNotificar("No se encontro el documento","error")
        return
    }

    verpdf(nombre_pdf_ruc)
    
}
function verPdfArtesano(){
   
    var nombre_pdf_artesano=$('#pdf_artesano').val()
    var contribuyente= $('#ruc_cedula').val()
    if(contribuyente==""){
        alertNotificar("Seleccione el contribuyente","error")
        return
    }
    if(nombre_pdf_artesano==""){
        alertNotificar("No se encontro el documento","error")
        return
    }

    verpdf(nombre_pdf_artesano)
    
}

function habilitarCamposObligadoOno(obligado){
    const noObligadosFieldset = document.getElementById('no_obligados_fieldset');
    const obligadosFieldset = document.getElementById('obligados_fieldset');
    if (obligado=="Si") {
        // Mostrar el fieldset para "Obligados a llevar contabilidad"
        // obligadosFieldset.classList.remove("d-none");
        // obligadosFieldset.classList.add("d-flex"); // Para que respete la estructura de Bootstrap
        // noObligadosFieldset.classList.add("d-none"); // Ocultar el otro fieldset
        // checkboxprofesional.checked = false;
        $('#no_obligados_fieldset').hide()
        $('#obligados_fieldset').show()
    } else {
        $('#no_obligados_fieldset').show()
        $('#obligados_fieldset').hide()
        // Mostrar el fieldset para "No obligados a llevar contabilidad"
        // noObligadosFieldset.classList.remove("d-none");
        // noObligadosFieldset.classList.add("d-flex");
        // obligadosFieldset.classList.add("d-none");
        // checkboxprofesional.checked = false;
    }

}

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

$('#cmb_propietario').select2({
    ajax: {
        url: 'catastrocontribuyente/buscarContribuyente',
        dataType: 'json',
        processResults: function (data) {
            return {
                results: data.map(item => ({
                    text: item.documento+ " - "+item.nombre,
                    id: item.idper
                }))
            };
        }
    }
});

function cargaInfoActividad(){
    var codigo_desc=$('#cmb_actividad').val()
    if(codigo_desc==""){return}
    let texto = $('#cmb_actividad option:selected').text();
    let separa=texto.split("-")
    $('#codigo_act').val(separa[0])
    $('#descripcion_act').val(separa[1])

}

$('#actividadLocal').on('shown.bs.modal', function () {
    $('#cmb_actividad').select2({
        dropdownParent: $('#actividadLocal'),
        ajax: {
            url: 'catastrocontribuyente/buscarActividad',
            dataType: 'json',
            delay: 250,
            data: function (params) {
                return {
                    q: params.term
                };
            },
            processResults: function (data) {
                return {
                    results: data.map(item => ({
                        id: item.id,
                        text: item.ciiu + " - " + item.nombre
                    }))
                };
            }
        },
        minimumInputLength: 1
    });
});

function guardaActividad(){
    var cmb_actividad=$('#cmb_actividad').val()
    var cmb_propietario=$('#cmb_propietario').val()

    if(cmb_propietario==null || cmb_propietario==""){
        alertNotificar("Debe seleccionar el contribuyente","error")
        return
    }

    if(cmb_actividad==null || cmb_actividad==""){
        alertNotificar("Debe seleccionar la actividad","error")
        return
    }

    vistacargando("m","Espere por favor")
    $.ajaxSetup({
        headers: {
            'X-CSRF-TOKEN': $('meta[name="csrf-token"]').attr('content')
        }
    });
    var tipo='POST'
    $.ajax({
        
        type: tipo,
        url: "catastrocontribuyente/agregar-actividad",
        method: tipo,             
        data: {
            cmb_actividad: cmb_actividad,
            cmb_propietario:cmb_propietario
        },
        
        success: function(response) {
            vistacargando("")
            if(response.error==true){
                alertNotificar(response.mensaje,"error")
                return
            }
            alertNotificar(response.mensaje,"success")
            $('#actividadLocal').modal('hide')
            
            cargaInfoContribuyente()
        },
        error: function(xhr, status, error) {
            vistacargando("")
            console.error("Error al obtener los datos:", error);
        }
    });
}


let inputs = document.querySelectorAll(".desabilita_txt"); // Selecciona todos los inputs con la clase "miClase"
inputs.forEach(input => input.disabled = true); // Bloquea cada input



function limpiarCampos() {
    document.getElementById("propietario_nombre").value = "";
    document.getElementById("propietario_cedula").value = "";
    document.getElementById("ruc_cedula").value = "";
    // document.getElementById("nombre_contribuyente").value = "";
    document.getElementById("razon_social").value = "";
    // document.getElementById("domicilio").value = "";
    // document.getElementById("Telefono_domicilio").value = "";
    // document.getElementById("correo_electronico").value = "";
    document.getElementById("representante_legal").value = "";
    document.getElementById("cedula_representante").value = "";
    document.getElementById("inicio_actividades").value = "";
    document.getElementById("estado_contribuyente").value = "";
    document.getElementById("clase_contribuyente").value = "";
    document.getElementById("tipo_contribuyente").value = "";
    document.getElementById("num_establecimientos").value = "";
    // document.getElementById("estado_establecimiento").value = "";
    // document.getElementById("local_propio").value = "";
    document.getElementById("catastro_id").value = "";
    // document.getElementById("lleva_cont").value = "";
    
}
globalThis.Emitir=0
function enviarFormulario() {        
    Emitir=1    
    $('#emision').val(1)
    $("#formPatente").trigger("submit");
    // $("#formPatente").submit();
}

function simular() { 
    // $('#documentopdf').modal('show')
    // verpdf("c.pdf");
    // return
    Emitir=0   
    $('#emision').val(2)        
    $("#formPatente").trigger("submit");
}

function verpdf(ruta){
    var iframe=$('#iframePdf');
    iframe.attr("src", "patente/documento/"+ruta);   
    $("#vinculo").attr("href", 'patente/descargar-documento/'+ruta);
    $("#documentopdf").modal("show");
}

$('#documentopdf').on('hidden.bs.modal', function () {
    if(Emitir==1){
        location.reload();
    }
});
// funcion para seleccionar contribuyente y cargar los datos
function seleccionarcontribuyente(id){
    limpiarCampos();
    axios.post("{{route('get.catastro')}}", {
        _token: token,
        id:id
    }).then(function(res) {
        console.log(res)
        if(res.status==200) {


            let inputs = document.querySelectorAll(".desabilita_txt");
            inputs.forEach(input => input.disabled = false); // Desbloquea cada input
           
                           
          
            const data = res.data.contribuyente; // Los datos del contribuyente
            const datapropietario = res.data.propietarios; // Los datos del contribuyente
            const dataclase = res.data.clase_contribuyente; // Los datos del contribuyente
            const actividadesComerciales = res.data.actividades; // Los datos del contribuyente
            const locales=res.data.locales
            console.log(locales)
            let estadoContribuyente;
            let tipoContribuyente;
            let estadoEstablecimiento;
            let localPropio;

            if (data.estado_contribuyente_id === 1) {
                estadoContribuyente = "Activo";
            } else if (estado_contribuyente_id === 2) {
                estadoContribuyente = "Inactivo";
            } else if (estado_contribuyente_id === 3) {
                estadoContribuyente = "Suspendido";
            } else {
                estadoContribuyente = "Desconocido"; // Opcional: texto si el estado no coincide
            }
            if (data.tipo_contribuyente === 1) {
                tipoContribuyente = "Persona natural";
            } else if (tipo_contribuyente === 2) {
                tipoContribuyente = "Sociedades";
            } else {
                tipoContribuyente = "Desconocido"; // Opcional: texto si el estado no coincide
            }
            if (data.estado_establecimiento === 1) {
                estadoEstablecimiento = "Abierto";
            } else if (tipo_contribuyente === 2) {
                estadoEstablecimiento = "Cerrado";
            } else {
                estadoEstablecimiento = "Desconocido"; // Opcional: texto si el estado no coincide
            }
            // if (data.local_propio === "1") {
            //     localPropio = "Propio";
            // } else if (tipo_contribuyente === "2") {
            //     localPropio = "Arrendado";
            // } else {
            //     localPropio = "Desconocido"; // Opcional: texto si el estado no coincide
            // }

            let checkbox = document.getElementById("lleva_contabilidad");    
            const noObligadosFieldset1 = document.getElementById('no_obligados_fieldset');
            const obligadosFieldset1 = document.getElementById('obligados_fieldset');

            // if(data.obligado_contabilidad!=true){ 
               
            //     checkbox.checked =  true; 
            //     checkbox.classList.add("readonly-checkbox");
            //     obligadosFieldset1.style.display = 'block';
            //     // Ocultar el fieldset para "No obligados a llevar contabilidad"
            //     noObligadosFieldset1.style.display = 'none';

            // }else{
               
            //     checkbox.checked =  false; 
            //     checkbox.classList.add("readonly-checkbox");
            //     // Mostrar el fieldset para "No obligados a llevar contabilidad"
            //     noObligadosFieldset1.style.display = 'block';
            //     // Ocultar el fieldset para "Obligados a llevar contabilidad"
            //     obligadosFieldset1.style.display = 'none';
                
            // }              
            

            // Asigna los datos a los campos del formulario
            document.getElementById("catastro_id").value = data.id;
            document.getElementById("buscar_contribuyente").value = data.ruc;
            document.getElementById("propietario_nombre").value = datapropietario.nombres ?? ""+" "+datapropietario.apellidos ?? "";
            document.getElementById("propietario_cedula").value = datapropietario.ci_ruc ?? "";
            document.getElementById("ruc_cedula").value = data.ruc ?? "";
            // document.getElementById("nombre_contribuyente").value = data.nombre_fantasia_comercial ?? "" // Nombre de fantasía comercial
            document.getElementById("razon_social").value = data.razon_social ?? "";
            // document.getElementById("domicilio").value = `${data.calle_principal ?? ""} ${data.calle_secundaria ?? ""} ${data.referencia_ubicacion ?? ""}`.trim();
            // document.getElementById("Telefono_domicilio").value = data.telefono ?? "";
            // document.getElementById("correo_electronico").value = data.correo_1 ?? "";
            document.getElementById("representante_legal").value = data.representante_legal_id ?? "";
            document.getElementById("cedula_representante").value = data.representante_legal_id ?? ""; // Ajustar si tienes datos separados para la cédula
            document.getElementById("inicio_actividades").value = data.fecha_inicio_actividades ?? "";
            document.getElementById("estado_contribuyente").value = estadoContribuyente;
            document.getElementById("clase_contribuyente").value = dataclase.nombre ?? "";
            document.getElementById("tipo_contribuyente").value = tipoContribuyente ?? "";
            document.getElementById("num_establecimientos").value = data.id ?? "";
            // document.getElementById("estado_establecimiento").value = estadoEstablecimiento ?? "";
            // document.getElementById("local_propio").value = localPropio ?? "";
            limpiarTabla();
            // document.getElementById("lleva_cont").value = LocalPropio ?? "";
            actividadesComerciales.forEach((actividad) => {
                console.log(actividad)
                seleccionarActividad(actividad.id,actividad.descripcion,actividad.ciiu);
            });
            limpiarTablaLocal();
            locales.forEach((local) => {
                console.log("qqqqqqqqqqqqq")
                console.log(local)
                let direccion=local.provincia.descripcion+"-"+local.canton.nombre+"-"+local.parroquia.descripcion+", Calle "+local.calle_principal+" y "+local.calle_secundaria
                seleccionarLocales(local.id,local.actividad_descripcion,direccion,local.local_propio);
            });

            
             // Cerrar el modal
            // Selecciona el elemento modal

          
            

        }else{
            alert(res.status);
        }
    }).catch(function(err) {
        console.log(err)
    }).then(function() {

    });
    var modalElement = document.getElementById('modalContribuyente');
    // Crea una instancia del modal o toma la instancia existente
    var modal = bootstrap.Modal.getInstance(modalElement) || new bootstrap.Modal(modalElement);
    // Oculta el modal
    modal.hide();
}
globalThis.LocalComercial=""
function seleccionarcontribuyentesinactividad(id){
    limpiarCampos();
    axios.post("{{route('get.catastro')}}", {
        _token: token,
        id:id
    }).then(function(res) {
        
        if(res.status==200) {

            const data = res.data.contribuyente; // Los datos del contribuyente
            const datapropietario = res.data.propietarios; // Los datos del contribuyente
            const dataclase = res.data.clase_contribuyente; // Los datos del contribuyente
            const actividadesComerciales = res.data.actividades; // Los datos del contribuyente
            const locales=res.data.locales
            let estadoContribuyente;
            let tipoContribuyente;
            let estadoEstablecimiento;
            let localPropio;

            if (data.estado_contribuyente_id === 1) {
                estadoContribuyente = "Activo";
            } else if (estado_contribuyente_id === 2) {
                estadoContribuyente = "Inactivo";
            } else if (estado_contribuyente_id === 3) {
                estadoContribuyente = "Suspendido";
            } else {
                estadoContribuyente = "Desconocido"; // Opcional: texto si el estado no coincide
            }
            if (data.tipo_contribuyente === 1) {
                tipoContribuyente = "Persona natural";
            } else if (tipo_contribuyente === 2) {
                tipoContribuyente = "Sociedades";
            } else {
                tipoContribuyente = "Desconocido"; // Opcional: texto si el estado no coincide
            }
            // if (data.estado_establecimiento === 1) {
            //     estadoEstablecimiento = "Abierto";
            // } else if (tipo_contribuyente === 2) {
            //     estadoEstablecimiento = "Cerrado";
            // } else {
            //     estadoEstablecimiento = "Desconocido"; // Opcional: texto si el estado no coincide
            // }
            // if (data.local_propio === 1) {
            //     localPropio = "Propio";
            // } else if (tipo_contribuyente === 2) {
            //     localPropio = "Arrendado";
            // } else {
            //     localPropio = "Desconocido"; // Opcional: texto si el estado no coincide
            // }
            // LocalComercial=localPropio
            // Asigna los datos a los campos del formulario
            document.getElementById("catastro_id").value = data.id;
            document.getElementById("buscar_contribuyente").value = data.ruc;
            document.getElementById("propietario_nombre").value = datapropietario.nombres ?? ""+" "+datapropietario.apellidos ?? "";
            document.getElementById("propietario_cedula").value = datapropietario.ci_ruc ?? "";
            document.getElementById("ruc_cedula").value = data.ruc ?? "";
            document.getElementById("nombre_contribuyente").value = data.nombre_fantasia_comercial ?? "" // Nombre de fantasía comercial
            document.getElementById("razon_social").value = data.razon_social ?? "";
            document.getElementById("domicilio").value = `${data.calle_principal ?? ""} ${data.calle_secundaria ?? ""} ${data.referencia_ubicacion ?? ""}`.trim();
            document.getElementById("Telefono_domicilio").value = data.telefono ?? "";
            // document.getElementById("correo_electronico").value = data.correo_1 ?? "";
            document.getElementById("representante_legal").value = data.representante_legal_id ?? "";
            document.getElementById("cedula_representante").value = data.representante_legal_id ?? ""; // Ajustar si tienes datos separados para la cédula
            document.getElementById("inicio_actividades").value = data.fecha_inicio_actividades ?? "";
            document.getElementById("estado_contribuyente").value = estadoContribuyente;
            document.getElementById("clase_contribuyente").value = dataclase.nombre ?? "";
            document.getElementById("tipo_contribuyente").value = tipoContribuyente ?? "";
            document.getElementById("num_establecimientos").value = data.id ?? "";
            // document.getElementById("estado_establecimiento").value = estadoEstablecimiento ?? "";
            // document.getElementById("local_propio").value = localPropio ?? "";

           
            // document.getElementById('lleva_contabilidad').value=1

        }else{
            alert(res.status);
        }
    }).catch(function(err) {
        console.log(err)
    }).then(function() {

    });
    var modalElement = document.getElementById('modalContribuyente');
    // Crea una instancia del modal o toma la instancia existente
    var modal = bootstrap.Modal.getInstance(modalElement) || new bootstrap.Modal(modalElement);
    // Oculta el modal
    modal.hide();
}
function limpiarTablaLocal(){
    // Obtener el cuerpo de la tabla
    const tbody = document.querySelector("#tablaLocales tbody");

    // Eliminar todas las filas existentes
    while (tbody.firstChild) {
        tbody.removeChild(tbody.firstChild);
    }
}
function seleccionarLocales(id, local_descripcion, direccion, local_propio_data,estado_establecimiento ){
    // alert(local_descripcion)
    // limpiarTablaLocal();
    const nuevaFila = document.createElement("tr");
    let local_propio=""
    if (local_propio_data === 1) {
        local_propio = "Propio";
    } else if (local_propio_data === 2) {
        local_propio = "Arrendado";
    } else {
        local_propio = "Desconocido"; // Opcional: texto si el estado no coincide
    }

    var desabilitar=""
    if(estado_establecimiento=="Cerrado"){
        desabilitar="disabled"
    }

    // Definir el contenido de la nueva fila, incluyendo inputs ocultos para enviar a Laravel
    nuevaFila.innerHTML = `
        <td>
            <input ${desabilitar} class="form-check-input checkbox-grande" type="checkbox" name="locales[${id}][id]" value="${id}">
        </td>
        <td>
            <input type="hidden" name="locales[${id}][local_descripcion]" value="${local_descripcion}">
            ${local_descripcion}
        </td>
        <td>
            <input type="hidden" name="locales[${id}][direccion]" value="${direccion}">
            ${direccion}
        </td>
        <td>
            <input type="hidden" name="locales[${id}][local_propio]" value="${local_propio}">
            ${local_propio}
        </td>

        <td>
            <input type="hidden" name="locales[${id}][local_propio]" value="${local_propio}">
            ${estado_establecimiento}
        </td>
        <td>
            <button type="button" class="btn btn-primary btn-sm" onclick="editarLocal(${id})" >
                <i class="fa fa-edit" ></i>
            </button>
        </td>
        

    `;

    // Añadir la nueva fila a la tabla
    document.querySelector("#tablaLocales tbody").appendChild(nuevaFila);

    // Cerrar el modal
    // var modal = bootstrap.Modal.getInstance(document.getElementById('actividadModal'));
    // modal.hide();
}
//una vez cargados los contribuyentes esta funcion hace cargar las actividades asociadas
function seleccionarActividad(idAct, nombre, ciiu, id) {
    // Crear un nuevo elemento <tr>
  
    const nuevaFila = document.createElement("tr");

    // Definir el contenido de la nueva fila, incluyendo inputs ocultos para enviar a Laravel
    nuevaFila.innerHTML = `
        <td>
            <input class="form-check-input checkbox-grande" type="checkbox" name="actividades[${idAct}][id]" value="${idAct}">
        </td>
        <td>
            <input type="hidden" name="actividades[${idAct}][ciiu]" value="${ciiu}">
            ${ciiu}
        </td>
        <td>
            <input type="hidden" name="actividades[${idAct}][nombre]" value="${nombre}">
            ${nombre}
        </td>

        <td>
            <button type="button" class="btn btn-danger btn-sm" onclick="eliminarActividad(${id})">
                <i class="fa fa-trash" ></i>
            </button>
        </td>

    `;

    // Añadir la nueva fila a la tabla
    document.querySelector("#tablaActividades tbody").appendChild(nuevaFila);

    // Cerrar el modal
    // var modal = bootstrap.Modal.getInstance(document.getElementById('actividadModal'));
    // modal.hide();
}
//limpia las filas de la tabla cuando se selecciona una persona
function limpiarTabla() {
    // Obtener el cuerpo de la tabla
    const tbody = document.querySelector("#tablaActividades tbody");

    // Eliminar todas las filas existentes
    while (tbody.firstChild) {
        tbody.removeChild(tbody.firstChild);
    }
}
function eliminarFila(boton) {
        // Eliminar la fila padre del botón clicado
        boton.closest("tr").remove();
}

function eliminarActividad(idactividad){
    if(confirm('¿Quiere eliminar el registro?')){
        vistacargando("m","Espere por favor")
        $.get("catastrocontribuyente/eliminar-activida-contr/"+idactividad, function(data){
            vistacargando("")
            if(data.error==true){
                alertNotificar(data.mensaje,"error");
                return;   
            }
        
            alertNotificar(data.mensaje,"success");
            cargaInfoContribuyente()
        
        }).fail(function(){
            vistacargando("")
            alertNotificar("Se produjo un error, por favor intentelo más tarde","error");  
        });
    }
}

function cargarparroquiaLocal(idcanton){
    // alert("asa")
    var canton_id = document.getElementById('canton_id');
    var parroquia_id = document.getElementById('parroquia_id');


    axios.post('catastrocontribuyente/parroquia', {
        _token: token,
        idcanton:idcanton
    }).then(function(res) {
        if(res.status==200) {
            console.log("cargando parroquia");
            parroquia_id.innerHTML = res.data;
        }
    }).catch(function(err) {
        if(err.response.status == 500){
            console.log('error al consultar al servidor');
        }

        if(err.response.status == 419){
            console.log('Es posible que tu session haya caducado, vuelve a iniciar sesion');
        }
    }).then(function() {

    });
}

globalThis.idEditarLocal=0
function editarLocal(id){
    idEditarLocal=id
    $('#provincia_local').val('')
    $('#canton_local').val('')
    $('#parroquia_id').val('')
    $('#callep_local').val('')
    $('#calles_local').val('')
    $('#referencia_local').val('')
    $('#ncomercial_local').val('')
    $('#estado_establecimiento_id').val('')
    $('#tipo_local').val('')
   
    $('#modalLocalLabel').html('Actualizar Local')
    $('#label_btn_local').html('Actualizar')
    cargarparroquiaLocal(157)
    
    vistacargando("m","Espere por favor")
    $.get('patente/ver-local/'+id, function(data){
       
        if(data.error==true){
            vistacargando("")
            alert(data.mensaje);
            return;   
        }
        
        $('#provincia_local').val('Manabi')
        $('#canton_local').val('San Vicente')
       
        $('#callep_local').val(data.data.calle_principal)
        $('#calles_local').val(data.data.calle_secundaria)
        $('#referencia_local').val(data.data.referencia_ubicacion)
        $('#ncomercial_local').val(data.data.actividad_descripcion)
        $('#estado_establecimiento_id').val(data.data.estado_establecimiento)
        $('#tipo_local').val(data.data.local_propio)
        
        setTimeout(() => {
            $('#parroquia_id').val(data.data.parroquia_id)
           
            vistacargando("")
            $('#modalLocal').modal('show')
        }, 500); // 5000 milisegundos = 5 segundos

       

    }).fail(function(){
        vistacargando("")
        alertNotificar('Ocurrió un error','error');
    })
   
}
$('#modalLocal').on('hidden.bs.modal', function () {
    $('#provincia_local').val('')
    $('#canton_local').val('')
    $('#parroquia_id').val('')
    $('#callep_local').val('')
    $('#calles_local').val('')
    $('#referencia_local').val('')
    $('#ncomercial_local').val('')
    $('#estado_establecimiento_id').val('')
    $('#tipo_local').val('')
});


document.querySelectorAll(".input-decimales").forEach(input => {
    // Formato al perder el foco
    input.addEventListener("blur", function() {
        formatearConDosDecimales(this);
    });

    // Formato al presionar Enter
    input.addEventListener("keypress", function(event) {
        if (event.key === "Enter") {
            formatearConDosDecimales(this);
        }
    });
});

function sumarValores(claseInputs, idExclusion) {
    // Selecciona todos los elementos con la clase especificada, excluyendo el campo con el id dado
    const inputs = document.querySelectorAll(`.${claseInputs}:not(#${idExclusion})`);
    let total = 0;

    // Suma los valores de los campos input
    inputs.forEach(input => {
        const value = parseFloat(input.value) || 0;
        total += value;
    });

    // Asigna el total al campo excluido
    document.getElementById(idExclusion).value = total.toFixed(2);

    //si  total activo y pasivo es diferente a null, calcula patrimonio_total
    var act=document.getElementById("act_total_activos").value
    var pas=document.getElementById("pas_total_pasivos").value
    if(pas!="" && act!=""){
        // alert(act+pas)
        // sumarValores('obligado_contabilidad', 'act_total_activos')
        restarValores('totales_pasivos_activos', 'patrimonio_total')
    }
}

function calcularBI(){
   var activo_total=$('#total_activo15').val()
   var pasivo_corriente=$('#cont_total_pasivos_corriente').val()
   var patrimonio_act=activo_total - pasivo_corriente
   patrimonio_act=patrimonio_act.toFixed(2)
   $('#cont_total_bi_act_total').val(patrimonio_act)
   calculaPatente()
}



function restarValores(claseInputs, idExclusion) {
    // alert("sa")
   
    // Selecciona todos los elementos con la clase especificada, excluyendo el campo con el id dado
    const inputs = document.querySelectorAll(`.${claseInputs}:not(#${idExclusion})`);
    let total = parseFloat(inputs[0]?.value) || 0; // Inicia con el primer valor

    // Resta los valores de los campos input a partir del segundo
    inputs.forEach((input, index) => {
        if (index > 0) { // Omite el primer elemento ya que se usó para inicializar `total`
            const value = parseFloat(input.value) || 0;
            total -= value;
        }
    });

    let es_activo_total=0
    var total_activo15=$('#total_activo15').val()

    if(LlevaContabilidad==1){
        // alert(LlevaContabilidad)
        var cont_total_activos=$('#cont_total_activos').val()
        // alert(cont_total_activos)
        // cont_total_activos=cont_total_activos.toFixed(2)
        $('#total_activo15').val(cont_total_activos)

    }
    // alert(total_activo15)

    // Asigna el total al campo excluido
    document.getElementById(idExclusion).value = total.toFixed(2);

    calculaPatente()
}


// Función para formatear el valor a dos decimales
function formatearConDosDecimales(input) {
    let valor = parseFloat(input.value);
    if (!isNaN(valor)) {
        input.value = valor.toFixed(2);
    }
}

// Obtener el checkbox y los fieldsets
const checkbox = document.getElementById('lleva_contabilidad');
const noObligadosFieldset = document.getElementById('no_obligados_fieldset');
const obligadosFieldset = document.getElementById('obligados_fieldset');

// Función para actualizar la visibilidad de los fieldsets
// function toggleFieldsets() {
//     let checkboxprofesional = document.getElementById("profesionales");
//     if (checkbox.checked) {
//         // Mostrar el fieldset para "Obligados a llevar contabilidad"
//         obligadosFieldset.style.display = 'block';
//         let div = document.getElementById("obligados_fieldset");
//         div.classList.toggle("mostrar");
//         // Ocultar el fieldset para "No obligados a llevar contabilidad"
//         noObligadosFieldset.style.display = 'none';

//         checkboxprofesional.checked =false
//     } else {
//         // Mostrar el fieldset para "No obligados a llevar contabilidad"
//         noObligadosFieldset.style.display = 'block';
//         // Ocultar el fieldset para "Obligados a llevar contabilidad"
//         obligadosFieldset.style.display = 'none';
//         checkboxprofesional.checked =false
//     }
// }

//     function toggleFieldsets() {
//     let checkbox = document.getElementById('lleva_contabilidad');
//     let checkboxprofesional = document.getElementById("profesionales");
//     let noObligadosFieldset = document.getElementById('no_obligados_fieldset');
//     let obligadosFieldset = document.getElementById('obligados_fieldset');

//     if (checkbox.checked) {
//         // Mostrar el fieldset para "Obligados a llevar contabilidad"
//         obligadosFieldset.classList.remove("d-none");
//         obligadosFieldset.classList.add("d-flex"); // Para que respete la estructura de Bootstrap
//         noObligadosFieldset.classList.add("d-none"); // Ocultar el otro fieldset
//         checkboxprofesional.checked = false;
//     } else {
//         // Mostrar el fieldset para "No obligados a llevar contabilidad"
//         noObligadosFieldset.classList.remove("d-none");
//         noObligadosFieldset.classList.add("d-flex");
//         obligadosFieldset.classList.add("d-none");
//         checkboxprofesional.checked = false;
//     }
// }


// Ejecutar la función al cambiar el estado del checkbox
// checkbox.addEventListener('change', toggleFieldsets);

// Ejecutar la función al cargar la página para establecer el estado inicial
// window.onload = toggleFieldsets;

document.addEventListener("DOMContentLoaded", function() {
// Ejecutar la llamada de Axios cuando el DOM esté listo


document.addEventListener('keydown', function (event) {
        // Verifica si la tecla presionada es ArrowDown o ArrowUp
        if (event.key === 'ArrowDown' || event.key === 'ArrowUp') {
            // Prevenir el comportamiento por defecto
            event.preventDefault();

            // Seleccionar todos los inputs con la clase "navegable"
            const inputs = Array.from(document.querySelectorAll('.input-decimales'));

            // Obtener el input actualmente enfocado
            const currentIndex = inputs.findIndex(input => input === document.activeElement);

            if (currentIndex !== -1) {
                let nextIndex;
                if (event.key === 'ArrowDown') {
                    // Siguiente índice (baja al siguiente input)
                    nextIndex = (currentIndex + 1) % inputs.length;
                } else if (event.key === 'ArrowUp') {
                    // Índice anterior (sube al input anterior)
                    nextIndex = (currentIndex - 1 + inputs.length) % inputs.length;
                }

                // Enfocar el siguiente o anterior input
                inputs[nextIndex].focus();
            }
        }
    });
});



document.getElementById("impuesto_1punto5").addEventListener("change", function() {
   
    LimpiarTotalActivo()
    LimpiarTotalPatente()

    calculaPatente()
});

document.getElementById("calcula_patente").addEventListener("change", function() {
   
   LimpiarTotalActivo()
   LimpiarTotalPatente()

   calculaPatente()
});



// document.getElementById("lleva_contabilidad").addEventListener("change", function() {
//     document.getElementById("cont_exoneracion").value= parseFloat(Math.round(0.00)).toFixed(2);
//     // document.getElementById("cont_pf").value = parseFloat(Math.round(0.00)).toFixed(2);
//     document.getElementById("cont_impuesto").value = parseFloat(Math.round(0.00)).toFixed(2);
//     document.getElementById("cont_sta").value = parseFloat(Math.round(0.00)).toFixed(2);
//     document.getElementById("cont_pago_patente").value = parseFloat(Math.round(0.00)).toFixed(2);

//     $('.desabilita_txt ').val('')
//     $('#cont_total_percibidos_sv').val(100)

//     let checkboxcalcula_patente = document.getElementById("calcula_patente");
//     let checkboximpuesto_1punto5 = document.getElementById("impuesto_1punto5");

//     let checkboxlleva_contabilidad = document.getElementById("lleva_contabilidad");
//     if (checkboxlleva_contabilidad.checked) {
//         LlevaContabilidad=1     
        
//         checkboxcalcula_patente.checked=true
//         checkboximpuesto_1punto5.checked=true

//     }else{
//         LlevaContabilidad=0        
//     }
//     LimpiarTotalActivo()
//     LimpiarTotalPatente()

//     // calculaPatente()
// });

// document.getElementById("calificacion_artesanal").addEventListener("change", function() {
    
//     let checkbox = document.getElementById("calificacion_artesanal");
//     let checkbox2 = document.getElementById("tercera_edad");
//     if (checkbox.checked) {
//         checkbox2.checked=false    
//         TerceraEdadSeleccionado=0          
//     }
//     LimpiarTotalActivo()
//     LimpiarTotalPatente()

//     calculaPatente()
// });
globalThis.TerceraEdadSeleccionado=0
// document.getElementById("tercera_edad").addEventListener("change", function() {
//     let checkbox = document.getElementById("calificacion_artesanal");
//     let checkbox2 = document.getElementById("tercera_edad");
//     if (checkbox2.checked) {
//         checkbox.checked=false   
//         TerceraEdadSeleccionado=1         
//     }else{
//         TerceraEdadSeleccionado=0        
//     }
//     calculaPatente()
// });

globalThis.EsProfesional=0
document.getElementById("profesionales").addEventListener("change", function() {
    if(LlevaContabilidad==1){
        $('#profesionales').prop('checked',false)
        alertNotificar("Opcion no valida el contribuyente lleva contabilidad","error")
        return
    }
    let checkboxlleva_contabilidad = document.getElementById("lleva_contabilidad");
    let checkboxprofesional = document.getElementById("profesionales");
    if (checkboxprofesional.checked) {
        // checkboxlleva_contabilidad.checked=false   
        EsProfesional=1     
        $('#act_caja_banco').val(0.00)   
        $('#act_ctas_cobrar').val(0.00)    
        $('#act_inv_mercaderia').val(0)   
        $('#act_vehiculo_maquinaria').val(0)   
        $('#act_equipos_oficinas').val(0)   
        $('#act_edificios_locales').val(0)   
        $('#act_terrenos').val(0)   
        $('#act_total_activos').val(0)   
        $('#pas_ctas_dctos_pagar').val(0)   
        $('#pas_obligaciones_financieras').val(0)   
        $('#pas_otras_ctas_pagar').val(0)   
        $('#pas_otros_pasivos').val(0)   
        $('#pas_total_pasivos').val(0)   
        $('#patrimonio_total').val(0)   
        // $('#act_caja_banco').val(0)   
    }else{
        EsProfesional=0        
    }
    // Mostrar el fieldset para "No obligados a llevar contabilidad"
    noObligadosFieldset.style.display = 'none';
    // Ocultar el fieldset para "Obligados a llevar contabilidad"
    obligadosFieldset.style.display = 'none';
    calculaPatente()
});

document.getElementById("cont_total_percibidos_sv").addEventListener("blur", function() {
    let anio_declaracion=$('#year_declaracion').val() 
    valor=$('#cont_total_patrimonio').val()

    if(valor=="" || anio_declaracion==""){return}
    let porcentaje=$('#cont_total_percibidos_sv').val()
    if(porcentaje>100 || porcentaje<=0){
        alertNotificar("El porcentaje debe ser mayor a cero y menor o igual a 100","error")
        return
    }
    calculaPatente()
});


function calculaPatente(){
    LimpiarTotalActivo()
    LimpiarTotalPatente()

    
    let checkbox1 = document.getElementById("lleva_contabilidad");
    let anio_declaracion=$('#year_declaracion').val()
    let obligado="N";
    let valor=0;
    let checkboxprofesional = document.getElementById("profesionales");
    var obligado_cont=$('#lleva_cont').val()

    if(obligado_cont=="Si"){
        obligado="S"
        let porcentaje=$('#cont_total_percibidos_sv').val()
        valor=$('#cont_total_patrimonio').val()
        valor = Number(valor) * Number(porcentaje/100)
        if(valor==0 || anio_declaracion==""){return}
    }

    
    // if (checkbox1.checked) {
    //     obligado="S"
    //     let porcentaje=$('#cont_total_percibidos_sv').val()
    //     valor=$('#cont_total_patrimonio').val()
    //     valor = Number(valor) * Number(porcentaje/100)
    //     if(valor==0 || anio_declaracion==""){return}
    // } else {
    //     obligado="N"
    //     valor=$('#patrimonio_total').val()
        
    //     if(!checkboxprofesional.checked){
    //         if(valor==0 || anio_declaracion==""){return}
    //     }
    // }
    
    // var lc=document.getElementById("local_propio").value 
    // if(lc==""){
    //     alert("Seleccione el contribuyente")
    //     return
    // }
    
    var impuesto=0;
    var exoneracion=0
    var sta=0
    var pf=0
    var total=0
    document.getElementById("cont_exoneracion").value =parseFloat(Math.round(0.00)).toFixed(2);

    // alert("s")
    if(checkboxprofesional.checked){

        valor=0
        var exoneracion=0        
        document.getElementById("cont_impuesto").value = parseFloat(Math.round(30.00)).toFixed(2);
        document.getElementById("cont_sta").value = parseFloat(Math.round(5.00)).toFixed(2);
        exoneracion=document.getElementById("cont_exoneracion").value
        sta=document.getElementById("cont_sta").value
        impuesto=document.getElementById("cont_impuesto").value

        total=(Number(impuesto) + Number(sta) + Number(pf)) - Number(exoneracion)
        document.getElementById("cont_pago_patente").value =total.toFixed(2);

        return
    }else {
        // valor=0

        document.getElementById("cont_exoneracion").value= parseFloat(Math.round(0.00)).toFixed(2);
      
        document.getElementById("cont_impuesto").value = parseFloat(Math.round(0.00)).toFixed(2);
        document.getElementById("cont_sta").value = parseFloat(Math.round(0.00)).toFixed(2);
        document.getElementById("cont_pago_patente").value = parseFloat(Math.round(0.00)).toFixed(2);

        let checkboxlleva_contabilidas = document.getElementById("lleva_contabilidad");
        // if(!checkboxlleva_contabilidas.checked){
            
        //     document.getElementById("cont_exoneracion").value= parseFloat(Math.round(0.00)).toFixed(2);
        //     // document.getElementById("cont_pf").value = parseFloat(Math.round(0.00)).toFixed(2);
        //     document.getElementById("cont_impuesto").value = parseFloat(Math.round(0.00)).toFixed(2);
        //     document.getElementById("cont_sta").value = parseFloat(Math.round(0.00)).toFixed(2);
        //     document.getElementById("cont_pago_patente").value = parseFloat(Math.round(0.00)).toFixed(2);

        //     // return
        // }
       
    }
   
    vistacargando("m","Espere por favor")
   
    $.get("patente/calcular-impuesto/"+valor+"/"+obligado+"/"+anio_declaracion+"/"+TerceraEdadSeleccionado, function(data){
        console.log(data)
        vistacargando("")
        if(data.error==true){
            // alertNotificar(data.mensaje,"error");
            alert(data.mensaje);
            return;   
        }
       
        impuesto=data.calcular.sumar_total;
        if(impuesto>25000){
            impuesto=parseFloat(Math.round(25000.00)).toFixed(2);
        }
        $('#cont_impuesto').val(impuesto)
        let checkbox = document.getElementById("calificacion_artesanal");
        let checkbox2 = document.getElementById("tercera_edad");

        let es_artesano=$('#artesano_cont').val()
        let es_mayor_Edad=$('#edad_cont').val()

        let porcentaje_exon=0
        if(es_artesano=="Si"){
        // if (checkbox.checked) {
           
            let exone= impuesto/2
            document.getElementById("cont_exoneracion").value =exone.toFixed(2);
            porcentaje_exon=50
        }   
       
        if(es_mayor_Edad>=65){
        // if (checkbox2.checked) {
           
            if(data.calcular.aplica==0){
                let exone= impuesto   
               
                document.getElementById("cont_exoneracion").value =exone; 
                porcentaje_exon=100
            }else{
                // document.getElementById("cont_exoneracion").value =data.calcular.comprobar; 
            }             
            
        }
       

        
        let checkboxImpAct = document.getElementById("impuesto_1punto5");
        let calculaActivo=0
        if (checkboxImpAct.checked) {
            calculaActivo=1
       }


    
        var patente = parseFloat(Math.round(0.00)).toFixed(2);

        document.getElementById("cont_sta").value = parseFloat(Math.round(5.00)).toFixed(2);

        exoneracion=document.getElementById("cont_exoneracion").value
        sta=document.getElementById("cont_sta").value
        // pf=document.getElementById("cont_pf").value
        pf=0;

        var intereses_=data.calcular.valor_intereses
        document.getElementById("cont_intereses").value =intereses_;

        var recargos_=data.calcular.valor_recargo
        document.getElementById("cont_recargos").value =recargos_;
       
        total=(Number(impuesto) + Number(sta) + Number(pf)) + Number(intereses_) + Number(recargos_) - Number(exoneracion)
        document.getElementById("cont_pago_patente").value =total.toFixed(2);

        if(obligado=="S" && calculaActivo==1){
            var valor_bi=$('#cont_total_bi_act_total').val()
            let porcentaje=$('#cont_total_percibidos_sv').val()
            valor_bi=valor_bi*(Number(porcentaje)/100)
            // cont_total_percibidos_sv
            calculaActivoTotales(valor_bi,porcentaje_exon,data.calcular.porcentaje_intereses, data.calcular.valor_recargo)
        }

        // if(obligado=="S"){
        //     $('#cont_impuesto').val('')
        //     $('#cont_exoneracion').val('')
        //     $('#cont_sta').val('')
        //     $('#cont_pago_patente').val('')
        // }   

      
    }).fail(function(){
        vistacargando("")
        alert("Se produjo un error, por favor intentelo más tarde");  
    });

}
function calculaActivoTotales(valor,porcentaje_exon, intereses, recargo){
    
    let activo_total=Number(valor) * (1.5/1000)

    document.getElementById("cont_sta").value = parseFloat(Math.round(5.00)).toFixed(2);

    let exoneracion_act=Number(activo_total) * Number(porcentaje_exon/100)
    let sta_act=document.getElementById("cont_sta").value
    sta_act=sta_act*1
    // let pf_act=document.getElementById("cont_pf").value
    let pf_act=0

    document.getElementById("cont_exoneracion_act").value = parseFloat(Math.round(exoneracion_act * 100) / 100).toFixed(2);
    document.getElementById("cont_impuesto_act").value = Math.round(activo_total * 100) / 100;
    document.getElementById("cont_sta_act").value =sta_act.toFixed(2);

    let recargo_aux=Number(recargo)
    let intereses_aux=Number(intereses)

    recargo_aux=Number(activo_total) * 0.10
    intereses_aux=Number(activo_total) * Number(intereses)
    // alert(intereses)
    // alert(recargo)


    document.getElementById("cont_intereses_act").value =intereses_aux.toFixed(2);
    document.getElementById("cont_recargos_act").value =recargo_aux.toFixed(2);

    // document.getElementById("cont_pf_act").value =Math.round(pf_act * 100) / 100;; 
    
    total=(Number(activo_total) + Number(sta_act) + Number(pf_act)) + Number(recargo_aux) + Number(intereses_aux) - Number(exoneracion_act)
    document.getElementById("cont_pago_activo_total").value =total.toFixed(2);

}

function LimpiarTotalActivo(){
    document.getElementById("cont_exoneracion_act").value = "";
    document.getElementById("cont_impuesto_act").value ="";
    document.getElementById("cont_pago_activo_total").value ="";
    document.getElementById("cont_sta_act").value ="";
    
}

function LimpiarTotalPatente(){
    
    document.getElementById("cont_impuesto").value = "";
    document.getElementById("cont_sta").value = "";
    document.getElementById("cont_exoneracion").value=""
    document.getElementById("cont_pago_patente").value ="";
}

$("#formPatente").submit(function(e){
   
    e.preventDefault();
    
    //validamos los campos obligatorios
    let catastro_id=$('#cmb_propietario').val()

       
    if(catastro_id=="" || catastro_id==null){
        alertNotificar("Debe seleccionar un contribuyente","error")
        return
    } 

    var num_locales = $("#tablaLocales tbody tr").length;
   
    let actividadesSeleccionados = $("input[name^='actividades']:checked").length;
        
    if (actividadesSeleccionados === 0 ) {
        alertNotificar("Debe seleccionar al menos una actividad", "error");
        return;
    }

    let localesSeleccionados = $("input[name^='locales']:checked").length;
        
    if (localesSeleccionados === 0 || localesSeleccionados >1) {
        alertNotificar("Debe seleccionar un local", "error");
        return;
    }

    let anio_declaracion=$('#year_declaracion').val()
       
    if(anio_declaracion=="" || anio_declaracion==null){
        alertNotificar("Debe seleccionar el año de declaracion","error")
        return
    } 

    let fecha_declaracion=$('#fecha_declaracion').val()
       
    if(fecha_declaracion=="" || fecha_declaracion==null){
        alertNotificar("Debe seleccionar la fecha de declaracion","error")
        return
    } 

    if(EsProfesional==0){
        
        if(LlevaContabilidad==0){
            let act_caja_banco=$('#act_caja_banco').val()
       
            if(act_caja_banco=="" || act_caja_banco==null){
                alertNotificar("Debe ingresar el activo caja y bancos","error")
                $('#act_caja_banco').focus()
                return
            } 

            let act_ctas_cobrar=$('#act_ctas_cobrar').val()
            if(act_ctas_cobrar=="" || act_ctas_cobrar==null){
                alertNotificar("Debe ingresar el activo cuentas por cobrar","error")
                $('#act_ctas_cobrar').focus()
                return
            } 

            let act_inv_mercaderia=$('#act_inv_mercaderia').val()
            if(act_inv_mercaderia=="" || act_inv_mercaderia==null){
                alertNotificar("Debe ingresar el activo inventario de mercaderia","error")
                $('#act_inv_mercaderia').focus()
                return
            }
            
            let act_vehiculo_maquinaria=$('#act_vehiculo_maquinaria').val()
            if(act_vehiculo_maquinaria=="" || act_vehiculo_maquinaria==null){
                alertNotificar("Debe ingresar el activo vehiculo y maquinaria","error")
                $('#act_vehiculo_maquinaria').focus()
                return
            }

            let act_equipos_oficinas=$('#act_equipos_oficinas').val()
            if(act_equipos_oficinas=="" || act_equipos_oficinas==null){
                alertNotificar("Debe ingresar el activo equipos de oficina","error")
                $('#act_equipos_oficinas').focus()
                return
            }

            let act_edificios_locales=$('#act_edificios_locales').val()
            if(act_edificios_locales=="" || act_edificios_locales==null){
                alertNotificar("Debe ingresar el activo edificios y locales","error")
                $('#act_edificios_locales').focus()
                return
            }

            let act_terrenos=$('#act_terrenos').val()
            if(act_terrenos=="" || act_terrenos==null){
                alertNotificar("Debe ingresar el activo terrenos","error")
                $('#act_terrenos').focus()
                return
            }

            let pas_ctas_dctos_pagar=$('#pas_ctas_dctos_pagar').val()
            if(pas_ctas_dctos_pagar=="" || pas_ctas_dctos_pagar==null){
                alertNotificar("Debe ingresar el pasivo cuentas y documentos por pagar","error")
                $('#pas_ctas_dctos_pagar').focus()
                return
            }


            let pas_obligaciones_financieras=$('#pas_obligaciones_financieras').val()
            if(pas_obligaciones_financieras=="" || pas_obligaciones_financieras==null){
                alertNotificar("Debe ingresar el pasivo obligaciones financieras","error")
                $('#pas_obligaciones_financieras').focus()
                return
            }

            let pas_otras_ctas_pagar=$('#pas_otras_ctas_pagar').val()
            if(pas_otras_ctas_pagar=="" || pas_otras_ctas_pagar==null){
                alertNotificar("Debe ingresar el pasivo cuentas por pagar","error")
                $('#pas_otras_ctas_pagar').focus()
                return
            }

            let pas_otros_pasivos=$('#pas_otros_pasivos').val()
            if(pas_otros_pasivos=="" || pas_otros_pasivos==null){
                alertNotificar("Debe ingresar el pasivo otros pasivos","error")
                $('#pas_otros_pasivos').focus()
                return
            }


        }else{
            let cont_total_activos=$('#cont_total_activos').val()
            if(cont_total_activos=="" || cont_total_activos==null){
                alertNotificar("Debe ingresar el total de activosQQQ","error")
                $('#cont_total_activos').focus()
                return
            }

            let cont_total_pasivos=$('#cont_total_pasivos').val()
            if(cont_total_pasivos=="" || cont_total_pasivos==null){
                alertNotificar("Debe ingresar el total de pasivos","error")
                $('#cont_total_pasivos').focus()
                return
            }

            // if(Emitir==1){
                let cont_form_sri=$('#cont_form_sri').val()
                if(cont_form_sri=="" || cont_form_sri==null){
                    alertNotificar("Debe ingresar numero formulario SRI","error")
                    $('#cont_form_sri').focus()
                    return
                }

                let cont_original=$('#cont_original').val()
                if(cont_original=="" || cont_original==null){
                    alertNotificar("Debe seleccionar si es Original o Sustitutiva","error")
                    return
                }

                let archivo_patente=$('#archivo_patente').val()
                if(archivo_patente=="" || archivo_patente==null){
                    alertNotificar("Debe seleccionar el documento","error")
                    return
                }
            // }

        }
    }
    // var ruta="patente/simular-patente"
    // if(Emitir==1){
    //     ruta="patente"
    // }

    

    var ruta="patente"
    
    vistacargando("m","Espere por favor")
    $.ajaxSetup({
        headers: {
            'X-CSRF-TOKEN': $('meta[name="csrf-token"]').attr('content')
        }
    });

    //comprobamos si es registro o edicion
    let AccionForm="R"
    let tipo_=""
    let url_form=""
    if(AccionForm=="R"){
        tipo_="POST"
        url_form=ruta
    }else{
        tipo_="PUT"
        url_form=ruta
    }

    // var FrmData=$("#formPatente").serialize();
    var FrmData = new FormData(this);

    let actividadesSeleccionado = $("input[name^='actividades']:checked")
    actividadesSeleccionado.each(function () {
        FrmData.append("actividades[]", $(this).val());
    });

    $.ajax({
            
        type: tipo_,
        url: url_form,
        method: tipo_,             
        // data: FrmData,      
        
        // processData:false, 
        data: FrmData,
        contentType:false,
        cache:false,
        processData:false, 

        success: function(data){
            console.log(data)
            vistacargando("");                
            if(data.error==true){
                alertNotificar(data.mensaje,'error');
                return;                      
            }
            if(Emitir==1){
                // limpiarCampos()
            }
            alertNotificar(data.mensaje,"success");
            crearPdf(data.id)
             
        }, error:function (data) {
            console.log(data)

            vistacargando("");     
            alertNotificar('Ocurrió un error','error');
        }
    });
})

function crearPdf(id){
    vistacargando("m","Espere por favor")
    $.get('patente/reporte/'+id, function(data){
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

function NuevoActividad(){
    $('#modalActividadLabel').html('Nueva Actividad')
    $('#label_btn_actividad').html('Guardar')
    $('#actividadLocal').modal('show')
    limpiarCampoActividad()
}

function limpiarCampoActividad(){

    $('.modal_act').val('')
}

function NuevoLocal(){
    idEditarLocal=0
    $('#modalLocalLabel').html('Nuevo Local')
    $('#label_btn_local').html('Guardar')
    $('#modalLocal').modal('show')
    $('#provincia_local').val('Manabi')
    $('#canton_local').val('San Vicente')
    cargarparroquiaLocal(157)
}

function guardaLocal(){
    var provincia_local=13
    var canton_local=157
    var parroquia_local=$('#parroquia_id').val()
    var calle_principal=$('#callep_local').val()
    var calle_secundaria=$('#calles_local').val()
    var referencia_local=$('#referencia_local').val()
    var ncomercial_local=$('#ncomercial_local').val()
    var estado_establecimiento_id=$('#estado_establecimiento_id').val()
    var tipo_local=$('#tipo_local').val()
    var cmb_propietario=$('#cmb_propietario').val()

    if(cmb_propietario==null || cmb_propietario==""){
        alertNotificar("Debe primero seleccionar el contribuyente","error")
        return
    }

    if(parroquia_local==null || parroquia_local==""){
        alertNotificar("Debe seleccionar la parroquia","error")
        return
    }

    if(calle_principal==null || calle_principal==""){
        alertNotificar("Debe ingresar la calle principal","error")
        return
    }

    if(referencia_local==null || referencia_local==""){
        alertNotificar("Debe ingresar la referencia","error")
        return
    }

    if(ncomercial_local==null || ncomercial_local==""){
        alertNotificar("Debe ingresar el nombre comercial","error")
        return
    }

    if(estado_establecimiento_id==null || estado_establecimiento_id==""){
        alertNotificar("Debe seleccionar el estado","error")
        return
    }

    if(tipo_local==null || tipo_local==""){
        alertNotificar("Debe seleccionar el tipo","error")
        return
    }
    

    vistacargando("m","Espere por favor")
    $.ajaxSetup({
        headers: {
            'X-CSRF-TOKEN': $('meta[name="csrf-token"]').attr('content')
        }
    });
    var tipo='POST'
    $.ajax({
        
        type: tipo,
        url: "catastrocontribuyente/agregar-local",
        method: tipo,             
        data: {
            prov: provincia_local,
            cant: canton_local,
            parr: parroquia_local,
            calle_princ: calle_principal,
            calle_secund: calle_secundaria,
            referencia: referencia_local,
            descr: ncomercial_local,
            establ: estado_establecimiento_id,
            tipo: tipo_local,
            idcont:cmb_propietario,
            idEditarLocal:idEditarLocal
        },
        
        // processData:false, 

        success: function(response) {
            vistacargando("")
            if(response.error==true){
                alertNotificar(response.mensaje,"error")
                return
            }
            alertNotificar(response.mensaje,"success")
            $('#modalLocal').modal('hide')
            
            cargaInfoContribuyente()
        },
        error: function(xhr, status, error) {
            vistacargando("")
            console.error("Error al obtener los datos:", error);
        }
    });
}

function NuevoContribuyente(){
    $('#modalContribuyenteLabel').html('Nueva Actividad')
    $('#label_btn_contribuyente').html('Guardar')
    $('#modalContri').modal('show')
    
    limpiarCamposModalCont()
}

function limpiarCamposModalCont(){
    $('.modal_new_cont').val('')
    $('#es_artesano').prop('checked', false);
    $('#obligado_contabilidad').prop('checked', false);
    $('#doc_artesano').prop('disabled', true);
}

function seleccionaProvincia(){
    var idprov=$('#provincia').val()
    if(idprov=="" || idprov==null){return}
    cargarcantones(idprov)
}

function cargarcantones(idprovincia){
    var canton_id = document.getElementById('canton_id');

    axios.post('catastrocontribuyente/canton', {
        _token: token,
        idprovincia:idprovincia
    }).then(function(res) {
        if(res.status==200) {
            console.log("cargando cantones");
            canton_id.innerHTML = res.data;
        }
    }).catch(function(err) {
        if(err.response.status == 500){
            console.log('error al consultar al servidor');
        }

        if(err.response.status == 419){
            console.log('Es posible que tu session haya caducado, vuelve a iniciar sesion');
        }
    }).then(function() {

    });
}

function seleccionaParroquia(){
     var idcanton=$('#canton_id').val()
    if(idcanton=="" || idcanton==null){return}
    cargarparroquia(idcanton)
}

function cargarparroquia(idcanton){
   
    var parroquia_id = document.getElementById('parroquia_id_');

    axios.post('catastrocontribuyente/parroquia', {
        _token: token,
        idcanton:idcanton
    }).then(function(res) {
        if(res.status==200) {
            console.log("cargando parroquia");
            console.log(res)
            parroquia_id.innerHTML = res.data;
        }
    }).catch(function(err) {
        if(err.response.status == 500){
            console.log('error al consultar al servidor');
        }

        if(err.response.status == 419){
            console.log('Es posible que tu session haya caducado, vuelve a iniciar sesion');
        }
    }).then(function() {

    });
}
globalThis.EsArtesanoNew=0
$('#es_artesano').change(function () {
    EsArtesanoNew=0
    $('#doc_artesano').val('')
    $('#doc_artesano').prop('disabled', true);
    if ($(this).is(':checked')) {

        var tipo=$('#tipo_persona_new').val()
        if(tipo==2){
           alertNotificar("No se permite como artesano a SOCIEDADES","error")
            $('#es_artesano').prop('checked', false);
           return 
        }

        $('#doc_artesano').prop('disabled', false);
        EsArtesanoNew=1
        
    } 
});

globalThis.LlevaContabilidaNew=0
$('#obligado_contabilidad').change(function () {
    LlevaContabilidaNew=0
    $('#doc_ruc').val('')
   
    if ($(this).is(':checked')) {
        // $('#doc_ruc').prop('disabled', false);
       
        LlevaContabilidaNew=1
    } 
});

$('#modalContri').on('shown.bs.modal', function () {
    $('#cmb_ruc').select2({
        dropdownParent: $('#modalContri'),
        ajax: {
            url: 'catastrocontribuyente/buscarRucContribuyente',
            dataType: 'json',
            delay: 250,
            data: function (params) {
                return {
                    q: params.term
                };
            },
            processResults: function (data) {
                console.log(data)
                return {
                    results: data.map(item => ({
                        
                        id: item.id,
                        text: item.id + " - " + item.text
                    }))
                };
            }
        },
        minimumInputLength: 13
    });

    $('#cmb_ruc_rep').select2({
        dropdownParent: $('#modalContri'),
        ajax: {
            url: 'catastrocontribuyente/buscarRucContribuyente',
            dataType: 'json',
            delay: 250,
            data: function (params) {
                return {
                    q: params.term
                };
            },
            processResults: function (data) {
                console.log(data)
                return {
                    results: data.map(item => ({
                        
                        id: item.id,
                        text: item.id + " - " + item.text
                    }))
                };
            }
        },
        minimumInputLength: 13
    });

});

function cargaContribuyente(){
    var nombre_contr=$('#cmb_ruc option:selected').text();
    nombre_contr=nombre_contr.split("-")
    $('#contribuyente').val(nombre_contr[1])
}

function cargaRepresentanteLegal(){
    var nombre_rl=$('#cmb_ruc_rep option:selected').text();
    nombre_rl=nombre_rl.split("-")
    $('#representante').val(nombre_rl[1])
}

function cambioTipoPersona(){
    limpiaTipo()
    var tipo_per=$('#tipo_persona_new').val()
    if(tipo_per==1){
        $('#representante').prop('disabled', true);
        $('#cmb_ruc_rep').prop('disabled', true);
    }else{
        $('#obligado_contabilidad').prop('checked', true);
        $('#es_artesano').prop('checked', false);
        $('#doc_artesano').val('');
    }
    
}

function limpiaTipo(){
    $('#contribuyente').prop('disabled', false);
    $('#cmb_ruc_rep').prop('disabled', false);

    $('#representante').val('');
    $('#cmb_ruc_rep').val('').trigger('change.select2')

    $('#contribuyente').val('');
    $('#cmb_ruc').val('').trigger('change.select2')
}

function guardaContribuyente(){
    var tipo=$('#tipo_persona_new').val()
    var contribuyente=$('#cmb_ruc').val()
    var txt_contribuyente=$('#contribuyente').val()
    var fecha_nacimiento=$('#fecha_nacimiento').val()
    var cmb_ruc_rep=$('#cmb_ruc_rep').val()
    var txt_repres=$('#representante').val()
    var id_provincia=$('#provincia').val()
    var id_canton=$('#canton_id').val()
    var id_parroquia=$('#parroquia_id_').val()
    var direccion=$('#direccion').val()
    var correo=$('#correo').val()
    var telefono=$('#telefono').val()
    var doc_ruc=$('#doc_ruc').val()
    var doc_artesano=$('#doc_artesano').val()
    var clase_contribuyente_id=$('#clase_contribuyente_id').val()

    if(tipo=="" || tipo==null){
        alertNotificar("Seleccione un tipo","error")
        return
    }

    if(contribuyente=="" || contribuyente==null){
        alertNotificar("Seleccione un contribuyente","error")
        return
    }

    if(fecha_nacimiento=="" || fecha_nacimiento==null){
        alertNotificar("Ingrese la fecha de nacimiento","error")
        return
    }

    if(tipo==2){
        if(cmb_ruc_rep=="" || cmb_ruc_rep==null){
            alertNotificar("Seleccione un representante legal","error")
            return
        }
    }

    if(id_provincia=="" || id_provincia==null){
        alertNotificar("Seleccione la provincia","error")
        return
    }

    if(id_canton=="" || id_canton==null){
        alertNotificar("Seleccione el canton","error")
        return
    }

    if(id_parroquia=="" || id_parroquia==null){
        alertNotificar("Seleccione la parroquia","error")
        return
    }

    if(direccion=="" || direccion==null){
        alertNotificar("Ingrese la direccion","error")
        return
    }

    if(correo=="" || correo==null){
        alertNotificar("Ingrese el correo","error")
        return
    }

    if(telefono=="" || telefono==null){
        alertNotificar("Ingrese el telefono","error")
        return
    }

    if(EsArtesanoNew==1){
        if(doc_artesano=="" || doc_artesano==null){
            alertNotificar("Seleccione el documento del artesano","error")
            return
        } 
    }

    if(doc_ruc=="" || doc_ruc==null){
        alertNotificar("Seleccione el documento del RUC","error")
        return
    } 

    if(clase_contribuyente_id=="" || clase_contribuyente_id==null){
        alertNotificar("Seleccione el regimen","error")
        return
    } 

    $("#form_new_contribuyente").submit()

}

$("#form_new_contribuyente").submit(function(e){
   
    e.preventDefault();

    var ruta="patente-new-contribuyente"
    
    vistacargando("m","Espere por favor")
    $.ajaxSetup({
        headers: {
            'X-CSRF-TOKEN': $('meta[name="csrf-token"]').attr('content')
        }
    });

    //comprobamos si es registro o edicion
    let AccionForm="R"
    let tipo_=""
    let url_form=""
    if(AccionForm=="R"){
        tipo_="POST"
        url_form=ruta
    }else{
        tipo_="PUT"
        url_form=ruta
    }

    // var FrmData=$("#formPatente").serialize();
    var FrmData = new FormData(this);

    $.ajax({
            
        type: tipo_,
        url: url_form,
        method: tipo_,             
        // data: FrmData,      
        
        // processData:false, 
        data: FrmData,
        contentType:false,
        cache:false,
        processData:false, 

        success: function(data){
            console.log(data)
            vistacargando("");                
            if(data.error==true){
                alertNotificar(data.mensaje,'error');
                return;                      
            }
            
            $('#modalContri').modal('hide')
            alertNotificar(data.mensaje,'success');

        }, error:function (data) {
            console.log(data)

            vistacargando("");     
            alertNotificar('Ocurrió un error','error');
        }
    });

})

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
    $.get('patente/llenar-tabla-rango', function(data){
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
                                                <td style="width:10%; text-align:center; vertical-align:middle">
                                                    ${item.desde} 
                                                    
                                                </td>

                                                <td style="width:10%;  text-align:left; vertical-align:middle">
                                                    ${valor_hasta} 
                                                </td>
                                               
                                                <td style="width:10%; text-align:center; vertical-align:middle">
                                                    ${item.dolares}
                                                </td>

                                                <td style="width:20%; text-align:center; vertical-align:middle">
                                                    ${item.imp_sobre_fraccion_exec}
                                                </td>

                                               
                                                <td style="width:10%; text-align:right; vertical-align:middle">
                                                    <button class="btn btn-sm btn-warning" onclick="editarRango('${item.id}','${item.desde}','${item.hasta}','${item.dolares}','${item.imp_sobre_fraccion_exec}')"><i class="bi bi-pencil-square"></i></button>
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

$("#form_base").submit(function(e){
    e.preventDefault();
    
    //validamos los campos obligatorios
    let desde_base=$('#desde_base').val()
    let hasta_base=$('#hasta_base').val()
    let valor_base=$('#valor_base').val()
    let impuesto=$('#impuesto_fracion').val()

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

    if(impuesto=="" || impuesto==null){
        alertNotificar("Debe ingresar impuesto","error")
        $('#impuesto').focus()
        return
    } 

    if (isNaN(impuesto) || impuesto.trim() === "") {
        alertNotificar('Debe ingresar impuesto',"error");
        $('#impuesto').focus()
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
        url_form="patente/guardar-rango"
    }else{
        tipo="PUT"
        url_form="patente/actualizar-rango/"+IdEditarRango
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

function editarRango(id, desde, hasta, rango, impuesto){
    $('#desde_base').val(desde)
    $('#hasta_base').val(hasta)
    $('#valor_base').val(rango)
    $('#impuesto_fracion').val(impuesto)
    $('#id_base').val(id)
    $('#btn_base').html('Actualizar')
    FormAccionRango='A'
    IdEditarRango=id
}

function cancelarRango(){
    $('#desde_base').val('')
    $('#hasta_base').val('')
    $('#valor_base').val('')
    $('#impuesto_fracion').val('')
    $('#id_base').val('')
    $('#btn_base').html('Guardar')
    FormAccionRango='R'
}