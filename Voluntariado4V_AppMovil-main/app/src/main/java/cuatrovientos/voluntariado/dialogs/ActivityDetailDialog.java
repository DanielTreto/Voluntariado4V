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
        // Por defecto no es modo estudiante (sin navegación a organización)
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
        // Usar un estilo de pantalla completa
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
            
            // Etiqueta de Categoría
            tvType.setText(activity.getCategory());
            tvType.setBackgroundColor(activity.getImageColor());
            
            // Traducir Estado
            String rawStatus = activity.getStatus();
            
            tvStatus.setText(cuatrovientos.voluntariado.utils.ActivityMapper.getStatusLabel(rawStatus));
            tvStatus.setTextColor(cuatrovientos.voluntariado.utils.ActivityMapper.getStatusTextColor(rawStatus));
            tvStatus.setBackgroundColor(cuatrovientos.voluntariado.utils.ActivityMapper.getStatusBackgroundColor(rawStatus));

            // Lógica de Organización
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
                         tempOrg.setId(orgId); // Solo ID necesario para lógica de loadActivities
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


            // Formatear fechas a dd/mm/yyyy
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
                imageUrl = "https://blog.vicensvives.com/wp-content/uploads/2019/12/Voluntariado.png"; // URL de respaldo
            }
            Glide.with(this)
                 .load(imageUrl)
                 .centerCrop()
                 .error(Glide.with(this).load("https://blog.vicensvives.com/wp-content/uploads/2019/12/Voluntariado.png")) // Fallback URL
                 .placeholder(R.drawable.ic_launcher_background)
                 .into(imgHeader);

            // Lista de Voluntarios
            List<cuatrovientos.voluntariado.model.Volunteer> volunteers = activity.getParticipants();

            
            // Comprobar Rol de Admin para navegación de voluntarios
            android.content.SharedPreferences prefs = requireContext().getSharedPreferences("UserSession", android.content.Context.MODE_PRIVATE);
            String role = prefs.getString("USER_ROLE", "");
            boolean isAdmin = "admin".equalsIgnoreCase(role) || "administrator".equalsIgnoreCase(role) 
                              || "organization".equalsIgnoreCase(role) || "organizacion".equalsIgnoreCase(role);

            // Renderizado de Lista de Voluntarios con lógica de Admin
            if (volunteers != null && !volunteers.isEmpty()) {
                tvVolunteersTitle.setVisibility(View.VISIBLE);
                layoutVolunteers.setVisibility(View.VISIBLE);
                layoutVolunteers.removeAllViews(); // Clear previous
                
                for (cuatrovientos.voluntariado.model.Volunteer v : volunteers) {
                     View volView = inflater.inflate(R.layout.item_volunteer_avatar, layoutVolunteers, false);
                     ImageView imgVol = volView.findViewById(R.id.imgVolunteerAvatar);
                     TextView tvName = volView.findViewById(R.id.tvVolunteerName);
                     ImageView btnArrow = volView.findViewById(R.id.btnVolunteerDetailArrow);
                     
                     tvName.setText(v.getName());
                     Glide.with(this).load(v.getAvatarUrl()).circleCrop().placeholder(R.drawable.ic_profile_placeholder).error(R.drawable.ic_profile_placeholder).into(imgVol);
                     
                     if (isAdmin) {
                         btnArrow.setVisibility(View.VISIBLE);
                         volView.setClickable(true);
                         volView.setFocusable(true);
                         // Add ripple background programmatically if needed, or rely on parent
                         
                         volView.setOnClickListener(clickedView -> {
                             cuatrovientos.voluntariado.dialogs.VolunteerDetailDialog volDialog = 
                                 cuatrovientos.voluntariado.dialogs.VolunteerDetailDialog.newInstance(v);
                             volDialog.show(getParentFragmentManager(), "VolunteerDetail_FromActivity");
                         });
                     } else {
                         btnArrow.setVisibility(View.GONE);
                     }
                     
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
                // Configurar layout para scroll horizontal o linear layout horizontal si son pocos
                layoutOdsList.setOrientation(android.widget.LinearLayout.HORIZONTAL);
                
                // Convert 50dp to pixels
                int sizeInDp = 50;
                int sizeInPx = (int) android.util.TypedValue.applyDimension(
                        android.util.TypedValue.COMPLEX_UNIT_DIP, sizeInDp, 
                        getResources().getDisplayMetrics());

                for (cuatrovientos.voluntariado.model.Ods ods : odsList) {
                    ImageView imgOds = new ImageView(getContext());
                    android.widget.LinearLayout.LayoutParams params = new android.widget.LinearLayout.LayoutParams(sizeInPx, sizeInPx);
                    
                    // ¿Margen también en dp? Mantenemos simple o convertimos 4dp
                    int marginPx = (int) android.util.TypedValue.applyDimension(
                            android.util.TypedValue.COMPLEX_UNIT_DIP, 4, 
                            getResources().getDisplayMetrics());
                    params.setMargins(marginPx, 0, marginPx, 0);

                    imgOds.setLayoutParams(params);
                    imgOds.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    
                    int resId = getOdsDrawable(ods.getId());
                    
                    if (resId != 0) {
                        Glide.with(this)
                             .load(resId)
                             .placeholder(R.drawable.ic_launcher_background)
                             .error(R.drawable.ic_launcher_background)
                             .into(imgOds);
                    } else {
                         // Fallback
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

        // Contar DialogFragments
        List<androidx.fragment.app.Fragment> fragments = getParentFragmentManager().getFragments();
        List<DialogFragment> dialogs = new java.util.ArrayList<>();
        for (androidx.fragment.app.Fragment f : fragments) {
            if (f instanceof DialogFragment) {
                dialogs.add((DialogFragment) f);
            }
        }

        // BTN CERRAR (X) -> Cierra la pila completa de diálogos
        btnClose.setOnClickListener(v -> {
             for (DialogFragment d : dialogs) {
                 d.dismiss();
             }
        });

        // BTN ATRAS (Flecha) -> Cierra solo el actual
        ImageView btnCloseStack = view.findViewById(R.id.btnCloseStack);

        if (dialogs.size() > 1) {
            btnCloseStack.setVisibility(View.VISIBLE);
            btnCloseStack.setOnClickListener(v -> dismiss());
        }

        return view;
    }

    private int getOdsDrawable(int odsId) {
        switch (odsId) {
            case 1: return R.drawable.ods_01;
            case 2: return R.drawable.ods_02;
            case 3: return R.drawable.ods_03;
            case 4: return R.drawable.ods_04;
            case 5: return R.drawable.ods_05;
            case 6: return R.drawable.ods_06;
            case 7: return R.drawable.ods_07;
            case 8: return R.drawable.ods_08;
            case 9: return R.drawable.ods_09;
            case 10: return R.drawable.ods_10;
            case 11: return R.drawable.ods_11;
            case 12: return R.drawable.ods_12;
            case 13: return R.drawable.ods_13;
            case 14: return R.drawable.ods_14;
            case 15: return R.drawable.ods_15;
            case 16: return R.drawable.ods_16;
            case 17: return R.drawable.ods_17;
            default: return 0;
        }
    }
}
