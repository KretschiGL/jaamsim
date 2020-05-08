package ch.hsr.plm.jaamsim.Transportation;

import ch.hsr.plm.jaamsim.Transportation.Logic.IRequestHandlingStrategy;
import ch.hsr.plm.jaamsim.Transportation.Logic.RequestHandlingFactory;
import ch.hsr.plm.jaamsim.Transportation.Logic.RequestHandlingStrategies;
import com.jaamsim.input.EnumInput;
import com.jaamsim.input.Keyword;

import java.util.ArrayList;
import java.util.LinkedList;

public class LogicController extends RequestDispatcher {

    @Keyword(description = "The strategy used to dispatch requests.",
             exampleList = { "FIFO" })
    private final EnumInput<RequestHandlingStrategies> _requestHandling;

    {
        this._requestHandling = new EnumInput<>(RequestHandlingStrategies.class, "Request Dispatching", KEY_INPUTS, RequestHandlingStrategies.FIFO);
        this.addInput(this._requestHandling);
    }

    private IRequestHandlingStrategy _requestHandlingStrategy;
    @Override
    public void lateInit() {
        super.lateInit();
        this._requestHandlingStrategy = RequestHandlingFactory.getStrategyFor(this._requestHandling.getValue());
    }

    private final ArrayList<IDispatchable> _pool = new ArrayList<>();

    public void register(IDispatchable dispatchable) {
        if(dispatchable == null) {
            return;
        }
        if(this._pool.contains(dispatchable)) {
            return;
        }
        this._pool.add(dispatchable);
    }

    public void unregister(IDispatchable dispatchable) {
        if(dispatchable == null) {
            return;
        }
        this._pool.remove(dispatchable);
    }

    private final LinkedList<IRequest> _requests = new LinkedList<>();

    @Override
    public void enqueue(IRequest request) {
        this._requests.addLast(request);
        this.dispatch();
    }

    @Override
    public void revoke(IRequest request) {
        this._requests.removeIf(r -> r.equals(request));
    }

    public void notifyAvailability(IDispatchable dispatchable) {
        if(!this._pool.contains(dispatchable)) {
            return;
        }
        this.dispatch(dispatchable);
    }

    private void dispatch() {
        synchronized (this._requests) {
            if(this._requests.size() == 0) {
                return;
            }
            IDispatchable dispatchable = this.findDispatchableFor(this._requestHandlingStrategy.peekNext(this._requests));
            if (dispatchable == null) {
                return;
            }
            dispatchable.dispatch(this._requestHandlingStrategy.pollNext(this._requests));
        }
    }

    private IDispatchable findDispatchableFor(IRequest request) {
        synchronized (this._pool) {
            for(IDispatchable dispatchable : this._pool) {
                if(!this.canHandle(request, dispatchable)) {
                    continue;
                }
                return dispatchable;
            }
        }
        return null;
    }

    private void dispatch(IDispatchable dispatchable) {
        synchronized (this._requests) {
            if(this._requests.size() == 0) {
                return;
            }
            if (this.canHandle(this._requestHandlingStrategy.peekNext(this._requests), dispatchable)) {
                return;
            }
            dispatchable.dispatch(this._requestHandlingStrategy.pollNext(this._requests));
        }
    }

    private boolean canHandle(IRequest request, IDispatchable dispatchable) {
        if(dispatchable.isDispatched()) {
            return false;
        }
        if(!dispatchable.canHandle(request)) {
            return false;
        }
        return true;
    }

    @Override
    public void earlyInit() {
        super.earlyInit();
        this._pool.clear();
    }

    @Override
    public boolean isBusy() {
        return false;
    }

    @Override
    public void thresholdChanged() {

    }

    @Override
    public void kill() {
        this._pool.clear();
        this._requests.clear();
        super.kill();
    }
}
