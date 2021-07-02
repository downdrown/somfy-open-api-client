package at.downdrown.somfy.gson;

import at.downdrown.somfy.domain.Capability;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.Arrays;

public class CapabilityDeserializer implements JsonDeserializer<Capability> {
    @Override
    public Capability deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return Arrays.stream(Capability.values())
            .filter(capability -> json.getAsJsonObject().get("name").getAsString().equals(capability.getIdentification()))
            .findFirst()
            .orElseThrow();
    }
}
