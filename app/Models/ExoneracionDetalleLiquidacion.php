<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class ExoneracionDetalleLiquidacion extends Model
{
    use HasFactory;
    protected $table = 'exoneracion_detalle_liquidacions';
    protected $fillable = ['id',
                            'rubro',
                            'descripcion',
                            'valor',
                            'exoneracion_detalles_id',
                            'created_at',
                            'updated_at'
                        ];

}
