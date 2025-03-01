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

import java.util.List;


/**
 * Utility methods for lists.
 */
public final class VectorUtility {

    /**
     * Private constructor to prevent instantiation.
     */
    private VectorUtility() {
        // Do nothing
    }

    /**
     * Adds all, allowing duplicates.
     *
     * @param <T>  The type of data in the lists.
     * @param dest The destination list.
     * @param src  The source list.
     * @return The destination list.
     */
    public static <T> List<T> addAll(List<T> dest, List<T> src) {
        return addAll(dest, src, true);
    }

    /**
     * Adds all entries from one list to another.
     *
     * @param <T>             The type of data in the lists.
     * @param dest            The destination list.
     * @param src             The source list.
     * @param allowDuplicates Whether to allow duplicates.
     * @return The destination list.
     */
    public static <T> List<T> addAll(List<T> dest, List<T> src, boolean allowDuplicates) {
        for (T value : src) {
            if (allowDuplicates || !dest.contains(value))
                dest.add(value);
        }
        return dest;
    }
}
