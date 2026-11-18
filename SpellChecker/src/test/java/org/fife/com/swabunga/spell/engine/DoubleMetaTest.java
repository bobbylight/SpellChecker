package org.fife.com.swabunga.spell.engine;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Locale;

/**
 * Unit tests for {@link DoubleMeta}.
 */
class DoubleMetaTest {

    private DoubleMeta doubleMeta;

    @BeforeEach
    void setUp() {
        doubleMeta = new DoubleMeta();
    }

    @Test
    void testGetCodes_onlyPrimaryWhenSame() {
        Assertions.assertEquals(List.of("TST"), doubleMeta.getCodes("test"));
    }

    @Test
    void testGetCodes_primaryThenAlternate() {
        // "CIA": Italian-style "S" first, English "sh" second
        Assertions.assertEquals(List.of("SPSL", "SPXL"), doubleMeta.getCodes("special"));
        // A silent final French "R" is pronounced in the alternate code
        Assertions.assertEquals(List.of("FR", "FRR"), doubleMeta.getCodes("Fourier"));
    }

    @Test
    void testGetCodes_nullOrBlank() {
        Assertions.assertEquals(List.of(""), doubleMeta.getCodes(null));
        Assertions.assertEquals(List.of(""), doubleMeta.getCodes("  "));
    }

    @Test
    void testCodeChars() {
        char[] expected = {'A', 'B', 'X', 'S', 'K', 'J', 'T', 'F', 'H', 'L', 'M', 'N', 'P', 'R', '0'};
        Assertions.assertArrayEquals(expected, DoubleMeta.CODE_CHARS);
    }

    @Test
    void testGetPrimaryCode_null() {
        Assertions.assertEquals("", doubleMeta.getPrimaryCode(null));
    }

    @Test
    void testGetPrimaryCode_emptyString() {
        Assertions.assertEquals("", doubleMeta.getPrimaryCode(""));
    }

    @Test
    void testGetPrimaryCode_blankString() {
        Assertions.assertEquals("", doubleMeta.getPrimaryCode("   "));
    }

    @Test
    void testGetPrimaryCode_codeLongerThanFourCharacters() {
        // Commons Codec limits codes to 4 characters by default
        Assertions.assertEquals("ARTFRK", doubleMeta.getPrimaryCode("aardvark"));
        Assertions.assertEquals("ANFRNMNT", doubleMeta.getPrimaryCode("environment"));
    }

    @Test
    void testGetPrimaryCode_endsWith_frenchR_silent() {
        Assertions.assertEquals("FR", doubleMeta.getPrimaryCode("Fourier"));
    }

    @Test
    void testGetPrimaryCode_endsWith_frenchS_silent() {
        Assertions.assertEquals("ALN", doubleMeta.getPrimaryCode("Illinois"));
    }

    @Test
    void testGetPrimaryCode_endsWith_vowelThenW_silent() {
        // Only the alternate encoding has an "F" here
        Assertions.assertEquals("AL", doubleMeta.getPrimaryCode("allow"));
    }

    @Test
    void testGetPrimaryCode_simpleWord() {
        Assertions.assertEquals("TST", doubleMeta.getPrimaryCode("test"));
    }

    @Test
    void testGetPrimaryCode_caseInsensitive() {
        Assertions.assertEquals("TST", doubleMeta.getPrimaryCode("Test"));
    }

    @Test
    void testGetPrimaryCode_independentOfDefaultLocale() {
        String expected = doubleMeta.getPrimaryCode("item");
        Locale orig = Locale.getDefault();
        try {
            // Turkish uppercases 'i' to a dotted capital I, which isn't a vowel to DoubleMeta
            Locale.setDefault(Locale.of("tr", "TR"));
            Assertions.assertEquals(expected, doubleMeta.getPrimaryCode("item"));
        } finally {
            Locale.setDefault(orig);
        }
    }

    @Test
    void testGetPrimaryCode_endsWith_alle() {
        Assertions.assertEquals("FRFL", doubleMeta.getPrimaryCode("farfalle"));
    }

    @Test
    void testGetPrimaryCode_endsWith_eaux() {
        Assertions.assertEquals("PR", doubleMeta.getPrimaryCode("breaux")); // Final French "X" is silent
    }

    @Test
    void testGetPrimaryCode_endsWith_gh() {
        Assertions.assertEquals("P", doubleMeta.getPrimaryCode("bough"));
    }

    @Test
    void testGetPrimaryCode_endsWith_j_1() {
        Assertions.assertEquals("TJ", doubleMeta.getPrimaryCode("dj"));
    }

