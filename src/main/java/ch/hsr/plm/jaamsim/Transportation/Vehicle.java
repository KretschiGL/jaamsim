package ch.hsr.plm.jaamsim.Transportation;

import ch.hsr.plm.jaamsim.Transportation.Routing.IRoute;
import ch.hsr.plm.jaamsim.Transportation.Routing.IRoutingStrategy;
import ch.hsr.plm.jaamsim.Transportation.Routing.RoutingFactory;
import ch.hsr.plm.jaamsim.Transportation.Routing.RoutingStrategies;
import com.jaamsim.Samples.SampleConstant;
import com.jaamsim.Samples.SampleInput;
import com.jaamsim.basicsim.EntityTarget;
import com.jaamsim.events.ProcessTarget;
import com.jaamsim.input.*;
import com.jaamsim.math.Vec3d;
import com.jaamsim.units.SpeedUnit;

public class Vehicle extends RequestDispatcher implements IMovable, ISteerable, IDispatchable {

    @Keyword(description = "Speed used to move towards the destination.",
             exampleList = { "2.0 m/s" })
    private final SampleInput _speed;

    @Keyword(description = "The node a vehicle should start from.",
             exampleList = { "Node1"})
    private final EntityInput<Node> _homeNode;

    @Keyword(description = "The behavior how the vehicle should reach the destination.",
             exampleList = { "Direct" })
    private final EnumInput<RoutingStrategies> _routing;

    @Keyword(description = "The controller responsible for dispatching this vehicle. If not set, the vehicle controls itself.",
             exampleList = { "Controller1" })
    private final EntityInput<LogicController> _controller;

    {
        this.displayModelListInput.clearValidClasses();

        this.stateGraphics.setHidden(false);

        this._speed = new SampleInput("Speed", KEY_INPUTS, new SampleConstant(SpeedUnit.class, Traveling.DEFAULT_SPEED));
        this._speed.setUnitType(SpeedUnit.class);
        this._speed.setValidRange(Traveling.MIN_SPEED, Traveling.MAX_SPEED);
        this.addInput(this._speed);

        this._homeNode = new EntityInput<>(Node.class, "HomeNode", KEY_INPUTS, null);
        this.addInput(this._homeNode);

        this._routing = new EnumInput<>(RoutingStrategies.class, "Routing", KEY_INPUTS, RoutingStrategies.Direct);
        this.addInput(this._routing);

        this._controller = new EntityInput<>(LogicController.class, "Controller", KEY_INPUTS, null);
        this.addInput(this._controller);
    }

    @Override
    public void earlyInit() {
        super.earlyInit();
        this._currentRoute = null;
    }

    @Override
    public void lateInit() {
        super.lateInit();
        this.resetGraphics();
        this.setupController();
        this.setupRouting();
    }

    private LogicController _activeController;
    private void setupController() {
        this._activeController = this._controller.getValue();
        if(this._activeController == null) {
            this._activeController = this.createInternalController();
        }
        this._activeController.register(this);
    }

    private LogicController createInternalController() {
        LogicController controller = this.getJaamSimModel().createInstance(LogicController.class, this.getName() + "_Controller", this, false, true, false ,false);
        if(controller == null) {
            throw new RuntimeException("Unable to create controller for vehicle " + this.getName());
        }
        controller.earlyInit();
        controller.lateInit();
        return controller;
    }

    private IRoutingStrategy _routingStrategy;
    private void setupRouting() {
        this._routingStrategy = RoutingFactory.getStrategyFor(this);
    }

    @Override
    public void startUp() {
        super.startUp();
        this.sendHome();
    }

    private void sendHome() {
        Node node = this._homeNode.getValue();
        if(node == null) {
            return;
        }
        this.setGlobalPosition(node.getGlobalPosition());
    }

    @Override
    public String getInitialState() {
        return STATE_IDLE;
    }

    @Override
    public boolean isBusy() {
        return false;
    }

    @Override
    public void thresholdChanged() {

    }

    @Override
    public Vec3d getCurrentGlobalPosition() {
        return this.getGlobalPosition();
    }

    @Override
    public void updateGlobalPosition(Vec3d position, Vec3d orientation) {
        this.setGlobalPosition(position);
        this.setRelativeOrientation(orientation);
    }

    @Override
    public RoutingStrategies getRoutingStrategy() {
        return this._routing.getValue();
    }

    @Override
    public void enqueue(IRequest request) {
        this._activeController.enqueue(request);
    }

    @Override
    public void revoke(IRequest request) {
        this._activeController.revoke(request);
    }

    private boolean _isDispatched = false;
    @Override
    public boolean isDispatched() {
        return this._isDispatched;
    }

    @Override
    public boolean canHandle(IRequest request) {
        if(this.isDispatched()) {
            return false;
        }
        return true;
    }

    @Override
    public void dispatch(IRequest request) {
        this._isDispatched = true;
        this.updateState();
        this.startProcess(new HandleRequest(this , request,"handleRequest"));
    }

    private static final class HandleRequest extends EntityTarget<Vehicle> {

        private final IRequest _request;

        public HandleRequest(Vehicle ent, IRequest request, String method) {
            super(ent, method);
            this._request = request;
        }

        @Override
        public void process() {
            this.ent.moveTo(this._request.getCurrentDestination());
        }
    }

    private IRoute _currentRoute;

    private void moveTo(Node destination) {
        this._currentRoute = this._routingStrategy.getRouteTo(destination, this);
        double destinationReachedTime = this._currentRoute.getDistance() / this.getCurrentSpeed(this.getSimTime());
        this.scheduleProcess(destinationReachedTime, 1, new DestinationReachedHandler(this));
    }

    private static class DestinationReachedHandler extends EntityTarget<Vehicle> {

        public DestinationReachedHandler(Vehicle ent) {
            super(ent, "destinationReached");
        }

        @Override
        public void process() {
            this.ent.onDestinationReached();
        }
    }

    private void onDestinationReached() {
        Node destination = this._currentRoute.getDestination();
        this._currentRoute = null;
        this._activeController.reachedDestination(this, destination);
    }

    @Override
    public void onHandle(IRequest request, Node node) {

    }

    @Override
    public void onHandleCompleted(IRequest request, Node node) {

    }

    @Override
    public void onRequestCompleted(IRequest request) {
        this._isDispatched = false;
        this.updateState();
        this.startProcess(vehicleAvailable);
    }

    private ProcessTarget vehicleAvailable = new EntityTarget<>(this, "vehicleAvailable") {

        @Override
        public void process() {
            this.ent.onVehicleAvailable();
        }
    };

    private void onVehicleAvailable() {
        this._activeController.notifyAvailability(this);
    }

    private void updateState() {
        if(this.isDispatched()) {
            this.setPresentState(STATE_WORKING);
        } else {
            this.setPresentState(STATE_IDLE);
        }
    }

    @Override
    public void updateGraphics(double simTime) {
        super.updateGraphics(simTime);
        if(this._currentRoute == null) {
            return;
        }
        this._currentRoute.updatePosition(simTime);
    }

    @Output(name="Current Speed",
            description = "The current speed of the vehicle.",
            sequence = 1)
    public double getCurrentSpeed(double simTime) {
        return this._speed.getValue().getMeanValue(simTime);
    }

    @Override
    public void kill() {
        if(this._activeController != null) {
            this._activeController.unregister(this);
        }
        super.kill();
    }
}
