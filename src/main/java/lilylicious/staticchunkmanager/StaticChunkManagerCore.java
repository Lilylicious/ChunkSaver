package lilylicious.staticchunkmanager;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import lilylicious.staticchunkmanager.api.ExampleRequests;
import lilylicious.staticchunkmanager.commands.ChunkSaveCommand;
import lilylicious.staticchunkmanager.proxies.IProxy;
import lilylicious.staticchunkmanager.world.csWorldType;
import net.minecraft.world.WorldType;

import java.io.IOException;

@Mod(modid = StaticChunkManagerCore.MODID, name = StaticChunkManagerCore.MODNAME, version = StaticChunkManagerCore.VERSION)
public class StaticChunkManagerCore {
    public static final String MODID = "staticchunkmanager";
    public static final String MODNAME = "Static Chunk Manager";
    public static final String VERSION = "@VERSION@";

    public static WorldType csWorldType;

    @Mod.Instance(MODID)
    public static StaticChunkManagerCore instance;

    @SidedProxy(clientSide = "lilylicious.staticchunkmanager.proxies.ClientProxy", serverSide = "lilylicious.staticchunkmanager.proxies.ServerProxy")
    public static IProxy proxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
    }

    @EventHandler
    public void load(FMLInitializationEvent event) {
        csWorldType = new csWorldType();
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        try {
            ExampleRequests.requestChunks();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void serverLoad(FMLServerStartingEvent event) {
        event.registerServerCommand(new ChunkSaveCommand());
    }


}
