package repository.ipml;

import logger.Logger;
import logger.LoggerFactory;
import mapper.UserMapper;
import model.User;
import repository.UserRepository;

import java.io.*;
import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class FileUserRepository implements UserRepository {
    private static FileUserRepository instance;
    private static final String USERS_FILE_PATH = "./resources/Users.csv";
    private static final Logger log = LoggerFactory.getInstance(FileUserRepository.class);

    private AtomicLong userIdCounter;

    private FileUserRepository() {
        try (FileReader fileReader = new FileReader(USERS_FILE_PATH);
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
        try (FileReader fileReader = new FileReader(USERS_FILE_PATH);
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
        try (FileWriter fileWriter = new FileWriter(USERS_FILE_PATH, true);
             BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
             PrintWriter printWriter = new PrintWriter(bufferedWriter)) {

            long newUserId = userIdCounter.incrementAndGet();
            log.debug("Generated new UserIdCounter value: %d".formatted(newUserId));
            user.setId(newUserId);
            String csvString = UserMapper.toCsv(user);
            printWriter.println(csvString);
            log.debug("Saved new User \"%s\"".formatted(csvString));
            return user;
        } catch (IOException e) {
            log.error("Error during inserting new user. " + e.getMessage());
            return null;
        }
    }

    private User update(User user) {
        try (FileReader reader = new FileReader(USERS_FILE_PATH);
             BufferedReader bufferedReader = new BufferedReader(reader)) {

            List<String> lines = bufferedReader.lines().collect(Collectors.toList());
            int updateLineIndex = -1;
            for (int i = 0; i < lines.size(); i++) {
                if (lines.get(i).split(",")[0].equals(user.getId().toString())) {
                    updateLineIndex = i;
                }
            }

            String csvString = UserMapper.toCsv(user);
            if (updateLineIndex != -1) {
                lines.remove(updateLineIndex);
                lines.add(updateLineIndex, csvString);
            }

            try (FileWriter writer = new FileWriter(USERS_FILE_PATH);
                 BufferedWriter bufferedWriter = new BufferedWriter(writer);
                 PrintWriter printWriter = new PrintWriter(bufferedWriter)) {
                lines.forEach(printWriter::println);
            }
            log.debug("Updated User \"%s\"".formatted(csvString));

            return user;
        } catch (IOException e) {
            log.error("Error during updating user. " +  e.getMessage());
            return null;
        }
    }
}
