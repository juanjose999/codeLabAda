package org.adaschool.api.controller;

import org.adaschool.api.controller.user.UsersController;
import org.adaschool.api.exception.UserNotFoundException;
import org.adaschool.api.repository.user.User;
import org.adaschool.api.repository.user.UserDto;
import org.adaschool.api.service.user.UsersService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@SpringBootTest
public class UsersControllerTest {

    final String BASE_URL = "/v1/users/";
    @MockBean
    private UsersService usersService;
    @Autowired
    private UsersController controller;
    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = standaloneSetup(controller).build();
    }

    @Test
    public void testFindByIdExistingUser() throws Exception {
        User user = new User("1", "Ada", "Lovelace", "ada@mail.com", "123456789");
        when(usersService.findById("1")).thenReturn(Optional.of(user));

        mockMvc.perform(get(BASE_URL + "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("1")))
                .andExpect(jsonPath("$.name", is("Ada")))
                .andExpect(jsonPath("$.lastName", is("Lovelace")));

        verify(usersService, times(1)).findById("1");
    }

    @Test
    public void testFindByIdNotExistingUser() throws Exception {
        String id = "511";
        when(usersService.findById(id)).thenReturn(Optional.empty());

        mockMvc.perform(get(BASE_URL + id))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertInstanceOf(UserNotFoundException.class, result.getResolvedException()))
                .andExpect(result -> {
                    UserNotFoundException exception = assertInstanceOf(UserNotFoundException.class, result.getResolvedException());
                    assertTrue(exception.getMessage().contains("user with ID: " + id + " not found"));
                });

    }


    @Test
    public void testSaveNewUser() throws Exception {
        UserDto userDto = new UserDto("Ada", "Lovelace", "ada@mail.com", "123456789");
        User user = new User(userDto);

        when(usersService.save(any())).thenReturn(user);

        String json = "{" +
                "\"name\":\"Ada\"," +
                "\"lastName\":\"Lovelace\"," +
                "\"email\":\"ada@mail.com\"," +
                "\"password\":\"123456789\"" +
                "}";
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());

        verify(usersService, times(1)).save(any());
    }

    @Test
    public void testUpdateExistingUser() throws Exception {
        User user = new User("1", "Ada", "Lovelace", "ada@mail.com", "123456789");
        when(usersService.findById("1")).thenReturn(Optional.of(user));

        mockMvc.perform(get(BASE_URL + "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("Ada"))
                .andExpect(jsonPath("$.lastName").value("Lovelace"));

        verify(usersService, times(1)).findById("1");
    }

    @Test
    public void testUpdateNotExistingUser() throws Exception {
        String id = "1";
        when(usersService.findById(id)).thenReturn(Optional.empty());

        mockMvc.perform(delete(BASE_URL + id))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof UserNotFoundException))
                .andExpect(result -> assertEquals("404 NOT_FOUND \"user with ID: " + id + " not found\"", result.getResolvedException().getMessage()));

        // Verify that deleteById was not called since the user does not exist
        verify(usersService, times(0)).deleteById(id);
    }

    @Test
    public void testDeleteExistingUser() throws Exception {
        String id ="1";
        UserDto userDto = new UserDto("Ada", "Lovelace", "ada@mail.com", "123456789");
        User user = new User(userDto);
        when(usersService.findById(id)).thenReturn(Optional.empty());

        String json = "{\"id\":\"1\",\"name\":\"Ada\",\"lastName\":\"Lovelace\"}";
        mockMvc.perform(delete(BASE_URL + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());

        verify(usersService, times(1)).deleteById(id);
    }

    @Test
    public void testDeleteNotExistingUser() throws Exception {
        String id = "someUserId";

        // Configura el comportamiento del servicio mock
        doThrow(new UserNotFoundException("User not found")).when(usersService).deleteById(id);

        // Realiza la solicitud de eliminación y espera un código de estado 404
        mockMvc.perform(delete("/users/{id}", id))
                .andExpect(status().isNotFound());
    }

}
