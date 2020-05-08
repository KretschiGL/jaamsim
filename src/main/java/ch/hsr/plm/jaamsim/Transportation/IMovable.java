package ch.hsr.plm.jaamsim.Transportation;

import com.jaamsim.math.Vec3d;

public interface IMovable {
    double getCurrentSpeed(double simTime);
    Vec3d getCurrentGlobalPosition();
    void updateGlobalPosition(Vec3d position, Vec3d orientation);
}
