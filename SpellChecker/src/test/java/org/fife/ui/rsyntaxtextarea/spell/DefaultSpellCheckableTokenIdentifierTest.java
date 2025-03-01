package org.fife.ui.rsyntaxtextarea.spell;

import org.fife.ui.rsyntaxtextarea.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link DefaultSpellCheckableTokenIdentifier}.
 */
public class DefaultSpellCheckableTokenIdentifierTest {

    private DefaultSpellCheckableTokenIdentifier identifier;

    private static Token createToken(String text, int type) {
        char[] chars = text.toCharArray();
        return new TokenImpl(chars, 0, chars.length, 0, type, 0);
    }

    @BeforeEach
    void setUp() {
        identifier = new DefaultSpellCheckableTokenIdentifier();
    }

    @Test
    void testBegin() {
        identifier.begin();
        // No state to verify, just ensure no exceptions are thrown
    }

    @Test
    void testEnd() {
        identifier.end();
        // No state to verify, just ensure no exceptions are thrown
    }

    @Test
    void testIsSpellCheckable_docComment() {
        Token token = createToken("/** foo */", TokenTypes.COMMENT_DOCUMENTATION);
        assertTrue(identifier.isSpellCheckable(token));
    }

    @Test
    void testIsSpellCheckable_eolComment() {
        Token token = createToken("// hello world", TokenTypes.COMMENT_EOL);
        assertTrue(identifier.isSpellCheckable(token));
    }

    @Test
    void testIsSpellCheckable_multiLineComment() {
        Token token = createToken("/* foo */", TokenTypes.COMMENT_MULTILINE);
        assertTrue(identifier.isSpellCheckable(token));
    }

    @Test
    void testIsSpellCheckable_commentMarkup() {
        Token token = createToken("<pre>", TokenTypes.COMMENT_MARKUP);
        assertFalse(identifier.isSpellCheckable(token));
    }

    @Test
    void testIsSpellCheckable_nonCommentToken() {
        Token token = createToken("foo", TokenTypes.IDENTIFIER);
        assertFalse(identifier.isSpellCheckable(token));
    }
}
