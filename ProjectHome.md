Viewer to show text files without loading the whole content in memory.

# Description #

The project will provide:

  * an indexer that scans the text file and creates an index mapping line numbers to file positions.
  * a model of the file content. The model uses the indexer to provide methods to access the file content on a line basis.
  * an eclipse text viewer (using a simple SWT StyledWidget) which uses the model to retrieve the text to be displayed.

# Update site #

Use the following URL as update site

http://largefileviewer.googlecode.com/svn/trunk/speedyviewer-update-site