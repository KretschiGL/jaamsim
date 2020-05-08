package ch.hsr.plm.jaamsim.Transportation.Routing;

import ch.hsr.plm.jaamsim.Transportation.IMovable;
import ch.hsr.plm.jaamsim.Transportation.Node;

public class DirectRoutingStrategy implements IRoutingStrategy {

    @Override
    public IRoute getRouteTo(Node destination, IMovable movable) {
        return new DirectRoute(movable, destination);
    }
}
