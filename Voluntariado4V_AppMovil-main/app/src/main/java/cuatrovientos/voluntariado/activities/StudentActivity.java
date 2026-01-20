package cuatrovientos.voluntariado.activities;

import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import cuatrovientos.voluntariado.R;
import cuatrovientos.voluntariado.fragments.EventsFragment;
import cuatrovientos.voluntariado.fragments.SettingsFragment;
import cuatrovientos.voluntariado.fragments.StudentHistoryFragment;
import cuatrovientos.voluntariado.fragments.StudentHomeFragment;

public class StudentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_main); // Usamos el nuevo layout con navegación

        BottomNavigationView bottomNav = findViewById(R.id.student_bottom_nav);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        // Cargar fragmento inicial
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.student_fragment_container, new StudentHomeFragment())
                    .commit();
        }
    }

    private final BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;
                    int itemId = item.getItemId();

                    if (itemId == R.id.nav_home) {
                        selectedFragment = new StudentHomeFragment();
                    } else if (itemId == R.id.nav_history) {
                        selectedFragment = new StudentHistoryFragment();
                    } else if (itemId == R.id.nav_calendar) {
                         selectedFragment = new EventsFragment();
                    } else if (itemId == R.id.nav_settings) {
                        selectedFragment = new SettingsFragment();
                    }

                    if (selectedFragment != null) {
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.student_fragment_container, selectedFragment)
                                .commit();
                    }
                    return true;
                }
            };
}
