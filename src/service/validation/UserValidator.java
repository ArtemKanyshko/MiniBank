package service.validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserValidator {
    private static UserValidator instance;
    private UserValidator (){}
    
    public static UserValidator getInstance(){
        if (instance == null) {
            instance = new UserValidator();
        }
        return instance;
    }

    public UserValidationResult validate(UserValidationRequest request) {
        UserValidationResult result = new UserValidationResult();
        Pattern patternUserName = Pattern.compile("^\\w{4,8}$");
        if (!patternUserName.matcher(request.getUserName()).matches()) {
            System.out.println();
        }
        Pattern patternPassword = Pattern.compile("^\\d{6,10}$");
        if (!patternPassword.matcher(request.getPassword()).matches()) {
            System.out.println();
        }
        Pattern patternName = Pattern.compile("^[a-zA-Z]+$");
        if (!patternName.matcher(request.getFirstName()).matches()) {
            System.out.println();
        }
        if (!patternName.matcher(request.getLastName()).matches()) {
            System.out.println();
        }
        Pattern patternEmail = Pattern.compile("\\w+@\\w+\\.-\\w+");
        return result;

    }
}
