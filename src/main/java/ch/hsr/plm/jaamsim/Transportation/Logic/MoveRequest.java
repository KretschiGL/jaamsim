package ch.hsr.plm.jaamsim.Transportation.Logic;

import ch.hsr.plm.jaamsim.Transportation.IRequest;
import ch.hsr.plm.jaamsim.Transportation.Node;

import java.util.Collection;

public class MoveRequest extends BaseRequest implements IRequest {

    public MoveRequest(Node destination) {
        this(destination, DEFAULT_PRIORITY);
    }

    public MoveRequest(Node destination, int priority) {
        this.add(destination);
        this.setPriority(priority);
    }

    public MoveRequest(Collection<Node> path) {
        this(path, DEFAULT_PRIORITY);
    }

    public MoveRequest(Collection<Node> path, int priority) {
        for(Node n : path) {
            this.add(n);
        }
        this.tasksUpdated();
        this.setPriority(priority);
    }



}
