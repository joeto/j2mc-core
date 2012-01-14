package to.joe.j2mc.core.exceptions;

public class LostSQLConnectionException extends Exception {

    private static final long serialVersionUID = 1L;

    public LostSQLConnectionException() {
        super("Lost connection to MySQL Database");
    }

}
