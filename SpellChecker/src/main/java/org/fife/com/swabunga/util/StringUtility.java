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
package org.fife.com.swabunga.util;

/**
 * String utility methods.
 */
public final class StringUtility {

  private StringUtility() {
    // Private constructor to prevent instantiation.
  }

  /**
   * Replaces a region in a string builder with new text. Example:
   * <code>
   *   StringBuilder sb = new StringBuilder("Hello world");
   *   String replaceWith = "foobar";
   *   println(replace(buf, 3, 5, replaceWith));
   * </code>
   * would print {@code "Helfoobar world"}.
   *
   * @param sb The buffer to update.
   * @param start The offset of the first character to replace, inclusive.
   * @param end The offset of the end character to replace, exclusive.
   * @param replaceWith The text to insert.
   * @return The string builder.
   */
  public static StringBuilder replace(StringBuilder sb, int start, int end, String replaceWith) {
    // robert: Use StringBuilder
    int len = replaceWith.length();
    char[] ch = new char[sb.length() + len - (end - start)];
    sb.getChars(0, start, ch, 0);
    replaceWith.getChars(0, len, ch, start);
    sb.getChars(end, sb.length(), ch, start + len);
    sb.setLength(0);
    sb.append(ch);
    return sb;
  }
}
