package ca.fxco.moreculling.mixin;

import ca.fxco.moreculling.MoreCulling;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.resource.ReloadableResourceManagerImpl;
import net.minecraft.resource.SynchronousResourceReloader;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClient_managersMixin {

    @Shadow
    @Final
    private BlockRenderManager blockRenderManager;

    @Shadow
    @Final
    private BakedModelManager bakedModelManager;

    @Shadow
    @Final
    private ReloadableResourceManagerImpl resourceManager;

    @Inject(
            method = "<init>",
            at = @At(
                    value = "NEW",
                    target = "net/minecraft/client/render/model/BakedModelManager",
                    shift = At.Shift.AFTER
            )
    )
    private void onBakedModelManagerInitialized(RunArgs args, CallbackInfo ci) {
        MoreCulling.bakedModelManager = this.bakedModelManager;
    }


    @Inject(
            method = "<init>",
            at = @At(
                    value = "NEW",
                    target = "net/minecraft/client/render/WorldRenderer",
                    shift = At.Shift.BEFORE
            )
    )
    private void onBlockRenderManagerInitialized(RunArgs args, CallbackInfo ci) {
        MoreCulling.blockRenderManager = this.blockRenderManager;

        // Make sure to reload block states on resource reload
        this.resourceManager.registerReloader((SynchronousResourceReloader) manager -> Blocks.refreshShapeCache());
    }
}
