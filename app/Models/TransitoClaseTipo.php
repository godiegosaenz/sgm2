<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class TransitoClaseTipo extends Model
{
    use HasFactory;
    protected $connection = 'pgsql'; // Nombre de la conexión configurada
    protected $table = 'sgm_transito.clase_tipo_vehiculo';
}
