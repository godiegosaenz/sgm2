<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class Liquidation extends Model
{
    use HasFactory;

    protected $table = 'liquidations';
    protected $fillable = ['id',
                            'voucher_number',
                            'total_payment',
                            'username',
                            'observation',
                            'year',
                            'type_liquidation_id',
                            'cita_id',
                            'categoria_id',
                            'created_at',
                            'updated_at'
                        ];

    public function liquidation_rubros(){
        return $this->belongsToMany(Rubro::class,'liquidation_rubros','liquidation_id','rubro_id')->withPivot('id','value','status')->withTimestamps();
    }
}
