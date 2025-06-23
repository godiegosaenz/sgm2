<?php

namespace App\Policies;

use App\Models\User;

class PatentePolicy
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
        return  $user->hasPermissionTo('Declarar patentes');
    }

    public function index(User $user): bool
    {
        return  $user->hasPermissionTo('Lista de patentes');
    }
}
