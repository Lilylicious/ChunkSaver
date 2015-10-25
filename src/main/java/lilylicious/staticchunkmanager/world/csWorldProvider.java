package lilylicious.staticchunkmanager.world;

import lilylicious.staticchunkmanager.chunk.csChunkProvider;
import net.minecraft.world.WorldProviderSurface;
import net.minecraft.world.chunk.IChunkProvider;

public class csWorldProvider extends WorldProviderSurface {

    @Override
    public IChunkProvider createChunkGenerator()
    {
        if (terrainType.getWorldTypeName().equals("SavedChunkWorld"))
        {
            return new csChunkProvider(this.worldObj, this.worldObj.getSeed(), this.worldObj.getWorldInfo().isMapFeaturesEnabled(), "normal");
        }
        else
        {
            return super.createChunkGenerator();
        }
    }

}
