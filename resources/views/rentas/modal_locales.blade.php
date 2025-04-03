<div class="modal fade" id="modal_local" tabindex="-1" aria-labelledby="propietarioModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-xl">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="modalContribuyenteLabel">Locales</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            @csrf
            <div class="modal-body">
                <div class="row mb-12" style="margin-bottom: 35px;">
                    <!-- Campo Provincia -->
                    <div class="col-md-6">
                        <label for="provincia" class="form-label">Provincia <span class="text-danger">*</span></label>
                        <div class="input-group">
                            <select class="form-select {{$errors->has('provincia_id_modal') ? 'is-invalid' : ''}}" id="provincia_id_modal" name="provincia_id_modal" >
                                <option value="">Seleccione una provincia</option>
                                @foreach ($PsqlProvincia as $p)
                                    <option value="{{$p->id}}" {{ old('provincia_id_modal') == $p->id ? 'selected' : '' }}>{{$p->descripcion}}</option>
                                @endforeach
                            </select>
                            
                        </div>
                    </div>
                    <input type="hidden" name="idcont" id="idcont">

                    <!-- Campo Cantón -->
                    <div class="col-md-6">
                        <label for="canton" class="form-label">Cantón <span class="text-danger">*</span></label>
                        <div class="input-group">
                            <select class="form-select {{$errors->has('canton_id_modal') ? 'is-invalid' : ''}}" id="canton_id_modal" name="canton_id_modal" required>
                            <option value="" id="optionSelectCanton">Seleccione un cantón</option>
                                @if(old('provincia_id'))
                                    @foreach (session('cantones') as $c)
                                        <option value="{{$c->id}}" {{ old('canton_id_modal') == $c->id ? 'selected' : '' }}>{{$c->nombre}}</option>
                                    @endforeach
                                @endif
                            </select>
                            
                        </div>
                    </div>

                    <!-- Campo Parroquia -->
                    <div class="col-md-6">
                        <label for="canton" class="form-label">Parroquia <span class="text-danger">*</span></label>
                        <div class="input-group">
                            <select class="form-select {{$errors->has('parroquia_id_modal') ? 'is-invalid' : ''}}" id="parroquia_id_modal" name="parroquia_id_modal" required>
                                <option value="" id="optionSelectParroquia">Seleccione una parroquia</option>
                                @if(old('canton_id'))
                                    @foreach (session('parroquia') as $p)
                                        <option value="{{$p->id}}" {{ old('parroquia_id_modal') == $p->id ? 'selected' : '' }}>{{$p->descripcion}}</option>
                                    @endforeach
                                @endif
                            </select>
                            
                        </div>
                    </div>

                        <!-- Campo Parroquia -->
                    <div class="col-md-6">
                        <label for="calle_principal_modal" class="form-label">Calle Principal <span class="text-danger">*</span></label>
                        <div class="input-group">
                            <input type="text" class="form-control {{$errors->has('calle_principal_modal') ? 'is-invalid' : ''}}" 
                                id="calle_principal_modal" name="calle_principal_modal" 
                                placeholder="Ingrese la calle principal" value="{{old('calle_principal_modal')}}" required>
                            
                            
                        </div>
                    </div>

                    <div class="col-md-6">
                        <label for="calle_secundaria_modal" class="form-label">Calle Secundaria <span class="text-danger">*</span></label>
                        <div class="input-group">
                            <input type="text" class="form-control {{$errors->has('calle_secundaria_modal') ? 'is-invalid' : ''}}" 
                                id="calle_secundaria_modal" name="calle_secundaria_modal" 
                                placeholder="Ingrese la calle secundaria" value="{{old('calle_secundaria_modal')}}" required>
                            
                        </div>
                    </div>

                    <div class="col-md-6">
                        <label for="referencia_modal" class="form-label">Referencia Ubicacion <span class="text-danger">*</span></label>
                        <div class="input-group">
                            <input type="text" class="form-control {{$errors->has('referencia_modal') ? 'is-invalid' : ''}}" 
                                id="referencia_modal" name="referencia_modal" 
                                placeholder="Ingrese la referencia" value="{{old('referencia_modal')}}" required>
                            
                            
                        </div>
                    </div>

                    <div class="col-md-6">
                        <label for="descripcion_modal" class="form-label">Descripcion <span class="text-danger">*</span></label>
                        <div class="input-group">
                            <input type="text" class="form-control {{$errors->has('descripcion_modal') ? 'is-invalid' : ''}}" 
                                id="descripcion_modal" name="descripcion_modal" 
                                placeholder="Ingrese la descripcion" value="{{old('descripcion_modal')}}" required>
                            
                        </div>
                    </div>

                    <div class="col-md-6">
                        <label for="estado_modal" class="form-label">Estado <span class="text-danger">*</span></label>
                        <div class="input-group">
                        <select class="form-select {{$errors->has('estado_establecimiento_id_modal') ? 'is-invalid' : ''}}" id="estado_establecimiento_id_modal" name="estado_establecimiento_id_modal" required>
                            <option value="">Seleccione un estado</option>
                            <option value="1" {{ old('estado_establecimiento_id_modal') == '1' ? 'selected' : '' }}>Abierto</option>
                            <option value="2" {{ old('estado_establecimiento_id_modal') == '2' ? 'selected' : '' }}>Cerrado</option>
                        </select>
                            
                            
                        </div>
                    </div>

                    <div class="col-md-6">
                        <label for="estado_modal" class="form-label">Local <span class="text-danger">*</span></label>
                        <div class="input-group">
                        <select class="form-select {{$errors->has('estado_establecimiento_id_modal') ? 'is-invalid' : ''}}" id="tipo_local_modal" name="tipo_local_modal" required>
                            <option value="">Seleccione un tipo</option>
                            <option value="1" {{ old('tipo_local_modal') == '1' ? 'selected' : '' }}>Propio</option>
                            <option value="2" {{ old('tipo_local_modal') == '2' ? 'selected' : '' }}>Arrendado</option>
                        </select>
                            
                            
                        </div>
                    </div>

                    <div class="col-md-12" style="margin-top:12px">
                        <center>
                            <button type="button" onclick="guardaLocal()" class="btn btn-success btn-sm">Guardar</button>
                        </center>
                       
                    </div>

                </div>

               
                <div class="col-md-12">
                    <div class="table-responsive">
                        <table class="table table-bordered" id="tablaLocales" style="width: 100%">
                            <thead>
                                <tr>
                                    <th>Ubicacion</th>
                                    <th>Direccion</th>
                                    <th>Local</th>
                                    <th>Nombre Comercial</th>
                                </tr>
                            </thead>
                            <tbody>
                            </tbody>

                        </table>
                    </div>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cerrar</button>
            </div>
        </div>
    </div>
