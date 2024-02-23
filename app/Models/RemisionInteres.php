<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class RemisionInteres extends Model
{
    use HasFactory;

    protected $table = 'remision_interes';
    protected $fillable = ['id',
                            'num_predio',
                            'num_resolucion',
                            'observacion',
                            'contribuyente',
                            'ruta_resolucion',
                            'estado',
                            'usuario',
                            'usuariosgm',
                            'valorInteres',
                            'valorRecargo',
                            'valorTotal',
                            'created_at',
                            'updated_at'
                        ];

    public function remision_liquidacion_detalle(){
        return $this->hasMany(RemisionLiquidacion::class,'remision_interes_id');
    }
}
