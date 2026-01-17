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

public class StudentHomeFragment extends Fragment {

    private RecyclerView rvAvailableActivities;
    private RecyclerView rvMyActivities;
    private ActivitiesAdapter adapterAvailable;
    private ActivitiesAdapter adapterMy;
    private List<VolunteerActivity> myActivitiesList = new ArrayList<>();
    private List<VolunteerActivity> availableActivitiesList = new ArrayList<>();
    private android.widget.LinearLayout emptyMyActivities;
    private android.widget.LinearLayout emptyAvailableActivities;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_student_dashboard, container, false);

        rvAvailableActivities = view.findViewById(R.id.rvAvailableActivities);
        rvMyActivities = view.findViewById(R.id.rvMyActivities);
        emptyMyActivities = view.findViewById(R.id.emptyMyActivities);
        emptyAvailableActivities = view.findViewById(R.id.emptyAvailableActivities);
        
        rvAvailableActivities.setLayoutManager(new LinearLayoutManager(getContext()));
        rvMyActivities.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize adapters
        adapterAvailable = new ActivitiesAdapter(availableActivitiesList, true);
        rvAvailableActivities.setAdapter(adapterAvailable);
        
        adapterMy = new ActivitiesAdapter(myActivitiesList, true);
        rvMyActivities.setAdapter(adapterMy);

        loadData();

        return view;
    }

    private void loadData() {
        String userId = null;
        if (getArguments() != null) {
            userId = getArguments().getString("USER_ID");
        }

        if (userId == null) userId = "1"; // Fallback for testing/dev if not passed

        cuatrovientos.voluntariado.network.ApiService apiService = 
             cuatrovientos.voluntariado.network.RetrofitClient.getClient().create(cuatrovientos.voluntariado.network.ApiService.class);

        // Fetch My Activities
        apiService.getVolunteerActivities(userId).enqueue(new retrofit2.Callback<List<cuatrovientos.voluntariado.network.model.ApiActivity>>() {
            @Override
            public void onResponse(retrofit2.Call<List<cuatrovientos.voluntariado.network.model.ApiActivity>> call, retrofit2.Response<List<cuatrovientos.voluntariado.network.model.ApiActivity>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    myActivitiesList.clear();
                    List<Integer> myActivityIds = new ArrayList<>();
                    
                    for (cuatrovientos.voluntariado.network.model.ApiActivity apiAct : response.body()) {
                        // Filter: Exclude FINISHED activities (They go to History)
                        String status = apiAct.getStatus() != null ? apiAct.getStatus() : "ACTIVO";
                        if (status.equalsIgnoreCase("FINALIZADA")) continue;

                        VolunteerActivity va = mapApiToModel(apiAct);
                        myActivitiesList.add(va);
                        myActivityIds.add(apiAct.getId());
                    }
                    adapterMy.notifyDataSetChanged();
                    
                    if (myActivitiesList.isEmpty()) {
                        rvMyActivities.setVisibility(View.GONE);
                        emptyMyActivities.setVisibility(View.VISIBLE);
                    } else {
                        rvMyActivities.setVisibility(View.VISIBLE);
                        emptyMyActivities.setVisibility(View.GONE);
                    }

                    // After fetching my activities, fetch ALL and filter
                    fetchAvailableActivities(apiService, myActivityIds);
                }
            }

            @Override
            public void onFailure(retrofit2.Call<List<cuatrovientos.voluntariado.network.model.ApiActivity>> call, Throwable t) {
                // If MyActivities fails, at least try to load Available but empty MyList
                 fetchAvailableActivities(apiService, new ArrayList<>());
            }
        });
    }

    private void fetchAvailableActivities(cuatrovientos.voluntariado.network.ApiService apiService, List<Integer> excludeIds) {
        apiService.getActivities().enqueue(new retrofit2.Callback<List<cuatrovientos.voluntariado.network.model.ApiActivity>>() {
            @Override
            public void onResponse(retrofit2.Call<List<cuatrovientos.voluntariado.network.model.ApiActivity>> call, retrofit2.Response<List<cuatrovientos.voluntariado.network.model.ApiActivity>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    availableActivitiesList.clear();
                    for (cuatrovientos.voluntariado.network.model.ApiActivity apiAct : response.body()) {
                        // Filter: Exclude my activities (already joined)
                        if (excludeIds.contains(apiAct.getId())) continue;

                        // Filter: Status (Only ACTIVO or PENDIENTE as requested)
                        String status = apiAct.getStatus();
                        if (status == null || 
                           (status.equalsIgnoreCase("ACTIVO") || status.equalsIgnoreCase("PENDIENTE"))) {
                           
                           availableActivitiesList.add(mapApiToModel(apiAct));
                        }
                    }
                    adapterAvailable.notifyDataSetChanged();

                    if (availableActivitiesList.isEmpty()) {
                        rvAvailableActivities.setVisibility(View.GONE);
                        emptyAvailableActivities.setVisibility(View.VISIBLE);
                    } else {
                        rvAvailableActivities.setVisibility(View.VISIBLE);
                        emptyAvailableActivities.setVisibility(View.GONE);
                    }
                }
            }
             @Override
            public void onFailure(retrofit2.Call<List<cuatrovientos.voluntariado.network.model.ApiActivity>> call, Throwable t) {}
        });
    }

    private VolunteerActivity mapApiToModel(cuatrovientos.voluntariado.network.model.ApiActivity apiAct) {
        String rawStatus = apiAct.getStatus() != null ? apiAct.getStatus() : "ACTIVO";
        String status = "Active"; 
        if (rawStatus.equalsIgnoreCase("ACTIVO")) status = "Active";
        else if (rawStatus.equalsIgnoreCase("PENDIENTE")) status = "Pending";
        else if (rawStatus.equalsIgnoreCase("SUSPENDIDO")) status = "Suspended";
        else if (rawStatus.equalsIgnoreCase("FINALIZADA")) status = "Finished";
        else status = rawStatus;

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

        return new VolunteerActivity(
                apiAct.getTitle(),
                description,
                "Ubicación por definir", // Location placeholder (not in API yet)
                "2025-01-01", // Date placeholder (not in API yet)
                type,
                status,
                color,
                imageUrl
        );
    }
}
