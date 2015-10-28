package lilylicious.staticchunkmanager.events;

import net.minecraft.world.World;
import cpw.mods.fml.common.eventhandler.Event;

public class WorldChunkRegEvent extends Event {

    public final World world;

    //Called before chunks are provided allowing mods to randomize per world
    public WorldChunkRegEvent(World world)
    {
        this.world = world;
    }
}
