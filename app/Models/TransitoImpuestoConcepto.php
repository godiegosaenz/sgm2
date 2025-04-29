<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class TransitoImpuestoConcepto extends Model
{
    use HasFactory;

    protected $connection = 'pgsql'; // Nombre de la conexión configurada
    protected $table = 'sgm_transito.concepto_impuesto';

    protected $fillable = [
        'impuesto_matriculacion_id',
        'concepto_id',
        'valor',
        'id',
    ];
}
