package lilylicious.staticchunkmanager.chunk;

import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkProviderGenerate;

import java.awt.*;
import java.io.IOException;
import java.util.Arrays;

public class csChunkProvider extends ChunkProviderGenerate {

    public static World worldObj;
    public String fallback;

    public csChunkProvider(World world, long seed, boolean featuresEnabled, String fallbackChunk) {
        super(world, seed, featuresEnabled);
        this.worldObj = world;
        this.fallback = fallbackChunk;

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
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (chunk != null) {
            return chunk;
        } else {
            if (fallback.equals("void")) {
                return provideVoidChunk(chunkX, chunkZ);
            } else {
                return normalChunk;
            }
        }
    }

    public Chunk provideVoidChunk(int chunkX, int chunkZ) {
        Chunk voidChunk = new Chunk(this.worldObj, chunkX, chunkZ);
        Arrays.fill(voidChunk.getBiomeArray(), (byte)0);
        voidChunk.generateSkylightMap();
        return voidChunk;
    }

    public Chunk requestNormal(int cx, int cy) {
        return super.provideChunk(cx, cy);
    }


}
