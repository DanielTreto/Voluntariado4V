package cuatrovientos.voluntariado.activities;

import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import cuatrovientos.voluntariado.R;
import cuatrovientos.voluntariado.fragments.EventsFragment;
import cuatrovientos.voluntariado.fragments.OrganizationHistoryFragment;
import cuatrovientos.voluntariado.fragments.OrganizationHomeFragment;
import cuatrovientos.voluntariado.fragments.SettingsFragment;

/**
 * Actividad principal para el perfil de Organización.
 * Gestiona la navegación inferior entre inicio, historial, calendario y ajustes.
 */
public class OrganizationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organization_main); // Nuevo layout con navegación

        BottomNavigationView bottomNav = findViewById(R.id.org_bottom_nav);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        // Cargar fragmento inicial
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.org_fragment_container, new OrganizationHomeFragment())
                    .commit();
        }
    }

    private final BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;
                    int itemId = item.getItemId();

                    if (itemId == R.id.nav_home_org) {
                        selectedFragment = new OrganizationHomeFragment();
                    } else if (itemId == R.id.nav_history_org) {
                        selectedFragment = new OrganizationHistoryFragment();
                    } else if (itemId == R.id.nav_calendar_org) {
                        selectedFragment = new EventsFragment();
                    } else if (itemId == R.id.nav_settings_org) {
                        selectedFragment = new SettingsFragment();
                    }

                    if (selectedFragment != null) {
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.org_fragment_container, selectedFragment)
                                .commit();
                    }
                    return true;
                }
            };
}
