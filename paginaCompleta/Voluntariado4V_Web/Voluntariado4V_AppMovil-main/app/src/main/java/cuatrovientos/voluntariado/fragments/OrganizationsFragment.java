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
import com.google.android.material.tabs.TabLayout;
import java.util.ArrayList;
import java.util.List;

import cuatrovientos.voluntariado.R;
import cuatrovientos.voluntariado.adapters.OrganizationsAdapter;
import cuatrovientos.voluntariado.model.Organization;

public class OrganizationsFragment extends Fragment {

    private List<Organization> masterList;
    private OrganizationsAdapter adapter;

    public OrganizationsFragment() {
        // Constructor vacío
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_organizations, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerOrganizations);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        createMockData();

        adapter = new OrganizationsAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        // Cargar vista inicial (Solicitudes)
        filterList("Solicitudes");

        TabLayout tabLayout = view.findViewById(R.id.tabLayoutOrg);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    filterList("Solicitudes");
                } else {
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
        // Solicitudes (Imagen 1)
        masterList.add(new Organization("Future Horizons NGO", "contact@future.org", "2025-11-20", "0", "Pending"));

        // Registradas (Imagen 2)
        masterList.add(new Organization("Soluciones Globales A.C.", "info@globales.com", "2023-01-01", "12", "Active"));
        masterList.add(new Organization("Community Helpers", "hello@helpers.org", "2023-05-15", "5", "Active"));
    }

    private void filterList(String tabName) {
        List<Organization> filteredList = new ArrayList<>();
        for (Organization org : masterList) {
            if (tabName.equals("Solicitudes")) {
                if (org.getStatus().equals("Pending")) {
                    filteredList.add(org);
                }
            } else {
                if (org.getStatus().equals("Active")) {
                    filteredList.add(org);
                }
            }
        }
        adapter.updateList(filteredList);
    }
}