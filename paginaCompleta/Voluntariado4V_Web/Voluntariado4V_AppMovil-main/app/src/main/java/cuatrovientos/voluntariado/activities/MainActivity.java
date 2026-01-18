package cuatrovientos.voluntariado.activities;

import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;

import cuatrovientos.voluntariado.R;
import cuatrovientos.voluntariado.fragments.BlankFragment;

import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private TextView toolbarTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 1. CAMBIO IMPORTANTE: Cargar el layout del Dashboard que contiene el Drawer
        setContentView(R.layout.activity_dashboard);

        // 2. Configurar la Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Ocultar el título por defecto ya que tienes un TextView personalizado en el XML
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        
        // Referencia al título personalizado
        toolbarTitle = findViewById(R.id.toolbar_title);

        // 3. Configurar el DrawerLayout y el Toggle (botón hamburguesa)
        drawerLayout = findViewById(R.id.main); // ID definido en activity_dashboard.xml
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // 4. Cargar el fragmento por defecto (Reports) al iniciar
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.nav_host_fragment, new cuatrovientos.voluntariado.fragments.ReportsFragment())
                    .commit();
            navigationView.setCheckedItem(R.id.nav_reports);
            toolbarTitle.setText("Informes");
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment selectedFragment = null;
        String title = "";
        int id = item.getItemId();

        if (id == R.id.nav_volunteers) {
            selectedFragment = new cuatrovientos.voluntariado.fragments.VolunteersFragment();
            title = "Voluntarios";
        } else if (id == R.id.nav_organizations) {
            selectedFragment = new cuatrovientos.voluntariado.fragments.OrganizationsFragment();
            title = "Organizaciones";
        } else if (id == R.id.nav_activities) {
            selectedFragment = new cuatrovientos.voluntariado.fragments.ActivitiesFragment();
            title = "Actividades";
        } else if (id == R.id.nav_events) {
            selectedFragment = new cuatrovientos.voluntariado.fragments.EventsFragment();
            title = "Eventos";
        } else if (id == R.id.nav_reports) {
            selectedFragment = new cuatrovientos.voluntariado.fragments.ReportsFragment();
            title = "Informes";
        } else if (id == R.id.nav_settings) {
            selectedFragment = new cuatrovientos.voluntariado.fragments.SettingsFragment();
            title = "Ajustes";
        } else if (id == R.id.nav_logout) {
            // Logout Logic
            android.content.Intent intent = new android.content.Intent(this, cuatrovientos.voluntariado.activities.LoginActivity.class);
            intent.setFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK | android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            return true;
        }

        if (selectedFragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.nav_host_fragment, selectedFragment)
                    .commit();
            if (toolbarTitle != null) {
                toolbarTitle.setText(title);
            }
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}