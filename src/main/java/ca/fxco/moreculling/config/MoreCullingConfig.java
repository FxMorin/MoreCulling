package ca.fxco.moreculling.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

@Config(name = "moreculling")
public class MoreCullingConfig implements ConfigData {

    // You can disable the sodium menu in the config
    public boolean enableSodiumMenu = true;

    public boolean useBlockStateCulling = true;

    public boolean useCustomItemFrameRenderer = true;

    public boolean useItemFrameLOD = true;

    public boolean useItemFrame3FaceCulling = true;
}
