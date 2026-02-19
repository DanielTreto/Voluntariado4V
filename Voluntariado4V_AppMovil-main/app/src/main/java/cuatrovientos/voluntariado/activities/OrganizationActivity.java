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

public class OrganizationActivity extends AppCompatActivity {

    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organization_main);

        // Read USER_ID from intent extras or SharedPreferences
        userId = getIntent().getStringExtra("USER_ID");
        if (userId == null) {
            android.content.SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
            userId = prefs.getString("USER_ID", null);
        }

        BottomNavigationView bottomNav = findViewById(R.id.org_bottom_nav);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        if (savedInstanceState == null) {
            loadFragment(new OrganizationHomeFragment());
        }
    }

    private void loadFragment(Fragment fragment) {
        if (userId != null) {
            Bundle args = fragment.getArguments();
            if (args == null)
                args = new Bundle();
            args.putString("USER_ID", userId);
            fragment.setArguments(args);
        }
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.org_fragment_container, fragment)
                .commit();
    }

    private final BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
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
                loadFragment(selectedFragment);
            }
            return true;
        }
    };
}
