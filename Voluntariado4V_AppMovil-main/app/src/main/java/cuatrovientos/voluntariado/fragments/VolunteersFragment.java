package cuatrovientos.voluntariado.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.tabs.TabLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import cuatrovientos.voluntariado.R;
import cuatrovientos.voluntariado.adapters.VolunteersAdapter;
import cuatrovientos.voluntariado.model.Volunteer;

public class VolunteersFragment extends Fragment {

    private List<Volunteer> masterList;
    private VolunteersAdapter adapter;

    public VolunteersFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_volunteers, container, false);
    }

    private String currentSearchQuery = "";
    private String currentCourseFilter = "Todos";
    private String currentStatusFilter = "Todos";
    private String currentPreferenceFilter = "Todos";
    private String currentAvailabilityFilter = "Todos";
    private boolean isAscending = true;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerVolunteers);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        android.widget.EditText etSearch = view.findViewById(R.id.etSearch);
        TabLayout tabLayout = view.findViewById(R.id.tabLayout);
        
        etSearch.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentSearchQuery = s.toString();
                if (tabLayout.getSelectedTabPosition() == 0) filterList("Solicitudes");
                else filterList("Registrados");
            }
            @Override public void afterTextChanged(android.text.Editable s) {}
        });

        android.widget.Button btnFilterCourse = view.findViewById(R.id.btnFilterCourse);
        btnFilterCourse.setOnClickListener(v -> showCourseFilterDialog());

        android.widget.Button btnFilterStatus = view.findViewById(R.id.btnFilterStatus);
        btnFilterStatus.setOnClickListener(v -> showStatusFilterDialog());

        android.widget.Button btnFilterPreferences = view.findViewById(R.id.btnFilterPreferences);
        btnFilterPreferences.setOnClickListener(v -> showPreferencesFilterDialog());

        android.widget.Button btnFilterAvailability = view.findViewById(R.id.btnFilterAvailability);
        btnFilterAvailability.setOnClickListener(v -> showAvailabilityFilterDialog());

        android.widget.Button btnSortVolunteers = view.findViewById(R.id.btnSortVolunteers);
        btnSortVolunteers.setOnClickListener(v -> {
            isAscending = !isAscending;
            btnSortVolunteers.setText(isAscending ? "Orden: A-Z" : "Orden: Z-A");
            if (tabLayout.getSelectedTabPosition() == 0) filterList("Solicitudes");
            else filterList("Registrados");
        });

        masterList = new ArrayList<>();
        fetchVolunteers();

        adapter = new VolunteersAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);
        filterList("Solicitudes");

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) filterList("Solicitudes");
                else filterList("Registrados");
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void showCourseFilterDialog() {
        if (masterList == null) return;
        java.util.Set<String> coursesSet = new java.util.HashSet<>();
        coursesSet.add("Todos");
        for (Volunteer v : masterList) {
            if (v.getCourse() != null && !v.getCourse().isEmpty()) coursesSet.add(v.getCourse());
        }
        List<String> coursesList = new ArrayList<>(coursesSet);
        Collections.sort(coursesList);
        String[] coursesArray = coursesList.toArray(new String[0]);

        new android.app.AlertDialog.Builder(getContext())
            .setTitle("Filtrar por Curso")
            .setItems(coursesArray, (dialog, which) -> {
                currentCourseFilter = coursesArray[which];
                android.widget.Button btn = getView().findViewById(R.id.btnFilterCourse);
                if (btn != null) btn.setText(currentCourseFilter.equals("Todos") ? "Curso" : currentCourseFilter);
                
                TabLayout tabs = getView().findViewById(R.id.tabLayout);
                if (tabs.getSelectedTabPosition() == 0) filterList("Solicitudes");
                else filterList("Registrados");
            })
            .show();
    }

    private void showStatusFilterDialog() {
        String[] statuses = {"Todos", "Active", "Suspended"};
        String[] displayStatuses = {"Todos", "Activos", "Suspendidos"};
        
        new android.app.AlertDialog.Builder(getContext())
            .setTitle("Filtrar por Estado")
            .setItems(displayStatuses, (dialog, which) -> {
                currentStatusFilter = statuses[which];
                android.widget.Button btn = getView().findViewById(R.id.btnFilterStatus);
                if (btn != null) btn.setText(currentStatusFilter.equals("Todos") ? "Estado" : displayStatuses[which]);
                
                TabLayout tabs = getView().findViewById(R.id.tabLayout);
                if (tabs.getSelectedTabPosition() == 0) filterList("Solicitudes");
                else filterList("Registrados");
            })
            .show();
    }

    private void showPreferencesFilterDialog() {
        if (masterList == null) return;
        java.util.Set<String> prefSet = new java.util.HashSet<>();
        prefSet.add("Todos");
        for (Volunteer v : masterList) {
            if (v.getPreferences() != null) {
                prefSet.addAll(v.getPreferences());
            }
        }
        List<String> prefList = new ArrayList<>(prefSet);
        Collections.sort(prefList);
        String[] prefArray = prefList.toArray(new String[0]);

        new android.app.AlertDialog.Builder(getContext())
            .setTitle("Filtrar por Preferencia")
            .setItems(prefArray, (dialog, which) -> {
                currentPreferenceFilter = prefArray[which];
                android.widget.Button btn = getView().findViewById(R.id.btnFilterPreferences);
                if (btn != null) btn.setText(currentPreferenceFilter.equals("Todos") ? "Preferencias" : currentPreferenceFilter);
                
                TabLayout tabs = getView().findViewById(R.id.tabLayout);
                if (tabs.getSelectedTabPosition() == 0) filterList("Solicitudes");
                else filterList("Registrados");
            })
            .show();
    }

    private void showAvailabilityFilterDialog() {
        if (masterList == null) return;
        java.util.Set<String> availSet = new java.util.HashSet<>();
        availSet.add("Todos");
        
        for (Volunteer v : masterList) {
            if (v.getAvailability() != null) {
                for (String avString : v.getAvailability()) {
                    if (avString.contains(":")) {
                        String day = avString.split(":")[0].trim().toUpperCase();
                        availSet.add(day);
                    }
                }
            }
        }
        List<String> availList = new ArrayList<>(availSet);
        Collections.sort(availList);
        String[] availArray = availList.toArray(new String[0]);

        new android.app.AlertDialog.Builder(getContext())
            .setTitle("Filtrar por Disponibilidad (Día)")
            .setItems(availArray, (dialog, which) -> {
                currentAvailabilityFilter = availArray[which];
                android.widget.Button btn = getView().findViewById(R.id.btnFilterAvailability);
                if (btn != null) btn.setText(currentAvailabilityFilter.equals("Todos") ? "Disponibilidad" : currentAvailabilityFilter);
                
                TabLayout tabs = getView().findViewById(R.id.tabLayout);
                if (tabs.getSelectedTabPosition() == 0) filterList("Solicitudes");
                else filterList("Registrados");
            })
            .show();
    }

    private void fetchVolunteers() {
        cuatrovientos.voluntariado.network.ApiService apiService = 
            cuatrovientos.voluntariado.network.RetrofitClient.getClient().create(cuatrovientos.voluntariado.network.ApiService.class);

        apiService.getVolunteers().enqueue(new retrofit2.Callback<List<cuatrovientos.voluntariado.network.model.ApiVolunteer>>() {
            @Override
            public void onResponse(retrofit2.Call<List<cuatrovientos.voluntariado.network.model.ApiVolunteer>> call, retrofit2.Response<List<cuatrovientos.voluntariado.network.model.ApiVolunteer>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    masterList.clear();
                    for (cuatrovientos.voluntariado.network.model.ApiVolunteer apiVol : response.body()) {
                        String status = mapStatus(apiVol.getStatus());
                        String avatarPath = apiVol.getAvatar();
                        String avatarUrl = null;
                        if (avatarPath != null) {
                            if (avatarPath.startsWith("http")) avatarUrl = avatarPath;
                            else avatarUrl = "http://10.0.2.2:8000" + avatarPath;
                        }
                        
                        java.util.List<String> availability = new java.util.ArrayList<>();
                        if (apiVol.getAvailability() != null) {
                            for (cuatrovientos.voluntariado.network.model.ApiAvailability av : apiVol.getAvailability()) {
                                availability.add(av.getDay() + ": " + av.getTime());
                            }
                        }

                        masterList.add(new Volunteer(
                            String.valueOf(apiVol.getId()),
                            apiVol.getName(),
                            apiVol.getSurname1(),
                            apiVol.getSurname2(),
                            apiVol.getEmail(),
                            apiVol.getPhone() != null ? apiVol.getPhone() : "",
                            apiVol.getDni(),
                            apiVol.getDateOfBirth() != null ? apiVol.getDateOfBirth() : "",
                            apiVol.getDescription(),
                            "Voluntario",
                            apiVol.getPreferences(),
                            status,
                            avatarUrl,
                            apiVol.getCourse(),
                            availability
                        ));
                    }
                    if (getView() != null) {
                        TabLayout tabLayout = getView().findViewById(R.id.tabLayout);
                        if (tabLayout != null) {
                             if (tabLayout.getSelectedTabPosition() == 0) filterList("Solicitudes");
                             else filterList("Registrados");
                        }
                    }
                } else {
                    android.util.Log.e("VolunteersFragment", "Error fetching: " + response.code());
                }
            }

            @Override
            public void onFailure(retrofit2.Call<List<cuatrovientos.voluntariado.network.model.ApiVolunteer>> call, Throwable t) {
                android.util.Log.e("VolunteersFragment", "Network error: " + t.getMessage());
            }
        });
    }

    private String mapStatus(String backendStatus) {
        if (backendStatus == null) return "Pending";
        switch (backendStatus.toUpperCase()) {
            case "ACTIVO": return "Active";
            case "PENDIENTE": return "Pending";
            case "SUSPENDIDO": return "Suspended";
            default: return "Pending";
        }
    }

    private void filterList(String tabName) {
        List<Volunteer> filteredList = new ArrayList<>();
        if (masterList == null) return;

        for (Volunteer v : masterList) {
            boolean matchesTab = false;
            if (tabName.equals("Solicitudes")) {
                 if (v.getStatus().equals("Pending")) matchesTab = true;
            } else {
                 if (v.getStatus().equals("Active") || v.getStatus().equals("Suspended")) matchesTab = true;
            }
            
            boolean matchesCourse = true;
            if (!currentCourseFilter.equals("Todos")) {
                 if (v.getCourse() == null || !v.getCourse().equalsIgnoreCase(currentCourseFilter)) matchesCourse = false;
            }

            boolean matchesStatus = true;
            if (!tabName.equals("Solicitudes") && !currentStatusFilter.equals("Todos")) {
                 if (!v.getStatus().equalsIgnoreCase(currentStatusFilter)) matchesStatus = false;
            }

            boolean matchesPreference = true;
            if (!currentPreferenceFilter.equals("Todos")) {
                matchesPreference = false;
                if (v.getPreferences() != null) {
                    for (String pref : v.getPreferences()) {
                        if (pref.equalsIgnoreCase(currentPreferenceFilter)) {
                            matchesPreference = true;
                            break;
                        }
                    }
                }
            }

            boolean matchesAvailability = true;
            if (!currentAvailabilityFilter.equals("Todos")) {
                matchesAvailability = false;
                if (v.getAvailability() != null) {
                    for (String avString : v.getAvailability()) {
                        if (avString.toUpperCase().contains(currentAvailabilityFilter.toUpperCase())) {
                            matchesAvailability = true;
                             break;
                        }
                    }
                }
            }

            if (matchesTab && matchesCourse && matchesStatus && matchesPreference && matchesAvailability) {
                if (currentSearchQuery.isEmpty()) {
                    filteredList.add(v);
                } else {
                    String query = currentSearchQuery;
                    boolean matchesSearch = false;
                    String fullName = v.getName() + " " + (v.getSurname1() != null ? v.getSurname1() : "") + " " + (v.getSurname2() != null ? v.getSurname2() : "");
                    
                    if (cuatrovientos.voluntariado.utils.SearchUtils.matches(fullName, query)) matchesSearch = true;
                    if (cuatrovientos.voluntariado.utils.SearchUtils.matches(v.getEmail(), query)) matchesSearch = true;
                    if (v.getDni() != null && cuatrovientos.voluntariado.utils.SearchUtils.matches(v.getDni(), query)) matchesSearch = true;
                    
                    if (matchesSearch) filteredList.add(v);
                }
            }
        }

        Collections.sort(filteredList, (v1, v2) -> {
            String name1 = (v1.getName() + " " + (v1.getSurname1() != null ? v1.getSurname1() : "")).toLowerCase();
            String name2 = (v2.getName() + " " + (v2.getSurname1() != null ? v2.getSurname1() : "")).toLowerCase();
            if (isAscending) return name1.compareTo(name2);
            else return name2.compareTo(name1);
        });

        if (adapter != null) adapter.updateList(filteredList);

        if (getView() != null) {
            RecyclerView recyclerView = getView().findViewById(R.id.recyclerVolunteers);
            android.widget.LinearLayout emptyView = getView().findViewById(R.id.emptyVolunteers);

            if (filteredList.isEmpty()) {
                recyclerView.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);
            } else {
                recyclerView.setVisibility(View.VISIBLE);
                emptyView.setVisibility(View.GONE);
            }
        }
    }
}