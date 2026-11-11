package org.fife.com.swabunga.spell.engine;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;

/**
 * Unit tests for {@link EditDistance}.
 */
class EditDistanceTest {

    private Configuration config;
    private EditDistance editDistance;

    @BeforeEach
    void setUp() {
        config = Configuration.getConfiguration();
        editDistance = new EditDistance(config);
    }

    @Test
    void testGetDistance_identicalWords() {
        Assertions.assertEquals(0, editDistance.getDistance("test", "test"));
    }

    @Test
    void testGetDistance_caseInsensitive_firstChar() {
        Assertions.assertEquals(config.getInteger(Configuration.COST_CHANGE_CASE),
                editDistance.getDistance("test", "Test"));
    }

    @Test
    void testGetDistance_caseInsensitive_nonFirstChar() {
        Assertions.assertEquals(config.getInteger(Configuration.COST_CHANGE_CASE),
                editDistance.getDistance("test", "tEst"));
    }

    @Test
    void testGetDistance_differentWords() {
        int expectedDistance = config.getInteger(Configuration.COST_SUBST_CHARS);
        Assertions.assertEquals(expectedDistance, editDistance.getDistance("test", "tent"));
    }

    @Test
    void testGetDistance_swappedCharacters() {
        int expectedDistance = config.getInteger(Configuration.COST_SWAP_CHARS);
        Assertions.assertEquals(expectedDistance, editDistance.getDistance("abcd", "abdc"));
    }

    @Test
    void testGetDistance_insertCharacter() {
        int expectedDistance = config.getInteger(Configuration.COST_INSERT_CHAR);
        Assertions.assertEquals(expectedDistance, editDistance.getDistance("test", "tests"));
    }

    @Test
    void testGetDistance_deleteCharacter() {
        int expectedDistance = config.getInteger(Configuration.COST_REMOVE_CHAR);
        Assertions.assertEquals(expectedDistance, editDistance.getDistance("tests", "test"));
    }

    @Test
    void testGetDistance_insertAndRemoveCostsAreDistinct() {
        // "word" is the original; the distance is the cost of turning it into "similar"
        config.setInteger(Configuration.COST_REMOVE_CHAR, 10);
        config.setInteger(Configuration.COST_INSERT_CHAR, 100);
        EditDistance distance = new EditDistance(config);

        Assertions.assertEquals(10, distance.getDistance("tests", "test")); // remove 's'
        Assertions.assertEquals(100, distance.getDistance("test", "tests")); // insert 's'
        Assertions.assertEquals(10, distance.getDistance("xtest", "test")); // remove at the start
        Assertions.assertEquals(100, distance.getDistance("test", "xtest")); // insert at the start
        Assertions.assertEquals(20, distance.getDistance("ab", "")); // remove everything
        Assertions.assertEquals(200, distance.getDistance("", "ab")); // insert everything
    }

    @Test
    void testGetDistance_usesConfiguredCosts() {
        config.setInteger(Configuration.COST_SUBST_CHARS, 7);
        Assertions.assertEquals(7, new EditDistance(config).getDistance("test", "tent"));
    }

    @Test
    void testGetDistance_costsReadAtConstruction() {
        int expectedDistance = config.getInteger(Configuration.COST_SUBST_CHARS);
        config.setInteger(Configuration.COST_SUBST_CHARS, 7);
        Assertions.assertEquals(expectedDistance, editDistance.getDistance("test", "tent"));
    }

    @Test
    void testGetDistance_matchesFullMatrix() {
        // Distinct costs, so mixing up rows or operations changes the answer
        config.setInteger(Configuration.COST_REMOVE_CHAR, 97);
        config.setInteger(Configuration.COST_INSERT_CHAR, 93);
        config.setInteger(Configuration.COST_SUBST_CHARS, 101);
        config.setInteger(Configuration.COST_SWAP_CHARS, 89);
        config.setInteger(Configuration.COST_CHANGE_CASE, 11);
        editDistance = new EditDistance(config);

        // A small alphabet makes matches, swaps and case changes common
        Random random = new Random(42);
        String alphabet = "abcAB";
        for (int n = 0; n < 20_000; n++) {
            String word = randomWord(random, alphabet);
            String similar = randomWord(random, alphabet);
            Assertions.assertEquals(fullMatrixDistance(word, similar), editDistance.getDistance(word, similar),
                () -> word + " -> " + similar);
        }
    }

    private static String randomWord(Random random, String alphabet) {
        StringBuilder sb = new StringBuilder();
        int length = random.nextInt(8);
        for (int i = 0; i < length; i++) {
            sb.append(alphabet.charAt(random.nextInt(alphabet.length())));
        }
        return sb.toString();
    }

    /**
     * The full-matrix version of {@link EditDistance#getDistance(String, String)},
     * kept as a reference for the three-row version.
     */
    private int fullMatrixDistance(String word, String similar) {
        int deleteCost = config.getInteger(Configuration.COST_REMOVE_CHAR);
        int insertCost = config.getInteger(Configuration.COST_INSERT_CHAR);
        int substituteCost = config.getInteger(Configuration.COST_SUBST_CHARS);
        int swapCost = config.getInteger(Configuration.COST_SWAP_CHARS);
        int changeCaseCost = config.getInteger(Configuration.COST_CHANGE_CASE);
        int aSize = word.length() + 1;
        int bSize = similar.length() + 1;
        int[][] matrix = new int[aSize][bSize];
        for (int i = 1; i < aSize; i++) {
            matrix[i][0] = matrix[i - 1][0] + deleteCost;
        }
        for (int j = 1; j < bSize; j++) {
            matrix[0][j] = matrix[0][j - 1] + insertCost;
        }
        for (int i = 1; i < aSize; i++) {
            char a = word.charAt(i - 1);
            for (int j = 1; j < bSize; j++) {
                char b = similar.charAt(j - 1);
                if (a == b) {
                    matrix[i][j] = matrix[i - 1][j - 1];
                    continue;
                }
                int cost = Math.min(substituteCost + matrix[i - 1][j - 1],
                    Math.min(deleteCost + matrix[i - 1][j], insertCost + matrix[i][j - 1]));
                if (i > 1 && j > 1 && a == similar.charAt(j - 2) && word.charAt(i - 2) == b) {
                    cost = Math.min(cost, swapCost + matrix[i - 2][j - 2]);
                }
                if (Character.toLowerCase(a) == Character.toLowerCase(b)) {
                    cost = Math.min(cost, changeCaseCost + matrix[i - 1][j - 1]);
                }
                matrix[i][j] = cost;
            }
        }
        return matrix[aSize - 1][bSize - 1];
    }
}
