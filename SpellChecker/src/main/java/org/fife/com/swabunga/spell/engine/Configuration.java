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

import java.lang.reflect.InvocationTargetException;

/**
 * The various settings used to control how a spell checker works are read from here.
 * Includes the COST_* constants that decide how to figure the cost of converting one word to
 * another in the EditDistance class.
 * <p/>
 * Also includes SPELL_* constants that control how misspellings are detected, for example, how to handle
 * mixed-case words, etc.
 *
 * @author aim4min
 * @see EditDistance
 */
public abstract class Configuration {

  /**
   * System property that, if defined, should be a fully qualified class name of a configuration.
   */
  public static final String PROPERTY_CONFIG_OVERRIDE = "jazzy.config";

  /** used by EditDistance: the cost of having to remove a character <br/>(integer greater than 0). */
  public static final String COST_REMOVE_CHAR = "EDIT_DEL1";

  /** used by EditDistance: the cost of having to insert a character <br/>(integer greater than 0).*/
  public static final String COST_INSERT_CHAR = "EDIT_DEL2";

  /**
   * used by EditDistance: the cost of having to swap two adjoining characters
   * for the swap value to ever be used, it should be smaller than the COST_REMOVE_CHAR or COST_INSERT_CHAR values.
   * <br/>(integer greater than 0)
   */
  public static final String COST_SWAP_CHARS = "EDIT_SWAP";

  /**
   * used by EditDistance: the cost of having to change case, for example, from i to I.
   * <br/>(integer greater than 0)
   */
  public static final String COST_CHANGE_CASE = "EDIT_CASE";

  /**
   * Used by EditDistance: the cost of having to substitute one character for another
   * for the sub value to ever be used, it should be smaller than the COST_REMOVE_CHAR or COST_INSERT_CHAR values.
   * (integer greater than 0)
   */
  public static final String COST_SUBST_CHARS = "EDIT_SUB";

  /**
   * The maximum cost of suggested spelling. Any suggestions that cost more are thrown away
   * integer greater than 1).
   */
  public static final String SPELL_THRESHOLD = "SPELL_THRESHOLD";

  /** words that are all upper case are not spell checked, example: "CIA". <br/>(boolean) */
  public static final String SPELL_IGNOREUPPERCASE = "SPELL_IGNOREUPPERCASE";
  /**  words that have mixed case are not spell checked, example: "SpellChecker".<br/>(boolean) */
  public static final String SPELL_IGNOREMIXEDCASE = "SPELL_IGNOREMIXEDCASE";
  /** words that look like an Internet address are not spell checked, example: "http://www.google.com". <br/>(boolean)*/
  public static final String SPELL_IGNOREINTERNETADDRESSES = "SPELL_IGNOREINTERNETADDRESS";
  /** words that have digits in them are not spell checked, example: "mach5". <br/>(boolean) */
  public static final String SPELL_IGNOREDIGITWORDS = "SPELL_IGNOREDIGITWORDS";
  /** I don't know what this does. It doesn't seem to be used. <br/>(boolean) */
  public static final String SPELL_IGNOREMULTIPLEWORDS = "SPELL_IGNOREMULTIPLEWORDS";
  /** the first word of a sentence is expected to start with an upper case letter <br/>(boolean). */
  public static final String SPELL_IGNORESENTENCECAPITALIZATION = "SPELL_IGNORESENTENCECAPTILIZATION";

  /** Whether to ignore words that are a single letter (common in programming). */
  public static final String SPELL_IGNORESINGLELETTERS = "SPELL_IGNORESINGLELETTERS";

  /** Whether to inspect {@code camelCase} words (common in programming). */
  public static final String SPELL_ANALYZECAMELCASEWORDS = "SPELL_ANALYZECAMELCASEWORDS";

  /**
   * Gets one of the integer constants.
   *
   * @param key one of the integer constants defined in this class
   * @return int value of the setting
   */
  public abstract int getInteger(String key);

  /**
   * Gets one of the boolean constants.
   *
   * @param key one of the boolean constants defined in this class
   * @return boolean value of the setting
   */
  public abstract boolean getBoolean(String key);

  /**
   * Sets one of the integer constants. Does not persist the change.
   *
   * @param key one of the integer constants defined in this class
   * @param value new integer value of the constant.
   */
  public abstract void setInteger(String key, int value);

  /**
   * Sets one of the boolean constants. Does not persist the change.
   *
   * @param key one of the boolean constants defined in this class
   * @param value new boolean value of this setting.
   */
  public abstract void setBoolean(String key, boolean value);

  /**
   * Gets a new default Configuration.
   *
   * @return Configuration
   */
  public static Configuration getConfiguration() {
    String config = System.getProperty(PROPERTY_CONFIG_OVERRIDE); // added by bd
    if (config != null && !config.isEmpty())
        return getConfiguration(config);
    return getConfiguration(null);
  }

  /**
   * Returns a new instance of a Configuration class.
   *
   * @param className - the class to return, must be based on Configuration
   * @return The configuration.
   */
  public static Configuration getConfiguration(String className) {

    Configuration result;

    if (className != null && !className.isEmpty()) {
      try {
        result = (Configuration) Class.forName(className).getDeclaredConstructor().newInstance();
      } catch (InstantiationException | InvocationTargetException | IllegalAccessException |
               ClassNotFoundException | NoSuchMethodException e) {
        result = new PropertyConfiguration();
      }
    } else {
      result = new PropertyConfiguration();
    }
    return result;
  }
}
