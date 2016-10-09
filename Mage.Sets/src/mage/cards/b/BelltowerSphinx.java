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
package mage.cards.b;

import java.util.UUID;
import mage.MageInt;
import mage.abilities.TriggeredAbilityImpl;
import mage.abilities.dynamicvalue.common.StaticValue;
import mage.abilities.effects.common.PutLibraryIntoGraveTargetEffect;
import mage.abilities.keyword.FlyingAbility;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.CardType;
import mage.constants.Zone;
import mage.game.Game;
import mage.game.events.GameEvent;
import mage.game.events.GameEvent.EventType;
import mage.players.Player;
import mage.target.targetpointer.FixedTarget;

/**
 * @author nantuko
 */
public class BelltowerSphinx extends CardImpl {

    public BelltowerSphinx(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId,setInfo,new CardType[]{CardType.CREATURE},"{4}{U}");
        this.subtype.add("Sphinx");

        this.power = new MageInt(2);
        this.toughness = new MageInt(5);

        this.addAbility(FlyingAbility.getInstance());
        // Whenever a source deals damage to Belltower Sphinx, that source's controller puts that many cards from the top of his or her library into his or her graveyard.
        this.addAbility(new BelltowerSphinxEffect());
    }

    public BelltowerSphinx(final BelltowerSphinx card) {
        super(card);
    }

    @Override
    public BelltowerSphinx copy() {
        return new BelltowerSphinx(this);
    }
}

class BelltowerSphinxEffect extends TriggeredAbilityImpl {

    public BelltowerSphinxEffect() {
        super(Zone.BATTLEFIELD, new PutLibraryIntoGraveTargetEffect(0));
    }

    public BelltowerSphinxEffect(BelltowerSphinxEffect effect) {
        super(effect);
    }

    @Override
    public BelltowerSphinxEffect copy() {
        return new BelltowerSphinxEffect(this);
    }

    @Override
    public boolean checkEventType(GameEvent event, Game game) {
        return event.getType() == EventType.DAMAGED_CREATURE;
    }

    @Override
    public boolean checkTrigger(GameEvent event, Game game) {
        if (event.getTargetId().equals(this.sourceId)) {
            UUID controller = game.getControllerId(event.getSourceId());
            if (controller != null) {
                Player player = game.getPlayer(controller);
                if (player != null) {
                    getEffects().get(0).setTargetPointer(new FixedTarget(player.getId()));
                    ((PutLibraryIntoGraveTargetEffect) getEffects().get(0)).setAmount(new StaticValue(event.getAmount()));
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public String getRule() {
        return "Whenever a source deals damage to {this}, that source's controller puts that many cards from the top of his or her library into his or her graveyard.";
    }
}