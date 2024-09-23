package ca.fxco.moreculling.config;

import ca.fxco.moreculling.utils.MathUtils;
import me.shedaniel.autoconfig.AutoConfig;

import static ca.fxco.moreculling.MoreCulling.CURRENT_VERSION;

public class ConfigUpdater {

    public static void updateConfig(MoreCullingConfig config) {
        if (config.version == 0) { // Use real number that humans can compute
            config.itemFrameLODRange = MathUtils.clamp((int) Math.round(Math.sqrt(config.itemFrameLODRange)), 16, 256);
            config.itemFrame3FaceCullingRange = MathUtils.clamp((float) Math.sqrt(config.itemFrame3FaceCullingRange), 2F, 16F);
        }
        if (config.version != CURRENT_VERSION) {
            config.version = CURRENT_VERSION;
            AutoConfig.getConfigHolder(MoreCullingConfig.class).save();
        }
    }
}
