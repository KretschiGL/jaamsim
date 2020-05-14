package ch.hsr.plm.jaamsim.Transportation;

public interface IDispatchable {
    String getName();

    boolean isDispatched();

    boolean canHandle(IRequest request);
    void dispatch(IRequest request);
    void onHandle(IRequest request, Node node);
    void onHandleCompleted(IRequest request, Node node);
    void onRequestCompleted(IRequest request);
}
