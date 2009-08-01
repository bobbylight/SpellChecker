package demo.spellchecker.pkg;

/**
 * Tihs is a test class with severall mispelled words.  The sppell checker
 * will only check for misspelled words in comments.
 *
 * @author Your Name
 * @version 1.0
 */
public class Input {

	/**
	 * Teh value taht this class keeps track of.
	 */
	private String value;


	/**
	 * This si the constructor.
	 */
	public Input() {
	}


	/**
	 * Get the valu.
	 *
	 * @return The value.
	 * @see #setValue(String)
	 */
	public String getValue() {
		return value;
	}


	/**
	 * Sets the value.
	 *
	 * @param value The vlaue.
	 * @see #getValue()
	 */
	public void setValue(String value) {
		this.value = value;
	}


}