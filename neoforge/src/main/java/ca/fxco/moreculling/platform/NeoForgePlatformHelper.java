package ca.fxco.moreculling.platform;

import ca.fxco.moreculling.platform.services.IPlatformHelper;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;

import java.util.ArrayList;
import java.util.List;

public class NeoForgePlatformHelper implements IPlatformHelper {
    private final List<BlockStateModelPart> parts = new ObjectArrayList<>();

    @Override
    public String getPlatformName() {

        return "NeoForge";
    }

    @Override
    public boolean isModLoaded(String modId) {

        return ModList.get().isLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return !FMLLoader.getCurrent().isProduction();
    }

    @Override
    public String getModName(String modId) {
        ModContainer container = ModList.get().getModContainerById(modId).orElse(null);

        return container == null ? modId : container.getModInfo().getDisplayName();
    }

    @Override
    public List<BakedQuad> getQuads(BlockStateModel model, BlockState state, Direction direction,
                                    RandomSource source, BlockAndTintGetter level, BlockPos pos) {
        List<BakedQuad> quads = new ArrayList<>();
        model.collectParts(level, pos, state, source, parts);

        if (!this.parts.isEmpty()) {
            try {
                for (BlockStateModelPart part : parts) {
                    quads.addAll(part.getQuads(direction));
                }
            } finally {
                this.parts.clear();
            }
        }

        return quads;
    }

}