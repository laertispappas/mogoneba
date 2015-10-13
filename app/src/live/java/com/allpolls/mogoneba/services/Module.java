package com.allpolls.mogoneba.services;

import android.util.Log;

import com.allpolls.mogoneba.infrastructure.Auth;
import com.allpolls.mogoneba.infrastructure.MogonebaApplication;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;

public class Module {

    public static void register(MogonebaApplication application){
        MogonebaWebService api = createWebService(application);

        new LiveAccountService(application, api);
        new LiveContactsService(application, api);
        new LiveMessagesService(application, api);
    }

    private static MogonebaWebService createWebService(MogonebaApplication application) {
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .registerTypeAdapter(Calendar.class, new DateSerializer())
                .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
                .create();

        OkHttpClient client = new OkHttpClient();
        client.interceptors().add(new AuthInterceptor(application.getAuth()));

        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(MogonebaApplication.API_ENDPOINT.toString())
                .setConverter(new GsonConverter(gson))
                .setClient(new OkClient(client))
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestFacade request) {
                        request.addHeader("X-Student", MogonebaApplication.TOKEN);
                    }
                })
                .build();

        return adapter.create(MogonebaWebService.class);
    }

    private static class AuthInterceptor implements Interceptor {
        private final Auth auth;

        private AuthInterceptor(Auth auth) {
            this.auth = auth;
        }

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();

            if (auth.hasAuthToken()) {
                request = request.newBuilder().addHeader("Authorization", "Bearer " + auth.getAuthToken()).build();
            }

            Response response = chain.proceed(request);
            if (response.isSuccessful()) {
                return response;
            }

            if (response.code() == 401 && auth.hasAuthToken()) {
                auth.setAuthToken(null);
            }

            return response;
        }
    }

    private static final String[] DATE_FORMATS = new String[] {
            "yyyy-MM-dd'T'HH:mm:ss.SSSZ",
            "yyyy-MM-dd'T'HH:mm:ss.SSS",
            "yyyy-MM-dd'T'HH:mmZ",
            "yyyy-MM-dd'T'HH:mm",
            "yyyy-MM-dd"
    };

    private static class DateSerializer implements JsonDeserializer<Calendar> {

        @Override
        public Calendar deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            for (String format : DATE_FORMATS) {
                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.US);
                    dateFormat.setTimeZone(TimeZone.getDefault());
                    Date date = dateFormat.parse(json.getAsString());
                    Calendar calendar = new GregorianCalendar();
                    calendar.setTimeInMillis(date.getTime() + TimeZone.getDefault().getOffset(0));

                    return calendar;
                } catch (Exception ignored) {}
            }

            throw new JsonParseException("Can not parse date: " + json.getAsString());
        }
    }
}