    @Test
    void testGetPrimaryCode_endsWith_j_2() {
        Assertions.assertEquals("HJ", doubleMeta.getPrimaryCode("haj"));
    }

    @Test
    void testGetPrimaryCode_endsWith_ough_1() {
        Assertions.assertEquals("TF", doubleMeta.getPrimaryCode("tough"));
    }

    @Test
    void testGetPrimaryCode_endsWith_vowel_then_w() {
        Assertions.assertEquals("M", doubleMeta.getPrimaryCode("maw"));
    }

    @Test
    void testGetPrimaryCode_endsWith_w() {
        Assertions.assertEquals("ANH", doubleMeta.getPrimaryCode("anyhow"));
    }

    @Test
    void testGetPrimaryCode_phoneticMatch() {
        Assertions.assertEquals("F", doubleMeta.getPrimaryCode("ph"));
    }

    @Test
    void testGetPrimaryCode_startsWith_a() {
        Assertions.assertEquals("AT", doubleMeta.getPrimaryCode("at"));
    }

    @Test
    void testGetPrimaryCode_startsWith_agn() {
        Assertions.assertEquals("AKNL", doubleMeta.getPrimaryCode("agnel"));
    }

    @Test
    void testGetPrimaryCode_startsWith_b() {
        Assertions.assertEquals("PT", doubleMeta.getPrimaryCode("bat"));
    }

    @Test
    void testGetPrimaryCode_startsWith_u00c7() {
        Assertions.assertEquals("ST", doubleMeta.getPrimaryCode("\u00c7at"));
    }

    @Test
    void testGetPrimaryCode_startsWith_c() {
        Assertions.assertEquals("KT", doubleMeta.getPrimaryCode("cat"));
    }

    @Test
    void testGetPrimaryCode_startsWith_c_chae() {
        Assertions.assertEquals("X", doubleMeta.getPrimaryCode("chae"));
    }

    @Test
    void testGetPrimaryCode_startsWith_c_equals_casear() {
        Assertions.assertEquals("SSR", doubleMeta.getPrimaryCode("caesar"));
    }

    @Test
    void testGetPrimaryCode_startsWith_charism() {
        Assertions.assertEquals("KRSM", doubleMeta.getPrimaryCode("charisma"));
    }

    @Test
    void testGetPrimaryCode_startsWith_cr_1() {
        Assertions.assertEquals("KRSP", doubleMeta.getPrimaryCode("crisp"));
    }

    @Test
    void testGetPrimaryCode_startsWith_cr_2() {
        Assertions.assertEquals("KR", doubleMeta.getPrimaryCode("cry"));
    }

    @Test
    void testGetPrimaryCode_startsWith_d() {
        Assertions.assertEquals("TRT", doubleMeta.getPrimaryCode("dart"));
    }

    @Test
    void testGetPrimaryCode_startsWith_f() {
        Assertions.assertEquals("FT", doubleMeta.getPrimaryCode("fat"));
    }

    @Test
    void testGetPrimaryCode_startsWith_g() {
        Assertions.assertEquals("KRTN", doubleMeta.getPrimaryCode("garden"));
    }

    @Test
    void testGetPrimaryCode_startsWith_ger() {
        Assertions.assertEquals("KRM", doubleMeta.getPrimaryCode("germ"));
    }

    @Test
    void testGetPrimaryCode_startsWith_ghi() {
        Assertions.assertEquals("JK", doubleMeta.getPrimaryCode("ghig"));
    }

    @Test
    void testGetPrimaryCode_startsWith_ghi_2() {
        Assertions.assertEquals("J", doubleMeta.getPrimaryCode("ghi"));
    }

    @Test
    void testGetPrimaryCode_startsWith_gho() {
        Assertions.assertEquals("KST", doubleMeta.getPrimaryCode("ghost"));
    }

    @Test
    void testGetPrimaryCode_startsWith_gli() {
        Assertions.assertEquals("KLP", doubleMeta.getPrimaryCode("glib"));
    }

    @Test
    void testGetPrimaryCode_startsWith_gy() {
        Assertions.assertEquals("KM", doubleMeta.getPrimaryCode("gym"));
    }

    @Test
    void testGetPrimaryCode_startsWith_h() {
        Assertions.assertEquals("HT", doubleMeta.getPrimaryCode("hat"));
    }

    @Test
    void testGetPrimaryCode_startsWith_j_1() {
        Assertions.assertEquals("JK", doubleMeta.getPrimaryCode("joke"));
    }

    @Test
    void testGetPrimaryCode_startsWith_j_2() {
        Assertions.assertEquals("J", doubleMeta.getPrimaryCode("jaw"));
    }

