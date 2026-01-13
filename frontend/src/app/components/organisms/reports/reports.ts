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
  @ViewChild('growthChart') growthChartRef!: ElementRef;
  @ViewChild('distributionChart') distributionChartRef!: ElementRef;

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
    // Charts will be initialized after data load
  }

  loadData() {
    forkJoin({
      activities: this.apiService.getActivities(),
      volunteers: this.apiService.getVolunteers()
    }).subscribe({
      next: ({ activities, volunteers }) => {
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
    // Group by Year-Month
    const activitiesByMonth: { [key: string]: number } = {};
    activities.forEach(act => {
      const date = new Date(act.FECHA_INICIO);
      const key = `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}`; // YYYY-MM
      activitiesByMonth[key] = (activitiesByMonth[key] || 0) + 1;
    });

    // Sort months and take last 6 or all
    const sortedMonths = Object.keys(activitiesByMonth).sort();
    const growthLabels = sortedMonths.map(m => {
      const [year, month] = m.split('-');
      return `${month}/${year}`;
    });
    // Cumulative growth? User screenshot says "Voluntariados creados por trimestre". 
    // I'll show count per month for now. Or Cumulative? "Crecimiento" implies cumulative is better visually.
    // Let's do Cumulative.
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

    this.initCharts(growthLabels, growthData, Object.values(statusCounts));
  }

  initCharts(growthLabels: string[], growthData: number[], statusData: number[]) {
    if (this.growthChart) this.growthChart.destroy();
    if (this.distributionChart) this.distributionChart.destroy();

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
        plugins: {
          title: {
            display: false // Handled in HTML
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
  }
}
