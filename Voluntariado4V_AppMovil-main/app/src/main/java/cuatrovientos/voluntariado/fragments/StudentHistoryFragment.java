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

public class StudentHistoryFragment extends Fragment {

    private RecyclerView rvHistory;
    private ActivitiesAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_history, container, false);

        rvHistory = view.findViewById(R.id.rvHistory);
        rvHistory.setLayoutManager(new LinearLayoutManager(getContext()));

        List<VolunteerActivity> historyList = new ArrayList<>();
        // Mock Data: Actividades finalizadas
        historyList.add(new VolunteerActivity(
                "Gran Recogida 2024", "Campaña anual en supermercados.", "Supermercados Varios", "2024-11-20", "Social", "Finished", 0xFFE91E63, "https://images.unsplash.com/photo-1593113598332-cd288d649433?ixlib=rb-4.0.3&auto=format&fit=crop&w=600&q=80"
        ));

        adapter = new ActivitiesAdapter(historyList, true);
        rvHistory.setAdapter(adapter);

        return view;
    }
}
