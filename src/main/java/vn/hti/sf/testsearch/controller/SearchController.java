package vn.hti.sf.testsearch.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.hti.sf.testsearch.dto.SearchResult;
import vn.hti.sf.testsearch.dto.res.ResponseDto;
import vn.hti.sf.testsearch.utils.AiClientUtils;
import vn.hti.sf.testsearch.utils.FileUtils;
import vn.hti.sf.testsearch.utils.QdrantClientUtils;

import java.util.List;

import static vn.hti.sf.testsearch.constants.QdrantConstants.STORAGE_PATH;
import static vn.hti.sf.testsearch.constants.QdrantConstants.TEST_IMAGE_SEARCH;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api")
public class SearchController {

    @GetMapping(value = "/search/image")
    public ResponseDto<List<SearchResult>> search(@RequestPart MultipartFile file,
                              @RequestParam Integer limit) {
        return new ResponseDto<>(QdrantClientUtils.searchByVector(TEST_IMAGE_SEARCH, limit, AiClientUtils.getVectorImage(file)));
    }


    @GetMapping("/file")
    public ResponseEntity<Resource> getAudioRecord(@RequestParam String fileName) {

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(FileUtils.getResourceFile(STORAGE_PATH, fileName));

    }


}
