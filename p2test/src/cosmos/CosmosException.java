package cosmos;

public class CosmosException extends Exception {
    private static final long serialVersionUID = 4579377646107356694L;

    public CosmosException(String message, Throwable cause) {
        super(message, cause);
    }
}
