package cuatrovientos.voluntariado.fragments;

import android.graphics.Color;
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
import cuatrovientos.voluntariado.adapters.ActivitiesAdapter;
import cuatrovientos.voluntariado.model.VolunteerActivity;

public class ActivitiesFragment extends Fragment {

    private List<VolunteerActivity> masterList;
    private ActivitiesAdapter adapter;

    public ActivitiesFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_activities, container, false);
    }

    private String currentSearchQuery = "";
    private String currentTypeFilter = "Todos";
    private String currentOdsFilter = "Todos";
    private String currentStatusFilter = "Todos";
    private String currentCapacityFilter = "Todos";
    private String currentSortOption = "NameAsc"; // NameAsc, NameDesc, DateAsc, DateDesc

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerActivities);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        
        android.widget.EditText etSearch = view.findViewById(R.id.etSearchAct);
        TabLayout tabLayout = view.findViewById(R.id.tabLayoutAct);

        etSearch.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentSearchQuery = s.toString();
                if (tabLayout.getSelectedTabPosition() == 0) filterList("Solicitudes");
                else filterList("Registradas");
            }
            @Override public void afterTextChanged(android.text.Editable s) {}
        });

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

        masterList = new ArrayList<>();
        fetchActivities();

        adapter = new ActivitiesAdapter(new ArrayList<>(), true);
        recyclerView.setAdapter(adapter);
        filterList("Solicitudes");

        if (tabLayout.getTabAt(0) != null) tabLayout.getTabAt(0).select();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) filterList("Solicitudes");
                else filterList("Registradas");
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });
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
        
        TabLayout tabs = getView().findViewById(R.id.tabLayoutAct);
        if (tabs != null && tabs.getSelectedTabPosition() == 0) filterList("Solicitudes");
        else filterList("Registradas");
    }

    private void showCapacityFilterDialog() {
        String[] options = {"Todos", "Con Plazas", "Llenas"};
        new android.app.AlertDialog.Builder(getContext())
            .setTitle("Filtrar por Plazas")
            .setItems(options, (dialog, which) -> {
                currentCapacityFilter = options[which];
                android.widget.Button btn = getView().findViewById(R.id.btnFilterCapacity);
                if (btn != null) btn.setText(currentCapacityFilter.equals("Todos") ? "Plazas" : currentCapacityFilter);
                
                TabLayout tabs = getView().findViewById(R.id.tabLayoutAct);
                if (tabs.getSelectedTabPosition() == 0) filterList("Solicitudes");
                else filterList("Registradas");
            })
            .show();
    }

    private void showStatusFilterDialog() {
        String[] statuses = {"Todos", "Active", "Suspended", "Finished", "InProgress"};
        String[] displayStatuses = {"Todos", "Activas", "Suspendidas", "Finalizadas", "En Progreso"};
        
        new android.app.AlertDialog.Builder(getContext())
            .setTitle("Filtrar por Estado")
            .setItems(displayStatuses, (dialog, which) -> {
                currentStatusFilter = statuses[which];
                android.widget.Button btn = getView().findViewById(R.id.btnFilterStatus);
                if (btn != null) btn.setText(currentStatusFilter.equals("Todos") ? "Estado" : displayStatuses[which]);
                
                TabLayout tabs = getView().findViewById(R.id.tabLayoutAct);
                if (tabs.getSelectedTabPosition() == 0) filterList("Solicitudes");
                else filterList("Registradas");
            })
            .show();
    }

    private void showTypeFilterDialog() {
        if (masterList == null) return;
        java.util.Set<String> typesSet = new java.util.HashSet<>();
        typesSet.add("Todos");
        for (VolunteerActivity act : masterList) {
            if (act.getCategory() != null && !act.getCategory().isEmpty()) typesSet.add(act.getCategory());
        }
        
        List<String> typesList = new ArrayList<>(typesSet);
        Collections.sort(typesList);
        String[] typesArray = typesList.toArray(new String[0]);

        new android.app.AlertDialog.Builder(getContext())
            .setTitle("Filtrar por Tipo")
            .setItems(typesArray, (dialog, which) -> {
                currentTypeFilter = typesArray[which];
                android.widget.Button btn = getView().findViewById(R.id.btnFilterType);
                if (btn != null) btn.setText(currentTypeFilter.equals("Todos") ? "Tipo" : currentTypeFilter);
                TabLayout tabs = getView().findViewById(R.id.tabLayoutAct);
                if (tabs.getSelectedTabPosition() == 0) filterList("Solicitudes");
                else filterList("Registradas");
            })
            .show();
    }

    private void showOdsFilterDialog() {
        // Hardcoded ODS 1-17
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
                TabLayout tabs = getView().findViewById(R.id.tabLayoutAct);
                if (tabs.getSelectedTabPosition() == 0) filterList("Solicitudes");
                else filterList("Registradas");
            })
            .show();
    }
    
    private void fetchActivities() {
        cuatrovientos.voluntariado.network.ApiService apiService = 
            cuatrovientos.voluntariado.network.RetrofitClient.getClient().create(cuatrovientos.voluntariado.network.ApiService.class);

        apiService.getActivities().enqueue(new retrofit2.Callback<List<cuatrovientos.voluntariado.network.model.ApiActivity>>() {
            @Override
            public void onResponse(retrofit2.Call<List<cuatrovientos.voluntariado.network.model.ApiActivity>> call, retrofit2.Response<List<cuatrovientos.voluntariado.network.model.ApiActivity>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    masterList.clear();
                    for (cuatrovientos.voluntariado.network.model.ApiActivity apiAct : response.body()) {
                        masterList.add(cuatrovientos.voluntariado.utils.ActivityMapper.mapApiToModel(apiAct));
                    }
                    if (getView() != null) {
                        TabLayout tabLayout = getView().findViewById(R.id.tabLayoutAct);
                        if (tabLayout != null) {
                             if (tabLayout.getSelectedTabPosition() == 0) filterList("Solicitudes");
                             else filterList("Registradas");
                        }
                    }
                } else {
                    android.util.Log.e("ActivitiesFragment", "Error fetching activities: " + response.code());
                }
            }

            @Override
            public void onFailure(retrofit2.Call<List<cuatrovientos.voluntariado.network.model.ApiActivity>> call, Throwable t) {
                android.util.Log.e("ActivitiesFragment", "Network error: " + t.getMessage());
            }
        });
    }

    private void filterList(String tabName) {
        List<VolunteerActivity> filteredList = new ArrayList<>();
        
        if (masterList == null) return;

        for (VolunteerActivity act : masterList) {
            boolean matchesTab = false;
            // 1. Tab Logic
            if (tabName.equals("Solicitudes")) {
                if (act.getStatus().equals("Pending")) matchesTab = true;
            } else {
                if (act.getStatus().equals("Active") || act.getStatus().equals("Finished") || act.getStatus().equals("Suspended") || act.getStatus().equals("InProgress")) {
                    matchesTab = true;
                }
            }
            
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
                            if (ods.getId() == filterId) {
                                 hasOds = true;
                                 break;
                            }
                        }
                    }
                } catch (NumberFormatException e) { }
                if (!hasOds) matchesOds = false;
            }

            // 4. Status Filter
            boolean matchesStatus = true;
            if (!tabName.equals("Solicitudes") && !currentStatusFilter.equals("Todos")) {
                 if (!act.getStatus().equalsIgnoreCase(currentStatusFilter)) matchesStatus = false;
            }

            // 5. Capacity Filter
            boolean matchesCapacity = true;
            if (!currentCapacityFilter.equals("Todos")) {
                 int currentParticipants = act.getParticipants() != null ? act.getParticipants().size() : 0;
                 if (currentCapacityFilter.equals("Con Plazas")) {
                     if (currentParticipants >= act.getMaxVolunteers()) matchesCapacity = false;
                 } else if (currentCapacityFilter.equals("Llenas")) {
                     if (currentParticipants < act.getMaxVolunteers()) matchesCapacity = false;
                 }
            }

            if (matchesTab && matchesType && matchesOds && matchesStatus && matchesCapacity) {
                if (currentSearchQuery.isEmpty()) {
                    filteredList.add(act);
                } else {
                    String query = currentSearchQuery.toLowerCase();
                    boolean matchesSearch = false;
                    if (act.getTitle().toLowerCase().contains(query)) matchesSearch = true;
                    if (act.getCategory().toLowerCase().contains(query)) matchesSearch = true;
                    if (matchesSearch) filteredList.add(act);
                }
            }
        }

        Collections.sort(filteredList, (a1, a2) -> {
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

        if (adapter != null) adapter.updateList(filteredList);

        if (getView() != null) {
            RecyclerView recyclerView = getView().findViewById(R.id.recyclerActivities);
            android.widget.LinearLayout emptyView = getView().findViewById(R.id.emptyActivities);

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
