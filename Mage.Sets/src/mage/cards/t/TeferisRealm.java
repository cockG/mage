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
package mage.cards.t;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import mage.abilities.Ability;
import mage.abilities.common.BeginningOfUpkeepTriggeredAbility;
import mage.abilities.effects.OneShotEffect;
import mage.abilities.effects.common.PhaseOutAllEffect;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.choices.Choice;
import mage.choices.ChoiceImpl;
import mage.constants.*;
import mage.filter.FilterPermanent;
import mage.filter.predicate.Predicates;
import mage.filter.predicate.mageobject.CardTypePredicate;
import mage.filter.predicate.mageobject.SubtypePredicate;
import mage.filter.predicate.permanent.TokenPredicate;
import mage.game.Game;
import mage.game.permanent.Permanent;
import mage.players.Player;

/**
 *
 * @author LevelX2
 */
public class TeferisRealm extends CardImpl {

    public TeferisRealm(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.ENCHANTMENT}, "{1}{U}{U}");
        addSuperType(SuperType.WORLD);

        // At the beginning of each player's upkeep, that player chooses artifact, creature, land, or non-Aura enchantment. All nontoken permanents of that type phase out.
        this.addAbility(new BeginningOfUpkeepTriggeredAbility(new TeferisRealmEffect(), TargetController.ANY, false));
    }

    public TeferisRealm(final TeferisRealm card) {
        super(card);
    }

    @Override
    public TeferisRealm copy() {
        return new TeferisRealm(this);
    }
}

class TeferisRealmEffect extends OneShotEffect {

    private static final String ARTIFACT = "Artifact";
    private static final String CREATURE = "Creature";
    private static final String LAND = "Land";
    private static final String NON_AURA_ENCHANTMENT = "Non-Aura enchantment";
    private static final Set<String> choices = new HashSet<>();

    static {
        choices.add(ARTIFACT);
        choices.add(CREATURE);
        choices.add(LAND);
        choices.add(NON_AURA_ENCHANTMENT);
    }

    public TeferisRealmEffect() {
        super(Outcome.Detriment);
        this.staticText = "that player chooses artifact, creature, land, or non-Aura enchantment. All nontoken permanents of that type phase out";
    }

    public TeferisRealmEffect(final TeferisRealmEffect effect) {
        super(effect);
    }

    @Override
    public TeferisRealmEffect copy() {
        return new TeferisRealmEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Player player = game.getPlayer(getTargetPointer().getFirst(game, source));
        Player controller = game.getPlayer(source.getControllerId());
        if (player != null && controller != null) {
            Choice choiceImpl = new ChoiceImpl(true);
            choiceImpl.setMessage("Phase out which kind of permanents?");
            choiceImpl.setChoices(choices);
            if (!player.choose(outcome, choiceImpl, game)) {
                return false;
            }
            String choosenType = choiceImpl.getChoice();
            FilterPermanent filter = new FilterPermanent();
            filter.add(Predicates.not(new TokenPredicate()));
            switch (choosenType) {
                case ARTIFACT:
                    filter.add(new CardTypePredicate(CardType.ARTIFACT));
                    break;
                case CREATURE:
                    filter.add(new CardTypePredicate(CardType.CREATURE));
                    break;
                case LAND:
                    filter.add(new CardTypePredicate(CardType.LAND));
                    break;
                case NON_AURA_ENCHANTMENT:
                    filter.add(new CardTypePredicate(CardType.ENCHANTMENT));
                    filter.add(Predicates.not(new SubtypePredicate(SubType.AURA)));
                    break;
                default:
                    return false;
            }
            game.informPlayers(player.getLogName() + " chooses " + choosenType + "s to phase out");
            List<UUID> permIds = new ArrayList<>();
            for (Permanent permanent : game.getBattlefield().getActivePermanents(filter, controller.getId(), game)) {
                permIds.add(permanent.getId());
            }
            return new PhaseOutAllEffect(permIds).apply(game, source);
        }
        return false;
    }
}
