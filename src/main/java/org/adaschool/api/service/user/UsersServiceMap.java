package org.adaschool.api.service.user;

import org.adaschool.api.exception.UserNotFoundException;
import org.adaschool.api.repository.user.User;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UsersServiceMap implements UsersService {
    private final Map<String, User> userMap = new HashMap<>();

    @Override
    public User save(User user) {
        String idUser = user.getId();
        userMap.put(idUser,user);
        return user;
    }

    @Override
    public Optional<User> findById(String id) {
        User user = userMap.get(id);

        // Return an Optional based on whether the user is found or not
        return Optional.ofNullable(user);
    }

    @Override
    public List<User> all() {
        return userMap.values().stream().collect(Collectors.toList());
    }

    @Override
    public void deleteById(String id) {
        System.out.println("Deleting user with ID: " + id);
        if(!userMap.containsKey(id)){
            System.out.println("User not found. Throwing UserNotFoundException");
            throw new UserNotFoundException(id);
        }
        System.out.println("User found. Removing from userMap");
        userMap.remove(id);
    }

    @Override
    public User update(User user, String userId) {
        if(userMap.containsKey(userId)){
            User userExisting = userMap.get(userId);
            userExisting.setName(user.getName());
            userExisting.setLastName(user.getLastName());
            userExisting.setEmail(user.getEmail());

            userMap.put(userId, userExisting);

            return userExisting;
        }else {
            throw new UserNotFoundException("user with ID: " + userId + " not found");
        }
    }
}
