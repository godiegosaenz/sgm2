<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class RemisionLiquidacion extends Model
{
    use HasFactory;
    protected $table = 'remision_liquidacions';
    protected $fillable = ['id',
                            'liquidacion_id',
                            'cod_liquidacion',
                            'valor_total',
                            'valor_remision',
                            'valor_total_con_remision',
                            'valor_total_sin_remision',
                            'interes',
                            'recargo',
                            'contribuyente',
                            'remision_interes_id',
                            'created_at',
                            'updated_at'
                        ];
}
