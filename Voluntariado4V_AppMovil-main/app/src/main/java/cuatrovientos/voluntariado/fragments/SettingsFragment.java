package cuatrovientos.voluntariado.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import cuatrovientos.voluntariado.R;
import cuatrovientos.voluntariado.activities.MainActivity;
import cuatrovientos.voluntariado.activities.OrganizationActivity;
import cuatrovientos.voluntariado.activities.StudentActivity;

import com.bumptech.glide.Glide;

public class SettingsFragment extends Fragment {

    private LinearLayout containerAccountDetails;
    private MaterialButton btnToggleDetails;
    private SwitchMaterial switchNotifications;
    private TextView tvProfileName;
    private TextView tvProfileEmail;
    private android.widget.ImageView imgProfile;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        // Initialize Views
        MaterialButton btnLogout = view.findViewById(R.id.btnLogout);
        btnToggleDetails = view.findViewById(R.id.btnToggleDetails);
        containerAccountDetails = view.findViewById(R.id.containerAccountDetails);
        switchNotifications = view.findViewById(R.id.switchNotifications);
        tvProfileName = view.findViewById(R.id.tvProfileName);
        tvProfileEmail = view.findViewById(R.id.tvProfileEmail);
        imgProfile = view.findViewById(R.id.imgProfile);

        // Load User Data from Session
        android.content.Context context = getContext();
        if (context != null) {
            android.content.SharedPreferences prefs = context.getSharedPreferences("UserSession", android.content.Context.MODE_PRIVATE);
            String name = prefs.getString("USER_NAME", null);
            String email = prefs.getString("USER_EMAIL", null);
            String avatar = prefs.getString("USER_AVATAR", null);

            if (name != null) tvProfileName.setText(name);
            else tvProfileName.setText("Usuario");

            if (email != null) tvProfileEmail.setText(email);
            
            if (avatar != null && !avatar.isEmpty()) {
                Glide.with(this)
                     .load(avatar)
                     .circleCrop()
                     .placeholder(R.drawable.ic_profile_placeholder)
                     .error(R.drawable.ic_profile_placeholder)
                     .into(imgProfile);
            } else {
                 imgProfile.setImageResource(R.drawable.ic_profile_placeholder);
            }

            // Dark Mode Logic
            SwitchMaterial switchDarkMode = view.findViewById(R.id.switchDarkMode);
            // Check current night mode in prefs (or system default if not set)
            // Ideally we save this pref. Let's use "AppSettings" for app configs.
            android.content.SharedPreferences appPrefs = context.getSharedPreferences("AppSettings", android.content.Context.MODE_PRIVATE);
            boolean isDarkMode = appPrefs.getBoolean("DARK_MODE", false);
            // Also check if system is already dark to set initial switch state correctly if not set
            int nightModeFlags = context.getResources().getConfiguration().uiMode & android.content.res.Configuration.UI_MODE_NIGHT_MASK;
            if (!appPrefs.contains("DARK_MODE")) {
                 isDarkMode = (nightModeFlags == android.content.res.Configuration.UI_MODE_NIGHT_YES);
            }
            
            if(switchDarkMode != null) {
                switchDarkMode.setChecked(isDarkMode);
                switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    // Update Prefs
                    appPrefs.edit().putBoolean("DARK_MODE", isChecked).apply();
                    // Apply Mode
                    if (isChecked) {
                        androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES);
                        Toast.makeText(getContext(), "Modo oscuro activado", Toast.LENGTH_SHORT).show();
                    } else {
                        androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO);
                        Toast.makeText(getContext(), "Modo oscuro desactivado", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }

        // Toggle Account Details Form
        btnToggleDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (containerAccountDetails.getVisibility() == View.GONE) {
                    containerAccountDetails.setVisibility(View.VISIBLE);
                    btnToggleDetails.setText("Ocultar detalles de la cuenta");
                } else {
                    containerAccountDetails.setVisibility(View.GONE);
                    btnToggleDetails.setText("Cambiar detalles de la cuenta");
                }
            }
        });

        // Notifications Switch Listener
        switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                Toast.makeText(getContext(), "Notificaciones Activadas", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Notificaciones Desactivadas", Toast.LENGTH_SHORT).show();
            }
        });

        // Logout Logic
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new com.google.android.material.dialog.MaterialAlertDialogBuilder(getContext())
                    .setTitle("Cerrar Sesión")
                    .setMessage("¿Estás seguro de que deseas cerrar sesión?")
                    .setPositiveButton("Sí", (dialog, which) -> {
                        // Clear Session
                        if (getContext() != null) {
                            android.content.SharedPreferences prefs = getContext().getSharedPreferences("UserSession", android.content.Context.MODE_PRIVATE);
                            prefs.edit().clear().apply();
                        }

                        // Ir a LoginActivity
                        Intent intent = new Intent(getActivity(), cuatrovientos.voluntariado.activities.LoginActivity.class);
                        // Limpiar back stack para que no se pueda volver atrás
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    })
                    .setNegativeButton("No", null)
                    .show();
            }
        });

        return view;
    }
}
