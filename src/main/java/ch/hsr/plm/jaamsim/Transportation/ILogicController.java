package ch.hsr.plm.jaamsim.Transportation;

public interface ILogicController {
    void register(IDispatchable dispatchable);
    void unregister(IDispatchable dispatchable);

    void reachedDestination(IDispatchable dispatchable, Node destination);
}
