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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_organizations, container, false);
    }

    private String currentSearchQuery = "";
    private String currentStatusFilter = "Todos";
    private String currentScopeFilter = "Todos";
    private String currentActivityStatusFilter = "Todos";
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

        android.widget.Button btnFilterStatus = view.findViewById(R.id.btnFilterStatus);
        btnFilterStatus.setOnClickListener(v -> showStatusFilterDialog());

        android.widget.Button btnFilterScope = view.findViewById(R.id.btnFilterScope);
        btnFilterScope.setOnClickListener(v -> showScopeFilterDialog());

        android.widget.Button btnFilterActivityStatus = view.findViewById(R.id.btnFilterActivityStatus);
        btnFilterActivityStatus.setOnClickListener(v -> showActivityStatusFilterDialog());

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

    private void showScopeFilterDialog() {
        if (masterList == null) return;
        java.util.Set<String> scopeSet = new java.util.HashSet<>();
        scopeSet.add("Todos");
        for (Organization org : masterList) {
            if (org.getScope() != null && !org.getScope().isEmpty()) scopeSet.add(org.getScope());
        }
        
        List<String> scopeList = new ArrayList<>(scopeSet);
        Collections.sort(scopeList);
        String[] scopeArray = scopeList.toArray(new String[0]);

        new android.app.AlertDialog.Builder(getContext())
            .setTitle("Filtrar por Ámbito")
            .setItems(scopeArray, (dialog, which) -> {
                currentScopeFilter = scopeArray[which];
                android.widget.Button btn = getView().findViewById(R.id.btnFilterScope);
                if (btn != null) btn.setText(currentScopeFilter.equals("Todos") ? "Ámbito" : currentScopeFilter);
                TabLayout tabs = getView().findViewById(R.id.tabLayoutOrg);
                if (tabs.getSelectedTabPosition() == 0) filterList("Solicitudes");
                else filterList("Registradas");
            })
            .show();
    }

    private void showActivityStatusFilterDialog() {
        String[] statuses = {"Todos", "Con actividades pendientes", "Con actividades activas", "Con actividades en progreso", "Con actividades finalizadas", "Con actividades suspendidas"};
        
        new android.app.AlertDialog.Builder(getContext())
            .setTitle("Filtrar por Actividades")
            .setItems(statuses, (dialog, which) -> {
                currentActivityStatusFilter = statuses[which];
                android.widget.Button btn = getView().findViewById(R.id.btnFilterActivityStatus);
                if (btn != null) btn.setText(currentActivityStatusFilter.equals("Todos") ? "Actividades" : currentActivityStatusFilter);
                
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
            public void onResponse(retrofit2.Call<List<cuatrovientos.voluntariado.network.model.ApiOrganization>> callOrg, retrofit2.Response<List<cuatrovientos.voluntariado.network.model.ApiOrganization>> responseOrg) {
                if (responseOrg.isSuccessful() && responseOrg.body() != null) {
                    List<cuatrovientos.voluntariado.network.model.ApiOrganization> apiOrgs = responseOrg.body();
                    
                    apiService.getActivities().enqueue(new retrofit2.Callback<List<cuatrovientos.voluntariado.network.model.ApiActivity>>() {
                        @Override
                        public void onResponse(retrofit2.Call<List<cuatrovientos.voluntariado.network.model.ApiActivity>> callAct, retrofit2.Response<List<cuatrovientos.voluntariado.network.model.ApiActivity>> responseAct) {
                             java.util.Map<String, Integer> orgCounts = new java.util.HashMap<>();
                             java.util.Map<String, java.util.Set<String>> orgActivityStatuses = new java.util.HashMap<>();

                             if (responseAct.isSuccessful() && responseAct.body() != null) {
                                  for (cuatrovientos.voluntariado.network.model.ApiActivity act : responseAct.body()) {
                                       if (act.getOrganization() != null && act.getOrganization().getId() != null) {
                                            String orgId = act.getOrganization().getId();
                                            orgCounts.put(orgId, orgCounts.getOrDefault(orgId, 0) + 1);
                                            
                                            if (!orgActivityStatuses.containsKey(orgId)) {
                                                orgActivityStatuses.put(orgId, new java.util.HashSet<>());
                                            }
            
                                            if (act.getStatus() != null) {
                                                orgActivityStatuses.get(orgId).add(act.getStatus().toUpperCase());
                                            }
                                       }
                                  }
                             }

                             masterList.clear();
                             for (cuatrovientos.voluntariado.network.model.ApiOrganization apiOrg : apiOrgs) {
                                String status = mapStatus(apiOrg.getStatus());
                                String avatarPath = apiOrg.getAvatar();
                                String avatarUrl = null;
                                if (avatarPath != null) {
                                    if (avatarPath.startsWith("http")) avatarUrl = avatarPath;
                                    else avatarUrl = "http://10.0.2.2:8000" + avatarPath;
                                }

                                int count = orgCounts.getOrDefault(apiOrg.getId(), 0);
                                java.util.Set<String> activityStatuses = orgActivityStatuses.getOrDefault(apiOrg.getId(), new java.util.HashSet<>());

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
                                    String.valueOf(count), 
                                    status,
                                    avatarUrl,
                                    activityStatuses
                                ));
                            }

                            if (getView() != null) {
                                TabLayout tabLayout = getView().findViewById(R.id.tabLayoutOrg);
                                if (tabLayout != null) {
                                    if (tabLayout.getSelectedTabPosition() == 0) filterList("Solicitudes");
                                    else filterList("Registradas");
                                }
                            }
                        }

                        @Override
                        public void onFailure(retrofit2.Call<List<cuatrovientos.voluntariado.network.model.ApiActivity>> callAct, Throwable t) {
                             android.util.Log.e("OrganizationsFragment", "Error fetching activities for counts: " + t.getMessage());
                             masterList.clear();
                             for (cuatrovientos.voluntariado.network.model.ApiOrganization apiOrg : apiOrgs) {
                                String status = mapStatus(apiOrg.getStatus());
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
                                    "0",
                                    status,
                                    avatarUrl,
                                    new java.util.HashSet<>()
                                ));
                            }
                            if (getView() != null) filterList("Solicitudes");
                        }
                    });

                } else {
                    android.util.Log.e("OrganizationsFragment", "Error fetching organizations: " + responseOrg.code());
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
            if (tabName.equals("Solicitudes")) {
                if (org.getStatus().equals("Pending")) matchesTab = true;
            } else {
                if (org.getStatus().equals("Active") || org.getStatus().equals("Suspended")) matchesTab = true;
            }
            
            boolean matchesStatus = true;
            if (!tabName.equals("Solicitudes") && !currentStatusFilter.equals("Todos")) {
                 if (!org.getStatus().equalsIgnoreCase(currentStatusFilter)) matchesStatus = false;
            }

            boolean matchesScope = true;
            if (!currentScopeFilter.equals("Todos")) {
                if (org.getScope() == null || !org.getScope().equalsIgnoreCase(currentScopeFilter)) matchesScope = false;
            }

            boolean matchesActivityStatus = true;
            if (!currentActivityStatusFilter.equals("Todos")) {
                matchesActivityStatus = false;
                java.util.Set<String> statuses = org.getActivityStatuses();
                if (statuses != null) {
                    if (currentActivityStatusFilter.equals("Con actividades pendientes")) {
                        if (statuses.contains("PENDIENTE") || statuses.contains("PENDING")) matchesActivityStatus = true;
                    } else if (currentActivityStatusFilter.equals("Con actividades activas")) {
                        if (statuses.contains("ACTIVA") || statuses.contains("ACTIVO") || statuses.contains("ABIERTA") || statuses.contains("ACTIVE")) matchesActivityStatus = true;
                    } else if (currentActivityStatusFilter.equals("Con actividades en progreso")) {
                        if (statuses.contains("ENPROGRESO") || statuses.contains("EN_PROGRESO") || statuses.contains("INPROGRESS") || statuses.contains("IN_PROGRESS") || statuses.contains("EN CURSO")) matchesActivityStatus = true;
                    } else if (currentActivityStatusFilter.equals("Con actividades finalizadas")) {
                        if (statuses.contains("FINALIZADA") || statuses.contains("FINALIZADO") || statuses.contains("FINISHED")) matchesActivityStatus = true;
                    } else if (currentActivityStatusFilter.equals("Con actividades suspendidas")) {
                        if (statuses.contains("SUSPENDIDA") || statuses.contains("SUSPENDIDO") || statuses.contains("SUSPENDED")) matchesActivityStatus = true;
                    }
                }
            }

            if (matchesTab && matchesStatus && matchesScope && matchesActivityStatus) {
                if (currentSearchQuery.isEmpty()) {
                    filteredList.add(org);
                } else {
                    String query = currentSearchQuery;
                    boolean matchesSearch = false;
                    
                    if (cuatrovientos.voluntariado.utils.SearchUtils.matches(org.getName(), query)) matchesSearch = true;
                    if (cuatrovientos.voluntariado.utils.SearchUtils.matches(org.getEmail(), query)) matchesSearch = true;
                    if (cuatrovientos.voluntariado.utils.SearchUtils.matches(org.getContactPerson(), query)) matchesSearch = true;
                    
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

        if (getView() != null) {
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
}