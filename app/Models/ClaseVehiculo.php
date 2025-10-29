<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class ClaseVehiculo extends Model
{
    use HasFactory;
    protected $connection = 'pgsql'; // Nombre de la conexión configurada
    protected $table = 'sgm_transito.clase_vehiculo';
}
