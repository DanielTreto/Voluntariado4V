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

    private List<VolunteerActivity> masterHistoryList = new ArrayList<>();
    private String currentSearchQuery = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_history, container, false);

        rvHistory = view.findViewById(R.id.rvHistory);
        emptyHistory = view.findViewById(R.id.emptyHistory);
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
                        
                        // Filter: Only FINISHED activities
                        // Using normalized status from ActivityMapper ("Active", "Pending", "Finished", etc.)
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
