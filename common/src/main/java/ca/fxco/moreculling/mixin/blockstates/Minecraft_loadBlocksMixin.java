package ca.fxco.moreculling.mixin.blockstates;

import ca.fxco.moreculling.MoreCulling;
import ca.fxco.moreculling.api.block.MoreBlockCulling;
import net.minecraft.client.Minecraft;
import net.minecraft.client.main.GameConfig;
import net.minecraft.core.registries.BuiltInRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class Minecraft_loadBlocksMixin {

    @Inject(
            method = "<init>",
            at = @At("RETURN")
    )
    private void moreculling$onInit(GameConfig args, CallbackInfo ci) {
        BuiltInRegistries.BLOCK.forEach(block -> {
            ((MoreBlockCulling) block).moreculling$setCanCull(MoreCulling.CONFIG.modCompatibility.putIfAbsent(
                    BuiltInRegistries.BLOCK.getKey(block).getNamespace(),
                    MoreCulling.CONFIG.useOnModdedBlocksByDefault
            ));
        });
        MoreCulling.LOGGER.info("Cached all modded block culling states");
    }
}
