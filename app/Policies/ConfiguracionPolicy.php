<?php

namespace App\Policies;

use App\Models\User;

class ConfiguracionPolicy
{
    /**
     * Create a new policy instance.
     */
    public function __construct()
    {
        //
    }

    public function lista_usuario(User $user): bool
    {
        return  $user->hasPermissionTo('Lista de usuarios');
    }

    public function crear_usuario(User $user): bool
    {
        return  $user->hasPermissionTo('Crear usuario');
    }

    public function lista_empleados(User $user): bool
    {
        return  $user->hasPermissionTo('Lista de empleados');
    }

    public function crear_empleados(User $user): bool
    {
        return  $user->hasPermissionTo('Ingreso de empleados');
    }

    public function crear_roles(User $user): bool
    {
        return  $user->hasPermissionTo('Crear Roles');
    }

}
