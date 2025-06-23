<?php

namespace App\Policies;

use App\Models\User;

class RemisionInteresPolicy
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
        return  $user->hasPermissionTo('Remision de interes');
    }

    public function index(User $user): bool
    {
        return  $user->hasPermissionTo('Lista de remisiones');
    }
}
