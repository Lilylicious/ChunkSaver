package lilylicious.chunksaver.commands;

import lilylicious.chunksaver.chunk.Chunks;
import lilylicious.chunksaver.util.csLogger;
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

import java.io.DataInputStream;
import java.io.File;
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

        int blockX = coords.posX;
        int blockZ = coords.posZ;
        int chunkX = Math.floorDiv(blockX, 16);
        int chunkZ = Math.floorDiv(blockZ, 16);
        int regionX = Math.floorDiv(chunkX, 32);
        int regionZ = Math.floorDiv(chunkZ, 32);
        File worldFolder = new File("saves\\" + intServ.getFolderName());
        RegionFile region = new RegionFile(new File(worldFolder, "\\region\\r." + regionX + "." + regionZ + ".mca"));


        String name = astring.length == 0 ? chunkX + "." + chunkZ : astring[0];

        File folder = new File("saved chunks");
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
            CompressedStreamTools.write(nbttagcompound, file);
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
}
