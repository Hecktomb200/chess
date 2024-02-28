package model.listGames;
import model.GameData;

import java.util.ArrayList;
import java.util.Collection;

public record ListGamesResult(Collection<GameData> games) {}
