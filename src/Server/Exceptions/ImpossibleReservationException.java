package Server.Exceptions;

public class ImpossibleReservationException extends Exception{

    public ImpossibleReservationException(){
        super();
    }

    public ImpossibleReservationException(String msg){
        super(msg);
    }
}
