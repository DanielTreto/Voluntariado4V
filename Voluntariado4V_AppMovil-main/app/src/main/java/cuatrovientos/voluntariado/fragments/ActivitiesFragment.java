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
        masterList = new ArrayList<>();
        fetchActivities();

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

    private void fetchActivities() {
        cuatrovientos.voluntariado.network.ApiService apiService = 
            cuatrovientos.voluntariado.network.RetrofitClient.getClient().create(cuatrovientos.voluntariado.network.ApiService.class);

        apiService.getActivities().enqueue(new retrofit2.Callback<List<cuatrovientos.voluntariado.network.model.ApiActivity>>() {
            @Override
            public void onResponse(retrofit2.Call<List<cuatrovientos.voluntariado.network.model.ApiActivity>> call, retrofit2.Response<List<cuatrovientos.voluntariado.network.model.ApiActivity>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    masterList.clear();
                    for (cuatrovientos.voluntariado.network.model.ApiActivity apiAct : response.body()) {
                        int color = getColorForType(apiAct.getType());
                        String rawStatus = apiAct.getStatus() != null ? apiAct.getStatus() : "ACTIVO";
                        String status = "Active"; // Default
                        
                        // Normalize Backend Status (Spanish) to Frontend (English)
                        if (rawStatus.equalsIgnoreCase("ACTIVO")) status = "Active";
                        else if (rawStatus.equalsIgnoreCase("PENDIENTE")) status = "Pending";
                        else if (rawStatus.equalsIgnoreCase("SUSPENDIDO")) status = "Suspended";
                        else if (rawStatus.equalsIgnoreCase("FINALIZADA")) status = "Finished";
                        else if (rawStatus.equalsIgnoreCase("EN_PROGRESO")) status = "InProgress";
                        else status = rawStatus; // Fallback
                        
                        List<cuatrovientos.voluntariado.model.Volunteer> participants = new ArrayList<>();
                        if (apiAct.getVolunteers() != null) {
                            for (cuatrovientos.voluntariado.network.model.ApiVolunteer apiVol : apiAct.getVolunteers()) {
                                 String avatarUrl = apiVol.getAvatar();
                                 if (avatarUrl != null && !avatarUrl.startsWith("http")) {
                                     avatarUrl = "http://10.0.2.2:8000/" + avatarUrl;
                                 }
                                 participants.add(new cuatrovientos.voluntariado.model.Volunteer(
                                    apiVol.getId(),
                                    apiVol.getName(),
                                    null, // Surname1
                                    null, // Surname2
                                    null, // Email
                                    null, // Phone
                                    null, // DNI
                                    null, // BirthDate
                                    null, // Description
                                    "Voluntario", // Role
                                    null, // Preferences
                                    "Active", // Status (Asumido)
                                    avatarUrl
                                 ));
                            }
                        }

                        String orgName = "Cuatrovientos";
                        String orgAvatar = null;
                        if (apiAct.getOrganization() != null) {
                            orgName = apiAct.getOrganization().getName();
                            String orgAvPath = apiAct.getOrganization().getAvatar();
                            if (orgAvPath != null && !orgAvPath.startsWith("http")) {
                                orgAvatar = "http://10.0.2.2:8000/" + orgAvPath;
                            } else {
                                orgAvatar = orgAvPath;
                            }
                        }

                        masterList.add(new VolunteerActivity(
                            apiAct.getTitle(),
                            apiAct.getDescription(),
                            apiAct.getLocation(),
                            apiAct.getDate(),
                            apiAct.getDuration(),
                            apiAct.getEndDate(),
                            apiAct.getMaxVolunteers(),
                            apiAct.getType(),
                            status,
                            orgName,
                            orgAvatar,
                            color,
                            apiAct.getImagen(),
                            participants
                        ));
                    }
                    // Refresh current tab
                    TabLayout tabLayout = getView().findViewById(R.id.tabLayoutAct);
                    if (tabLayout.getSelectedTabPosition() == 0) {
                        filterList("Solicitudes");
                    } else {
                        filterList("Registradas");
                    }
                } else {
                    android.util.Log.e("ActivitiesFragment", "Error fetching activities: " + response.code());
                    if (getContext() != null) {
                        android.widget.Toast.makeText(getContext(), "Error al cargar actividades", android.widget.Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(retrofit2.Call<List<cuatrovientos.voluntariado.network.model.ApiActivity>> call, Throwable t) {
                android.util.Log.e("ActivitiesFragment", "Network error: " + t.getMessage());
                if (getContext() != null) {
                    android.widget.Toast.makeText(getContext(), "Error de conexión", android.widget.Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private int getColorForType(String type) {
        if (type == null) return 0xFF9E9E9E; // Gray default
        switch (type) {
            case "Medio Ambiente": return 0xFF2E7D32; // Green
            case "Social": return 0xFF1976D2; // Blue
            case "Tecnológico": return 0xFFFFC107; // Amber
            case "Educativo": return 0xFF512DA8; // Deep Purple
            default: return 0xFFEF5350; // Red default
        }
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
                // Filtramos por estado "Active", "Finished", "Suspended", "InProgress"
                if (act.getStatus().equals("Active") || act.getStatus().equals("Finished") || act.getStatus().equals("Suspended") || act.getStatus().equals("InProgress")) {
                    filteredList.add(act);
                }
            }
        }

        // Importante: Actualizar el adaptador
        if (adapter != null) {
            adapter.updateList(filteredList);
        }

        RecyclerView recyclerView = getView().findViewById(R.id.recyclerActivities);
        android.widget.LinearLayout emptyView = getView().findViewById(R.id.emptyActivities);

        if (filteredList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }
}