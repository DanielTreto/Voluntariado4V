package cuatrovientos.voluntariado.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;

public class RetrofitClient {
    private static Retrofit retrofit = null;
    private static final String BASE_URL = "http://10.0.2.2:8000/api/";
    private static String authToken = null;

    public static void setAuthToken(String token) {
        authToken = token;
    }

    public static Retrofit getClient() {
        if (retrofit == null) {
            okhttp3.OkHttpClient client = new okhttp3.OkHttpClient.Builder()
                .addInterceptor(new okhttp3.Interceptor() {
                    @Override
                    public okhttp3.Response intercept(Chain chain) throws java.io.IOException {
                        okhttp3.Request original = chain.request();
                        okhttp3.Request.Builder builder = original.newBuilder();
                        if (authToken != null && !authToken.isEmpty()) {
                            builder.header("Authorization", "Bearer " + authToken);
                        }
                        okhttp3.Request request = builder.build();
                        return chain.proceed(request);
                    }
                })
                .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client) 
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
