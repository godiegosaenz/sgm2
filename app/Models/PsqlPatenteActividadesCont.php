<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class PsqlPatenteActividadesCont extends Model
{
    use HasFactory;
    protected $connection = 'pgsql'; // Nombre de la conexión configurada
    protected $table = 'sgm_patente.pa_patente_actividad_contr';

    protected $primaryKey  = 'id';
    public $timestamps = false;
}
