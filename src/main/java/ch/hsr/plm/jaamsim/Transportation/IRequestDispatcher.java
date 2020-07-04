package ch.hsr.plm.jaamsim.Transportation;

public interface IRequestDispatcher {
    void enqueue(IRequest request);
    void revoke(IRequest request);
}
