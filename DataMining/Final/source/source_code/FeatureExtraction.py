import os
import Settings
from NLP import NLP
from FileWriter import FileWriter
from FileReader import FileReader
from gensim import corpora, matutils

class FeatureExtraction(object):
    def __init__(self, data):
        self.data = data

    def __build_dictionary(self):
        print('Building dictionary')
        dict_words = []
        i = 0
        for text in self.data:
            i += 1
            print("FeatureExtraction.__build_dictionary(): Step {} / {}".format(i, len(self.data)))
            words = NLP(text = text['content']).get_words_feature()
            dict_words.append(words)
        FileWriter(filePath=Settings.DICTIONARY_PATH).store_dictionary(dict_words)

    def __load_dictionary(self):
        if os.path.exists(Settings.DICTIONARY_PATH) == False:
            self.__build_dictionary()
        self.dictionary = FileReader(Settings.DICTIONARY_PATH).load_dictionary()

    def load_dict(self):
        self.__load_dictionary()

    def __build_dataset(self):
        self.features = []
        self.labels = []
        i = 0
        for d in self.data:
            i += 1
            print("FeatureExtraction.__build_dataset(): Step {} / {}".format(i, len(self.data)))
            self.features.append(self.get_dense(d['content']))
            self.labels.append(d['category'])

    def get_dense(self, text):
        self.__load_dictionary()
        words = NLP(text).get_words_feature()
        # Bag of words
        vec = self.dictionary.doc2bow(words)
        dense = list(matutils.corpus2dense([vec], num_terms=len(self.dictionary)).T[0])
        return dense

    def get_data_and_label(self):
        self.__build_dataset()
        return self.features, self.labels