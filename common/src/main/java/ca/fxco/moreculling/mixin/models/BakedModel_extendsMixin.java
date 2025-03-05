package ca.fxco.moreculling.mixin.models;

import ca.fxco.moreculling.api.model.BakedOpacity;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BlockStateModel.class)
public interface BakedModel_extendsMixin extends BakedOpacity {}