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
        
        java.util.List<androidx.fragment.app.Fragment> fragments = getParentFragmentManager().getFragments();
        java.util.List<DialogFragment> dialogs = new java.util.ArrayList<>();
        for (androidx.fragment.app.Fragment f : fragments) {
            if (f instanceof DialogFragment) {
                dialogs.add((DialogFragment) f);
            }
        }

        btnClose.setOnClickListener(v -> {
             for (DialogFragment d : dialogs) {
                 d.dismiss();
             }
        });

        ImageView btnCloseStack = view.findViewById(R.id.btnCloseStack);
        if (dialogs.size() > 1) {
            btnCloseStack.setVisibility(View.VISIBLE);
            btnCloseStack.setOnClickListener(v -> dismiss());
        }

        if (volunteer == null) return view;
        
        android.widget.ProgressBar progressBar = view.findViewById(R.id.progressBar);
        android.widget.LinearLayout layoutContent = view.findViewById(R.id.layoutDetailContent);
        
        if (volunteer.getId() != null) {
            progressBar.setVisibility(View.VISIBLE);
            layoutContent.setVisibility(View.GONE);
        } else {
             progressBar.setVisibility(View.GONE);
             layoutContent.setVisibility(View.VISIBLE);
        }

        ImageView imgHeader = view.findViewById(R.id.imgDetailHeader);
        TextView tvName = view.findViewById(R.id.tvDetailName);
        TextView tvStatus = view.findViewById(R.id.tvDetailStatus);
        TextView tvEmail = view.findViewById(R.id.tvDetailEmail);
        TextView tvPhone = view.findViewById(R.id.tvDetailPhone);
        TextView tvRole = view.findViewById(R.id.tvDetailRole);
        TextView tvDesc = view.findViewById(R.id.tvDetailDesc);

        String fullName = volunteer.getName();
        if (volunteer.getSurname1() != null) fullName += " " + volunteer.getSurname1();
        if (volunteer.getSurname2() != null) fullName += " " + volunteer.getSurname2();
        
        tvName.setText(fullName);
        tvEmail.setText(volunteer.getEmail());
        tvPhone.setText(volunteer.getPhone());
        tvRole.setText(volunteer.getCourse() != null && !volunteer.getCourse().isEmpty() ? volunteer.getCourse() : "Sin curso");

        TextView tvDni = view.findViewById(R.id.tvDetailDni);
        TextView tvBirthDate = view.findViewById(R.id.tvDetailBirthDate);
        TextView tvAvailability = view.findViewById(R.id.tvDetailAvailability); 
        
        tvDni.setText("DNI: " + (volunteer.getDni() != null ? volunteer.getDni() : ""));
        tvBirthDate.setText("Fecha Nac.: " + formatDate(volunteer.getBirthDate()));
        tvAvailability.setText("Disponibilidad: Lunes a Viernes, 16:00 - 20:00");

        com.google.android.material.chip.ChipGroup chipGroup = view.findViewById(R.id.chipGroupPreferences);
        TextView tvNoPreferences = view.findViewById(R.id.tvDetailNoPreferences);
        
        Runnable updateUI = () -> {
            if (volunteer.getPreferences() != null && !volunteer.getPreferences().isEmpty()) {
                chipGroup.setVisibility(View.VISIBLE);
                tvNoPreferences.setVisibility(View.GONE);
                chipGroup.removeAllViews();
                
                for (String pref : volunteer.getPreferences()) {
                    String typeName = cuatrovientos.voluntariado.utils.ActivityMapper.getTypeName(pref);

                    com.google.android.material.chip.Chip chip = new com.google.android.material.chip.Chip(getContext());
                    chip.setText(typeName);
                    chip.setTextColor(android.graphics.Color.WHITE);
                    chip.setChipCornerRadius(0f); 
                    
                    int color = cuatrovientos.voluntariado.utils.ActivityMapper.getColorForType(typeName);
                    chip.setChipBackgroundColor(android.content.res.ColorStateList.valueOf(color));
                    
                    chipGroup.addView(chip);
                }
            } else {
                chipGroup.setVisibility(View.GONE);
                tvNoPreferences.setVisibility(View.VISIBLE);
            }
    
            tvStatus.setText(volunteer.getStatus());
            if ("Active".equalsIgnoreCase(volunteer.getStatus()) || "ACTIVO".equalsIgnoreCase(volunteer.getStatus())) {
                tvStatus.setBackgroundColor(android.graphics.Color.parseColor("#E8F5E9"));
                tvStatus.setTextColor(android.graphics.Color.parseColor("#4CAF50"));
                tvStatus.setText("Activo");
            } else if ("Suspended".equalsIgnoreCase(volunteer.getStatus()) || "Suspendido".equalsIgnoreCase(volunteer.getStatus())) {
                tvStatus.setBackgroundColor(android.graphics.Color.parseColor("#FFEBEE"));
                tvStatus.setTextColor(android.graphics.Color.parseColor("#D32F2F"));
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
        };
        
        updateUI.run(); 

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

        androidx.recyclerview.widget.RecyclerView recyclerActivities = view.findViewById(R.id.recyclerVolunteerActivities);
        recyclerActivities.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(getContext()));
        cuatrovientos.voluntariado.adapters.SimpleActivityAdapter activitiesAdapter = new cuatrovientos.voluntariado.adapters.SimpleActivityAdapter(new java.util.ArrayList<>());
        recyclerActivities.setAdapter(activitiesAdapter);

        if (volunteer.getId() != null) {
            cuatrovientos.voluntariado.network.ApiService apiService = cuatrovientos.voluntariado.network.RetrofitClient.getClient().create(cuatrovientos.voluntariado.network.ApiService.class);
            
            apiService.getVolunteer(volunteer.getId()).enqueue(new retrofit2.Callback<cuatrovientos.voluntariado.network.model.ApiVolunteer>() {
                @Override
                public void onResponse(retrofit2.Call<cuatrovientos.voluntariado.network.model.ApiVolunteer> call, retrofit2.Response<cuatrovientos.voluntariado.network.model.ApiVolunteer> response) {
                     progressBar.setVisibility(View.GONE);
                     layoutContent.setVisibility(View.VISIBLE);

                    if (response.isSuccessful() && response.body() != null) {
                         cuatrovientos.voluntariado.network.model.ApiVolunteer apiVol = response.body();
                         java.util.List<String> availability = new java.util.ArrayList<>();
                         if (apiVol.getAvailability() != null) {
                            for (cuatrovientos.voluntariado.network.model.ApiAvailability av : apiVol.getAvailability()) {
                                availability.add(av.getDay() + ": " + av.getTime());
                            }
                         }

                         volunteer = new Volunteer(
                             apiVol.getId(),
                             apiVol.getName(),
                             apiVol.getSurname1(),
                             apiVol.getSurname2(),
                             apiVol.getEmail(),
                             apiVol.getPhone(),
                             apiVol.getDni(),
                             apiVol.getDateOfBirth(),
                             apiVol.getDescription(),
                             "Voluntario", 
                             apiVol.getPreferences(),
                             apiVol.getStatus(),
                             apiVol.getAvatar(),
                             apiVol.getCourse(), 
                             availability
                         );
                         
                         if (getContext() != null) {
                             String fullN = volunteer.getName();
                             if (volunteer.getSurname1() != null) fullN += " " + volunteer.getSurname1();
                             if (volunteer.getSurname2() != null) fullN += " " + volunteer.getSurname2();
                             tvName.setText(fullN);
                             
                             tvEmail.setText(volunteer.getEmail());
                             tvPhone.setText(volunteer.getPhone());
                             tvRole.setText(volunteer.getCourse() != null && !volunteer.getCourse().isEmpty() ? volunteer.getCourse() : "Sin curso");
                             
                             tvDni.setText("DNI: " + (volunteer.getDni() != null ? volunteer.getDni() : ""));
                             tvBirthDate.setText("Fecha Nac.: " + formatDate(volunteer.getBirthDate()));
                             
                             if (volunteer.getAvailability() != null && !volunteer.getAvailability().isEmpty()) {
                                 StringBuilder sb = new StringBuilder();
                                 sb.append("Disponibilidad:\n");
                                 for (String avail : volunteer.getAvailability()) {
                                     sb.append("• ").append(avail).append("\n");
                                 }
                                 tvAvailability.setText(sb.toString().trim()); 
                             } else {
                                 tvAvailability.setText("Sin disponibilidad especificada.");
                             }
                             
                             updateUI.run(); 
                         }
                    }
                }
                @Override
                public void onFailure(retrofit2.Call<cuatrovientos.voluntariado.network.model.ApiVolunteer> call, Throwable t) { 
                     progressBar.setVisibility(View.GONE);
                     layoutContent.setVisibility(View.VISIBLE);
                }
            });

            apiService.getVolunteerActivities(volunteer.getId()).enqueue(new retrofit2.Callback<java.util.List<cuatrovientos.voluntariado.network.model.ApiActivity>>() {
                @Override
                public void onResponse(retrofit2.Call<java.util.List<cuatrovientos.voluntariado.network.model.ApiActivity>> call, retrofit2.Response<java.util.List<cuatrovientos.voluntariado.network.model.ApiActivity>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        java.util.List<cuatrovientos.voluntariado.model.VolunteerActivity> mappedList = new java.util.ArrayList<>();
                        for (cuatrovientos.voluntariado.network.model.ApiActivity apiAct : response.body()) {
                            String status = apiAct.getStatus();
                            if (status != null && (status.equalsIgnoreCase("Finished") || status.equalsIgnoreCase("FINALIZADA"))) {
                                continue;
                            }
                            
                            cuatrovientos.voluntariado.model.VolunteerActivity volAct = cuatrovientos.voluntariado.utils.ActivityMapper.mapApiToModel(apiAct);
                            if (volAct != null) {
                                mappedList.add(volAct);
                            }
                        }
                        activitiesAdapter.updateList(mappedList);
                    }
                }

                @Override
                public void onFailure(retrofit2.Call<java.util.List<cuatrovientos.voluntariado.network.model.ApiActivity>> call, Throwable t) {
                }
            });
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
    private String formatDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) return "";
        try {
            String cleanDate = dateStr.contains("T") ? dateStr.split("T")[0] : dateStr;
            java.text.SimpleDateFormat inputFormat = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
            java.text.SimpleDateFormat outputFormat = new java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault());
            java.util.Date date = inputFormat.parse(cleanDate);
            return outputFormat.format(date);
        } catch (Exception e) {
            return dateStr;
        }
    }
}
