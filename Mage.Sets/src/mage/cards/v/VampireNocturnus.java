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
package mage.cards.v;

import java.util.UUID;
import mage.MageInt;
import mage.abilities.Ability;
import mage.abilities.StaticAbility;
import mage.abilities.common.SimpleStaticAbility;
import mage.abilities.condition.Condition;
import mage.abilities.decorator.ConditionalContinuousEffect;
import mage.abilities.effects.common.continuous.BoostControlledEffect;
import mage.abilities.effects.common.continuous.BoostSourceEffect;
import mage.abilities.effects.common.continuous.GainAbilityControlledEffect;
import mage.abilities.effects.common.continuous.GainAbilitySourceEffect;
import mage.abilities.effects.common.continuous.PlayWithTheTopCardRevealedEffect;
import mage.abilities.keyword.FlyingAbility;
import mage.cards.Card;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.CardType;
import mage.constants.Duration;
import mage.constants.SubType;
import mage.constants.Zone;
import mage.filter.common.FilterCreaturePermanent;
import mage.filter.predicate.mageobject.SubtypePredicate;
import mage.game.Game;
import mage.players.Player;

/**
 *
 * @author North
 */
public class VampireNocturnus extends CardImpl {

    public VampireNocturnus(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId,setInfo,new CardType[]{CardType.CREATURE},"{1}{B}{B}{B}");
        this.subtype.add(SubType.VAMPIRE);

        this.power = new MageInt(3);
        this.toughness = new MageInt(3);

        // Play with the top card of your library revealed.
        this.addAbility(new SimpleStaticAbility(Zone.BATTLEFIELD, new PlayWithTheTopCardRevealedEffect()));
        // As long as the top card of your library is black, Vampire Nocturnus and other Vampire creatures you control get +2/+1 and have flying.
        this.addAbility(new VampireNocturnusAbility());
    }

    public VampireNocturnus(final VampireNocturnus card) {
        super(card);
    }

    @Override
    public VampireNocturnus copy() {
        return new VampireNocturnus(this);
    }
}

class VampireNocturnusAbility extends StaticAbility {

    private static final FilterCreaturePermanent filter = new FilterCreaturePermanent();

    static {
        filter.add(new SubtypePredicate(SubType.VAMPIRE));
    }

    public VampireNocturnusAbility() {
        super(Zone.BATTLEFIELD, null);
        this.addEffect(new ConditionalContinuousEffect(
                new BoostSourceEffect(2, 1, Duration.WhileOnBattlefield),
                new VampireNocturnusCondition(), ""));
        this.addEffect(new ConditionalContinuousEffect(
                new BoostControlledEffect(2, 1, Duration.WhileOnBattlefield, filter, true),
                new VampireNocturnusCondition(), ""));
        this.addEffect(new ConditionalContinuousEffect(new GainAbilitySourceEffect(FlyingAbility.getInstance()),
                new VampireNocturnusCondition(), ""));
        this.addEffect(new ConditionalContinuousEffect(new GainAbilityControlledEffect(FlyingAbility.getInstance(), Duration.WhileOnBattlefield, filter, true),
                new VampireNocturnusCondition(), ""));
    }

    public VampireNocturnusAbility(VampireNocturnusAbility ability) {
        super(ability);
    }

    @Override
    public VampireNocturnusAbility copy() {
        return new VampireNocturnusAbility(this);
    }

    @Override
    public String getRule() {
        return "As long as the top card of your library is black, {this} and other Vampire creatures you control get +2/+1 and have flying.";
    }
}

class VampireNocturnusCondition implements Condition {

    @Override
    public boolean apply(Game game, Ability source) {
        Player player = game.getPlayer(source.getControllerId());
        if (player != null) {
            Card card = player.getLibrary().getFromTop(game);
            if (card != null) {
                return card.getColor(game).isBlack();
            }
        }
        return false;
    }
}
