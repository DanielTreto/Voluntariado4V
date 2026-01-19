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
import cuatrovientos.voluntariado.adapters.OrganizationsAdapter;
import cuatrovientos.voluntariado.model.Organization;

public class OrganizationsFragment extends Fragment {

    private List<Organization> masterList;
    private OrganizationsAdapter adapter;

    public OrganizationsFragment() {
        // Constructor vacío
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_organizations, container, false);
    }

    private String currentSearchQuery = "";
    private String currentTypeFilter = "Todos";
    private String currentStatusFilter = "Todos";
    private boolean isAscending = true;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerOrganizations);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        
        android.widget.EditText etSearch = view.findViewById(R.id.etSearchOrg);
        TabLayout tabLayout = view.findViewById(R.id.tabLayoutOrg);
        
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

        android.widget.Button btnFilterStatus = view.findViewById(R.id.btnFilterStatus);
        btnFilterStatus.setOnClickListener(v -> showStatusFilterDialog());

        android.widget.Button btnSortOrganizations = view.findViewById(R.id.btnSortOrganizations);
        btnSortOrganizations.setOnClickListener(v -> {
            isAscending = !isAscending;
            btnSortOrganizations.setText(isAscending ? "Orden: A-Z" : "Orden: Z-A");
            if (tabLayout.getSelectedTabPosition() == 0) filterList("Solicitudes");
            else filterList("Registradas");
        });

        masterList = new ArrayList<>();
        fetchOrganizations();

        adapter = new OrganizationsAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        filterList("Solicitudes");

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) filterList("Solicitudes");
                else filterList("Registradas");
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void showStatusFilterDialog() {
        String[] statuses = {"Todos", "Active", "Suspended"};
        String[] displayStatuses = {"Todos", "Activas", "Suspendidas"};
        
        new android.app.AlertDialog.Builder(getContext())
            .setTitle("Filtrar por Estado")
            .setItems(displayStatuses, (dialog, which) -> {
                currentStatusFilter = statuses[which];
                android.widget.Button btn = getView().findViewById(R.id.btnFilterStatus);
                if (btn != null) btn.setText(currentStatusFilter.equals("Todos") ? "Estado" : displayStatuses[which]);
                
                TabLayout tabs = getView().findViewById(R.id.tabLayoutOrg);
                if (tabs.getSelectedTabPosition() == 0) filterList("Solicitudes");
                else filterList("Registradas");
            })
            .show();
    }

    private void showTypeFilterDialog() {
        if (masterList == null) return;
        java.util.Set<String> typesSet = new java.util.HashSet<>();
        typesSet.add("Todos");
        for (Organization org : masterList) {
            if (org.getType() != null && !org.getType().isEmpty()) typesSet.add(org.getType());
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
                TabLayout tabs = getView().findViewById(R.id.tabLayoutOrg);
                if (tabs.getSelectedTabPosition() == 0) filterList("Solicitudes");
                else filterList("Registradas");
            })
            .show();
    }

    private void fetchOrganizations() {
        cuatrovientos.voluntariado.network.ApiService apiService = 
            cuatrovientos.voluntariado.network.RetrofitClient.getClient().create(cuatrovientos.voluntariado.network.ApiService.class);

        apiService.getOrganizations().enqueue(new retrofit2.Callback<List<cuatrovientos.voluntariado.network.model.ApiOrganization>>() {
            @Override
            public void onResponse(retrofit2.Call<List<cuatrovientos.voluntariado.network.model.ApiOrganization>> call, retrofit2.Response<List<cuatrovientos.voluntariado.network.model.ApiOrganization>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    masterList.clear();
                    for (cuatrovientos.voluntariado.network.model.ApiOrganization apiOrg : response.body()) {
                        String status = mapStatus(apiOrg.getStatus());
                        String volunteersCount = apiOrg.getDescription() != null && apiOrg.getDescription().isEmpty() ? "0" : "5"; 
                        String avatarPath = apiOrg.getAvatar();
                        String avatarUrl = null;
                        if (avatarPath != null) {
                            if (avatarPath.startsWith("http")) avatarUrl = avatarPath;
                            else avatarUrl = "http://10.0.2.2:8000" + avatarPath;
                        }

                        masterList.add(new Organization(
                            apiOrg.getId(),
                            apiOrg.getName(),
                            apiOrg.getEmail(),
                            apiOrg.getDescription(),
                            apiOrg.getType(),
                            apiOrg.getPhone() != null ? apiOrg.getPhone() : "",
                            apiOrg.getSector(),
                            apiOrg.getScope(),
                            apiOrg.getContactPerson(),
                            "2023-01-01",
                            volunteersCount,
                            status,
                            avatarUrl
                        ));
                    }
                    if (getView() != null) {
                        TabLayout tabLayout = getView().findViewById(R.id.tabLayoutOrg);
                        if (tabLayout != null) {
                            if (tabLayout.getSelectedTabPosition() == 0) filterList("Solicitudes");
                            else filterList("Registradas");
                        }
                    }
                } else {
                    android.util.Log.e("OrganizationsFragment", "Error fetching organizations: " + response.code());
                }
            }

            @Override
            public void onFailure(retrofit2.Call<List<cuatrovientos.voluntariado.network.model.ApiOrganization>> call, Throwable t) {
                android.util.Log.e("OrganizationsFragment", "Network error: " + t.getMessage());
            }
        });
    }

    private String mapStatus(String backendStatus) {
        if (backendStatus == null) return "Pending";
        switch (backendStatus.toUpperCase()) {
            case "ACTIVO": return "Active";
            case "PENDIENTE": return "Pending";
            case "SUSPENDIDO": 
            case "SUSPENDIDA": return "Suspended";
            default: return "Pending";
        }
    }

    private void filterList(String tabName) {
        List<Organization> filteredList = new ArrayList<>();
        if (masterList == null) return;

        for (Organization org : masterList) {
            boolean matchesTab = false;
            // 1. Tab Status
            if (tabName.equals("Solicitudes")) {
                if (org.getStatus().equals("Pending")) matchesTab = true;
            } else {
                // Modificado: Incluir "Suspended" en la lista de Registradas
                if (org.getStatus().equals("Active") || org.getStatus().equals("Suspended")) matchesTab = true;
            }
            
            // 2. Type Filter
            boolean matchesType = true;
            if (!currentTypeFilter.equals("Todos")) {
                if (org.getType() == null || !org.getType().equalsIgnoreCase(currentTypeFilter)) matchesType = false;
            }

            // 3. Status Filter
            boolean matchesStatus = true;
            if (!tabName.equals("Solicitudes") && !currentStatusFilter.equals("Todos")) {
                 if (!org.getStatus().equalsIgnoreCase(currentStatusFilter)) matchesStatus = false;
            }

            // 4. Search Query
            if (matchesTab && matchesType && matchesStatus) {
                if (currentSearchQuery.isEmpty()) {
                    filteredList.add(org);
                } else {
                    String query = currentSearchQuery.toLowerCase();
                    boolean matchesSearch = false;
                    
                    if (org.getName().toLowerCase().contains(query)) matchesSearch = true;
                    if (org.getEmail().toLowerCase().contains(query)) matchesSearch = true;
                    if (org.getContactPerson() != null && org.getContactPerson().toLowerCase().contains(query)) matchesSearch = true;
                    
                    if (matchesSearch) filteredList.add(org);
                }
            }
        }
        
        Collections.sort(filteredList, (o1, o2) -> {
            String name1 = o1.getName().toLowerCase();
            String name2 = o2.getName().toLowerCase();
            if (isAscending) return name1.compareTo(name2);
            else return name2.compareTo(name1);
        });

        if (adapter != null) adapter.updateList(filteredList);

        RecyclerView recyclerView = getView().findViewById(R.id.recyclerOrganizations);
        android.widget.LinearLayout emptyView = getView().findViewById(R.id.emptyOrganizations);

        if (filteredList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }
}