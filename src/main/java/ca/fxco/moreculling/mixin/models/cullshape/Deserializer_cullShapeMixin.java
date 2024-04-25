package ca.fxco.moreculling.mixin.models.cullshape;

import ca.fxco.moreculling.api.model.CullShapeElement;
import ca.fxco.moreculling.api.model.ExtendedUnbakedModel;
import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.util.JsonHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Type;
import java.util.List;

@Mixin(JsonUnbakedModel.Deserializer.class)
public class Deserializer_cullShapeMixin {

    @Inject(
            method = "deserialize(Lcom/google/gson/JsonElement;Ljava/lang/reflect/Type;" +
                    "Lcom/google/gson/JsonDeserializationContext;)" +
                    "Lnet/minecraft/client/render/model/json/JsonUnbakedModel;",
            at = @At("RETURN")
    )
    private void onDeserialize(JsonElement jsonElement, Type type,
                               JsonDeserializationContext jsonContext, CallbackInfoReturnable<JsonUnbakedModel> cir) {
        ExtendedUnbakedModel unbakedModel = (ExtendedUnbakedModel) cir.getReturnValue();
        JsonObject jsonObj = jsonElement.getAsJsonObject();
        List<CullShapeElement> list = this.moreculling$cullshapesFromJson(jsonContext, jsonObj);
        unbakedModel.setCullShapeElements(list);

        if (jsonObj.has("useModelShape")) {
            unbakedModel.setUseModelShape(jsonObj.get("useModelShape").getAsBoolean());
        }
    }

    @Unique
    private List<CullShapeElement> moreculling$cullshapesFromJson(JsonDeserializationContext context,
                                                                  JsonObject jsonObj) {
        if (jsonObj.has("cullshapes")) {
            List<CullShapeElement> list = Lists.newArrayList();
            for (JsonElement shape : JsonHelper.getArray(jsonObj, "cullshapes")) {
                list.add(context.deserialize(shape, CullShapeElement.class));
            }
            return list;
        }
        return null;
    }
}
