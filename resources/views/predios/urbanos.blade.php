@extends('layouts.appv2')
@section('title', 'Lista de Predios')
@push('styles')
<link href="{{ asset('css/dataTables.bootstrap5.min.css') }}" rel="stylesheet">
<link href="{{ asset('css/rowReorder.bootstrap5.min.css') }}" rel="stylesheet">
@endpush
@section('content')
    <div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
        <h4 class="h2">Predios Urbanos</h4>
        <div class="btn-toolbar mb-2 mb-md-0">
        <div class="btn-group me-2">
            
        </div>
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
    <!-- Mensaje general de errores -->
    @if($errors->any())
        <div class="alert alert-danger">
            "Por favor, revise los campos obligatorios y corrija los errores antes de continuar."
        </div>
    @endif
    <div class="row">
        <div class="col-md-12">
            <center>
                <button id="btnExcel" class="btn btn-success btn-sm">
                    <i class="bi bi-file-earmark-excel"></i> Exportar Excel
                </button>
            </center>
            <div class="table-responsive">
                @csrf
                <table class="table table-bordered" style="width:100%" id="tablaPredio">
                    <thead>
                        <tr>
                            <th scope="col">Clave catastral</th>
                            <th scope="col">Contribuyente</th>
                            <th scope="col">Tercera Edad</th>
                            <th scope="col">%</th>
                           
                            
                        </tr>
                    </thead>
                    <tbody>

                    </tbody>
                </table>
            </div>
        </div>
    </div>

   
    
@endsection
@push('scripts')
 <!-- jQuery -->
 <script src="{{ asset('js/jquery-3.5.1.js') }}"></script>
 <!-- DataTables -->

 <script src="{{ asset('js/jquery.dataTables.min.js') }}"></script>
 <script src="{{ asset('js/dataTables.bootstrap5.min.js') }}"></script>
 <script src="{{ asset('js/dataTables.rowReorder.min.js') }}"></script>

 <!-- Buttons -->
<!-- DataTables Buttons -->
<script src="https://cdn.datatables.net/buttons/2.4.2/js/dataTables.buttons.min.js"></script>

<!-- Excel -->
<script src="https://cdnjs.cloudflare.com/ajax/libs/jszip/3.10.1/jszip.min.js"></script>
<script src="https://cdn.datatables.net/buttons/2.4.2/js/buttons.html5.min.js"></script>

<script>
    $(document).ready(function(){
       
        tablaPredio = $("#tablaPredio").DataTable({
            lengthMenu: [
                [10, 20, -1],
                [10, 20, "Todos"]
            ],
            pageLength: 10,
            language: {
                url: '{{ asset("/js/spanish.json") }}',
            },
            autoWidth: false,
            processing: true,
            serverSide: true,
            order: [],
            ajax: {
                url: '{{ url("/predios/datatables") }}',
                type: "post",
                data: function (d) {
                    d._token = $("input[name=_token]").val();
                }
            },
            columns: [
                { data: 'clave', title: 'Clave catastral' },
                { data: 'contribuyente', title: 'Propietario(s)'},
                {data: 'tercera_edad', title: 'Exoneracion'},
                {data: 'porcentaje', title: '%',orderable: false, searchable: false }
            ],
            // dom: 'Blfrtip', // 👈 activa botones
            buttons: [
                {
                    extend: 'excelHtml5',
                    text: '📥 Descargar Excel',
                    title: 'Listado_Predios_Urbano',
                    exportOptions: {
                        columns: [0, 1,2,3] // columnas a exportar
                    }
                }
            ]
        });


    })

    $('#btnExcel').on('click', function () {
        tablaPredio.button('.buttons-excel').trigger();
    });

</script>
@endpush
