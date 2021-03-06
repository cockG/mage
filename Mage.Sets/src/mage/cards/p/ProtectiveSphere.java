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
package mage.cards.p;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import mage.Mana;
import mage.ObjectColor;
import mage.abilities.Ability;
import mage.abilities.common.SimpleActivatedAbility;
import mage.abilities.costs.common.PayLifeCost;
import mage.abilities.costs.mana.ManaCostsImpl;
import mage.abilities.effects.PreventionEffectImpl;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.CardType;
import mage.constants.Duration;
import mage.constants.Outcome;
import mage.game.Game;
import mage.game.events.GameEvent;
import mage.game.permanent.Permanent;
import mage.players.Player;
import mage.target.TargetSource;
import mage.util.CardUtil;

/**
 *
 * @author jeffwadsworth
 */
public class ProtectiveSphere extends CardImpl {

    public ProtectiveSphere(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.ENCHANTMENT}, "{2}{W}");

        // {1}, Pay 1 life: Prevent all damage that would be dealt to you this turn by a source of your choice that shares a color with the mana spent on this activation cost.
        Ability ability = new SimpleActivatedAbility(new ProtectiveSphereEffect(), new ManaCostsImpl("{1}"));
        ability.addCost(new PayLifeCost(1));
        this.addAbility(ability);

    }

    public ProtectiveSphere(final ProtectiveSphere card) {
        super(card);
    }

    @Override
    public ProtectiveSphere copy() {
        return new ProtectiveSphere(this);
    }
}

class ProtectiveSphereEffect extends PreventionEffectImpl {

    private final TargetSource target;
    private static Mana manaUsed;
    private static List<ObjectColor> colorsOfChosenSource = new ArrayList<>();

    public ProtectiveSphereEffect() {
        super(Duration.EndOfTurn, Integer.MAX_VALUE, false, false);
        this.staticText = "Prevent all damage that would be dealt to you this turn by a source of your choice that shares a color with the mana spent on this activation cost.";
        this.target = new TargetSource();
    }

    public ProtectiveSphereEffect(final ProtectiveSphereEffect effect) {
        super(effect);
        this.target = effect.target.copy();
    }

    @Override
    public ProtectiveSphereEffect copy() {
        return new ProtectiveSphereEffect(this);
    }

    @Override
    public void init(Ability source, Game game) {
        target.setNotTarget(true);
        target.setRequired(false);
        Player controller = game.getPlayer(source.getControllerId());
        Permanent protectiveSphere = game.getPermanent(source.getSourceId());
        if (controller != null
                && protectiveSphere != null) {
            game.getState().setValue("ProtectiveSphere" + source.getSourceId().toString(), source.getManaCostsToPay().getUsedManaToPay()); //store the mana used to pay
            protectiveSphere.addInfo("MANA USED", CardUtil.addToolTipMarkTags("Last mana used for protective ability: " + source.getManaCostsToPay().getUsedManaToPay()), game);
        }
        this.target.choose(Outcome.PreventDamage, source.getControllerId(), source.getSourceId(), game);
        super.init(source, game);
    }

    @Override
    public boolean replaceEvent(GameEvent event, Ability source, Game game) {
        return true;
    }

    @Override
    public boolean applies(GameEvent event, Ability source, Game game) {
        manaUsed = (Mana) game.getState().getValue("ProtectiveSphere" + source.getSourceId().toString());
        if (super.applies(event, source, game)) {
            if (event.getTargetId().equals(source.getControllerId())
                    && event.getSourceId().equals(target.getFirstTarget())) {
                colorsOfChosenSource = game.getObject(target.getFirstTarget()).getColor(game).getColors();
                if (colorsOfChosenSource.stream().anyMatch((c) -> (manaUsed.getColor(c.getOneColoredManaSymbol()) > 0))) {
                    return true;
                }
            }
        }
        return false;
    }
}
