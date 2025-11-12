<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Relations\BelongsToMany;

class TransitoImpuesto extends Model
{
    use HasFactory;

    protected $connection = 'pgsql'; // Nombre de la conexión configurada
    protected $table = 'sgm_transito.impuestos';

    protected $fillable = [
        'year_impuesto',
        'cat_ente_id',
        'numero_titulo',
        'total_pagar',
        'vehiculo_id',
        'created_at',
        'updated_at',
        'id',
    ];

    public function conceptos(): BelongsToMany
    {
        return $this->belongsToMany(TransitoConcepto::class, 'sgm_transito.concepto_impuesto','impuesto_matriculacion_id','concepto_id')->withPivot('id','valor')->withTimestamps()->orderBy('orden','asc');
    }

    public function vehiculo()
    {
        return $this->belongsTo(TransitoVehiculo::class,'vehiculo_id','id')->with('clase_vehiculo');
    }

    public function cliente()
    {
        return $this->belongsTo(PsqlEnte::class,'cat_ente_id','id');
    }
}
