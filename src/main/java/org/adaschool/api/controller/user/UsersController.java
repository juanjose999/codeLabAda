package org.adaschool.api.controller.user;

import org.adaschool.api.exception.ProductNotFoundException;
import org.adaschool.api.exception.UserNotFoundException;
import org.adaschool.api.repository.product.Product;
import org.adaschool.api.repository.user.User;
import org.adaschool.api.service.user.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.adaschool.api.repository.user.UserDto;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/v1/users/")
public class UsersController {

    private final UsersService usersService;

    public UsersController(@Autowired UsersService usersService) {
        this.usersService = usersService;
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User createUser = usersService.save(user);
        URI createdUserUri = URI.create("/v1/users/" + createUser.getId());
        return ResponseEntity.created(createdUserUri).body(createUser);
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = usersService.all();
        return ResponseEntity.ok(users);
    }

    @GetMapping("{id}")
    public ResponseEntity<?> findById(@PathVariable String id) {
        User user = usersService.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        return ResponseEntity.ok(user);
    }

    @PutMapping("{id}")
    public ResponseEntity<User> updateUser(@PathVariable("id") String id, @RequestBody UserDto userDto) {
        try {
            Optional<User> optionalUser = usersService.findById(id);
            if (optionalUser.isPresent()) {
                User existingUser = optionalUser.get();
                existingUser.setName(userDto.getName());
                existingUser.setLastName(userDto.getLastName());
                usersService.save(existingUser);  // Guardar el usuario actualizado
                return ResponseEntity.ok(existingUser);
            } else {
                throw new UserNotFoundException(id);
            }
        } catch (UserNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new UserNotFoundException(id);
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteUser(@PathVariable("id") String id) {
        Optional<User> optionalUser = usersService.findById(id);

        if (optionalUser.isEmpty()) {
            // User not found, throw a UserNotFoundException
            throw new UserNotFoundException(id);
        }

        // User found, delete and return 200 status
        usersService.deleteById(id);
        return ResponseEntity.ok("User deleted successfully");
    }
}
