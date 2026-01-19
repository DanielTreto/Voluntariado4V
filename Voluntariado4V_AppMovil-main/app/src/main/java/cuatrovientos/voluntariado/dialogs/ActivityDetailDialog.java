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
import java.util.List;
import cuatrovientos.voluntariado.R;
import cuatrovientos.voluntariado.model.VolunteerActivity;

public class ActivityDetailDialog extends DialogFragment {

    private VolunteerActivity activity;
    private boolean isStudent = false;

    public static ActivityDetailDialog newInstance(VolunteerActivity activity) {
        // Default to false or maybe consider overloading
        // For compatibility with other calls (though I should update them)
        // Let's overload or default. 
        // User requested "Only for volunteer dashboard".
        // If called without arg, safe assumption? Or better to explicit?
        // Let's update calls.
        return newInstance(activity, false);
    }
    
    public static ActivityDetailDialog newInstance(VolunteerActivity activity, boolean isStudent) {
        ActivityDetailDialog fragment = new ActivityDetailDialog();
        fragment.activity = activity;
        fragment.isStudent = isStudent;
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Use a theme that supports DayNight (Light/Dark mode)
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Material_Light_NoActionBar_Fullscreen); 
        // Wait, Theme_Material_Light is also fixed light. 
        // Better to use a style that inherits parent or app theme. 
        // Or Theme_DeviceDefault_NoActionBar_Fullscreen which usually adapts.
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_DeviceDefault_NoActionBar_Fullscreen);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_activity_detail, container, false);

        ImageView imgHeader = view.findViewById(R.id.imgDetailHeader);
        TextView tvTitle = view.findViewById(R.id.tvDetailTitle);
        TextView tvDesc = view.findViewById(R.id.tvDetailDesc);
        TextView tvLocation = view.findViewById(R.id.tvDetailLocation);
        TextView tvDate = view.findViewById(R.id.tvDetailDate);
        TextView tvType = view.findViewById(R.id.tvDetailType);
        TextView tvMaxVolunteers = view.findViewById(R.id.tvDetailMaxVolunteers);
        TextView tvStatus = view.findViewById(R.id.tvDetailStatus);
        
        ImageView imgOrgAvatar = view.findViewById(R.id.imgDetailOrg);
        TextView tvOrgName = view.findViewById(R.id.tvOrgName);

        TextView tvVolunteersTitle = view.findViewById(R.id.tvVolunteersTitle);
        android.widget.LinearLayout layoutVolunteers = view.findViewById(R.id.layoutVolunteersList);
        ImageView btnClose = view.findViewById(R.id.btnClose);

        if (activity != null) {
            tvTitle.setText(activity.getTitle());
            tvDesc.setText(activity.getDescription());
            tvLocation.setText("Ubicación: " + activity.getLocation());
            
            // Category Tag
            tvType.setText(activity.getCategory());
            tvType.setBackgroundColor(activity.getImageColor());
            
            // Translate Status
            String rawStatus = activity.getStatus();
            String displayStatus = rawStatus;
            int statusColor = 0xFF000000;
            int statusBg = 0xFFE0E0E0;
            
            if ("Active".equalsIgnoreCase(rawStatus) || "ACTIVO".equalsIgnoreCase(rawStatus)) {
                displayStatus = "Activa";
                statusColor = 0xFF4CAF50; // Green
                statusBg = 0xFFE8F5E9;
            } else if ("Pending".equalsIgnoreCase(rawStatus) || "PENDIENTE".equalsIgnoreCase(rawStatus)) {
                displayStatus = "Pendiente";
                statusColor = 0xFFFFA000; // Orange
                statusBg = 0xFFFFF8E1;
            } else if ("Finished".equalsIgnoreCase(rawStatus) || "FINALIZADA".equalsIgnoreCase(rawStatus)) {
                displayStatus = "Finalizada";
                statusColor = 0xFFF44336; // Red
                statusBg = 0xFFFFEBEE;
            } else if ("InProgress".equalsIgnoreCase(rawStatus) || "EN_PROGRESO".equalsIgnoreCase(rawStatus)) {
                displayStatus = "En Progreso";
                statusColor = 0xFF2196F3; // Blue
                statusBg = 0xFFE3F2FD;
            }
            
            tvStatus.setText(displayStatus);
            tvStatus.setTextColor(statusColor);
            tvStatus.setBackgroundColor(statusBg);

            // Organization Logic
            tvOrgName.setText(activity.getOrganizationName() != null ? activity.getOrganizationName() : "Cuatrovientos");
            if (activity.getOrganizationAvatar() != null) {
                Glide.with(this)
                     .load(activity.getOrganizationAvatar())
                     .circleCrop()
                     .placeholder(R.drawable.ic_business)
                     .error(R.drawable.ic_business)
                     .into(imgOrgAvatar);
            } else {
                 imgOrgAvatar.setImageResource(R.drawable.ic_business);
            }

            android.widget.LinearLayout layoutOrg = view.findViewById(R.id.layoutDetailOrg);
            ImageView btnOrgDetailArrow = view.findViewById(R.id.btnOrgDetailArrow);

            if (isStudent) {
                 btnOrgDetailArrow.setVisibility(View.VISIBLE);
                 layoutOrg.setOnClickListener(v -> {
                    String orgId = activity.getOrganizationId();
                     if (orgId != null && !orgId.isEmpty()) {
                         cuatrovientos.voluntariado.model.Organization tempOrg = new cuatrovientos.voluntariado.model.Organization();
                         tempOrg.setId(orgId); // Only ID needed for loadActivities logic
                         tempOrg.setName(activity.getOrganizationName());
                         tempOrg.setAvatarUrl(activity.getOrganizationAvatar());
                         
                         cuatrovientos.voluntariado.dialogs.OrganizationDetailDialog orgDialog = 
                             cuatrovientos.voluntariado.dialogs.OrganizationDetailDialog.newInstance(tempOrg);
                         orgDialog.show(getParentFragmentManager(), "OrganizationDetailDialog_FromActivity");
                    } else {
                        android.widget.Toast.makeText(getContext(), "Información de organización no disponible", android.widget.Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                 btnOrgDetailArrow.setVisibility(View.GONE);
                 layoutOrg.setClickable(false);
                 layoutOrg.setFocusable(false);
                 layoutOrg.setOnClickListener(null);
            }


            // Format dates to dd/mm/yyyy (remove time if present)
            String startDate = activity.getDate();
            if (startDate != null && startDate.contains(" ")) {
                startDate = startDate.split(" ")[0];
            }
            String dateText = "Inicio: " + startDate;
            
            if (activity.getEndDate() != null) {
                 String endDate = activity.getEndDate();
                 if (endDate.contains(" ")) {
                     endDate = endDate.split(" ")[0];
                 }
                 dateText += "\nFin: " + endDate;
            }
            
            if (activity.getDuration() != null) {
                String dur = activity.getDuration();
                if (dur.length() > 5) dur = dur.substring(0, 5);
                dateText += " (Duración: " + dur + ")";
            }
            tvDate.setText(dateText);
            
            tvMaxVolunteers.setText("Cupo de Voluntarios: " + activity.getMaxVolunteers());


            String imageUrl = activity.getImageUrl();
            if (imageUrl == null || imageUrl.isEmpty()) {
                imageUrl = "https://blog.vicensvives.com/wp-content/uploads/2019/12/Voluntariado.png"; // Fallback URL
            }
            Glide.with(this)
                 .load(imageUrl)
                 .centerCrop()
                 .error(R.drawable.ic_launcher_background) // Fallback if URL fails
                 .placeholder(R.drawable.ic_launcher_background)
                 .into(imgHeader);

            // Volunteers List
            List<cuatrovientos.voluntariado.model.Volunteer> volunteers = activity.getParticipants();
            if (volunteers != null && !volunteers.isEmpty()) {
                tvVolunteersTitle.setVisibility(View.VISIBLE);
                layoutVolunteers.setVisibility(View.VISIBLE);
                layoutVolunteers.removeAllViews(); // Clear previous
                
                for (cuatrovientos.voluntariado.model.Volunteer v : volunteers) {
                     View volView = inflater.inflate(R.layout.item_volunteer_avatar, layoutVolunteers, false);
                     ImageView imgVol = volView.findViewById(R.id.imgVolunteerAvatar);
                     TextView tvName = volView.findViewById(R.id.tvVolunteerName);
                     
                     tvName.setText(v.getName());
                     Glide.with(this).load(v.getAvatarUrl()).circleCrop().placeholder(R.drawable.ic_profile_placeholder).error(R.drawable.ic_profile_placeholder).into(imgVol);
                     
                     layoutVolunteers.addView(volView);
                }
            } else {
                tvVolunteersTitle.setText("Sin voluntarios apuntados");
                layoutVolunteers.setVisibility(View.GONE);
            }

            // ODS List
            TextView tvOdsTitle = view.findViewById(R.id.tvOdsTitle);
            android.widget.LinearLayout layoutOdsList = view.findViewById(R.id.layoutOdsList);
            List<cuatrovientos.voluntariado.model.Ods> odsList = activity.getOds();

            tvOdsTitle.setVisibility(View.VISIBLE);
            layoutOdsList.setVisibility(View.VISIBLE);
            layoutOdsList.removeAllViews();

            if (odsList != null && !odsList.isEmpty()) {
                // Configure layout for horizontal scrolling or just horizontal linear layout if few
                layoutOdsList.setOrientation(android.widget.LinearLayout.HORIZONTAL);
                
                for (cuatrovientos.voluntariado.model.Ods ods : odsList) {
                    ImageView imgOds = new ImageView(getContext());
                    android.widget.LinearLayout.LayoutParams params = new android.widget.LinearLayout.LayoutParams(150, 150); // 50dp roughly
                    params.setMargins(8, 0, 8, 0);
                    imgOds.setLayoutParams(params);
                    imgOds.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    
                    String formattedId = (ods.getId() < 10) ? "0" + ods.getId() : String.valueOf(ods.getId());
                    String resourceName = "ods_" + formattedId;
                    int resId = getResources().getIdentifier(resourceName, "drawable", getContext().getPackageName());

                    if (resId != 0) {
                        Glide.with(this)
                             .load(resId)
                             .placeholder(R.drawable.ic_launcher_background)
                             .error(R.drawable.ic_launcher_background)
                             .into(imgOds);
                    } else {
                        // Fallback if resource missing for some reason
                         Glide.with(this)
                             .load(R.drawable.ic_launcher_background)
                             .into(imgOds);
                    }
                    
                    layoutOdsList.addView(imgOds);
                }
            } else {
                TextView tvNoOds = new TextView(getContext());
                tvNoOds.setText("Esta actividad no contribuye a ningún ODS específico.");
                tvNoOds.setTextColor(0xFF757575);
                tvNoOds.setTextSize(14f);
                layoutOdsList.addView(tvNoOds);
            }
        }

        // Count DialogFragments
        List<androidx.fragment.app.Fragment> fragments = getParentFragmentManager().getFragments();
        List<DialogFragment> dialogs = new java.util.ArrayList<>();
        for (androidx.fragment.app.Fragment f : fragments) {
            if (f instanceof DialogFragment) {
                dialogs.add((DialogFragment) f);
            }
        }

        // BTN CLOSE (X) -> Now closes ALL (Close Stack)
        btnClose.setOnClickListener(v -> {
             for (DialogFragment d : dialogs) {
                 d.dismiss();
             }
        });

        // BTN BACK (Arrow) -> Now closes ONLY CURRENT (Dismiss)
        ImageView btnCloseStack = view.findViewById(R.id.btnCloseStack);

        if (dialogs.size() > 1) {
            btnCloseStack.setVisibility(View.VISIBLE);
            btnCloseStack.setOnClickListener(v -> dismiss());
        }

        return view;
    }

    // Removed getOdsImageUrl method as it is no longer needed
}
