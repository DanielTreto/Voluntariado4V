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
                String password = etPassword.getText().toString().trim();

                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Por favor ingrese usuario y contraseña", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Temporary Admin backdoor until backend supports it
                if (username.equalsIgnoreCase("admin") && password.equalsIgnoreCase("admin")) {
                     Intent intent = new Intent(LoginActivity.this, MainActivity.class);
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
                        String role = loginResponse.getRole();
                        
                        // Save Session
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
                            // Intent extras kept for backward compatibility if needed, but Prefs is primary now
                            intent.putExtra("USER_ID", loginResponse.getId());
                            intent.putExtra("USER_NAME", loginResponse.getName());
                            intent.putExtra("USER_EMAIL", loginResponse.getEmail());
                            startActivity(intent);
                            finish();
                        } else if ("organization".equalsIgnoreCase(role)) {
                            Intent intent = new Intent(LoginActivity.this, OrganizationActivity.class);
                            intent.putExtra("USER_ID", loginResponse.getId());
                            intent.putExtra("USER_NAME", loginResponse.getName());
                            intent.putExtra("USER_EMAIL", loginResponse.getEmail());
                            startActivity(intent);
                            finish();
                        } else if ("admin".equalsIgnoreCase(role) || "administrator".equalsIgnoreCase(role)) {
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            // Save session extras if needed, though MainActivity loads from Prefs now
                            intent.putExtra("USER_ID", loginResponse.getId());
                            intent.putExtra("USER_NAME", loginResponse.getName());
                            intent.putExtra("USER_EMAIL", loginResponse.getEmail());
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, "Rol desconocido: " + role, Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "Credenciales incorrectas: " + loginResponse.getError(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Error en el servidor: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<cuatrovientos.voluntariado.network.LoginResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Fallo de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
