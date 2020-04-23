package ch.hsr.plm.jaamsim.ProcessFlow;

import com.jaamsim.DisplayModels.DisplayModel;
import com.jaamsim.DisplayModels.PolylineModel;
import com.jaamsim.Graphics.DisplayEntity;
import com.jaamsim.Graphics.LineEntity;
import com.jaamsim.Graphics.PolylineInfo;
import com.jaamsim.ProcessFlow.LinkedComponent;
import com.jaamsim.Samples.SampleConstant;
import com.jaamsim.Samples.SampleInput;
import com.jaamsim.basicsim.EntityTarget;
import com.jaamsim.events.Conditional;
import com.jaamsim.events.EventHandle;
import com.jaamsim.events.EventManager;
import com.jaamsim.input.*;
import com.jaamsim.math.Color4d;
import com.jaamsim.math.Vec3d;
import com.jaamsim.units.SpeedUnit;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class PathSegment extends LinkedComponent implements LineEntity {

    @Keyword(description = "The maximum speed an entity can have on this segment.",
             exampleList = {"2.0 m/s"})
    private final SampleInput _maxSpeed;

    @Keyword(description = "Determines whether to rotate the entities to match the path.",
            exampleList = {"TRUE"})
    private final BooleanInput _rotateEntities;

    @Keyword(description = "The width of the path in pixels.",
            exampleList = {"1"})
    private final IntegerInput _widthInput;

    @Keyword(description = "The colour of the path.",
            exampleList = {"red"})
    private final ColourInput _colorInput;

    @Keyword(description = "Show direction of the path.",
             exampleList = {"TRUE"})
    private final BooleanInput _showArrowHead;

    private final LinkedHashMap<Long, PathSegment.PathSegmentEntry> _entityMap = new LinkedHashMap<>();  // Entities being handled

    {
        this.displayModelListInput.clearValidClasses();
        this.displayModelListInput.addValidClass(PolylineModel.class);

        this.stateGraphics.setHidden(false);

        this._maxSpeed = new SampleInput("MaxSpeed", KEY_INPUTS, new SampleConstant(SpeedUnit.class, 1.0d));
        this._maxSpeed.setUnitType(SpeedUnit.class);
        this._maxSpeed.setValidRange(.001d, 299792458); // Speed of light obviously
        this.addInput(this._maxSpeed);

        this._rotateEntities = new BooleanInput("RotateEntities", FORMAT, true);
        this.addInput(this._rotateEntities);

        this._widthInput = new IntegerInput("LineWidth", FORMAT, 1);
        this._widthInput.setValidRange(1, Integer.MAX_VALUE);
        this._widthInput.setDefaultText("PolylineModel");
        this.addInput(this._widthInput);
        this.addSynonym(this._widthInput, "Width");

        this._colorInput = new ColourInput("LineColour", FORMAT, ColourInput.BLACK);
        this._colorInput.setDefaultText("PolylineModel");
        this.addInput(this._colorInput);
        this.addSynonym(this._colorInput, "Color");
        this.addSynonym(this._colorInput, "Colour");

        this._showArrowHead = new BooleanInput("ShowArrowHead", FORMAT, true);
        this.addInput(this._showArrowHead);
    }

    private static class PathSegmentEntry {
        final DisplayEntity _entity;
        final double _speed;

        PathSegmentEntry(DisplayEntity e, double startTime, double speed) {
            this._entity = e;
            this._lastTime = startTime;
            this._speed = speed;
        }

        private double _lastTime;
        private double _travelDist = 0.0d;
        double getRelativePositionTo(double totalDistance, double simTime) {
            double dt = simTime - this._lastTime;
            this._travelDist += dt * this._speed;
            this._lastTime = simTime;
            double relPos = this._travelDist / totalDistance;
            if(relPos >= 1.0d) {
                this._reachedEnd = true;
            }
            return relPos;
        }

        private boolean _reachedEnd = false;
        boolean reachedEnd() {
            return this._reachedEnd;
        }
    }

    @Override
    public void earlyInit() {
        super.earlyInit();
        this._entityMap.clear();
    }

    @Override
    public String getInitialState() {
        return STATE_IDLE;
    }

    @Override
    public void addEntity(DisplayEntity ent) {
        super.addEntity(ent);

        double simTime = this.getSimTime();
        double speed = this._maxSpeed.getValue().getMeanValue(simTime);
        double duration = this.getGraphicalLength(simTime) / speed;
        long ticks = EventManager.secsToNearestTick(duration);

        PathSegmentEntry entry = new PathSegmentEntry(ent, simTime, speed);
        this._entityMap.put(ent.getEntityNumber(), entry);

        TargetReachedHandler t = new TargetReachedHandler(this, entry);
        this.startProcess(t);

        this.updateState();
    }

    private static class TargetReachedHandler  extends EntityTarget<PathSegment> {

        private final PathSegment.PathSegmentEntry _entry;

        TargetReachedHandler(PathSegment source, PathSegment.PathSegmentEntry entry) {
            super(source, "onEndReached");
            this._entry = entry;
        }

        @Override
        public void process() {
            EventManager.waitUntil(new TargetReachedCondition(this._entry), null);
            this.ent.onEndReached(this._entry._entity);
        }
    }

    private static class TargetReachedCondition extends Conditional {

        private final PathSegment.PathSegmentEntry _entry;

        TargetReachedCondition(PathSegment.PathSegmentEntry entry) {
            this._entry = entry;
        }

        @Override
        public boolean evaluate() {
            return this._entry.reachedEnd();
        }
    }

    private void onEndReached(DisplayEntity entity) {
        this._entityMap.remove(entity.getEntityNumber());

        this.sendToNextComponent(entity);
        this.updateState();

        this.notifyObservers();
    }

    private void updateState() {
        if(this.getNumberInProgress() > 0) {
            this.setPresentState(STATE_WORKING);
        } else {
            this.setPresentState(STATE_IDLE);
        }
    }

    @Override
    public void updateGraphics(double simTime) {

        if (!usePointsInput())
            return;

        for (PathSegment.PathSegmentEntry entry : this._entityMap.values()) {
            double frac = entry.getRelativePositionTo(this.getGraphicalLength(simTime), simTime);

            entry._entity.setRegion(this.getCurrentRegion());

            Vec3d localPos = PolylineInfo.getPositionOnPolyline(getCurvePoints(), frac);
            entry._entity.setGlobalPosition(this.getGlobalPosition(localPos));

            Vec3d orient = new Vec3d();
            if (this._rotateEntities.getValue()) {
                orient.z = PolylineInfo.getAngleOnPolyline(getCurvePoints(), frac);
            }
            entry._entity.setRelativeOrientation(orient);
        }
    }

    @Override
    public boolean isOutlined() {
        return true;
    }

    @Override
    public int getLineWidth() {
        PolylineModel model = getPolylineModel();
        if (this._widthInput.isDefault() && model != null)
            return model.getLineWidth();
        return this._widthInput.getValue();
    }

    @Override
    public Color4d getLineColour() {
        PolylineModel model = this.getPolylineModel();
        if (this._colorInput.isDefault() && model != null)
            return model.getLineColour();
        return this._colorInput.getValue();
    }

    public PolylineModel getPolylineModel() {
        DisplayModel dm = this.getDisplayModel();
        if (dm instanceof PolylineModel)
            return (PolylineModel) dm;
        return null;
    }

    @Output(name = "EntityList",
            description = "The entities being processed at present.",
            sequence = 1)
    public ArrayList<DisplayEntity> getEntityList(double simTime) {
        ArrayList<DisplayEntity> ret = new ArrayList<>(this._entityMap.size());
        for (PathSegment.PathSegmentEntry entry : this._entityMap.values()) {
            ret.add(entry._entity);
        }
        return ret;
    }
}
