package ca.fxco.moreculling.mixin.blockstates;

import ca.fxco.moreculling.MoreCulling;
import ca.fxco.moreculling.api.block.MoreBlockCulling;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClient_loadBlocksMixin {


    @Inject(
            method = "<init>(Lnet/minecraft/client/RunArgs;)V",
            at = @At("RETURN")
    )
    private void onInit(RunArgs args, CallbackInfo ci) {
        Registry.BLOCK.forEach(block -> { // May be expensive, check on it
            ((MoreBlockCulling)block).setCanCull(MoreCulling.CONFIG.modCompatibility.putIfAbsent(
                    Registry.BLOCK.getId(block).getNamespace(),
                    MoreCulling.CONFIG.useOnModdedBlocksByDefault
            ));
        });
        MoreCulling.LOGGER.info("Cached all modded block culling states");
    }
}
