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
import cuatrovientos.voluntariado.model.Volunteer;
import com.bumptech.glide.Glide;
import android.widget.ImageView;
import cuatrovientos.voluntariado.dialogs.VolunteerDetailDialog;

public class VolunteersAdapter extends RecyclerView.Adapter<VolunteersAdapter.VolunteerViewHolder> {

    private List<Volunteer> volunteerList;

    public VolunteersAdapter(List<Volunteer> volunteerList) {
        this.volunteerList = volunteerList;
    }
    public void updateList(List<Volunteer> newList) {
        this.volunteerList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VolunteerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_volunteer_card, parent, false);
        return new VolunteerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VolunteerViewHolder holder, int position) {
        Volunteer volunteer = volunteerList.get(position);

        String fullName = volunteer.getName();
        if (volunteer.getSurname1() != null) fullName += " " + volunteer.getSurname1();
        if (volunteer.getSurname2() != null) fullName += " " + volunteer.getSurname2();
        
        holder.tvName.setText(fullName);
        holder.tvRole.setText(volunteer.getCourse() != null && !volunteer.getCourse().isEmpty() ? volunteer.getCourse() : "Sin curso");
        holder.tvEmail.setText(volunteer.getEmail());
        holder.tvPhone.setText(volunteer.getPhone());
        holder.tvDni.setText(volunteer.getDni());
        holder.tvStatus.setText(volunteer.getStatus());

        if (volunteer.getAvatarUrl() != null) {
             Glide.with(holder.itemView.getContext())
                 .load(volunteer.getAvatarUrl())
                 .placeholder(R.drawable.ic_profile_placeholder)
                 .error(R.drawable.ic_profile_placeholder)
                 .circleCrop()
                 .into(holder.imgAvatar);
        } else {
             holder.imgAvatar.setImageResource(R.drawable.ic_profile_placeholder);
        }

        if (volunteer.getStatus().equals("Active") || volunteer.getStatus().equals("Suspended")) {
            holder.actionsLayout.setVisibility(View.GONE);
            holder.btnMoreOptions.setVisibility(View.VISIBLE);
            holder.btnMoreOptions.setOnClickListener(v -> {
                 if (v.getContext() instanceof androidx.fragment.app.FragmentActivity) {
                      androidx.fragment.app.FragmentActivity activity = (androidx.fragment.app.FragmentActivity) v.getContext();
                      VolunteerDetailDialog dialog = VolunteerDetailDialog.newInstance(volunteer);
                      dialog.show(activity.getSupportFragmentManager(), "VolunteerDetailDialog");
                 }
            });

            if (volunteer.getStatus().equals("Active")) {
                holder.tvStatus.setBackgroundColor(Color.parseColor("#E8F5E9")); 
                holder.tvStatus.setTextColor(Color.parseColor("#4CAF50")); 
                holder.tvStatus.setText("Activo");
            } else if (volunteer.getStatus().equals("Suspended")) {
                holder.tvStatus.setBackgroundColor(Color.parseColor("#FFEBEE")); 
                holder.tvStatus.setTextColor(Color.parseColor("#D32F2F")); 
                holder.tvStatus.setText("Suspendido");
            }

        } else {
            holder.actionsLayout.setVisibility(View.GONE); 
            holder.btnMoreOptions.setVisibility(View.VISIBLE); 
            
            holder.btnMoreOptions.setOnClickListener(v -> {
                 if (v.getContext() instanceof androidx.fragment.app.FragmentActivity) {
                      androidx.fragment.app.FragmentActivity activity = (androidx.fragment.app.FragmentActivity) v.getContext();
                      VolunteerDetailDialog dialog = VolunteerDetailDialog.newInstance(volunteer);
                      dialog.show(activity.getSupportFragmentManager(), "VolunteerDetailDialog");
                 }
            });

            holder.tvStatus.setBackgroundColor(Color.parseColor("#FFF8E1")); 
            holder.tvStatus.setTextColor(Color.parseColor("#FFA000")); 
            holder.tvStatus.setText("Pendiente");
        }
    }

    @Override
    public int getItemCount() {
        return volunteerList.size();
    }

    public static class VolunteerViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvRole, tvEmail, tvPhone, tvStatus, tvDni;
        android.widget.ImageView btnMoreOptions;
        ImageView imgAvatar;
        LinearLayout actionsLayout;

        public VolunteerViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
            tvName = itemView.findViewById(R.id.tvName);
            tvRole = itemView.findViewById(R.id.tvRole);
            tvStatus = itemView.findViewById(R.id.chipStatus);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvPhone = itemView.findViewById(R.id.tvPhone);
            tvDni = itemView.findViewById(R.id.tvDni);

            actionsLayout = itemView.findViewById(R.id.actionsLayout);
            btnMoreOptions = itemView.findViewById(R.id.btnMoreOptions);
        }
    }
}