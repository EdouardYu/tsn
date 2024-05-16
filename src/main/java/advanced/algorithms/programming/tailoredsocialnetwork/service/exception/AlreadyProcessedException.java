package advanced.algorithms.programming.tailoredsocialnetwork.service.exception;

public class AlreadyProcessedException extends RuntimeException {
    public AlreadyProcessedException(String msg) {
        super(msg);
    }
}
