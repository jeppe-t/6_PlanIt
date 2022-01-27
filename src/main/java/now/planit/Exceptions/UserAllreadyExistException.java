package now.planit.Exceptions;

//Superklassen er Throwable, derefter Exception, RuntimeException, osv..
public class UserAllreadyExistException extends Throwable {

    public UserAllreadyExistException(String message) {
        super(message);
    }
}
