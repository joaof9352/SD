package Exceptions;

public class FlightIsFullException extends Exception{

    public FlightIsFullException(){
        super();
    }

    public FlightIsFullException(String msg){
        super(msg);
    }

}
