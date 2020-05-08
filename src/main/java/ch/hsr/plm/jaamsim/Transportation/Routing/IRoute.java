package ch.hsr.plm.jaamsim.Transportation.Routing;

public interface IRoute {
    boolean destinationReached();
    boolean updatePosition(double simTime);
}
