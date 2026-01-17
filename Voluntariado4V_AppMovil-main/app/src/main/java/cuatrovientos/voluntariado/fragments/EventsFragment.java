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
    private List<VolunteerActivity> activities;

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
        activities = new ArrayList<>();
        fetchActivities();

        // Actualizar vista inicial (Se actualizará al recibir respuesta de API)
        // updateCalendar();

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

    private void fetchActivities() {
        cuatrovientos.voluntariado.network.ApiService apiService = 
            cuatrovientos.voluntariado.network.RetrofitClient.getClient().create(cuatrovientos.voluntariado.network.ApiService.class);

        apiService.getActivities().enqueue(new retrofit2.Callback<List<cuatrovientos.voluntariado.network.model.ApiActivity>>() {
            @Override
            public void onResponse(retrofit2.Call<List<cuatrovientos.voluntariado.network.model.ApiActivity>> call, retrofit2.Response<List<cuatrovientos.voluntariado.network.model.ApiActivity>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    activities.clear();
                    for (cuatrovientos.voluntariado.network.model.ApiActivity apiAct : response.body()) {
                        String rawStatus = apiAct.getStatus() != null ? apiAct.getStatus() : "ACTIVO";
                        String status = "Active"; 
                        
                        // Normalize Backend Status (Spanish) to Frontend (English)
                        if (rawStatus.equalsIgnoreCase("ACTIVO")) status = "Active";
                        else if (rawStatus.equalsIgnoreCase("PENDIENTE")) status = "Pending";
                        else if (rawStatus.equalsIgnoreCase("SUSPENDIDO")) status = "Suspended";
                        else if (rawStatus.equalsIgnoreCase("FINALIZADA")) status = "Finished";
                        else status = rawStatus;

                        // Use Status for Color instead of Type
                        int color = getColorForStatus(status);
                        
                        activities.add(new VolunteerActivity(
                            apiAct.getTitle(),
                            apiAct.getDescription(),
                            apiAct.getLocation(),
                            apiAct.getDate(),
                            apiAct.getDuration(),
                            apiAct.getEndDate(),
                            apiAct.getMaxVolunteers(),
                            apiAct.getType(),
                            status,
                            (apiAct.getOrganization() != null ? apiAct.getOrganization().getName() : "Cuatrovientos"),
                            null, // Avatar
                            color,
                            apiAct.getImagen()
                        ));
                    }
                    updateCalendar();
                } else {
                    android.util.Log.e("EventsFragment", "Error fetching activities: " + response.code());
                    if (getContext() != null) {
                        android.widget.Toast.makeText(getContext(), "Error al cargar actividades", android.widget.Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(retrofit2.Call<List<cuatrovientos.voluntariado.network.model.ApiActivity>> call, Throwable t) {
                android.util.Log.e("EventsFragment", "Network error: " + t.getMessage());
                if (getContext() != null) {
                    android.widget.Toast.makeText(getContext(), "Error de conexión", android.widget.Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private int getColorForStatus(String status) {
        if (status == null) return 0xFF9E9E9E; // Gray default
        switch (status) {
            case "Active": return 0xFF4CAF50; // Green
            case "Pending": return 0xFFFFC107; // Amber
            case "Finished": return 0xFF9E9E9E; // Grey
            case "Suspended": return 0xFFF44336; // Red
            default: return 0xFF2196F3; // Blue default
        }
    }

    private int getColorForType(String type) {
        // Deprecated for Calendar, kept if needed elsewhere
        if (type == null) return 0xFF9E9E9E; 
        switch (type) {
            case "Medio Ambiente": return 0xFF2E7D32; 
            case "Social": return 0xFF1976D2; 
            case "Tecnológico": return 0xFFFFC107; 
            case "Educativo": return 0xFF512DA8; 
            default: return 0xFFEF5350; 
        }
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
            
            for (VolunteerActivity activity : activities) {
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