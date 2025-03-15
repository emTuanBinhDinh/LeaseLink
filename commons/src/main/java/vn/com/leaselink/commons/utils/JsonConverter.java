package vn.com.leaselink.commons.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Base64;
import java.util.Map;

public class JsonConverter {

    private static Gson gson = new GsonBuilder()
            .disableHtmlEscaping()
            .setDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
            .registerTypeHierarchyAdapter(byte[].class, new ByteArrayToBase64TypeAdapter())
            .create();

    private static JsonParser parser = new JsonParser();

    public static Gson getGson() {

        if (gson == null) {
            gson = new GsonBuilder().disableHtmlEscaping().create();
        }
        return gson;
    }

    public static JsonParser getParser() {
        if (parser == null) {
            parser = new JsonParser();
        }
        return parser;
    }

    public static JsonElement toJsonElement(Object object) {
        return getGson().toJsonTree(object);
    }

    public static String toJson(Object object) {
        return getGson().toJson(object);
    }

    public static JsonElement toJsonElement(String json) {
        return getParser().parse(json);
    }

    public static com.google.gson.JsonObject toJson(String json) {
//        return gson.fromJson(json, com.google.gson.JsonObject.class);
        return getParser().parse(json).getAsJsonObject();
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        return getGson().fromJson(json, clazz);
    }

    public static <T> T fromJson(String json, Type type) {
        return getGson().fromJson(json, type);
    }

    public static <T> T fromJson(com.google.gson.JsonObject json, Type type) {
        return getGson().fromJson(json, type);
    }

    public static <T> T fromJson(com.google.gson.JsonObject jsonObject, Class<T> clazz) {
        return getGson().fromJson(jsonObject, clazz);
    }

    public static <T> T fromJson(JsonElement jsonObject, Class<T> clazz) {
        return getGson().fromJson(jsonObject, clazz);
    }

    public static <T> T fromJsonAcceptSingleValue(String json, Class<T> clazz) throws JsonProcessingException {
        return new ObjectMapper()
                .reader(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
                .forType(clazz)
                .readValue(json);
    }


    public static <OUT,IN> OUT fromJson(String json,Class<OUT> out,Class<IN> in){
        //Example: ABC<DEF> -> OUT = ABC.class and IN = DEF.class
        Type type = TypeToken.getParameterized(out,in).getType();
        return (OUT)(getGson().fromJson(json,type));
    }

    public static <OUT,IN> OUT fromJson(JsonElement json, Class<OUT> out, Class<IN> in){
        Type type = TypeToken.getParameterized(out,in).getType();
        return (OUT)(new Gson().fromJson(json,type));
    }

    public static String fromMap(Map<String, Object> map) {
        return new Gson().toJson(map);
    }

    public static JsonElement fromMapToJson(Map<String, Object> map) {
        return new Gson().toJsonTree(map);
    }

    private static class ByteArrayToBase64TypeAdapter implements JsonSerializer<byte[]>, JsonDeserializer<byte[]> {
        public byte[] deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return Base64.getDecoder().decode(json.getAsString());
        }

        public JsonElement serialize(byte[] src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(Base64.getEncoder().encodeToString(src));
        }
    }

    public static <OUT> OUT mapToObject(Object object, Class<OUT> out) {
        return gson.fromJson(getGson().toJson(object), out);
    }


}
