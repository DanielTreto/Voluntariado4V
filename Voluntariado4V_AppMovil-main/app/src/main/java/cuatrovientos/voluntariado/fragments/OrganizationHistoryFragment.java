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

public class OrganizationHistoryFragment extends Fragment {

    private RecyclerView rvHistory;
    private ActivitiesAdapter adapter;
    private android.widget.LinearLayout emptyStateView;

    private List<VolunteerActivity> masterHistoryList = new ArrayList<>();
    private String currentSearchQuery = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // We can reuse the student history layout as it's just a Title + RecyclerView
        View view = inflater.inflate(R.layout.fragment_student_history, container, false);

        rvHistory = view.findViewById(R.id.rvHistory);
        emptyStateView = view.findViewById(R.id.emptyHistory); // ID from fragment_student_history.xml
        rvHistory.setLayoutManager(new LinearLayoutManager(getContext()));

        // Setup Search
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

        // Retrieve User ID from SharedPreferences
        android.content.SharedPreferences prefs = getActivity().getSharedPreferences("UserSession", android.content.Context.MODE_PRIVATE);
        String userId = prefs.getString("USER_ID", null);

        // Initialize empty adapter
        adapter = new ActivitiesAdapter(new ArrayList<>(), false); // false = org view (no buttons, no arrow)
        rvHistory.setAdapter(adapter);

        if (userId != null) {
            loadHistory(userId);
        } else {
             android.widget.Toast.makeText(getContext(), "Error: No se encontró sesión activa", android.widget.Toast.LENGTH_SHORT).show();
             showEmptyState(true);
        }

        return view;
    }

    private void filterList(String query) {
        List<VolunteerActivity> filteredList = new ArrayList<>();
        if (query.isEmpty()) {
            filteredList.addAll(masterHistoryList);
        } else {
            String q = query.toLowerCase();
            for (VolunteerActivity act : masterHistoryList) {
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

    private void showEmptyState(boolean show) {
        if (show) {
            rvHistory.setVisibility(View.GONE);
            emptyStateView.setVisibility(View.VISIBLE);
        } else {
            rvHistory.setVisibility(View.VISIBLE);
            emptyStateView.setVisibility(View.GONE);
        }
    }

    private void loadHistory(String userId) {
        cuatrovientos.voluntariado.network.ApiService apiService = cuatrovientos.voluntariado.network.RetrofitClient.getClient().create(cuatrovientos.voluntariado.network.ApiService.class);
        apiService.getOrganizationActivities(userId).enqueue(new retrofit2.Callback<List<cuatrovientos.voluntariado.network.model.ApiActivity>>() {
            @Override
            public void onResponse(retrofit2.Call<List<cuatrovientos.voluntariado.network.model.ApiActivity>> call, retrofit2.Response<List<cuatrovientos.voluntariado.network.model.ApiActivity>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    masterHistoryList.clear();
                    List<VolunteerActivity> mappedList = new ArrayList<>();
                    for (cuatrovientos.voluntariado.network.model.ApiActivity apiAct : response.body()) {
                        
                        String status = apiAct.getStatus();
                        // Filter for Finished, Suspended
                        if (!("Finished".equalsIgnoreCase(status) || "FINALIZADA".equalsIgnoreCase(status) || "Suspended".equalsIgnoreCase(status) || "SUSPENDIDO".equalsIgnoreCase(status))) {
                            continue;
                        }

                        String imageUrl = null;
                        if (apiAct.getImagen() != null) {
                             imageUrl = apiAct.getImagen().startsWith("http") ? apiAct.getImagen() : "http://10.0.2.2:8000" + apiAct.getImagen();
                        }
                        
                        // Map Volunteers if available
                        List<cuatrovientos.voluntariado.model.Volunteer> volunteerList = new ArrayList<>();

                        // Map ODS
                        List<cuatrovientos.voluntariado.model.Ods> odsList = new ArrayList<>();
                        if (apiAct.getOds() != null) {
                            for (cuatrovientos.voluntariado.network.model.ApiOds apiOds : apiAct.getOds()) {
                                odsList.add(new cuatrovientos.voluntariado.model.Ods(apiOds.getId(), apiOds.getDescription()));
                            }
                        }

                         VolunteerActivity volAct = new VolunteerActivity(
                            apiAct.getTitle(),
                            apiAct.getDescription(),
                            apiAct.getLocation(),
                            apiAct.getDate(),
                            apiAct.getDuration(),
                            apiAct.getEndDate(),
                            apiAct.getMaxVolunteers(),
                            apiAct.getType(),
                            apiAct.getStatus(),
                            "Mi Organización", 
                            null, 
                            android.graphics.Color.GRAY, // Gray for history
                            imageUrl,
                            volunteerList, // Pass the list here
                            odsList // Pass ODS list
                        );
                        volAct.setId(apiAct.getId());
                        
                        mappedList.add(volAct);
                    }
                    masterHistoryList.addAll(mappedList);
                    filterList(currentSearchQuery);
                    
                } else {
                    android.widget.Toast.makeText(getContext(), "Error al cargar historial", android.widget.Toast.LENGTH_SHORT).show();
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
