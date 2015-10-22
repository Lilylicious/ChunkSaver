package lilylicious.staticchunkmanager.world;

import net.minecraft.world.WorldType;
import net.minecraftforge.common.DimensionManager;

public class csWorldType extends WorldType{
    
    public csWorldType() {
        
        super("SavedChunkWorld");
        DimensionManager.unregisterProviderType(0);
        DimensionManager.registerProviderType(0, csWorldProvider.class, true);
    }
}
