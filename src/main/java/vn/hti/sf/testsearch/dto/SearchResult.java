package vn.hti.sf.testsearch.dto;

import lombok.*;

import java.io.Serializable;
import java.util.Map;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SearchResult implements Serializable {

    private Id id;
    private Map<String, String> payload;
    private Float score;


    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Id {
        Long num;
    }


}
