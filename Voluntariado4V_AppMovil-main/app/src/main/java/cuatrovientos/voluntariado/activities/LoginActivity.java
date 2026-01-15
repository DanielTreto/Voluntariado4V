package cuatrovientos.voluntariado.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import cuatrovientos.voluntariado.R;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etUsername;
    private TextInputEditText etPassword;
    private MaterialButton btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString().trim();
                
                if (username.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Por favor ingrese un usuario", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (username.equalsIgnoreCase("admin")) {
                    // Ir al Dashboard de Admin (Existe actualmente)
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else if (username.equalsIgnoreCase("alumno")) {
                    // Ir al Dashboard de Alumno
                    Intent intent = new Intent(LoginActivity.this, StudentActivity.class);
                    startActivity(intent);
                    finish();
                } else if (username.equalsIgnoreCase("org")) {
                    // Ir al Dashboard de Organización
                    Intent intent = new Intent(LoginActivity.this, OrganizationActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Usuario no reconocido", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
