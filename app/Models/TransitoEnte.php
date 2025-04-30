<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class TransitoEnte extends Model
{
    use HasFactory;
    protected $connection = 'pgsql'; // Nombre de la conexión configurada
    protected $table = 'sgm_transito.cat_ente';

    protected $fillable = ['id',
                            'cc_ruc',
                            'nombres',
                            'apellidos',
                            'correo',
                            'telefono',
                            'fecha_nacimiento',
                            'direccion',
                        ];
}
