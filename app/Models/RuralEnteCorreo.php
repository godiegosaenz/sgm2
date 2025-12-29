<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class RuralEnteCorreo extends Model
{
    use HasFactory;

    protected $connection = 'sqlsrv'; // Nombre de la conexión configurada
    protected $table = 'CORREO_CONTRIBUYENTE';

    protected $fillable = ['id',
                            'telefono',
                            'correo',
                        ];

    public $timestamps = false;

   
}
