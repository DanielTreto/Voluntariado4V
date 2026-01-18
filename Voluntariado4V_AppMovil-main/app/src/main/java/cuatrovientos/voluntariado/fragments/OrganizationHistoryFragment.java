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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // We can reuse the student history layout as it's just a Title + RecyclerView
        View view = inflater.inflate(R.layout.fragment_student_history, container, false);

        rvHistory = view.findViewById(R.id.rvHistory);
        emptyStateView = view.findViewById(R.id.emptyHistory); // ID from fragment_student_history.xml
        rvHistory.setLayoutManager(new LinearLayoutManager(getContext()));

        // Retrieve User ID from SharedPreferences
        android.content.SharedPreferences prefs = getActivity().getSharedPreferences("UserSession", android.content.Context.MODE_PRIVATE);
        String userId = prefs.getString("USER_ID", null);

        // Initialize empty adapter
        adapter = new ActivitiesAdapter(new ArrayList<>(), true); // true = hide buttons (history mode)
        rvHistory.setAdapter(adapter);

        if (userId != null) {
            loadHistory(userId);
        } else {
             android.widget.Toast.makeText(getContext(), "Error: No se encontró sesión activa", android.widget.Toast.LENGTH_SHORT).show();
             showEmptyState(true);
        }

        return view;
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
                        if (apiAct.getVolunteers() != null) {
                            for (cuatrovientos.voluntariado.network.model.ApiVolunteer apiVol : apiAct.getVolunteers()) {
                                String volName = apiVol.getName();
                                if (apiVol.getSurname1() != null) volName += " " + apiVol.getSurname1();
                                if (apiVol.getSurname2() != null) volName += " " + apiVol.getSurname2();
                                
                                String volAvatar = null;
                                if (apiVol.getAvatar() != null) {
                                     volAvatar = apiVol.getAvatar().startsWith("http") ? apiVol.getAvatar() : "http://10.0.2.2:8000" + apiVol.getAvatar();
                                }

                                volunteerList.add(new cuatrovientos.voluntariado.model.Volunteer(
                                    apiVol.getId(),          // 1. id
                                    apiVol.getName(),        // 2. name
                                    apiVol.getSurname1(),    // 3. surname1
                                    apiVol.getSurname2(),    // 4. surname2
                                    apiVol.getEmail(),       // 5. email
                                    apiVol.getPhone(),       // 6. phone
                                    null,                    // 7. dni
                                    null,                    // 8. birthDate
                                    null,                    // 9. description
                                    "Voluntario",            // 10. role
                                    null,                    // 11. preferences
                                    "Active",                // 12. status
                                    volAvatar                // 13. avatarUrl
                                ));
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
                            volunteerList // Pass the list here
                        );
                        volAct.setId(apiAct.getId());
                        
                        mappedList.add(volAct);
                    }
                    adapter.updateList(mappedList);
                    showEmptyState(mappedList.isEmpty());
                    
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
