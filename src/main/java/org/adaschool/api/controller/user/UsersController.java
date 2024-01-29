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
    public ResponseEntity<User> updateUser(@PathVariable("id") String id, @RequestBody User user) {
        try {
            User updatedUser = usersService.update(user, id);
            if (updatedUser != null) {
                return ResponseEntity.ok(updatedUser);
            } else {
                // User with the specified ID was not found
                throw new UserNotFoundException(id);
            }
        } catch (UserNotFoundException e) {
            // Log the exception for debugging purposes
            e.printStackTrace();
            throw e; // Re-throw UserNotFoundException
        } catch (Exception e) {
            // Log the exception for debugging purposes
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", e);
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteUser(@PathVariable("id") String id) {
        Optional<User> optionalUser = usersService.findById(id);

        if (optionalUser.isEmpty()) {
            // User not found, return 404 status
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("user with ID: " + id + " not found");
        }

        // User found, delete and return 200 status
        usersService.deleteById(id);
        return ResponseEntity.ok("User deleted successfully");
    }
}
