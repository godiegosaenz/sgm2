@extends('layouts.appv2')
@section('title', 'Lista de Predios')
@push('styles')
<link href="{{ asset('css/dataTables.bootstrap5.min.css') }}" rel="stylesheet">
<link href="{{ asset('css/rowReorder.bootstrap5.min.css') }}" rel="stylesheet">
@endpush
@section('content')
    <div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
        <h4 class="h2">Predios Rurales</h4>
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
                </button><br>
                <div class="col-3">
                <div class="mb-3">
                    <label for="num_predio">Poligono: </label>
                    <select class="form-select" aria-label="Default select example" id="poligono" name="poligono" onchange="consultarPredioR()">
                        <option value="001">001</option>
                        <option value='002'>002</option>
                        <option value="003">003</option>
                        <option value="004">004</option>
                        <option value="005">005</option>
                        <option value="006">006</option>
                        <option value="007">007</option>
                        <option value="008">008</option>
                        <option value="009">009</option>
                    </select>

                </div>
            </div>
            </center>
            <div class="table-responsive" style="margin-top:12px">
                @csrf
                <table class="table table-bordered" style="width:100%" id="tablaPredio">
                    <thead>
                        <tr>
                            <th scope="col">Clave catastral</th>
                            <th scope="col">Direcccion</th>
                                                        
                        </tr>
                    </thead>
                    <tbody id="tablaPoligono">
                        <tr>
                            <td colspan="2"><center>No hay datos disponibles</center></td>
                        </tr>
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
<script src="{{ asset('js/predios/poligono_rural.js?v=' . rand())}}"></script>
<script>
    consultarPredioR()
    $('#btnExcel').on('click', function () {
        tablaPredio.button('.buttons-excel').trigger();
    });

</script>
@endpush
