package Exceptions;

public class IncorrectPasswordException extends Exception{

    public IncorrectPasswordException(){
        super();
    }

    public IncorrectPasswordException(String msg){
        super(msg);
    }
}
