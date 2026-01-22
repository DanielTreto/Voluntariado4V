package cuatrovientos.voluntariado.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import cuatrovientos.voluntariado.R;
import cuatrovientos.voluntariado.model.Organization;
import cuatrovientos.voluntariado.model.VolunteerActivity;
import cuatrovientos.voluntariado.adapters.ActivitiesAdapter;
import cuatrovientos.voluntariado.network.ApiService;
import cuatrovientos.voluntariado.network.RetrofitClient;
import cuatrovientos.voluntariado.network.model.ApiActivity;

public class OrganizationDetailDialog extends DialogFragment {

    private static final String ARG_ORG = "arg_org";
    private Organization organization;
    private RecyclerView recyclerActivities;
    private cuatrovientos.voluntariado.adapters.SimpleActivityAdapter activitiesAdapter;

    public static OrganizationDetailDialog newInstance(Organization org) {
         OrganizationDetailDialog fragment = new OrganizationDetailDialog();
         Bundle args = new Bundle();
         // Serializar objeto a JSON para pasar argumentos
         com.google.gson.Gson gson = new com.google.gson.Gson();
         String json = gson.toJson(org);
         args.putString(ARG_ORG, json);
         fragment.setArguments(args);
         return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String json = getArguments().getString(ARG_ORG);
            organization = new com.google.gson.Gson().fromJson(json, Organization.class);
        }
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_organization_detail, container, false);

        ImageView btnClose = view.findViewById(R.id.btnClose);
        List<androidx.fragment.app.Fragment> fragments = getParentFragmentManager().getFragments();
        List<DialogFragment> dialogs = new java.util.ArrayList<>();
        for (androidx.fragment.app.Fragment f : fragments) {
            if (f instanceof DialogFragment) {
                dialogs.add((DialogFragment) f);
            }
        }

        // BTN CERRAR (X) -> Cierra la Pila
        btnClose.setOnClickListener(v -> {
             for (DialogFragment d : dialogs) {
                 d.dismiss();
             }
        });

        // BTN ATRAS (Flecha) -> Cierra Actual
        ImageView btnCloseStack = view.findViewById(R.id.btnCloseStack);
        if (dialogs.size() > 1) {
            btnCloseStack.setVisibility(View.VISIBLE);
            btnCloseStack.setOnClickListener(v -> dismiss());
        }

        if (organization == null) return view;

        ImageView imgHeader = view.findViewById(R.id.imgDetailHeader);
        TextView tvName = view.findViewById(R.id.tvDetailName);
        TextView tvStatus = view.findViewById(R.id.tvDetailStatus);
        TextView tvEmail = view.findViewById(R.id.tvDetailEmail);
        TextView tvDesc = view.findViewById(R.id.tvDetailDesc);

        tvName.setText(organization.getName());
        tvEmail.setText(organization.getEmail());
        
        // Fecha eliminada del layout

        // Comprobación de Estado
        tvStatus.setText(organization.getStatus());
         if ("Active".equalsIgnoreCase(organization.getStatus()) || "Activo".equalsIgnoreCase(organization.getStatus())) {
              tvStatus.setBackgroundColor(android.graphics.Color.parseColor("#E8F5E9"));
              tvStatus.setTextColor(android.graphics.Color.parseColor("#4CAF50"));
              tvStatus.setText("Activa");
         } else if ("Suspended".equalsIgnoreCase(organization.getStatus()) || "Suspendido".equalsIgnoreCase(organization.getStatus()) || "Suspendida".equalsIgnoreCase(organization.getStatus())) {
              tvStatus.setBackgroundColor(android.graphics.Color.parseColor("#FFEBEE"));
              tvStatus.setTextColor(android.graphics.Color.parseColor("#D32F2F"));
              tvStatus.setText("Suspendida");
         } else {
              tvStatus.setBackgroundColor(android.graphics.Color.parseColor("#FFF8E1"));
              tvStatus.setTextColor(android.graphics.Color.parseColor("#FFA000"));
              tvStatus.setText("Pendiente");
         }

        // Vincular Nuevos Campos
        TextView tvPhone = view.findViewById(R.id.tvDetailPhone);
        TextView tvType = view.findViewById(R.id.tvDetailType);
        TextView tvSector = view.findViewById(R.id.tvDetailSector);
        TextView tvScope = view.findViewById(R.id.tvDetailScope);
        TextView tvContact = view.findViewById(R.id.tvDetailContact);

        tvPhone.setText(organization.getPhone() != null ? organization.getPhone() : "");
        tvType.setText(organization.getType() != null ? organization.getType() : "");
        tvSector.setText(organization.getSector() != null ? organization.getSector() : "");
        tvScope.setText(organization.getScope() != null ? organization.getScope() : "");
        
        if (organization.getContactPerson() != null && !organization.getContactPerson().isEmpty()) {
            tvContact.setText(organization.getContactPerson());
        } else {
            tvContact.setText("Sin persona de contacto asignada");
        }

        if (organization.getDescription() != null && !organization.getDescription().isEmpty()) {
            tvDesc.setText(organization.getDescription());
        } else {
            tvDesc.setText("Sin descripción disponible.");
        }


        if (organization.getAvatarUrl() != null && !organization.getAvatarUrl().isEmpty()) {
             Glide.with(this)
                 .load(organization.getAvatarUrl())
                 .placeholder(R.drawable.ic_business)
                 .error(R.drawable.ic_business)
                 .centerCrop()
                 .into(imgHeader);
        } else {
             imgHeader.setImageResource(R.drawable.ic_business);
        }
        
        // Poblado Inicial
        updateUI(view);

        // Obtener detalles completos si es probable que estén incompletos (ej. falta descripción)
        if (organization.getDescription() == null || organization.getDescription().isEmpty() || organization.getType() == null) {
            fetchOrganizationDetails(view);
        } else {
             view.findViewById(R.id.layoutDetailContent).setVisibility(View.VISIBLE);
        }

        // Setup RecyclerView
        recyclerActivities = view.findViewById(R.id.recyclerOrgActivities);
        recyclerActivities.setLayoutManager(new LinearLayoutManager(getContext()));
        activitiesAdapter = new cuatrovientos.voluntariado.adapters.SimpleActivityAdapter(new ArrayList<>());
        recyclerActivities.setAdapter(activitiesAdapter);

        loadActivities();

        return view;
    }

    private void loadActivities() {
        if (organization.getId() == null) return;

        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        apiService.getOrganizationActivities(organization.getId()).enqueue(new Callback<List<ApiActivity>>() {
            @Override
            public void onResponse(Call<List<ApiActivity>> call, Response<List<ApiActivity>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<VolunteerActivity> mappedList = new ArrayList<>();
                    for (ApiActivity apiAct : response.body()) {
                        // Image URL handled by Mapper
                        
                        // Mapeo de ApiActivity a VolunteerActivity usando el Mapper estándar
                        // Esto asegura que ODS y Voluntarios se pueblen correctamente
                        VolunteerActivity volAct = cuatrovientos.voluntariado.utils.ActivityMapper.mapApiToModel(apiAct);
                        
                        // Sobrescribir info de Organización ya que estamos en el contexto de la Organización
                        // (El endpoint de actividades de organización no siempre devuelve el objeto organización anidado)
                        
                        if (volAct != null) {
                            volAct.setOrganizationName(organization.getName());
                            volAct.setOrganizationAvatar(organization.getAvatarUrl());
                            mappedList.add(volAct);
                        }
                    }
                    activitiesAdapter.updateList(mappedList);
                }
            }

            @Override
            public void onFailure(Call<List<ApiActivity>> call, Throwable t) {
                if (getContext() != null)
                    Toast.makeText(getContext(), "Error cargando actividades", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI(View view) {
        if (organization == null || view == null) return;

        TextView tvName = view.findViewById(R.id.tvDetailName);
        TextView tvStatus = view.findViewById(R.id.tvDetailStatus);
        TextView tvEmail = view.findViewById(R.id.tvDetailEmail);
        TextView tvDesc = view.findViewById(R.id.tvDetailDesc);
        TextView tvPhone = view.findViewById(R.id.tvDetailPhone);
        TextView tvType = view.findViewById(R.id.tvDetailType);
        TextView tvSector = view.findViewById(R.id.tvDetailSector);
        TextView tvScope = view.findViewById(R.id.tvDetailScope);
        TextView tvContact = view.findViewById(R.id.tvDetailContact);

        tvName.setText(organization.getName());
        tvEmail.setText(organization.getEmail() != null ? organization.getEmail() : "Correo no disponible");
        
        tvStatus.setText(organization.getStatus() != null ? organization.getStatus() : "Desconocido");
         if ("Active".equalsIgnoreCase(organization.getStatus()) || "Activo".equalsIgnoreCase(organization.getStatus())) {
              tvStatus.setBackgroundColor(android.graphics.Color.parseColor("#E8F5E9"));
              tvStatus.setTextColor(android.graphics.Color.parseColor("#4CAF50"));
              tvStatus.setText("Activa");
         } else if ("Suspended".equalsIgnoreCase(organization.getStatus()) || "Suspendido".equalsIgnoreCase(organization.getStatus()) || "Suspendida".equalsIgnoreCase(organization.getStatus())) {
              tvStatus.setBackgroundColor(android.graphics.Color.parseColor("#FFEBEE"));
              tvStatus.setTextColor(android.graphics.Color.parseColor("#D32F2F"));
              tvStatus.setText("Suspendida");
         } else {
              tvStatus.setBackgroundColor(android.graphics.Color.parseColor("#FFF8E1"));
              tvStatus.setTextColor(android.graphics.Color.parseColor("#FFA000"));
              tvStatus.setText("Pendiente");
         }

        tvPhone.setText(organization.getPhone() != null ? organization.getPhone() : "");
        tvType.setText(organization.getType() != null ? organization.getType() : "");
        tvSector.setText(organization.getSector() != null ? organization.getSector() : "");
        tvScope.setText(organization.getScope() != null ? organization.getScope() : "");
        
        if (organization.getContactPerson() != null && !organization.getContactPerson().isEmpty()) {
            tvContact.setText(organization.getContactPerson());
        } else {
            tvContact.setText("Sin persona de contacto asignada");
        }

        if (organization.getDescription() != null && !organization.getDescription().isEmpty()) {
            tvDesc.setText(organization.getDescription());
        } else {
            tvDesc.setText("Sin descripción disponible.");
        }
    }

    private void fetchOrganizationDetails(View view) {
        if (organization.getId() == null) return;

        android.widget.ProgressBar progressBar = view.findViewById(R.id.progressBar);
        View contentLayout = view.findViewById(R.id.layoutDetailContent);

        progressBar.setVisibility(View.VISIBLE);
        contentLayout.setVisibility(View.GONE);

        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        apiService.getOrganization(organization.getId()).enqueue(new Callback<Organization>() {
             @Override
             public void onResponse(Call<Organization> call, Response<Organization> response) {
                 if (response.isSuccessful() && response.body() != null) {
                     // Update current organization object with fetched details
                     Organization fetched = response.body();
                     organization.setEmail(fetched.getEmail());
                     organization.setDescription(fetched.getDescription());
                     organization.setType(fetched.getType());
                     organization.setPhone(fetched.getPhone());
                     organization.setSector(fetched.getSector());
                     organization.setScope(fetched.getScope());
                     organization.setContactPerson(fetched.getContactPerson());
                     organization.setStatus(fetched.getStatus());
                     // Avoid overwriting Name/Avatar if they are visually correct, but safe to overwrite usually.
                     organization.setName(fetched.getName());
                     organization.setAvatarUrl(fetched.getAvatarUrl());
                     
                     if (getContext() != null) {
                         updateUI(view);
                         // Also refresh avatar if URL changed/loaded
                         ImageView imgHeader = view.findViewById(R.id.imgDetailHeader);
                         if (organization.getAvatarUrl() != null && !organization.getAvatarUrl().isEmpty()) {
                              Glide.with(getContext()) // Use getContext() safely inside callback
                                  .load(organization.getAvatarUrl())
                                  .placeholder(R.drawable.ic_business)
                                  .error(R.drawable.ic_business)
                                  .centerCrop()
                                  .into(imgHeader);
                         }
                     }
                 }
                 if (getContext() != null) {
                     progressBar.setVisibility(View.GONE);
                     contentLayout.setVisibility(View.VISIBLE);
                 }
             }

             @Override
             public void onFailure(Call<Organization> call, Throwable t) {
                 if (getContext() != null) {
                     progressBar.setVisibility(View.GONE);
                     contentLayout.setVisibility(View.VISIBLE);
                     Toast.makeText(getContext(), "Error cargando detalles", Toast.LENGTH_SHORT).show();
                 }
             }
        });
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
