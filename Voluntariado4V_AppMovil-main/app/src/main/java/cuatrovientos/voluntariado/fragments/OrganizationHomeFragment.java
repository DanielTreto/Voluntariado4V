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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_organization_dashboard, container, false); // Reusing layout

        rvMyActivities = view.findViewById(R.id.rvMyActivities);
        rvMyActivities.setLayoutManager(new LinearLayoutManager(getContext()));

        // Mock data
        List<VolunteerActivity> myActivities = new ArrayList<>();
        myActivities.add(new VolunteerActivity(
                "Taller de Reciclaje", "Taller educativo para niños.", "Centro Cívico", "2025-05-25", "Educación", "Active", 0xFF9C27B0
        ));
        myActivities.add(new VolunteerActivity(
                "Recogida de Ropa", "Campaña de invierno.", "Plaza Mayor", "2025-11-15", "Social", "Active", 0xFFFF5722
        ));

        // false = Organization mode (with buttons)
        adapter = new ActivitiesAdapter(myActivities, false);
        rvMyActivities.setAdapter(adapter);

        return view;
    }
}
