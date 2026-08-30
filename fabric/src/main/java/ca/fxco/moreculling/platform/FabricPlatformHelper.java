package ca.fxco.moreculling.platform;

import ca.fxco.moreculling.platform.services.IPlatformHelper;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

public class FabricPlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {
        return "Fabric";
    }

    @Override
    public boolean isModLoaded(String modId) {

        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @Override
    public String getModName(String modId) {
        ModContainer container = FabricLoader.getInstance().getModContainer(modId).orElse(null);

        return container == null ? modId : container.getMetadata().getName();
    }

    @Override
    public List<BakedQuad> getQuads(BlockStateModel model, BlockState state, Direction direction,
                                    RandomSource source, BlockAndTintGetter level, BlockPos pos) {
        List<BakedQuad> quads = new ArrayList<>();
        List<BlockStateModelPart> parts = new ObjectArrayList<>();
        model.collectParts(source, parts);

        if (!parts.isEmpty()) {
            for (BlockStateModelPart part : parts) {
                quads.addAll(part.getQuads(direction));
            }
        }

        return quads;
    }
}
