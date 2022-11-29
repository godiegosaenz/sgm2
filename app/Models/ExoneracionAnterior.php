<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class ExoneracionAnterior extends Model
{
    use HasFactory;

    protected $table = 'exoneracion_anteriors';
    protected $fillable = ['id',
                            'num_predio',
                            'num_resolucion',
                            'observacion',
                            'ruta_resolucion',
                            'usuario',
                            'created_at',
                            'updated_at'
                        ];
    public function exoneracion_detalle(){
        return $this->hasMany(ExoneracionDetalle::class,'exoneracion_id');
    }
}
