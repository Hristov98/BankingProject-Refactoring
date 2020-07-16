package userStorage;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class UserWrapperTest {

    @Test
    void testIfWrapperIsInitialisedThroughObjectCopy() {
        UserWrapper wrapper = new UserWrapper();
        wrapper.addUser(new User("testUser1", "testPass1", AccessRights.ENCRYPTION));
        wrapper.addUser(new User("testUser2", "testPass2", AccessRights.DECRYPTION));
        wrapper.addUser(new User("testUser3", "testPass3", AccessRights.FULL_ACCESS));

        UserWrapper copy = new UserWrapper(wrapper);
        String expectedText = copy.toString();
        wrapper.addUser(new User("testUser4", "testPass4", AccessRights.FULL_ACCESS));
        String realText = copy.toString();

        Assertions.assertEquals(expectedText, realText);
    }

    @Test
    void testIfWrapperAddsUsers() {
        UserWrapper wrapper = new UserWrapper();

        String emptyWrapper = wrapper.toString();
        wrapper.addUser(new User("testUser1", "testPass1", AccessRights.ENCRYPTION));
        wrapper.addUser(new User("testUser2", "testPass2", AccessRights.DECRYPTION));
        wrapper.addUser(new User("testUser3", "testPass3", AccessRights.FULL_ACCESS));
        String fullWrapper = wrapper.toString();

        Assertions.assertNotEquals(emptyWrapper, fullWrapper);
    }

    @Test
    void testIfWrapperAddsDuplicateUsers() {
        UserWrapper wrapper = new UserWrapper();
        wrapper.addUser(new User("testUser1", "testPass1", AccessRights.ENCRYPTION));

        String wrapperBeforeDuplicates = wrapper.toString();
        wrapper.addUser(new User("testUser2", "testPass1", AccessRights.DECRYPTION));
        wrapper.addUser(new User("testUser1", "testPass2", AccessRights.FULL_ACCESS));
        String wrapperAfterDuplicates = wrapper.toString();

        Assertions.assertEquals(wrapperBeforeDuplicates, wrapperAfterDuplicates);
    }
}