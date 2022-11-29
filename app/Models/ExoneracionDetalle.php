<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class ExoneracionDetalle extends Model
{
    use HasFactory;
    protected $table = 'exoneracion_detalles';
    protected $fillable = ['id',
                            'liquidacion_id',
                            'cod_liquidacion',
                            'valor',
                            'valor_anterior',
                            'exoneracion_id',
                            'created_at',
                            'updated_at'
                        ];
    public function exoneracion_anterior(){
        return $this->belongsTo(ExoneracionAnterior::class,'exoneracion_id');
    }
}
