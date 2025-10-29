<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Casts\Attribute;

class TransitoVehiculo extends Model
{
    use HasFactory;
    protected $connection = 'pgsql'; // Nombre de la conexión configurada
    protected $table = 'sgm_transito.vehiculo';

    protected $fillable = [
        'marca_id',
        'tipo_clase_id',
        'year',
        'chasis',
        'avaluo',
        'placa'
    ];

    protected $appends = ['Tipo','Marcav'];

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

    public function clase_vehiculo()
    {
        return $this->belongsTo(ClaseVehiculo::class,'clase_id','id');
    }

    public function getTipoAttribute()
    {
        return $this->tipo_vehiculo?->descripcion ?? null;
    }

    public function getMarcavAttribute()
    {
        return $this->marca?->descripcion ?? null;
    }
}
