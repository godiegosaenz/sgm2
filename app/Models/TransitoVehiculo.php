<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class TransitoVehiculo extends Model
{
    use HasFactory;
    protected $connection = 'pgsql'; // Nombre de la conexión configurada
    protected $table = 'sgm_transito.vehiculo';

    public function impuesto_transito()
    {
        return $this->belongsTo(TransitoImpuesto::class,'id','vehiculo_id');
    }

    public function marca()
    {
        return $this->belongsTo(TransitoMarca::class,'marca_id','id');
    }

    public function tipo_vehiculo()
    {
        return $this->belongsTo(TransitoTipoVehiculo::class,'tipo_clase_id','id');
    }
}
