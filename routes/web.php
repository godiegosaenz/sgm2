<?php

use Illuminate\Support\Facades\Route;
use App\Http\Controllers\TesoreriaController;
use App\Http\Controllers\paciente\ListarPacienteController;
use App\Http\Controllers\paciente\PacienteController;
use App\Http\Controllers\paciente\DetallarPacienteController;
use App\Http\Controllers\configuracion\CantonController;
use App\Http\Controllers\configuracion\CategoriaController;
use App\Http\Controllers\citas\CitasController;
use App\Http\Controllers\consultas\ConsultaController;
use App\Http\Controllers\especialista\EspecialistaController;
use App\Http\Controllers\reportes\CitaReporteController;
use App\Http\Controllers\liquidaciones\PagoController;
use GuzzleHttp\Psr7\Request;

/*
|--------------------------------------------------------------------------
| Web Routes
|--------------------------------------------------------------------------
|
| Here is where you can register web routes for your application. These
| routes are loaded by the RouteServiceProvider within a group which
| contains the "web" middleware group. Now create something great!
|
*/

Route::get('/', function () {
    return view('auth.login');
})->middleware('guest');

Route::middleware(['auth', 'verified'])->group(function () {
    Route::view('home', 'home')->name('home');
});



Route::get('paciente/mostrar', [ListarPacienteController::class, 'index'])->name('mostrar.persona');
Route::post('paciente/listar', [ListarPacienteController::class, 'listar'])->name('listar.persona');
Route::get('paciente/ingresar', [PacienteController::class, 'index'])->name('ingresar.persona');
Route::post('paciente/guardar', [PacienteController::class, 'store'])->name('guardar.persona');
Route::patch('paciente/actualizar/{id}', [PacienteController::class, 'update'])->name('actualizar.paciente');
Route::post('paciente/foto', [DetallarPacienteController::class, 'guardarFoto'])->name('foto.persona');
Route::post('paciente/archivo', [DetallarPacienteController::class, 'guardarArchivo'])->name('guardar.archivo');
Route::get('paciente/detallar/{id}',[DetallarPacienteController::class, 'index'])->name('detallar.persona');
Route::get('paciente/editar/{id}',[PacienteController::class, 'edit'])->name('editar.paciente');
Route::post('paciente/verificar', [PacienteController::class, 'verificarCedula'])->name('verificar.persona');
Route::post('canton/obtener', [CantonController::class, 'obtener'])->name('canton.obtener');
Route::get('usuario/listar', [ListarUsuarioController::class, 'index'])->name('listar.usuario');
Route::get('usuario/crear', [CrearUsuarioController::class, 'index'])->name('crear.usuario');
Route::post('usuario/guardar',[CrearUsuarioController::class, 'guardar'])->name('guardar.usuario');
Route::post('usuario/verificar',[CrearUsuarioController::class, 'verificarUsuario'])->name('verificar.usuario');
Route::get('usuario/detallar/{idusuario}/persona/{idpersona}', [DetallarUsuarioController::class, 'index'])->name('detallar.usuario');

Route::get('tesoreria/terceraedad', [TesoreriaController::class, 'index'])->name('index.tesoreria');
Route::post('tesoreria/datatable', [TesoreriaController::class, 'index'])->name('datatable.tesoreria');
Route::post('tesoreria/consulta', [TesoreriaController::class, 'consulta'])->name('consulta.tesoreria');
Route::post('tesoreria/exonerar', [TesoreriaController::class, 'store'])->name('store.tesoreria');

