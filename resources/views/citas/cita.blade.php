@extends('layouts.app')
@section('title', 'Citas')
@push('styles')
@endpush
@section('content')
    <div class="container-fluid">
        <div class="row">
            <div class="col-12">
                <div class="col-md-12">
                    <h2 class="text-center">Información de Citas </h2>
                </div>
            </div>
        </div>
        <div class="row">
            <div class="col-12">
                <div class="card">
                    <div class="card-body">
                        <div class="row">
                            <label for="">Citas del dia</label>
                        </div>
                        <div class="row">
                            <div class="col-4">
                                <div class="mb-3">
                                    <input class="form-control" type="date" id="fecha" name="fecha" value="<?php echo date("Y-m-d");?>">
                                </div>
                            </div>
                            <div class="col-4">
                                <button id="btnActualizar" class="btn btn-primary">Actualizar</button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <br>
        <div class="row">
            <div class="col-md-12">
                @csrf
                <div class="table-responsive">
                    <table class="table table-bordered" id="tableCita" style="width: 100%">
                        <thead>
                            <tr>
                            <th scope="col">Acciones</th>
                            <th>Paciente</th>
                            <th scope="col">Especialista</th>
                            <th scope="col">Estado</th>
                            <th scope="col">Fecha</th>
                            <th scope="col">Motivo</th>
                            </tr>
                        </thead>
                        <tbody>

                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
    <div class="toast-container position-fixed top-0 end-0">
        <div class="toast" role="alert" aria-live="assertive" aria-atomic="true" id="ToastCancelCita">
            <div class="toast-header bg-danger text-light pt-3 pb-3">
                <h5 class="my-0">Estas seguro de cancelar la cita.</h5>
              </div>
            <div class="toast-body">
                <button id="buttonSI" type="button" class="btn btn-primary btn-sm"><i class="bi bi-check-circle-fill"></i> Si</button>
                <button type="button" class="btn btn-secondary btn-sm" data-bs-dismiss="toast"><i class="bi bi-x-circle-fill"></i> No</button>

            </div>
        </div>
    </div>
@endsection
@push('scripts')
 <!-- jQuery -->
 <script src="//code.jquery.com/jquery-3.5.1.js"></script>
 <!-- DataTables -->
 <script src="//cdn.datatables.net/1.12.1/js/jquery.dataTables.min.js"></script>
 <script src="//cdn.datatables.net/1.12.1/js/dataTables.bootstrap5.min.js"></script>
 <script src="//cdn.datatables.net/rowreorder/1.2.8/js/dataTables.rowReorder.min.js"></script>

<script>

    $(document).ready(function(){
        tableCita = $("#tableCita").DataTable({
            "lengthMenu": [ 5, 10],
            "language" : {
                "url": '{{ asset("/js/spanish.json") }}',
            },
            "autoWidth": true,
            "rowReorder": true,
            "order": [], //Initial no order
            "processing" : true,
            "serverSide": true,
            "ajax": {
                "url": '{{ url("/cita/listar") }}',
                "type": "post",
                "data": function (d){
                    d._token = $("input[name=_token]").val();
                    d.fecha =  $("input[name=fecha]").val();
                }
            },
            //"columnDefs": [{ targets: [3], "orderable": false}],
            "columns": [
                {width: '',data: 'action', name: 'action', orderable: false, searchable: false},
                {width: '',data: 'paciente'},
                {width: '',data: 'especialista'},
                {width: '',data: 'estado'},
                {width: '',data: 'fechahora'},
                {width: '',data: 'motivo'},

            ],
            "fixedColumns" : true
        });
    });
</script>
<script>

    function mostrarToasCancelarCita(id){
        new bootstrap.Toast(document.querySelector('#ToastCancelCita')).show();
        let buttonSI = document.getElementById('buttonSI');
        buttonSI.addEventListener('click', function(e) {
            cancelarCita(id);
        });
    }
    let token = "{{csrf_token()}}";
    function cancelarCita(id){
        axios.post('{{route('cancel.cita')}}',{
            id: id,
            _token: token
        }).then(function(res) {
            console.log(res);
            if(res.status==200) {
                if(res.data.respuesta == true){
                    tableCita.ajax.reload();
                    new bootstrap.Toast(document.querySelector('#ToastCancelCita')).hide();
                }else{

                }
            }
        }).catch(function(err) {
            console.log(err);
            /*if(err.response.status == 500){

                console.log('error al consultar al servidor');
            }

            if(err.response.status == 419){
                console.log('Es posible que tu session haya caducado, vuelve a iniciar sesion');
            }*/
        }).then(function() {

        });
    }
    let btnActualizar = document.getElementById('btnActualizar');
    btnActualizar.addEventListener('click',function(){
        tableCita.ajax.reload();
    })

</script>
@endpush
