SpellChecker is a spell check add-on for RSyntaxTextArea.  For programming languages, it spell-checks text in comments, and when editing plain text files, the entire file is spell-checked.  Spelling errors are squiggle-underlined in the color of your choosing, and hovering the mouse over a misspelled word displays a tool tip with suggested fixes (if any).  You can configure the library to also use a "user dictionary" file, allowing the user to add extra words to the spell check white list.

This add-on is based on [Jazzy](http://jazzy.sourceforge.net), a Java spell checker.  Indeed, 99% of the code is just Jazzy, ever-so-slightly modified for performance and bug fixes.

Included with this distribution is an English dictionary (both American and
British).  The easiest method to add spell checking to RSTA is as follows:

```java
import org.fife.ui.rsyntaxtextarea.spell.*;
// ...
File zip = new File("location/of/included/english_dic.zip");
boolean american = true; // "false" will use British English
SpellingParser parser = SpellingParser.
createEnglishSpellingParser(zip, american);
textArea.addParser(parser);
```

See class org.fife.ui.rsyntaxtextarea.spell.demo.SpellingParserDemo for a working example.  

Just like Jazzy itself, this add-on is licensed under the LGPL; see the included
[SpellChecker.License.txt](https://github.com/bobbylight/SpellChecker/blob/master/distfiles/SpellChecker.License.txt) file.

# Sister Projects

* [RSyntaxTextArea](https://github.com/bobbylight/RSyntaxTextArea) provides syntax highlighting, code folding, and many other features out-of-the-box.
* [AutoComplete](https://github.com/bobbylight/AutoComplete) - Adds code completion to RSyntaxTextArea (or any other JTextComponent).
* [RSTALanguageSupport](https://github.com/bobbylight/RSTALanguageSupport) - Code completion for RSTA for the following languages: Java, JavaScript, HTML, PHP, JSP, Perl, C, Unix Shell.  Built on both RSTA and AutoComplete.
* [RSTAUI](https://github.com/bobbylight/RSTAUI) - Common dialogs needed by text editing applications: Find, Replace, Go to Line, File Properties.

