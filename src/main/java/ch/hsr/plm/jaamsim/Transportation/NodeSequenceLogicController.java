package ch.hsr.plm.jaamsim.Transportation;

import ch.hsr.plm.jaamsim.Transportation.Logic.MoveRequest;
import com.jaamsim.basicsim.EntityTarget;
import com.jaamsim.input.EntityListInput;
import com.jaamsim.input.Keyword;

import java.util.ArrayList;

public class NodeSequenceLogicController extends LogicController {

    @Keyword(description = "The sequence of nodes the controller should send its vehicles to.", exampleList = { "Node1 Node2" })
    private final EntityListInput<Node> _sequence;

    {
        this._sequence = new EntityListInput<>(Node.class, "Sequence", KEY_INPUTS, new ArrayList<>());
        this.addInput(this._sequence);
    }

    @Override
    public void startUp() {
        super.startUp();

        this.scheduleProcess(2, 1, new GenerateTour(this));
    }

    private static final class GenerateTour extends EntityTarget<NodeSequenceLogicController> {

        public GenerateTour(NodeSequenceLogicController ent) {
            super(ent, "generateTour");
        }

        @Override
        public void process() {
            this.ent.generateTour();
        }
    }

    private boolean _generatingTour = false;
    private void generateTour() {
        if(this._generatingTour) {
            return;
        }
        try {
            this._generatingTour = true;
            for (Node node : this._sequence.getValue()) {
                MoveRequest request = new MoveRequest(node);
                this.enqueue(request);
            }
        } finally {
            this._generatingTour = false;
        }
    }

    @Override
    protected void onDispatched(IDispatchable dispatchable) {
        if(this._generatingTour) {
            return;
        }
        if(this.numberOfRequestsInQueue(this.getSimTime()) != 0) {
            return;
        }
        this.startProcess(new GenerateTour(this));
    }
}
