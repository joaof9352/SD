package Exceptions;

public class NoFlightFoundException extends Exception{
    public NoFlightFoundException(){
        super();
    }

    public NoFlightFoundException(String msg){
        super(msg);
    }
}
