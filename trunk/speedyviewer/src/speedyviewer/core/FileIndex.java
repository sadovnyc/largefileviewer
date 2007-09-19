package speedyviewer.core;

import speedyviewer.util.ChunkIntArray;

public class FileIndex
{

	private ChunkIntArray chunkIntArray;

	public FileIndex(int chunkSize)
	{
		chunkIntArray = new ChunkIntArray(chunkSize);
	}

}
