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
 * A phonetic encoding algorithm that takes an English word and computes a phonetic version of it. This
 * allows for phonetic matches in a spell checker. This class is a port of the C++ DoubleMetaphone() class,
 * which was intended to return two possible phonetic translations for certain words, although the Java version
 * only seems to be concerned with one, making the "double" part erroneous.
 * <br>
 * source code for the original C++ can be found
 * here: <a href="http://aspell.sourceforge.net/metaphone/">http://aspell.sourceforge.net/metaphone/</a>
 * DoubleMetaphone does some processing, such as uppercasing, on the input string first to normalize it. Then, to
 * create the key, the function traverses the input string in a while loop, sending successive characters into a giant
 * switch statement. Before determining the appropriate pronunciation, the algorithm considers the context
 * surrounding each character within the input string.
 * <p>
 * Things that were changed:
 *   <br/>The alternate flag could be set to true but was never checked so why bother with it. REMOVED
 *   <br/>Why was this class serializable?
 *   <br/>The primary, in, length and last variables could be initialized and local to the
 *   process method and references passed around the appropriate methods. As such there are
 *   no class variables and this class becomes firstly threadsafe and secondly could be static final.
 *   <br/>The function call SlavoGermaic was called repeatedly in the process function, it is now only called once.
 *
 */
// robert: Use StringBuilder instead of StringBuffer in transform()
public class DoubleMeta implements Transformator {

  /**
   * Used in the getSuggestions method.
   * All of the letters in the misspelled word are replaced with the characters from
   * this list to try and generate more suggestions, which implies l*n tries,
   * if l is the size of the string, and n is the size of this list.
   * <p>
   * In addition to that, each of these letters is added to the misspelled word.
   */
  private static char[] replaceList = {'A', 'B', 'X', 'S', 'K', 'J', 'T', 'F', 'H', 'L', 'M', 'N', 'P', 'R', '0'};


