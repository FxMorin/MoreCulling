package ca.fxco.moreculling;

import ca.fxco.moreculling.config.ModMenuConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;

public class MoreCullingFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        MoreCulling.init();
        ClientLifecycleEvents.CLIENT_STARTED.register(client -> ModMenuConfig.createConfigScreenBuilder(null));
    }
}
