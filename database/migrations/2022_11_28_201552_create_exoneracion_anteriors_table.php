<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    /**
     * Run the migrations.
     *
     * @return void
     */
    public function up()
    {
        Schema::create('exoneracion_anteriors', function (Blueprint $table) {
            $table->increments('id');
            $table->integer('num_predio');
            $table->string('num_resolucion');
            $table->text('observacion')->nullable();
            $table->text('ruta_resolucion');
            $table->string('usuario');
            $table->timestamps();
        });
    }

    /**
     * Reverse the migrations.
     *
     * @return void
     */
    public function down()
    {
        Schema::dropIfExists('exoneracion_anteriors');
    }
};
