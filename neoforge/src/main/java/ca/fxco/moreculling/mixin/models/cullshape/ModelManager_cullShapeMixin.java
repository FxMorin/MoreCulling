package ca.fxco.moreculling.mixin.models.cullshape;

import ca.fxco.moreculling.api.model.ExtendedUnbakedModel;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.io.Reader;
import java.util.Map;

@Mixin(ModelManager.class)
public class ModelManager_cullShapeMixin {

    @WrapOperation(
            method = "lambda$loadBlockModels$10",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/neoforged/neoforge/client/model/UnbakedModelParser;parse(" +
                            "Ljava/io/Reader;)Lnet/minecraft/client/resources/model/UnbakedModel;"
            )
    )
    private static UnbakedModel moreculling$enableUseModelShape(Reader reader, Operation<BlockModel> original,
                                                                @Local(argsOnly = true)
                                                              Map.Entry<ResourceLocation, Resource> entry) {
        BlockModel model = original.call(reader);

        if (entry.getValue().sourcePackId().startsWith("file/")) {
            ((ExtendedUnbakedModel) model).moreculling$setUseModelShape(true);
        }

        return model;
    }
}
