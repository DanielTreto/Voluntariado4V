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

    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_main); 
        
        userId = getIntent().getStringExtra("USER_ID"); // Capture ID passed from LoginActivity
        
        if (userId == null) {
            android.content.SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
            userId = prefs.getString("USER_ID", null);
        }

        BottomNavigationView bottomNav = findViewById(R.id.student_bottom_nav);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        if (savedInstanceState == null) {
            loadFragment(new StudentHomeFragment());
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
                        loadFragment(selectedFragment);
                    }
                    return true;
                }
            };
            
    private void loadFragment(Fragment fragment) {
        if (userId != null) {
            Bundle bundle = new Bundle();
            bundle.putString("USER_ID", userId);
            fragment.setArguments(bundle);
        }
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.student_fragment_container, fragment)
                .commit();
    }
}
