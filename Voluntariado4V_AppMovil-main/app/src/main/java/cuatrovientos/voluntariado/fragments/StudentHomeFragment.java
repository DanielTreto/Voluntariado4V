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

                        VolunteerActivity va = cuatrovientos.voluntariado.utils.ActivityMapper.mapApiToModel(apiAct);
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

                        // Filter: Status (Only ACTIVO, PENDIENTE, or EN_PROGRESO)
                        String status = apiAct.getStatus();
                        if (status == null || 
                           (status.equalsIgnoreCase("ACTIVO") || status.equalsIgnoreCase("PENDIENTE") || status.equalsIgnoreCase("EN_PROGRESO"))) {
                           
                           availableActivitiesList.add(cuatrovientos.voluntariado.utils.ActivityMapper.mapApiToModel(apiAct));
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
}
