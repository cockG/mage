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
package mage.watchers.common;

import mage.Mana;
import mage.constants.WatcherScope;
import mage.constants.Zone;
import mage.game.Game;
import mage.game.events.GameEvent;
import mage.game.events.ZoneChangeEvent;
import mage.game.stack.Spell;
import mage.watchers.Watcher;

/**
 * Watcher saves the mana that was spent to cast a spell
 *
 * @author LevelX2
 */
public class ManaSpentToCastWatcher extends Watcher {

    Mana payment = null;

    public ManaSpentToCastWatcher() {
        super(ManaSpentToCastWatcher.class.getSimpleName(), WatcherScope.CARD);
    }

    public ManaSpentToCastWatcher(final ManaSpentToCastWatcher watcher) {
        super(watcher);
        this.payment = watcher.payment;
    }

    @Override
    public void watch(GameEvent event, Game game) {
        if (event.getType() == GameEvent.EventType.SPELL_CAST && event.getZone() == Zone.HAND) {
            Spell spell = (Spell) game.getObject(event.getTargetId());
            if (spell != null && this.getSourceId().equals(spell.getSourceId())) {
                payment = spell.getSpellAbility().getManaCostsToPay().getPayment();
            }
        }
        if (event.getType() == GameEvent.EventType.ZONE_CHANGE && this.getSourceId().equals(event.getSourceId())) {
            if (((ZoneChangeEvent) event).getFromZone() == Zone.BATTLEFIELD) {
                payment = null;
            }
        }
    }

    @Override
    public ManaSpentToCastWatcher copy() {
        return new ManaSpentToCastWatcher(this);
    }

    public Mana getAndResetLastPayment() {
        Mana returnPayment = null;
        if (payment != null) {
            returnPayment = payment.copy();
        }
        return returnPayment;

    }

    @Override
    public void reset() {
        super.reset();
        payment = null;
    }

}
