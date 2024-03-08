package dataAccessTests;

import dataAccess.AuthDAO.SQLAuthDatabase;
import dataAccess.AuthDAO.AuthDAO;
import dataAccess.DataAccessException;
import model.AuthData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.UUID;

public class SQLAuthDatabaseTests {

    @Test
    void createAuthTestPositive() throws DataAccessException {
        AuthDAO authDAO = new SQLAuthDatabase();

        String authToken = authDAO.createAuth("GoodUsername");
        String authToken1 = authDAO.createAuth("GoodUsername1");

        AuthData retrievedData = authDAO.getAuth(authToken);
        AuthData retrievedData1 = authDAO.getAuth(authToken1);


        Assertions.assertEquals(new AuthData(authToken, "GoodUsername"), retrievedData);
        Assertions.assertEquals(new AuthData(authToken1, "GoodUsername1"), retrievedData1);

        authDAO.deleteAuthTotal();
    }

    @Test
    void createAuthTestNegative() throws DataAccessException {
        AuthDAO authDAO = new SQLAuthDatabase();

        Assertions.assertThrows(DataAccessException.class, () -> authDAO.createAuth(null));
    }

    @Test
    void getAuthTestPositive() throws DataAccessException {
        AuthDAO authDAO = new SQLAuthDatabase();

        String authToken = authDAO.createAuth("GoodUsername");
        String authToken1 = authDAO.createAuth("GoodUsername1");

        AuthData retrievedData = authDAO.getAuth(authToken);
        AuthData retrievedData1 = authDAO.getAuth(authToken1);


        Assertions.assertEquals(new AuthData(authToken, "GoodUsername"), retrievedData);
        Assertions.assertEquals(new AuthData(authToken1, "GoodUsername1"), retrievedData1);

        authDAO.deleteAuthTotal();
    }

    @Test
    void getAuthTestNegative() throws DataAccessException {
        AuthDAO authDAO = new SQLAuthDatabase();

        Assertions.assertNull(authDAO.getAuth(UUID.randomUUID().toString()));
    }

    @Test
    void deleteAuthTestPositive() throws DataAccessException {
        AuthDAO authDAO = new SQLAuthDatabase();

        String authToken = authDAO.createAuth("GoodUsername");
        String authToken1 = authDAO.createAuth("GoodUsername1");

        AuthData retrievedData = authDAO.getAuth(authToken);
        AuthData retrievedData1 = authDAO.getAuth(authToken1);


        Assertions.assertEquals(new AuthData(authToken, "GoodUsername"), retrievedData);
        Assertions.assertEquals(new AuthData(authToken1, "GoodUsername1"), retrievedData1);

        authDAO.deleteAuth(authToken);

        Assertions.assertNull(authDAO.getAuth(authToken));
        Assertions.assertEquals(new AuthData(authToken1, "GoodUsername1"), retrievedData1);
    }

    @Test
    void deleteAuthTotalTestNegative() throws DataAccessException {
        AuthDAO authDAO = new SQLAuthDatabase();

        String authToken = authDAO.createAuth("GoodUsername");
        String authToken1 = authDAO.createAuth("GoodUsername1");

        AuthData retrievedData = authDAO.getAuth(authToken);
        AuthData retrievedData1 = authDAO.getAuth(authToken1);


        Assertions.assertEquals(new AuthData(authToken, "GoodUsername"), retrievedData);
        Assertions.assertEquals(new AuthData(authToken1, "GoodUsername1"), retrievedData1);

        authDAO.deleteAuthTotal();

        Assertions.assertNull(authDAO.getAuth(authToken));
        Assertions.assertNull(authDAO.getAuth(authToken1));
    }

}