package ui;

import java.util.HashMap;
import java.util.Map;

public class ChessFile {
  private static final Map<String, Integer> LettersToNumbers = new HashMap<>();

  static {
    LettersToNumbers.put("a", 1);
    LettersToNumbers.put("b", 2);
    LettersToNumbers.put("c", 3);
    LettersToNumbers.put("d", 4);
    LettersToNumbers.put("e", 5);
    LettersToNumbers.put("f", 6);
    LettersToNumbers.put("g", 7);
    LettersToNumbers.put("h", 8);
  }

  public static Integer letterToNumber(String letter) {
    return LettersToNumbers.get(letter.toLowerCase());
  }
}