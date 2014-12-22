/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cloud.exception;

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
