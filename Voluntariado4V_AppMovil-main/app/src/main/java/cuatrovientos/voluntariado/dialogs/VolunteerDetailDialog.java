package cuatrovientos.voluntariado.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.bumptech.glide.Glide;
import cuatrovientos.voluntariado.R;
import cuatrovientos.voluntariado.model.Volunteer;

public class VolunteerDetailDialog extends DialogFragment {

    private static final String ARG_VOLUNTEER = "arg_volunteer";
    private Volunteer volunteer;

    public static VolunteerDetailDialog newInstance(Volunteer volunteer) {
        VolunteerDetailDialog fragment = new VolunteerDetailDialog();
        Bundle args = new Bundle();
        com.google.gson.Gson gson = new com.google.gson.Gson();
        String json = gson.toJson(volunteer);
        args.putString(ARG_VOLUNTEER, json);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String json = getArguments().getString(ARG_VOLUNTEER);
            volunteer = new com.google.gson.Gson().fromJson(json, Volunteer.class);
        }
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_volunteer_detail, container, false);

        ImageView btnClose = view.findViewById(R.id.btnClose);
        btnClose.setOnClickListener(v -> dismiss());

        if (volunteer == null) return view;

        ImageView imgHeader = view.findViewById(R.id.imgDetailHeader);
        TextView tvName = view.findViewById(R.id.tvDetailName);
        TextView tvStatus = view.findViewById(R.id.tvDetailStatus);
        TextView tvEmail = view.findViewById(R.id.tvDetailEmail);
        TextView tvPhone = view.findViewById(R.id.tvDetailPhone);
        TextView tvRole = view.findViewById(R.id.tvDetailRole);
        TextView tvDesc = view.findViewById(R.id.tvDetailDesc);

        // Construct Full Name
        String fullName = volunteer.getName();
        if (volunteer.getSurname1() != null) fullName += " " + volunteer.getSurname1();
        if (volunteer.getSurname2() != null) fullName += " " + volunteer.getSurname2();
        
        tvName.setText(fullName);
        tvEmail.setText(volunteer.getEmail());
        tvPhone.setText(volunteer.getPhone());
        tvRole.setText(volunteer.getRole());

        // Bind new fields
        TextView tvDni = view.findViewById(R.id.tvDetailDni);
        TextView tvBirthDate = view.findViewById(R.id.tvDetailBirthDate);
        
        tvDni.setText("DNI: " + (volunteer.getDni() != null ? volunteer.getDni() : ""));
        tvBirthDate.setText("Fecha Nac.: " + (volunteer.getBirthDate() != null ? volunteer.getBirthDate() : ""));

        // Status Check
        tvStatus.setText(volunteer.getStatus());
        if ("Active".equalsIgnoreCase(volunteer.getStatus()) || "ACTIVO".equalsIgnoreCase(volunteer.getStatus())) {
            tvStatus.setBackgroundColor(android.graphics.Color.parseColor("#E8F5E9"));
            tvStatus.setTextColor(android.graphics.Color.parseColor("#4CAF50"));
            tvStatus.setText("Activo");
        } else if ("Suspended".equalsIgnoreCase(volunteer.getStatus())) {
            tvStatus.setBackgroundColor(android.graphics.Color.parseColor("#FFEBEE"));
            tvStatus.setTextColor(android.graphics.Color.parseColor("#F44336"));
             tvStatus.setText("Suspendido");
        } else {
             tvStatus.setBackgroundColor(android.graphics.Color.parseColor("#FFF8E1"));
             tvStatus.setTextColor(android.graphics.Color.parseColor("#FFA000"));
             tvStatus.setText("Pendiente");
        }

        if (volunteer.getDescription() != null && !volunteer.getDescription().isEmpty()) {
            tvDesc.setText(volunteer.getDescription());
        } else {
            tvDesc.setText("Sin descripción disponible.");
        }


        if (volunteer.getAvatarUrl() != null && !volunteer.getAvatarUrl().isEmpty()) {
             Glide.with(this)
                 .load(volunteer.getAvatarUrl())
                 .placeholder(R.drawable.ic_profile_placeholder)
                 .error(R.drawable.ic_profile_placeholder)
                 .centerCrop()
                 .into(imgHeader);
        } else {
             imgHeader.setImageResource(R.drawable.ic_profile_placeholder);
        }

        return view;
    }
    
    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }
}
