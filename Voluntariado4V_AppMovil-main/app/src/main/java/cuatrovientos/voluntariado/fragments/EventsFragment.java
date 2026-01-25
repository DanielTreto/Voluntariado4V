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
    private RecyclerView recyclerView;
    private CalendarAdapter adapter;
    private List<VolunteerActivity> activities;

    public EventsFragment() {
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

        currentMonth = Calendar.getInstance();
        
        activities = new ArrayList<>();
        fetchActivities();

        String[] months = new String[]{"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};
        android.widget.ArrayAdapter<String> monthAdapter = new android.widget.ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, months);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMonth.setAdapter(monthAdapter);
        spinnerMonth.setSelection(currentMonth.get(Calendar.MONTH));

        List<String> years = new ArrayList<>();
        int currentYear = currentMonth.get(Calendar.YEAR);
        for (int i = currentYear - 2; i <= currentYear + 5; i++) {
            years.add(String.valueOf(i));
        }
        android.widget.ArrayAdapter<String> yearAdapter = new android.widget.ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, years);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerYear.setAdapter(yearAdapter);
        spinnerYear.setSelection(2); 

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
        if (status == null) return 0xFF9E9E9E; 
        switch (status) {
            case "Active": return 0xFF4CAF50; 
            case "Pending": return 0xFFFFC107; 
            case "Finished": return 0xFF9E9E9E; 
            case "Suspended": return 0xFFF44336; 
            case "InProgress": return 0xFF2196F3; 
            default: return 0xFF2196F3; 
        }
    }

    private void updateCalendar() {

        List<EventDay> days = new ArrayList<>();

        Calendar calendar = (Calendar) currentMonth.clone();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int firstDayOffset = dayOfWeek - 2; 
        if (firstDayOffset < 0) firstDayOffset = 6; 

        for (int i = 0; i < firstDayOffset; i++) {
            days.add(new EventDay("", null, null, null));
        }

        int maxDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        SimpleDateFormat dateSdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        
        SimpleDateFormat activitySdf = new SimpleDateFormat("dd/MM/yy HH:mm", Locale.getDefault());

        for (int i = 1; i <= maxDays; i++) {
            calendar.set(Calendar.DAY_OF_MONTH, i);
            String currentDateString = dateSdf.format(calendar.getTime());
            
            String eventTitle = null;
            String eventColor = null;
            VolunteerActivity matchedActivity = null;
            
            for (VolunteerActivity activity : activities) {
                String actDateStr = activity.getDate(); 
                if (actDateStr == null || actDateStr.contains("difinir")) continue;

                try {
                    Date actDate = null;
                    if (actDateStr.contains("/")) {
                         try {
                              actDate = activitySdf.parse(actDateStr);
                         } catch(Exception e) {
                              try {
                                  actDate = new SimpleDateFormat("dd/MM/yy", Locale.getDefault()).parse(actDateStr);
                              } catch (Exception e2) {
                                  try {
                                      actDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(actDateStr);
                                  } catch (Exception e3) {
                                  }
                              }
                         }
                    } else if (actDateStr.contains("-")) {
                         actDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(actDateStr);
                    }
                    
                    if (actDate != null) {
                         String actDateFormatted = dateSdf.format(actDate);
                         if (actDateFormatted.equals(currentDateString)) {
                             eventTitle = activity.getTitle();
                             int color = getColorForStatus(activity.getStatus());
                             eventColor = String.format("#%06X", (0xFFFFFF & color));
                             matchedActivity = activity;
                             break; 
                         }
                    }
                } catch (Exception e) {
                }
            }

            days.add(new EventDay(String.valueOf(i), eventTitle, eventColor, matchedActivity));
        }

        adapter = new CalendarAdapter(days, new CalendarAdapter.OnEventClickListener() {
            @Override
            public void onEventClick(List<VolunteerActivity> activities) {
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