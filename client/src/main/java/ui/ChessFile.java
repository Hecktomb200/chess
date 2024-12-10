package ui;

import java.util.HashMap;
import java.util.Map;

public class ChessFile {
  private static final Map<String, Integer> LETTERS_TO_NUMBERS = new HashMap<>();

  static {
    LETTERS_TO_NUMBERS.put("a", 1);
    LETTERS_TO_NUMBERS.put("b", 2);
    LETTERS_TO_NUMBERS.put("c", 3);
    LETTERS_TO_NUMBERS.put("d", 4);
    LETTERS_TO_NUMBERS.put("e", 5);
    LETTERS_TO_NUMBERS.put("f", 6);
    LETTERS_TO_NUMBERS.put("g", 7);
    LETTERS_TO_NUMBERS.put("h", 8);
  }

  public static Integer letterToNumber(String letter) {
    return LETTERS_TO_NUMBERS.get(letter.toLowerCase());
  }
}