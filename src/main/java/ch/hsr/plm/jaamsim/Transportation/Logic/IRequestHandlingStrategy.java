package ch.hsr.plm.jaamsim.Transportation.Logic;

import ch.hsr.plm.jaamsim.Transportation.IRequest;

import java.util.LinkedList;

public interface IRequestHandlingStrategy {
    IRequest peekNext(LinkedList<IRequest> requests);
    IRequest pollNext(LinkedList<IRequest> requests);
}
