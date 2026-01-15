package cuatrovientos.voluntariado.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import cuatrovientos.voluntariado.R;
import cuatrovientos.voluntariado.adapters.CalendarAdapter;
import cuatrovientos.voluntariado.model.EventDay;

public class EventsFragment extends Fragment {

    public EventsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_events, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerCalendar);

        // CLAVE: Usar GridLayoutManager con 7 columnas (días de la semana)
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 7));

        List<EventDay> days = new ArrayList<>();

        // Simulación: Diciembre 2025
        // Supongamos que el día 1 empieza en Lunes (basado en tu imagen)
        // Llenamos del 1 al 31

        for (int i = 1; i <= 31; i++) {
            String num = String.valueOf(i);
            String title = null;
            String color = null;

            // Agregamos los eventos específicos de la imagen
            if (i == 5) { // Viernes 5
                title = "Limpieza de Playa";
                color = "#2E7D32"; // Verde oscuro
            } else if (i == 10) { // Miércoles 10
                title = "Taller de Programación";
                color = "#1976D2"; // Azul
            } else if (i == 15) { // Lunes 15
                title = "Reparto de Alimentos";
                color = "#FFC107"; // Amarillo/Ámbar
            }

            days.add(new EventDay(num, title, color));
        }

        CalendarAdapter adapter = new CalendarAdapter(days);
        recyclerView.setAdapter(adapter);
    }
}