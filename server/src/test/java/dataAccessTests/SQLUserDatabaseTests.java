package dataAccessTests;

import dataAccess.UserDAO.UserDAO;
import dataAccess.DataAccessException;
import dataAccess.UserDAO.SQLUserDatabase;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SQLUserDatabaseTests {

    @Test
    void createUserSuccess() throws DataAccessException {
        UserDAO userDAO = new SQLUserDatabase();

        userDAO.createUser("GoodUsername", "GoodPassword", "GoodEmail");
        userDAO.createUser("GoodUsername1", "GoodPassword1", "GoodEmail1");

        UserData retrievedData = userDAO.getUser("GoodUsername");
        UserData retrievedData1 = userDAO.getUser("GoodUsername1");

        Assertions.assertEquals(new UserData("GoodUsername", "GoodPassword", "GoodEmail"), retrievedData);
        Assertions.assertEquals(new UserData("GoodUsername1", "GoodPassword1", "GoodEmail1"), retrievedData1);

        userDAO.deleteUsers();
    }

    @Test
    void createUserFail() throws DataAccessException {
        UserDAO userDAO = new SQLUserDatabase();

        Assertions.assertThrows(DataAccessException.class, () -> userDAO.createUser(null, null, null));
    }

    @Test
    void getUserSuccess() throws DataAccessException {
        UserDAO userDAO = new SQLUserDatabase();

        userDAO.createUser("GoodUsername", "GoodPassword", "GoodEmail");
        userDAO.createUser("GoodUsername1", "GoodPassword1", "GoodEmail1");

        UserData retrievedData = userDAO.getUser("GoodUsername");
        UserData retrievedData1 = userDAO.getUser("GoodUsername1");

        Assertions.assertEquals(new UserData("GoodUsername", "GoodPassword", "GoodEmail"), retrievedData);
        Assertions.assertEquals(new UserData("GoodUsername1", "GoodPassword1", "GoodEmail1"), retrievedData1);

        userDAO.deleteUsers();
    }

    @Test
    void getUserFail() throws DataAccessException {
        UserDAO userDAO = new SQLUserDatabase();

        Assertions.assertNull(userDAO.getUser("test"));
    }

    @Test
    void deleteAllUsersSuccess() throws DataAccessException {
        UserDAO userDAO = new SQLUserDatabase();

        userDAO.createUser("GoodUsername", "GoodPassword", "GoodEmail");
        userDAO.createUser("GoodUsername1", "GoodPassword1", "GoodEmail1");

        UserData retrievedData = userDAO.getUser("GoodUsername");
        UserData retrievedData1 = userDAO.getUser("GoodUsername1");

        Assertions.assertEquals(new UserData("GoodUsername", "GoodPassword", "GoodEmail"), retrievedData);
        Assertions.assertEquals(new UserData("GoodUsername1", "GoodPassword1", "GoodEmail1"), retrievedData1);

        userDAO.deleteUsers();

        Assertions.assertNull(userDAO.getUser("GoodUsername"));
        Assertions.assertNull(userDAO.getUser("GoodUsername1"));
    }

}
