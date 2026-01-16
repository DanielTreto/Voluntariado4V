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
    private ActivitiesAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Reutilizamos el layout que ya funcionaba (activity_student_dashboard pero adaptado a fragment)
        // Pero ATENCIÓN: activity_student_dashboard es un layout completo con NestedScrollView.
        // Lo correcto sería mover su contenido a un nuevo layout 'fragment_student_home.xml' o reutilizarlo.
        // Vamos a asumir que podemos inflar el layout existente, aunque el nombre 'activity_...' sea confuso.
        View view = inflater.inflate(R.layout.activity_student_dashboard, container, false);

        rvAvailableActivities = view.findViewById(R.id.rvAvailableActivities);
        rvAvailableActivities.setLayoutManager(new LinearLayoutManager(getContext()));

        // Datos simulados para actividades disponibles
        List<VolunteerActivity> availableActivities = new ArrayList<>();
        availableActivities.add(new VolunteerActivity(
                "Limpieza de Playa", "Jornada de limpieza ambiental.", "Playa La Concha", "2025-05-20", "Medio Ambiente", "Active", 0xFF4CAF50, "https://images.unsplash.com/photo-1618477461853-5e8790b933a2?ixlib=rb-4.0.3&auto=format&fit=crop&w=600&q=80"
        ));
        availableActivities.add(new VolunteerActivity(
                "Acompañamiento Mayores", "Visita a residencia de ancianos.", "Residencia San José", "2025-06-10", "Social", "Active", 0xFF2196F3, "https://images.unsplash.com/photo-1571019614242-c5c5dee9f50b?ixlib=rb-4.0.3&auto=format&fit=crop&w=600&q=80"
        ));
        availableActivities.add(new VolunteerActivity(
                "Torneo de Fútbol", "Organización de torneo benéfico.", "Polideportivo", "2025-07-01", "Deporte", "Active", 0xFFFF9800, "https://images.unsplash.com/photo-1452626038306-9aae5e071dd3?ixlib=rb-4.0.3&auto=format&fit=crop&w=600&q=80"
        ));

        adapter = new ActivitiesAdapter(availableActivities, true);
        rvAvailableActivities.setAdapter(adapter);

        return view;
    }
}
