package cuatrovientos.voluntariado.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.widget.Button;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import cuatrovientos.voluntariado.R;
import cuatrovientos.voluntariado.adapters.CalendarAdapter;
import cuatrovientos.voluntariado.model.EventDay;
import cuatrovientos.voluntariado.model.VolunteerActivity;

public class EventsFragment extends Fragment {

    private Calendar currentMonth;
    private TextView tvMonthTitle;
    private RecyclerView recyclerView;
    private CalendarAdapter adapter;
    private List<VolunteerActivity> mockActivities;

    public EventsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_events, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerCalendar);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 7));

        tvMonthTitle = view.findViewById(R.id.tvMonthTitle);
        Button btnPrevMonth = view.findViewById(R.id.btnPrevMonth);
        Button btnNextMonth = view.findViewById(R.id.btnNextMonth);

        // Inicializar calendario al mes actual
        currentMonth = Calendar.getInstance();
        
        // Cargar datos de prueba
        loadMockData();

        // Actualizar vista inicial
        updateCalendar();

        // Listeners
        btnPrevMonth.setOnClickListener(v -> {
            currentMonth.add(Calendar.MONTH, -1);
            updateCalendar();
        });

        btnNextMonth.setOnClickListener(v -> {
            currentMonth.add(Calendar.MONTH, 1);
            updateCalendar();
        });
    }

    private void loadMockData() {
        mockActivities = new ArrayList<>();
        // Ejemplos para Enero 2026 (Mes actual según prompt entorno, aunque la fecha real sea 2026-01-16)
        mockActivities.add(new VolunteerActivity("Limpieza Río", "", "", "2026-01-05", "Medio Ambiente", "Active", 0xFF2E7D32));
        mockActivities.add(new VolunteerActivity("Reparto Alimentos", "", "", "2026-01-16", "Social", "Active", 0xFF1976D2));
        mockActivities.add(new VolunteerActivity("Taller Digital", "", "", "2026-01-20", "Tecnológico", "Active", 0xFFFFC107));
        
        // Ejemplos para Febrero 2026
        mockActivities.add(new VolunteerActivity("Charla Educativa", "", "", "2026-02-10", "Educativo", "Active", 0xFF512DA8));
        
        // Ejemplos para Diciembre 2025
        mockActivities.add(new VolunteerActivity("Cena Navidad", "", "", "2025-12-24", "Social", "Active", 0xFFD32F2F));
    }

    private void updateCalendar() {
        // 1. Actualizar Título
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy", new Locale("es", "ES"));
        String title = sdf.format(currentMonth.getTime());
        // Capitalizar primera letra
        title = title.substring(0, 1).toUpperCase() + title.substring(1);
        tvMonthTitle.setText(title);

        List<EventDay> days = new ArrayList<>();

        // 2. Calcular días del mes
        Calendar calendar = (Calendar) currentMonth.clone();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        
        // Obtener día de la semana del primer día (1=Domingo, 2=Lunes...)
        // Queremos que la semana empiece en Lunes (Index 0 en nuestra vista)
        // Calendar.MONDAY = 2.
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int firstDayOffset = dayOfWeek - 2; // Lunes(2) -> 0. Domingo(1) -> -1 (será 6)
        if (firstDayOffset < 0) firstDayOffset = 6; 

        // Rellenar espacios vacios antes del día 1
        for (int i = 0; i < firstDayOffset; i++) {
            days.add(new EventDay("", null, null));
        }

        // Rellenar días del mes
        int maxDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        SimpleDateFormat dateSdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        for (int i = 1; i <= maxDays; i++) {
            calendar.set(Calendar.DAY_OF_MONTH, i);
            String dateString = dateSdf.format(calendar.getTime());
            
            // Buscar evento para esta fecha
            String eventTitle = null;
            String eventColor = null;
            
            for (VolunteerActivity activity : mockActivities) {
                if (activity.getDate().equals(dateString)) {
                    eventTitle = activity.getTitle();
                    // Convertir int color a Hex string si es necesario, o pasar int directamente al adaptador
                    // Aquí asumimos Hex String para adaptar al modelo EventDay existente
                    eventColor = String.format("#%06X", (0xFFFFFF & activity.getImageColor()));
                    break; // Mostrar solo el primero
                }
            }

            days.add(new EventDay(String.valueOf(i), eventTitle, eventColor));
        }

        if (adapter == null) {
            adapter = new CalendarAdapter(days);
            recyclerView.setAdapter(adapter);
        } else {
            // Recrear adaptador o actualizar lista es más seguro para cambio total de estructura
            adapter = new CalendarAdapter(days);
            recyclerView.setAdapter(adapter);
        }
    }
}