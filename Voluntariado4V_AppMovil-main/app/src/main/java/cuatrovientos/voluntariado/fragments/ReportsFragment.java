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

import java.util.ArrayList;
import java.util.List;

import cuatrovientos.voluntariado.R;

public class ReportsFragment extends Fragment {

    private LineChart lineChart;
    private PieChart pieChart;

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

        setupLineChart();
        setupPieChart();
    }

    // --- CONFIGURACIÓN DEL GRÁFICO DE LÍNEAS ---
    private void setupLineChart() {
        // Obtenemos el color dinámico del texto (Blanco en Dark Mode, Oscuro en Light Mode)
        int textColor = androidx.core.content.ContextCompat.getColor(requireContext(), R.color.text_primary);

        // 1. Crear los datos (Simulando la imagen: crecimiento constante)
        List<Entry> entries = new ArrayList<>();
        entries.add(new Entry(0, 15f)); // Q1 2024
        entries.add(new Entry(1, 22f)); // Q2 2024
        entries.add(new Entry(2, 35f)); // Q3 2024
        entries.add(new Entry(3, 48f)); // Q4 2024
        entries.add(new Entry(4, 55f)); // Q1 2025
        entries.add(new Entry(5, 75f)); // Q2 2025

        // 2. Configurar el Dataset (Estilo de la línea)
        LineDataSet dataSet = new LineDataSet(entries, "Voluntariados Creados");
        dataSet.setColor(Color.parseColor("#2196F3")); // Azul
        dataSet.setCircleColor(Color.parseColor("#2196F3"));
        dataSet.setLineWidth(3f);
        dataSet.setCircleRadius(5f);
        dataSet.setDrawValues(false); // No mostrar números sobre cada punto
        dataSet.setMode(LineDataSet.Mode.LINEAR);
        dataSet.setDrawFilled(true); // Rellenar debajo de la línea
        dataSet.setFillColor(Color.parseColor("#BBDEFB")); // Azul muy claro

        // 3. Configurar ejes (Labels Q1, Q2...)
        String[] quarters = new String[]{"Q1 2024", "Q2 2024", "Q3 2024", "Q4 2024", "Q1 2025", "Q2 2025"};

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(quarters));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(true);
        xAxis.setTextColor(textColor); // <-- APLICAR COLOR DINÁMICO

        lineChart.getAxisLeft().setTextColor(textColor); // <-- APLICAR COLOR DINÁMICO
        lineChart.getAxisRight().setEnabled(false); // Ocultar eje derecho
        lineChart.getDescription().setEnabled(false); // Ocultar descripción pequeña
        lineChart.getLegend().setTextColor(textColor); // <-- APLICAR COLOR DINÁMICO

        // 4. Aplicar datos
        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);
        lineChart.animateX(1000); // Animación simple
        lineChart.invalidate(); // Refrescar
    }

    // --- CONFIGURACIÓN DEL GRÁFICO DE PASTEL ---
    private void setupPieChart() {
        int textColor = androidx.core.content.ContextCompat.getColor(requireContext(), R.color.text_primary);

        // 1. Crear los datos (Simulando la imagen)
        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(35f, "Medio Ambiente"));
        entries.add(new PieEntry(40f, "Educación"));
        entries.add(new PieEntry(15f, "Asistencia Social"));
        entries.add(new PieEntry(10f, "Cultural"));

        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.parseColor("#4CAF50")); // Verde
        colors.add(Color.parseColor("#2196F3")); // Azul
        colors.add(Color.parseColor("#FF9800")); // Naranja
        colors.add(Color.parseColor("#E91E63")); // Rosa


        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(colors);
        dataSet.setSliceSpace(3f);
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setValueTextSize(12f);

        // 4. Configurar Estilo del Gráfico
        PieData pieData = new PieData(dataSet);
        // Formatear para que muestre % si se desea, aquí lo dejaremos simple

        pieChart.setData(pieData);
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setCenterText("Eventos\nActivos");
        pieChart.setCenterTextSize(14f);
        pieChart.setCenterTextColor(textColor); // <-- APLICAR COLOR DINÁMICO
        pieChart.setHoleRadius(40f); // Agujero en el centro (tipo Donut)
        pieChart.setTransparentCircleRadius(45f);
        pieChart.setHoleColor(Color.TRANSPARENT); // Asegurar que el agujero sea transparente/del color de fondo de la tarjeta

        // Configurar Leyenda
        Legend l = pieChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.CENTER);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setTextColor(textColor); // <-- APLICAR COLOR DINÁMICO

        pieChart.setEntryLabelColor(Color.WHITE); // Labels dentro del pastel (siempre visible sobre color)

        pieChart.animateY(1000);
        pieChart.invalidate();
    }
}