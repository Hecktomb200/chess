package dataAccess.GameDAO;

import model.GameData;

import java.util.Collection;

public class GameDatabase implements SQLGameDAO {
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