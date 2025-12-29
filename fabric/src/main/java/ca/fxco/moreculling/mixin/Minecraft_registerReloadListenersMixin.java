package ca.fxco.moreculling.mixin;

import ca.fxco.moreculling.utils.CacheUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.main.GameConfig;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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
        this.resourceManager.registerReloadListener((ResourceManagerReloadListener) manager -> CacheUtils.resetAllCache());
    }
}
