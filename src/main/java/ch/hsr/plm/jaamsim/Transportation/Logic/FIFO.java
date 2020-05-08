package ch.hsr.plm.jaamsim.Transportation.Logic;

import ch.hsr.plm.jaamsim.Transportation.IRequest;

import java.util.LinkedList;

public class FIFO implements IRequestHandlingStrategy {

    @Override
    public IRequest peekNext(LinkedList<IRequest> requests) {
        return requests.peekFirst();
    }

    @Override
    public IRequest pollNext(LinkedList<IRequest> requests) {
        return requests.pollFirst();
    }
}
