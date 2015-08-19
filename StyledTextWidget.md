# Background #

The large viewer needs a widget to display the file, but, as the whole concept is in not reading the file content except the small window that is necessary to show, the text can't be set as a String (could be many GBytes large!).

The SWT StyledText allow to specify a StyledTextContent, which is used by the widget to get a line when it has to be displayed. An implementation of this class can load the line from the file (maybe implementing a cache if necessary) every time.

BUT: The widget (or better the associated Render class) stores two arrays with width and height for each line : for a log that can contain many millions of lines the performance may become unacceptable.

# Proposal #

A stripped down version of StyledText to handle read only text, provided by a StyledTextContent.