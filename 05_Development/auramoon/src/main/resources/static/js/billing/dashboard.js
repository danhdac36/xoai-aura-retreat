document.addEventListener('DOMContentLoaded', () => {

    // 1. Initialize Pie Chart
    const pieCtx = document.getElementById('pieChart');
    if (pieCtx && typeof chartData !== 'undefined') {
        new Chart(pieCtx, {
            type: 'pie',
            data: {
                labels: ['Gói Retreat', 'Spa & Trị liệu', 'Ẩm thực (F&B)'],
                datasets: [{
                    data: [chartData.packageRev, chartData.spaRev, chartData.fbRev],
                    backgroundColor: [
                        '#1F513A', // Dark Green
                        '#52B788', // Bright Green
                        '#D4E157'  // Yellow-Green
                    ],
                    borderWidth: 1,
                    borderColor: '#ffffff',
                    hoverOffset: 4
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        display: true,
                        position: 'bottom',
                        labels: {
                            usePointStyle: true,
                            padding: 20,
                            font: { size: 11 }
                        }
                    },
                    tooltip: {
                        callbacks: {
                            label: function (context) {
                                let label = context.label || '';
                                if (label) {
                                    label += ': ';
                                }
                                if (context.parsed !== null) {
                                    label += new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(context.parsed);
                                }
                                return label;
                            }
                        }
                    }
                }
            }
        });
    }

    // 2. Initialize Daily Density Multi-line Chart
    const lineCtx = document.getElementById('dailyLineChart');
    if (lineCtx && typeof chartData !== 'undefined' && chartData.dailyTrend) {

        const labels = chartData.dailyTrend.map(d => d.dateLabel);
        const packageData = chartData.dailyTrend.map(d => d.packageRev);
        const spaData = chartData.dailyTrend.map(d => d.spaRev);
        const fbData = chartData.dailyTrend.map(d => d.fbRev);

        new Chart(lineCtx, {
            type: 'line',
            data: {
                labels: labels,
                datasets: [
                    {
                        label: 'Gói Retreat',
                        data: packageData,
                        borderColor: '#1F513A', // Dark Green
                        backgroundColor: 'transparent',
                        borderWidth: 2,
                        tension: 0.4,
                        fill: false,
                        pointRadius: 0,
                        pointHoverRadius: 4
                    },
                    {
                        label: 'Spa & Trị liệu',
                        data: spaData,
                        borderColor: '#52B788', // Bright Green
                        backgroundColor: 'transparent',
                        borderWidth: 2,
                        tension: 0.4,
                        fill: false,
                        pointRadius: 0,
                        pointHoverRadius: 4
                    },
                    {
                        label: 'Ẩm thực',
                        data: fbData,
                        borderColor: '#D4E157', // Yellow-Green
                        backgroundColor: 'transparent',
                        borderWidth: 2,
                        tension: 0.4,
                        fill: false,
                        pointRadius: 0,
                        pointHoverRadius: 4
                    }
                ]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                interaction: {
                    mode: 'index',
                    intersect: false,
                },
                plugins: {
                    legend: {
                        display: false // Using custom HTML legend
                    },
                    tooltip: {
                        callbacks: {
                            label: function (context) {
                                let label = context.dataset.label || '';
                                if (label) {
                                    label += ': ';
                                }
                                if (context.parsed.y !== null) {
                                    label += new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(context.parsed.y);
                                }
                                return label;
                            }
                        }
                    }
                },
                scales: {
                    x: {
                        grid: {
                            display: false
                        },
                        ticks: {
                            maxTicksLimit: 15,
                            font: { size: 10 }
                        }
                    },
                    y: {
                        beginAtZero: true,
                        grid: {
                            color: '#F3F4F6'
                        },
                        ticks: {
                            font: { size: 10 },
                            callback: function (value) {
                                if (value >= 1000000) {
                                    return (value / 1000000) + 'M';
                                }
                                return value;
                            }
                        }
                    }
                }
            }
        });

        // 3. Mini sparkline charts
        const commonMiniOptions = {
            responsive: true,
            maintainAspectRatio: false,
            plugins: { legend: { display: false }, tooltip: { enabled: false } },
            scales: { x: { display: false }, y: { display: false } },
            elements: { point: { radius: 0 } },
            layout: { padding: 0 }
        };

        const m1 = document.getElementById('miniChart1');
        if (m1) new Chart(m1, { type: 'line', data: { labels: labels, datasets: [{ data: packageData, borderColor: '#1F513A', backgroundColor: 'rgba(31, 81, 58, 0.1)', fill: true, tension: 0.4, borderWidth: 1.5 }] }, options: commonMiniOptions });

        const m2 = document.getElementById('miniChart2');
        if (m2) new Chart(m2, { type: 'line', data: { labels: labels, datasets: [{ data: spaData, borderColor: '#52B788', backgroundColor: 'rgba(82, 183, 136, 0.1)', fill: true, tension: 0.4, borderWidth: 1.5 }] }, options: commonMiniOptions });

        const m3 = document.getElementById('miniChart3');
        if (m3) new Chart(m3, { type: 'line', data: { labels: labels, datasets: [{ data: fbData, borderColor: '#9E9D24', backgroundColor: 'rgba(212, 225, 87, 0.2)', fill: true, tension: 0.4, borderWidth: 1.5 }] }, options: commonMiniOptions });
    }
});
