package ch.hsr.plm.jaamsim.Transportation;

import ch.hsr.plm.jaamsim.Transportation.Routing.IRoute;
import ch.hsr.plm.jaamsim.Transportation.Routing.IRoutingStrategy;
import ch.hsr.plm.jaamsim.Transportation.Routing.RoutingFactory;
import ch.hsr.plm.jaamsim.Transportation.Routing.RoutingStrategies;
import com.jaamsim.Samples.SampleConstant;
import com.jaamsim.Samples.SampleInput;
import com.jaamsim.basicsim.EntityTarget;
import com.jaamsim.events.Conditional;
import com.jaamsim.events.EventManager;
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
        this._currentRequest = null;
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

    @Override
    public boolean isDispatched() {
        return this._currentRequest != null;
    }

    @Override
    public boolean canHandle(IRequest request) {
        if(this.isDispatched()) {
            return false;
        }
        return true;
    }

    private IRequest _currentRequest;
    @Override
    public void dispatch(IRequest request) {
        this._currentRequest = request;
        this.updateState();
        this.move();
    }

    private IRoute _currentRoute;

    private void move() {
        this._currentRoute = this._routingStrategy.getRouteTo(this._currentRequest.getCurrentDestination(), this);
        DestinationReachedHandler t = new DestinationReachedHandler(this);
        this.startProcess(t);
    }

    private static class DestinationReachedHandler extends EntityTarget<Vehicle> {

        public DestinationReachedHandler(Vehicle ent) {
            super(ent, "destinationReached");
        }

        @Override
        public void process() {
            EventManager.waitUntil(new DestinationReachedCondition(this.ent), null);
            this.ent.onDestinationReached();
        }
    }

    private static class DestinationReachedCondition extends Conditional {

        private Vehicle _vehicle;

        DestinationReachedCondition(Vehicle vehicle) {
            this._vehicle = vehicle;
        }

        @Override
        public boolean evaluate() {
            if(this._vehicle._currentRoute == null) {
                return true;
            }
            return this._vehicle._currentRoute.destinationReached();
        }
    }

    private void onDestinationReached() {
        boolean requestCompleted = this._currentRequest.reached(this._currentRoute.getDestination());
        if (!requestCompleted) {
            this.move();
            return;
        }
        this.onRequestCompleted();
        this.updateState();
        this._activeController.notifyAvailability(this);
    }

    private void onRequestCompleted(){
        this._currentRequest.dispose();
        this._currentRequest = null;
    }

    private void updateState() {
        if(this._currentRequest != null) {
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