    @Test
    void testGetPrimaryCode_startsWith_jose_1() {
        Assertions.assertEquals("HS", doubleMeta.getPrimaryCode("jose"));
    }

    @Test
    void testGetPrimaryCode_startsWith_jose_2() {
        Assertions.assertEquals("JSF", doubleMeta.getPrimaryCode("joseph")); // "PH" is "F"
    }

    @Test
    void testGetPrimaryCode_startsWith_k() {
        Assertions.assertEquals("KT", doubleMeta.getPrimaryCode("kite"));
    }

    @Test
    void testGetPrimaryCode_startsWith_lamb() {
        Assertions.assertEquals("LMP", doubleMeta.getPrimaryCode("lamb"));
    }

    @Test
    void testGetPrimaryCode_startsWith_m() {
        Assertions.assertEquals("MT", doubleMeta.getPrimaryCode("mat"));
    }

    @Test
    void testGetPrimaryCode_startsWith_n() {
        Assertions.assertEquals("NP", doubleMeta.getPrimaryCode("nap"));
    }

    @Test
    void testGetPrimaryCode_startsWith_u00D1() {
        Assertions.assertEquals("NP", doubleMeta.getPrimaryCode("\u00d1ap"));
    }

    @Test
    void testGetPrimaryCode_startsWith_p() {
        Assertions.assertEquals("PT", doubleMeta.getPrimaryCode("pat"));
    }

    @Test
    void testGetPrimaryCode_startsWith_q() {
        Assertions.assertEquals("KK", doubleMeta.getPrimaryCode("quick"));
    }

    @Test
    void testGetPrimaryCode_startsWith_r() {
        Assertions.assertEquals("RT", doubleMeta.getPrimaryCode("rat"));
    }

    @Test
    void testGetPrimaryCode_startsWith_s() {
        Assertions.assertEquals("ST", doubleMeta.getPrimaryCode("sat"));
    }

    @Test
    void testGetPrimaryCode_startsWith_sce() {
        Assertions.assertEquals("SN", doubleMeta.getPrimaryCode("scene"));
    }

    @Test
    void testGetPrimaryCode_startsWith_sch() {
        Assertions.assertEquals("SKL", doubleMeta.getPrimaryCode("school"));
    }

    @Test
    void testGetPrimaryCode_startsWith_schen() {
        Assertions.assertEquals("XNK", doubleMeta.getPrimaryCode("schenk"));
    }

    @Test
    void testGetPrimaryCode_startsWith_schoo() {
        Assertions.assertEquals("SKL", doubleMeta.getPrimaryCode("school"));
    }

    @Test
    void testGetPrimaryCode_startsWith_sia() {
        Assertions.assertEquals("SLT", doubleMeta.getPrimaryCode("sialoid"));
    }

    @Test
    void testGetPrimaryCode_startsWith_sm() {
        Assertions.assertEquals("SML", doubleMeta.getPrimaryCode("smell"));
    }

    @Test
    void testGetPrimaryCode_startsWith_sugar() {
        Assertions.assertEquals("XKR", doubleMeta.getPrimaryCode("sugar"));
    }

    @Test
    void testGetPrimaryCode_startsWith_sz() {
        Assertions.assertEquals("SXN", doubleMeta.getPrimaryCode("szechuan"));
    }

    @Test
    void testGetPrimaryCode_startsWith_t() {
        Assertions.assertEquals("TNT", doubleMeta.getPrimaryCode("taint"));
    }

    @Test
    void testGetPrimaryCode_startsWith_v() {
        Assertions.assertEquals("FT", doubleMeta.getPrimaryCode("vat"));
    }

    @Test
    void testGetPrimaryCode_startsWith_w() {
        Assertions.assertEquals("AT", doubleMeta.getPrimaryCode("what"));
    }

    @Test
    void testGetPrimaryCode_startsWith_x() {
        Assertions.assertEquals("SR", doubleMeta.getPrimaryCode("xray"));
    }

    @Test
    void testGetPrimaryCode_startsWith_z() {
        Assertions.assertEquals("ST", doubleMeta.getPrimaryCode("zit"));
    }

    @Test
    void textTransform_startsWith_chi() {
        Assertions.assertEquals("XSLM", doubleMeta.getPrimaryCode("chisholm"));
    }

    @Test
    void textTransform_startsWith_w_then_vowel() {
        Assertions.assertEquals("ART", doubleMeta.getPrimaryCode("ward"));
    }

    @Test
    void textTransform_startsWith_wh() {
        Assertions.assertEquals("AL", doubleMeta.getPrimaryCode("while"));
    }

