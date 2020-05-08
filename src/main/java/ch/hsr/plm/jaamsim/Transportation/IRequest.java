package ch.hsr.plm.jaamsim.Transportation;

public interface IRequest {
    int getPriority();

    boolean isCompleted();
    Node getCurrentDestination();


}
