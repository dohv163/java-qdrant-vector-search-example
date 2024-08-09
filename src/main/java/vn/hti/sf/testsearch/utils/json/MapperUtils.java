package vn.hti.sf.testsearch.utils.json;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
public class MapperUtils {

    private static ObjectMapper objectMapper = null;

    private MapperUtils() {

    }

    static {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.enable(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS);
        objectMapper.enable(SerializationFeature.FAIL_ON_SELF_REFERENCES);
    }

    public static String objToJson(Object object)  {

        String tmp = "";
        try {
            if (object != null) {
                tmp = objectMapper.writeValueAsString(object);
            }
        } catch (JsonProcessingException e) {
            log.error("Mapping Object to Json was failed {}", e.getMessage());
        }
        return tmp;

    }

    public static <T> T jsonToObject(final String json, TypeReference<T> type) {
        try {
            JsonNode jsonNode = objectMapper.readTree(json);
            return objectMapper.readValue(jsonNode.toString(), type);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object jsonToObject(String json, Class<?> clazz) throws Exception {
        Object object = null;
        try {
            object = objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            log.error("Mapping Json to Object was failed {}", e.getMessage());
            throw new Exception("Mapping Json to Object was failed {}" + e.getMessage());
        }
        return object;
    }

    public static List<Map<String, Object>> jsonToMap(String json) throws Exception {
        List<Map<String, Object>> map = null;
        try {
            map = Arrays.asList(objectMapper.readValue(json, new TypeReference<Map<String, Object>[]>() {
            }));
        } catch (JsonProcessingException e) {
            log.error("Mapping Json to Map was failed {}", e.getMessage());
            throw new Exception("Mapping Json to Map was failed {}" + e.getMessage());
        }
        return map;
    }

    public static <T> T parse(final String json, String key, TypeReference<T> type) {
        try {
            JsonNode jsonNode = objectMapper.readTree(json);
            JsonNode jsonArray = jsonNode.get(key);
            if (jsonArray != null) {
                return objectMapper.readValue(jsonArray.toString(), type);
            } else {
                return objectMapper.readValue(jsonNode.toString(), type);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    public static <T> T convert(Object source, Class<T> target) {
        return objectMapper.convertValue(source, target);
    }

    public static <T> T convert(Object source, TypeReference<T> typeReference) {
        return objectMapper.convertValue(source, typeReference);
    }

    public static synchronized <T> void writeJsonToFile(List<T> dataObjects, String pathFile)
            throws JsonGenerationException, JsonMappingException, IOException {

        File file = new File(pathFile);
        if (Files.notExists(Paths.get(pathFile), new LinkOption[]{LinkOption.NOFOLLOW_LINKS})) {
            file.createNewFile();
        }
        FileWriter writer = (new FileWriter(file, true));
        objectMapper.writeValue(writer, dataObjects);
        writer.close();
    }


    public static class LocalDateTimeSerializer extends JsonSerializer<LocalDateTime> {
        public LocalDateTimeSerializer() {
        }

        public void serialize(LocalDateTime localDateTime, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeNumber(localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        }
    }

    public static class LocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {
        public LocalDateTimeDeserializer() {
        }

        public LocalDateTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
            return LocalDateTime.ofInstant(Instant.ofEpochMilli(jsonParser.getLongValue()), ZoneId.systemDefault());
        }
    }

}
