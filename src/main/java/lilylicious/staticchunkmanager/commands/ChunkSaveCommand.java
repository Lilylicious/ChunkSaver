package lilylicious.staticchunkmanager.commands;

import lilylicious.staticchunkmanager.chunk.Chunks;
import lilylicious.staticchunkmanager.util.MathUtils;
import lilylicious.staticchunkmanager.util.csLogger;
import net.minecraft.client.Minecraft;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.chunk.storage.RegionFile;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ChunkSaveCommand implements ICommand {

    private List aliases;
    private Chunk chunk;
    private IChunkProvider chunkProvider;


    public ChunkSaveCommand() {
        this.aliases = new ArrayList();
        this.aliases.add("chunksave");
        this.aliases.add("cs");
    }


    @Override
    public String getCommandName() {
        return "chunk save";
    }

    @Override
    public String getCommandUsage(ICommandSender icommandsender) {
        return "chunksave <name>";
    }

    @Override
    public List getCommandAliases() {
        return this.aliases;
    }

    @Override
    public void processCommand(ICommandSender icommandsender, String[] astring) {

        ChunkCoordinates coords = icommandsender.getPlayerCoordinates();
        Chunk chunk;

        IntegratedServer intServ = Minecraft.getMinecraft().getIntegratedServer();

        intServ.getConfigurationManager().saveAllPlayerData();
        this.saveAllWorlds(false, intServ);

        int blockX = coords.posX;
        int blockZ = coords.posZ;
        int chunkX = MathUtils.floorDiv(blockX, 16);
        int chunkZ = MathUtils.floorDiv(blockZ, 16);
        int regionX = MathUtils.floorDiv(chunkX, 32);
        int regionZ = MathUtils.floorDiv(chunkZ, 32);
        File worldFolder = new File("saves/" + intServ.getFolderName());
        RegionFile region = new RegionFile(new File(worldFolder, "/region/r." + regionX + "." + regionZ + ".mca"));


        String name = astring.length == 0 ? chunkX + "." + chunkZ : astring[0];

        File folder = new File("savedchunks");
        File file = new File(folder, name + ".nbt");

        if (!folder.isDirectory()) {
            folder.mkdir();
        }

        try {
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            NBTTagCompound nbttagcompound = Chunks.chunkData(chunkX, chunkZ);

            DataOutputStream dataoutputstream = new DataOutputStream(new FileOutputStream(file));
            
            //Changed to non-compressed, make new chunks and test if it's still closing streams,
            CompressedStreamTools.write(nbttagcompound, dataoutputstream);
            csLogger.logInfo("Successfully wrote chunk to nbt file");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean canCommandSenderUseCommand(ICommandSender icommandsender) {
        return true;
    }

    @Override
    public List addTabCompletionOptions(ICommandSender icommandsender, String[] astring) {
        return null;
    }

    @Override
    public boolean isUsernameIndex(String[] astring, int p_82358_2_) {
        return false;
    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }

    protected void saveAllWorlds(boolean p_71267_1_, IntegratedServer intServ) {
        WorldServer[] aworldserver = intServ.worldServers;
        if (aworldserver == null) return; //Forge: Just in case, NPE protection as it has been encountered.
        int i = aworldserver.length;

        for (int j = 0; j < i; ++j) {
            WorldServer worldserver = aworldserver[j];

            if (worldserver != null) {
                if (!p_71267_1_) {
                    csLogger.logInfo("Saving chunks for level \'" + worldserver.getWorldInfo().getWorldName() + "\'/" + worldserver.provider.getDimensionName());
                }

                try {
                    worldserver.saveAllChunks(true, (IProgressUpdate) null);
                } catch (MinecraftException minecraftexception) {
                    csLogger.logWarn(minecraftexception.getMessage());
                }
            }
        }
    }


}
