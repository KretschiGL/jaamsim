package ch.hsr.plm.jaamsim.Transportation.Routing;

import ch.hsr.plm.jaamsim.Transportation.IMovable;
import ch.hsr.plm.jaamsim.Transportation.Node;
import com.jaamsim.math.Vec3d;

public class DirectRoute implements IRoute {

    private final IMovable _movable;
    private final Node _destination;

    public DirectRoute(IMovable movable, Node destination) {
        this._movable = movable;
        this._destination = destination;
        this._targetPosition = destination.getGlobalPosition();
        Vec3d direction = new Vec3d();
        direction.sub3(this._targetPosition, movable.getCurrentGlobalPosition());
        direction.normalize3();
        this._direction = direction;
        this._orientation.z = Math.atan2(direction.y, direction.x);
    }

    private final Vec3d _targetPosition;

    @Override
    public boolean destinationReached() {
        return this._targetPosition.near3(this._movable.getCurrentGlobalPosition());
    }

    private Vec3d _direction;
    private Vec3d _orientation = new Vec3d();

    private double _lastSimTime;

    @Override
    public boolean updatePosition(double simTime) {
        double dt = simTime - this._lastSimTime;
        double change = dt * this._movable.getCurrentSpeed(simTime);
        Vec3d movement = new Vec3d(this._direction);
        movement.scale3(change);
        movement.add3(this._movable.getCurrentGlobalPosition());
        this._movable.updateGlobalPosition(movement, this._orientation);
        this._lastSimTime = simTime;
        return this.destinationReached();
    }
}
