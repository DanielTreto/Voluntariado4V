package cuatrovientos.voluntariado.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import cuatrovientos.voluntariado.R;
import cuatrovientos.voluntariado.model.Organization;
import com.bumptech.glide.Glide;
import android.widget.ImageView;

public class OrganizationsAdapter extends RecyclerView.Adapter<OrganizationsAdapter.OrgViewHolder> {

    private List<Organization> orgList;

    public OrganizationsAdapter(List<Organization> orgList) {
        this.orgList = orgList;
    }

    public void updateList(List<Organization> newList) {
        this.orgList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrgViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_organization_card, parent, false);
        return new OrgViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrgViewHolder holder, int position) {
        Organization org = orgList.get(position);

        holder.tvName.setText(org.getName());
        holder.tvDescription.setText(org.getDescription() != null && !org.getDescription().isEmpty() ? org.getDescription() : "Sin descripción disponible.");
        holder.tvStatus.setText(org.getStatus());
        
        holder.tvEmailInfo.setText(org.getEmail());

        // Teléfono (Común)
        String phone = org.getPhone();
        if (phone == null || phone.isEmpty()) holder.tvPhone.setText("Sin teléfono");
        else holder.tvPhone.setText(phone);

        if (org.getAvatarUrl() != null) {
             Glide.with(holder.itemView.getContext())
                 .load(org.getAvatarUrl())
                 .placeholder(R.drawable.ic_business)
                 .error(R.drawable.ic_business)
                 .circleCrop()
                 .into(holder.imgLogo);
        } else {
             holder.imgLogo.setImageResource(R.drawable.ic_business);
             holder.imgLogo.setBackground(null);
        }

        if (org.getStatus().equals("Pending")) {
            // --- MODO SOLICITUD ---
            holder.actionsLayout.setVisibility(View.GONE);
            holder.btnMoreOptions.setVisibility(View.VISIBLE);
            holder.btnMoreOptions.setOnClickListener(v -> {
                 if (v.getContext() instanceof androidx.fragment.app.FragmentActivity) {
                      androidx.fragment.app.FragmentActivity activity = (androidx.fragment.app.FragmentActivity) v.getContext();
                      cuatrovientos.voluntariado.dialogs.OrganizationDetailDialog dialog = 
                          cuatrovientos.voluntariado.dialogs.OrganizationDetailDialog.newInstance(org);
                      dialog.show(activity.getSupportFragmentManager(), "OrganizationDetailDialog");
                 }
            });

            // Ocultar contador de voluntarios
            holder.tvVolunteersCount.setVisibility(View.GONE);

            // Color Pendiente
            holder.tvStatus.setBackgroundColor(Color.parseColor("#FFF8E1"));
            holder.tvStatus.setTextColor(Color.parseColor("#FFA000"));
            holder.tvStatus.setText("Pendiente");

        } else {
            // --- MODO REGISTRADA ---
            holder.actionsLayout.setVisibility(View.GONE);
            holder.btnMoreOptions.setVisibility(View.VISIBLE);
            holder.btnMoreOptions.setOnClickListener(v -> {
                 if (v.getContext() instanceof androidx.fragment.app.FragmentActivity) {
                      androidx.fragment.app.FragmentActivity activity = (androidx.fragment.app.FragmentActivity) v.getContext();
                      cuatrovientos.voluntariado.dialogs.OrganizationDetailDialog dialog = 
                          cuatrovientos.voluntariado.dialogs.OrganizationDetailDialog.newInstance(org);
                      dialog.show(activity.getSupportFragmentManager(), "OrganizationDetailDialog");
                 }
            });

            // Mostrar contador de voluntarios
            holder.tvVolunteersCount.setVisibility(View.VISIBLE);
            holder.tvVolunteersCount.setText(org.getVolunteersCount() + " Voluntariados creados");

            // Color Activo/Suspendido
            if (org.getStatus().equals("Active")) {
                holder.tvStatus.setBackgroundColor(Color.parseColor("#E8F5E9"));
                holder.tvStatus.setTextColor(Color.parseColor("#4CAF50"));
                holder.tvStatus.setText("Activa");
            } else if (org.getStatus().equals("Suspended")) {
                holder.tvStatus.setBackgroundColor(Color.parseColor("#FFEBEE"));
                holder.tvStatus.setTextColor(Color.parseColor("#D32F2F"));
                holder.tvStatus.setText("Suspendida");
            } else {
                holder.tvStatus.setText(org.getStatus());
            }
        }
    }

    @Override
    public int getItemCount() {
        return orgList.size();
    }

    public static class OrgViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDescription, tvStatus, tvPhone, tvVolunteersCount, tvEmailInfo;
        android.widget.ImageView btnMoreOptions;
        ImageView imgLogo;
        LinearLayout actionsLayout;

        public OrgViewHolder(@NonNull View itemView) {
            super(itemView);
            imgLogo = itemView.findViewById(R.id.imgLogo);
            tvName = itemView.findViewById(R.id.tvOrgName);
            tvDescription = itemView.findViewById(R.id.tvOrgDescription);
            tvStatus = itemView.findViewById(R.id.chipOrgStatus);
            tvEmailInfo = itemView.findViewById(R.id.tvOrgEmailInfo);
            tvPhone = itemView.findViewById(R.id.tvOrgPhone);
            tvVolunteersCount = itemView.findViewById(R.id.tvOrgVolunteersCount);
            btnMoreOptions = itemView.findViewById(R.id.btnOrgMoreOptions);
            actionsLayout = itemView.findViewById(R.id.actionsOrgLayout);
        }
    }
}