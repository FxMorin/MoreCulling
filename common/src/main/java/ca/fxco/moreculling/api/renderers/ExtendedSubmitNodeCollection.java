package ca.fxco.moreculling.api.renderers;

import ca.fxco.moreculling.api.renderers.modelsubmit.MorecullingBlockModelSubmit;

import java.util.List;

public interface ExtendedSubmitNodeCollection {
    default List<MorecullingBlockModelSubmit> moreculling$getBlockModelSubmits() {
        throw new UnsupportedOperationException("Implemented via Mixin.");
    }
}
