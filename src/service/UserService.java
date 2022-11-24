package service;

import mapper.UserMapper;
import model.User;
import repository.UserRepository;
import repository.ipml.FileUserRepository;
import service.validation.UserValidationRequest;
import service.validation.UserValidationResult;
import service.validation.UserValidator;

import java.util.Optional;

public class UserService {
    private static UserService instance;
    private final UserRepository userRepository = FileUserRepository.getInstance();
    private final UserValidator userValidator = UserValidator.getInstance();
    private UserService() {}

    public static UserService getInstance() {
        if (instance == null) {
            instance = new UserService();
        }
        return instance;
    }
    public User checkLogin (String loginInput, String passwordInput) {
        String login = loginInput.trim();
        String password = passwordInput.trim();
        Optional<User> userOptional = userRepository.findByUsername(login);
        if (userOptional.isPresent()) {
            if ((userOptional.get().getPassword().equals(password))) {
                return userOptional.get();
            } else {
                return null;
            }
        } else {
            return null;
        }
    }
    public UserValidationResult validate(UserValidationRequest request) {
        if (userRepository.findByUsername(request.getUserName()).isPresent()) {
            UserValidationResult nonUniqueUserName = new UserValidationResult();
            nonUniqueUserName.addError("Этот логин занят");
            return nonUniqueUserName;
        }
        return userValidator.validate(request);
    }

    public User create(UserValidationRequest request) {
        return userRepository.save(UserMapper.toObject(request));
    }
}
