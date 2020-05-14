package ch.hsr.plm.jaamsim.Transportation;

import ch.hsr.plm.jaamsim.Transportation.Logic.ITask;

public interface IRequest {
    int getPriority();

    boolean isCompleted();
    Node getCurrentDestination();
    boolean nextTask();

    ITask at(Node node);

    void dispose();
}
