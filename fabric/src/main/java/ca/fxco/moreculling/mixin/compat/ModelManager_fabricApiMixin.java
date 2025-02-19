package ca.fxco.moreculling.mixin.compat;

import ca.fxco.moreculling.api.model.ExtendedUnbakedModel;
import com.llamalad7.mixinextras.sugar.Local;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
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
@Restriction(require = @Condition("fabric-model-loading-api-v1"))
public class ModelManager_fabricApiMixin {

    @ModifyArg(method = "method_65750(Ljava/util/Map$Entry;)Lcom/mojang/datafixers/util/Pair;", at = @At(value = "INVOKE", target = "com/mojang/datafixers/util/Pair.of(Ljava/lang/Object;Ljava/lang/Object;)Lcom/mojang/datafixers/util/Pair;", remap = false), index = 1)
    private static Object actuallyDeserializeModel(Object originalModel, @Local Reader reader,
                                                   @Local(argsOnly = true)
                                                   Map.Entry<ResourceLocation, Resource> entry) {
        UnbakedModel model = (UnbakedModel) originalModel;

        if (entry.getValue().sourcePackId().startsWith("file/")) {
            ((ExtendedUnbakedModel) model).moreculling$setUseModelShape(true);
        }

        return model;
    }
}
