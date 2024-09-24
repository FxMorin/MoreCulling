package ca.fxco.moreculling.mixin.sorting;

import ca.fxco.moreculling.states.SortingStates;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RenderType.class)
public class RenderTypeMixin {

    @Inject(
            method = "create(Ljava/lang/String;Lcom/mojang/blaze3d/vertex/VertexFormat;" +
                    "Lcom/mojang/blaze3d/vertex/VertexFormat$Mode;" +
                    "IZZLnet/minecraft/client/renderer/RenderType$CompositeState;)" +
                    "Lnet/minecraft/client/renderer/RenderType$CompositeRenderType;",
            at = @At("HEAD")
    )
    private static void moreculling$dynamicSorting(
            String name, VertexFormat format, VertexFormat.Mode mode, int bufferSize,
            boolean affectsCrumbling, boolean sortOnUpload, RenderType.CompositeState state,
            CallbackInfoReturnable<RenderType.CompositeRenderType> cir,
            @Local(ordinal = 1, argsOnly = true) LocalBooleanRef shouldSortRef
    ) {
        if (SortingStates.CAN_SKIP_SORTING) {
            SortingStates.CAN_SKIP_SORTING = false;
            shouldSortRef.set(false);
        }
    }
}
