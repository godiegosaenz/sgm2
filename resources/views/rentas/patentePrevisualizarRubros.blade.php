@extends('layouts.appv2')
@section('title', 'Editar patente')
@push('styles')
<style>
.checkbox-grande {
    transform: scale(1.5); /* Ajusta el número para el tamaño deseado */
    margin-right: 8px; /* Ajuste opcional */
}
</style>

@endpush
@section('content')
<div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
    <h4 class="h2">Editar declaracion de patente</h4>
    <div class="btn-toolbar mb-2 mb-md-0">
        <button type="button" class="btn btn-sm btn-primary d-flex align-items-center gap-1 me-2" onclick="enviarFormulario()">
            Actualizar Declaracion
        </button>
        <a href="{{ route('create.patente') }}" class="btn btn-sm btn-secondary d-flex align-items-center gap-1">
            Nuevo Declaracion
        </a>
    </div>
</div>
@if(@session('error'))
        <div class="alert alert-danger">
            {{session('error')}}
        </div>
@endif
@if(@session('success'))
        <div class="alert alert-success">
            {{session('success')}}
        </div>
@endif
@if ($errors->any())
    <div class="alert alert-danger">
        <ul>
            @foreach ($errors->all() as $error)
                <li>{{ $error }}</li>
            @endforeach
        </ul>
    </div>
@endif

<fieldset class="border p-3">
    <legend class="float-none w-auto px-2 text-primary fw-bold">Previsualización de Rubros de Cobro</legend>
    <form>
    <div class="row mb-3">
        <label for="rubro1" class="col-sm-4 col-form-label">Rubro 1:</label>
        <div class="col-sm-8">
        <input type="text" id="rubro1" name="rubro1" class="form-control" placeholder="Ingrese el rubro 1">
        </div>
    </div>
    <div class="row mb-3">
        <label for="rubro2" class="col-sm-4 col-form-label">Rubro 2:</label>
        <div class="col-sm-8">
        <input type="text" id="rubro2" name="rubro2" class="form-control" placeholder="Ingrese el rubro 2">
        </div>
    </div>
    <div class="row mb-3">
        <label for="rubro3" class="col-sm-4 col-form-label">Rubro 3:</label>
        <div class="col-sm-8">
        <input type="text" id="rubro3" name="rubro3" class="form-control" placeholder="Ingrese el rubro 3">
        </div>
    </div>
    <div class="row mb-3">
        <label for="rubro4" class="col-sm-4 col-form-label">Rubro 4:</label>
        <div class="col-sm-8">
        <input type="text" id="rubro4" name="rubro4" class="form-control" placeholder="Ingrese el rubro 4">
        </div>
    </div>
    <div class="text-end">
        <button type="submit" class="btn btn-primary">Previsualizar</button>
    </div>
    </form>
</fieldset>

@endsection
@push('scripts')
<!-- jQuery -->
<script src="{{ asset('js/jquery-3.5.1.js') }}"></script>
<!-- DataTables -->

<script src="{{ asset('js/jquery.dataTables.min.js') }}"></script>
<script src="{{ asset('js/dataTables.bootstrap5.min.js') }}"></script>
<script src="{{ asset('js/dataTables.rowReorder.min.js') }}"></script>
<script>
    $(document).ready(function(){
        tablaContribuyente = $("#tablaContribuyente").DataTable({
            "lengthMenu": [ 5, 10],
            "language" : {
                "url": '{{ asset("/js/spanish.json") }}',
            },
            "autoWidth": true,
            "order": [], //Initial no order
            "processing" : true,
            "serverSide": true,
            "ajax": {
                "url": '{{ url("/catastrocontribuyente/datatables") }}',
                "type": "post",
                "data": function (d){
                    d._token = $("input[name=_token]").val();
                }
            },
            //"columnDefs": [{ targets: [3], "orderable": false}],
            "columns": [
                {width: '',data: 'action', name: 'action', orderable: false, searchable: false},
                {width: '',data: 'ruc', name: 'ruc'},
                {width: '',data: 'razon_social', name: 'razon_social'},
            ],
            "fixedColumns" : true
        });
    })
