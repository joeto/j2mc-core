package to.joe.j2mc.core.exceptions;

public class NoPlayersException extends BadPlayerMatchException {

    private static final long serialVersionUID = 1L;

    public NoPlayersException() {
        super("No players matched");
    }

}
