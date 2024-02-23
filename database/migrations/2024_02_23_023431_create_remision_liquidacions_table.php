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
        Schema::create('remision_liquidacions', function (Blueprint $table) {
            $table->increments('id');
            $table->integer('liquidacion_id');
            $table->string('cod_liquidacion');
            $table->decimal('emision');
            $table->decimal('valor_total_con_remision');
            $table->decimal('valor_total_sin_remision');
            $table->decimal('valor_remision');
            $table->decimal('interes');
            $table->decimal('recargo');
            $table->text('contribuyente')->nullable();
            $table->unsignedInteger('remision_interes_id');
            $table->foreign('remision_interes_id')->references('id')->on('remision_interes');
            $table->timestamps();
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::dropIfExists('remision_liquidacions');
    }
};
