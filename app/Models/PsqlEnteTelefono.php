<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class PsqlEnteTelefono extends Model
{
    use HasFactory;

    protected $connection = 'pgsql'; // Nombre de la conexión configurada
    protected $table = 'sgm_app.ente_telefono';

    protected $fillable = ['id',
                            'telefono',
                            'ente',
                        ];

    public $timestamps = false;

    public function ente()
    {
        return $this->belongsTo(PsqlEnte::class,'id','ente');
    }
}
