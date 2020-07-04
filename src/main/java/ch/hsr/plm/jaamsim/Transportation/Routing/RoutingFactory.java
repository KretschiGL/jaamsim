package ch.hsr.plm.jaamsim.Transportation.Routing;

import ch.hsr.plm.jaamsim.Transportation.ISteerable;

public class RoutingFactory {
    public static IRoutingStrategy getStrategyFor(ISteerable steerable) {
        switch (steerable.getRoutingStrategy()) {
            //RoutingStrategies.NetworkOnly: return new NetworkOnlyRoutingStrategy();
            default:
                return new DirectRoutingStrategy();
        }
    }
}
