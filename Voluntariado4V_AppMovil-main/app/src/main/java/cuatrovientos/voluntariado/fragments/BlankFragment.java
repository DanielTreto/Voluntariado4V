package cuatrovientos.voluntariado.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.graphics.Color;
import android.view.Gravity;

// No necesitamos layout XML para este ejemplo rápido, lo crearemos por código
// para asegurarnos de que ves los cambios.
/**
 * Fragmento en blanco de ejemplo.
 * No requiere layout XML, se genera programáticamente.
 */
public class BlankFragment extends Fragment {

    private static final String ARG_TITLE = "title";
    private static final String ARG_SUBTITLE = "subtitle";

    private String mTitle;
    private String mSubtitle;

    public BlankFragment() {
        // Constructor vacío requerido
    }

    public static BlankFragment newInstance(String title, String subtitle) {
        BlankFragment fragment = new BlankFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putString(ARG_SUBTITLE, subtitle);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTitle = getArguments().getString(ARG_TITLE);
            mSubtitle = getArguments().getString(ARG_SUBTITLE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Creamos una vista simple programáticamente para mostrar dónde estamos
        TextView textView = new TextView(getActivity());
        textView.setText(mTitle + "\n\n" + mSubtitle);
        textView.setTextSize(24);
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(Color.BLACK);
        textView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        return textView;
    }
}