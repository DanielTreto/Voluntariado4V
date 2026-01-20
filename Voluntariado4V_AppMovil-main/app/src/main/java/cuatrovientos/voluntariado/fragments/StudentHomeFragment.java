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

import java.util.ArrayList;
import java.util.List;

import cuatrovientos.voluntariado.R;
import cuatrovientos.voluntariado.adapters.ActivitiesAdapter;
import cuatrovientos.voluntariado.model.VolunteerActivity;

public class StudentHomeFragment extends Fragment {

    private RecyclerView rvAvailableActivities;
    private RecyclerView rvMyActivities;
    private ActivitiesAdapter adapterAvailable;
    private ActivitiesAdapter adapterMy;
    private List<VolunteerActivity> myActivitiesList = new ArrayList<>();
    private List<VolunteerActivity> availableActivitiesList = new ArrayList<>();
    private List<VolunteerActivity> masterMyList = new ArrayList<>();
    private List<VolunteerActivity> masterAvailableList = new ArrayList<>();
    
    private android.widget.LinearLayout emptyMyActivities;
    private android.widget.LinearLayout emptyAvailableActivities;
    private String currentSearchQuery = "";

    private String currentTypeFilter = "Todos";
    private String currentOdsFilter = "Todos";
    private String currentStatusFilter = "Todos";
    private String currentCapacityFilter = "Todos";
    private String currentSortOption = "NameAsc";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_student_dashboard, container, false);

        rvAvailableActivities = view.findViewById(R.id.rvAvailableActivities);
        rvMyActivities = view.findViewById(R.id.rvMyActivities);
        emptyMyActivities = view.findViewById(R.id.emptyMyActivities);
        emptyAvailableActivities = view.findViewById(R.id.emptyAvailableActivities);
        
        rvAvailableActivities.setLayoutManager(new LinearLayoutManager(getContext()));
        rvMyActivities.setLayoutManager(new LinearLayoutManager(getContext()));

        // Filter Buttons
        android.widget.Button btnFilterType = view.findViewById(R.id.btnFilterType);
        btnFilterType.setOnClickListener(v -> showTypeFilterDialog());

        android.widget.Button btnFilterOds = view.findViewById(R.id.btnFilterODS);
        btnFilterOds.setOnClickListener(v -> showOdsFilterDialog());

        android.widget.Button btnFilterStatus = view.findViewById(R.id.btnFilterStatus);
        btnFilterStatus.setOnClickListener(v -> showStatusFilterDialog());

        android.widget.Button btnFilterCapacity = view.findViewById(R.id.btnFilterCapacity);
        btnFilterCapacity.setOnClickListener(v -> showCapacityFilterDialog());

        android.widget.Button btnSortActivities = view.findViewById(R.id.btnSortActivities);
        btnSortActivities.setOnClickListener(v -> toggleSort());

        // Setup Search
        android.widget.EditText etSearchStudentDash = view.findViewById(R.id.etSearchStudentDash);
        etSearchStudentDash.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentSearchQuery = s.toString();
                filterList(currentSearchQuery);
            }
            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });

        // Initialize adapters
        adapterAvailable = new ActivitiesAdapter(availableActivitiesList, true);
        rvAvailableActivities.setAdapter(adapterAvailable);
        
        adapterMy = new ActivitiesAdapter(myActivitiesList, true);
        rvMyActivities.setAdapter(adapterMy);

        loadData();

        return view;
    }

    private void toggleSort() {
        android.widget.Button btn = getView().findViewById(R.id.btnSortActivities);
        if (btn == null) return;
        
        if (currentSortOption.equals("NameAsc")) {
            currentSortOption = "NameDesc";
            btn.setText("Orden: Z-A");
        } else {
            currentSortOption = "NameAsc";
            btn.setText("Orden: A-Z");
        }
        filterList(currentSearchQuery);
    }

    private void showCapacityFilterDialog() {
        String[] options = {"Todos", "Con Plazas", "Llenas"};
        new android.app.AlertDialog.Builder(getContext())
            .setTitle("Filtrar por Plazas")
            .setItems(options, (dialog, which) -> {
                currentCapacityFilter = options[which];
                android.widget.Button btn = getView().findViewById(R.id.btnFilterCapacity);
                if (btn != null) btn.setText(currentCapacityFilter.equals("Todos") ? "Plazas" : currentCapacityFilter);
                filterList(currentSearchQuery);
            })
            .show();
    }
    
    private void showStatusFilterDialog() {
        // Exclude SUSPENDIDA
        String[] statuses = {"Todos", "Active", "Pending", "InProgress", "Finished"};
        String[] displayStatuses = {"Todos", "Activa", "Pendiente", "En Progreso", "Finalizada"};
        
        new android.app.AlertDialog.Builder(getContext())
            .setTitle("Filtrar por Estado")
            .setItems(displayStatuses, (dialog, which) -> {
                currentStatusFilter = statuses[which];
                android.widget.Button btn = getView().findViewById(R.id.btnFilterStatus);
                if (btn != null) btn.setText(currentStatusFilter.equals("Todos") ? "Estado" : displayStatuses[which]);
                filterList(currentSearchQuery);
            })
            .show();
    }

    private void showTypeFilterDialog() {
        if (masterAvailableList == null) return;
        java.util.Set<String> typesSet = new java.util.HashSet<>();
        typesSet.add("Todos");
        for (VolunteerActivity act : masterAvailableList) {
            if (act.getCategory() != null && !act.getCategory().isEmpty()) typesSet.add(act.getCategory());
        }
        
        List<String> typesList = new ArrayList<>(typesSet);
        java.util.Collections.sort(typesList);
        String[] typesArray = typesList.toArray(new String[0]);

        new android.app.AlertDialog.Builder(getContext())
            .setTitle("Filtrar por Tipo")
            .setItems(typesArray, (dialog, which) -> {
                currentTypeFilter = typesArray[which];
                android.widget.Button btn = getView().findViewById(R.id.btnFilterType);
                if (btn != null) btn.setText(currentTypeFilter.equals("Todos") ? "Tipo" : currentTypeFilter);
                filterList(currentSearchQuery);
            })
            .show();
    }

    private void showOdsFilterDialog() {
        String[] odsArray = new String[18];
        odsArray[0] = "Todos";
        for (int i = 1; i <= 17; i++) {
            odsArray[i] = String.valueOf(i);
        }

        new android.app.AlertDialog.Builder(getContext())
            .setTitle("Filtrar por ODS")
            .setItems(odsArray, (dialog, which) -> {
                currentOdsFilter = odsArray[which];
                android.widget.Button btn = getView().findViewById(R.id.btnFilterODS);
                if (btn != null) btn.setText(currentOdsFilter.equals("Todos") ? "ODS" : "ODS " + currentOdsFilter);
                filterList(currentSearchQuery);
            })
            .show();
    }

    private void filterList(String query) {
        String q = query.toLowerCase();

        // Helper filter function
        java.util.function.Predicate<VolunteerActivity> matchesFilters = act -> {
            boolean matchesType = true;
            if (!currentTypeFilter.equals("Todos")) {
                if (act.getCategory() == null || !act.getCategory().equalsIgnoreCase(currentTypeFilter)) matchesType = false;
            }

            boolean matchesOds = true;
            if (!currentOdsFilter.equals("Todos")) {
                boolean hasOds = false;
                try {
                    int filterId = Integer.parseInt(currentOdsFilter);
                    if (act.getOds() != null) {
                        for (cuatrovientos.voluntariado.model.Ods ods : act.getOds()) {
                            if (ods.getId() == filterId) { hasOds = true; break; }
                        }
                    }
                } catch (NumberFormatException e) { }
                if (!hasOds) matchesOds = false;
            }

            boolean matchesStatus = true;
            if (!currentStatusFilter.equals("Todos")) {
                if (!act.getStatus().equalsIgnoreCase(currentStatusFilter)) matchesStatus = false;
            }

            boolean matchesCapacity = true;
            if (!currentCapacityFilter.equals("Todos")) {
                 int currentParticipants = act.getParticipants() != null ? act.getParticipants().size() : 0;
                 if (currentCapacityFilter.equals("Con Plazas")) {
                     if (currentParticipants >= act.getMaxVolunteers()) matchesCapacity = false;
                 } else if (currentCapacityFilter.equals("Llenas")) {
                     if (currentParticipants < act.getMaxVolunteers()) matchesCapacity = false;
                 }
            }
            
            boolean matchesSearch = true;
            if (!q.isEmpty()) {
                matchesSearch = false;
                if (act.getTitle().toLowerCase().contains(q)) matchesSearch = true;
                if (act.getCategory().toLowerCase().contains(q)) matchesSearch = true;
                if (act.getLocation() != null && act.getLocation().toLowerCase().contains(q)) matchesSearch = true;
            }

            return matchesType && matchesOds && matchesStatus && matchesCapacity && matchesSearch;
        };

        // 1. Filter My Activities
        myActivitiesList.clear();
        for (VolunteerActivity act : masterMyList) {
            if (matchesFilters.test(act)) {
                myActivitiesList.add(act);
            }
        }
        sortList(myActivitiesList);
        adapterMy.notifyDataSetChanged();
        if (myActivitiesList.isEmpty()) {
            rvMyActivities.setVisibility(View.GONE);
            emptyMyActivities.setVisibility(View.VISIBLE);
        } else {
            rvMyActivities.setVisibility(View.VISIBLE);
            emptyMyActivities.setVisibility(View.GONE);
        }

        // 2. Filter Available Activities
        availableActivitiesList.clear();
        for (VolunteerActivity act : masterAvailableList) {
            if (matchesFilters.test(act)) {
                availableActivitiesList.add(act);
            }
        }
        sortList(availableActivitiesList);
        adapterAvailable.notifyDataSetChanged();
        if (availableActivitiesList.isEmpty()) {
            rvAvailableActivities.setVisibility(View.GONE);
            emptyAvailableActivities.setVisibility(View.VISIBLE);
        } else {
            rvAvailableActivities.setVisibility(View.VISIBLE);
            emptyAvailableActivities.setVisibility(View.GONE);
        }
    }

    private void sortList(List<VolunteerActivity> list) {
        java.util.Collections.sort(list, (a1, a2) -> {
            if (currentSortOption.startsWith("Date")) {
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
                java.util.Date d1, d2;
                try { d1 = sdf.parse(a1.getDate()); } catch(Exception e) { d1 = new java.util.Date(0); }
                try { d2 = sdf.parse(a2.getDate()); } catch(Exception e) { d2 = new java.util.Date(0); }
                
                if (currentSortOption.equals("DateAsc")) return d1.compareTo(d2);
                else return d2.compareTo(d1);
            } else {
                String title1 = a1.getTitle().toLowerCase();
                String title2 = a2.getTitle().toLowerCase();
                if (currentSortOption.equals("NameAsc")) return title1.compareTo(title2);
                else return title2.compareTo(title1);
            }
        });
    }

    private void loadData() {
        String userId = null;
        if (getArguments() != null) {
            userId = getArguments().getString("USER_ID");
        }
        if (userId == null) userId = "1"; 

        cuatrovientos.voluntariado.network.ApiService apiService = 
             cuatrovientos.voluntariado.network.RetrofitClient.getClient().create(cuatrovientos.voluntariado.network.ApiService.class);

        apiService.getVolunteerActivities(userId).enqueue(new retrofit2.Callback<List<cuatrovientos.voluntariado.network.model.ApiActivity>>() {
            @Override
            public void onResponse(retrofit2.Call<List<cuatrovientos.voluntariado.network.model.ApiActivity>> call, retrofit2.Response<List<cuatrovientos.voluntariado.network.model.ApiActivity>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    masterMyList.clear();
                    List<Integer> myActivityIds = new ArrayList<>();
                    
                    for (cuatrovientos.voluntariado.network.model.ApiActivity apiAct : response.body()) {
                        VolunteerActivity va = cuatrovientos.voluntariado.utils.ActivityMapper.mapApiToModel(apiAct);
                        if ("Finished".equalsIgnoreCase(va.getStatus())) continue;
                        // SUSPENDIDA is implicitly hidden by backend now, but good to keep robust logic
                        if ("Suspended".equalsIgnoreCase(va.getStatus()) || "SUSPENDIDA".equalsIgnoreCase(va.getStatus())) continue;

                        masterMyList.add(va);
                        myActivityIds.add(apiAct.getId());
                    }
                    filterList(currentSearchQuery);
                    fetchAvailableActivities(apiService, myActivityIds);
                }
            }
            @Override
            public void onFailure(retrofit2.Call<List<cuatrovientos.voluntariado.network.model.ApiActivity>> call, Throwable t) {
                 fetchAvailableActivities(apiService, new ArrayList<>());
            }
        });
    }

    private void fetchAvailableActivities(cuatrovientos.voluntariado.network.ApiService apiService, List<Integer> excludeIds) {
        apiService.getActivities().enqueue(new retrofit2.Callback<List<cuatrovientos.voluntariado.network.model.ApiActivity>>() {
            @Override
            public void onResponse(retrofit2.Call<List<cuatrovientos.voluntariado.network.model.ApiActivity>> call, retrofit2.Response<List<cuatrovientos.voluntariado.network.model.ApiActivity>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    masterAvailableList.clear();
                    for (cuatrovientos.voluntariado.network.model.ApiActivity apiAct : response.body()) {
                        if (excludeIds.contains(apiAct.getId())) continue;

                        String status = apiAct.getStatus();
                        // Backend might send SUSPENDIDA, but we double check constraints
                        if ("Suspended".equalsIgnoreCase(status) || "SUSPENDIDA".equalsIgnoreCase(status)) continue;
                        
                        if (status == null || 
                           (status.equalsIgnoreCase("ACTIVO") || status.equalsIgnoreCase("PENDIENTE") || status.equalsIgnoreCase("EN_PROGRESO") || status.equalsIgnoreCase("Active") || status.equalsIgnoreCase("Pending") || status.equalsIgnoreCase("InProgress"))) {
                           
                           masterAvailableList.add(cuatrovientos.voluntariado.utils.ActivityMapper.mapApiToModel(apiAct));
                        }
                    }
                    filterList(currentSearchQuery);
                }
            }
             @Override
            public void onFailure(retrofit2.Call<List<cuatrovientos.voluntariado.network.model.ApiActivity>> call, Throwable t) {}
        });
    }
}

