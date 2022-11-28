<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class Cita extends Model
{
    use HasFactory;

    protected $table = 'citas';
    protected $fillable = ['id',
                            'persona_id',
                            'especialista_id',
                            'especialidades_id',
                            'fecha',
                            'hora',
                            'estado',
                            'motivo',
                            'created_at',
                            'updated_at'
                        ];

    public function persona(){
        return $this->belongsTo(Persona::class,'persona_id');
    }

    public function especialista(){
        return $this->belongsTo(Especialista::class,'especialista_id','persona_id');
    }

    public function consulta(){
        return $this->hasOne(Consulta::class,'cita_id');
    }
}
