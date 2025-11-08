<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class PsqlPatenteActividadesCont extends Model
{
    use HasFactory;
    protected $connection = 'pgsql'; // Nombre de la conexión configurada
    protected $table = 'sgm_patente.pa_patente_actividad_contr';
    // protected $table = 'sgm_patente.pa_patente_actividad_contribu';

    protected $primaryKey  = 'id';
    public $timestamps = false;

    public function detalle_actividad(){
        return $this->belongsTo('App\Models\PsqlPaActividadesComerciales', 'id_actividad_cont', 'id');
    }
}
