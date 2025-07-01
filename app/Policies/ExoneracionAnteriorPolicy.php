<?php

namespace App\Policies;

use App\Models\User;
use App\Models\ExoneracionAnterior;
use Illuminate\Auth\Access\HandlesAuthorization;
use Illuminate\Auth\Access\Response;

class ExoneracionAnteriorPolicy
{
    use HandlesAuthorization;
    /**
     * Create a new policy instance.
     */
    public function __construct()
    {
        //
    }

    // public function create(User $user): bool
    // {
    //     return  $user->hasPermissionTo('Exoneracion tercera edad');
    // }

    // public function index(User $user): bool
    // {
    //     return  $user->hasPermissionTo('Lista de exoneraciones');
    // }

}
