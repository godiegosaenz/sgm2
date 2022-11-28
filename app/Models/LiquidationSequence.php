<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class LiquidationSequence extends Model
{
    use HasFactory;

    protected $table = 'liquidation_sequences';

    protected $fillable = ['id',
                            'sequence',
                            'year',
                            'type_liquidation_id',
                            'created_at',
                            'updated_at'
                        ];

}
