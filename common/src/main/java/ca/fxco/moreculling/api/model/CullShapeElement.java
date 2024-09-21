package ca.fxco.moreculling.api.model;

import com.google.gson.*;
import net.minecraft.util.GsonHelper;
import org.joml.Vector3f;

import java.lang.reflect.Type;

public class CullShapeElement {

    public final Vector3f from;
    public final Vector3f to;

    public CullShapeElement(Vector3f from, Vector3f to) {
        this.from = from;
        this.to = to;
    }

    public static class Deserializer implements JsonDeserializer<CullShapeElement> {

        public Deserializer() {}

        public CullShapeElement deserialize(JsonElement jsonElement, Type type,
                                            JsonDeserializationContext jsonContext) throws JsonParseException {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            Vector3f vector3f = this.deserializeFrom(jsonObject);
            Vector3f vector3f2 = this.deserializeTo(jsonObject);
            return new CullShapeElement(vector3f, vector3f2);
        }

        private Vector3f deserializeTo(JsonObject object) {
            Vector3f vec3f = this.deserializeVec3f(object, "to");
            if (!(vec3f.x() < -16.0F) && !(vec3f.y() < -16.0F) && !(vec3f.z() < -16.0F) &&
                    !(vec3f.x() > 32.0F) && !(vec3f.y() > 32.0F) && !(vec3f.z() > 32.0F)) {
                return vec3f;
            }
            throw new JsonParseException("'to' specifier exceeds the allowed boundaries: " + vec3f);
        }

        private Vector3f deserializeFrom(JsonObject object) {
            Vector3f vec3f = this.deserializeVec3f(object, "from");
            if (!(vec3f.x() < -16.0F) && !(vec3f.y() < -16.0F) && !(vec3f.z() < -16.0F) &&
                    !(vec3f.x() > 32.0F) && !(vec3f.y() > 32.0F) && !(vec3f.z() > 32.0F)) {
                return vec3f;
            }
            throw new JsonParseException("'from' specifier exceeds the allowed boundaries: " + vec3f);
        }

        private Vector3f deserializeVec3f(JsonObject object, String name) {
            JsonArray jsonArray = GsonHelper.getAsJsonArray(object, name);
            if (jsonArray.size() != 3) {
                throw new JsonParseException("Expected 3 " + name + " values, found: " + jsonArray.size());
            }
            float[] fs = new float[3];

            for (int i = 0; i < fs.length; ++i) {
                fs[i] = GsonHelper.convertToFloat(jsonArray.get(i), name + "[" + i + "]");
            }

            return new Vector3f(fs[0], fs[1], fs[2]);
        }
    }
}
