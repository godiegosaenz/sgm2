<?php

namespace App\Policies;

use App\Models\User;

class TransitoPolicy
{
    /**
     * Create a new policy instance.
     */
    public function __construct()
    {
        //
    }

    public function create(User $user): bool
    {
        return  $user->hasPermissionTo('Impuestos transito');
    }

    public function index(User $user): bool
    {
        return  $user->hasPermissionTo('Lista de impuestos transito');
    }

    public function reporte_transito(User $user): bool
    {
        return  $user->hasPermissionTo('Reporte transito');
    }
}
