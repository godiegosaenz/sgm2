<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class TransitoConcepto extends Model
{
    use HasFactory;

    protected $connection = 'pgsql'; // Nombre de la conexión configurada
    protected $table = 'sgm_transito.conceptos';

    protected $fillable = [
        'concepto',
        'valor',
        'id',
    ];

    public function liquidacion_rubros(){
        //return $this->belongsToMany(TransitoImpuesto::class,'matriculacion_conceptos','concepto_id','impuesto_matriculacion_id')->withPivot('id','valor')->withTimestamps();
        return $this->belongsToMany(TransitoImpuesto::class,'sgm_transito.concepto_impuesto','concepto_id','impuesto_matriculacion_id')->withPivot('id','valor')->withTimestamps();
    }
}
