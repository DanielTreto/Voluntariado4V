package cuatrovientos.voluntariado.adapters;

import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import cuatrovientos.voluntariado.R;
import cuatrovientos.voluntariado.model.VolunteerActivity;

public class ActivitiesAdapter extends RecyclerView.Adapter<ActivitiesAdapter.ActivityViewHolder> {

    private List<VolunteerActivity> activityList;
    private boolean isStudent = false;

    public ActivitiesAdapter(List<VolunteerActivity> activityList) {
        this.activityList = activityList;
        this.isStudent = false;
    }

    public ActivitiesAdapter(List<VolunteerActivity> activityList, boolean isStudent) {
        this.activityList = activityList;
        this.isStudent = isStudent;
    }

    public void updateList(List<VolunteerActivity> newList) {
        this.activityList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ActivityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_activity_card, parent, false);
        return new ActivityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActivityViewHolder holder, int position) {
        VolunteerActivity act = activityList.get(position);

        holder.tvTitle.setText(act.getTitle());
        holder.tvDesc.setText(act.getDescription());
        holder.tvLocation.setText(act.getLocation());
        holder.tvDate.setText(act.getDate());
        holder.tvCategory.setText(act.getCategory());

        // Simular imagen con color
        holder.imgHeader.setColorFilter(act.getImageColor(), PorterDuff.Mode.SRC_IN);

        if (isStudent) {
            // Estudiante: No mostrar botones de gestión
            holder.layoutButtonsActive.setVisibility(View.GONE);
            holder.layoutButtonsPending.setVisibility(View.GONE);
        } else {
            if (act.getStatus().equals("Active")) {
                // Pestaña ACTIVIDADES: Mostrar botones de gestión
                holder.layoutButtonsActive.setVisibility(View.VISIBLE);
                holder.layoutButtonsPending.setVisibility(View.GONE);
            } else {
                // Pestaña SOLICITUDES: Mostrar botones Aceptar/Denegar grande
                holder.layoutButtonsActive.setVisibility(View.GONE);
                holder.layoutButtonsPending.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return activityList.size();
    }

    public static class ActivityViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDesc, tvLocation, tvDate, tvCategory;
        ImageView imgHeader;
        LinearLayout layoutButtonsActive, layoutButtonsPending;

        public ActivityViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvActTitle);
            tvDesc = itemView.findViewById(R.id.tvActDesc);
            tvLocation = itemView.findViewById(R.id.tvActLocation);
            tvDate = itemView.findViewById(R.id.tvActDate);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            imgHeader = itemView.findViewById(R.id.imgActivityHeader);

            layoutButtonsActive = itemView.findViewById(R.id.layoutButtonsActive);
            layoutButtonsPending = itemView.findViewById(R.id.layoutButtonsPending);
        }
    }
}