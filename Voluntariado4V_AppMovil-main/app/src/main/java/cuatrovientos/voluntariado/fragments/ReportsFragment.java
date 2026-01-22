package cuatrovientos.voluntariado.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

// Importaciones de MPAndroidChart
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;

import android.widget.TextView;
import cuatrovientos.voluntariado.network.ApiService;
import cuatrovientos.voluntariado.network.RetrofitClient;
import cuatrovientos.voluntariado.network.model.AdminStats;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.AdapterView;
import java.util.Calendar;

import java.util.ArrayList;
import java.util.List;

import cuatrovientos.voluntariado.R;

/**
 * Fragmento que muestra informes y estadísticas.
 * Utiliza gráficos (LineChart y PieChart) para visualizar datos.
 */
public class ReportsFragment extends Fragment {

    private LineChart lineChart;
    private PieChart pieChart;
    private TextView tvTotalActivities, tvTotalVolunteers, tvTotalOrgs;


    private Spinner spinnerYear;

    public ReportsFragment() {
        // Constructor vacío requerido
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reports, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        lineChart = view.findViewById(R.id.lineChart);
        pieChart = view.findViewById(R.id.pieChart);

        tvTotalActivities = view.findViewById(R.id.tvTotalActivities);
        tvTotalVolunteers = view.findViewById(R.id.tvTotalVolunteers);
        tvTotalOrgs = view.findViewById(R.id.tvTotalOrgs);
        spinnerYear = view.findViewById(R.id.spinnerYear);

        setupYearSpinner();
        setupLineChart();
        setupPieChart();
        // loadStats() eliminado de aquí, se llama desde el listener del spinner
    }

    private void setupYearSpinner() {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        List<String> years = new ArrayList<>();
        for (int i = currentYear; i >= 2023; i--) { // Mostrar desde 2023 hasta actual
            years.add(String.valueOf(i));
        }
        // Añadir año siguiente por si acaso
        years.add(0, String.valueOf(currentYear + 1));

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, years);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerYear.setAdapter(adapter);
        
        spinnerYear.setAdapter(adapter);
        
        // Seleccionar año actual por defecto
        int defaultPos = adapter.getPosition(String.valueOf(currentYear));
        spinnerYear.setSelection(defaultPos, false); // false para prevenir disparo inmediato si es posible
        
        // Carga inicial de TODOS los datos (True) - Hecho manualmente una vez
        loadStats(currentYear, true);

        // Establecer listener para futuras interacciones del usuario
        spinnerYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedYear = (String) parent.getItemAtPosition(position);

                loadStats(Integer.parseInt(selectedYear), false);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    // --- CONFIGURACIÓN DEL GRÁFICO DE LÍNEAS ---
    private void setupLineChart() {
        int textColor = androidx.core.content.ContextCompat.getColor(requireContext(), R.color.text_primary);

        // Datos vacíos iniciales
        List<Entry> entries = new ArrayList<>();
        
        LineDataSet dataSet = new LineDataSet(entries, "Actividades realizadas");
        dataSet.setColor(Color.parseColor("#2196F3"));
        dataSet.setCircleColor(Color.parseColor("#2196F3"));
        dataSet.setLineWidth(3f);
        dataSet.setCircleRadius(5f);
        dataSet.setDrawValues(true);
        dataSet.setValueTextColor(textColor);
        dataSet.setMode(LineDataSet.Mode.LINEAR);
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(Color.parseColor("#BBDEFB"));

        String[] months = new String[]{"Ene", "Feb", "Mar", "Abr", "May", "Jun", "Jul", "Ago", "Sep", "Oct", "Nov", "Dic"};

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(months));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(true);
        xAxis.setTextColor(textColor);
        xAxis.setLabelCount(12);

