package ch.hsr.plm.jaamsim.Transportation.Logic;

import ch.hsr.plm.jaamsim.Transportation.IRequest;
import ch.hsr.plm.jaamsim.Transportation.Node;

import java.util.ArrayList;
import java.util.Collection;

public class MoveRequest implements IRequest {

    private static final int DEFAULT_PRIORITY = 10;

    private final ArrayList<Node> _destinations = new ArrayList<>();
    private int _currentDestination = 0;
    private int _maxDestination = 0;

    public MoveRequest(Node destination) {
        this(destination, DEFAULT_PRIORITY);
    }

    public MoveRequest(Node destination, int priority) {
        this._destinations.add(destination);
        this.setPriority(priority);
    }

    public MoveRequest(Collection<Node> path) {
        this(path, DEFAULT_PRIORITY);
    }

    public MoveRequest(Collection<Node> path, int priority) {
        this._destinations.addAll(path);
        this._maxDestination = this._destinations.size() - 1;
        this.setPriority(priority);
    }

    private void setPriority(int priority) {
        this._priority = Math.max(1, priority);
    }

    private int _priority;

    @Override
    public int getPriority() {
        return this._priority;
    }

    private boolean _isCompleted = false;
    @Override
    public boolean isCompleted() {
        return this._isCompleted;
    }

    @Override
    public Node getCurrentDestination() {
        return this._destinations.get(this._currentDestination);
    }

    private final Object _mutex = new Object();

    @Override
    public boolean reached(Node node) {
        synchronized (this._mutex) {
            if (node != this.getCurrentDestination()) {
                return false;
            }
            if (this._currentDestination < this._maxDestination) {
                this._currentDestination++;
                return false;
            }
            this._isCompleted = true;
            return true;
        }
    }

    @Override
    public void dispose() {
        this._destinations.clear();
    }
}
