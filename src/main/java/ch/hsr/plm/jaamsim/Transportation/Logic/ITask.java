package ch.hsr.plm.jaamsim.Transportation.Logic;

import ch.hsr.plm.jaamsim.Transportation.IDispatchable;

public interface ITask {
    void execute(IDispatchable dispatchable);
}
