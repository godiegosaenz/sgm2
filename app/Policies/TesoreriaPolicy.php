<?php

namespace App\Policies;

use App\Models\User;
use App\Models\PsqlLiquidacion;

class TesoreriaPolicy
{
    /**
     * Create a new policy instance.
     */
    public function __construct()
    {

    }

    // public function impresion_titulos_urb(User $user): bool
    // {
    //     return  $user->hasPermissionTo('Impresion de titulos urbanos');
    // }

    // public function impresion_titulos_rur(User $user): bool
    // {
    //     return  $user->hasPermissionTo('Impresion de titulos rurales');
    // }

    // public function reporte_liquidaciones(User $user): bool
    // {
    //     return  $user->hasPermissionTo('Reporte de liquidacion');
    // }
}
