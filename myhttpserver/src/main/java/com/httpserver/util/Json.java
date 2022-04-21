package com.httpserver.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;


public class Json {
    private static ObjectMapper ObjectMapper = defaultObjectMapper();

    private static ObjectMapper defaultObjectMapper() {
        ObjectMapper om = new ObjectMapper();
        om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);//<-- prevents crashing
        return om;                                                                 // in-case missing property
    }

    public static JsonNode parse(String jsonSrc) throws JsonProcessingException {//parse a json string in to
        return ObjectMapper.readTree(jsonSrc);                                  //json node
    }

    public static <A> A fromJson(JsonNode node, Class<A> clazz) throws JsonProcessingException {
        return ObjectMapper.treeToValue(node , clazz);//move JsonNode to Configuration(file)
    }

    public static JsonNode toJson(Object obj){//generates a Json node from Configuration(file)
        return ObjectMapper.valueToTree(obj);
    }

    public static String stringify(JsonNode node) throws JsonProcessingException {
        return generateJson(node,false);
    }

    public static String stringifyPretty(JsonNode node) throws JsonProcessingException {
        return generateJson(node,true);
    }

    private static String generateJson(Object o,boolean pretty) throws JsonProcessingException {
        ObjectWriter objectWriter = ObjectMapper.writer();
        if(pretty){
            objectWriter = objectWriter.with(SerializationFeature.INDENT_OUTPUT);//make it look pretty
        }
        return objectWriter.writeValueAsString(o);
    }
}
