package nti.exception;

import nti.model.OrderStatus;

public class InvalidOrderStateException extends RuntimeException {

    public InvalidOrderStateException(Long orderId, OrderStatus current, String action) {
        super("Cannot " + action + " order " + orderId + " in state " + current);
    }
}