package speedyviewer.core;

public interface ILineProcessor {

	/**
	 * This function is called every time a newline is encountered.
	 * 
	 * This is just a stub implementation, override this to add your
	 * own specific per-line parsing.
	 * 
	 * @param lineBuffer the line, as buffer of bytes
	 * @param length the number of valid characters in the buffer (line length)
	 * @param lineIndex the line index
	 */
	public abstract void processLine(byte[] lineBuffer, int length,
			int lineIndex);

}