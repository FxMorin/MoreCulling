package ca.fxco.moreculling;

import ca.fxco.moreculling.config.ModMenuConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(MoreCulling.MOD_ID)
public class MoreCullingNeoforge {
    public MoreCullingNeoforge(ModContainer container) {
        MoreCulling.init();

        container.registerExtensionPoint(IConfigScreenFactory.class, (con, screen) ->  ModMenuConfig.createConfigScreen(screen));
    }
}
