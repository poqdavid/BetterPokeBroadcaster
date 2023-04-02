package com.envyful.better.poke.broadcaster.api.type.impl.type;

import com.envyful.api.forge.world.UtilWorld;
import com.envyful.better.poke.broadcaster.api.type.impl.AbstractBroadcasterType;
import com.envyful.better.poke.broadcaster.api.util.BroadcasterUtil;
import com.pixelmonmod.pixelmon.api.battles.BattleResults;
import com.pixelmonmod.pixelmon.api.events.battles.BattleEndEvent;
import com.pixelmonmod.pixelmon.api.util.helpers.BiomeHelper;
import com.pixelmonmod.pixelmon.battles.controller.participants.BattleParticipant;
import com.pixelmonmod.pixelmon.battles.controller.participants.WildPixelmonParticipant;
import com.pixelmonmod.pixelmon.entities.pixelmon.PixelmonEntity;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.pokemon.stats.BattleStatsType;
import com.pixelmonmod.pixelmon.api.pokemon.stats.IVStore;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Map;
import java.util.Objects;

public class DefeatBroadcasterType extends AbstractBroadcasterType<BattleEndEvent> {

    public DefeatBroadcasterType() {
        super("defeat", BattleEndEvent.class);
    }

    @Override
    protected boolean isEvent(BattleEndEvent event) {
        PixelmonEntity entity = this.getEntity(event);

        if (entity == null) {
            return false;
        }

        BattleResults result = this.getResult(event, EntityType.PLAYER);

        if (result == null) {
            return false;
        }

        return result == BattleResults.VICTORY;
    }

    public BattleResults getResult(BattleEndEvent event, EntityType<?> entityType) {
        for (Map.Entry<BattleParticipant, BattleResults> entry : event.getResults().entrySet()) {
            if (Objects.equals(entityType, entry.getKey().getEntity().getType())) {
                return entry.getValue();
            }
        }

        return null;
    }

    @Override
    protected PixelmonEntity getEntity(BattleEndEvent event) {
        for (BattleParticipant battleParticipant : event.getResults().keySet()) {
            if (battleParticipant instanceof WildPixelmonParticipant) {
                return (PixelmonEntity)((WildPixelmonParticipant) battleParticipant).getEntity();
            }
        }

        return null;
    }

    @Override
    protected String translateEventMessage(BattleEndEvent event, String line, PixelmonEntity pixelmon, ServerPlayerEntity nearestPlayer) {
        final Pokemon pokemon = pixelmon.getPokemon();
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
                + pixelmon.getSpecies().getLocalizedName().toLowerCase()
                .replace("nidoranmale","nidoranm")
                .replace("nidoranfemale","nidoranf")
                + (!pokemon.getForm().getName().isEmpty() ? "-" + pokemon.getForm().getName().toLowerCase() : "")
                + ".gif";

        final String sprite_png_url = "https://play.pokemonshowdown.com/sprites/"
                + (pokemon.isShiny() ? "dex-shiny/" : "dex/")
                + pixelmon.getSpecies().getLocalizedName().toLowerCase()
                .replace("nidoranmale","nidoranm")
                .replace("nidoranfemale","nidoranf")
                + (!pokemon.getForm().getName().isEmpty() ? "-" + pokemon.getForm().getName().toLowerCase() : "")
                + ".png";

        return line.replace("%nearest_name%", nearestPlayer == null ? "None" : nearestPlayer.getName().getString())
                .replace("%x%", pixelmon.getX() + "")
                .replace("%y%", pixelmon.getY() + "")
                .replace("%z%", pixelmon.getZ() + "")
                .replace("%world%", UtilWorld.getName(pixelmon.level) + "")
                .replace("%pokemon%", pixelmon.getPokemonName())
                .replace("%species%", pixelmon.getSpecies().getLocalizedName())
                .replace("%species_lower%", pixelmon.getSpecies().getLocalizedName().toLowerCase())
                .replace("%level%", pixelmon.getLvl() + "")
                .replace("%gender%", pokemon.getGender().getLocalizedName())
                .replace("%unbreedable%", pokemon.isUnbreedable() ? "True" : "False")
                .replace("%nature%", pokemon.getNature().getLocalizedName())
                .replace("%ability%", pokemon.getAbility().getLocalizedName())
                .replace("%untradeable%", pokemon.isUntradeable() ? "True" : "False")
                .replace("%iv_percentage%", percentage + "")
                .replace("%iv_hp%", ((int) ivHP) + "")
                .replace("%iv_attack%", ((int) ivAtk) + "")
                .replace("%iv_defence%", ((int) ivDef) + "")
                .replace("%iv_spattack%", ((int) ivSAtk) + "")
                .replace("%iv_spdefence%", ((int) ivSDef) + "")
                .replace("%iv_speed%", ((int) ivSpeed) + "")
                .replace("%shiny%", pokemon.isShiny() ? "True" : "False")
                .replace("%form%", pixelmon.getForm().getLocalizedName())
                .replace("%size%", pokemon.getGrowth().getLocalizedName())
                .replace("%custom_texture%", pixelmon.getPalette().getLocalizedName())
                .replace("%biome%", BiomeHelper.getLocalizedBiomeName(pixelmon.level.getBiome(pixelmon.blockPosition())).getString())
                .replace("%sprite_gif_url%", sprite_gif_url)
                .replace("%sprite_png_url%", sprite_png_url);
    }

    @Override
    public ServerPlayerEntity findNearestPlayer(BattleEndEvent event, PixelmonEntity entity, double range) {
        return (ServerPlayerEntity) entity.level.getNearestPlayer(entity, range);
    }

    @SubscribeEvent
    public void onBattleEnd(BattleEndEvent event) {
        BroadcasterUtil.handleEvent(event);
    }
}
