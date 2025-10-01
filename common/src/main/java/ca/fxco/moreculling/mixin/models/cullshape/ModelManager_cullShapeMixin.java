package ca.fxco.moreculling.mixin.models.cullshape;

import ca.fxco.moreculling.api.model.ExtendedUnbakedModel;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.io.Reader;
import java.util.Map;

@Mixin(value = ModelManager.class, priority = 1050)
public class ModelManager_cullShapeMixin {

    @ModifyArg(
            method = {"method_65750", "lambda$loadBlockModels$6"},
            at = @At(
                    value = "INVOKE",
                    target = "com/mojang/datafixers/util/Pair.of(Ljava/lang/Object;" +
                            "Ljava/lang/Object;)Lcom/mojang/datafixers/util/Pair;",
                    remap = false
            ),
            index = 1)
    private static Object moreculling$enableModelShape(Object originalModel, @Local Reader reader,
                                                   @Local(argsOnly = true)
                                                   Map.Entry<ResourceLocation, Resource> entry) {
        UnbakedModel model = (UnbakedModel) originalModel;

        if (entry.getValue().sourcePackId().startsWith("file/") && entry.getKey().getPath().contains("block/")) {
            ((ExtendedUnbakedModel) model).moreculling$setUseModelShape(true);
        }

        return model;
    }
}
