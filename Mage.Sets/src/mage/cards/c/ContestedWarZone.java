/*
 *  Copyright 2011 BetaSteward_at_googlemail.com. All rights reserved.
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
package mage.cards.c;

import java.util.UUID;
import mage.abilities.Ability;
import mage.abilities.Mode;
import mage.abilities.TriggeredAbilityImpl;
import mage.abilities.common.SimpleActivatedAbility;
import mage.abilities.costs.common.TapSourceCost;
import mage.abilities.costs.mana.ManaCostsImpl;
import mage.abilities.effects.ContinuousEffectImpl;
import mage.abilities.effects.common.continuous.BoostAllEffect;
import mage.abilities.mana.ColorlessManaAbility;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.*;
import mage.filter.common.FilterAttackingCreature;
import mage.game.Game;
import mage.game.events.DamagedPlayerEvent;
import mage.game.events.GameEvent;
import mage.game.permanent.Permanent;

/**
 *
 * @author BetaSteward_at_googlemail.com
 */
public class ContestedWarZone extends CardImpl {

    private static final FilterAttackingCreature filter = new FilterAttackingCreature("Attacking creatures");

    public ContestedWarZone(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.LAND}, null);

        // Whenever a creature deals combat damage to you, that creature's controller gains control of Contested War Zone.
        this.addAbility(new ContestedWarZoneAbility());

        // {T}: Add {C}.
        this.addAbility(new ColorlessManaAbility());

        // {1}, {T}: Attacking creatures get +1/+0 until end of turn.
        Ability ability = new SimpleActivatedAbility(Zone.BATTLEFIELD, new BoostAllEffect(1, 0, Duration.EndOfTurn, filter, false), new ManaCostsImpl("{1}"));
        ability.addCost(new TapSourceCost());
        this.addAbility(ability);
    }

    public ContestedWarZone(final ContestedWarZone card) {
        super(card);
    }

    @Override
    public ContestedWarZone copy() {
        return new ContestedWarZone(this);
    }

}

class ContestedWarZoneAbility extends TriggeredAbilityImpl {

    public ContestedWarZoneAbility() {
        super(Zone.BATTLEFIELD, new ContestedWarZoneEffect());
    }

    public ContestedWarZoneAbility(final ContestedWarZoneAbility ability) {
        super(ability);
    }

    @Override
    public ContestedWarZoneAbility copy() {
        return new ContestedWarZoneAbility(this);
    }

    @Override
    public boolean checkEventType(GameEvent event, Game game) {
        return event.getType() == GameEvent.EventType.DAMAGED_PLAYER;
    }

    @Override
    public boolean checkTrigger(GameEvent event, Game game) {
        DamagedPlayerEvent damageEvent = (DamagedPlayerEvent) event;
        if (damageEvent.isCombatDamage()) {
            Permanent permanent = game.getPermanent(event.getSourceId());
            if (damageEvent.getPlayerId().equals(getControllerId()) && permanent != null && permanent.isCreature()) {
                game.getState().setValue(getSourceId().toString(), permanent.getControllerId());
                return true;
            }
        }
        return false;
    }

    @Override
    public String getRule() {
        return "Whenever a creature deals combat damage to you, that creature's controller gains control of {this}";
    }

}

class ContestedWarZoneEffect extends ContinuousEffectImpl {

    public ContestedWarZoneEffect() {
        super(Duration.Custom, Layer.ControlChangingEffects_2, SubLayer.NA, Outcome.GainControl);
    }

    public ContestedWarZoneEffect(final ContestedWarZoneEffect effect) {
        super(effect);
    }

    @Override
    public ContestedWarZoneEffect copy() {
        return new ContestedWarZoneEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Permanent permanent = game.getPermanent(source.getSourceId());
        UUID controllerId = (UUID) game.getState().getValue(source.getSourceId().toString());
        if (permanent != null && controllerId != null) {
            return permanent.changeControllerId(controllerId, game);
        } else {
            discard();
        }
        return false;
    }

    @Override
    public String getText(Mode mode) {
        return "Gain control of {this}";
    }
}
