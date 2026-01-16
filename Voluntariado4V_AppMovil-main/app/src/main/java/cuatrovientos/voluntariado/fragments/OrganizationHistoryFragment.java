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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // We can reuse the student history layout as it's just a Title + RecyclerView
        View view = inflater.inflate(R.layout.fragment_student_history, container, false);

        rvHistory = view.findViewById(R.id.rvHistory);
        rvHistory.setLayoutManager(new LinearLayoutManager(getContext()));

        List<VolunteerActivity> historyList = new ArrayList<>();
        // Mock Data: Finalized / Past activities for Org
        historyList.add(new VolunteerActivity(
                "Maratón Solidario 2024", "Evento deportivo benéfico.", "Ciudad Deportiva", "2024-03-15", "Deporte", "Finished", 0xFF009688, "https://images.unsplash.com/photo-1452626038306-9aae5e071dd3?ixlib=rb-4.0.3&auto=format&fit=crop&w=600&q=80"
        ));

        // Using false (Org Mode) but since they are finished, maybe buttons shouldn't be active?
        // Ideally we'd modify Adapter or Activity logic to handle 'Finished' state separately.
        // For now, let's stick to simple display. Using 'true' (Student Mode) just to hide buttons for History view is a quick hack.
        adapter = new ActivitiesAdapter(historyList, true); 
        rvHistory.setAdapter(adapter);

        return view;
    }
}
