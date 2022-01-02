package Server.Exceptions;

public class FlightIsFull extends Exception{

    public FlightIsFull(){
        super();
    }

    public FlightIsFull(String msg){
        super(msg);
    }

}
