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
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import android.view.View;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private TextView toolbarTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        
        toolbarTitle = findViewById(R.id.toolbar_title);

        drawerLayout = findViewById(R.id.main);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        ImageView navImage = headerView.findViewById(R.id.imageView);
        TextView navName = headerView.findViewById(R.id.tvNavHeaderName);
        TextView navRole = headerView.findViewById(R.id.tvNavHeaderRole);
        
        android.content.SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        String savedName = prefs.getString("USER_NAME", "Administrador");
        String savedAvatar = prefs.getString("USER_AVATAR", null);
        
        navName.setText(savedName);
        navRole.setText("Administrador");

        if (savedAvatar != null && !savedAvatar.isEmpty()) {
            Glide.with(this)
                .load(savedAvatar)
                .placeholder(R.drawable.ic_profile_placeholder)
                .error(R.drawable.ic_profile_placeholder)
                .centerCrop()
                .into(navImage);
        } else {
             navImage.setImageResource(R.drawable.ic_profile_placeholder);
        }

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.nav_host_fragment, new cuatrovientos.voluntariado.fragments.ReportsFragment())
                    .commit();
            navigationView.setCheckedItem(R.id.nav_reports);
            toolbarTitle.setText("Informes");
        }
        getOnBackPressedDispatcher().addCallback(this, new androidx.activity.OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                }
            }
        });
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
            new com.google.android.material.dialog.MaterialAlertDialogBuilder(this)
                .setTitle("Cerrar Sesión")
                .setMessage("¿Estás seguro de que deseas cerrar sesión?")
                .setPositiveButton("Sí", (dialog, which) -> {
                     android.content.Intent intent = new android.content.Intent(this, cuatrovientos.voluntariado.activities.LoginActivity.class);
                     intent.setFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK | android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK);
                     startActivity(intent);
                })
                .setNegativeButton("No", null)
                .show();
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