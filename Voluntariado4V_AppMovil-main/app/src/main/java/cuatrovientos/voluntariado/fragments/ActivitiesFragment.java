package cuatrovientos.voluntariado.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.tabs.TabLayout;
import java.util.ArrayList;
import java.util.List;

import cuatrovientos.voluntariado.R;
import cuatrovientos.voluntariado.adapters.ActivitiesAdapter;
import cuatrovientos.voluntariado.model.VolunteerActivity;

public class ActivitiesFragment extends Fragment {

    private List<VolunteerActivity> masterList;
    private ActivitiesAdapter adapter;

    public ActivitiesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_activities, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerActivities);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // 1. Crear los datos PRIMERO
        createMockData();

        // 2. Inicializar el adaptador con una lista vacía temporalmente
        adapter = new ActivitiesAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        // 3. Cargar la vista inicial CORRECTA (Solicitudes va primero ahora)
        // Esto soluciona que aparezca vacío al principio
        filterList("Solicitudes");

        // 4. Configurar el Listener
        TabLayout tabLayout = view.findViewById(R.id.tabLayoutAct);

        // Asegurarnos que la pestaña seleccionada visualmente es la 0
        if (tabLayout.getTabAt(0) != null) {
            tabLayout.getTabAt(0).select();
        }

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    // Pestaña 1: Solicitudes
                    filterList("Solicitudes");
                } else {
                    // Pestaña 2: Registradas (Actividades)
                    filterList("Registradas");
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void createMockData() {
        masterList = new ArrayList<>();

        // --- SOLICITUDES (Pending) ---
        // Estas aparecerán en la primera pestaña
        masterList.add(new VolunteerActivity("Clases de Apoyo", "Refuerzo escolar para niños en riesgo.", "Biblioteca Municipal", "2025-12-20", "Educativo", "Pending", Color.GREEN));
        masterList.add(new VolunteerActivity("Reparto Nocturno", "Entrega de mantas y comida.", "Centro Ciudad", "2025-12-22", "Social", "Pending", Color.DKGRAY));

        // --- REGISTRADAS (Active) ---
        // Estas aparecerán en la segunda pestaña
        masterList.add(new VolunteerActivity("Limpieza de Playa", "Jornada de limpieza y concienciación ambiental.", "Playa de la Concha", "2025-12-05", "Medio Ambiente", "Active", Color.CYAN));
        masterList.add(new VolunteerActivity("Taller de Programación", "Enseñanza de conceptos básicos a jóvenes.", "Centro Cívico", "2025-12-10", "Tecnológico", "Active", Color.BLUE));
        masterList.add(new VolunteerActivity("Reparto de Alimentos", "Distribución de alimentos a familias del barrio.", "Banco de Alimentos", "2025-12-15", "Social", "Active", Color.MAGENTA));
    }

    private void filterList(String tabName) {
        List<VolunteerActivity> filteredList = new ArrayList<>();

        for (VolunteerActivity act : masterList) {
            if (tabName.equals("Solicitudes")) {
                // Filtramos por estado "Pending"
                if (act.getStatus().equals("Pending")) {
                    filteredList.add(act);
                }
            } else { // Caso "Registradas"
                // Filtramos por estado "Active"
                if (act.getStatus().equals("Active")) {
                    filteredList.add(act);
                }
            }
        }

        // Importante: Actualizar el adaptador
        if (adapter != null) {
            adapter.updateList(filteredList);
        }
    }
}