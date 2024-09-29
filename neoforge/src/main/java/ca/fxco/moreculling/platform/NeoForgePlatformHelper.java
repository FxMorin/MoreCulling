package ca.fxco.moreculling.platform;

import ca.fxco.moreculling.platform.services.IPlatformHelper;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;

public class NeoForgePlatformHelper implements IPlatformHelper {

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
        return !FMLLoader.isProduction();
    }

    @Override
    public String getModName(String modId) {
        ModContainer container = ModList.get().getModContainerById(modId).orElse(null);

        return container == null ? modId : container.getModInfo().getDisplayName();
    }
}