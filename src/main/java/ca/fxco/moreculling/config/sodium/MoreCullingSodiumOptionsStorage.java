package ca.fxco.moreculling.config.sodium;

import ca.fxco.moreculling.MoreCulling;
import ca.fxco.moreculling.config.MoreCullingConfig;
import me.jellysquid.mods.sodium.client.gui.options.storage.OptionStorage;
import me.shedaniel.autoconfig.AutoConfig;

public class MoreCullingSodiumOptionsStorage implements OptionStorage<MoreCullingConfig> {

    @Override
    public MoreCullingConfig getData() {
        return MoreCulling.CONFIG;
    }

    @Override
    public void save() {
        AutoConfig.getConfigHolder(MoreCullingConfig.class).save();
    }
}
