package ch.hsr.plm.jaamsim.Transportation;

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


}