    @Test
    void testGetPrimaryCode_contains_cr() {
        Assertions.assertEquals("AKR", doubleMeta.getPrimaryCode("acre"));
    }

    @Test
    void testGetPrimaryCode_contains_agn() {
        Assertions.assertEquals("MNT", doubleMeta.getPrimaryCode("magnet"));
    }

    @Test
    void testGetPrimaryCode_contains_alle() {
        Assertions.assertEquals("KLR", doubleMeta.getPrimaryCode("caller"));
    }

    @Test
    void testGetPrimaryCode_contains_aux() {
        Assertions.assertEquals("A", doubleMeta.getPrimaryCode("aux")); // Final French "X" is silent
    }

    @Test
    void testGetPrimaryCode_contains_bb() {
        Assertions.assertEquals("TPL", doubleMeta.getPrimaryCode("dabble"));
    }

    @Test
    void testGetPrimaryCode_contains_cc() {
        Assertions.assertEquals("AKSNT", doubleMeta.getPrimaryCode("accent"));
    }

    @Test
    void testGetPrimaryCode_contains_cc_2() {
        Assertions.assertEquals("AXNTRK", doubleMeta.getPrimaryCode("eccentric"));
    }

    @Test
    void testGetPrimaryCode_contains_cc_3() {
        Assertions.assertEquals("AKMPN", doubleMeta.getPrimaryCode("accompany"));
    }

    @Test
    void testGetPrimaryCode_contains_cc_4() {
        Assertions.assertEquals("AKLT", doubleMeta.getPrimaryCode("occult"));
    }

    @Test
    void testGetPrimaryCode_contains_chae() {
        Assertions.assertEquals("RKL", doubleMeta.getPrimaryCode("rachael"));
    }

    @Test
    void testGetPrimaryCode_contains_chae_2() {
        Assertions.assertEquals("AXM", doubleMeta.getPrimaryCode("ischaemia"));
    }

    @Test
    void testGetPrimaryCode_contains_chae_3() {
        Assertions.assertEquals("ARKNS", doubleMeta.getPrimaryCode("archaeans"));
    }

    @Test
    void testGetPrimaryCode_contains_chia() {
        Assertions.assertEquals("K", doubleMeta.getPrimaryCode("chiao"));
    }

    @Test
    void testGetPrimaryCode_contains_cia() {
        Assertions.assertEquals("ALPS", doubleMeta.getPrimaryCode("alopecia"));
    }

    @Test
    void testGetPrimaryCode_contains_cia_2() {
        Assertions.assertEquals("PNFSL", doubleMeta.getPrimaryCode("beneficial")); // "CIA" is "S" (only "CCIA" is "X")
    }

    @Test
    void testGetPrimaryCode_contains_cia_3() {
        Assertions.assertEquals("S", doubleMeta.getPrimaryCode("ciao"));
    }

    @Test
    void testGetPrimaryCode_contains_ck() {
        Assertions.assertEquals("PK", doubleMeta.getPrimaryCode("back"));
    }

    @Test
    void testGetPrimaryCode_contains_ck_2() {
        Assertions.assertEquals("LKS", doubleMeta.getPrimaryCode("licks"));
    }

    @Test
    void testGetPrimaryCode_contains_cz() {
        Assertions.assertEquals("SR", doubleMeta.getPrimaryCode("czar"));
    }

    @Test
    void testGetPrimaryCode_contains_dd() {
        Assertions.assertEquals("RTR", doubleMeta.getPrimaryCode("rudder"));
    }

    @Test
    void testGetPrimaryCode_contains_dg() {
        Assertions.assertEquals("AJ", doubleMeta.getPrimaryCode("edge"));
    }

    @Test
    void testGetPrimaryCode_contains_de() {
        Assertions.assertEquals("PRJ", doubleMeta.getPrimaryCode("bridge"));
    }

    @Test
    void testGetPrimaryCode_contains_dga() {
        Assertions.assertEquals("NLTKPL", doubleMeta.getPrimaryCode("knowledgable"));
    }

    @Test
    void testGetPrimaryCode_contains_ff() {
        Assertions.assertEquals("RFL", doubleMeta.getPrimaryCode("ruffle"));
    }

    @Test
    void testGetPrimaryCode_contains_ft() {
        Assertions.assertEquals("RFT", doubleMeta.getPrimaryCode("raft"));
    }

    @Test
    void testGetPrimaryCode_contains_ger() {
        Assertions.assertEquals("TKR", doubleMeta.getPrimaryCode("dagger"));
    }

