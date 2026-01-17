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
import android.widget.LinearLayout; // Add this import

import java.util.ArrayList;
import java.util.List;

import cuatrovientos.voluntariado.R;
import cuatrovientos.voluntariado.adapters.ActivitiesAdapter;
import cuatrovientos.voluntariado.model.VolunteerActivity;

public class StudentHistoryFragment extends Fragment {

    private RecyclerView rvHistory;
    private ActivitiesAdapter adapter;
    private LinearLayout emptyHistory;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_history, container, false);

        rvHistory = view.findViewById(R.id.rvHistory);
        emptyHistory = view.findViewById(R.id.emptyHistory);
        rvHistory.setLayoutManager(new LinearLayoutManager(getContext()));

        // Default empty adapter
        adapter = new ActivitiesAdapter(new ArrayList<>(), true);
        rvHistory.setAdapter(adapter);

        if (getArguments() != null) {
            loadData(getArguments().getString("USER_ID"));
        } else {
             loadData("1"); // Fallback
        }

        return view;
    }

    private void loadData(String userId) {
        if (userId == null) return;

        cuatrovientos.voluntariado.network.ApiService apiService = 
             cuatrovientos.voluntariado.network.RetrofitClient.getClient().create(cuatrovientos.voluntariado.network.ApiService.class);

        apiService.getVolunteerActivities(userId).enqueue(new retrofit2.Callback<List<cuatrovientos.voluntariado.network.model.ApiActivity>>() {
            @Override
            public void onResponse(retrofit2.Call<List<cuatrovientos.voluntariado.network.model.ApiActivity>> call, retrofit2.Response<List<cuatrovientos.voluntariado.network.model.ApiActivity>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<VolunteerActivity> historyList = new ArrayList<>();
                    
                    for (cuatrovientos.voluntariado.network.model.ApiActivity apiAct : response.body()) {
                        String status = apiAct.getStatus() != null ? apiAct.getStatus() : "";
                        // Filter: Only FINISHED activities
                        if (status.equalsIgnoreCase("FINALIZADA")) {
                             historyList.add(mapApiToModel(apiAct));
                        }
                    }
                    
                    adapter = new ActivitiesAdapter(historyList, true);
                    rvHistory.setAdapter(adapter);

                    if (historyList.isEmpty()) {
                        rvHistory.setVisibility(View.GONE);
                        emptyHistory.setVisibility(View.VISIBLE);
                    } else {
                        rvHistory.setVisibility(View.VISIBLE);
                        emptyHistory.setVisibility(View.GONE);
                    }
                }
            }
             @Override
            public void onFailure(retrofit2.Call<List<cuatrovientos.voluntariado.network.model.ApiActivity>> call, Throwable t) {}
        });
    }

    private VolunteerActivity mapApiToModel(cuatrovientos.voluntariado.network.model.ApiActivity apiAct) {
        String rawStatus = apiAct.getStatus() != null ? apiAct.getStatus() : "ACTIVO";
        String status = "Finished"; 
        
        // Color Logic based on Type (Category)
        String type = apiAct.getType() != null ? apiAct.getType() : "General";
        int color;
        // Default colors
        if (type.equalsIgnoreCase("Social")) color = 0xFF2196F3;       // Blue
        else if (type.equalsIgnoreCase("Medio Ambiente")) color = 0xFF4CAF50; // Green
        else if (type.equalsIgnoreCase("Deporte")) color = 0xFFFF9800;        // Orange
        else if (type.equalsIgnoreCase("Educación")) color = 0xFFE91E63;      // Pink
        else if (type.equalsIgnoreCase("Salud")) color = 0xFFF44336;          // Red
        else if (type.equalsIgnoreCase("Cultura")) color = 0xFF9C27B0;        // Purple
        else {
             type = "General"; // Default type if unknown or null
             color = 0xFF757575; // Grey
        }

        String description = apiAct.getDescription() != null ? apiAct.getDescription() : "";
        String imageUrl = apiAct.getImagen();
        if (imageUrl != null && !imageUrl.startsWith("http")) {
            imageUrl = "http://10.0.2.2:8000" + imageUrl;
        }
                List<cuatrovientos.voluntariado.model.Volunteer> participants = new ArrayList<>();
                if (apiAct.getVolunteers() != null) {
                    for (cuatrovientos.voluntariado.network.model.ApiVolunteer apiVol : apiAct.getVolunteers()) {
                         String avatarUrl = apiVol.getAvatar();
                         if (avatarUrl != null && !avatarUrl.startsWith("http")) {
                             avatarUrl = "http://10.0.2.2:8000/" + avatarUrl;
                         }
                         participants.add(new cuatrovientos.voluntariado.model.Volunteer(apiVol.getId(), apiVol.getName(), avatarUrl));
                    }
                }

                String orgName = "Cuatrovientos";
                String orgAvatar = null;
                if (apiAct.getOrganization() != null) {
                    orgName = apiAct.getOrganization().getName();
                    String orgAvPath = apiAct.getOrganization().getAvatar();
                    if (orgAvPath != null && !orgAvPath.startsWith("http")) {
                        orgAvatar = "http://10.0.2.2:8000/" + orgAvPath;
                    } else {
                        orgAvatar = orgAvPath;
                    }
                }

                return new VolunteerActivity(
                        apiAct.getTitle(),
                        description,
                        apiAct.getLocation() != null ? apiAct.getLocation() : "Ubicación por definir", 
                        apiAct.getDate() != null ? apiAct.getDate() : "Fecha por definir", 
                        apiAct.getDuration() != null ? apiAct.getDuration() : "N/A",
                        apiAct.getEndDate(),
                        apiAct.getMaxVolunteers(),
                        type,
                        status,
                        orgName,
                        orgAvatar,
                        color,
                        imageUrl,
                        participants
                );
    }
}
