package userStorage;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {
    private UserController exampleController;
    private UserController exampleEmptyController;

    UserControllerTest() {
        exampleController = new UserController("src/userStorage/testingFiles/exampleUsers.ser");
        exampleController.loadUsers();
        exampleEmptyController = new UserController("src/userStorage/testingFiles/exampleEmptyFile.ser");
        exampleEmptyController.loadUsers();
    }

    @Test
    void testIfControllerLoadsEmptyFile() {
        UserController controller = new UserController("src/userStorage/testingFiles/testEmptyUserLoading.ser");
        controller.loadUsers();

        String expectedOutput = exampleEmptyController.getRegisteredUsers().toString();
        String realOutput = controller.getRegisteredUsers().toString();

        assertEquals(expectedOutput, realOutput);
    }

    @Test
    void testIfControllerLoadsFile() {
        UserController controller = new UserController("src/userStorage/testingFiles/testUserLoading.ser");
        controller.loadUsers();

        String expectedOutput = exampleController.getRegisteredUsers().toString();
        String realOutput = controller.getRegisteredUsers().toString();

        assertEquals(expectedOutput, realOutput);
    }
}