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
        // 2. Crear los DATOS (Lista Maestra)
        masterList = new ArrayList<>();
        fetchVolunteers();

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

    private void fetchVolunteers() {
        cuatrovientos.voluntariado.network.ApiService apiService = 
            cuatrovientos.voluntariado.network.RetrofitClient.getClient().create(cuatrovientos.voluntariado.network.ApiService.class);

        apiService.getVolunteers().enqueue(new retrofit2.Callback<List<cuatrovientos.voluntariado.network.model.ApiVolunteer>>() {
            @Override
            public void onResponse(retrofit2.Call<List<cuatrovientos.voluntariado.network.model.ApiVolunteer>> call, retrofit2.Response<List<cuatrovientos.voluntariado.network.model.ApiVolunteer>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    masterList.clear();
                    for (cuatrovientos.voluntariado.network.model.ApiVolunteer apiVol : response.body()) {
                        String status = mapStatus(apiVol.getStatus());
                        String fullName = apiVol.getName() + " " + apiVol.getSurname1() + (apiVol.getSurname2() != null ? " " + apiVol.getSurname2() : "");
                        String avatarPath = apiVol.getAvatar();
                        String avatarUrl = null;
                        if (avatarPath != null) {
                            if (avatarPath.startsWith("http")) {
                                avatarUrl = avatarPath;
                            } else {
                                avatarUrl = "http://10.0.2.2:8000" + avatarPath;
                            }
                        }
                        
                        masterList.add(new Volunteer(
                            fullName,
                            "Voluntario", 
                            apiVol.getEmail(),
                            apiVol.getPhone(),
                            status,
                            apiVol.getDateOfBirth(),
                            avatarUrl
                        ));
                    }
                    // Refresh current view (defaulting to Solicitudes initially)
                    TabLayout tabLayout = getView().findViewById(R.id.tabLayout);
                    if (tabLayout.getSelectedTabPosition() == 0) {
                        filterList("Solicitudes");
                    } else {
                        filterList("Registrados");
                    }
                } else {
                    android.util.Log.e("VolunteersFragment", "Error fetching volunteers: " + response.code());
                    if (getContext() != null) {
                        android.widget.Toast.makeText(getContext(), "Error al cargar voluntarios", android.widget.Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(retrofit2.Call<List<cuatrovientos.voluntariado.network.model.ApiVolunteer>> call, Throwable t) {
                android.util.Log.e("VolunteersFragment", "Network error: " + t.getMessage());
                if (getContext() != null) {
                    android.widget.Toast.makeText(getContext(), "Error de conexión", android.widget.Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private String mapStatus(String backendStatus) {
        if (backendStatus == null) return "Pending";
        switch (backendStatus.toUpperCase()) {
            case "ACTIVO": return "Active";
            case "PENDIENTE": return "Pending";
            case "SUSPENDIDO": return "Suspended";
            default: return "Pending";
        }
    }

    // Lógica de filtrado
    private void filterList(String tabName) {
        List<Volunteer> filteredList = new ArrayList<>();

        if (masterList == null) return; // Guard against null

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
        if (adapter != null) {
            adapter.updateList(filteredList);
        }
    }
}