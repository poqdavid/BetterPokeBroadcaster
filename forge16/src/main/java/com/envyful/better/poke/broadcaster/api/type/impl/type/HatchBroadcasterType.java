package com.envyful.better.poke.broadcaster.api.type.impl.type;

import com.envyful.better.poke.broadcaster.api.type.impl.AbstractBroadcasterType;
import com.envyful.better.poke.broadcaster.api.util.BroadcasterUtil;
import com.pixelmonmod.pixelmon.api.events.EggHatchEvent;
import com.pixelmonmod.pixelmon.entities.pixelmon.PixelmonEntity;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.pokemon.stats.BattleStatsType;
import com.pixelmonmod.pixelmon.api.pokemon.stats.IVStore;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class HatchBroadcasterType extends AbstractBroadcasterType<EggHatchEvent.Post> {

    public HatchBroadcasterType() {
        super("hatch", EggHatchEvent.Post.class);
    }

    @Override
    protected boolean isEvent(EggHatchEvent.Post event) {
        return true;
    }

    @Override
    protected PixelmonEntity getEntity(EggHatchEvent.Post event) { return event.getPokemon().getOrCreatePixelmon(); }

    @Override
    protected String translateEventMessage(EggHatchEvent.Post event, String line, PixelmonEntity pixelmon, ServerPlayerEntity nearestPlayer) {
        final Pokemon pokemon = event.getPokemon();
        IVStore iVs = pokemon.getIVs();
        float ivHP = iVs.getStat(BattleStatsType.HP);
        float ivAtk = iVs.getStat(BattleStatsType.ATTACK);
        float ivDef = iVs.getStat(BattleStatsType.DEFENSE);
        float ivSpeed = iVs.getStat(BattleStatsType.SPEED);
        float ivSAtk = iVs.getStat(BattleStatsType.SPECIAL_ATTACK);
        float ivSDef = iVs.getStat(BattleStatsType.SPECIAL_DEFENSE);
        int percentage = Math.round(((ivHP + ivDef + ivAtk + ivSpeed + ivSAtk + ivSDef) / 186f) * 100);

        final String sprite_gif_url = "https://play.pokemonshowdown.com/sprites/"
                + (pokemon.isShiny() ? "ani-shiny/" : "ani/")
                + pokemon.getSpecies().getLocalizedName().toLowerCase()
                .replace("nidoranmale","nidoranm")
                .replace("nidoranfemale","nidoranf")
                + (!pokemon.getForm().getName().isEmpty() ? "-" + pokemon.getForm().getName().toLowerCase() : "")
                + ".gif";

        final String sprite_png_url = "https://play.pokemonshowdown.com/sprites/"
                + (pokemon.isShiny() ? "dex-shiny/" : "dex/")
                + pokemon.getSpecies().getLocalizedName().toLowerCase()
                .replace("nidoranmale","nidoranm")
                .replace("nidoranfemale","nidoranf")
                + (!pokemon.getForm().getName().isEmpty() ? "-" + pokemon.getForm().getName().toLowerCase() : "")
                + ".png";

        return line.replace("%player%", (event.getPokemon().getOwnerPlayer() != null ? event.getPokemon().getOwnerPlayer().getName().getString() : ""))
                .replace("%pokemon%", pixelmon.getPokemonName())
                .replace("%species%", pokemon.getSpecies().getLocalizedName())
                .replace("%species_lower%", pokemon.getSpecies().getLocalizedName().toLowerCase())
                .replace("%gender%", pokemon.getGender().getLocalizedName())
                .replace("%unbreedable%", pokemon.isUnbreedable() ? "True" : "False")
                .replace("%nature%", pokemon.getNature().getLocalizedName())
                .replace("%ability%", pokemon.getAbility().getLocalizedName())
                .replace("%untradeable%", pokemon.isUntradeable() ? "True" : "False")
                .replace("%iv_percentage%", String.valueOf(percentage))
                .replace("%iv_hp%", String.valueOf((int) ivHP))
                .replace("%iv_attack%", String.valueOf((int) ivAtk))
                .replace("%iv_defence%", String.valueOf((int) ivDef))
                .replace("%iv_spattack%", String.valueOf((int) ivSAtk))
                .replace("%iv_spdefence%", String.valueOf((int) ivSDef))
                .replace("%iv_speed%", String.valueOf((int) ivSpeed))
                .replace("%shiny%", pokemon.isShiny() ? "True" : "False")
                .replace("%form%", pokemon.getForm().getLocalizedName())
                .replace("%size%", pokemon.getGrowth().getLocalizedName())
                .replace("%custom_texture%", pokemon.getPalette().getLocalizedName())
                .replace("%sprite_gif_url%", sprite_gif_url)
                .replace("%sprite_png_url%", sprite_png_url);
    }

    @Override
    protected ServerPlayerEntity findNearestPlayer(EggHatchEvent.Post event, PixelmonEntity entity, double range) {
        return event.getPlayer();
    }

    @SubscribeEvent
    public void onHatch(EggHatchEvent.Post event) {
        BroadcasterUtil.handleEvent(event);
    }
}
