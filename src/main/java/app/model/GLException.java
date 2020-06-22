package app.model;

public class GLException extends Exception{

    public GLException() {
    }

    public GLException(String message) {
        super(message);
    }

    public GLException(String message, Throwable cause) {
        super(message, cause);
    }

    public GLException(Throwable cause) {
        super(cause);
    }
}
