package mongo.exception;

/**
 *
 * @author Lalala
 */
public class MongoException extends Exception {
    private String message;
    public MongoException() {
        message = "Mongo DB Controller encountered unknown error";
    }

    public MongoException(String message) {
        this.message = message;
    }

    
    @Override
    public String toString() {
        return getMessage();
    }

    @Override
    public String getMessage() {
        return this.message;
    }
    
}
