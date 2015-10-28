Unlike normal chunk registration which is impossible to randomize on a per world basis, world chunks can be randomized using whatever method you wish.


To use world chunks you will first need a Forge event handler, adding one is beyond the scope of this guide however there is plenty of information on the Forge wiki among other sources. Once you have your event handler you will need to subscribe to SCMs WorldChunkRegEvent event, the following code will get the job done:

    @SubscribeEvent
    public void chunkReg(WorldChunkRegEvent event)
    {
        String streampath;
        InputStream is;

        streampath = "/assets/staticchunkmanager/storedchunks/bedrockcube.nbt";
        is = csEvents.class.getResourceAsStream(streampath);
        csAPI.requestWorldChunk(new ChunkRequest(is, streampath, 0, 0, false));
    }

As you can see this is much like registering a normal chunk, albeit done in your event handler instead. This event is called when the world is created, just before chunks are provided. Your registered chunks list is then saved to the world folder by SCM in case they are not generated in the initial game session.


While the need for randomization will differ greatly depending on the mod being developed, the following is a basic example of how this could be used:


    @SubscribeEvent
    public void chunkReg(WorldChunkRegEvent event)
    {
        Random rand = new Random();
        String streampath;
        InputStream is;

		int randomnum = rand.nextInt(10);

        streampath = "/assets/staticchunkmanager/storedchunks/labyrinth" + randomnum + ".nbt";
        is = csEvents.class.getResourceAsStream(streampath);
        csAPI.requestWorldChunk(new ChunkRequest(is, streampath, 0, 0, false));
    }


As the event is only called during the initial world creation all data such as the position can be safely randomized. 


Further advanced functionality, such as generating non registered chunks as void instead of vanilla, can be accomplished by creating a dimension provider for your mod which extends SCMs, and overrides the appropriate method.
