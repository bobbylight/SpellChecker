/*
 * This library is distributed under the LGPL.  See the included
 * LICENSE.md file for details.
 */
package org.fife.com.swabunga.spell.event;

/**
 * The outcome of a call to {@link SpellChecker#checkSpelling(WordTokenizer)}.
 * Instances are immutable.
 */
public final class SpellCheckResult {

  private final int errorCount;
  private final boolean cancelled;

  /**
   * Constructor.
   *
   * @param errorCount The number of errors found.
   * @param cancelled Whether the spell check was cancelled by a listener.
   * @throws IllegalArgumentException If {@code errorCount} is negative.
   */
  public SpellCheckResult(int errorCount, boolean cancelled) {
    if (errorCount < 0) {
      throw new IllegalArgumentException("errorCount cannot be negative: " + errorCount);
    }
    this.errorCount = errorCount;
    this.cancelled = cancelled;
  }

  /**
   * Returns the number of errors found. These are the errors found
   * <em>before</em> any corrections are made. If the spell check was
   * cancelled, this is the number of errors found before the cancellation,
   * including the one whose event triggered it.
   *
   * @return The number of errors found.
   */
  public int getErrorCount() {
    return errorCount;
  }

  /**
   * Returns whether the spell check found any errors.
   *
   * @return Whether any errors were found.
   * @see #getErrorCount()
   */
  public boolean hasErrors() {
    return errorCount > 0;
  }

  /**
   * Returns whether the spell check was cancelled by a listener before
   * all words were checked.
   *
   * @return Whether the spell check was cancelled.
   */
  public boolean isCancelled() {
    return cancelled;
  }

  @Override
  public String toString() {
    return "SpellCheckResult[errorCount=" + errorCount + ", cancelled=" + cancelled + "]";
  }
}
