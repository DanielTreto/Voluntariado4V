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

import com.google.android.material.tabs.TabLayout; // Importante para las pestañas

import java.util.ArrayList;
import java.util.List;

import cuatrovientos.voluntariado.R;
import cuatrovientos.voluntariado.adapters.VolunteersAdapter;
import cuatrovientos.voluntariado.model.Volunteer;

public class VolunteersFragment extends Fragment {

    private List<Volunteer> masterList; // Lista con TODOS los datos
    private VolunteersAdapter adapter;

    public VolunteersFragment() {
        // Constructor vacío
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_volunteers, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Configurar RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.recyclerVolunteers);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // 2. Crear los DATOS (Lista Maestra)
        createMockData();

        // 3. Inicializar el adaptador con la lista vacía o filtrada inicialmente
        // Por defecto mostramos la pestaña 0 (Solicitudes -> Pending)
        adapter = new VolunteersAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);
        filterList("Solicitudes"); // Cargar la primera vista

        // 4. Configurar el Listener de las Pestañas (TabLayout)
        TabLayout tabLayout = view.findViewById(R.id.tabLayout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    filterList("Solicitudes"); // Pestaña 1: Solo pendientes
                } else {
                    filterList("Registrados"); // Pestaña 2: Activos y Suspendidos
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    // Método para crear datos de prueba
    private void createMockData() {
        masterList = new ArrayList<>();
        // Solicitudes (Pending)
        masterList.add(new Volunteer("Michael Brown", "Pending Assignment", "michael.b@example.com", "(555) 222-3333", "Pending", "2023-11-20"));
        masterList.add(new Volunteer("Sarah Davis", "Pending Assignment", "sarah.d@example.com", "(555) 444-5555", "Pending", "2023-11-25"));

        // Registrados (Active / Suspended)
        masterList.add(new Volunteer("Jane Doe", "Community Garden", "jane.doe@example.com", "(555) 123-4567", "Active", "2023-10-26"));
        masterList.add(new Volunteer("Emily Johnson", "Literacy Program", "emily.j@example.com", "(555) 456-7890", "Suspended", "2023-10-15"));
        masterList.add(new Volunteer("John Smith", "Food Bank Initiative", "john.s@example.com", "(555) 987-6543", "Active", "2023-11-01"));
    }

    // Lógica de filtrado
    private void filterList(String tabName) {
        List<Volunteer> filteredList = new ArrayList<>();

        for (Volunteer v : masterList) {
            if (tabName.equals("Solicitudes")) {
                // Si es la pestaña Solicitudes, solo queremos los "Pending"
                if (v.getStatus().equals("Pending")) {
                    filteredList.add(v);
                }
            } else {
                // Si es la pestaña Registrados, queremos "Active" o "Suspended"
                if (v.getStatus().equals("Active") || v.getStatus().equals("Suspended")) {
                    filteredList.add(v);
                }
            }
        }

        // Actualizamos el adaptador con la nueva lista filtrada
        adapter.updateList(filteredList);
    }
}