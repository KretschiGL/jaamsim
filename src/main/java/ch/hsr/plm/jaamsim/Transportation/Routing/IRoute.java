package ch.hsr.plm.jaamsim.Transportation.Routing;

import ch.hsr.plm.jaamsim.Transportation.Node;

public interface IRoute {
    boolean destinationReached();
    void updatePosition(double simTime);
    Node getDestination();
}
