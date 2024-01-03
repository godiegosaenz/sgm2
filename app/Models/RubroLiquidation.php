<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class RubroLiquidation extends Model
{
    use HasFactory;
    protected $table = 'liquidation_rubros';
    protected $fillable = ['id',
                            'rubro_id',
                            'liquidation_id',
                            'value',
                            'status',
                            'created_at',
                            'updated_at'
                        ];

}
