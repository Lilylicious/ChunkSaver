package lilylicious.chunksaver;

import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import lilylicious.chunksaver.api.csAPI;
import lilylicious.chunksaver.commands.ChunkSaveCommand;
import lilylicious.chunksaver.proxies.IProxy;
import lilylicious.chunksaver.world.csWorldType;
import net.minecraft.world.WorldType;

import java.io.File;

@Mod(modid = ChunkSaverCore.MODID, name = ChunkSaverCore.MODNAME, version = ChunkSaverCore.VERSION)
public class ChunkSaverCore
{
    public static final String MODID = "chunksaver";
    public static final String MODNAME = "Chunk Saver";
    public static final String VERSION = "@VERSION@";

    public static WorldType csWorldType;

    @Mod.Instance(MODID)
    public static ChunkSaverCore instance;

    @SidedProxy(clientSide = "lilylicious.chunksaver.proxies.ClientProxy", serverSide = "lilylicious.chunksaver.proxies.ServerProxy")
    public static IProxy proxy;
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
    }

    @EventHandler
    public void load(FMLInitializationEvent event)
    {
        csWorldType = new csWorldType();
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
    }

    @EventHandler
    public void serverLoad(FMLServerStartingEvent event)
    {
        event.registerServerCommand(new ChunkSaveCommand());
    }
    
    
}