        lineChart.getAxisLeft().setTextColor(textColor);
        lineChart.getAxisRight().setEnabled(false);
        lineChart.getDescription().setEnabled(false);
        lineChart.getLegend().setTextColor(textColor);
        lineChart.setExtraBottomOffset(10f); // Margen para Leyenda

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);
        lineChart.invalidate();
    }


    // --- CONFIGURACIÓN DEL GRÁFICO DE PASTEL ---
    private void setupPieChart() {
        int textColor = androidx.core.content.ContextCompat.getColor(requireContext(), R.color.text_primary);

        // Datos vacíos iniciales
        List<PieEntry> entries = new ArrayList<>();
        // entries.add(new PieEntry(1f, "Sin Datos")); // Placeholder opcional

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setSliceSpace(3f);
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setValueTextSize(12f);
        
        dataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) value);
            }
        });

        // Los colores se establecerán dinámicamente, pero aquí el por defecto
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.parseColor("#BDBDBD")); 
        dataSet.setColors(colors);

        PieData pieData = new PieData(dataSet);
        
        pieChart.setData(pieData);
        pieChart.setUsePercentValues(false);
        pieChart.getDescription().setEnabled(false);
        pieChart.setCenterText("Actividades\npor Estado");
        pieChart.setCenterTextSize(14f);
        pieChart.setCenterTextColor(textColor); 
        pieChart.setHoleRadius(40f); 
        pieChart.setTransparentCircleRadius(45f);
        pieChart.setHoleColor(Color.TRANSPARENT); 

        // Configurar Leyenda
        Legend l = pieChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.CENTER);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setTextColor(textColor); 

        pieChart.setEntryLabelColor(Color.WHITE); 

        pieChart.animateY(1000);
        pieChart.invalidate();
    }


    private void loadStats(int year, boolean updateAll) {
        ApiService api = RetrofitClient.getClient().create(ApiService.class);
        api.getAdminStats(year).enqueue(new Callback<AdminStats>() {
             @Override
             public void onResponse(Call<AdminStats> call, Response<AdminStats> response) {
                 if(response.isSuccessful() && response.body() != null) {
                      AdminStats stats = response.body();
                      
                      if (updateAll) {
                          if (tvTotalActivities != null) tvTotalActivities.setText(String.valueOf(stats.getTotalActivities()));
                          if (tvTotalVolunteers != null) tvTotalVolunteers.setText(String.valueOf(stats.getTotalVolunteers()));
                          if (tvTotalOrgs != null) tvTotalOrgs.setText(String.valueOf(stats.getTotalOrganizations()));
    
                          // Actualizar Gráfico de Pastel
                          if (stats.getStatusDistribution() != null && pieChart != null) {
                              List<PieEntry> entries = new ArrayList<>();
                              ArrayList<Integer> colors = new ArrayList<>();
                              
                              for (java.util.Map.Entry<String, Integer> entry : stats.getStatusDistribution().entrySet()) {
                                  String status = entry.getKey();
                                  int count = entry.getValue();
                                  if (count == 0) continue;
    
                                  String label = status;
                                  int color = Color.GRAY;
    
                                  if ("PENDIENTE".equals(status)) {
                                      label = "Pendientes";
                                      color = Color.parseColor("#FFA000"); // Naranja
                                  } else if ("EN_PROGRESO".equals(status)) {
                                      label = "En Progreso";
                                      color = Color.parseColor("#2196F3"); // Azul
                                  } else if ("FINALIZADA".equals(status)) {
                                      label = "Finalizadas";
                                      color = Color.parseColor("#757575"); // Gris
                                  } else if ("DENEGADA".equals(status)) {
                                      label = "Denegadas";
                                      color = Color.parseColor("#616161"); // Gris Oscuro
                                  } else if ("SUSPENDIDA".equals(status)) {
                                      label = "Suspendidas";
                                      color = Color.parseColor("#D32F2F"); // Rojo Oscuro
                                  } else if ("ACTIVE".equalsIgnoreCase(status) || "ACTIVO".equalsIgnoreCase(status)) {
                                      label = "Activas";
                                      color = Color.parseColor("#4CAF50"); // Verde
                                  }
    
                                  entries.add(new PieEntry((float)count, label));
                                  colors.add(color);
                              }
                              
                              PieDataSet set = (PieDataSet) pieChart.getData().getDataSetByIndex(0);
                              if (set != null) {
                                  set.setValues(entries);
                                  set.setColors(colors);
                                  pieChart.getData().notifyDataChanged();
                                  pieChart.notifyDataSetChanged();
                                  pieChart.animateY(1000);
                                  pieChart.invalidate();
                              }
                          }
                      }

                      // Actualizar Gráfico de Líneas
                      if (stats.getMonthlyActivities() != null && lineChart != null) {
                          List<Entry> entries = new ArrayList<>();
                          for (int i = 0; i < stats.getMonthlyActivities().size(); i++) {
                              entries.add(new Entry(i, stats.getMonthlyActivities().get(i).floatValue()));
                          }
                          
                          LineDataSet set = (LineDataSet) lineChart.getData().getDataSetByIndex(0);
                          if (set != null) {
                              set.setValues(entries);
                              lineChart.getData().notifyDataChanged();
                              lineChart.notifyDataSetChanged();
                              lineChart.animateX(1000);
                              lineChart.invalidate();
                          }
                      }
                 }
             }
             @Override
             public void onFailure(Call<AdminStats> call, Throwable t) {
                 // Ignorar o loguear
             }
        });
    }
}