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
        holder.tvEmail.setText(org.getEmail());
        holder.tvStatus.setText(org.getStatus());

        if (org.getStatus().equals("Pending")) {
            // --- MODO SOLICITUD ---
            // 1. Mostrar botones Aceptar/Denegar
            holder.actionsLayout.setVisibility(View.VISIBLE);
            // 2. Ocultar los 3 puntos
            holder.btnMoreOptions.setVisibility(View.GONE);

            // 3. Mostrar FECHA
            holder.tvInfo.setText("Fecha solicitud: " + org.getDate());

            // 4. Color Pendiente (Azul claro/Cyan según tu imagen)
            holder.tvStatus.setBackgroundColor(Color.parseColor("#E0F7FA"));
            holder.tvStatus.setTextColor(Color.parseColor("#006064"));
            holder.tvStatus.setText("Pendiente");

        } else {
            // --- MODO REGISTRADA ---
            // 1. Ocultar botones
            holder.actionsLayout.setVisibility(View.GONE);
            // 2. Mostrar 3 puntos
            holder.btnMoreOptions.setVisibility(View.VISIBLE);

            // 3. Mostrar CONTADOR DE VOLUNTARIOS
            holder.tvInfo.setText("Voluntariados creados: " + org.getVolunteersCount());

            // 4. Color Activo (Verde)
            holder.tvStatus.setBackgroundColor(Color.parseColor("#E8F5E9"));
            holder.tvStatus.setTextColor(Color.parseColor("#4CAF50"));
        }
    }

    @Override
    public int getItemCount() {
        return orgList.size();
    }

    public static class OrgViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvEmail, tvStatus, tvInfo, btnMoreOptions;
        LinearLayout actionsLayout;

        public OrgViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvOrgName);
            tvEmail = itemView.findViewById(R.id.tvOrgEmail);
            tvStatus = itemView.findViewById(R.id.chipOrgStatus);
            tvInfo = itemView.findViewById(R.id.tvOrgInfo);
            btnMoreOptions = itemView.findViewById(R.id.btnOrgMoreOptions);
            actionsLayout = itemView.findViewById(R.id.actionsOrgLayout);
        }
    }
}