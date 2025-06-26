<?php

namespace App\Providers;

use Illuminate\Support\ServiceProvider;
use App\Models\ExoneracionAnterior;
use App\Models\PsqlCatastroContribuyente;
use App\Models\PsqlLiquidacion;
use App\Models\PsqlPaPatente;
use App\Models\RemisionInteres;
use App\Models\TransitoImpuesto;
use App\Models\User;
use App\Policies\CatastroContribuyentePolicy;
use App\Policies\ConfiguracionPolicy;
use App\Policies\ExoneracionAnteriorPolicy;
use App\Policies\PatentePolicy;
use App\Policies\RemisionInteresPolicy;
use App\Policies\TesoreriaPolicy;
use App\Policies\TransitoPolicy;
use Illuminate\Support\Facades\Gate;

class AppServiceProvider extends ServiceProvider
{
    /**
     * Register any application services.
     */
    public function register(): void
    {
        //
    }

    /**
     * Bootstrap any application services.
     */
    public function boot(): void
    {
        Gate::policy(ExoneracionAnterior::class, ExoneracionAnteriorPolicy::class);
        Gate::policy(RemisionInteres::class, RemisionInteresPolicy::class);
        Gate::policy(PsqlLiquidacion::class, TesoreriaPolicy::class);
        Gate::policy(PsqlCatastroContribuyente::class, CatastroContribuyentePolicy::class);
        Gate::policy(PsqlPaPatente::class, PatentePolicy::class);
        Gate::policy(TransitoImpuesto::class, TransitoPolicy::class);
        Gate::policy(User::class, ConfiguracionPolicy::class);
    }
}
