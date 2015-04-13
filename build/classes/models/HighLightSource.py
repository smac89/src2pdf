from com.google.common.eventbus import EventBus


class HighLight(EventBus):
    """
    A class that uses the Pygments module to do syntax highlighting
    on given source documents.
    """

    def __init__(self):
        import models.HTMLToPDF as Transformer

        self.observers = []
        self.Transformer = Transformer()

    #         self.Transformer.addProgressEventListener(self)


    def addObserver(self, observer):
        from java.util import Observer

        if not isinstance(observer, Observer):
            raise TypeError("object of type java.util.Observer expected")
        self.observers.append(observer)


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


    def highlight_and_save_files(self, fnames):
        from pygments import highlight
        from org.jsoup import Jsoup

        st = """
        <style type="text/css">
            @font-face {
                font-family: 'Exo';
                src: url('/Exo/Exo-Bold.ttf') format('truetype');
            }
            @page { 
                size: 15in 11in;
                margin: 0.2in 0.2in;
                display: table;
                font-family: 'Exo';
            }
        </style>
        """
        for filename in fnames:
            filestring = self.file_to_api_string(filename)
            if filestring:
                html = highlight(filestring, self.lexer, self.formatter)
                doc = Jsoup.parseBodyFragment(html)
                doc.head().html(st).after(doc.getElementsByTag('div').first())
                self.writehtmltofile(filename + ".pdf", str(doc))


    def progress(self, prog):
        """
        @sig public void progress(com.realobjects.pdfreactor.events.ProgressEvent
        prog)
        """
        for observer in self.observers:
            observer.update(self, (prog.getDescription(), prog.getProgressValue() >> 1))


    def writehtmltofile(self, filename, contents):
        """
        Given a filename and some content, will write the contents to
        the given file
        """
        # implement progress report
        with open(filename, 'wb') as dest:
            self.Transformer.convertDocumentFromContent(contents, dest)


    def file_to_api_string(self, filename):
        """
        Given a src name, tries to open the src
        On success, the src is read and contents returned.
        On failure Null or None is returned
        """
        with open(filename, 'r') as src:
            return "".join(src.readlines())
