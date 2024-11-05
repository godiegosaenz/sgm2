@extends('layouts.appv2')
@section('title', 'Panel de inicio')
@section('content')
    <div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
        <h4 class="h2">Panel principal</h4>
        <div class="btn-toolbar mb-2 mb-md-0">
        <div class="btn-group me-2">
            <button type="button" class="btn btn-sm btn-outline-secondary">Share</button>
            <button type="button" class="btn btn-sm btn-outline-secondary">Export</button>
        </div>
        <button type="button" class="btn btn-sm btn-outline-secondary dropdown-toggle d-flex align-items-center gap-1">
            <svg class="bi"><use xlink:href="#calendar3"/></svg>
            This week
        </button>
        </div>
    </div>
    <h5 class="mb-4">Resumen de contribuyentes</h5>

    <div class="row">
        <!-- Tarjetas de información con bordes secundarios -->
        <div class="col-md-3 mb-3">
            <div class="card border border-secondary shadow-sm">
                <div class="card-body">
                    <h5 class="card-title">Número de Clientes</h5>
                    <p class="card-text fs-4">{{$totalContribuyentes}}</p>
                </div>
            </div>
        </div>
        <div class="col-md-3 mb-3">
            <div class="card border border-secondary shadow-sm">
                <div class="card-body">
                    <h5 class="card-title">Cedulas erroneas</h5>
                    <p class="card-text fs-4">{{$contribuyentesConCedula}}</p>
                </div>
            </div>
        </div>
        <div class="col-md-3 mb-3">
            <div class="card border border-secondary shadow-sm">
                <div class="card-body">
                    <h5 class="card-title">Clientes con Correo</h5>
                    <p class="card-text fs-4">{{$contribuyentesConCorreo}}</p>
                </div>
            </div>
        </div>
        <div class="col-md-3 mb-3">
            <div class="card border border-secondary shadow-sm">
                <div class="card-body">
                    <h5 class="card-title">Clientes con Teléfonos</h5>
                    <p class="card-text fs-4">{{$contribuyentesConTelefono}}</p>
                </div>
            </div>
        </div>
    </div>

    <h5 class="mb-4 mt-5">Grafico de contribuyentes</h5>
    <div class="row mt-5">
        <div class="col-md-6">
            <canvas id="myChart" width="400" height="200"></canvas>
        </div>
        <div class="col-md-6">
            <canvas id="contribuyentesChart" width="400" height="200"></canvas>
        </div>
        <div class="col-md-4 mt-5">
            <canvas id="tipocontribuyente" width="400" height="400"></canvas>
        </div>
        <div class="col-md-8 mt-5">
            <canvas id="clientesChart" width="400" height="200"></canvas>
        </div>
    </div>

     <!-- Sección de notificaciones -->
     <h4 class="mt-5">Notificaciones</h2>
        <ul class="list-group">
            <li class="list-group-item">Notificación 1: Actualización pendiente en los datos de clientes.</li>
            <li class="list-group-item">Notificación 2: Nuevos clientes registrados hoy.</li>
            <li class="list-group-item">Notificación 3: Revise los clientes sin información de contacto.</li>
        </ul>

