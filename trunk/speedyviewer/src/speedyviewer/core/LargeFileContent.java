package speedyviewer.core;

import java.io.RandomAccessFile;

import org.eclipse.swt.custom.StyledTextContent;
import org.eclipse.swt.custom.TextChangeListener;
import java.io.File;

/**
 * This class is a content model for a large file.
 * It does not read the whole file into memory, rather it
 * caches a portion of it around the current position.
 * Note that the model is read only.
 * 
 * Cache updating is done through a different thread
 */
public class LargeFileContent implements StyledTextContent {

	static int MAX_LINE_LEN = 256;
	private RandomAccessFile rafile;
	private IndexerThread indexer;
	private 	int[] lineDelimiter = new int[2];
	
	public LargeFileContent(File file, IndexerThread indexerTh){
		try{
			rafile = new RandomAccessFile(file, "r");
			indexer = indexerTh;
		}
		catch (Exception e) {
			// TODO: handle exception
		}
	}
	//TODO un distruttore per chiamare fileInputStream.finalize()
	
	public void addTextChangeListener(TextChangeListener listener) {
		// TODO Auto-generated method stub

	}

	public int getCharCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getLine(int lineIndex) {
		if(indexer == null)
			return "me manca l'indexer, speta";
		if(lineIndex >= indexer.getLineCount())
			return "gnancora pronto";
		String line = "";
		try{
			indexer.getLineDelimiters(lineIndex, lineDelimiter);
			rafile.seek(lineDelimiter[0]);
			line = rafile.readLine();	
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		
		return  line; //new String(lineBuffer) + String.format("(%3d byte)", count);
	}

	public int getLineAtOffset(int offset) {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getLineCount() {
		// TODO Auto-generated method stub
		return 1000000;
	}

	public String getLineDelimiter() {
		return "\n";
	}

	public int getOffsetAtLine(int lineIndex) {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getTextRange(int start, int length) {
		// TODO Auto-generated method stub
		return null;
	}

	public void removeTextChangeListener(TextChangeListener listener) {
		// TODO Auto-generated method stub

	}

	/**
	 * This method has no effect.
	 * 
	 * TODO should it throw an exception?
	 * 
	 * @see org.eclipse.swt.custom.StyledTextContent#replaceTextRange(int, int, java.lang.String)
	 */
	public void replaceTextRange(int start, int replaceLength, String text) {
	}

	/**
	 * This method has no effect.
	 * 
	 * TODO should it throw an exception?
	 * 
	 * @see org.eclipse.swt.custom.StyledTextContent#setText(java.lang.String)
	 */
	public void setText(String text) {
	}
	
	public void setFileAndIndexer(File file, IndexerThread indexerTh)
	{
		try{
			rafile = new RandomAccessFile(file, "r");
			indexer = indexerTh;
		}
		catch (Exception e) {
			// TODO: handle exception
		}
	}

}
