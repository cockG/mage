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
package mage.cards.s;

import java.util.UUID;
import mage.Mana;
import mage.abilities.Ability;
import mage.abilities.costs.common.TapSourceCost;
import mage.abilities.effects.common.ChoosePlayerEffect;
import mage.abilities.effects.common.ManaEffect;
import mage.abilities.mana.SimpleManaAbility;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.choices.ChoiceColor;
import mage.constants.CardType;
import mage.constants.Outcome;
import mage.constants.Zone;
import mage.game.Game;
import mage.players.Player;

/**
 *
 * @author Galatolol
 */
public class SpectralSearchlight extends CardImpl {

    public SpectralSearchlight(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.ARTIFACT}, "{3}");

        // {T}: Choose a player. That player adds one mana of any color he or she chooses to their mana pool.
        ManaEffect effect = new SpectralSearchlightManaEffect("chosen player");
        effect.setText("That player adds one mana of any color he or she chooses to their mana pool");
        Ability ability = new SimpleManaAbility(Zone.BATTLEFIELD, effect, new TapSourceCost());
        // choosing player as first effect, before adding mana effect
        ability.getEffects().add(0, new ChoosePlayerEffect(Outcome.PutManaInPool));
        this.addAbility(ability);
    }

    public SpectralSearchlight(final SpectralSearchlight card) {
        super(card);
    }

    @Override
    public SpectralSearchlight copy() {
        return new SpectralSearchlight(this);
    }
}

class SpectralSearchlightManaEffect extends ManaEffect {

    public SpectralSearchlightManaEffect(String textManaPoolOwner) {
        super();
        this.staticText = (textManaPoolOwner.equals("their") ? "that player adds " : "add ") + "one mana of any color" + " to " + textManaPoolOwner + " mana pool";
    }

    public SpectralSearchlightManaEffect(final SpectralSearchlightManaEffect effect) {
        super(effect);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Player player = game.getPlayer((UUID) game.getState().getValue(source.getSourceId() + "_player"));
        if (player != null) {
            checkToFirePossibleEvents(getMana(game, source), game, source);
            player.getManaPool().addMana(getMana(game, source), game, source);
            return true;
        }
        return false;
    }

    @Override
    public Mana produceMana(boolean netMana, Game game, Ability source) {
        if (netMana) {
            return null;
        }
        UUID playerId = (UUID) game.getState().getValue(source.getSourceId() + "_player");
        Player player = game.getPlayer(playerId);
        ChoiceColor choice = new ChoiceColor();
        if (player != null && player.choose(outcome, choice, game)) {
            return choice.getMana(1);
        }
        return new Mana();
    }

    @Override
    public SpectralSearchlightManaEffect copy() {
        return new SpectralSearchlightManaEffect(this);
    }

}
