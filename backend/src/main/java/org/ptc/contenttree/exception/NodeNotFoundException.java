package org.ptc.contenttree.exception;

public class NodeNotFoundException extends RuntimeException {

    public NodeNotFoundException() {
        super("Node not found!");
    }

    public NodeNotFoundException(String msg) {
        super(msg);
    }

    public NodeNotFoundException(String msg, Throwable e) {
        super(msg, e);
    }
}
