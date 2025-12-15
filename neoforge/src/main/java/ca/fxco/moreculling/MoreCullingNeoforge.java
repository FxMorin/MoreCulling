package ca.fxco.moreculling;

import ca.fxco.moreculling.api.blockstate.StateCullingShapeCache;
import ca.fxco.moreculling.api.model.BakedOpacity;
import ca.fxco.moreculling.config.ModMenuConfig;
import ca.fxco.moreculling.mixin.accessors.BlockModelShaperAccessor;
import net.minecraft.util.Unit;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.AddClientReloadListenersEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

import static ca.fxco.moreculling.MoreCulling.blockRenderManager;

@Mod(value = MoreCulling.MOD_ID, dist = Dist.CLIENT)
public class MoreCullingNeoforge {
    public MoreCullingNeoforge(ModContainer container, IEventBus bus) {
        MoreCulling.init();

        container.registerExtensionPoint(IConfigScreenFactory.class,
                (con, screen) ->  ModMenuConfig.createConfigScreen(screen));

        bus.addListener(this::registerReloadListener);
    }

    public void registerReloadListener(AddClientReloadListenersEvent event) {
        event.addListener(MoreCulling.RELOAD_LISTENER_ID, (barrier,
                                                           resourceManager,
                                                           executor, gameExecutor) ->
                executor.wait(Unit.INSTANCE).thenRunAsync(() -> {
                    ProfilerFiller profilerfiller = Profiler.get();
                    profilerfiller.push("listener");

                    Block.BLOCK_STATE_REGISTRY.forEach(state -> ((StateCullingShapeCache) state).moreculling$initCustomCullingShape());

                    ((BlockModelShaperAccessor) blockRenderManager.getBlockModelShaper()).getModels()
                            .forEach((state, model) -> {
                                if (!state.canOcclude()) {
                                    ((BakedOpacity) model).moreculling$resetTranslucencyCache(state);
                                }
                            });

                    profilerfiller.pop();
                }, gameExecutor));
    }
}
