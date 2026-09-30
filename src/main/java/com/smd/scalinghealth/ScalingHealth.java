package com.smd.scalinghealth;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import com.smd.scalinghealth.command.CommandRecalculate;
import com.smd.scalinghealth.command.CommandScalingHealth;
import com.smd.scalinghealth.event.DifficultyHandler;
import com.smd.scalinghealth.init.ModItems;
import com.smd.scalinghealth.proxy.ScalingHealthCommonProxy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import java.util.Random;

@Mod(modid = Tags.MOD_ID,
        name = Tags.MOD_NAME,
        version = Tags.VERSION,
        guiFactory = "com.smd.scalinghealth.gui.GuiFactoryScalingHealth")

public class ScalingHealth {

    public static final String GAME_RULE_DIFFICULTY = "ScalingHealthDifficulty";

    public static final Random random = new Random();

    public static final Logger LOGGER = LogManager.getLogger(Tags.MOD_NAME);

    public static final CreativeTabs creativeTab = new CreativeTabs(Tags.MOD_ID) {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(ModItems.heart);
        }
    };

    @SidedProxy(clientSide = "com.smd.scalinghealth.proxy.ScalingHealthClientProxy",
                serverSide = "com.smd.scalinghealth.proxy.ScalingHealthCommonProxy")

    public static ScalingHealthCommonProxy proxy;

    public static boolean isDifficultyRuleEnabled(World world) {
        if (world == null || world.isRemote) {
            return true;
        }
        if (!world.getGameRules().hasRule(GAME_RULE_DIFFICULTY)) {
            return true;
        }
        return world.getGameRules().getBoolean(GAME_RULE_DIFFICULTY);
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit(event);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }

    @EventHandler
    public void onServerLoad(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandScalingHealth());
        event.registerServerCommand(new CommandRecalculate());
    }

    @EventHandler
    public void onServerStarted(FMLServerStartedEvent event) {
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        if (server != null && server.worlds != null) {
            for (WorldServer world : server.worlds) {
                if (world != null && !world.getGameRules().hasRule(GAME_RULE_DIFFICULTY)) {
                    world.getGameRules().addGameRule(GAME_RULE_DIFFICULTY, "true", GameRules.ValueType.BOOLEAN_VALUE);
                }
            }
        }
    }

    @EventHandler
    public void onServerStopped(FMLServerStoppedEvent event) {
        DifficultyHandler.INSTANCE.clearPendingEntities();
    }

}
