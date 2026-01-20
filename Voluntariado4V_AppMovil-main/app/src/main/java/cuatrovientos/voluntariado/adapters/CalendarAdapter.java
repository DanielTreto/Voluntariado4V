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

    public CalendarAdapter(List<EventDay> days) {
        this.days = days;
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

        // Poner el número
        holder.tvDayNumber.setText(day.getDayNumber());

        // Configurar el evento
        if (day.getEventTitle() != null && !day.getEventTitle().isEmpty()) {
            holder.tvEventTag.setVisibility(View.VISIBLE);
            holder.tvEventTag.setText(day.getEventTitle());

            // Parsear el color hexadecimal
            try {
                holder.tvEventTag.setBackgroundColor(Color.parseColor(day.getColorHex()));
            } catch (Exception e) {
                holder.tvEventTag.setBackgroundColor(Color.GRAY); // Color por defecto si falla
            }
        } else {
            holder.tvEventTag.setVisibility(View.INVISIBLE); // Invisible para mantener altura o GONE para colapsar
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