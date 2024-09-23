package ca.fxco.moreculling.config;

import me.fallenbreath.conditionalmixin.api.mixin.RestrictiveMixinConfigPlugin;

import java.util.List;
import java.util.Set;

public class MixinConfigPlugin extends RestrictiveMixinConfigPlugin {

    @Override
    public void onLoad(String mixinPackage) {
        super.onLoad(mixinPackage);
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}

    @Override
    public List<String> getMixins() {
        return null;
    }
}
