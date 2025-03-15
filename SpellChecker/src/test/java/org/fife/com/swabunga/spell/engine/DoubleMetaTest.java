package org.fife.com.swabunga.spell.engine;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
    void testGetReplaceList() {
        char[] expectedReplaceList = {'A', 'B', 'X', 'S', 'K', 'J', 'T', 'F', 'H', 'L', 'M', 'N', 'P', 'R', '0'};
        Assertions.assertArrayEquals(expectedReplaceList, doubleMeta.getReplaceList());
    }

    @Test
    void testTransform_null() {
        Assertions.assertEquals("", doubleMeta.transform(null));
    }

    @Test
    void testTransform_emptyString() {
        Assertions.assertEquals("", doubleMeta.transform(""));
    }

    @Test
    void testTransform_simpleWord() {
        Assertions.assertEquals("TST", doubleMeta.transform("test"));
    }

    @Test
    void testTransform_caseInsensitive() {
        Assertions.assertEquals("TST", doubleMeta.transform("Test"));
    }

    @Test
    void testTransform_endsWith_alle() {
        Assertions.assertEquals("FRFL", doubleMeta.transform("farfalle"));
    }

    @Test
    void testTransform_endsWith_eaux() {
        Assertions.assertEquals("PRKS", doubleMeta.transform("breaux"));
    }

    @Test
    void testTransform_endsWith_gh() {
        Assertions.assertEquals("P", doubleMeta.transform("bough"));
    }

    @Test
    void testTransform_endsWith_j_1() {
        Assertions.assertEquals("TJ", doubleMeta.transform("dj"));
    }

    @Test
    void testTransform_endsWith_j_2() {
        Assertions.assertEquals("HJ", doubleMeta.transform("haj"));
    }

    @Test
    void testTransform_endsWith_ough_1() {
        Assertions.assertEquals("TF", doubleMeta.transform("tough"));
    }

    @Test
    void testTransform_endsWith_vowel_then_w() {
        Assertions.assertEquals("M", doubleMeta.transform("maw"));
    }

    @Test
    void testTransform_endsWith_w() {
        Assertions.assertEquals("ANH", doubleMeta.transform("anyhow"));
    }

    @Test
    void testTransform_phoneticMatch() {
        Assertions.assertEquals("P", doubleMeta.transform("ph"));
    }

    @Test
    void testTransform_startsWith_a() {
        Assertions.assertEquals("AT", doubleMeta.transform("at"));
    }

    @Test
    void testTransform_startsWith_agn() {
        Assertions.assertEquals("AKNL", doubleMeta.transform("agnel"));
    }

    @Test
    void testTransform_startsWith_b() {
        Assertions.assertEquals("PT", doubleMeta.transform("bat"));
    }

    @Test
    void testTransform_startsWith_u00c7() {
        Assertions.assertEquals("ST", doubleMeta.transform("\u00c7at"));
    }

    @Test
    void testTransform_startsWith_c() {
        Assertions.assertEquals("KT", doubleMeta.transform("cat"));
    }

    @Test
    void testTransform_startsWith_c_chae() {
        Assertions.assertEquals("X", doubleMeta.transform("chae"));
    }

    @Test
    void testTransform_startsWith_c_equals_casear() {
        Assertions.assertEquals("SSR", doubleMeta.transform("caesar"));
    }

    @Test
    void testTransform_startsWith_charism() {
        Assertions.assertEquals("KRSM", doubleMeta.transform("charisma"));
    }

    @Test
    void testTransform_startsWith_cr_1() {
        Assertions.assertEquals("KRSP", doubleMeta.transform("crisp"));
    }

    @Test
    void testTransform_startsWith_cr_2() {
        Assertions.assertEquals("KR", doubleMeta.transform("cry"));
    }

    @Test
    void testTransform_startsWith_d() {
        Assertions.assertEquals("TRT", doubleMeta.transform("dart"));
    }

    @Test
    void testTransform_startsWith_f() {
        Assertions.assertEquals("FT", doubleMeta.transform("fat"));
    }

    @Test
    void testTransform_startsWith_g() {
        Assertions.assertEquals("KRTN", doubleMeta.transform("garden"));
    }

    @Test
    void testTransform_startsWith_ger() {
        Assertions.assertEquals("KRM", doubleMeta.transform("germ"));
    }

    @Test
    void testTransform_startsWith_ghi() {
        Assertions.assertEquals("JK", doubleMeta.transform("ghig"));
    }

    @Test
    void testTransform_startsWith_ghi_2() {
        Assertions.assertEquals("J", doubleMeta.transform("ghi"));
    }

    @Test
    void testTransform_startsWith_gho() {
        Assertions.assertEquals("KST", doubleMeta.transform("ghost"));
    }

    @Test
    void testTransform_startsWith_gli() {
        Assertions.assertEquals("KLP", doubleMeta.transform("glib"));
    }

    @Test
    void testTransform_startsWith_gy() {
        Assertions.assertEquals("KM", doubleMeta.transform("gym"));
    }

    @Test
    void testTransform_startsWith_h() {
        Assertions.assertEquals("HT", doubleMeta.transform("hat"));
    }

    @Test
    void testTransform_startsWith_j_1() {
        Assertions.assertEquals("JK", doubleMeta.transform("joke"));
    }

    @Test
    void testTransform_startsWith_j_2() {
        Assertions.assertEquals("J", doubleMeta.transform("jaw"));
    }

    @Test
    void testTransform_startsWith_jose_1() {
        Assertions.assertEquals("HS", doubleMeta.transform("jose"));
    }

    @Test
    void testTransform_startsWith_jose_2() {
        Assertions.assertEquals("JSP", doubleMeta.transform("joseph"));
    }

    @Test
    void testTransform_startsWith_k() {
        Assertions.assertEquals("KT", doubleMeta.transform("kite"));
    }

    @Test
    void testTransform_startsWith_lamb() {
        Assertions.assertEquals("LMP", doubleMeta.transform("lamb"));
    }

    @Test
    void testTransform_startsWith_m() {
        Assertions.assertEquals("MT", doubleMeta.transform("mat"));
    }

    @Test
    void testTransform_startsWith_n() {
        Assertions.assertEquals("NP", doubleMeta.transform("nap"));
    }

    @Test
    void testTransform_startsWith_u00D1() {
        Assertions.assertEquals("NP", doubleMeta.transform("\u00d1ap"));
    }

    @Test
    void testTransform_startsWith_p() {
        Assertions.assertEquals("PT", doubleMeta.transform("pat"));
    }

    @Test
    void testTransform_startsWith_q() {
        Assertions.assertEquals("KK", doubleMeta.transform("quick"));
    }

    @Test
    void testTransform_startsWith_r() {
        Assertions.assertEquals("RT", doubleMeta.transform("rat"));
    }

    @Test
    void testTransform_startsWith_s() {
        Assertions.assertEquals("ST", doubleMeta.transform("sat"));
    }

    @Test
    void testTransform_startsWith_sce() {
        Assertions.assertEquals("SN", doubleMeta.transform("scene"));
    }

    @Test
    void testTransform_startsWith_sch() {
        Assertions.assertEquals("SKL", doubleMeta.transform("school"));
    }

    @Test
    void testTransform_startsWith_schen() {
        Assertions.assertEquals("XNK", doubleMeta.transform("schenk"));
    }

    @Test
    void testTransform_startsWith_schoo() {
        Assertions.assertEquals("SKL", doubleMeta.transform("school"));
    }

    @Test
    void testTransform_startsWith_sia() {
        Assertions.assertEquals("SLT", doubleMeta.transform("sialoid"));
    }

    @Test
    void testTransform_startsWith_sm() {
        Assertions.assertEquals("SML", doubleMeta.transform("smell"));
    }

    @Test
    void testTransform_startsWith_sugar() {
        Assertions.assertEquals("XKR", doubleMeta.transform("sugar"));
    }

    @Test
    void testTransform_startsWith_sz() {
        Assertions.assertEquals("SXN", doubleMeta.transform("szechuan"));
    }

    @Test
    void testTransform_startsWith_t() {
        Assertions.assertEquals("TNT", doubleMeta.transform("taint"));
    }

    @Test
    void testTransform_startsWith_v() {
        Assertions.assertEquals("FT", doubleMeta.transform("vat"));
    }

    @Test
    void testTransform_startsWith_w() {
        Assertions.assertEquals("AT", doubleMeta.transform("what"));
    }

    @Test
    void testTransform_startsWith_x() {
        Assertions.assertEquals("SR", doubleMeta.transform("xray"));
    }

    @Test
    void testTransform_startsWith_z() {
        Assertions.assertEquals("ST", doubleMeta.transform("zit"));
    }

    @Test
    void textTransform_startsWith_chi() {
        Assertions.assertEquals("XSLM", doubleMeta.transform("chisholm"));
    }

    @Test
    void textTransform_startsWith_w_then_vowel() {
        Assertions.assertEquals("ART", doubleMeta.transform("ward"));
    }

    @Test
    void textTransform_startsWith_wh() {
        Assertions.assertEquals("AL", doubleMeta.transform("while"));
    }

    @Test
    void testTransform_contains_cr() {
        Assertions.assertEquals("AKR", doubleMeta.transform("acre"));
    }

    @Test
    void testTransform_contains_agn() {
        Assertions.assertEquals("MNT", doubleMeta.transform("magnet"));
    }

    @Test
    void testTransform_contains_alle() {
        Assertions.assertEquals("KLR", doubleMeta.transform("caller"));
    }

    @Test
    void testTransform_contains_aux() {
        Assertions.assertEquals("AKS", doubleMeta.transform("aux"));
    }

    @Test
    void testTransform_contains_bb() {
        Assertions.assertEquals("TPL", doubleMeta.transform("dabble"));
    }

    @Test
    void testTransform_contains_cc() {
        Assertions.assertEquals("AKSNT", doubleMeta.transform("accent"));
    }

    @Test
    void testTransform_contains_cc_2() {
        Assertions.assertEquals("AXNTRK", doubleMeta.transform("eccentric"));
    }

    @Test
    void testTransform_contains_cc_3() {
        Assertions.assertEquals("AKMPN", doubleMeta.transform("accompany"));
    }

    @Test
    void testTransform_contains_cc_4() {
        Assertions.assertEquals("AKLT", doubleMeta.transform("occult"));
    }

    @Test
    void testTransform_contains_chae() {
        Assertions.assertEquals("RKL", doubleMeta.transform("rachael"));
    }

    @Test
    void testTransform_contains_chae_2() {
        Assertions.assertEquals("AXM", doubleMeta.transform("ischaemia"));
    }

    @Test
    void testTransform_contains_chae_3() {
        Assertions.assertEquals("ARKNS", doubleMeta.transform("archaeans"));
    }

    @Test
    void testTransform_contains_chia() {
        Assertions.assertEquals("K", doubleMeta.transform("chiao"));
    }

    @Test
    void testTransform_contains_cia() {
        Assertions.assertEquals("ALPX", doubleMeta.transform("alopecia"));
    }

    @Test
    void testTransform_contains_cia_2() {
        Assertions.assertEquals("PNFXL", doubleMeta.transform("beneficial"));
    }

    @Test
    void testTransform_contains_cia_3() {
        Assertions.assertEquals("X", doubleMeta.transform("ciao"));
    }

    @Test
    void testTransform_contains_ck() {
        Assertions.assertEquals("PK", doubleMeta.transform("back"));
    }

    @Test
    void testTransform_contains_ck_2() {
        Assertions.assertEquals("LKS", doubleMeta.transform("licks"));
    }

    @Test
    void testTransform_contains_cz() {
        Assertions.assertEquals("SR", doubleMeta.transform("czar"));
    }

    @Test
    void testTransform_contains_dd() {
        Assertions.assertEquals("RTR", doubleMeta.transform("rudder"));
    }

    @Test
    void testTransform_contains_dg() {
        Assertions.assertEquals("AJ", doubleMeta.transform("edge"));
    }

    @Test
    void testTransform_contains_de() {
        Assertions.assertEquals("PRJ", doubleMeta.transform("bridge"));
    }

    @Test
    void testTransform_contains_dga() {
        Assertions.assertEquals("NLTKPL", doubleMeta.transform("knowledgable"));
    }

    @Test
    void testTransform_contains_ff() {
        Assertions.assertEquals("RFL", doubleMeta.transform("ruffle"));
    }

    @Test
    void testTransform_contains_ft() {
        Assertions.assertEquals("RFT", doubleMeta.transform("raft"));
    }

    @Test
    void testTransform_contains_ger() {
        Assertions.assertEquals("TKR", doubleMeta.transform("dagger"));
    }

    @Test
    void testTransform_contains_get() {
        Assertions.assertEquals("PNKR", doubleMeta.transform("banger"));
    }

    @Test
    void testTransform_contains_gg() {
        Assertions.assertEquals("PKR", doubleMeta.transform("bagger"));
    }

    @Test
    void testTransform_contains_isl() {
        Assertions.assertEquals("ALNT", doubleMeta.transform("island"));
    }

    @Test
    void testTransform_contains_jj() {
        Assertions.assertEquals("HJ", doubleMeta.transform("hajji"));
    }

    @Test
    void testTransform_contains_jo() {
        Assertions.assertEquals("JP", doubleMeta.transform("job"));
    }

    @Test
    void testTransform_contains_jo_2() {
        Assertions.assertEquals("KJL", doubleMeta.transform("cajole"));
    }

    @Test
    void testTransform_contains_jo_3() {
        Assertions.assertEquals("AJR", doubleMeta.transform("ajar"));
    }

    @Test
    void testTransform_contains_ll() {
        Assertions.assertEquals("KLR", doubleMeta.transform("caller"));
    }

    @Test
    void testTransform_contains_ll_2() {
        Assertions.assertEquals("FRFL", doubleMeta.transform("farfalle"));
    }

    @Test
    void testTransform_contains_macher() {
        Assertions.assertEquals("MKR", doubleMeta.transform("macher"));
    }

    @Test
    void testTransform_contains_orches() {
        Assertions.assertEquals("TRKS", doubleMeta.transform("torches"));
    }

    @Test
    void testTransform_contains_pn() {
        Assertions.assertEquals("AF", doubleMeta.transform("apnea"));
    }

    @Test
    void testTransform_contains_pp() {
        Assertions.assertEquals("HP", doubleMeta.transform("happy"));
    }

    @Test
    void testTransform_contains_rgh_notPrecededByVowel() {
        Assertions.assertEquals("ARK", doubleMeta.transform("argh"));
    }

    @Test
    void testTransform_contains_rr() {
        Assertions.assertEquals("KRNT", doubleMeta.transform("current"));
    }

    @Test
    void testTransform_contains_ss() {
        Assertions.assertEquals("MSL", doubleMeta.transform("missile"));
    }

    @Test
    void testTransform_contains_tch() {
        Assertions.assertEquals("MX", doubleMeta.transform("match"));
    }

    @Test
    void testTransform_contains_th() {
        Assertions.assertEquals("M0", doubleMeta.transform("math"));
    }

    @Test
    void testTransform_contains_thom() {
        Assertions.assertEquals("FTM", doubleMeta.transform("fathom"));
    }

    @Test
    void testTransform_contains_tion() {
        Assertions.assertEquals("RXN", doubleMeta.transform("ration"));
    }

    @Test
    void testTransform_contains_tt() {
        Assertions.assertEquals("MTR", doubleMeta.transform("matter"));
    }

    @Test
    void testTransform_contains_witz() {
        Assertions.assertEquals("HRTS", doubleMeta.transform("horowitz"));
    }

    @Test
    void testTransform_contains_wr_1() {
        Assertions.assertEquals("R0", doubleMeta.transform("wrath"));
    }

    @Test
    void testTransform_contains_wr_2() {
        Assertions.assertEquals("RRT", doubleMeta.transform("rewrite"));
    }

    @Test
    void testTransform_contains_xx() {
        Assertions.assertEquals("FKST", doubleMeta.transform("vaxxed"));
    }

    @Test
    void testTransform_contains_zh() {
        Assertions.assertEquals("AJR0", doubleMeta.transform("azharoth"));
    }

    @Test
    void testTransform_contains_zz() {
        Assertions.assertEquals("JS", doubleMeta.transform("jazz"));
    }
}
