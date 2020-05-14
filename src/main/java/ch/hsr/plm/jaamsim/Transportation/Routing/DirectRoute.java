package ch.hsr.plm.jaamsim.Transportation.Routing;

import ch.hsr.plm.jaamsim.Transportation.IMovable;
import ch.hsr.plm.jaamsim.Transportation.Node;
import com.jaamsim.events.EventManager;
import com.jaamsim.math.Vec3d;

public class DirectRoute implements IRoute {

    private final IMovable _movable;
    private final Node _destination;

    public DirectRoute(IMovable movable, Node destination) {
        this._movable = movable;
        this._destination = destination;

        this._lastSimTime = EventManager.simSeconds();

        Vec3d dist = new Vec3d(destination.getGlobalPosition());
        dist.sub3(movable.getCurrentGlobalPosition());
        this._distance = dist.mag3();
        dist.normalize3();
        this._direction = dist;
        this._orientation.z = Math.atan2(dist.y, dist.x);
    }

    private final double _distance;
    private final Vec3d _direction;
    private final Vec3d _orientation = new Vec3d();

    @Override
    public double getDistance() {
        return this._distance;
    }

    private boolean _destinationReached = false;
    @Override
    public boolean destinationReached() {
        return this._destinationReached;
    }

    private double _lastSimTime;

    @Override
    public void updatePosition(double simTime) {
        double dt = simTime - this._lastSimTime;
        double change = dt * this._movable.getCurrentSpeed(simTime);
        Vec3d movement = new Vec3d(this._direction);
        movement.scale3(change);
        movement.add3(this._movable.getCurrentGlobalPosition());
        this._movable.updateGlobalPosition(movement, this._orientation);
        this._lastSimTime = simTime;
    }

    @Override
    public Node getDestination() {
        return this._destination;
    }
}
