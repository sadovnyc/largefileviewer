package speedyviewer.ui;

import java.io.RandomAccessFile;

import org.eclipse.swt.custom.StyledTextContent;
//import org.eclipse.swt.custom.StyledTextEvent;
//import org.eclipse.swt.custom.StyledTextListener;
import org.eclipse.swt.custom.TextChangeListener;
import org.eclipse.swt.custom.TextChangedEvent;
import org.eclipse.swt.custom.TextChangingEvent;
import org.eclipse.swt.widgets.Display;

import speedyviewer.core.AbstractFileIndexer;
import speedyviewer.core.FileIndexer;
import speedyviewer.core.IIndexerListener;

import java.io.File;
import java.util.Vector;

/**
 * This class is a content model for a large file. It does not read the whole
 * file into memory, rather it caches a portion of it around the current
 * position. Note that the model is read only.
 * 
 * A FileIndexer is used to seek into the file when a line needs to
 * be read. The indexer is passed as a parameter in the constructor,
 * the class registers for indexing updates, this makes it
 * possible to have the indexing in a separate thread.
 * 
 */
public class LargeFileContent implements StyledTextContent
{
	//constants
	//private static final int MAX_LINE_LEN = 256;
	private static final String LINE_DELIMITER = "\n";

	//file used to read lines when needed
	private RandomAccessFile rafile;

	//the indexer
	private FileIndexer indexer;

	/**
	 * Number of characters, including line separators
	 */
	private int charCount;

	/**
	 * Number of lines, as should be reported by getLine() according
	 * to StyledTextContent interface contract, i.e. the number of
	 * separators plus one.
	 */
	private int lineCount;
	
	//stores text listeners for event sending
	private Vector<TextChangeListener> textListeners = new Vector<TextChangeListener>();

	/**
	 * Runnable used to send text updates in UI thread.
	 * 
	 * @author fab
	 *
	 */
	private class TextChangingEventSender implements Runnable
	{
		public int addedLines;
		public int addedChars;
		public int charOffset;

		public void run()
		{
			TextChangingEvent event = new TextChangingEvent(LargeFileContent.this);
			event.replaceCharCount = 0;
			event.replaceLineCount = 0;
			event.newCharCount = addedChars;
			event.newLineCount = addedLines;
			event.newText = null; // hope this is not needed...
			event.start = charOffset;
			for (int i = 0; i < textListeners.size(); i++)
				((TextChangeListener) textListeners.elementAt(i)).textChanging(event);
		}
	};

	TextChangingEventSender sendTextChangingEvent = new TextChangingEventSender();
	
	private Runnable sendTextChangedEvent = new Runnable()
	{
		public void run()
		{
			TextChangedEvent event = new TextChangedEvent(LargeFileContent.this);
			for (int i = 0; i < textListeners.size(); i++)
				((TextChangeListener) textListeners.elementAt(i))
						.textChanged(event);
		}
	};

	private IIndexerListener listener = new IIndexerListener()
	{
		private int count = 0;
		public void newIndexChunk(AbstractFileIndexer indexer)
		{
			if(count > 10)
			{
				lineCount = indexer.getLineCount() + 1;
				charCount = indexer.getCharCount();
				Display.getDefault().syncExec(sendTextChangedEvent);
				count = 0;
			}
			else
				count++;
		}

		public void addingIndexChunk(AbstractFileIndexer indexer, int[] chunk, int len, int charCount)
		{
			if(count > 10)
			{
				sendTextChangingEvent.addedChars += charCount;
				sendTextChangingEvent.addedLines += len;
				sendTextChangingEvent.charOffset = LargeFileContent.this.charCount;
				Display.getDefault().syncExec(sendTextChangingEvent);
				sendTextChangingEvent.addedChars = 0;
				sendTextChangingEvent.addedLines = 0;
			}
			else
			{
				sendTextChangingEvent.addedChars += charCount;
				sendTextChangingEvent.addedLines += len;
			}
		}

		public void indexingComplete(AbstractFileIndexer indexer)
		{
			if(count > 0)
			{
				sendTextChangingEvent.charOffset = LargeFileContent.this.charCount;
				Display.getDefault().syncExec(sendTextChangingEvent);
				sendTextChangingEvent.addedChars = 0;
				sendTextChangingEvent.addedLines = 0;
				lineCount = indexer.getLineCount() + 1;
				charCount = indexer.getCharCount();
				Display.getDefault().syncExec(sendTextChangedEvent);
			}
			count = 0;
		}

		@Override
		public boolean isCanceled()
		{
			return false;
		}
	};

	/**
	 * Constructor.
	 * Note that the class will not start indexing, this
	 * has to be done by the client.
	 * 
	 * @param file the file to read from.
	 * @param indexer the indexer to use.
	 */
	public LargeFileContent(File file, FileIndexer indexer)
	{
		try
		{
			//create a random access file to read
			//lines when needed
			rafile = new RandomAccessFile(file, "r");
			this.indexer = indexer;
			this.charCount = indexer.getCharCount();
			this.lineCount = indexer.getLineCount() + 1;

			//register as listener to detect index update
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
		return charCount;
	}

	public String getLine(int lineIndex)
	{
		if (indexer == null)
			return "me manca l'indexer, speta";
		if (lineIndex >= indexer.getLineCount())
			return "";
		String line = "";
		try
		{
			int offset = indexer.getOffsetForLine(lineIndex);
			//offset may still be beyond end of file if the last character in file is
			//a new line
			if(offset < indexer.getCharCount())
			{
				rafile.seek(offset);
				line = rafile.readLine();
			}
		} catch (Exception e)
		{
			// TODO: handle exception
		}

		return line; // new String(lineBuffer) + String.format("(%3d byte)",
						// count);
	}

	public int getLineAtOffset(int offset)
	{
		/*
		 * as per interface contract, if offset is
		 * the number of chars, return the number of lines,
		 * intended as number of line separators.
		 */
		if( offset >= charCount )
			return lineCount - 1;

		return indexer.getLineForOffset(offset);
	}

	/**
	 * Get the line count.
	 * 
	 * @see org.eclipse.swt.custom.StyledTextContent#getLineCount()
	 */
	public int getLineCount()
	{
		return lineCount;
	}

	/**
	 * Get the line delimiter.
	 * 
	 * @see org.eclipse.swt.custom.StyledTextContent#getLineDelimiter()
	 */
	public String getLineDelimiter()
	{
		return LINE_DELIMITER;
	}

	public int getOffsetAtLine(int lineIndex)
	{
		if(lineIndex >= lineCount)
			return charCount;

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
