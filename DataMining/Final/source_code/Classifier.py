from sklearn.svm import LinearSVC
from FileWriter import FileWriter
from sklearn.metrics import classification_report

class Classifier(object):
    def __init__(self, features_train = None, labels_train = None, features_test = None, labels_test = None,  estimator = LinearSVC(random_state=0)):
        self.features_train = features_train
        self.features_test = features_test
        self.labels_train = labels_train
        self.labels_test = labels_test
        self.estimator = estimator

    def training(self):
        self.estimator.fit(self.features_train, self.labels_train)
        self.__training_result()

    def save_model(self, filePath):
        FileWriter(filePath=filePath).save_pickle(obj=self.estimator)

    def __training_result(self):
        y_true, y_pred = self.labels_test, self.estimator.predict(self.features_test)
        print(classification_report(y_true, y_pred))