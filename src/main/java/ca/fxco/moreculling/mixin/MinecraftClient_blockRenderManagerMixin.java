package ca.fxco.moreculling.mixin;

import ca.fxco.moreculling.MoreCulling;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import net.minecraft.client.render.block.BlockRenderManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClient_blockRenderManagerMixin {

    @Shadow
    @Final
    private BlockRenderManager blockRenderManager;


    @Inject(
            method = "<init>",
            at = @At(
                    value = "NEW",
                    target = "net/minecraft/client/render/WorldRenderer",
                    shift = At.Shift.BEFORE
            )
    )
    private void onBlockRenderManageInitialized(RunArgs args, CallbackInfo ci) {
        MoreCulling.blockRenderManager = this.blockRenderManager;
    }
}
