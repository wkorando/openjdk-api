package net.openjdk.api.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.List;


public class JSONUtil {

    protected static final ObjectMapper mapper = new ObjectMapper();

    public static ObjectNode listOfStringsToJSON(String key, List<String> items) {
        var node = mapper.createObjectNode();
        var arr = node.putArray(key);
        items.forEach(arr::add);
        return node;
    }

    public static ObjectNode listOfObjectsToJson(String key, List<?> items) {
        var node = mapper.createObjectNode();
        var arr = node.putArray(key);
        items.forEach(x-> arr.add(
            mapper.convertValue(x, JsonNode.class)
        ));
        return node;
    }

}
