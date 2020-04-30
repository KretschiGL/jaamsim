package ch.hsr.plm.jaamsim.Transportation;

import com.jaamsim.ProcessFlow.StateUserEntity;
import com.jaamsim.Samples.SampleConstant;
import com.jaamsim.Samples.SampleInput;
import com.jaamsim.input.Keyword;
import com.jaamsim.units.SpeedUnit;

public class Vehicle extends StateUserEntity {

    @Keyword(description = "Speed used to move towards the destination.",
             exampleList = {"2.0 m/s"})
    private final SampleInput _speed;

    {
        this.displayModelListInput.clearValidClasses();

        this.stateGraphics.setHidden(false);

        this._speed = new SampleInput("Speed", KEY_INPUTS, new SampleConstant(SpeedUnit.class, Traveling.DEFAULT_SPEED));
        this._speed.setUnitType(SpeedUnit.class);
        this._speed.setValidRange(Traveling.MIN_SPEED, Traveling.MAX_SPEED);
        this.addInput(this._speed);
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
}
