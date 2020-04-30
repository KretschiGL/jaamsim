package ch.hsr.plm.jaamsim.Transportation;

import com.jaamsim.ProcessFlow.Device;

public class Vehicle extends Device {
    @Override
    protected boolean startProcessing(double simTime) {
        return false;
    }

    @Override
    protected double getStepDuration(double simTime) {
        return 0;
    }

    @Override
    protected void updateProgress(double dt) {

    }

    @Override
    protected void processStep(double simTime) {

    }

    @Override
    protected void processChanged() {

    }

    @Override
    protected boolean isNewStepReqd(boolean completed) {
        return false;
    }

    @Override
    protected void setProcessStopped() {

    }
}
