package ca.fxco.moreculling.mixin.models.cullshape;

import ca.fxco.moreculling.api.model.ExtendedUnbakedModel;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.Map;

@Mixin(BakedModelManager.class)
public class BakedModelManager_cullShapeMixin {

    @ModifyArg(
            method = {"method_45898"},
            at = @At(
                    value = "INVOKE",
                    target = "com/mojang/datafixers/util/Pair.of(Ljava/lang/Object;" +
                            "Ljava/lang/Object;)Lcom/mojang/datafixers/util/Pair;",
                    remap = false
            ),
            index = 1)
    private static Object moreculling$enableModelShape(Object originalModel,
                                                       @Local(argsOnly = true)
                                                       Map.Entry<Identifier, Resource> entry) {
        JsonUnbakedModel model = (JsonUnbakedModel) originalModel;

        if (entry.getValue().getResourcePackName().startsWith("file/") && entry.getKey().getPath().contains("block/")) {
            ((ExtendedUnbakedModel) model).setUseModelShape(true);
        }

        return model;
    }
}
