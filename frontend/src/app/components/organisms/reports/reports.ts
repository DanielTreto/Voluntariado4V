import { Component, AfterViewInit, ElementRef, ViewChild, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import Chart from 'chart.js/auto';
import { ApiService } from '../../../services/api.service';
import { forkJoin } from 'rxjs';

@Component({
  selector: 'app-reports',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './reports.html',
  styleUrl: './reports.css'
})
export class ReportsComponent implements OnInit, AfterViewInit {
  @ViewChild('growthChart', { static: true }) growthChartRef!: ElementRef;
  @ViewChild('distributionChart', { static: true }) distributionChartRef!: ElementRef;

  private apiService = inject(ApiService);

  // Metrics
  totalActivities = 0;
  totalVolunteers = 0;
  activeActivities = 0;
  participationRate = 0;

  // Analysis Text
  analysisText = 'Cargando análisis...';

  // Chart Instances
  growthChart: any;
  distributionChart: any;

  ngOnInit() {
    this.loadData();
  }

  ngAfterViewInit() {
    // Charts initialized in processData
  }

  loadData() {
    forkJoin({
      activities: this.apiService.getActivities(),
      volunteers: this.apiService.getVolunteers()
    }).subscribe({
      next: ({ activities, volunteers }) => {
        console.log('Data loaded:', activities.length, volunteers.length);
        this.processData(activities, volunteers);
      },
      error: (err) => {
        console.error('Error loading report data', err);
        this.analysisText = 'Error al cargar los datos para el análisis.';
      }
    });
  }

  processData(activities: any[], volunteers: any[]) {
    this.totalActivities = activities.length;
    this.totalVolunteers = volunteers.length;
    this.activeActivities = activities.filter(a => a.ESTADO === 'EN_PROGRESO').length;

    // Calculate Growth (Activities by Month)
    const activitiesByMonth: { [key: string]: number } = {};
    activities.forEach(act => {
      // Handle potential date format issues
      const dateStr = act.FECHA_INICIO;
      let date = new Date(dateStr);
      if (isNaN(date.getTime())) {
        // Try manual parsing if format is YYYY-MM-DD and browser fails (unlikely but safe)
        const parts = dateStr.split('-');
        if (parts.length === 3) {
          date = new Date(parseInt(parts[0]), parseInt(parts[1]) - 1, parseInt(parts[2]));
        }
      }

      if (!isNaN(date.getTime())) {
        const key = `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}`; // YYYY-MM
        activitiesByMonth[key] = (activitiesByMonth[key] || 0) + 1;
      }
    });

    const sortedMonths = Object.keys(activitiesByMonth).sort();
    const growthLabels = sortedMonths.map(m => {
      const [year, month] = m.split('-');
      return `${month}/${year}`;
    });

    let cumulative = 0;
    const growthData = sortedMonths.map(m => {
      cumulative += activitiesByMonth[m];
      return cumulative;
    });

    // Calculate Distribution (By Status)
    const statusCounts = {
      'PENDIENTE': 0,
      'EN_PROGRESO': 0,
      'FINALIZADA': 0,
      'DENEGADA': 0
    };
    activities.forEach(a => {
      const status = a.ESTADO as keyof typeof statusCounts;
      if (statusCounts[status] !== undefined) {
        statusCounts[status]++;
      }
    });

    // Generate Analysis Text
    this.analysisText = `Actualmente contamos con un total de ${this.totalActivities} actividades registradas y ${this.totalVolunteers} voluntarios. 
    Hay ${this.activeActivities} actividades activas en este momento. 
    La tendencia muestra un crecimiento constante con ${activities.length} nuevos eventos acumulados.`;

    console.log('Initializing charts with:', { growthLabels, growthData, statusCounts });
    this.initCharts(growthLabels, growthData, Object.values(statusCounts));
  }

  initCharts(growthLabels: string[], growthData: number[], statusData: number[]) {
    if (this.growthChart) this.growthChart.destroy();
    if (this.distributionChart) this.distributionChart.destroy();

    if (!this.growthChartRef || !this.distributionChartRef) {
      console.warn('Chart refs not available');
      return;
    }

    try {
      // Growth Chart
      this.growthChart = new Chart(this.growthChartRef.nativeElement, {
        type: 'line',
        data: {
          labels: growthLabels,
          datasets: [{
            label: 'Acumulado de Actividades',
            data: growthData,
            borderColor: '#1e88e5',
            backgroundColor: 'rgba(30, 136, 229, 0.1)',
            tension: 0.4,
            fill: true,
            pointRadius: 4,
            pointBackgroundColor: '#1e88e5'
          }]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          plugins: {
            title: {
              display: false
            }
          },
          scales: {
            y: {
              beginAtZero: true
            }
          }
        }
      });

      // Distribution Chart
      this.distributionChart = new Chart(this.distributionChartRef.nativeElement, {
        type: 'doughnut',
        data: {
          labels: ['Pendiente', 'En Progreso', 'Finalizada', 'Denegada'],
          datasets: [{
            label: 'Cantidad',
            data: statusData,
            backgroundColor: [
              '#FF9800', // PENDIENTE (Orange)
              '#4CAF50', // EN_PROGRESO (Green)
              '#1e88e5', // FINALIZADA (Blue)
              '#F44336'  // DENEGADA (Red)
            ],
            hoverOffset: 4
          }]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          plugins: {
            legend: {
              position: 'right',
            }
          }
        }
      });
      console.log('Charts initialized successfully');
    } catch (e) {
      console.error('Error initializing charts', e);
    }
  }
}
