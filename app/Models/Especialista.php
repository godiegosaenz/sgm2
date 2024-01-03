<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class Especialista extends Model
{
    use HasFactory;

    protected $table = 'especialistas';
    protected $fillable = ['persona_id',
                            'correo',
                            'telefono',
                            'especialidades_id',
                            'titulo',
                            'activo',
                            'created_at',
                            'updated_at'
                        ];
    public function persona() {
        return $this->hasOne(Persona::class,'id', 'persona_id');
    }

    public function especialidad(){
        return $this->belongsTo(Especialidades::class,'especialidades_id');
    }
}
