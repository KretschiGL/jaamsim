package ch.hsr.plm.jaamsim.Transportation.Logic;

public class RequestHandlingFactory {
    public static IRequestHandlingStrategy getStrategyFor(RequestHandlingStrategies strategy) {
        switch (strategy) {
            default:
                return new FIFO();
        }
    }
}
