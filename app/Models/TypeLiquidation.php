<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class TypeLiquidation extends Model
{
    use HasFactory;
    protected $table = 'type_liquidations';
    protected $fillable = ['id',
                            'name',
                            'status',
                            'pref',
                            'created_at',
                            'updated_at'
                        ];
}
