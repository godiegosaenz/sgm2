<?php

namespace App\models;

use Illuminate\Database\Eloquent\Model;

class Canton extends Model
{
    protected $table = 'cantones';
    protected $fillable = ['id',
                            'nombre',
                            'id_provincia',
                            'created_at',
                            'updated_at'
                        ];
}
