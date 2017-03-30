package wtsc.letsplay10;

/**
 * Created by a1995 on 3/30/2017.
 */

public class InputOutputException extends Exception {
    private String info;

    public InputOutputException(String info) {
        super();
        this.info = info;
    }

    public String getData() {
        return info;
    }
}
