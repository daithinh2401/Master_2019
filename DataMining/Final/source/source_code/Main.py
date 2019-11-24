# -*- coding: utf-8 -*-

import Settings
from DataLoader import DataLoader
from FileWriter import FileWriter
from NLP import NLP
from FileReader import FileReader
from FeatureExtraction import FeatureExtraction
from Classifier import Classifier
import pickle as cPickle

if __name__ == '__main__':

    # Get from data/test files and store as json
    json_train = DataLoader(dataPath=Settings.DATA_TRAIN_PATH).get_json()
    json_test = DataLoader(dataPath=Settings.DATA_TEST_PATH).get_json()
    FileWriter(filePath=Settings.DATA_TRAIN_JSON, data=json_train).store_json()
    FileWriter(filePath=Settings.DATA_TEST_JSON, data=json_test).store_json()

    # Load from json files
    train_loader = FileReader(filePath=Settings.DATA_TRAIN_JSON)
    test_loader = FileReader(filePath=Settings.DATA_TEST_JSON)


    ######################  Train  ######################
    # data_train = train_loader.read_json()
    # data_test = test_loader.read_json()

    # FeatureExtraction(data=data_train).load_dict()
    # features_train, labels_train = FeatureExtraction(data=data_train).get_data_and_label()
    # features_test, labels_test = FeatureExtraction(data=data_test).get_data_and_label()

    # est = Classifier(features_train=features_train, features_test=features_test, labels_train=labels_train, labels_test=labels_test)
    # est.training()
    # est.save_model(filePath='..//linear_svc_model.pk')
    ######################  Train  ######################


    ######################  Test  ######################
    features_test_2D = []

    test = 'Đây là lần thứ ba sách Việt Nam được triển lãm tại Trung Quốc, đều do nỗ lực của dịch giả Nguyễn Lệ Chi và thương hiệu sách Chibooks "tự thân vận động": 2006 (Hội chợ triển lãm Trung Quốc - ASEAN (CAEXPO), 2016 (Hội sách quốc tế Bắc Kinh) và 2019 (Triển lãm sách Quảng Tây). "Mục tiêu của Chibooks là nỗ lực đưa sách Việt ra với thị trường thế giới" - dịch giả Nguyễn Lệ Chi chia sẻ. Dịp này, các sách tiếng Việt được giới thiệu tại "Quảng Tây thư triển" gồm: Vắt qua những ngàn mây (của Đỗ Quang Tuấn Hoàng), From Zero to Hero (Ray Đoàn Huy -Toàn Juno), Dành cả thanh xuân để chạy theo idol (Hồng Trân); các sách dịch như Ếch (Mạc Ngôn), Cây thạch lựu bói trái anh đào (Lý Nhĩ - tác giả vừa đoạt giải Mao Thuẫn 2019), Mộng đổi đời (Đông Tây)...'
    features_test_1D = FeatureExtraction(data=None).get_dense(test)
    features_test_2D.append(features_test_1D)

    filename = '../linear_svc_model.pk'
    model = cPickle.load(open(filename, 'rb'))
    result = model.predict(features_test_2D)
    print(result)
    ######################  Test  ######################