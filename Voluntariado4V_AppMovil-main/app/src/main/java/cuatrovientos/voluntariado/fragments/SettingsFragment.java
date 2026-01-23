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


    private TextView tvProfileName;
    private TextView tvProfileEmail;
    private android.widget.ImageView imgProfile;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        MaterialButton btnLogout = view.findViewById(R.id.btnLogout);

        tvProfileName = view.findViewById(R.id.tvProfileName);
        tvProfileEmail = view.findViewById(R.id.tvProfileEmail);
        imgProfile = view.findViewById(R.id.imgProfile);

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

            SwitchMaterial switchDarkMode = view.findViewById(R.id.switchDarkMode);
            android.content.SharedPreferences appPrefs = context.getSharedPreferences("AppSettings", android.content.Context.MODE_PRIVATE);
            boolean isDarkMode = appPrefs.getBoolean("DARK_MODE", false);
            int nightModeFlags = context.getResources().getConfiguration().uiMode & android.content.res.Configuration.UI_MODE_NIGHT_MASK;
            if (!appPrefs.contains("DARK_MODE")) {
                 isDarkMode = (nightModeFlags == android.content.res.Configuration.UI_MODE_NIGHT_YES);
            }
            
            if(switchDarkMode != null) {
                switchDarkMode.setChecked(isDarkMode);
                switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    appPrefs.edit().putBoolean("DARK_MODE", isChecked).apply();
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

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new com.google.android.material.dialog.MaterialAlertDialogBuilder(getContext())
                    .setTitle("Cerrar Sesión")
                    .setMessage("¿Estás seguro de que deseas cerrar sesión?")
                    .setPositiveButton("Sí", (dialog, which) -> {
                        if (getContext() != null) {
                            android.content.SharedPreferences prefs = getContext().getSharedPreferences("UserSession", android.content.Context.MODE_PRIVATE);
                            prefs.edit().clear().apply();
                        }

                        Intent intent = new Intent(getActivity(), cuatrovientos.voluntariado.activities.LoginActivity.class);
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
