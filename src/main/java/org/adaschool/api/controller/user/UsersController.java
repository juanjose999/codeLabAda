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
                .orElseThrow(() -> new UserNotFoundException("user with ID: " + id + " not found"));
        return ResponseEntity.ok(user);
    }

    @PutMapping("{id}")
    public ResponseEntity<User> updateUser(@PathVariable("id") String id, @RequestBody User user) {
        User updateUser = usersService.update(user, id);
        if(updateUser != null){
            return ResponseEntity.ok(updateUser);
        }else {
            throw new UserNotFoundException(id);
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") String id) {
        try {
            usersService.deleteById(id);
            System.out.println("User deleted successfully");
            return ResponseEntity.ok().build();
        } catch (UserNotFoundException e) {
            System.out.println("User not found");
            return ResponseEntity.notFound().build();
        }
    }
}
