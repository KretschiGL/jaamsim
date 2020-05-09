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
    }

    private final Vec3d _targetPosition;

    private final double HIT_RANGE = 0.1d; // 10 Centimeter

    @Override
    public boolean destinationReached() {
        Vec3d target = new Vec3d(this._targetPosition);
        target.sub3(this._movable.getCurrentGlobalPosition());
        return target.mag3() < HIT_RANGE;
    }

    private Vec3d _direction;
    private Vec3d _orientation = new Vec3d();

    private double _lastSimTime = -1;

    @Override
    public void updatePosition(double simTime) {
        double change = 0.0d;
        if(this._lastSimTime > 0) {
            double dt = simTime - this._lastSimTime;
            change = dt * this._movable.getCurrentSpeed(simTime);
        }
        Vec3d path = new Vec3d(this._targetPosition);
        path.sub3(this._movable.getCurrentGlobalPosition());
        Vec3d movement = new Vec3d(path);
        movement.normalize3();
        movement.scale3(change);
        this._orientation.z = Math.atan2(movement.y, movement.x);

        if(movement.mag3() < path.mag3()) {
            movement.add3(this._movable.getCurrentGlobalPosition());
        } else {
            movement.set3(this._targetPosition);
        }
        this._movable.updateGlobalPosition(movement, this._orientation);
        this._lastSimTime = simTime;
    }

    @Override
    public Node getDestination() {
        return this._destination;
    }
}
