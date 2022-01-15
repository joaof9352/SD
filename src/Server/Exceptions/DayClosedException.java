package Server.Exceptions;

public class DayClosedException extends Exception{

    public DayClosedException(){
        super();
    }

    public DayClosedException(String msg){
        super(msg);
    }
}
