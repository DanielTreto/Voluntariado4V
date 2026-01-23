package cuatrovientos.voluntariado.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.ImageButton;
import java.util.List;
import cuatrovientos.voluntariado.R;
import cuatrovientos.voluntariado.model.VolunteerActivity;

public class SimpleActivityAdapter extends RecyclerView.Adapter<SimpleActivityAdapter.SimpleViewHolder> {

    private List<VolunteerActivity> activityList;

    public SimpleActivityAdapter(List<VolunteerActivity> activityList) {
        this.activityList = activityList;
    }

    public void updateList(List<VolunteerActivity> newList) {
        this.activityList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SimpleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_activity_simple, parent, false);
        return new SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SimpleViewHolder holder, int position) {
        VolunteerActivity act = activityList.get(position);

        holder.tvTitle.setText(act.getTitle());
        holder.tvDesc.setText(act.getDescription());
        holder.tvType.setText(act.getCategory());
        
        String dateText = act.getDate();
        if (dateText != null && dateText.contains(" ")) {
            dateText = dateText.split(" ")[0];
        }
        holder.tvDate.setText(dateText);

        String status = act.getStatus();
        holder.tvStatus.setText(cuatrovientos.voluntariado.utils.ActivityMapper.getStatusLabel(status));
        holder.tvStatus.setBackgroundColor(cuatrovientos.voluntariado.utils.ActivityMapper.getStatusBackgroundColor(status));
        holder.tvStatus.setTextColor(cuatrovientos.voluntariado.utils.ActivityMapper.getStatusTextColor(status));

        holder.tvType.setBackgroundColor(act.getImageColor());
        holder.tvType.setTextColor(android.graphics.Color.WHITE);

        View.OnClickListener listener = v -> {
            androidx.fragment.app.FragmentActivity activity = getActivity(v.getContext());
            if (activity != null) {
                android.content.SharedPreferences prefs = activity.getSharedPreferences("UserSession", android.content.Context.MODE_PRIVATE);
                String userRole = prefs.getString("USER_ROLE", "");
                boolean isStudent = "volunteer".equalsIgnoreCase(userRole) || "admin".equalsIgnoreCase(userRole) || "administrator".equalsIgnoreCase(userRole) 
                                    || "organization".equalsIgnoreCase(userRole) || "organizacion".equalsIgnoreCase(userRole);

                cuatrovientos.voluntariado.dialogs.ActivityDetailDialog dialog = 
                    cuatrovientos.voluntariado.dialogs.ActivityDetailDialog.newInstance(act, isStudent);
                 dialog.show(activity.getSupportFragmentManager(), "ActivityDetailDialog");
            }
        };

        holder.btnOpenDetail.setOnClickListener(listener);
        holder.itemView.setOnClickListener(listener);
    }

    @NonNull
    private androidx.fragment.app.FragmentActivity getActivity(android.content.Context context) {
        while (context instanceof android.content.ContextWrapper) {
            if (context instanceof androidx.fragment.app.FragmentActivity) {
                return (androidx.fragment.app.FragmentActivity) context;
            }
            context = ((android.content.ContextWrapper) context).getBaseContext();
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return activityList.size();
    }

    public static class SimpleViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDesc, tvStatus, tvType, tvDate;
        android.widget.ImageButton btnOpenDetail;

        public SimpleViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvSimpleTitle);
            tvDesc = itemView.findViewById(R.id.tvSimpleDesc);
            tvStatus = itemView.findViewById(R.id.tvSimpleStatus);
            tvType = itemView.findViewById(R.id.tvSimpleType);
            tvDate = itemView.findViewById(R.id.tvSimpleDate);
            btnOpenDetail = itemView.findViewById(R.id.btnOpenDetail);
        }
    }
}