  private static final String[] MY_LIST = {"GN", "KN", "PN", "WR", "PS", ""};
  private static final String[] LIST_1 = {"ACH", ""};
  private static final String[] LIST_2 = {"BACHER", "MACHER", ""};
  private static final String[] LIST_3 = {"CAESAR", ""};
  private static final String[] LIST_4 = {"CHIA", ""};
  private static final String[] LIST_5 = {"CH", ""};
  private static final String[] LIST_6 = {"CHAE", ""};
  private static final String[] LIST_7 = {"HARAC", "HARIS", ""};
  private static final String[] LIST_8 = {"HOR", "HYM", "HIA", "HEM", ""};
  private static final String[] LIST_9 = {"CHORE", ""};
  private static final String[] LIST_10 = {"VAN ", "VON ", ""};
  private static final String[] LIST_11 = {"SCH", ""};
  private static final String[] LIST_12 = {"ORCHES", "ARCHIT", "ORCHID", ""};
  private static final String[] LIST_13 = {"T", "S", ""};
  private static final String[] LIST_14 = {"A", "O", "U", "E", ""};
  private static final String[] LIST_15 = {"L", "R", "N", "M", "B", "H", "F", "V", "W", " ", ""};
  private static final String[] LIST_16 = {"MC", ""};
  private static final String[] LIST_17 = {"CZ", ""};
  private static final String[] LIST_18 = {"WICZ", ""};
  private static final String[] LIST_19 = {"CIA", ""};
  private static final String[] LIST_20 = {"CC", ""};
  private static final String[] LIST_21 = {"I", "E", "H", ""};
  private static final String[] LIST_22 = {"HU", ""};
  private static final String[] LIST_23 = {"UCCEE", "UCCES", ""};
  private static final String[] LIST_24 = {"CK", "CG", "CQ", ""};
  private static final String[] LIST_25 = {"CI", "CE", "CY", ""};
  // LIST_26 was removed at some point in Jazzy's history
  private static final String[] LIST_27 = {" C", " Q", " G", ""};
  private static final String[] LIST_28 = {"C", "K", "Q", ""};
  private static final String[] LIST_29 = {"CE", "CI", ""};
  private static final String[] LIST_30 = {"DG", ""};
  private static final String[] LIST_31 = {"I", "E", "Y", ""};
  private static final String[] LIST_32 = {"DT", "DD", ""};
  private static final String[] LIST_33 = {"B", "H", "D", ""};
  private static final String[] LIST_34 = {"B", "H", "D", ""};
  private static final String[] LIST_35 = {"B", "H", ""};
  private static final String[] LIST_36 = {"C", "G", "L", "R", "T", ""};
  private static final String[] LIST_37 = {"EY", ""};
  private static final String[] LIST_38 = {"LI", ""};
  private static final String[] LIST_39 = {"ES", "EP", "EB", "EL", "EY", "IB", "IL", "IN", "IE", "EI", "ER", ""};
  private static final String[] LIST_40 = {"ER", ""};
  private static final String[] LIST_41 = {"DANGER", "RANGER", "MANGER", ""};
  private static final String[] LIST_42 = {"E", "I", ""};
  private static final String[] LIST_43 = {"RGY", "OGY", ""};
  private static final String[] LIST_44 = {"E", "I", "Y", ""};
  private static final String[] LIST_45 = {"AGGI", "OGGI", ""};
  private static final String[] LIST_46 = {"VAN ", "VON ", ""};
  private static final String[] LIST_47 = {"SCH", ""};
  private static final String[] LIST_48 = {"ET", ""};
  // LIST_49 was removed at some point in Jazzy's history
  private static final String[] LIST_50 = {"JOSE", ""};
  private static final String[] LIST_51 = {"SAN ", ""};
  private static final String[] LIST_52 = {"SAN ", ""};
  private static final String[] LIST_53 = {"JOSE", ""};
  private static final String[] LIST_54 = {"L", "T", "K", "S", "N", "M", "B", "Z", ""};
  private static final String[] LIST_55 = {"S", "K", "L", ""};
  private static final String[] LIST_56 = {"ILLO", "ILLA", "ALLE", ""};
  private static final String[] LIST_57 = {"AS", "OS", ""};
  private static final String[] LIST_58 = {"A", "O", ""};
  private static final String[] LIST_59 = {"ALLE", ""};
  private static final String[] LIST_60 = {"UMB", ""};
  private static final String[] LIST_61 = {"ER", ""};
  private static final String[] LIST_62 = {"P", "B", ""};
  private static final String[] LIST_63 = {"IE", ""};
  private static final String[] LIST_64 = {"ME", "MA", ""};
  private static final String[] LIST_65 = {"ISL", "YSL", ""};
  private static final String[] LIST_66 = {"SUGAR", ""};
  private static final String[] LIST_67 = {"SH", ""};
  private static final String[] LIST_68 = {"HEIM", "HOEK", "HOLM", "HOLZ", ""};
  private static final String[] LIST_69 = {"SIO", "SIA", ""};
  private static final String[] LIST_70 = {"SIAN", ""};
  private static final String[] LIST_71 = {"M", "N", "L", "W", ""};
  private static final String[] LIST_72 = {"Z", ""};
  private static final String[] LIST_73 = {"Z", ""};
  private static final String[] LIST_74 = {"SC", ""};
  private static final String[] LIST_75 = {"OO", "ER", "EN", "UY", "ED", "EM", ""};
  private static final String[] LIST_76 = {"ER", "EN", ""};
  private static final String[] LIST_77 = {"I", "E", "Y", ""};
  private static final String[] LIST_78 = {"AI", "OI", ""};
  private static final String[] LIST_79 = {"S", "Z", ""};
  private static final String[] LIST_80 = {"TION", ""};
  private static final String[] LIST_81 = {"TIA", "TCH", ""};
  private static final String[] LIST_82 = {"TH", ""};
  private static final String[] LIST_83 = {"TTH", ""};
  private static final String[] LIST_84 = {"OM", "AM", ""};
  private static final String[] LIST_85 = {"VAN ", "VON ", ""};
  private static final String[] LIST_86 = {"SCH", ""};
  private static final String[] LIST_87 = {"T", "D", ""};
  private static final String[] LIST_88 = {"WR", ""};
  private static final String[] LIST_89 = {"WH", ""};
  private static final String[] LIST_90 = {"EWSKI", "EWSKY", "OWSKI", "OWSKY", ""};
  private static final String[] LIST_91 = {"SCH", ""};
  private static final String[] LIST_92 = {"WICZ", "WITZ", ""};
  private static final String[] LIST_93 = {"IAU", "EAU", ""};
  private static final String[] LIST_94 = {"AU", "OU", ""};
  private static final String[] LIST_95 = {"C", "X", ""};

