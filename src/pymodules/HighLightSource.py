from java.util import Observable
from java.util.concurrent import Executors

from com.google.common.eventbus import AsyncEventBus
import models.AsynchronousConversionEvent as AsyncEvent
import models.AsynchronousConversionEvent.ACTION as ACTION


class HighLight(Observable):
    """
    A class that uses the Pygments module to add syntax highlighting
    on given source documents.
    """

    def __init__(self):
        self.changeUpdate = AsyncEventBus("HighLight", Executors.newCachedThreadPool())


    def addObserver(self, observer):
        import interfaces.ProgressListener as ProgressListener

        if not isinstance(observer, ProgressListener):
            raise TypeError("object of type interfaces.ProgressListener expected")
        self.changeUpdate.register(observer)


    def setComponents(self, **data):
        from pygments.formatters.html import HtmlFormatter
        from pygments.lexers import get_lexer_by_name

        options = dict(
            [('style', data.get('style', 'colorful')),
             ('linenos', 'inline'),
             ('noclasses', True),
             ('prestyles', 'margin:0;'),
             ('cssstyles', ("overflow: auto;"
                            "width: auto;"
                            "border:double black;"
                            "border-width:.1em .1em .1em .2em;"
                            "padding:.2em .2em;"))])

        self.lexer = get_lexer_by_name(data.get('lexer', 'text'))
        self.formatter = HtmlFormatter(**options)


    def highlight_files(self, fnames):
        from pygments import highlight
        from org.jsoup import Jsoup

        st = """
        <style type="text/css">
            @page { 
                size: 10in 11in;
                position: relative;
                margin: 0.2in 0.2in;
                display: inline-table;
            }
        </style>
        """
        for filename in fnames:
            self.changeUpdate.post(AsyncEvent(self,
                                              ACTION.FILEREAD, "Reading file: %s - 1/5" % filename))
            filestring = self.file_to_string(filename)

            self.changeUpdate.post(AsyncEvent(self,
                                              ACTION.CONVERTTOHTML, "Hiliting file: %s - 2/5" % filename))
            html = highlight(filestring, self.lexer, self.formatter)

            self.changeUpdate.post(AsyncEvent(self,
                                              ACTION.CLEANHTML, "Cleaning the HTML 3/5"))
            doc = Jsoup.parseBodyFragment(html)
            doc.head().html(st).after(doc.getElementsByTag('div').first())

            yield doc


    def file_to_string(self, filename):
        """
        Given a src name, tries to open the src
        On success, the src is read and contents returned.
        On failure Null or None is returned
        """
        with open(filename, 'r') as src:
            return "".join(src.readlines())
