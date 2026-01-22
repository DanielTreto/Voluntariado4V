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
import android.widget.LinearLayout; // Añadir este import

import java.util.ArrayList;
import java.util.List;

import cuatrovientos.voluntariado.R;
import cuatrovientos.voluntariado.adapters.ActivitiesAdapter;
import cuatrovientos.voluntariado.model.VolunteerActivity;

/**
 * Fragmento que muestra el historial de actividades finalizadas del estudiante.
 */
public class StudentHistoryFragment extends Fragment {

    private RecyclerView rvHistory;
    private ActivitiesAdapter adapter;
    private LinearLayout emptyHistory;

    private List<VolunteerActivity> masterHistoryList = new ArrayList<>();
    private String currentSearchQuery = "";

    private String currentTypeFilter = "Todos";
    private String currentOdsFilter = "Todos";
    private String currentStatusFilter = "Todos";
    private String currentCapacityFilter = "Todos";
    private String currentSortOption = "NameAsc";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_history, container, false);

        rvHistory = view.findViewById(R.id.rvHistory);
        emptyHistory = view.findViewById(R.id.emptyHistory);
        rvHistory.setLayoutManager(new LinearLayoutManager(getContext()));
        
        rvHistory.setLayoutManager(new LinearLayoutManager(getContext()));
        
        // Botones de Filtro
        android.widget.Button btnFilterType = view.findViewById(R.id.btnFilterType);
        btnFilterType.setOnClickListener(v -> showTypeFilterDialog());

        android.widget.Button btnFilterOds = view.findViewById(R.id.btnFilterODS);
        btnFilterOds.setOnClickListener(v -> showOdsFilterDialog());

        android.widget.Button btnFilterStatus = view.findViewById(R.id.btnFilterStatus);
        if (btnFilterStatus != null) btnFilterStatus.setVisibility(View.GONE); // Ocultar Filtro de Estado

        android.widget.Button btnFilterCapacity = view.findViewById(R.id.btnFilterCapacity);
        btnFilterCapacity.setOnClickListener(v -> showCapacityFilterDialog());

        android.widget.Button btnSortActivities = view.findViewById(R.id.btnSortActivities);
        btnSortActivities.setOnClickListener(v -> toggleSort());

        // Configurar Búsqueda
        android.widget.EditText etSearchHistory = view.findViewById(R.id.etSearchHistory);
        etSearchHistory.addTextChangedListener(new android.text.TextWatcher() {
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

        // Adaptador vacío por defecto
        adapter = new ActivitiesAdapter(new ArrayList<>(), true);
        rvHistory.setAdapter(adapter);

        if (getArguments() != null) {
            loadData(getArguments().getString("USER_ID"));
        } else {
             loadData("1"); // Respaldo
        }

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
        // Excluir SUSPENDIDA (solo Finalizadas terminan aquí normalmente)
        String[] statuses = {"Todos", "Finished"};
        String[] displayStatuses = {"Todos", "Finalizada"};
        
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
        if (masterHistoryList == null) return;
        java.util.Set<String> typesSet = new java.util.HashSet<>();
        typesSet.add("Todos");
        for (VolunteerActivity act : masterHistoryList) {
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
        List<VolunteerActivity> filteredList = new ArrayList<>();
        String q = query.toLowerCase();

        for (VolunteerActivity act : masterHistoryList) {
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
                } catch (NumberFormatException e) { /* Handle error or ignore if ODS is not a number */ }
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

        if (filteredList.isEmpty()) {
            rvHistory.setVisibility(View.GONE);
            emptyHistory.setVisibility(View.VISIBLE);
        } else {
            rvHistory.setVisibility(View.VISIBLE);
            emptyHistory.setVisibility(View.GONE);
        }
    }

    private void loadData(String userId) {
        if (userId == null) return;

        cuatrovientos.voluntariado.network.ApiService apiService = 
             cuatrovientos.voluntariado.network.RetrofitClient.getClient().create(cuatrovientos.voluntariado.network.ApiService.class);

        apiService.getVolunteerActivities(userId).enqueue(new retrofit2.Callback<List<cuatrovientos.voluntariado.network.model.ApiActivity>>() {
            @Override
            public void onResponse(retrofit2.Call<List<cuatrovientos.voluntariado.network.model.ApiActivity>> call, retrofit2.Response<List<cuatrovientos.voluntariado.network.model.ApiActivity>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    masterHistoryList.clear();
                    List<VolunteerActivity> historyList = new ArrayList<>();
                    
                    for (cuatrovientos.voluntariado.network.model.ApiActivity apiAct : response.body()) {
                        VolunteerActivity va = cuatrovientos.voluntariado.utils.ActivityMapper.mapApiToModel(apiAct);
                        

                        
                        // Filtro: Solo actividades FINALIZADAS
                        // Usando estado normalizado de ActivityMapper ("Active", "Pending", "Finished", etc.)
                        if ("Finished".equalsIgnoreCase(va.getStatus())) {
                             historyList.add(va);
                        }
                    }
                    
                    masterHistoryList.addAll(historyList);
                    filterList(currentSearchQuery);
                }
            }
             @Override
            public void onFailure(retrofit2.Call<List<cuatrovientos.voluntariado.network.model.ApiActivity>> call, Throwable t) {}
        });
    }
}
