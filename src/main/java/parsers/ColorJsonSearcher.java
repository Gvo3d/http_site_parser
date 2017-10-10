package parsers;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.Iterator;

public class ColorJsonSearcher implements JsonDeserializer<String> {
    @Override
    public String deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        String result = null;
        JsonObject object = jsonElement.getAsJsonObject().getAsJsonObject("adpPage").getAsJsonObject("product");
        JsonArray array = object.getAsJsonArray("attributeGroups");
        Iterator<JsonElement> iter = array.iterator();
        while (iter.hasNext()) {
            JsonElement element = iter.next();
            if (element.getAsJsonObject().get("name").getAsString().equals("color_detail")) {
                result = element.getAsJsonObject().get("fields").getAsJsonArray().get(0).getAsJsonObject().get("name").getAsString();
            }
        }
        return result;
    }
}
