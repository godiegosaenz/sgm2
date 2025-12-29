<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class RuralEnteTelefono extends Model
{
    use HasFactory;

    protected $connection = 'sqlsrv'; // Nombre de la conexión configurada
    protected $table = 'TELEFONO_CONTRIBUYENTE';

    protected $fillable = ['id',
                            'telefono',
                            'cedula_ruc',
                        ];

    public $timestamps = false;

   
}
