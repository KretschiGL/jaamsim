package ch.hsr.plm.jaamsim.Transportation;

public interface IDispatchable {
    boolean isDispatched();

    boolean canHandle(IRequest request);
    void dispatch(IRequest request);
}
