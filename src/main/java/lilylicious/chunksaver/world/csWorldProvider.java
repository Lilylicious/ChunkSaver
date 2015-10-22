package lilylicious.chunksaver.world;

import lilylicious.chunksaver.chunk.csChunkProvider;
import net.minecraft.world.WorldProviderSurface;
import net.minecraft.world.chunk.IChunkProvider;

public class csWorldProvider extends WorldProviderSurface {

    @Override
    public IChunkProvider createChunkGenerator()
    {
        return new csChunkProvider(this.worldObj, this.worldObj.getSeed(), this.worldObj.getWorldInfo().isMapFeaturesEnabled());
    }

}
