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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerOrganizations);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        
        // Setup Search Bar
        android.widget.EditText etSearch = view.findViewById(R.id.etSearchOrg);
        etSearch.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentSearchQuery = s.toString();
                TabLayout tabLayout = getView().findViewById(R.id.tabLayoutOrg);
                if (tabLayout.getSelectedTabPosition() == 0) {
                     filterList("Solicitudes");
                } else {
                     filterList("Registradas");
                }
            }
            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });

        masterList = new ArrayList<>();
        fetchOrganizations();

        adapter = new OrganizationsAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        // Cargar vista inicial (Solicitudes)
        filterList("Solicitudes");

        TabLayout tabLayout = view.findViewById(R.id.tabLayoutOrg);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    filterList("Solicitudes");
                } else {
                    filterList("Registradas");
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
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
                            if (avatarPath.startsWith("http")) {
                                avatarUrl = avatarPath;
                            } else {
                                avatarUrl = "http://10.0.2.2:8000" + avatarPath;
                            }
                        }

                        masterList.add(new Organization(
                            apiOrg.getId(),
                            apiOrg.getName(),
                            apiOrg.getEmail(),
                            apiOrg.getDescription(),
                            apiOrg.getType(),
                            apiOrg.getPhone(),
                            apiOrg.getSector(),
                            apiOrg.getScope(),
                            apiOrg.getContactPerson(),
                            "2023-01-01", 
                            volunteersCount,
                            status, // FIXED: Use mapped status field
                            avatarUrl
                        ));
                    }
                    TabLayout tabLayout = getView().findViewById(R.id.tabLayoutOrg);
                    if (tabLayout != null) {
                        if (tabLayout.getSelectedTabPosition() == 0) {
                            filterList("Solicitudes");
                        } else {
                            filterList("Registradas");
                        }
                    }
                } else {
                    android.util.Log.e("OrganizationsFragment", "Error fetching organizations: " + response.code());
                    if (getContext() != null) {
                        android.widget.Toast.makeText(getContext(), "Error al cargar organizaciones", android.widget.Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(retrofit2.Call<List<cuatrovientos.voluntariado.network.model.ApiOrganization>> call, Throwable t) {
                android.util.Log.e("OrganizationsFragment", "Network error: " + t.getMessage());
                if (getContext() != null) {
                    android.widget.Toast.makeText(getContext(), "Error de conexión", android.widget.Toast.LENGTH_SHORT).show();
                }
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
        List<Organization> filteredList = new ArrayList<>();
        if (masterList == null) return;

        for (Organization org : masterList) {
            boolean matchesTab = false;
            if (tabName.equals("Solicitudes")) {
                if (org.getStatus().equals("Pending")) {
                    matchesTab = true;
                }
            } else {
                if (org.getStatus().equals("Active")) {
                    matchesTab = true;
                }
            }
            
            if (matchesTab) {
                if (currentSearchQuery.isEmpty()) {
                    filteredList.add(org);
                } else {
                    String query = currentSearchQuery.toLowerCase();
                    boolean matchesSearch = false;
                    
                    if (org.getName().toLowerCase().contains(query)) matchesSearch = true;
                    if (org.getEmail().toLowerCase().contains(query)) matchesSearch = true;
                    if (org.getContactPerson() != null && org.getContactPerson().toLowerCase().contains(query)) matchesSearch = true;
                    
                    if (matchesSearch) {
                        filteredList.add(org);
                    }
                }
            }
        }
        if (adapter != null) {
            adapter.updateList(filteredList);
        }

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