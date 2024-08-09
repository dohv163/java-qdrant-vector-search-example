package vn.hti.sf.testsearch.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import java.nio.file.Path;
import java.nio.file.Paths;


@Slf4j
public class FileUtils {

    public static Resource getResourceFile(String storagePath, String fileName) {
        try {
            Path path = Paths.get(storagePath + "/" + fileName);
            return new UrlResource(path.toUri());

        } catch (Exception e) {
            log.error("Get file {} error {}", fileName, e.getMessage());
        }
        return null;
    }

}
