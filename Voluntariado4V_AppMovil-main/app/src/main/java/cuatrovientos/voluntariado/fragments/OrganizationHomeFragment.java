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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_organization_dashboard, container, false); // Reusing layout

        rvMyActivities = view.findViewById(R.id.rvMyActivities);
        emptyStateView = view.findViewById(R.id.emptyStateView);
        rvMyActivities.setLayoutManager(new LinearLayoutManager(getContext()));
        
        // Setup Search
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

        // Retrieve User ID from SharedPreferences
        android.content.SharedPreferences prefs = getActivity().getSharedPreferences("UserSession", android.content.Context.MODE_PRIVATE);
        String userId = prefs.getString("USER_ID", null);

        if (userId != null) {
            loadActivities(userId);
        } else {
             // Handle error or redirect to login
             android.widget.Toast.makeText(getContext(), "Error: No se encontró sesión activa", android.widget.Toast.LENGTH_SHORT).show();
             showEmptyState(true);
        }

        // Initialize empty adapter
        adapter = new ActivitiesAdapter(new ArrayList<>(), false);
        rvMyActivities.setAdapter(adapter);

        return view;
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
        if (query.isEmpty()) {
            filteredList.addAll(masterList);
        } else {
            String q = query.toLowerCase();
            for (VolunteerActivity act : masterList) {
                if (act.getTitle().toLowerCase().contains(q) || 
                    act.getCategory().toLowerCase().contains(q) ||
                    (act.getLocation() != null && act.getLocation().toLowerCase().contains(q))) {
                    filteredList.add(act);
                }
            }
        }
        
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
                        // Filter for Active, Pending, InProgress
                        if ("Finished".equalsIgnoreCase(status) || "FINALIZADA".equalsIgnoreCase(status) || "Suspended".equalsIgnoreCase(status) || "SUSPENDIDO".equalsIgnoreCase(status)) {
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
