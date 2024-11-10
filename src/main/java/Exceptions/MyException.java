package Exceptions;

public class MyException extends Exception{ // name should be more specific
    public MyException(String msg){
        super(msg);
    }
}
