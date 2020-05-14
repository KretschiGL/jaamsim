package ch.hsr.plm.jaamsim.Transportation;

import ch.hsr.plm.jaamsim.Transportation.Logic.IRequestHandlingStrategy;
import ch.hsr.plm.jaamsim.Transportation.Logic.RequestHandlingFactory;
import ch.hsr.plm.jaamsim.Transportation.Logic.RequestHandlingStrategies;
import com.jaamsim.basicsim.EntityTarget;
import com.jaamsim.input.EnumInput;
import com.jaamsim.input.Keyword;
import com.jaamsim.input.Output;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class LogicController extends RequestDispatcher implements ILogicController {

    @Keyword(description = "The strategy used to dispatch requests.",
             exampleList = { "FIFO" })
    private final EnumInput<RequestHandlingStrategies> _requestHandling;

    {
        this._requestHandling = new EnumInput<>(RequestHandlingStrategies.class, "RequestDispatching", KEY_INPUTS, RequestHandlingStrategies.FIFO);
        this.addInput(this._requestHandling);
    }

    @Override
    public void earlyInit() {
        super.earlyInit();
        this._pool.clear();
        this._requests.clear();
    }

    private IRequestHandlingStrategy _requestHandlingStrategy;
    @Override
    public void lateInit() {
        super.lateInit();
        this._requestHandlingStrategy = RequestHandlingFactory.getStrategyFor(this._requestHandling.getValue());
    }

    private final ArrayList<IDispatchable> _pool = new ArrayList<>();

    @Override
    public void register(IDispatchable dispatchable) {
        if(dispatchable == null) {
            return;
        }
        synchronized (this._pool) {
            if (this._pool.contains(dispatchable)) {
                return;
            }
            this._pool.add(dispatchable);
        }
        this.startProcess(new TryDispatch(this, dispatchable));
    }

    @Override
    public void unregister(IDispatchable dispatchable) {
        if(dispatchable == null) {
            return;
        }
        synchronized (this._pool) {
            this._pool.remove(dispatchable);
        }
    }

    private static final class TryDispatch extends EntityTarget<LogicController> {

        private final IDispatchable _dispatchable;

        public TryDispatch(LogicController ent) {
            super(ent, "tryDispatch");
            this._dispatchable = null;
        }

        public TryDispatch(LogicController ent, IDispatchable dispatchable) {
            super(ent, "tryDispatch");
            this._dispatchable = dispatchable;
        }

        @Override
        public void process() {
            this.ent.dispatch(_dispatchable);
        }
    }

    private final LinkedList<IRequest> _requests = new LinkedList<>();

    @Override
    public void enqueue(IRequest request) {
        synchronized (this._requests) {
            this._requests.addLast(request);
        }
        this.startProcess(new TryDispatch(this));
    }

    @Override
    public void revoke(IRequest request) {
        synchronized (this._requests) {
            if (this._requests.removeIf(r -> r.equals(request))) {
                request.dispose();
            }
        }
        synchronized (this._dispatchedRequests) {
            for(Map.Entry<IDispatchable, IRequest> entry : this._dispatchedRequests.entrySet()) {
                if(!entry.getValue().equals(request)) {
                    continue;
                }
                request.dispose();
                this._dispatchedRequests.remove(entry.getKey());
                break;
            }
        }
    }

    public void notifyAvailability(IDispatchable dispatchable) {
        if(!this._pool.contains(dispatchable)) {
            return;
        }
        this.startProcess(new TryDispatch(this, dispatchable));
    }

    private final Map<IDispatchable, IRequest> _dispatchedRequests = new HashMap<>();

    private void dispatch(IDispatchable specific) {
        DoDispatch process = this.createDispatchProcess(specific);
        if(process == null) {
            return;
        }
        this.startProcess(process);
    }

    private DoDispatch createDispatchProcess(IDispatchable specific) {
        synchronized (this._requests) {
            if(this._requests.isEmpty()) {
                return null;
            }
            IDispatchable dispatchable = this.getDispatchableFor(this._requestHandlingStrategy.peekNext(this._requests), specific);
            if (dispatchable == null) {
                return null;
            }
            IRequest request = this._requestHandlingStrategy.pollNext(this._requests);
            synchronized (this._dispatchedRequests) {
                this._dispatchedRequests.put(dispatchable, request);
            }
            return new DoDispatch(this, dispatchable, request);
        }
    }

    private IDispatchable getDispatchableFor(IRequest request, IDispatchable dispatchable) {
        if(dispatchable != null) {
            if(this.canHandle(request, dispatchable)) {
                return dispatchable;
            }
            return null;
        }
        return this.findDispatchableFor(request);
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

    private boolean canHandle(IRequest request, IDispatchable dispatchable) {
        if(dispatchable.isDispatched()) {
            return false;
        }
        if(!dispatchable.canHandle(request)) {
            return false;
        }
        return true;
    }

    private static final class DoDispatch extends EntityTarget<LogicController> {

        private final IDispatchable _dispatchable;
        private final IRequest _request;

        public DoDispatch(LogicController ent, IDispatchable dispatchable, IRequest request) {
            super(ent, "dispatch");
            this._dispatchable = dispatchable;
            this._request = request;
        }

        @Override
        public void process() {
            this.ent.onDispatching(this._dispatchable, _request);
            this._dispatchable.dispatch(_request);
            this.ent.onDispatched(this._dispatchable);
        }
    }

    protected void onDispatching(IDispatchable dispatchable, IRequest request) {

    }

    protected void onDispatched(IDispatchable dispatchable) {

    }

    @Override
    public void reachedDestination(IDispatchable dispatchable, Node destination) {
        this.startProcess(new Visit(this, destination, dispatchable));
    }

    private static final class Visit extends EntityTarget<LogicController> {

        private final IDispatchable _dispatchable;
        private final Node _node;

        public Visit(LogicController ent, Node node, IDispatchable dispatchable) {
            super(ent, "visit");
            this._dispatchable = dispatchable;
            this._node = node;
        }

        @Override
        public void process() {
            this.ent.onVisit(this._node, this._dispatchable);
        }
    }

    private void onVisit(Node node, IDispatchable dispatchable) {
        IRequest request = this.getRequestOf(dispatchable);
        if(request == null) {
            this.log("No request found for '%s'.", dispatchable.getName());
            dispatchable.onRequestCompleted(null);
            return;
        }

        // node.enter(dispatchable);
        request.at(node).execute(dispatchable);
        //node.leave(dispatchable);

        this.startProcess(new VisitCompleted(this, node, dispatchable, request));
    }

    private static final class VisitCompleted extends EntityTarget<LogicController> {

        private final Node _node;
        private final IDispatchable _dispatchable;
        private final IRequest _request;

        public VisitCompleted(LogicController ent, Node node, IDispatchable dispatchable, IRequest request) {
            super(ent, "visitCompleted");
            this._node = node;
            this._dispatchable = dispatchable;
            this._request = request;
        }

        @Override
        public void process() {
            this.ent.onVisitCompleted(this._node, this._dispatchable, this._request);
        }
    }

    private void onVisitCompleted(Node node, IDispatchable dispatchable, IRequest request) {
        boolean hasNext = request.nextTask();
        if (hasNext) {
            dispatchable.dispatch(request);
        } else {
            dispatchable.onRequestCompleted(request);
            this.disposeRequest(request, dispatchable);
        }
    }

    private IRequest getRequestOf(IDispatchable dispatchable) {
        synchronized (this._dispatchedRequests) {
            return this._dispatchedRequests.getOrDefault(dispatchable, null);
        }
    }

    private void disposeRequest(IRequest request, IDispatchable dispatchable) {
        request.dispose();
        synchronized (this._dispatchedRequests) {
            this._dispatchedRequests.remove(dispatchable);
        }
    }

    private void log(String fmt, Object... args) {
        String message = String.format(fmt, args);
        this.getJaamSimModel().logMessage(message);
    }

    @Override
    public boolean isBusy() {
        return !this._requests.isEmpty() || !this._dispatchedRequests.isEmpty();
    }

    @Override
    public void thresholdChanged() {

    }

    @Output(name="Requests in Queue")
    public int numberOfRequestsInQueue(double simTime) {
        return this._requests.size();
    }

    @Output(name="Requests dispatched")
    public int numberOfRequestsDispatched(double simTime) {
        return this._dispatchedRequests.size();
    }

    @Override
    public void kill() {
        this._dispatchedRequests.clear();
        this._pool.clear();
        this._requests.clear();
        super.kill();
    }
}
