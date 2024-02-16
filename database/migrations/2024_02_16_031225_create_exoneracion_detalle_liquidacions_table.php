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
        Schema::create('exoneracion_detalle_liquidacions', function (Blueprint $table) {
            $table->increments('id');
            $table->integer('rubro');
            $table->string('descripcion');
            $table->decimal('valor');
            $table->unsignedInteger('exoneracion_detalles_id');
            $table->foreign('exoneracion_detalles_id')->references('id')->on('exoneracion_detalles');
            $table->timestamps();
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::dropIfExists('exoneracion_detalle_liquidacions');
    }
};
