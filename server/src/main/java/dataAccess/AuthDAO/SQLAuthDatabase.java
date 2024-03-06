package dataAccess.AuthDAO;

import model.AuthData;

public class SQLAuthDatabase implements AuthDAO {

    @Override
    public String createAuth(String username) {
        return null;
    }

    @Override
    public AuthData getAuth(String authToken) {
        return null;
    }

    @Override
    public void deleteAuth(String authToken) {

    }

    @Override
    public void deleteAuthTotal() {

    }
}
