package vn.hti.sf.testsearch.utils;

import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Slf4j
public class OkHttpClientUtils {

    private static int READ_TIMEOUT = 100;

    private static int CONNECT_TIMEOUT = 60;

    private static int WRITE_TIMEOUT = 60;

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public static final MediaType FORM_BODY = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");
    private static final byte[] LOCKER = new byte[0];
    private static OkHttpClientUtils mInstance;
    private OkHttpClient okHttpClient;


    private OkHttpClientUtils() {

        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        clientBuilder.readTimeout(READ_TIMEOUT, TimeUnit.SECONDS);
        clientBuilder.connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS);
        clientBuilder.writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS);
        okHttpClient = clientBuilder.build();

    }


    public static OkHttpClientUtils getInstance() {

        if (mInstance == null) {
            synchronized (LOCKER) {
                if (mInstance == null) {
                    mInstance = new OkHttpClientUtils();
                }
            }
        }
        return mInstance;

    }

    public Response newCall(Request request) throws IOException {
        return okHttpClient.newCall(request).execute();
    }

}
