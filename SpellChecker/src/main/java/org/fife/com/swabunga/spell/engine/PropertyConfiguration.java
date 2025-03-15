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

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Properties;


/**
 * Implementation class to read the properties controlling the spell engine.
 * The properties are read form the <code>configuration.properties</code> file.
 *
 * @author aim4min
 */
public class PropertyConfiguration extends Configuration {

  /**
   * The persistent set of properties supported by the spell engine.
   */
  private Properties prop;

  private static final String DEFAULT_PROPERTIES = "org/fife/com/swabunga/spell/engine/configuration.properties";

  private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

  /**
   * Constructs and loads spell engine properties configuration.
   */
  public PropertyConfiguration() {
      this(DEFAULT_PROPERTIES);
  }

  public PropertyConfiguration(String resource) {
    prop = new Properties();
    InputStream in = getClass().getClassLoader().getResourceAsStream(resource);
    if (in != null) {
        try (BufferedReader r = new BufferedReader(new InputStreamReader(in, DEFAULT_CHARSET))) {
            prop.load(r);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
  }

  @Override
  public boolean getBoolean(String key) {
	  return Boolean.parseBoolean(prop.getProperty(key));
  }

  @Override
  public int getInteger(String key) {
	  return Integer.parseInt(prop.getProperty(key), 10);
  }

  @Override
  public void setBoolean(String key, boolean value) {
    prop.setProperty(key, String.valueOf(value));
  }

  @Override
  public void setInteger(String key, int value) {
    prop.setProperty(key, Integer.toString(value));
  }
}
