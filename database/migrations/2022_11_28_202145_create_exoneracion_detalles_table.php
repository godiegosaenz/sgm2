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
        Schema::create('exoneracion_detalles', function (Blueprint $table) {
            $table->increments('id');
            $table->integer('liquidacion_id');
            $table->string('cod_liquidacion');
            $table->decimal('valor');
            $table->decimal('valor_anterior');
            $table->decimal('impuesto_predial_anterior');
            $table->decimal('impuesto_predial_actual');
            $table->json('det_liquidacion')->nullable();
            $table->unsignedInteger('exoneracion_id');
            $table->foreign('exoneracion_id')->references('id')->on('exoneracion_anteriors');
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
        Schema::dropIfExists('exoneracion_detalles');
    }
};
