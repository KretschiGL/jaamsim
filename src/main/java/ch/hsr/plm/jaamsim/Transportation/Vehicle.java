package ch.hsr.plm.jaamsim.Transportation;

import com.jaamsim.ProcessFlow.StateUserEntity;
import com.jaamsim.Samples.SampleConstant;
import com.jaamsim.Samples.SampleInput;
import com.jaamsim.input.EntityInput;
import com.jaamsim.input.Keyword;
import com.jaamsim.input.Output;
import com.jaamsim.math.Vec3d;
import com.jaamsim.units.SpeedUnit;

public class Vehicle extends StateUserEntity {

    @Keyword(description = "Speed used to move towards the destination.",
             exampleList = { "2.0 m/s" })
    private final SampleInput _speed;

    @Keyword(description = "The node a vehicle should start from.",
             exampleList = { "Node1"})
    private final EntityInput<Node> _homeNode;

    {
        this.displayModelListInput.clearValidClasses();

        this.stateGraphics.setHidden(false);

        this._speed = new SampleInput("Speed", KEY_INPUTS, new SampleConstant(SpeedUnit.class, Traveling.DEFAULT_SPEED));
        this._speed.setUnitType(SpeedUnit.class);
        this._speed.setValidRange(Traveling.MIN_SPEED, Traveling.MAX_SPEED);
        this.addInput(this._speed);

        this._homeNode = new EntityInput<>(Node.class, "Home Node", KEY_INPUTS, null);
        this.addInput(this._homeNode);
    }

    @Override
    public void lateInit() {
        super.lateInit();
        this.resetGraphics();
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
    public void updateGraphics(double simTime) {
        super.updateGraphics(simTime);
    }

    @Output(name="Current Speed",
            description = "The current speed of the vehicle.",
            sequence = 1)
    public double getCurrentSpeed(double simTime) {
        return this._speed.getValue().getMeanValue(simTime);
    }
}
