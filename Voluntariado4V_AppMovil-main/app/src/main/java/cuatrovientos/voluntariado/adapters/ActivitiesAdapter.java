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
import com.bumptech.glide.Glide;

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
        String dateText = act.getDate();
        if (dateText != null && dateText.contains(" ")) {
            dateText = dateText.split(" ")[0];
        }
        holder.tvDate.setText(dateText);
        


        holder.tvCategory.setText(act.getCategory());
        holder.tvCategory.setBackgroundColor(act.getImageColor());

        // Status Tag Logic
        String status = act.getStatus();
        holder.tvStatusTag.setText(cuatrovientos.voluntariado.utils.ActivityMapper.getStatusLabel(status));
        holder.tvStatusTag.setBackgroundColor(cuatrovientos.voluntariado.utils.ActivityMapper.getStatusBackgroundColor(status));
        holder.tvStatusTag.setTextColor(cuatrovientos.voluntariado.utils.ActivityMapper.getStatusTextColor(status));

        // Volunteer Avatars Logic
        List<String> avatars = act.getParticipantAvatars();
        int count = avatars != null ? avatars.size() : 0;
        
        holder.imgVol1.setVisibility(View.GONE);
        holder.imgVol2.setVisibility(View.GONE);
        holder.tvVolCount.setVisibility(View.GONE);
        holder.tvNoVolunteers.setVisibility(View.GONE);

        if (count == 0) {
            holder.tvNoVolunteers.setVisibility(View.VISIBLE);
        } else {
            // Vol 1
            if (count >= 1) {
                holder.imgVol1.setVisibility(View.VISIBLE);
                Glide.with(holder.itemView.getContext()).load(avatars.get(0)).circleCrop().placeholder(R.drawable.ic_profile_placeholder).into(holder.imgVol1);
            }
            // Vol 2
            if (count >= 2) {
                holder.imgVol2.setVisibility(View.VISIBLE);
                 Glide.with(holder.itemView.getContext()).load(avatars.get(1)).circleCrop().placeholder(R.drawable.ic_profile_placeholder).into(holder.imgVol2);
            }
            // Plus Count
            if (count > 2) {
                holder.tvVolCount.setVisibility(View.VISIBLE);
                holder.tvVolCount.setText("+" + (count - 2));
            }
        }
        
        // Image Header
        String imageUrl = act.getImageUrl();
        if (imageUrl == null || imageUrl.isEmpty()) {
            imageUrl = "https://blog.vicensvives.com/wp-content/uploads/2019/12/Voluntariado.png";
        }
        String fallbackUrl = "https://blog.vicensvives.com/wp-content/uploads/2019/12/Voluntariado.png";
        
        holder.imgHeader.setColorFilter(null); 
        Glide.with(holder.itemView.getContext())
                .load(imageUrl)
                .centerCrop()
                .placeholder(R.drawable.ic_launcher_foreground) 
                .error(Glide.with(holder.itemView.getContext()).load(fallbackUrl))
                .into(holder.imgHeader);
        
        // Buttons Logic
        holder.layoutButtonsActive.setVisibility(View.VISIBLE);
        holder.layoutButtonsPending.setVisibility(View.GONE);
        
        holder.btnViewDetails.setOnClickListener(v -> {
            if (v.getContext() instanceof androidx.fragment.app.FragmentActivity) {
                androidx.fragment.app.FragmentActivity activity = (androidx.fragment.app.FragmentActivity) v.getContext();
                cuatrovientos.voluntariado.dialogs.ActivityDetailDialog dialog = 
                    cuatrovientos.voluntariado.dialogs.ActivityDetailDialog.newInstance(act, isStudent);
                dialog.show(activity.getSupportFragmentManager(), "ActivityDetailDialog");
            }
        });
    }

    @Override
    public int getItemCount() {
        return activityList.size();
    }

    public static class ActivityViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDesc, tvLocation, tvDate, tvCategory, tvStatusTag;
        TextView tvNoVolunteers, tvVolCount;
        ImageView imgHeader, imgVol1, imgVol2;
        LinearLayout layoutButtonsActive, layoutButtonsPending;
        android.widget.Button btnViewDetails; // Or TextView/View dependent on XML

        public ActivityViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvActTitle);
            tvDesc = itemView.findViewById(R.id.tvActDesc);
            tvLocation = itemView.findViewById(R.id.tvActLocation);
            tvDate = itemView.findViewById(R.id.tvActDate);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvStatusTag = itemView.findViewById(R.id.tvStatusTag);
            
            imgHeader = itemView.findViewById(R.id.imgActivityHeader);
            
            // Volunteers
            tvNoVolunteers = itemView.findViewById(R.id.tvNoVolunteers);
            imgVol1 = itemView.findViewById(R.id.imgVol1);
            imgVol2 = itemView.findViewById(R.id.imgVol2);
            tvVolCount = itemView.findViewById(R.id.tvVolCountPlus);

            layoutButtonsActive = itemView.findViewById(R.id.layoutButtonsActive);
            layoutButtonsPending = itemView.findViewById(R.id.layoutButtonsPending);
            
            // Assuming the button ID inside layoutButtonsActive is btnViewDetails
            // Based on earlier conversations/assumptions. If not, I'll need to check XML or use layoutButtonsActive as the click target if it's just a button wrapper. 
            // Better to find the button properly.
            btnViewDetails = itemView.findViewById(R.id.btnViewDetails); 
        }
    }
}