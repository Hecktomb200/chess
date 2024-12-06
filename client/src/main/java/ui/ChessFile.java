package ui;

import java.util.HashMap;
import java.util.Map;

public class ChessFile {
  private static final Map<String, Integer> lettersToNumbers = new HashMap<>();

  static {
    lettersToNumbers.put("a", 1);
    lettersToNumbers.put("b", 2);
    lettersToNumbers.put("c", 3);
    lettersToNumbers.put("d", 4);
    lettersToNumbers.put("e", 5);
    lettersToNumbers.put("f", 6);
    lettersToNumbers.put("g", 7);
    lettersToNumbers.put("h", 8);
  }

  public static Integer letterToNumber(String letter) {
    return lettersToNumbers.get(letter.toLowerCase());
  }
}