package im.bclpbkiauv.ui.utils.apache;

public class UnhandledException extends NestableRuntimeException {
    private static final long serialVersionUID = 1832101364842773720L;

    public UnhandledException(Throwable cause) {
        super(cause);
    }
}
