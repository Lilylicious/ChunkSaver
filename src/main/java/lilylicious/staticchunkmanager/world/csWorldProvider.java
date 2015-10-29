package lilylicious.staticchunkmanager.world;

import lilylicious.staticchunkmanager.chunk.csChunkProvider;
import lilylicious.staticchunkmanager.chunk.csWorldChunkManager;
import net.minecraft.world.WorldProviderSurface;
import net.minecraft.world.chunk.IChunkProvider;

public class csWorldProvider extends WorldProviderSurface {

    @Override
    public IChunkProvider createChunkGenerator()
    {
        if (this.shouldCreateCP())
        {
            csWorldChunkManager.handleWorldChunks(this.worldObj);

            return new csChunkProvider(this.worldObj, this.worldObj.getSeed(), this.worldObj.getWorldInfo().isMapFeaturesEnabled(), this.getFallbackGenerator());
        }
        else
        {
            return super.createChunkGenerator();
        }
    }

    //Allow a mod which extends our provider to change the fallback without overriding createChunkGenerator
    public String getFallbackGenerator()
    {
        return "normal";
    }

    //The world type name may be different in a extending provider
    public boolean shouldCreateCP()
    {
        return terrainType.getWorldTypeName().equals("SavedChunkWorld");
    }

}
