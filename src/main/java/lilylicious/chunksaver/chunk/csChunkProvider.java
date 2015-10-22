package lilylicious.chunksaver.chunk;

import lilylicious.chunksaver.util.NBTHelper;
import lilylicious.chunksaver.util.csLogger;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraft.world.gen.ChunkProviderGenerate;

import java.awt.*;
import java.util.List;

public class csChunkProvider extends ChunkProviderGenerate {

    public static World worldObj;

    public csChunkProvider(World world, long seed, boolean featuresEnabled) {
        super(world, seed, featuresEnabled);
        this.worldObj = world;

    }


    public Chunk loadChunk(int cx, int cy) {
        return this.provideChunk(cx, cy);
    }

    @Override
    public Chunk provideChunk(int chunkX, int chunkZ) {
        Chunk chunk = null;
        Point key = new Point(chunkX, chunkZ);

        if (Chunks.chunkMap.containsKey(key)) {
            chunk = NBTHelper.checkedReadChunkFromNBT__Async(this.worldObj, chunkX, chunkZ, Chunks.chunkMap.get(key));
        }

        if (chunk != null) {
            return chunk;
        } else {
            return super.provideChunk(chunkX, chunkZ);
        }
    }
    

    
}
