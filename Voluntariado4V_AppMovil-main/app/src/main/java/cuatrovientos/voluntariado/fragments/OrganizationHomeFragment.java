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

public class OrganizationHomeFragment extends Fragment {

    private RecyclerView rvMyActivities;
    private ActivitiesAdapter adapter;

    private android.widget.LinearLayout emptyStateView;

    private List<VolunteerActivity> masterList = new ArrayList<>();
    private String currentSearchQuery = "";

    private String currentTypeFilter = "Todos";
    private String currentOdsFilter = "Todos";
    private String currentStatusFilter = "Todos";
    private String currentCapacityFilter = "Todos";
    private String currentSortOption = "NameAsc";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_organization_dashboard, container, false); 

        rvMyActivities = view.findViewById(R.id.rvMyActivities);
        emptyStateView = view.findViewById(R.id.emptyStateView);
        rvMyActivities.setLayoutManager(new LinearLayoutManager(getContext()));
        
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

        android.widget.EditText etSearchOrgDash = view.findViewById(R.id.etSearchOrgDash);
        etSearchOrgDash.addTextChangedListener(new android.text.TextWatcher() {
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

        android.content.SharedPreferences prefs = getActivity().getSharedPreferences("UserSession", android.content.Context.MODE_PRIVATE);
        String userId = prefs.getString("USER_ID", null);

        if (userId != null) {
            loadActivities(userId);
        } else {
             android.widget.Toast.makeText(getContext(), "Error: No se encontró sesión activa", android.widget.Toast.LENGTH_SHORT).show();
             showEmptyState(true);
        }

        adapter = new ActivitiesAdapter(new ArrayList<>(), true);
        rvMyActivities.setAdapter(adapter);

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
        String[] statuses = {"Todos", "InProgress", "Active", "Pending"};
        String[] displayStatuses = {"Todos", "En Progreso", "Activa", "Pendiente"};
        
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
        if (masterList == null) return;
        java.util.Set<String> typesSet = new java.util.HashSet<>();
        typesSet.add("Todos");
        for (VolunteerActivity act : masterList) {
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

    private void showEmptyState(boolean show) {
        if (show) {
            rvMyActivities.setVisibility(View.GONE);
            emptyStateView.setVisibility(View.VISIBLE);
        } else {
            rvMyActivities.setVisibility(View.VISIBLE);
            emptyStateView.setVisibility(View.GONE);
        }
    }

    private void filterList(String query) {
        List<VolunteerActivity> filteredList = new ArrayList<>();
        String q = query.toLowerCase();

        for (VolunteerActivity act : masterList) {
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
                if (cuatrovientos.voluntariado.utils.SearchUtils.matches(act.getTitle(), q)) matchesSearch = true;
                if (cuatrovientos.voluntariado.utils.SearchUtils.matches(act.getCategory(), q)) matchesSearch = true;
                if (cuatrovientos.voluntariado.utils.SearchUtils.matches(act.getLocation(), q)) matchesSearch = true;
            }

            if (matchesType && matchesOds && matchesStatus && matchesCapacity && matchesSearch) {
                filteredList.add(act);
            }
        }
        
        java.util.Collections.sort(filteredList, (a1, a2) -> {
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

        if (adapter != null) {
            adapter.updateList(filteredList);
        }
        showEmptyState(filteredList.isEmpty());
    }

    private void loadActivities(String userId) {
        cuatrovientos.voluntariado.network.ApiService apiService = cuatrovientos.voluntariado.network.RetrofitClient.getClient().create(cuatrovientos.voluntariado.network.ApiService.class);
        apiService.getOrganizationActivities(userId).enqueue(new retrofit2.Callback<List<cuatrovientos.voluntariado.network.model.ApiActivity>>() {
            @Override
            public void onResponse(retrofit2.Call<List<cuatrovientos.voluntariado.network.model.ApiActivity>> call, retrofit2.Response<List<cuatrovientos.voluntariado.network.model.ApiActivity>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    masterList.clear();
                    List<VolunteerActivity> mappedList = new ArrayList<>();
                    for (cuatrovientos.voluntariado.network.model.ApiActivity apiAct : response.body()) {
                        
                        String status = apiAct.getStatus();
                        if ("Finished".equalsIgnoreCase(status) || "FINALIZADA".equalsIgnoreCase(status) || "Suspended".equalsIgnoreCase(status) || "SUSPENDIDOS".equalsIgnoreCase(status) || "SUSPENDIDA".equalsIgnoreCase(status)) {
                            continue;
                        }

                        VolunteerActivity volAct = cuatrovientos.voluntariado.utils.ActivityMapper.mapApiToModel(apiAct);
                        if (volAct != null) {
                            mappedList.add(volAct);
                        }
                    }
                    masterList.addAll(mappedList);
                    filterList(currentSearchQuery);

                    
                } else {
                    android.widget.Toast.makeText(getContext(), "Error al cargar actividades", android.widget.Toast.LENGTH_SHORT).show();
                    showEmptyState(true);
                }
            }

            @Override
            public void onFailure(retrofit2.Call<List<cuatrovientos.voluntariado.network.model.ApiActivity>> call, Throwable t) {
                if (getContext() != null) {
                    android.widget.Toast.makeText(getContext(), "Fallo de conexión", android.widget.Toast.LENGTH_SHORT).show();
                    showEmptyState(true);
                }
            }
        });

    }
}