    @Test
    void testGetPrimaryCode_contains_get() {
        Assertions.assertEquals("PNKR", doubleMeta.getPrimaryCode("banger"));
    }

    @Test
    void testGetPrimaryCode_contains_gg() {
        Assertions.assertEquals("PKR", doubleMeta.getPrimaryCode("bagger"));
    }

    @Test
    void testGetPrimaryCode_contains_isl() {
        Assertions.assertEquals("ALNT", doubleMeta.getPrimaryCode("island"));
    }

    @Test
    void testGetPrimaryCode_contains_jj() {
        Assertions.assertEquals("HJ", doubleMeta.getPrimaryCode("hajji"));
    }

    @Test
    void testGetPrimaryCode_contains_jo() {
        Assertions.assertEquals("JP", doubleMeta.getPrimaryCode("job"));
    }

    @Test
    void testGetPrimaryCode_contains_jo_2() {
        Assertions.assertEquals("KJL", doubleMeta.getPrimaryCode("cajole"));
    }

    @Test
    void testGetPrimaryCode_contains_jo_3() {
        Assertions.assertEquals("AJR", doubleMeta.getPrimaryCode("ajar"));
    }

    @Test
    void testGetPrimaryCode_contains_ll() {
        Assertions.assertEquals("KLR", doubleMeta.getPrimaryCode("caller"));
    }

    @Test
    void testGetPrimaryCode_contains_ll_2() {
        Assertions.assertEquals("FRFL", doubleMeta.getPrimaryCode("farfalle"));
    }

    @Test
    void testGetPrimaryCode_contains_macher() {
        Assertions.assertEquals("MKR", doubleMeta.getPrimaryCode("macher"));
    }

    @Test
    void testGetPrimaryCode_contains_orches() {
        Assertions.assertEquals("TRKS", doubleMeta.getPrimaryCode("torches"));
    }

    @Test
    void testGetPrimaryCode_contains_pn() {
        Assertions.assertEquals("APN", doubleMeta.getPrimaryCode("apnea")); // Only "PH" is "F"
    }

    @Test
    void testGetPrimaryCode_contains_pp() {
        Assertions.assertEquals("HP", doubleMeta.getPrimaryCode("happy"));
    }

    @Test
    void testGetPrimaryCode_contains_rgh_notPrecededByVowel() {
        Assertions.assertEquals("ARK", doubleMeta.getPrimaryCode("argh"));
    }

    @Test
    void testGetPrimaryCode_contains_rr() {
        Assertions.assertEquals("KRNT", doubleMeta.getPrimaryCode("current"));
    }

    @Test
    void testGetPrimaryCode_contains_ss() {
        Assertions.assertEquals("MSL", doubleMeta.getPrimaryCode("missile"));
    }

    @Test
    void testGetPrimaryCode_contains_tch() {
        Assertions.assertEquals("MX", doubleMeta.getPrimaryCode("match"));
    }

    @Test
    void testGetPrimaryCode_contains_th() {
        Assertions.assertEquals("M0", doubleMeta.getPrimaryCode("math"));
    }

    @Test
    void testGetPrimaryCode_contains_thom() {
        Assertions.assertEquals("FTM", doubleMeta.getPrimaryCode("fathom"));
    }

    @Test
    void testGetPrimaryCode_contains_tion() {
        Assertions.assertEquals("RXN", doubleMeta.getPrimaryCode("ration"));
    }

    @Test
    void testGetPrimaryCode_contains_tt() {
        Assertions.assertEquals("MTR", doubleMeta.getPrimaryCode("matter"));
    }

    @Test
    void testGetPrimaryCode_contains_witz() {
        Assertions.assertEquals("HRTS", doubleMeta.getPrimaryCode("horowitz"));
    }

    @Test
    void testGetPrimaryCode_contains_wr_1() {
        Assertions.assertEquals("R0", doubleMeta.getPrimaryCode("wrath"));
    }

    @Test
    void testGetPrimaryCode_contains_wr_2() {
        Assertions.assertEquals("RRT", doubleMeta.getPrimaryCode("rewrite"));
    }

    @Test
    void testGetPrimaryCode_contains_xx() {
        Assertions.assertEquals("FKST", doubleMeta.getPrimaryCode("vaxxed"));
    }

    @Test
    void testGetPrimaryCode_contains_zh() {
        Assertions.assertEquals("AJR0", doubleMeta.getPrimaryCode("azharoth"));
    }

    @Test
    void testGetPrimaryCode_contains_zz() {
        Assertions.assertEquals("JS", doubleMeta.getPrimaryCode("jazz"));
    }
}
