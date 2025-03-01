/*
Jazzy - a Java library for Spell Checking
Copyright (C) 2001 Mindaugas Idzelis
Full text of license can be found in LICENSE.txt

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
*/
package org.fife.com.swabunga.spell.engine;

/**
 * This class is based on Levenshtein Distance algorithms, and it calculates how similar two words are. If the words
 * are identical, then the distance is 0. The more that the words have in common, the lower the distance value.
 * The distance value is based on how many operations it takes to get from one word to the other. Possible operations
 * are swapping characters, adding a character, deleting a character, and substituting a character.
 * The resulting distance is the sum of these operations weighted by their cost, which can be set in the Configuration
 * object. When there are multiple ways to convert one word into the other, the lowest cost distance is returned.
 * <br/>
 * Another way to think about this: what are the cheapest operations that would have to be done on the "original" word
 * to end up with the "similar" word? Each operation has a cost, and these are added up to get the distance.
 * <br/>
 *
 * @see org.fife.com.swabunga.spell.engine.Configuration#COST_REMOVE_CHAR
 * @see org.fife.com.swabunga.spell.engine.Configuration#COST_INSERT_CHAR
 * @see org.fife.com.swabunga.spell.engine.Configuration#COST_SUBST_CHARS
 * @see org.fife.com.swabunga.spell.engine.Configuration#COST_SWAP_CHARS
 *
 */
public final class EditDistance {

  /**
   * Fetches the spell engine configuration properties.
   */
  public static final Configuration CONFIG = Configuration.getConfiguration();

  /**
   * get the weights for each possible operation.
   */
  private static final int COST_OF_DELETING_SOURCE_CHARACTER = CONFIG.getInteger(Configuration.COST_REMOVE_CHAR);
  private static final int COST_OF_INSERTING_SOURCE_CHARACTER = CONFIG.getInteger(Configuration.COST_INSERT_CHAR);
  private static final int COST_OF_SUBSTITUTING_LETTERS = CONFIG.getInteger(Configuration.COST_SUBST_CHARS);
  private static final int COST_OF_SWAPPING_LETTERS = CONFIG.getInteger(Configuration.COST_SWAP_CHARS);
  private static final int COST_OF_CHANGING_CASE = CONFIG.getInteger(Configuration.COST_CHANGE_CASE);

  private EditDistance() {
      // Private constructor to prevent instantiation.
  }

  /**
   * Evaluates the distance between two words.
   *
   * @param word One word to evaluates
   * @param similar The other word to evaluates
   * @return A number representing how easy or complex it is to transform on
   *         word into a similar one.
   */
  public static int getDistance(String word, String similar) {
  	return getDistance(word,similar,null);
  }

  /**
   * Evaluates the distance between two words.
   *
   * @param word One word to evaluates
   * @param similar The other word to evaluates
   * @param matrix The matrix.
   * @return A number representing how easy or complex it is to transform on
   *         word into a similar one.
   */
  public static int getDistance(String word, String similar, int[][] matrix) {
  	// JMH Again, there is no need to have a global class matrix variable
  	// in this class. I have removed it and made the getDistance static final
  	// DMV: I refactored this method to make it more efficient, more readable, and simpler.
  	// I also fixed a bug with how the distance was being calculated. You could get wrong
  	// distances if you compared ("abc" to "ab") depending on what you had setup your
  	// COST_REMOVE_CHAR and EDIT_INSERTION_COST values to - that is now fixed.
  	// WRS: I added a distance for case comparison, so a misspelling of "i" would be closer to "I" than
  	// to "a".

  	//Allocate memory outside of the loops.
  	int i;
  	int j;
  	int costOfSubst;
  	int costOfSwap;
  	int costOfDelete;
  	int costOfInsertion;
  	int costOfCaseChange;

  	boolean isSwap;
  	char sourceChar = 0;
  	char otherChar = 0;

    int aSize = word.length() + 1;
    int bSize = similar.length() + 1;


    //Only allocate new memory if we need a bigger matrix.
    if (matrix == null || matrix.length < aSize || matrix[0].length < bSize)
    	matrix = new int[aSize][bSize];

    matrix[0][0] = 0;

    for (i = 1; i != aSize; ++i)
      matrix[i][0] = matrix[i - 1][0] + COST_OF_INSERTING_SOURCE_CHARACTER; //initialize the first column

    for (j = 1; j != bSize; ++j)
      matrix[0][j] = matrix[0][j - 1] + COST_OF_DELETING_SOURCE_CHARACTER; //initialize the first row

    for (i = 1; i != aSize; ++i) {
      sourceChar = word.charAt(i-1);
      for (j = 1; j != bSize; ++j) {

        otherChar = similar.charAt(j-1);
        if (sourceChar == otherChar) {
          matrix[i][j] = matrix[i - 1][j - 1]; //no change required, so just carry the current cost up
          continue;
        }

        costOfSubst = COST_OF_SUBSTITUTING_LETTERS + matrix[i - 1][j - 1];
        //if needed, add up the cost of doing a swap
        costOfSwap = Integer.MAX_VALUE;

        isSwap = (i != 1) && (j != 1) && sourceChar == similar.charAt(j - 2) && word.charAt(i - 2) == otherChar;
        if (isSwap)
          costOfSwap = COST_OF_SWAPPING_LETTERS + matrix[i - 2][j - 2];

        costOfDelete = COST_OF_DELETING_SOURCE_CHARACTER + matrix[i][j - 1];
        costOfInsertion = COST_OF_INSERTING_SOURCE_CHARACTER + matrix[i - 1][j];

        costOfCaseChange = Integer.MAX_VALUE;

        if (equalIgnoreCase(sourceChar, otherChar))
          costOfCaseChange = COST_OF_CHANGING_CASE + matrix[i - 1][j - 1];

        matrix[i][j] = minimum(costOfSubst, costOfSwap, costOfDelete, costOfInsertion, costOfCaseChange);
      }
    }

    return matrix[aSize - 1][bSize - 1];
  }

  /**
   * Checks to see if the two characters are equal ignoring case.
   *
   * @param ch1 The first character.
   * @param ch2 The second character.
   * @return Whether they are equal.
   */
  private static boolean equalIgnoreCase(char ch1, char ch2) {
    if (ch1 == ch2) {
    	return true;
    }
    else {
    	return (Character.toLowerCase(ch1) == Character.toLowerCase(ch2));
    }
  }

  private static int minimum(int a, int b, int c, int d, int e) {
    int mi = a;
    if (b < mi)
      mi = b;
    if (c < mi)
      mi = c;
    if (d < mi)
      mi = d;
    if (e < mi)
      mi = e;

    return mi;
  }

  /*
   * For testing edit distances.
   *
   * @param args an array of two strings we want to evaluate their distances.
   * @throws java.lang.Exception when problems occurs during reading args.
   */
  /*
  public static void main(String[] args) throws Exception {
    BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
    //int[][] matrix = new int[0][0];
    while (true) {

      String input1 = stdin.readLine();
      if (input1 == null || input1.isEmpty())
        break;

      String input2 = stdin.readLine();
      if (input2 == null || input2.isEmpty())
        break;

      //System.out.println(EditDistance.getDistance(input1, input2,matrix));
    }
    //System.out.println("done");
  }
  */
}