</script>
<script>
    let token = "{{csrf_token()}}";
    function limpiarCampos() {
        document.getElementById("propietario_nombre").value = "";
        document.getElementById("propietario_cedula").value = "";
        document.getElementById("ruc_cedula").value = "";
        document.getElementById("nombre_contribuyente").value = "";
        document.getElementById("razon_social").value = "";
        document.getElementById("domicilio").value = "";
        document.getElementById("Telefono_domicilio").value = "";
        document.getElementById("correo_electronico").value = "";
        document.getElementById("representante_legal").value = "";
        document.getElementById("cedula_representante").value = "";
        document.getElementById("inicio_actividades").value = "";
        document.getElementById("estado_contribuyente").value = "";
        document.getElementById("clase_contribuyente").value = "";
        document.getElementById("tipo_contribuyente").value = "";
        document.getElementById("num_establecimientos").value = "";
        document.getElementById("estado_establecimiento").value = "";
        document.getElementById("local_propio").value = "";
        document.getElementById("catastro_id").value = "";
    }

    function enviarFormulario() {
            // Selecciona el formulario y lo envía
            document.getElementById("formPatente").submit();
    }
    // funcion para seleccionar contribuyente y cargar los datos
    function seleccionarcontribuyente(id){
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
                if (data.local_propio === 1) {
                    localPropio = "Propio";
                } else if (tipo_contribuyente === 2) {
                    localPropio = "Arrendado";
                } else {
                    localPropio = "Desconocido"; // Opcional: texto si el estado no coincide
                }
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
                document.getElementById("correo_electronico").value = data.correo_1 ?? "";
                document.getElementById("representante_legal").value = data.representante_legal_id ?? "";
                document.getElementById("cedula_representante").value = data.representante_legal_id ?? ""; // Ajustar si tienes datos separados para la cédula
                document.getElementById("inicio_actividades").value = data.fecha_inicio_actividades ?? "";
                document.getElementById("estado_contribuyente").value = estadoContribuyente;
                document.getElementById("clase_contribuyente").value = dataclase.nombre ?? "";
                document.getElementById("tipo_contribuyente").value = tipoContribuyente ?? "";
                document.getElementById("num_establecimientos").value = data.id ?? "";
                document.getElementById("estado_establecimiento").value = estadoEstablecimiento ?? "";
                document.getElementById("local_propio").value = localPropio ?? "";
                actividadesComerciales.forEach((actividad) => {
                    seleccionarActividad(actividad.id,actividad.descripcion,actividad.ciiu);
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
                if (data.local_propio === 1) {
                    localPropio = "Propio";
                } else if (tipo_contribuyente === 2) {
                    localPropio = "Arrendado";
                } else {
                    localPropio = "Desconocido"; // Opcional: texto si el estado no coincide
                }
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
                document.getElementById("correo_electronico").value = data.correo_1 ?? "";
                document.getElementById("representante_legal").value = data.representante_legal_id ?? "";
                document.getElementById("cedula_representante").value = data.representante_legal_id ?? ""; // Ajustar si tienes datos separados para la cédula
                document.getElementById("inicio_actividades").value = data.fecha_inicio_actividades ?? "";
                document.getElementById("estado_contribuyente").value = estadoContribuyente;
                document.getElementById("clase_contribuyente").value = dataclase.nombre ?? "";
                document.getElementById("tipo_contribuyente").value = tipoContribuyente ?? "";
                document.getElementById("num_establecimientos").value = data.id ?? "";
                document.getElementById("estado_establecimiento").value = estadoEstablecimiento ?? "";
                document.getElementById("local_propio").value = localPropio ?? "";

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
    //una vez cargados los contribuyentes esta funcion hace cargar las actividades asociadas
    function seleccionarActividad(id, nombre, ciiu) {
        // Crear un nuevo elemento <tr>
        limpiarTabla();
        const nuevaFila = document.createElement("tr");

        // Definir el contenido de la nueva fila, incluyendo inputs ocultos para enviar a Laravel
        nuevaFila.innerHTML = `
            <td>
                <input class="form-check-input checkbox-grande" type="checkbox" name="actividades[${id}][id]" value="${id}">
            </td>
            <td>
                <input type="hidden" name="actividades[${id}][ciiu]" value="${ciiu}">
                ${ciiu}
            </td>
            <td>
                <input type="hidden" name="actividades[${id}][nombre]" value="${nombre}">
                ${nombre}
            </td>

        `;

        // Añadir la nueva fila a la tabla
        document.querySelector("#tablaActividades tbody").appendChild(nuevaFila);

        // Cerrar el modal
        var modal = bootstrap.Modal.getInstance(document.getElementById('actividadModal'));
        modal.hide();
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

    function cargarparroquia(idcanton){
        var canton_id = document.getElementById('canton_id');

        axios.post('{{route('getparroquia.catastro')}}', {
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
    }

    function restarValores(claseInputs, idExclusion) {
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

        // Asigna el total al campo excluido
        document.getElementById(idExclusion).value = total.toFixed(2);
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
    function toggleFieldsets() {
        if (checkbox.checked) {
            // Mostrar el fieldset para "Obligados a llevar contabilidad"
            obligadosFieldset.style.display = 'block';
            // Ocultar el fieldset para "No obligados a llevar contabilidad"
            noObligadosFieldset.style.display = 'none';
        } else {
            // Mostrar el fieldset para "No obligados a llevar contabilidad"
            noObligadosFieldset.style.display = 'block';
            // Ocultar el fieldset para "Obligados a llevar contabilidad"
            obligadosFieldset.style.display = 'none';
        }
    }

    // Ejecutar la función al cambiar el estado del checkbox
    checkbox.addEventListener('change', toggleFieldsets);

    // Ejecutar la función al cargar la página para establecer el estado inicial
    window.onload = toggleFieldsets;

    document.addEventListener("DOMContentLoaded", function() {
    // Ejecutar la llamada de Axios cuando el DOM esté listo
    @if (old('catastro_id'))
        @if (old('catastro_id') != '')
        seleccionarcontribuyentesinactividad({{old('catastro_id')}});
        @endif
    @endif

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
</script>
@endpush
