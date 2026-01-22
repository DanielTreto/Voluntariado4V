package cuatrovientos.voluntariado.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import cuatrovientos.voluntariado.R;
import cuatrovientos.voluntariado.model.EventDay;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder> {

    private List<EventDay> days;
    private OnEventClickListener listener;

    public interface OnEventClickListener {
        void onEventClick(java.util.List<cuatrovientos.voluntariado.model.VolunteerActivity> activities);
        void onEventClick(cuatrovientos.voluntariado.model.VolunteerActivity activity);
    }

    public CalendarAdapter(List<EventDay> days, OnEventClickListener listener) {
        this.days = days;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_calendar_day, parent, false);
        return new CalendarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position) {
        EventDay day = days.get(position);

        holder.tvDayNumber.setText(day.getDayNumber());

        if (day.getEventTitle() != null && !day.getEventTitle().isEmpty()) {
            holder.tvEventTag.setVisibility(View.VISIBLE);
            holder.tvEventTag.setText(day.getEventTitle());

            try {
                holder.tvEventTag.setBackgroundColor(Color.parseColor(day.getColorHex()));
            } catch (Exception e) {
                holder.tvEventTag.setBackgroundColor(Color.GRAY);
            }
            
            holder.itemView.setOnClickListener(v -> {
                if (listener != null && day.getActivity() != null) {
                    listener.onEventClick(day.getActivity());
                }
            });

        } else {
            holder.tvEventTag.setVisibility(View.INVISIBLE); 
            holder.itemView.setOnClickListener(null);
        }
    }

    @Override
    public int getItemCount() {
        return days.size();
    }

    public static class CalendarViewHolder extends RecyclerView.ViewHolder {
        TextView tvDayNumber, tvEventTag;

        public CalendarViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDayNumber = itemView.findViewById(R.id.tvDayNumber);
            tvEventTag = itemView.findViewById(R.id.tvEventTag);
        }
    }
}