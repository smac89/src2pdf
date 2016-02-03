def get_styles():
    from pygments.styles import get_all_styles
    "@sig public java.util.List<java.lang.String> get_styles()"

    ''' Function called to return the styles supported by this application'''
    return list(get_all_styles())


def get_languages():
    from pygments.lexers import _mapping
    "@sig public java.util.List<java.lang.String> get_languages()"

    ''' Function called to return the languages supported by this application'''
    return sorted([value[1] for value in _mapping.LEXERS.itervalues()])
