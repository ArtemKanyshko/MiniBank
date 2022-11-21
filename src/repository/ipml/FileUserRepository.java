package repository.ipml;

import logger.Logger;
import logger.LoggerFactory;
import mapper.UserMapper;
import model.User;
import repository.UserRepository;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.concurrent.atomic.AtomicLong;

public class FileUserRepository implements UserRepository {
    private static FileUserRepository instance;
    private static final String USER_FILE_PATH = "./resources/User.txt";
    private static final Logger log = LoggerFactory.getInstance(FileUserRepository.class);

    private AtomicLong userIdCounter;

    private FileUserRepository() {
        try (FileReader fileReader = new FileReader(USER_FILE_PATH);
             BufferedReader br = new BufferedReader(fileReader)) {
            OptionalLong maxUserId = br.lines()
                    .map(line -> line.split(",")[0])
                    .mapToLong(Long::parseLong)
                    .max();
            if (maxUserId.isPresent()) {
                userIdCounter = new AtomicLong(maxUserId.getAsLong());
                log.debug("Create UserIdCount with initial value: %d".formatted(userIdCounter.get()));
            } else {
                userIdCounter = new AtomicLong();
                log.debug("Create empty UserIdCounter started with 0");
            }
        } catch (IOException e) {
            log.error("Error during initializing");
        }
    }
    public static UserRepository getInstance () {
        if (instance == null ) {
            instance = new FileUserRepository();
        }
        return instance;
    }

    @Override
    public Optional<User> findByUsername(String username) {
        if (username == null || username.isEmpty()) {
            return Optional.empty();
        }
        try (FileReader fileReader = new FileReader(USER_FILE_PATH);
             BufferedReader br = new BufferedReader(fileReader)) {
            return br.lines()
                    .filter(line -> username.equals(line.split(",")[1]))
                    .map(UserMapper::toObject)
                    .findFirst();
        } catch (IOException e) {
            log.error("Error in time looking for user with username = " + username);
        }
        return Optional.empty();
    }

    @Override
    public User save(User user) {
        if (user.isNew()) {
            return insert(user);
        } else {
            return update(user);
        }
    }

    private User insert(User user) {
        // получаем новое id
        // обогащаем пользователя id
        // конвертируем пользователя в строку и сохраняем
        return null;
    }

    private User update(User user) {
        // вычетать все строки из файла и сохранить в локальную переменную
        // находим пользователя по id (сохранить номер строки)
        // заменяем старую строку пользователя на новую
        // записываем строки в файл
        return null;
    }
}
