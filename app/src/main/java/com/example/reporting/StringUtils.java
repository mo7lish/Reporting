package com.example.reporting;

import java.util.Arrays;
import java.util.stream.Collectors;

public class StringUtils {
    public static String capitalizeWords(String input) {
        if (input == null || input.length() == 0) {
            return input;
        }
        
        String[] words = input.trim().split("\\s+");
        StringBuilder result = new StringBuilder();
        
        for (int i = 0; i < words.length; i++) {
            if (words[i].length() > 0) {
                if (i > 0) {
                    result.append(" ");
                }
                result.append(Character.toUpperCase(words[i].charAt(0)))
                      .append(words[i].length() > 1 ? words[i].substring(1).toLowerCase() : "");
            }
        }
        
        return result.toString();
    }
}