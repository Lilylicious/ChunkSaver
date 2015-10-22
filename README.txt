-------------------------------------------
Saving Chunks for Fun and Profit
-------------------------------------------
This mod is intended as an API for primarily mod developers, 
as well as map makers for the minecraft modding community.

========================================

In game commands:

/chunksave <filename>
or
/cs <filename>

The chunk will be stored as an NBT file in the saved chunks directory, 
location in your instance root (same folder as the config folder, etc)
As a modder, call the API method, giving it the file's location in your
project (assets is a good spot for them) as well as the chunk coords you
want the chunk to be placed at. The same file can be used multiple times.

In the future there will be a way for non-modders to import chunks.
In all probability this will involve using filenames to request positions,
as well as a folder to place them in.