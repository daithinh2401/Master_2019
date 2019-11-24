from FileReader import FileReader
from pyvi import ViTokenizer

import Settings

class NLP(object):
    def __init__(self, text = None):
        self.text = text
        self.__set_stopwords()

    def __set_stopwords(self):
        self.stopwords = FileReader(Settings.STOP_WORDS).read_stopwords()

    def segmentation(self):
        return ViTokenizer.tokenize(self.text)

    def split_words(self):
        text = self.segmentation()
        try:
            return [x.strip(Settings.SPECIAL_CHARACTER).lower() for x in text.split()]
        except TypeError:
            return []

    def get_words_feature(self):
        split_words = self.split_words()
        return [word for word in split_words if word.encode('utf-8') not in self.stopwords]