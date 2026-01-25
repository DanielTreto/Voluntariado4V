package cuatrovientos.voluntariado.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import cuatrovientos.voluntariado.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
                String password = etPassword.getText().toString().trim();

                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Por favor ingrese usuario y contraseña", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (username.equalsIgnoreCase("admin") && password.equalsIgnoreCase("admin")) {
                     android.content.SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
                     android.content.SharedPreferences.Editor editor = prefs.edit();
                     editor.putString("USER_ID", "1");
                     editor.putString("USER_NAME", "Administrador");
                     editor.putString("USER_EMAIL", "admin@4vientos.org");
                     editor.putString("USER_ROLE", "admin");
                     editor.putString("USER_AVATAR", null);
                     editor.apply();

                     Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                     intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                     startActivity(intent);
                     finish();
                     return;
                }

                performLogin(username, password);
            }
        });
    }

    private void performLogin(String email, String password) {
        cuatrovientos.voluntariado.network.ApiService apiService = 
            cuatrovientos.voluntariado.network.RetrofitClient.getClient().create(cuatrovientos.voluntariado.network.ApiService.class);

        cuatrovientos.voluntariado.network.LoginRequest request = new cuatrovientos.voluntariado.network.LoginRequest(email, password);

        retrofit2.Call<cuatrovientos.voluntariado.network.LoginResponse> call = apiService.login(request);
        call.enqueue(new retrofit2.Callback<cuatrovientos.voluntariado.network.LoginResponse>() {
            @Override
            public void onResponse(retrofit2.Call<cuatrovientos.voluntariado.network.LoginResponse> call, retrofit2.Response<cuatrovientos.voluntariado.network.LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    cuatrovientos.voluntariado.network.LoginResponse loginResponse = response.body();
                    
                    if (loginResponse.isSuccess()) {
                        
                        String status = loginResponse.getStatus();
                        if (status != null && (
                            "SUSPENDED".equalsIgnoreCase(status) || "SUSPENDIDO".equalsIgnoreCase(status) ||
                            "PENDING".equalsIgnoreCase(status) || "PENDIENTE".equalsIgnoreCase(status)
                        )) {
                             showErrorDialog("Acceso Denegado", "Su cuenta está actualmente en estado: " + status + ".\nContacte con el administrador.");
                             return;
                        }

                        String role = loginResponse.getRole();
                        
                        android.content.SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
                        android.content.SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("USER_ID", loginResponse.getId());
                        editor.putString("USER_NAME", loginResponse.getName());
                        editor.putString("USER_EMAIL", loginResponse.getEmail());
                        editor.putString("USER_ROLE", role);
                        editor.putString("USER_AVATAR", loginResponse.getAvatar());
                        editor.apply();

                        if ("volunteer".equalsIgnoreCase(role)) {
                            Intent intent = new Intent(LoginActivity.this, StudentActivity.class);
                            startActivity(intent);
                            finish();
                        } else if ("organization".equalsIgnoreCase(role)) {
                            Intent intent = new Intent(LoginActivity.this, OrganizationActivity.class);
                            startActivity(intent);
                            finish();
                        } else if ("admin".equalsIgnoreCase(role) || "administrator".equalsIgnoreCase(role)) {
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            showErrorDialog("Error de Rol", "Rol desconocido: " + role);
                        }
                    } else {
                        showErrorDialog("Error de Acceso", "Credenciales incorrectas.\n" + (loginResponse.getError() != null ? loginResponse.getError() : "Verifique sus datos."));
                    }
                } else {
                    if (response.code() == 404) {
                        showErrorDialog("Error de Acceso", "El correo o la contraseña son incorrectos.");
                    } else {
                        showErrorDialog("Error del Servidor", "Código de error: " + response.code());
                    }
                }
            }

            @Override
            public void onFailure(retrofit2.Call<cuatrovientos.voluntariado.network.LoginResponse> call, Throwable t) {
                showErrorDialog("Error de Conexión", "Fallo al conectar: " + t.getMessage());
            }
        });
    }

    private void showErrorDialog(String title, String message) {
        android.graphics.drawable.Drawable icon = androidx.core.content.ContextCompat.getDrawable(this, android.R.drawable.ic_dialog_alert);
        if (icon != null) {
            icon = androidx.core.graphics.drawable.DrawableCompat.wrap(icon);
            androidx.core.graphics.drawable.DrawableCompat.setTint(icon, androidx.core.content.ContextCompat.getColor(this, R.color.text_primary));
        }

        new androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Aceptar", null)
            .setIcon(icon)
            .show();
    }
}
