# Word segmentation
# Using Vietnamese dictionary

# Reference: https://github.com/deepai-solutions/core_nlp


import re
from Ex1 import Tokenization


class WordSegmentation(Tokenization):

    def __init__(self):
        # super(WordSegmentation, self).__init__()
        self.bi_grams = []
        self.tri_grams = []
        self.bi_grams_path = 'Data/bi_grams.txt'
        self.tri_grams_path = 'Data/tri_grams.txt'

    def loadDictToArray(self):
        with open(self.bi_grams_path) as f:
            s = f.read()
            s = s.replace(" \'", "")
            self.bi_grams = re.findall(r'[\w+ ]+|[^\{\}\,\']', s, re.UNICODE)
        
        with open(self.tri_grams_path) as f:
            s = f.read()
            s = s.replace(" \'", "")
            self.tri_grams = re.findall(r'[\w+ ]+|[^\{\}\,\']', s, re.UNICODE)


    def getFromArray(self, sentence):
        sentences_array = self.segment_sentence_to_array(sentence)
        result = []
        done = False
        array_len = len(sentences_array)
        current_sentence_index = 0
        while (current_sentence_index < array_len and not done):
            curr_word = sentences_array[current_sentence_index]

            # End of array
            if current_sentence_index >= array_len - 1:
                result.append(curr_word)
                done = True
            else:
                if current_sentence_index >= array_len - 2:
                    next_word = sentences_array[current_sentence_index + 1]
                    bi_words = curr_word.lower() + ' ' + next_word.lower()
                    if bi_words in self.bi_grams:
                        result.append(bi_words)
                        current_sentence_index += 2
                    else:
                        result.append(curr_word)
                        current_sentence_index += 1
                else:
                    second_word = sentences_array[current_sentence_index + 1]
                    third_word = sentences_array[current_sentence_index + 2]
                    bi_words = curr_word.lower() + ' ' + second_word.lower()
                    tri_words = curr_word.lower() + ' ' + second_word.lower() + ' ' + third_word.lower()
                    if tri_words in self.tri_grams:
                        result.append(tri_words)
                        current_sentence_index += 3
                    elif bi_words in self.bi_grams:
                        result.append(bi_words)
                        current_sentence_index += 2
                    else:
                        result.append(curr_word)
                        current_sentence_index += 1 
        return result


    def segment_sentence_to_array(self, text):
        patterns = []
        patterns.extend([self.web])
        patterns.extend([self.email])
        patterns.extend([self.digits])
        patterns.extend([self.non_word])
        patterns.extend([self. word])
        patterns = "(" + "|".join(patterns) + ")"
        tokens = re.findall(patterns, text, re.UNICODE)
        
        return [token[0] for token in tokens]

    def appendSegmentArray(self, lst):
        return lst
        # result = ""
        # for l in lst:
        #     l = l.replace(" ", "_")
        #     result += l + " "
        # return result
    

word_segmentation = WordSegmentation()

word_segmentation.loadDictToArray()
lst = word_segmentation.getFromArray("Bộ Giao thông Vận tải đang lấy ý kiến vào dự thảo thông tư về \"hoạt động của trạm thu tiền dịch vụ sử dụng đường bộ\", thay thế cho thông tư 49.")

newList = word_segmentation.appendSegmentArray(lst)
print(newList)