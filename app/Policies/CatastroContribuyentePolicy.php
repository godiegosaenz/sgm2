<?php

namespace App\Policies;

use App\Models\User;

class CatastroContribuyentePolicy
{
    /**
     * Create a new policy instance.
     */
    public function __construct()
    {
        //
    }

    public function index(User $user): bool
    {
        return  $user->hasPermissionTo('Catastro contribuyentes');
    }
}
