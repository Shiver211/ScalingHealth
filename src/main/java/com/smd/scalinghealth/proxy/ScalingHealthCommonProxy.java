package com.smd.scalinghealth.proxy;

import com.smd.scalinghealth.event.DamageScaling;
import com.smd.scalinghealth.event.DifficultyHandler;
import com.smd.scalinghealth.event.ScalingHealthCommonEvents;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.storage.loot.properties.EntityPropertyManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import com.smd.scalinghealth.config.Config;
import com.smd.scalinghealth.loot.properties.PropertyDifficulty;
import com.smd.scalinghealth.network.NetworkHandler;
import com.smd.scalinghealth.network.message.MessagePlaySound;
import com.smd.scalinghealth.capability.player.PlayerStateCapability;
import com.smd.scalinghealth.capability.player.PlayerStateEventHandler;
import com.smd.scalinghealth.wealth.WealthDropHandler;
import com.smd.scalinghealth.wealth.WealthDropTable;

public class ScalingHealthCommonProxy {
    public void preInit(FMLPreInitializationEvent event) {
        Config.INSTANCE.init(event.getSuggestedConfigurationFile());
        PlayerStateCapability.register();

        EntityPropertyManager.registerProperty(new PropertyDifficulty.Serializer());

        NetworkHandler.init();
        WealthDropTable.INSTANCE.init(event.getModConfigurationDirectory());

        MinecraftForge.EVENT_BUS.register(new ScalingHealthCommonEvents());
        MinecraftForge.EVENT_BUS.register(new PlayerStateEventHandler());
        MinecraftForge.EVENT_BUS.register(DifficultyHandler.INSTANCE);
        MinecraftForge.EVENT_BUS.register(DamageScaling.INSTANCE);
        MinecraftForge.EVENT_BUS.register(WealthDropHandler.INSTANCE);
    }

    public void init(FMLInitializationEvent event) {
        DifficultyHandler.INSTANCE.initPotionMap();
        Config.INSTANCE.save();
    }

    public void postInit(FMLPostInitializationEvent event) {
        WealthDropTable.INSTANCE.reload();
    }

    public void playSoundOnClient(EntityPlayer player, SoundEvent sound, float volume, float pitch) {
        if (player instanceof EntityPlayerMP) {
            NetworkHandler.INSTANCE.sendTo(new MessagePlaySound(sound, volume, pitch), (EntityPlayerMP) player);
        }
    }

    public EntityPlayer getClientPlayer() {
        return null;
    }
}
