package ca.fxco.moreculling.mixin.models.cullshape;

import ca.fxco.moreculling.api.model.ExtendedUnbakedModel;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.io.Reader;
import java.util.Map;

@Mixin(ModelManager.class)
@Restriction(conflict = @Condition("fabric-model-loading-api-v1"))
public class ModelManager_cullShapeMixin {

    @WrapOperation(
            method = "method_65750",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/block/model/BlockModel;fromStream(" +
                            "Ljava/io/Reader;)Lnet/minecraft/client/renderer/block/model/BlockModel;"
            )
    )
    private static BlockModel moreculling$enableUseModelShape(Reader reader, Operation<BlockModel> original,
                                                              @Local(argsOnly = true)
                                                              Map.Entry<ResourceLocation, Resource> entry) {
        BlockModel model = original.call(reader);

        if (entry.getValue().sourcePackId().startsWith("file/")) {
            ((ExtendedUnbakedModel) model).moreculling$setUseModelShape(true);
        }

        return model;
    }
}