</div>
<!-- jQuery -->
<script src="{{ asset('js/jquery-3.5.1.js') }}"></script>
<script>
    // llenar_tabla_locales()
    globalThis.AccionForm="R";
    var provincia_id = document.getElementById('provincia_id_modal');
    provincia_id.addEventListener('change', function() {
        var optionSelectCanton = document.getElementById('optionSelectCanton');
        optionSelectCanton.innerHTML = 'Cargando...';
        var selectedOption = this.options[provincia_id.selectedIndex];
        cargarcantones(selectedOption.value);
        cargarparroquia(null);
    });
    function cargarcantones(idprovincia){
        var canton_id_modal = document.getElementById('canton_id_modal');

        axios.post('{{route('getcanton.catastro')}}', {
            _token: token,
            idprovincia:idprovincia
        }).then(function(res) {
            if(res.status==200) {
                console.log("cargando cantones");
                canton_id_modal.innerHTML = res.data;
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
    let token = "{{csrf_token()}}";
    var canton_id_modal = document.getElementById('canton_id_modal');
    canton_id_modal.addEventListener('change', function() {
        var optionSelectParroquia = document.getElementById('optionSelectParroquia');
        optionSelectParroquia.innerHTML = 'Cargando...';
        var selectedOption = this.options[canton_id_modal.selectedIndex];
        //console.log(selectedOption.value + ': ' + selectedOption.text);
        cargarparroquia(selectedOption.value);
    });
    function cargarparroquia(idcanton){
        var canton_id_modal = document.getElementById('canton_id_modal');
        

        axios.post('{{route('getparroquia.catastro')}}', {
            _token: token,
            idcanton:idcanton
        }).then(function(res) {
            if(res.status==200) {
                console.log("cargando parroquia");
                parroquia_id_modal.innerHTML = res.data;
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

    function limpiarCampos(){
        $('#provincia_id_modal').val('')
        $('#canton_id_modal').val('')
        $('#parroquia_id_modal').val('')
        $('#calle_principal_modal').val('')
        $('#calle_secundaria_modal').val('')
        $('#referencia_modal').val('')
        $('#descripcion_modal').val('')
        $('#estado_establecimiento_id_modal').val('')
        $('#tipo_local_modal').val('')
    }

    function guardaLocal(){
        var idcont=$('#idcont').val()
        var prov=$('#provincia_id_modal').val()
        var cant=$('#canton_id_modal').val()
        var parr=$('#parroquia_id_modal').val()
        var calle_princ=$('#calle_principal_modal').val()
        var calle_secund=$('#calle_secundaria_modal').val()
        var referencia=$('#referencia_modal').val()
        var descr=$('#descripcion_modal').val()
        var establ=$('#estado_establecimiento_id_modal').val()
        var tipo_local=$('#tipo_local_modal').val()

        if(prov==""){
            alertNotificar("Debe seleccionar la provincia","error")
            return
        }
        if(cant==""){
            alertNotificar("Debe seleccionar el canton","error")
            return
        }
        if(parr==""){
            alertNotificar("Debe seleccionar la parroquia","error")
            return
        }
        if(calle_princ==""){
            alertNotificar("Debe ingresar la calle principal","error")
            return
        }
        if(calle_secund==""){
            alertNotificar("Debe ingresar la calle secundaria","error")
            return
        }
        if(referencia==""){
            alertNotificar("Debe ingresar la referencia","error")
            return
        }
        if(descr==""){
            alertNotificar("Debe ingresar la descripcion","error")
            return
        }
        if(establ==""){
            alertNotificar("Debe seleccionar el estado del establecimiento","error")
            return
        }
        if(tipo_local==""){
            alertNotificar("Debe seleccionar el estado del local","error")
            return
        }
        
        
        vistacargando("m","Espere por favor")
        $.ajaxSetup({
            headers: {
                'X-CSRF-TOKEN': $('meta[name="csrf-token"]').attr('content')
            }
        });

        //comprobamos si es registro o edicion
        // let tipo=""
        let url_form=""
        if(AccionForm=="R"){
            tipo="POST"
            url_form="agregar-local"
        }else{
            tipo="PUT"
            url_form="catastrocontribuyente/actualizar-local/"+idMenuEditar
        }
    
        let FrmData = {
            idcont:idcont,
            prov: prov,
            cant:cant,
            parr: parr,
            calle_princ: calle_princ,
            calle_secund:calle_secund,
            referencia: referencia,
            descr: descr,
            establ:establ,
            tipo: tipo_local,
        };

        console.log(FrmData)

        $.ajax({
                
            type: tipo,
            url: url_form,
            method: tipo,  
            data: JSON.stringify(FrmData),      
            processData: false,  
            contentType: 'application/json',    
            
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
                $('#listado_menu').show(200)
                llenar_tabla_locales()
                                
            }, error:function (data) {
                console.log(data)

                vistacargando("");
                alertNotificar('Ocurrió un error','error');
            }
        });

    }

    function llenar_tabla_locales(idcontribuyente){
       
        
        var num_col = $("#tablaLocales thead tr th").length; //obtenemos el numero de columnas de la tabla
        $("#tablaLocales tbody").html(`<tr><td colspan="${num_col}" style="padding:40px; 0px; font-size:20px;"><center><span class="spinner-border" role="status" aria-hidden="true"></span><b> Obteniendo información</b></center></td></tr>`);
    
        
        $.get("listado-locales/"+idcontribuyente, function(data){
        
            if(data.error==true){
                alertNotificar(data.mensaje,"error");
                $("#tablaLocales tbody").html(`<tr><td colspan="${num_col}" style="padding:40px; 0px; font-size:20px;"><center>No se encontraron datos</center></td></tr>`);
                return;   
            }
            if(data.error==false){
                
                if(data.resultado.length <= 0){
                    $("#tablaLocales tbody").html(`<tr><td colspan="${num_col}" style="padding:40px; 0px; font-size:20px;"><center>No se encontraron datos</center></td></tr>`);
                    alertNotificar("No se encontró datos","error");
                    return;  
                }
            
                $('#tablaLocales').DataTable({
                    "destroy":true,
                    pageLength: 10,
                    autoWidth : true,
                    order: [[ 1, "desc" ]],
                    sInfoFiltered:false,
                    language: {
                        url: 'json/datatables/spanish.json',
                    },
                    columnDefs: [
                        { "width": "20%", "targets": 0 },
                        { "width": "35%", "targets": 1 },
                        { "width": "25%", "targets": 2 },
                        { "width": "20%", "targets": 3 },
                    
                    ],
                    data: data.resultado,
                    columns:[
                            {data: "id"},
                            {data: "provincia.descripcion" },
                            {data: "local_propio" },
                            {data: "actividad_descripcion"},
                    ],    
                    "rowCallback": function( row, data, index ) {
                        var local=""
                        if(data.local_propio==1){
                            local="Propio"
                        }else{
                            local="Propio"
                        }
                        // $('td', row).eq(0).html(index+1)
                        $('td', row).eq(0).html(`${data.provincia.descripcion}/${data.canton.nombre}/${data.parroquia.descripcion}
                                                `)
                        $('td', row).eq(1).html(`${data.calle_principal} - ${data.calle_secundaria} - ${data.referencia_ubicacion}</li>
                                               
                                                `)   
                        $('td', row).eq(2).html(local)                   
                        // $('td', row).eq(3).html(`
                                    
                        //                         <button type="button" class="btn btn-primary btn-xs" onclick="editarMenu(${data.id})">Editar</button>
                                                                                    
                        //                         <a onclick="eliminarMenu(${data.id})" class="btn btn-danger btn-xs"> Eliminar </a>
                                        
                                        
                        // `); 
                    }             
                });
            }
        }).fail(function(){
            $("#tablaLocales tbody").html(`<tr><td colspan="${num_col}" style="padding:40px; 0px; font-size:20px;"><center>No se encontraron datos</center></td></tr>`);
            alertNotificar("Se produjo un error, por favor intentelo más tarde","error");  
        });


    }

    // $('.collapse-link').click();
    // $('.datatable_wrapper').children('.row').css('overflow','inherit !important');

    // $('.table-responsive').css({'padding-top':'12px','padding-bottom':'12px', 'border':'0', 'overflow-x':'inherit'});

</script>