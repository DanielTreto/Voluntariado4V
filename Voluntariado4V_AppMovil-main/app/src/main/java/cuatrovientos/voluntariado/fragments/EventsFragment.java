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
import java.util.Date;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import cuatrovientos.voluntariado.R;
import cuatrovientos.voluntariado.adapters.CalendarAdapter;
import cuatrovientos.voluntariado.model.EventDay;
import cuatrovientos.voluntariado.model.VolunteerActivity;
import cuatrovientos.voluntariado.utils.ActivityMapper;
import cuatrovientos.voluntariado.dialogs.ActivityDetailDialog;

public class EventsFragment extends Fragment {

    private Calendar currentMonth;
    // private TextView tvMonthTitle; (Removed)
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

        android.widget.Spinner spinnerMonth = view.findViewById(R.id.spinnerMonth);
        android.widget.Spinner spinnerYear = view.findViewById(R.id.spinnerYear);

        // Inicializar calendario al mes actual
        currentMonth = Calendar.getInstance();
        
        activities = new ArrayList<>();
        fetchActivities();

        // Setup Month Spinner
        String[] months = new String[]{"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};
        android.widget.ArrayAdapter<String> monthAdapter = new android.widget.ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, months);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMonth.setAdapter(monthAdapter);
        spinnerMonth.setSelection(currentMonth.get(Calendar.MONTH));

        // Setup Year Spinner
        List<String> years = new ArrayList<>();
        int currentYear = currentMonth.get(Calendar.YEAR);
        for (int i = currentYear - 2; i <= currentYear + 5; i++) {
            years.add(String.valueOf(i));
        }
        android.widget.ArrayAdapter<String> yearAdapter = new android.widget.ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, years);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerYear.setAdapter(yearAdapter);
        // Select current year (index 2 because we started -2)
        spinnerYear.setSelection(2); 

        // Listeners
        android.widget.AdapterView.OnItemSelectedListener listener = new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                int selectedMonth = spinnerMonth.getSelectedItemPosition();
                String selectedYearStr = (String) spinnerYear.getSelectedItem();
                
                if (selectedYearStr != null) {
                    int selectedYear = Integer.parseInt(selectedYearStr);
                    currentMonth.set(Calendar.MONTH, selectedMonth);
                    currentMonth.set(Calendar.YEAR, selectedYear);
                    updateCalendar();
                }
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        };

        spinnerMonth.setOnItemSelectedListener(listener);
        spinnerYear.setOnItemSelectedListener(listener);
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
                        activities.add(ActivityMapper.mapApiToModel(apiAct));
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
            case "InProgress": return 0xFF2196F3; // Blue
            default: return 0xFF2196F3; // Blue default
        }
    }

    private void updateCalendar() {
        // 1. (Titulo eliminado, controlado por Spinners)

        List<EventDay> days = new ArrayList<>();

        // 2. Calcular días del mes
        Calendar calendar = (Calendar) currentMonth.clone();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int firstDayOffset = dayOfWeek - 2; // Lunes(2) -> 0. Domingo(1) -> -1 (será 6)
        if (firstDayOffset < 0) firstDayOffset = 6; 

        // Rellenar espacios vacios antes del día 1
        for (int i = 0; i < firstDayOffset; i++) {
            days.add(new EventDay("", null, null, null));
        }

        // Rellenar días del mes
        int maxDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        SimpleDateFormat dateSdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        
        // Formatter for parsing the Activity Date (d/m/y H:i) or (dd/mm/yyyy)
        // Adjust based on observation of backend data. "d/m/y H:i" -> "18/01/26 15:47"
        // But let's be safe and try multiple or assume ActivityMapper passes strings.
        // Wait, VolunteerController sends "d/m/y H:i". y is 2 digit year? 
        // VolunteerController: format('d/m/y H:i'). Php 'y' is 2 digit year.
        SimpleDateFormat activitySdf = new SimpleDateFormat("dd/MM/yy HH:mm", Locale.getDefault());
        // Sometimes it might receive full year? Let's try 2 digit year first as per controller.

        for (int i = 1; i <= maxDays; i++) {
            calendar.set(Calendar.DAY_OF_MONTH, i);
            String currentDateString = dateSdf.format(calendar.getTime());
            
            // Buscar evento para esta fecha
            String eventTitle = null;
            String eventColor = null;
            VolunteerActivity matchedActivity = null;
            
            for (VolunteerActivity activity : activities) {
                String actDateStr = activity.getDate(); // e.g., "18/01/26 15:47" or "2026-01-18" or "Fecha por definir"
                if (actDateStr == null || actDateStr.contains("difinir")) continue;

                try {
                    Date actDate = null;
                    // Try parsing
                    if (actDateStr.contains("/")) {
                         // Likely d/m/y or dd/MM/yyyy
                         try {
                              // Try full format with time first (VolunteerController)
                              actDate = activitySdf.parse(actDateStr);
                         } catch(Exception e) {
                              try {
                                  // Try dd/MM/yy (ActivityController uses this, e.g. 18/01/26)
                                  actDate = new SimpleDateFormat("dd/MM/yy", Locale.getDefault()).parse(actDateStr);
                              } catch (Exception e2) {
                                  try {
                                      // Try dd/MM/yyyy
                                      actDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(actDateStr);
                                  } catch (Exception e3) {
                                      // Fail
                                  }
                              }
                         }
                    } else if (actDateStr.contains("-")) {
                         // Likely yyyy-MM-dd
                         actDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(actDateStr);
                    }
                    
                    if (actDate != null) {
                         String actDateFormatted = dateSdf.format(actDate);
                         if (actDateFormatted.equals(currentDateString)) {
                             eventTitle = activity.getTitle();
                             // Color per status as requested
                             int color = getColorForStatus(activity.getStatus());
                             eventColor = String.format("#%06X", (0xFFFFFF & color));
                             matchedActivity = activity;
                             break; // Find match and stop
                         }
                    }
                } catch (Exception e) {
                    // Ignore parsing errors
                }
            }

            days.add(new EventDay(String.valueOf(i), eventTitle, eventColor, matchedActivity));
        }

        adapter = new CalendarAdapter(days, new CalendarAdapter.OnEventClickListener() {
            @Override
            public void onEventClick(List<VolunteerActivity> activities) {
                // Not used in this simplified implementation
            }

            @Override
            public void onEventClick(VolunteerActivity activity) {
                if (getActivity() != null && activity != null) {
                     ActivityDetailDialog dialog = ActivityDetailDialog.newInstance(activity, true);
                     dialog.show(getParentFragmentManager(), "ActivityDetailDialog");
                }
            }
        });
        recyclerView.setAdapter(adapter);
    }
}