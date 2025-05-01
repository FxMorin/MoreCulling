package ca.fxco.moreculling.mixin;

import ca.fxco.moreculling.api.blockstate.StateCullingShapeCache;
import ca.fxco.moreculling.api.model.BakedOpacity;
import ca.fxco.moreculling.mixin.accessors.BlockModelShaperAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.main.GameConfig;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static ca.fxco.moreculling.MoreCulling.blockRenderManager;

@Mixin(Minecraft.class)
public abstract class Minecraft_registerReloadListenersMixin {

    @Shadow @Final private ReloadableResourceManager resourceManager;

    @Shadow public abstract void stop();

    @Inject(
            method = "<init>",
            at = @At(
                    value = "NEW",
                    target = "net/minecraft/client/renderer/LevelRenderer",
                    shift = At.Shift.BEFORE
            )
    )
    private void moreculling$onBlockRenderManagerInitialized(GameConfig args, CallbackInfo ci) {
        this.resourceManager.registerReloadListener((ResourceManagerReloadListener) manager ->
                Block.BLOCK_STATE_REGISTRY.forEach(state -> ((StateCullingShapeCache) state).moreculling$initCustomCullingShape()));

        this.resourceManager.registerReloadListener((ResourceManagerReloadListener) manager ->
                ((BlockModelShaperAccessor) blockRenderManager.getBlockModelShaper()).getModels()
                        .forEach((state, model) ->
                                ((BakedOpacity) model).moreculling$resetTranslucencyCache(state)));
    }
}
