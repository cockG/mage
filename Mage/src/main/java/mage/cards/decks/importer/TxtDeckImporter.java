/*
 *  Copyright 2010 BetaSteward_at_googlemail.com. All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without modification, are
 *  permitted provided that the following conditions are met:
 *
 *     1. Redistributions of source code must retain the above copyright notice, this list of
 *        conditions and the following disclaimer.
 *
 *     2. Redistributions in binary form must reproduce the above copyright notice, this list
 *        of conditions and the following disclaimer in the documentation and/or other materials
 *        provided with the distribution.
 *
 *  THIS SOFTWARE IS PROVIDED BY BetaSteward_at_googlemail.com ``AS IS'' AND ANY EXPRESS OR IMPLIED
 *  WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 *  FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL BetaSteward_at_googlemail.com OR
 *  CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 *  CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 *  SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 *  ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *  The views and conclusions contained in the software and documentation are those of the
 *  authors and should not be interpreted as representing official policies, either expressed
 *  or implied, of BetaSteward_at_googlemail.com.
 */
package mage.cards.decks.importer;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import mage.cards.decks.DeckCardInfo;
import mage.cards.decks.DeckCardLists;
import mage.cards.repository.CardInfo;
import mage.cards.repository.CardRepository;

/**
 *
 * @author BetaSteward_at_googlemail.com
 */
public class TxtDeckImporter extends DeckImporter {

    public static final String[] SET_VALUES = new String[]{"lands", "creatures", "planeswalkers", "other spells", "sideboard cards",
        "Instant", "Land", "Enchantment", "Artifact", "Sorcery", "Planeswalker", "Creature"};
    public static final Set<String> IGNORE_NAMES = new HashSet<>(Arrays.asList(SET_VALUES));

    private boolean sideboard = false;
    private boolean switchSideboardByEmptyLine = true; // all cards after first empty line will be sideboard (like mtgo format)
    private int nonEmptyLinesTotal = 0;

    public TxtDeckImporter(boolean haveSideboardSection) {
        if (haveSideboardSection) {
            switchSideboardByEmptyLine = false;
        }
    }

    @Override
    protected void readLine(String line, DeckCardLists deckList) {

        line = line.trim();

        // process comment:
        // skip or force to sideboard
        String commentString = line.toLowerCase(Locale.ENGLISH);
        if (commentString.startsWith("//")) {
            // use start, not contains (card names may contain commands like "Legerdemain")

            if (commentString.startsWith("//sideboard")) {
                sideboard = true;
            }

            // skip comment line
            return;
        }

        // remove inner card comments from text line: 2 Blinding Fog #some text (like deckstats format)
        int commentDelim = line.indexOf('#');
        if (commentDelim >= 0) {
            line = line.substring(0, commentDelim).trim();
        }

        // switch sideboard by empty line
        if (switchSideboardByEmptyLine && line.isEmpty() && nonEmptyLinesTotal > 0) {
            if (!sideboard) {
                sideboard = true;
            } else {
                sbMessage.append("Found empty line at ").append(lineCount).append(", but sideboard already used. Use //sideboard switcher OR one empty line to devide your cards.").append('\n');
            }

            // skip empty line
            return;
        }

        nonEmptyLinesTotal++;

        // single line sideboard card from deckstats.net
        // SB: 3 Carnage Tyrant
        boolean singleLineSideBoard = false;
        if (line.startsWith("SB:")) {
            line = line.replace("SB:", "").trim();
            singleLineSideBoard = true;
        }

        line = line.replace("\t", " "); // changing tabs to blanks as delimiter
        int delim = line.indexOf(' ');
        if (delim < 0) {
            return;
        }
        String lineNum = line.substring(0, delim).trim();
        String lineName = line.substring(delim).replace("’", "\'").trim();
        lineName = lineName
                .replace("&amp;", "//")
                .replace("Ã†", "Ae")
                .replace("Ã¶", "o")
                .replace("û", "u")
                .replace("í", "i")
                .replace("â", "a")
                .replace("á", "a")
                .replace("\"", "'");
        if (lineName.contains("//") && !lineName.contains(" // ")) {
            lineName = lineName.replace("//", " // ");
        }
        if (lineName.contains(" / ")) {
            lineName = lineName.replace(" / ", " // ");
        }
        if (IGNORE_NAMES.contains(lineName) || IGNORE_NAMES.contains(lineNum)) {
            return;
        }
        try {
            int num = Integer.parseInt(lineNum.replaceAll("\\D+", ""));
            if ((num < 0) || (num > 100)) {
                sbMessage.append("Invalid number (too small or too big): ").append(lineNum).append(" at line ").append(lineCount).append('\n');
                return;
            }

            CardInfo cardInfo = CardRepository.instance.findPreferedCoreExpansionCard(lineName, true);
            if (cardInfo == null) {
                sbMessage.append("Could not find card: '").append(lineName).append("' at line ").append(lineCount).append('\n');
            } else {
                for (int i = 0; i < num; i++) {
                    if (!sideboard && !singleLineSideBoard) {
                        deckList.getCards().add(new DeckCardInfo(cardInfo.getName(), cardInfo.getCardNumber(), cardInfo.getSetCode()));
                    } else {
                        deckList.getSideboard().add(new DeckCardInfo(cardInfo.getName(), cardInfo.getCardNumber(), cardInfo.getSetCode()));
                    }
                }
            }
        } catch (NumberFormatException nfe) {
            sbMessage.append("Invalid number: ").append(lineNum).append(" at line ").append(lineCount).append('\n');
        }
    }
}
