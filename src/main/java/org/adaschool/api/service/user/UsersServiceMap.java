package org.adaschool.api.service.user;

import org.adaschool.api.exception.UserNotFoundException;
import org.adaschool.api.repository.user.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
        if (!userMap.containsKey(id)) {
            throw new UserNotFoundException(id);
        }
        userMap.remove(id);
    }

    @Override
    public User update(User user, String userId) {
        if (!userMap.containsKey(userId)) {
            // Usuario no encontrado, lanzar excepción o manejar según sea necesario
            throw new UserNotFoundException("User with ID " + userId + " not found");
        }

        User existingUser = userMap.get(userId);

        // Actualizar campos
        existingUser.setName(user.getName());
        existingUser.setLastName(user.getLastName());
        existingUser.setEmail(user.getEmail());

        // Actualizar contraseña si ha cambiado
        if (user.getPassword() != null && !user.getPassword().isEmpty() && !user.getPassword().equals(existingUser.getPassword())) {
            // Codificar la nueva contraseña
            existingUser.setPasswordHash(new BCryptPasswordEncoder().encode(user.getPassword()));
        }

        // Actualizar el usuario en el mapa
        userMap.put(userId, existingUser);

        return existingUser;
    }
}
