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

public class SettingsFragment extends Fragment {

    private LinearLayout containerAccountDetails;
    private MaterialButton btnToggleDetails;
    private SwitchMaterial switchNotifications;
    private TextView tvProfileName;
    private TextView tvProfileEmail;

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

        // Detect User Type and Set Placeholder Data
        if (getActivity() instanceof StudentActivity) {
            tvProfileName.setText("Alumno Ejemplo");
            tvProfileEmail.setText("alumno@cuatrovientos.org");
        } else if (getActivity() instanceof OrganizationActivity) {
            tvProfileName.setText("Organización Ejemplo");
            tvProfileEmail.setText("contacto@organizacion.org");
        } else if (getActivity() instanceof MainActivity) {
            tvProfileName.setText("Administrador");
            tvProfileEmail.setText("admin@cuatrovientos.org");
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
                // Ir a LoginActivity
                Intent intent = new Intent(getActivity(), cuatrovientos.voluntariado.activities.LoginActivity.class);
                // Limpiar back stack para que no se pueda volver atrás
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        return view;
    }
}
