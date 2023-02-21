package com.azunitech.lambda.json;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

public class JsonPlayTest {
    @Test
    public void testJsonMap(){
        Map<String, String> map = new LinkedHashMap<>();
        map.put("a", "v");

        String json = (new Gson()).toJson(map);
    }
}
