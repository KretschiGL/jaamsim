package ch.hsr.plm.jaamsim.Transportation.Routing;

import ch.hsr.plm.jaamsim.Transportation.IMovable;
import ch.hsr.plm.jaamsim.Transportation.Node;

public interface IRoutingStrategy {
    IRoute getRouteTo(Node destination, IMovable movable);
}
