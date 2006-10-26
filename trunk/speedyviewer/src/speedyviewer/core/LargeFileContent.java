package speedyviewer.core;

import org.eclipse.swt.custom.StyledTextContent;
import org.eclipse.swt.custom.TextChangeListener;

/**
 * This class is a content model for a large file.
 * It does not read the whole file into memory, rather it
 * caches a portion of it around the current position.
 * Note that the model is read only.
 * 
 * Cache updating is done through a different thread
 */
public class LargeFileContent implements StyledTextContent {

	public void addTextChangeListener(TextChangeListener listener) {
		// TODO Auto-generated method stub

	}

	public int getCharCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getLine(int lineIndex) {
		// TODO Auto-generated method stub
		return String.format("ciccio%6d", lineIndex);
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

}
