<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    /**
     * Run the migrations.
     */
    public function up(): void
    {
        Schema::create('remision_interes', function (Blueprint $table) {
            $table->increments('id');
            $table->integer('num_predio');
            $table->string('num_resolucion');
            $table->text('observacion')->nullable();
            $table->text('contribuyente')->nullable();
            $table->text('ruta_resolucion');
            $table->enum('estado', ['creado', 'aplicado','cancelado'])->default('creado');
            $table->string('usuario');
            $table->string('usuariosgm');
            $table->decimal('valorInteres');
            $table->decimal('valorRecargo');
            $table->decimal('valorTotal');
            $table->timestamps();
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::dropIfExists('remision_interes');
    }
};
