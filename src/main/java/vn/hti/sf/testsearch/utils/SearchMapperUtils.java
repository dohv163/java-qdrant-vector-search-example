package vn.hti.sf.testsearch.utils;

import com.google.protobuf.Value;
import io.qdrant.client.grpc.JsonWithInt;
import io.qdrant.client.grpc.Points;
import vn.hti.sf.testsearch.dto.SearchResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SearchMapperUtils {

    public static SearchResult convert(Points.ScoredPoint searchPoints) {

        if (searchPoints == null) {
            return null;
        }
        SearchResult searchResult = new SearchResult();
        searchResult.setId(new SearchResult.Id(searchPoints.getId().getNum()));
        searchResult.setScore(searchPoints.getScore());
        searchResult.setPayload(convertMapPayload(searchPoints.getPayloadMap()));

        return searchResult;
    }

    public static List<SearchResult> convert(List<Points.ScoredPoint> searchPoints) {

        if (searchPoints == null) {
            return new ArrayList<>();
        }

        return searchPoints.stream()
                .map(SearchMapperUtils::convert)
                .collect(Collectors.toList());

    }


    private static Map<String, String> convertMapPayload(Map<String, JsonWithInt.Value> map) {
        HashMap<String, String> result = new HashMap<>();
        map.forEach((key, value) -> result.put(key, value.getStringValue()));
        return result;
    }

}