  private static boolean slavoGermanic(String in) {
    return in.contains("W") || in.contains("K") || in.contains("CZ") || in.contains("WITZ");
  }

  private static void metaphAdd(StringBuilder primary, String main) {
    if (main != null) {
      primary.append(main);
    }
  }

  private static void metaphAdd(StringBuilder primary, char main) {
    primary.append(main);
  }

  private static boolean isVowel(String in, int at, int length) {
    if (at < 0 || at >= length)
      return false;
    char it = in.charAt(at);
    return it == 'A' || it == 'E' || it == 'I' || it == 'O' || it == 'U' || it == 'Y';
  }

  private static boolean stringAt(String string, int start, int length, String[] list) {
    if ((start < 0) || (start >= string.length()) || list.length == 0)
      return false;
    String substr = string.substring(start, start + length);
      for (String s : list) {
          if (s.equals(substr))
              return true;
      }
    return false;
  }

  /**
   * Take the given word, and return the best phonetic hash for it.
   * Vowels are minimized as much as possible, and consonants
   * that have similar sounds are converted to the same consonant
   * for example, 'v' and 'f' are both converted to 'f'
   *
   * @param word the text to transform
   * @return the result of the phonetic transformation
   */
  @Override
public final String transform(String word) {
	StringBuilder primary = new StringBuilder(word.length() + 5);
    String in = word.toUpperCase() + "     ";
    int current = 0;
    int length = in.length();
    if (length < 1)
      return "";
    int last = length - 1;
    boolean isSlavoGermaic = slavoGermanic(in);
    if (stringAt(in, 0, 2, MY_LIST))
      current += 1;
    if (in.charAt(0) == 'X') {
      metaphAdd(primary, 'S');
      current += 1;
    }
    while (current < length) {
      switch (in.charAt(current)) {
        case 'A':
        case 'E':
        case 'I':
        case 'O':
        case 'U':
        case 'Y':
          if (current == 0)
            metaphAdd(primary, 'A');
          current += 1;
          break;
        case 'B':
          metaphAdd(primary, 'P');
          if (in.charAt(current + 1) == 'B')
            current += 2;
          else
            current += 1;
          break;
        case '\u00C7':
          metaphAdd(primary, 'S');
          current += 1;
          break;
        case 'C':
          if ((current > 1) && !isVowel(in, current - 2, length) && stringAt(in, (current - 1), 3, LIST_1) &&
                  (in.charAt(current + 2) != 'I') && (in.charAt(current + 2) != 'E') ||
                  stringAt(in, (current - 2), 6, LIST_2)) {
            metaphAdd(primary, 'K');
            current += 2;
            break;
          }
          if ((current == 0) && stringAt(in, current, 6, LIST_3)) {
            metaphAdd(primary, 'S');
            current += 2;
            break;
          }
          if (stringAt(in, current, 4, LIST_4)) {
            metaphAdd(primary, 'K');
            current += 2;
            break;
          }
          if (stringAt(in, current, 2, LIST_5)) {
            if ((current > 0) && stringAt(in, current, 4, LIST_6)) {
              metaphAdd(primary, 'K');
              current += 2;
              break;
            }
            if ((current == 0) && stringAt(in, (current + 1), 5, LIST_7) ||
                    stringAt(in, current + 1, 3, LIST_8) && !stringAt(in, 0, 5, LIST_9)) {
              metaphAdd(primary, 'K');
              current += 2;
              break;
            }
            if (stringAt(in, 0, 4, LIST_10) || stringAt(in, 0, 3, LIST_11) ||
                    stringAt(in, current - 2, 6, LIST_12) ||
                    stringAt(in, current + 2, 1, LIST_13) ||
                    (stringAt(in, current - 1, 1, LIST_14) || (current == 0)) &&
                            stringAt(in, current + 2, 1, LIST_15)) {
              metaphAdd(primary, 'K');
            } else {
              if (current > 0) {
                if (stringAt(in, 0, 2, LIST_16))
                  metaphAdd(primary, 'K');
                else
                  metaphAdd(primary, 'X');
              } else {
                metaphAdd(primary, 'X');
              }
            }
            current += 2;
            break;
          }
          if (stringAt(in, current, 2, LIST_17) && !stringAt(in, current, 4, LIST_18)) {
            metaphAdd(primary, 'S');
            current += 2;
            break;
          }
          if (stringAt(in, current, 3, LIST_19)) {
            metaphAdd(primary, 'X');
            current += 2;
            break;
          }
          if (stringAt(in, current, 2, LIST_20) && !((current == 1) && in.charAt(0) == 'M')) {
            if (stringAt(in, current + 2, 1, LIST_21) && !stringAt(in, current + 2, 2, LIST_22)) {
              if (((current == 1) && (in.charAt(current - 1) == 'A')) || stringAt(in, (current - 1), 5, LIST_23))
                metaphAdd(primary, "KS");
              else
                metaphAdd(primary, 'X');
              current += 3;
              break;
            } else {
              metaphAdd(primary, 'K');
              current += 2;
              break;
            }
          }
          if (stringAt(in, current, 2, LIST_24)) {
            metaphAdd(primary, 'K');
            current += 2;
            break;
          } else if (stringAt(in, current, 2, LIST_25)) {
            metaphAdd(primary, 'S');
            current += 2;
            break;
          }

          metaphAdd(primary, 'K');
          if (stringAt(in, current + 1, 2, LIST_27))
            current += 3;
          else if (stringAt(in, current + 1, 1, LIST_28) && !stringAt(in, current + 1, 2, LIST_29))
            current += 2;
          else
            current += 1;
          break;
        case 'D':
          if (stringAt(in, current, 2, LIST_30)) {
            if (stringAt(in, current + 2, 1, LIST_31)) {
              metaphAdd(primary, 'J');
              current += 3;
              break;
            } else {
              metaphAdd(primary, "TK");
              current += 2;
              break;
            }
          }
          metaphAdd(primary, 'T');
          if (stringAt(in, current, 2, LIST_32)) {
            current += 2;
          } else {
            current += 1;
          }
          break;
        case 'F':
          if (in.charAt(current + 1) == 'F')
            current += 2;
          else
            current += 1;
          metaphAdd(primary, 'F');
          break;
        case 'G':
          if (in.charAt(current + 1) == 'H') {
            if ((current > 0) && !isVowel(in, current - 1, length)) {
              metaphAdd(primary, 'K');
              current += 2;
              break;
            }
            if (current < 3) {
              if (current == 0) {
                if (in.charAt(current + 2) == 'I')
                  metaphAdd(primary, 'J');
                else
                  metaphAdd(primary, 'K');
                current += 2;
                break;
              }
            }
            if ((current > 1) && stringAt(in, current - 2, 1, LIST_33) || ((current > 2) &&
                    stringAt(in, current - 3, 1, LIST_34)) |
                    ((current > 3) && stringAt(in, current - 4, 1, LIST_35))) {
              current += 2;
              break;
            } else {
              if ((current > 2) && (in.charAt(current - 1) == 'U') && stringAt(in, current - 3, 1, LIST_36)) {
                metaphAdd(primary, 'F');
              } else {
                if ((current > 0) && (in.charAt(current - 1) != 'I'))
                  metaphAdd(primary, 'K');
              }
              current += 2;
              break;
            }
          }
          if (in.charAt(current + 1) == 'N') {
            if ((current == 1) && isVowel(in, 0, length) && !isSlavoGermaic) {
              metaphAdd(primary, "KN");
            } else {
              if (!stringAt(in, current + 2, 2, LIST_37) && (in.charAt(current + 1) != 'Y') && !isSlavoGermaic) {
                metaphAdd(primary, "N");
              } else {
                metaphAdd(primary, "KN");
              }
            }
            current += 2;
            break;
          }
          if (stringAt(in, current + 1, 2, LIST_38) && !isSlavoGermaic) {
            metaphAdd(primary, "KL");
            current += 2;
            break;
          }
          if ((current == 0) && ((in.charAt(current + 1) == 'Y') || stringAt(in, current + 1, 2, LIST_39))) {
            metaphAdd(primary, 'K');
            current += 2;
            break;
          }
          if ((stringAt(in, current + 1, 2, LIST_40) || (in.charAt(current + 1) == 'Y')) &&
                  !stringAt(in, 0, 6, LIST_41) && !stringAt(in, current - 1, 1, LIST_42) &&
                  !stringAt(in, current - 1, 3, LIST_43)) {
            metaphAdd(primary, 'K');
            current += 2;
            break;
          }
          if (stringAt(in, current + 1, 1, LIST_44) || stringAt(in, current - 1, 4, LIST_45)) {
            if (stringAt(in, 0, 4, LIST_46) || stringAt(in, 0, 3, LIST_47) || stringAt(in, current + 1, 2, LIST_48)) {
              metaphAdd(primary, 'K');
            } else {
              metaphAdd(primary, 'J');
            }
            current += 2;
            break;
          }
          if (in.charAt(current + 1) == 'G')
            current += 2;
          else
            current += 1;
          metaphAdd(primary, 'K');
          break;
        case 'H':
          if (((current == 0) || isVowel(in, current - 1, length)) && isVowel(in, current + 1, length)) {
            metaphAdd(primary, 'H');
            current += 2;
          } else {
            current += 1;
          }
          break;
        case 'J':
          if (stringAt(in, current, 4, LIST_50) || stringAt(in, 0, 4, LIST_51)) {
            if ((current == 0) && (in.charAt(current + 4) == ' ') || stringAt(in, 0, 4, LIST_52)) {
              metaphAdd(primary, 'H');
            } else {
              metaphAdd(primary, 'J');
            }
            current += 1;
            break;
          }
          if ((current == 0) && !stringAt(in, current, 4, LIST_53)) {
            metaphAdd(primary, 'J');
          } else {
            if (isVowel(in, current - 1, length) && !isSlavoGermaic && ((in.charAt(current + 1) == 'A') ||
                    in.charAt(current + 1) == 'O')) {
              metaphAdd(primary, 'J');
            } else {
              if (current == last) {
                metaphAdd(primary, 'J');
              } else {
                if (!stringAt(in, current + 1, 1, LIST_54) && !stringAt(in, current - 1, 1, LIST_55)) {
                  metaphAdd(primary, 'J');
                }
              }
            }
          }
          if (in.charAt(current + 1) == 'J')
            current += 2;
          else
            current += 1;
          break;
        case 'K':
          if (in.charAt(current + 1) == 'K')
            current += 2;
          else
            current += 1;
          metaphAdd(primary, 'K');
          break;
        case 'L':
          if (in.charAt(current + 1) == 'L') {
            if (((current == (length - 3)) && stringAt(in, current - 1, 4, LIST_56)) ||
                    ((stringAt(in, last - 1, 2, LIST_57) || stringAt(in, last, 1, LIST_58)) &&
                            stringAt(in, current - 1, 4, LIST_59))) {
              metaphAdd(primary, 'L');
              current += 2;
              break;
            }
            current += 2;
          } else
            current += 1;
          metaphAdd(primary, 'L');
          break;
        case 'M':
          if ((stringAt(in, current - 1, 3, LIST_60) && (((current + 1) == last) ||
                  stringAt(in, current + 2, 2, LIST_61))) || (in.charAt(current + 1) == 'M'))
            current += 2;
          else
            current += 1;
          metaphAdd(primary, 'M');
          break;
        case 'N':
          if (in.charAt(current + 1) == 'N')
            current += 2;
          else
            current += 1;
          metaphAdd(primary, 'N');
          break;
        case '\u00D1':
          current += 1;
          metaphAdd(primary, 'N');
          break;
        case 'P':
          if (in.charAt(current + 1) == 'N') {
            metaphAdd(primary, 'F');
            current += 2;
            break;
          }
          if (stringAt(in, current + 1, 1, LIST_62))
            current += 2;
          else
            current += 1;
          metaphAdd(primary, 'P');
          break;
        case 'Q':
          if (in.charAt(current + 1) == 'Q')
            current += 2;
          else
            current += 1;
          metaphAdd(primary, 'K');
          break;
        case 'R':
          if ((current == last) && !isSlavoGermaic && stringAt(in, current - 2, 2, LIST_63) &&
                  !stringAt(in, current - 4, 2, LIST_64)) {
            // MetaphAdd(primary, "");
          } else
            metaphAdd(primary, 'R');
          if (in.charAt(current + 1) == 'R')
            current += 2;
          else
            current += 1;
          break;
        case 'S':
          if (stringAt(in, current - 1, 3, LIST_65)) {
            current += 1;
            break;
          }
          if ((current == 0) && stringAt(in, current, 5, LIST_66)) {
            metaphAdd(primary, 'X');
            current += 1;
            break;
          }
          if (stringAt(in, current, 2, LIST_67)) {
            if (stringAt(in, current + 1, 4, LIST_68))
              metaphAdd(primary, 'S');
            else
              metaphAdd(primary, 'X');
            current += 2;
            break;
          }
          if (stringAt(in, current, 3, LIST_69) || stringAt(in, current, 4, LIST_70)) {
            metaphAdd(primary, 'S');
            current += 3;
            break;
          }
          if (((current == 0) && stringAt(in, current + 1, 1, LIST_71)) || stringAt(in, current + 1, 1, LIST_72)) {
            metaphAdd(primary, 'S');
            if (stringAt(in, current + 1, 1, LIST_73))
              current += 2;
            else
              current += 1;
            break;
          }
          if (stringAt(in, current, 2, LIST_74)) {
            if (in.charAt(current + 2) == 'H')
              if (stringAt(in, current + 3, 2, LIST_75)) {
                if (stringAt(in, current + 3, 2, LIST_76)) {
                  metaphAdd(primary, "X");
                } else {
                  metaphAdd(primary, "SK");
                }
                current += 3;
                break;
              } else {
                metaphAdd(primary, 'X');
                current += 3;
                break;
              }
            if (stringAt(in, current + 2, 1, LIST_77)) {
              metaphAdd(primary, 'S');
              current += 3;
              break;
            }
            metaphAdd(primary, "SK");
            current += 3;
            break;
          }
          if ((current == last) && stringAt(in, current - 2, 2, LIST_78)) {
            //MetaphAdd(primary, "");
          } else {
              metaphAdd(primary, 'S');
          }
          if (stringAt(in, current + 1, 1, LIST_79))
            current += 2;
          else
            current += 1;
          break;
        case 'T':
          if (stringAt(in, current, 4, LIST_80)) {
            metaphAdd(primary, 'X');
            current += 3;
            break;
          }
          if (stringAt(in, current, 3, LIST_81)) {
            metaphAdd(primary, 'X');
            current += 3;
            break;
          }
          if (stringAt(in, current, 2, LIST_82) || stringAt(in, current, 3, LIST_83)) {
            if (stringAt(in, (current + 2), 2, LIST_84) || stringAt(in, 0, 4, LIST_85) || stringAt(in, 0, 3, LIST_86)) {
              metaphAdd(primary, 'T');
            } else {
              metaphAdd(primary, '0');
            }
            current += 2;
            break;
          }
          if (stringAt(in, current + 1, 1, LIST_87)) {
            current += 2;
          } else
            current += 1;
          metaphAdd(primary, 'T');
          break;
        case 'V':
          if (in.charAt(current + 1) == 'V')
            current += 2;
          else
            current += 1;
          metaphAdd(primary, 'F');
          break;
        case 'W':
          if (stringAt(in, current, 2, LIST_88)) {
            metaphAdd(primary, 'R');
            current += 2;
            break;
          }
          if ((current == 0) && (isVowel(in, current + 1, length) || stringAt(in, current, 2, LIST_89))) {
            metaphAdd(primary, 'A');
          }
          if (((current == last) && isVowel(in, current - 1, length)) || stringAt(in, current - 1,
                  5, LIST_90) || stringAt(in, 0, 3, LIST_91)) {
            metaphAdd(primary, 'F');
            current += 1;
            break;
          }
          if (stringAt(in, current, 4, LIST_92)) {
            metaphAdd(primary, "TS");
            current += 4;
            break;
          }
          current += 1;
          break;
        case 'X':
          if (!((current == last) && (stringAt(in, current - 3, 3, LIST_93) || stringAt(in, current - 2, 2, LIST_94))))
            metaphAdd(primary, "KS");
          if (stringAt(in, current + 1, 1, LIST_95))
            current += 2;
          else
            current += 1;
          break;
        case 'Z':
          if (in.charAt(current + 1) == 'H') {
            metaphAdd(primary, 'J');
            current += 2;
            break;
          } else {
            metaphAdd(primary, 'S');
          }
          if (in.charAt(current + 1) == 'Z')
            current += 2;
          else
            current += 1;
          break;
        default:
          current += 1;
      }
    }
    return primary.toString();
  }

  @Override
public char[] getReplaceList() {
    return replaceList;
  }
}
