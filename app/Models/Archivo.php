<?php

namespace App\models;

use Illuminate\Database\Eloquent\Model;

class Archivo extends Model
{
    protected $table = 'archivos_adjuntos';
    
    protected $fillable = [
        'nombreArchivo', 'rutaArchivo'
    ];
}
