package to.joe.j2mc.core.exceptions;

public class TooManyPlayersException extends BadPlayerMatchException {

    private static final long serialVersionUID = 1L;

    public TooManyPlayersException(int size) {
        super("Matches too many players");
    }

}
