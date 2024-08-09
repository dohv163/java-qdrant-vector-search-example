package vn.hti.sf.testsearch.repository;


import com.fasterxml.jackson.core.type.TypeReference;
import io.qdrant.client.grpc.Points;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.stereotype.Repository;

import okhttp3.*;
import vn.hti.sf.testsearch.utils.OkHttpClientUtils;
import vn.hti.sf.testsearch.utils.QdrantClientUtils;
import vn.hti.sf.testsearch.utils.json.MapperUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import static io.qdrant.client.PointIdFactory.id;
import static io.qdrant.client.ValueFactory.value;
import static io.qdrant.client.VectorsFactory.vectors;
import static vn.hti.sf.testsearch.constants.QdrantConstants.TEST_IMAGE_SEARCH;

@Slf4j
@Repository
public class QdrantRepository {


    public void initDataImageVector() throws IOException, ExecutionException, InterruptedException {

        System.out.println("Start init ");
        String folder = "C:\\TEST\\face-test\\";
        String imageName = "image_";
        File directoryPath = new File(folder);
        File[] filesList = directoryPath.listFiles();

        List<Points.PointStruct> pointStructs = new ArrayList<>();
        if (filesList != null) {
            for (int i = 0; i < filesList.length; i++) {

//                File file = new File(folder + "\\" + filesList[i].getName());
//                file.renameTo(new File(folder + "\\" + imageName + i + ".jpg"));

                String fileName = filesList[i].getName();
                MultipartBody.Builder multiparBuilder = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM);
                RequestBody requestBody = RequestBody.create(filesList[i], MediaType.parse(new Tika().detect(filesList[i])));
                multiparBuilder.addFormDataPart("file", fileName, requestBody);

                RequestBody multipartBody = multiparBuilder.build();
                Request request = new Request.Builder()
                        .headers(Headers.of("Content-Type", "multipart/form-data"))
                        .url("http://192.168.133.120:6789/extract_feature_file")
                        .post(multipartBody)
                        .build();
                Response response = OkHttpClientUtils.getInstance().newCall(request);
                String jsonResponse = response.body().string();

                List<Float> vector = new ArrayList<>();
                try {
                    vector = MapperUtils.parse(jsonResponse, "feature", new TypeReference<>() {
                    });
                    Points.PointStruct pointStruct = Points.PointStruct.newBuilder()
                            .setId(id(new Random().nextInt()))
                            .putPayload("image_name", value(fileName))
                            .setVectors(vectors(vector))
                            .build();
                    pointStructs.add(pointStruct);
                } catch (Exception e) {
                    log.error("Get vector image {} errors {}", imageName, e.getMessage());
                }
            }
            QdrantClientUtils.createEuclidCollection(TEST_IMAGE_SEARCH, 512);
            QdrantClientUtils.addVectorAsync(TEST_IMAGE_SEARCH, pointStructs);
        }
        System.out.println("Finish init ");
    }
}
