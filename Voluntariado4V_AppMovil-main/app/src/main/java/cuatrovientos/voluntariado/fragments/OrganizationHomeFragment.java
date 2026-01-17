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
                "Taller de Reciclaje", "Taller educativo para niños.", "Centro Cívico", "2025-05-25", "2h", "2025-05-25", 10, "Educación", "Active", "Mi Organización", null, 0xFF9C27B0, "https://images.unsplash.com/photo-1532996122724-e3c354a0b15b?ixlib=rb-4.0.3&auto=format&fit=crop&w=600&q=80"
        ));
        myActivities.add(new VolunteerActivity(
                "Recogida de Ropa", "Campaña de invierno.", "Plaza Mayor", "2025-11-15", "3h", "2025-11-15", 20, "Social", "Active", "Mi Organización", null, 0xFFFF5722, "https://images.unsplash.com/photo-1488521787991-ed7bbaae773c?ixlib=rb-4.0.3&auto=format&fit=crop&w=600&q=80"
        ));

        // false = Organization mode (with buttons)
        adapter = new ActivitiesAdapter(myActivities, false);
        rvMyActivities.setAdapter(adapter);

        return view;
    }
}
