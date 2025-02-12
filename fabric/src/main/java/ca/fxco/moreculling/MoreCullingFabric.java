package ca.fxco.moreculling;

import ca.fxco.moreculling.api.model.BakedOpacity;
import ca.fxco.moreculling.mixin.accessors.BlockModelShaperAccessor;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Unit;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import static ca.fxco.moreculling.MoreCulling.blockRenderManager;

public class MoreCullingFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        MoreCulling.init();

        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES)
                .registerReloadListener(new IdentifiableResourceReloadListener() {
                    @Override
                    public ResourceLocation getFabricId() {
                        return MoreCulling.RELOAD_LISTENER_ID;
                    }

                    @Override
                    public CompletableFuture<Void> reload(PreparationBarrier barrier, ResourceManager manager, Executor backgroundExecutor, Executor gameExecutor) {
                        return barrier.wait(Unit.INSTANCE).thenRunAsync(() -> {
                            ProfilerFiller profilerfiller = Profiler.get();
                            profilerfiller.push("listener");

                            Block.BLOCK_STATE_REGISTRY.forEach(BlockBehaviour.BlockStateBase::initCache);

                            ((BlockModelShaperAccessor) blockRenderManager.getBlockModelShaper()).getModels()
                                    .forEach((state, model) ->
                                            ((BakedOpacity) model).moreculling$resetTranslucencyCache(state));

                            profilerfiller.pop();
                        }, gameExecutor);
                    }
                });
    }
}
