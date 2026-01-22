package cuatrovientos.voluntariado.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Cliente Singleton para la configuración de Retrofit.
 * Gestiona la conexión con el backend.
 */
public class RetrofitClient {
    private static Retrofit retrofit = null;
    // 10.0.2.2 is the localhost for the Android Emulator
    private static final String BASE_URL = "http://10.0.2.2:8000/api/";

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
