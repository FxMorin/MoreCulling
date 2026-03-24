package ca.fxco.moreculling.mixin.blockentity;

import ca.fxco.moreculling.utils.CullingUtils;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.renderer.blockentity.AbstractSignRenderer;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AbstractSignRenderer.class)
public class SignRenderer_textMixin {

    @WrapOperation(
            method = "extractRenderState(Lnet/minecraft/world/level/block/entity/SignBlockEntity;" +
                    "Lnet/minecraft/client/renderer/blockentity/state/SignRenderState;" +
                    "FLnet/minecraft/world/phys/Vec3;" +
                    "Lnet/minecraft/client/renderer/feature/ModelFeatureRenderer$CrumblingOverlay;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/entity/SignBlockEntity;" +
                            "getFrontText()Lnet/minecraft/world/level/block/entity/SignText;"
            )
    )
    private SignText moreculling$cullFrontSignText(SignBlockEntity instance, Operation<SignText> original) {
        return CullingUtils.cullSignText(instance.getBlockPos(), instance.getBlockState(), true) ? original.call(instance) : null;
    }

    @WrapOperation(
            method = "extractRenderState(Lnet/minecraft/world/level/block/entity/SignBlockEntity;" +
                    "Lnet/minecraft/client/renderer/blockentity/state/SignRenderState;" +
                    "FLnet/minecraft/world/phys/Vec3;" +
                    "Lnet/minecraft/client/renderer/feature/ModelFeatureRenderer$CrumblingOverlay;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/entity/SignBlockEntity;" +
                            "getBackText()Lnet/minecraft/world/level/block/entity/SignText;"
            )
    )
    private SignText moreculling$cullBackSignText(SignBlockEntity instance, Operation<SignText> original) {
        return CullingUtils.cullSignText(instance.getBlockPos(), instance.getBlockState(), false) ? original.call(instance) : null;
    }
}
