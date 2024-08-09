package vn.hti.sf.testsearch.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import io.qdrant.client.QdrantClient;
import io.qdrant.client.QdrantGrpcClient;
import io.qdrant.client.grpc.Collections;
import io.qdrant.client.grpc.Points;
import lombok.extern.slf4j.Slf4j;
import vn.hti.sf.testsearch.config.QdrantClientConfiguration;
import vn.hti.sf.testsearch.dto.SearchResult;
import vn.hti.sf.testsearch.utils.json.MapperUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static io.qdrant.client.QueryFactory.fusion;
import static io.qdrant.client.QueryFactory.nearest;
import static io.qdrant.client.WithPayloadSelectorFactory.enable;


@Slf4j
public class QdrantClientUtils {

    private static volatile QdrantClient qdrantClient = null;

    public static QdrantClient getClient() {

        if (qdrantClient == null) {
            synchronized (QdrantClientUtils.class) {
                if (qdrantClient == null) {
                    initClient();
                }
            }
        }
        return qdrantClient;
    }

    private static void initClient() {

        qdrantClient = new QdrantClient(
                QdrantGrpcClient.newBuilder(QdrantClientConfiguration.qdrantHost, QdrantClientConfiguration.qdrantPort,
                                false)
                        .build());

    }

    public static void createEuclidCollection(String collectionName, int vectorSize) throws ExecutionException, InterruptedException {
        Collections.CollectionOperationResponse operationResponse = getClient().createCollectionAsync(collectionName, Collections.VectorParams.newBuilder()
                        .setSize(vectorSize)
                .setDistance(Collections.Distance.Euclid)
                .build()).get();
        log.info("Created collection result {}", operationResponse.getResult());
    }

    public static void addVectorAsync(String collectionName, List<Points.PointStruct> pointStructs) {

        try {
            Points.UpdateResult updateResult = getClient().upsertAsync(collectionName, pointStructs).get();
            log.info("Upsert point to collection {} result {} ", collectionName,  updateResult.toString());
        } catch (Exception e) {
            log.error("Add vector async to collection {} error {}", collectionName, e.getMessage());
        }
    }

    public static List<SearchResult> searchByVector(String collectionName, int limit, List<Float> vector) {

        List<SearchResult> results = new ArrayList<>();
        try {
//            List<Points.ScoredPoint> scoredPoints = getClient().searchAsync(Points.SearchPoints.newBuilder()
//                    .setCollectionName(collectionName)
//                    .setLimit(limit)
//                    .addAllVector(vector)
//                    .setWithPayload(enable(true))
//                    .build()).get();


//            for (Points.ScoredPoint scoredPoint : scoredPoints) {
//                SearchResult searchResult = SearchResult.builder()
//                        .payload(MapperUtils.convert(scoredPoint.getPayloadMap(), Map.class))
//                        .score(scoredPoint.getScore())
//                        .id(MapperUtils.convert(scoredPoint.getId(), SearchResult.Id.class))
//                        .build();
//                results.add(searchResult);
//            }

//            List<Points.ScoredPoint> scoredPoints = getClient().queryAsync(Points.QueryPoints.newBuilder()
//                    .setCollectionName(collectionName)
//                    .setQuery(nearest(vector))
//                    .setLimit(limit)
//                    .setWithPayload(enable(true))
//                    .build()).get();

//            List<Points.ScoredPoint> scoredPoints = getClient().queryAsync(
//                    Points.QueryPoints.newBuilder()
//                            .setCollectionName("{collection_name}")
//                            .addPrefetch(Points.PrefetchQuery.newBuilder()
//                                    .setQuery(nearest(List.of(0.22f, 0.8f), List.of(1, 42)))
//                                    .setUsing("sparse")
//                                    .setLimit(20)
//                                    .build())
//                                    ).get();

            Points.QueryPoints queryPoints = Points.QueryPoints.newBuilder()
                            .setCollectionName(collectionName)
                            .addPrefetch(Points.PrefetchQuery.newBuilder()
                                    .setQuery(nearest(vector))
                                    //.setUsing("sparse")
                                    .setLimit(limit))
                    .setQuery(fusion(Points.Fusion.RRF))
                    .setWithPayload(enable(true))
                    .build();
           List<Points.ScoredPoint> scoredPoints = getClient().queryAsync(queryPoints).get();
          return SearchMapperUtils.convert(scoredPoints);

        } catch (Exception e) {
            e.printStackTrace();
           log.error("Search vector by collection {} error {}", collectionName, e.getMessage());
        }
        return results;
    }


}
