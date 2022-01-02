package Server.Exceptions;

public class NoFlightFound extends Exception{
    public NoFlightFound(){
        super();
    }

    public NoFlightFound(String msg){
        super(msg);
    }
}
