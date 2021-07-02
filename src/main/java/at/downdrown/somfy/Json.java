package at.downdrown.somfy;

import at.downdrown.somfy.domain.Capability;
import at.downdrown.somfy.domain.Category;
import at.downdrown.somfy.domain.DeviceType;
import at.downdrown.somfy.gson.CapabilityDeserializer;
import at.downdrown.somfy.gson.CategoryDeserializer;
import at.downdrown.somfy.gson.DeviceTypeDeserializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class Json {

    private static final Gson gson = newGson();

    public static String toJson(Object object) {
        return gson.toJson(object);
    }

    public static <T> T fromJson(String json, Class<T> type) {
        return gson.fromJson(json, type);
    }

    private static Gson newGson() {

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Capability.class, new CapabilityDeserializer());
        gsonBuilder.registerTypeAdapter(Category.class, new CategoryDeserializer());
        gsonBuilder.registerTypeAdapter(DeviceType.class, new DeviceTypeDeserializer());

        return gsonBuilder.create();
    }
}
