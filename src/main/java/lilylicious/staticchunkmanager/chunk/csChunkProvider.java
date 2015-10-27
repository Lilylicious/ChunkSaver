package lilylicious.staticchunkmanager.chunk;

import lilylicious.staticchunkmanager.util.csLogger;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkProviderGenerate;

import java.awt.*;
import java.io.EOFException;
import java.io.IOException;

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
        Chunk normalChunk = super.provideChunk(chunkX, chunkZ);
        Point key = new Point(chunkX, chunkZ);

        try {
            if (Chunks.chunkMap.containsKey(key)) {
                chunk = Chunks.chunkMap.get(key).readChunk(this.worldObj, normalChunk);
            }
        } catch (EOFException e) {
            try{
                csLogger.logInfo("EOFException");
            Chunks.chunkMap.get(key).inStream.close();
            } catch (IOException k) {
                k.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (chunk != null) {
            return chunk;
        } else {
            return normalChunk;
        }
    }

    public Chunk requestNormal(int cx, int cy) {
        return super.provideChunk(cx, cy);
    }


}
