package com.martintintin3.Cardboard;

import com.martintintin3.Cardboard.blocks.BurningTorchBlock;
import com.martintintin3.Cardboard.commands.*;
import com.martintintin3.Cardboard.events.*;
import com.martintintin3.Cardboard.generator.WorldGenerator;

import net.minestom.server.MinecraftServer;
import net.minestom.server.command.CommandManager;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.entity.*;
import net.minestom.server.event.player.*;
import net.minestom.server.extras.PlacementRules;
import net.minestom.server.extras.optifine.OptifineSupport;
import net.minestom.server.instance.*;
import net.minestom.server.instance.block.BlockManager;
import net.minestom.server.network.ConnectionManager;
import net.minestom.server.timer.SchedulerManager;
import net.minestom.server.utils.Position;
import net.minestom.server.world.DimensionType;

import java.util.concurrent.locks.Condition;

public class Server {
    public Position worldSpawnPosition = new Position(0, 40, 0);
    public Boolean enabled = false;

    public MinecraftServer minecraftServer = MinecraftServer.init();
    public final InstanceManager instanceManager = MinecraftServer.getInstanceManager();
    public final InstanceContainer instanceContainer = this.instanceManager.createInstanceContainer(DimensionType.OVERWORLD);
    public final GlobalEventHandler globalEventHandler = MinecraftServer.getGlobalEventHandler();
    public final CommandManager commandManager = MinecraftServer.getCommandManager();
    public final ConnectionManager connectionManager = MinecraftServer.getConnectionManager();
    public final SchedulerManager schedulerManager = MinecraftServer.getSchedulerManager();
    public final BlockManager blockManager = MinecraftServer.getBlockManager();

    public Server(String[] args) {
        // Set the chunk generator
        this.instanceContainer.setChunkGenerator(new WorldGenerator());

        // Util options
        OptifineSupport.enable();
        PlacementRules.init();

        // Register everything
        this.registerCommands();
        this.registerEventHandlers();
    }

    public void start() {
        if(!enabled) {
            this.minecraftServer.start("0.0.0.0", 25565);
            enabled = true;
        }
    }

    public void stop() {
        if(enabled) {
            MinecraftServer.stopCleanly();
            enabled = false;
        }
    }

    public void registerCommands() {
        this.commandManager.register(new GamemodeCommand());
        this.commandManager.register(new StopCommand());
        this.commandManager.register(new SteveCommand(this));
    }

    public void registerEventHandlers() {
        this.globalEventHandler.addListener(EntityAttackEvent.class, event -> EntityAttackEventHandler.handle(event, this));
        this.globalEventHandler.addListener(PlayerLoginEvent.class, event -> PlayerLoginEventHandler.handle(event, this));
        this.globalEventHandler.addListener(PlayerDisconnectEvent.class, event -> PlayerDisconnectEventHandler.handle(event, this));
        this.globalEventHandler.addListener(EntityDeathEvent.class, event -> EntityDeathEventHandler.handle(event, this));
    }

    public void registerCustomBlocks() {
        this.blockManager.registerCustomBlock(new BurningTorchBlock());
    }
}