# Tokenizer with:
# symbol at start or end of word, include: , . : ; ? ! ' ` " % * ( ) [ ] { } < > / # $ &
# symbol at the middle, include: / ^ & - + *
# Case for symbol at the end of sentence not tokenize ?:
# Special token
# Vietnamese sentence, input: text file, output: list of sentence
# Chuyển câu có dấu sang ko dấu

import re


class Tokenization:

    # Special character at start of word
    special_characters_at_start = r'^[\,\.\:\;\?\!\`\'\"\%\*\(\)\[\]\{\}\<\>\/\#\$\&]'

    # Special character at end of word
    special_characters_at_end = r'[\,\.\:\;\?\!\`\'\"\%\*\(\)\[\]\{\}\<\>\/\#\$\&]$'

    # Special character at middle of word
    special_characters_at_middle = r'\b[\/\^\&\-\+\*]\b'

    # Match a sentence in a text
    sentence_pattern_in_text = r'[A-Z][^\.!?]*[\.!?]'

    # Match all words
    word = r'\w+'

    # Match all non-words
    non_word = "[^\w\s]"

    # Match all digits
    digits = "\d+([\.,_]\d+)+"

    # Match all emails
    email = "(^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\.[a-zA-Z0-9-.]+$)"

    # Match all webs
    web = "^(http[s]?://)?(?:[a-zA-Z]|[0-9]|[$-_@.&+]|[!*\(\),]|(?:%[0-9a-fA-F][0-9a-fA-F]))+$"

    def getTokenize(self, pattern, text):
        return re.findall(pattern, text)

    def generateListNoAccent(self, sentences):
        gen_list = []
        for i in sentences:
            i = self.sub_accent(i)
            gen_list.append(i)
        return gen_list

    def sub_accent(self, sentence):
        INTAB = "ạảãàáâậầấẩẫăắằặẳẵóòọõỏôộổỗồốơờớợởỡéèẻẹẽêếềệểễúùụủũưựữửừứíìịỉĩýỳỷỵỹđẠẢÃÀÁÂẬẦẤẨẪĂẮẰẶẲẴÓÒỌÕỎÔỘỔỖỒỐƠỜỚỢỞỠÉÈẺẸẼÊẾỀỆỂỄÚÙỤỦŨƯỰỮỬỪỨÍÌỊỈĨÝỲỶỴỸĐ"

        OUTTAB = "a" * 17 + "o" * 17 + "e" * 11 + "u" * 11 + "i" * 5 + "y" * 5 + "d" + \
            "A" * 17 + "O" * 17 + "E" * 11 + "U" * 11 + "I" * 5 + "Y" * 5 + "D"

        r = re.compile("|".join(INTAB))
        replaces_dict = dict(zip(INTAB, OUTTAB))

        return r.sub(lambda m: replaces_dict[m.group(0)], sentence)

    def match_sentence(self):
        list_sentence = self.readFile()
        self.writeFile(list_sentence)

    def readFile(self):
        ls = []
        with open('Data/data_input.txt') as f:
            s = f.read()
            ls = re.findall(self.sentence_pattern_in_text, s, flags=re.M)
        return ls

    def writeFile(self, sentences):
        with open('Data/data_output.txt', "w+") as f:
            for i in sentences:
                f.write(i + "\n")

    def getSymbol(self, pattern, text):
        result = []
        for i in text:
            if len(i) > 1:
                i = re.findall(pattern, i)
                if len(i) > 0 and i != None:
                    result.append(i)

        print(result)
                    


text = "% /Ma s/o: ;1860050-35, %K+hoa: 2018-202*1."
tokenization = Tokenization()


list_tokenize = []
# Symbol at start of words:
# print('Symbol at start of words:')
# text = "Trong đó, tên gọi \"trạm thu tiền\" được đề xuất thay thế \"trạm thu giá\" hay \"trạm thu phí\" hiện nay."
text = tokenization.getTokenize(r'[^\s]+', text)
print(text)
tokenization.getSymbol(r'[\w]+', text)
tokenization.getSymbol(tokenization.special_characters_at_start, text)
# print(tokenization.getTokenize(tokenization.special_characters_at_start, text))

# Symbol at end of words:
# print('Symbol at end of words:')
# text = tokenization.getTokenize(r'[^\s]+', text)
# print(text)
# tokenization.getSymbol(r'[\w]+', text)
# tokenization.getSymbol(tokenization.special_characters_at_end, text)
# print(tokenization.getTokenize(tokenization.special_characters_at_end , text))

# Symbol at middle of words:
# print('Symbol at middle of words:')
# text = tokenization.getTokenize(r'[^\s]+', text)
# print(text)
# tokenization.getSymbol(r'[\w]+', text)
# tokenization.getSymbol(tokenization.special_characters_at_middle, text)

# print(tokenization.getTokenize(tokenization.special_characters_at_middle , text))

# Out put list of sentences from text
# tokenization.match_sentence()

# Generate a sentence with no accents
# text = "Nguyễn Đại Thịnh, Khoa Công nghệ thông tin"
# print('Origin sentence: \n' + text)
# print('After generate: \n' + tokenization.sub_accent(text))
