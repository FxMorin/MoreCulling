package ca.fxco.moreculling.mixin.blockstates;

import ca.fxco.moreculling.MoreCulling;
import ca.fxco.moreculling.api.block.MoreBlockCulling;
import net.minecraft.client.Minecraft;
import net.minecraft.client.main.GameConfig;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(Minecraft.class)
public class Minecraft_loadBlocksMixin {

    @Inject(
            method = "<init>",
            at = @At("RETURN")
    )
    private void moreculling$onInit(GameConfig args, CallbackInfo ci) {
        if (MoreCulling.CONFIG == null) {
            return;
        }

        BuiltInRegistries.BLOCK.forEach(block -> {
            ((MoreBlockCulling) block).moreculling$setCanCull(MoreCulling.CONFIG.modCompatibility.putIfAbsent(
                    BuiltInRegistries.BLOCK.getKey(block).getNamespace(),
                    MoreCulling.CONFIG.useOnModdedBlocksByDefault
            ));
        });
        MoreCulling.CONFIG.dontCull.forEach(blockId -> {
            Optional<Block> optionalBlock =
                    BuiltInRegistries.BLOCK.getOptional(ResourceLocation.parse(blockId));

            if (optionalBlock.isEmpty()) {
                MoreCulling.LOGGER.warn("Block with id {} doesn't exist", blockId);
                return;
            }

            MoreBlockCulling block = (MoreBlockCulling) optionalBlock.get();
            if (block.moreculling$canCull())
                block.moreculling$setCanCull(false);
        });
        MoreCulling.LOGGER.info("Cached all modded block culling states");
    }
}
