<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class PsqlActividadesCont extends Model
{
    use HasFactory;
    protected $connection = 'pgsql'; // Nombre de la conexión configurada
    protected $table = 'sgm_patente.pa_actividad_contribuyente';
    // protected $table = 'sgm_patente.pa_patente_actividad_contribu';

    protected $primaryKey  = 'id';
    public $timestamps = false;
}
