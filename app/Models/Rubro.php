<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class Rubro extends Model
{
    use HasFactory;

    protected $table = 'rubros';
    protected $fillable = ['id',
                            'name',
                            'status',
                            'value',
                            'accounting_account',
                            'created_at',
                            'updated_at'
                        ];
    public function liquidacion_rubros(){
        return $this->belongsToMany(Liquidation::class,'liquidation_rubros','rubro_id','liquidation_id')->withPivot('id','value','status')->withTimestamps();
    }
}
