package lilylicious.staticchunkmanager.api;

import lilylicious.staticchunkmanager.chunk.ChunkRequest;

import java.io.InputStream;
import java.util.jar.JarFile;

public class ExampleRequests {

    public static void requestChunks() {
        InputStream is;

        try {
            is = ExampleRequests.class.getResourceAsStream("/assets/staticchunkmanager/storedchunks/test.nbt");
            csAPI.requestChunk(new ChunkRequest(is, 0, 0, true, true));

            is = ExampleRequests.class.getResourceAsStream("/assets/staticchunkmanager/storedchunks/test.nbt");
            csAPI.requestChunk(new ChunkRequest(is, 0, 1, true, true));

            is = ExampleRequests.class.getResourceAsStream("/assets/staticchunkmanager/storedchunks/test.nbt");
            csAPI.requestChunk(new ChunkRequest(is, 0, 2, true, true));

            is = ExampleRequests.class.getResourceAsStream("/assets/staticchunkmanager/storedchunks/test.nbt");
            csAPI.requestChunk(new ChunkRequest(is, 0, 3, true, true));

            is = ExampleRequests.class.getResourceAsStream("/assets/staticchunkmanager/storedchunks/test.nbt");
            csAPI.requestChunk(new ChunkRequest(is, 0, 4, true, true));

            is = ExampleRequests.class.getResourceAsStream("/assets/staticchunkmanager/storedchunks/test.nbt");
            csAPI.requestChunk(new ChunkRequest(is, 1, 0, true, true));

            is = ExampleRequests.class.getResourceAsStream("/assets/staticchunkmanager/storedchunks/test.nbt");
            csAPI.requestChunk(new ChunkRequest(is, 1, 1, true, true));

            is = ExampleRequests.class.getResourceAsStream("/assets/staticchunkmanager/storedchunks/test.nbt");
            csAPI.requestChunk(new ChunkRequest(is, 1, 2, true, true));

            is = ExampleRequests.class.getResourceAsStream("/assets/staticchunkmanager/storedchunks/test.nbt");
            csAPI.requestChunk(new ChunkRequest(is, 1, 3, true, true));

            is = ExampleRequests.class.getResourceAsStream("/assets/staticchunkmanager/storedchunks/test.nbt");
            csAPI.requestChunk(new ChunkRequest(is, 1, 4, true, true));
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
}
