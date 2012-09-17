package to.joe.j2mc.core.exceptions;

public class TooManyPlayersException extends BadPlayerMatchException {

    private static final long serialVersionUID = 1L;

    public TooManyPlayersException(String players) {
        super("Matches too many players (" + players + ")");
    }

}
