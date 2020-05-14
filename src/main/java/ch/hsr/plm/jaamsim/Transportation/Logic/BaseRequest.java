package ch.hsr.plm.jaamsim.Transportation.Logic;

import ch.hsr.plm.jaamsim.Transportation.IRequest;
import ch.hsr.plm.jaamsim.Transportation.Node;

import java.util.ArrayList;

public abstract class BaseRequest implements IRequest {

    protected static final int DEFAULT_PRIORITY = 10;

    private int _currentTask = 0;
    private int _maxTasks = 0;
    private int _priority;

    private final ArrayList<TaskEntry> _entries = new ArrayList<>();

    protected static final class TaskEntry {
        public final Node node;
        public final ITask task;

        public TaskEntry(Node node, ITask task) {
            this.node = node;
            this.task = task;
        }

        private TaskEntry(Node node) {
            this.node = node;
            this.task = new NullTask();
        }
    }

    protected void add(Node node) {
        this._entries.add(new TaskEntry(node));
    }

    protected void add(Node node, ITask task) {
        if(task == null) {
            throw new RuntimeException("Task is null. If no task should be registered, use add(Node node).");
        }
        this._entries.add(new TaskEntry(node, task));
    }

    public int getNumberOfTasks() {
        return this._entries.size();
    }

    protected void tasksUpdated() {
        this._maxTasks = this.getNumberOfTasks() - 1;
    }

    @Override
    public boolean nextTask() {
        if (this._currentTask < this._maxTasks) {
            this._currentTask++;
            return true;
        }
        this.setCompleted(true);
        return false;
    }

    protected TaskEntry getCurrentTask() {
        return this._entries.get(this._currentTask);
    }

    @Override
    public Node getCurrentDestination() {
        return this.getCurrentTask().node;
    }

    @Override
    public ITask at(Node node) {
        TaskEntry entry = this.getCurrentTask();
        if(entry.node.equals(node)) {
            return entry.task;
        }
        return new NullTask();
    }

    protected void setPriority(int priority) {
        this._priority = Math.max(1, priority);
    }

    @Override
    public int getPriority() {
        return this._priority;
    }

    protected void setCompleted(boolean isCompleted) {
        this._isCompleted = isCompleted;
    }

    private boolean _isCompleted = false;
    @Override
    public boolean isCompleted() {
        return this._isCompleted;
    }


    @Override
    public void dispose() {
        this.onDispose();
        this._entries.clear();
    }

    protected void onDispose() {

    }
}
