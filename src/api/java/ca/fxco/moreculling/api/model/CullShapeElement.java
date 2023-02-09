package ca.fxco.moreculling.api.model;

import com.google.gson.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.Vec3f;

import java.lang.reflect.Type;

@Environment(EnvType.CLIENT)
public class CullShapeElement {

    public final Vec3f from;
    public final Vec3f to;

    public CullShapeElement(Vec3f from, Vec3f to) {
        this.from = from;
        this.to = to;
    }

    @Environment(EnvType.CLIENT)
    public static class Deserializer implements JsonDeserializer<CullShapeElement> {

        public Deserializer() {}

        public CullShapeElement deserialize(JsonElement jsonElement, Type type,
                                            JsonDeserializationContext jsonContext) throws JsonParseException {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            Vec3f vector3f = this.deserializeFrom(jsonObject);
            Vec3f vector3f2 = this.deserializeTo(jsonObject);
            return new CullShapeElement(vector3f, vector3f2);
        }

        private Vec3f deserializeTo(JsonObject object) {
            Vec3f vec3f = this.deserializeVec3f(object, "to");
            if (!(vec3f.getX() < -16.0F) && !(vec3f.getY() < -16.0F) && !(vec3f.getZ() < -16.0F) &&
                    !(vec3f.getX() > 32.0F) && !(vec3f.getY() > 32.0F) && !(vec3f.getZ() > 32.0F)) {
                return vec3f;
            } else {
                throw new JsonParseException("'to' specifier exceeds the allowed boundaries: " + vec3f);
            }
        }

        private Vec3f deserializeFrom(JsonObject object) {
            Vec3f vec3f = this.deserializeVec3f(object, "from");
            if (!(vec3f.getX() < -16.0F) && !(vec3f.getY() < -16.0F) && !(vec3f.getZ() < -16.0F) &&
                    !(vec3f.getX() > 32.0F) && !(vec3f.getY() > 32.0F) && !(vec3f.getZ() > 32.0F)) {
                return vec3f;
            } else {
                throw new JsonParseException("'from' specifier exceeds the allowed boundaries: " + vec3f);
            }
        }

        private Vec3f deserializeVec3f(JsonObject object, String name) {
            JsonArray jsonArray = JsonHelper.getArray(object, name);
            if (jsonArray.size() != 3) {
                throw new JsonParseException("Expected 3 " + name + " values, found: " + jsonArray.size());
            } else {
                float[] fs = new float[3];

                for(int i = 0; i < fs.length; ++i) {
                    fs[i] = JsonHelper.asFloat(jsonArray.get(i), name + "[" + i + "]");
                }

                return new Vec3f(fs[0], fs[1], fs[2]);
            }
        }
    }
}
