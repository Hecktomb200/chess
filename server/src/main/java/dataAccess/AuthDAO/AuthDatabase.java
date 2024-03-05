package dataAccess.AuthDAO;

import model.AuthData;
import java.util.Collection;

public class AuthDatabase implements SQLAuthDAO{

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
