package vn.hti.sf.testsearch.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.web.multipart.MultipartFile;
import vn.hti.sf.testsearch.utils.json.MapperUtils;

import java.util.ArrayList;
import java.util.List;


@Slf4j
public class AiClientUtils {

    public static List<Float> getVectorImage(MultipartFile file) {
        try {
            MultipartBody.Builder multiparBuilder = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM);
            RequestBody requestBody = RequestBody.create(file.getBytes(), MediaType.parse(file.getContentType()));
            multiparBuilder.addFormDataPart("file", file.getName() ,requestBody);

            RequestBody multipartBody = multiparBuilder.build();
            Request request = new Request.Builder()
                    .headers(Headers.of("Content-Type", "multipart/form-data"))
                    .url("http://192.168.133.120:6789/extract_feature_file")
                    .post(multipartBody)
                    .build();
            Response response = OkHttpClientUtils.getInstance().newCall(request);
            String jsonResponse =  response.body().string();
            if (response.isSuccessful()) {
                return MapperUtils.parse(jsonResponse, "feature", new TypeReference<>() {});
            }
        } catch (Exception e) {
            log.error("Get vector image error from AI error {}", e.getMessage());
        }
        return new ArrayList<>();

    }

}
