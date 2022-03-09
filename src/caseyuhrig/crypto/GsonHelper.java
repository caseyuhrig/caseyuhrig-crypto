package caseyuhrig.crypto;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;

import java.lang.reflect.Type;
import java.util.Base64;


public class GsonHelper
{
    public static final Gson customGson = new GsonBuilder().setPrettyPrinting()
            .registerTypeHierarchyAdapter(byte[].class, new ByteArrayToBase64TypeAdapter()).create();


    private static class ByteArrayToBase64TypeAdapter implements JsonSerializer<byte[]>, JsonDeserializer<byte[]>
    {
        @Override
        public byte[] deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context)
                throws JsonParseException
        {
            return Base64.getDecoder().decode(json.getAsString());
        }


        @Override
        public JsonElement serialize(final byte[] src, final Type typeOfSrc, final JsonSerializationContext context)
        {
            return new JsonPrimitive(Base64.getEncoder().encodeToString(src));
        }
    }


    public static boolean isJSON(final String json)
    {
        try
        {
            customGson.fromJson(json, Object.class);
            return true;
        }
        catch (final JsonSyntaxException ex)
        {
            return false;
        }
    }


    public static Gson createBuilder()
    {
        return customGson;
    }
    // public static Gson createBuilder()
    // {
    // final GsonBuilder builder = new GsonBuilder();
    // // Adapter to convert byte[] in a Java Object to a String in Json for UI to display image
    // // properly
    // builder.registerTypeAdapter(byte[].class,
    // (JsonSerializer<byte[]>) (src, typeOfSrc, context) -> new JsonPrimitive(
    // Base64.getEncoder().encodeToString(src)));
    // builder.setPrettyPrinting();
    // return builder.create();
    // }
}
