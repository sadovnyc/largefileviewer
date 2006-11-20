package speedyviewer.core;

import java.io.RandomAccessFile;

import org.eclipse.swt.custom.StyledTextContent;
//import org.eclipse.swt.custom.StyledTextEvent;
//import org.eclipse.swt.custom.StyledTextListener;
import org.eclipse.swt.custom.TextChangeListener;
import org.eclipse.swt.custom.TextChangedEvent;
import org.eclipse.swt.custom.TextChangingEvent;
import org.eclipse.swt.widgets.Display;

import java.io.File;
import java.util.Vector;

/**
 * This class is a content model for a large file. It does not read the whole
 * file into memory, rather it caches a portion of it around the current
 * position. Note that the model is read only.
 * 
 * Cache updating is done through a different thread
 */
public class LargeFileContent implements StyledTextContent
{
	//constants
	private static final int MAX_LINE_LEN = 256;
	private static final String LINE_DELIMITER = "\n";

	//file used to read lines when needed
	private RandomAccessFile rafile;

	private FileIndexer indexer;

	private int[] lineDelimiter = new int[2];

	Vector textListeners = new Vector(); // stores text listeners for event
											// sending

	private Runnable sendTextSetEvent = new Runnable()
	{
		public void run()
		{
			TextChangedEvent event = new TextChangedEvent(LargeFileContent.this);
			for (int i = 0; i < textListeners.size(); i++)
				((TextChangeListener) textListeners.elementAt(i))
						.textSet(event);
		}
	};

	private IIndexerListener listener = new IIndexerListener()
	{
		public void newIndexChunk(FileIndexer indexer)
		{
			Display.getDefault().syncExec(sendTextSetEvent);
		}
	};

	public LargeFileContent(File file, FileIndexer indexer)
	{
		try
		{
			rafile = new RandomAccessFile(file, "r");
			this.indexer = indexer;
			indexer.addListener(listener);
		} catch (Exception e)
		{
			// TODO: handle exception
		}
	}

	// TODO un distruttore per chiamare fileInputStream.finalize()

	@SuppressWarnings("unchecked")
	public void addTextChangeListener(TextChangeListener listener)
	{
		textListeners.addElement(listener);
	}

	public int getCharCount()
	{
		return indexer.getCharCount();
	}

	public String getLine(int lineIndex)
	{
		if (indexer == null)
			return "me manca l'indexer, speta";
		if (lineIndex >= indexer.getLineCount())
			return "gnancora pronto";
		String line = "";
		try
		{
			rafile.seek(indexer.getOffsetForLine(lineIndex));
			line = rafile.readLine();

		} catch (Exception e)
		{
			// TODO: handle exception
		}

		return line; // new String(lineBuffer) + String.format("(%3d byte)",
						// count);
	}

	public int getLineAtOffset(int offset)
	{
		return indexer.getLineForOffset(offset);
	}

	public int getLineCount()
	{
		return indexer.getLineCount();
	}

	public String getLineDelimiter()
	{
		return LINE_DELIMITER;
	}

	public int getOffsetAtLine(int lineIndex)
	{
		return indexer.getOffsetForLine(lineIndex);
	}

	public String getTextRange(int start, int length)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public void removeTextChangeListener(TextChangeListener listener)
	{
		textListeners.remove(listener);
	}

	/**
	 * This method has no effect.
	 * 
	 * TODO should it throw an exception?
	 * 
	 * @see org.eclipse.swt.custom.StyledTextContent#replaceTextRange(int, int,
	 *      java.lang.String)
	 */
	public void replaceTextRange(int start, int replaceLength, String text)
	{
	}

	/**
	 * This method has no effect.
	 * 
	 * TODO should it throw an exception?
	 * 
	 * @see org.eclipse.swt.custom.StyledTextContent#setText(java.lang.String)
	 */
	public void setText(String text)
	{
	}

	public void setFileAndIndexer(File file, FileIndexer indexer)
	{
		try
		{
			rafile = new RandomAccessFile(file, "r");
			this.indexer = indexer;
		} catch (Exception e)
		{
			// TODO: handle exception
		}
	}

}
