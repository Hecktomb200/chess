package dataAccess.GameDAO;

import model.GameData;
import java.util.Collection;

public class SQLGameDatabase implements GameDAO {
    @Override
    public Integer createGame(String gameName) {
        return null;
    }

    @Override
    public void updateGame(GameData game) {

    }

    @Override
    public void deleteGames() {

    }

    @Override
    public GameData getGame(int gameID) {
        return null;
    }

    @Override
    public Collection<GameData> listGames() {
        return null;
    }
}