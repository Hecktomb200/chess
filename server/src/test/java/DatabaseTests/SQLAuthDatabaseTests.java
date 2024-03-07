package DatabaseTests;

import dataAccess.AuthDAO.SQLAuthDatabase;
import dataAccess.AuthDAO.AuthDAO;
import dataAccess.DataAccessException;
import model.AuthData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.UUID;

public class SQLAuthDatabaseTests {

    @Test
    void createAuthSuccess() throws DataAccessException {
        AuthDAO createAuthDAO = new SQLAuthDatabase();

        String authToken = createAuthDAO.createAuth("TestUsername");
        String authToken1 = createAuthDAO.createAuth("TestUsername1");

        AuthData retrievedData = createAuthDAO.getAuth(authToken);
        AuthData retrievedData1 = createAuthDAO.getAuth(authToken1);


        Assertions.assertEquals(new AuthData(authToken, "TestUsername"), retrievedData);
        Assertions.assertEquals(new AuthData(authToken1, "TestUsername1"), retrievedData1);

        createAuthDAO.deleteAuthTotal();
    }

    @Test
    void createAuthFail() throws DataAccessException {
        AuthDAO createAuthDAO = new SQLAuthDatabase();

        Assertions.assertThrows(DataAccessException.class, () -> createAuthDAO.createAuth(null));
    }

    @Test
    void getAuthSuccess() throws DataAccessException {
        AuthDAO getAuthDAO = new SQLAuthDatabase();

        String authToken = getAuthDAO.createAuth("TestUsername");
        String authToken1 = getAuthDAO.createAuth("TestUsername1");

        AuthData retrievedData = getAuthDAO.getAuth(authToken);
        AuthData retrievedData1 = getAuthDAO.getAuth(authToken1);


        Assertions.assertEquals(new AuthData(authToken, "TestUsername"), retrievedData);
        Assertions.assertEquals(new AuthData(authToken1, "TestUsername1"), retrievedData1);

        getAuthDAO.deleteAuthTotal();
    }

    @Test
    void getAuthFail() throws DataAccessException {
        AuthDAO getAuthDAO = new SQLAuthDatabase();

        Assertions.assertNull(getAuthDAO.getAuth(UUID.randomUUID().toString()));
    }

    @Test
    void deleteAuthSuccess() throws DataAccessException {
        AuthDAO deleteAuthDAO = new SQLAuthDatabase();

        String authToken = deleteAuthDAO.createAuth("TestUsername");
        String authToken1 = deleteAuthDAO.createAuth("TestUsername1");

        AuthData retrievedData = deleteAuthDAO.getAuth(authToken);
        AuthData retrievedData1 = deleteAuthDAO.getAuth(authToken1);


        Assertions.assertEquals(new AuthData(authToken, "TestUsername"), retrievedData);
        Assertions.assertEquals(new AuthData(authToken1, "TestUsername1"), retrievedData1);

        deleteAuthDAO.deleteAuth(authToken);

        Assertions.assertNull(deleteAuthDAO.getAuth(authToken));
        Assertions.assertEquals(new AuthData(authToken1, "TestUsername1"), retrievedData1);
    }

    @Test
    void deleteAllAuthSuccess() throws DataAccessException {
        AuthDAO deleteAllAuthDAO = new SQLAuthDatabase();

        String authToken = deleteAllAuthDAO.createAuth("TestUsername");
        String authToken1 = deleteAllAuthDAO.createAuth("TestUsername1");

        AuthData retrievedData = deleteAllAuthDAO.getAuth(authToken);
        AuthData retrievedData1 = deleteAllAuthDAO.getAuth(authToken1);


        Assertions.assertEquals(new AuthData(authToken, "TestUsername"), retrievedData);
        Assertions.assertEquals(new AuthData(authToken1, "TestUsername1"), retrievedData1);

        deleteAllAuthDAO.deleteAuthTotal();

        Assertions.assertNull(deleteAllAuthDAO.getAuth(authToken));
        Assertions.assertNull(deleteAllAuthDAO.getAuth(authToken1));
    }

}