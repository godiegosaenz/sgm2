<?php

namespace App\models;

use Illuminate\Database\Eloquent\Model;

class Provincia extends Model
{
    protected $table = 'provincias';
    protected $fillable = ['id',
                            'nombre',
                            'created_at',
                            'updated_at'
                        ];
}
