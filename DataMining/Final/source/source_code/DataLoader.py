from random import randint

import os
from FileReader import FileReader

class DataLoader(object):
    def __init__(self, dataPath):
        self.dataPath = dataPath

    def __get_files(self):
        folders = [self.dataPath + folder + '/' for folder in os.listdir(self.dataPath)]
        class_titles = os.listdir(self.dataPath)
        files = {}
        for folder, title in zip(folders, class_titles):
            files[title] = [folder + f for f in os.listdir(folder)]
        self.files = files

    def get_json(self):
        self.__get_files()
        data = []
        for topic in self.files:
            i = 0
            for file in self.files[topic]:
                content = FileReader(filePath=file).content()
                data.append({
                    'category': topic,
                    'content': content
                })
                if i == 1000:
                    break
                else:
                    i += 1
        return data
    
