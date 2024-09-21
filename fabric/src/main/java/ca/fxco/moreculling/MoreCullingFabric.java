package ca.fxco.moreculling;

import net.fabricmc.api.ClientModInitializer;

public class MoreCullingFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        MoreCulling.init();
    }
}
