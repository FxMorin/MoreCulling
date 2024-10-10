package ca.fxco.moreculling.mixin;

import ca.fxco.moreculling.MoreCulling;
import net.minecraft.client.Minecraft;
import net.minecraft.client.main.GameConfig;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class Minecraft_managersMixin {

    @Shadow
    @Final
    private BlockRenderDispatcher blockRenderer;

    @Shadow
    @Final
    private ModelManager modelManager;

    @Shadow
    @Final
    private ReloadableResourceManager resourceManager;

    @Inject(
            method = "<init>",
            at = @At(
                    value = "NEW",
                    target = "net/minecraft/client/resources/model/ModelManager",
                    shift = At.Shift.AFTER
            )
    )
    private void moreculling$onBakedModelManagerInitialized(GameConfig args, CallbackInfo ci) {
        MoreCulling.bakedModelManager = this.modelManager;
    }


    @Inject(
            method = "<init>",
            at = @At(
                    value = "NEW",
                    target = "net/minecraft/client/renderer/LevelRenderer",
                    shift = At.Shift.BEFORE
            )
    )
    private void moreculling$onBlockRenderManagerInitialized(GameConfig args, CallbackInfo ci) {
        MoreCulling.blockRenderManager = this.blockRenderer;

        // Make sure to reload block states on resource reload
        this.resourceManager.registerReloadListener((ResourceManagerReloadListener) manager ->
                Block.BLOCK_STATE_REGISTRY.forEach(BlockBehaviour.BlockStateBase::initCache));
    }
}