@endsection
<script>
    document.addEventListener("DOMContentLoaded", function() {
        fetch("{{ url('/dashboard/data') }}")
            .then(response => response.json())
            .then(data => {
                const ctx = document.getElementById('myChart').getContext('2d');
                const myChart = new Chart(ctx, {
                    type: 'bar',
                    data: {
                        labels: ['Con Cédula', 'Sin Cédula', 'Con Correo', 'Sin Correo', 'Con Teléfono', 'Sin Teléfono'],
                        datasets: [{
                            label: '# de Contribuyentes',
                            data: [
                                data.conCedula,
                                data.sinCedula,
                                data.conCorreo,
                                data.sinCorreo,
                                data.conTelefono,
                                data.sinTelefono
                            ],
                            backgroundColor: [
                                'rgba(75, 192, 192, 0.2)',
                                'rgba(255, 99, 132, 0.2)',
                                'rgba(54, 162, 235, 0.2)',
                                'rgba(255, 206, 86, 0.2)',
                                'rgba(153, 102, 255, 0.2)',
                                'rgba(201, 203, 207, 0.2)'
                            ],
                            borderColor: [
                                'rgba(75, 192, 192, 1)',
                                'rgba(255, 99, 132, 1)',
                                'rgba(54, 162, 235, 1)',
                                'rgba(255, 206, 86, 1)',
                                'rgba(153, 102, 255, 1)',
                                'rgba(201, 203, 207, 1)'
                            ],
                            borderWidth: 1
                        }]
                    },
                    options: {
                        scales: {
                            y: {
                                beginAtZero: true
                            }
                        }
                    }
                });
            });
    });
    document.addEventListener("DOMContentLoaded", function() {
    fetch("{{ url('/dashboard/contribuyentes') }}")
        .then(response => response.json())
        .then(data => {
            const months = data.new_contribuyentes.map(item => item.month);
            const newTotals = data.new_contribuyentes.map(item => item.total);
            const updatedTotals = data.updated_contribuyentes.map(item => item.total);

            const ctx = document.getElementById('contribuyentesChart').getContext('2d');
            const contribuyentesChart = new Chart(ctx, {
                type: 'line',
                data: {
                    labels: months,
                    datasets: [
                        {
                            label: 'Nuevos Contribuyentes',
                            data: newTotals,
                            backgroundColor: 'rgba(75, 192, 192, 0.2)',
                            borderColor: 'rgba(75, 192, 192, 1)',
                            borderWidth: 2,
                            fill: true
                        },
                        {
                            label: 'Contribuyentes Actualizados',
                            data: updatedTotals,
                            backgroundColor: 'rgba(255, 99, 132, 0.2)',
                            borderColor: 'rgba(255, 99, 132, 1)',
                            borderWidth: 2,
                            fill: true
                        }
                    ]
                },
                options: {
                    responsive: true,
                    scales: {
                        x: {
                            title: {
                                display: true,
                                text: 'Mes/Año'
                            }
                        },
                        y: {
                            title: {
                                display: true,
                                text: 'Cantidad de Contribuyentes'
                            },
                            beginAtZero: true
                        }
                    }
                }
            });
        });
    });
    document.addEventListener("DOMContentLoaded", function() {
        fetch("{{ url('/dashboard/datadistribuibles') }}")
            .then(response => response.json())
            .then(data => {
                const ctx = document.getElementById('tipocontribuyente').getContext('2d');
                const myChart = new Chart(ctx, {
                    type: 'pie', // Cambia a tipo 'pie' para gráfico de pastel
                    data: {
                        labels: ['Persona Natural', 'Persona Jurídica'],
                        datasets: [{
                            label: 'Distribución de Contribuyentes',
                            data: [
                                data.natural, // Total de personas naturales
                                data.juridica // Total de personas jurídicas
                            ],
                            backgroundColor: [
                                'rgba(75, 192, 192, 0.2)',
                                'rgba(255, 99, 132, 0.2)'
                            ],
                            borderColor: [
                                'rgba(75, 192, 192, 1)',
                                'rgba(255, 99, 132, 1)'
                            ],
                            borderWidth: 1
                        }]
                    },
                    options: {
                        responsive: true,
                        plugins: {
                            legend: {
                                display: true,
                                position: 'top',
                            },
                            tooltip: {
                                callbacks: {
                                    label: function(tooltipItem) {
                                        return `${tooltipItem.label}: ${tooltipItem.raw}`;
                                    }
                                }
                            }
                        }
                    }
                });
            });
    });
    document.addEventListener("DOMContentLoaded", function() {
        fetch("{{ url('/dashboard/discapacidad') }}")
            .then(response => response.json())
            .then(data => {
                const ctx = document.getElementById('clientesChart').getContext('2d');
                const clientesChart = new Chart(ctx, {
                    type: 'bar',
                    data: {
                        labels: ['Tercera Edad', 'Con Discapacidad', 'Llevan Contabilidad', 'No Llevan Contabilidad'],
                        datasets: [{
                            label: '# de Contribuyentes',
                            data: [
                                data.terceraEdad,
                                data.conDiscapacidad,
                                data.llevanContabilidad,
                                data.noLlevanContabilidad
                            ],
                            backgroundColor: [
                                'rgba(75, 192, 192, 0.2)',
                                'rgba(255, 99, 132, 0.2)',
                                'rgba(54, 162, 235, 0.2)',
                                'rgba(255, 206, 86, 0.2)'
                            ],
                            borderColor: [
                                'rgba(75, 192, 192, 1)',
                                'rgba(255, 99, 132, 1)',
                                'rgba(54, 162, 235, 1)',
                                'rgba(255, 206, 86, 1)'
                            ],
                            borderWidth: 1
                        }]
                    },
                    options: {
                        scales: {
                            y: {
                                beginAtZero: true
                            }
                        }
                    }
                });
        });
    });
</script>
