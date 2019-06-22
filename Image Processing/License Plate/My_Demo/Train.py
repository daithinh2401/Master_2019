import os
import numpy as np
from sklearn.svm import SVC
from sklearn.model_selection import cross_val_score
from sklearn.externals import joblib
from skimage.io import imread
from skimage.filters import threshold_otsu
import cv2

letters = [
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D',
            'E', 'F', 'G', 'H', 'J', 'K', 'L', 'M', 'N', 'P', 'Q', 'R', 'S', 'T',
            'U', 'V', 'W', 'X', 'Y', 'Z'
        ]

def preprocessing(imagePath):

        # Step 1a: input an image
        image = cv2.imread(imagePath)

        image = cv2.resize(image, (20, 20))

        # Step 1b: Transform to grayscale
        image_gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)

        # Step 1c: Remove noise by Bilateral Filter
        blue_image = cv2.bilateralFilter(image_gray,9,75,75)

        # Step 1d: Equalize Histogram
        equalize_image = cv2.equalizeHist(blue_image)

        # Step 1e: Morphological Operations
        kernel = cv2.getStructuringElement(cv2.MORPH_RECT,(5,5))
        morph_image = cv2.morphologyEx(equalize_image,cv2.MORPH_OPEN,kernel,iterations=20)
        sub_morp_image = cv2.subtract(equalize_image,morph_image)
        ret,thresh_image = cv2.threshold(sub_morp_image,0,255,cv2.THRESH_OTSU)

        thresh_image = np.invert(thresh_image)
        return image, thresh_image

def read_training_data(training_directory):
    image_data = []
    target_data = []
    for each_letter in letters:
        for each in range(10):
            image_path = os.path.join(training_directory, each_letter, each_letter + '_' + str(each) + '.jpg')
            image, thresh_image = preprocessing(image_path)
            flat_bin_image = thresh_image.reshape(-1)
            image_data.append(flat_bin_image)
            target_data.append(each_letter)

    return (np.array(image_data), np.array(target_data))

def cross_validation(model, num_of_fold, train_data, train_label):
    accuracy_result = cross_val_score(model, train_data, train_label,cv=num_of_fold)
    print("Cross Validation Result for ", str(num_of_fold), " -fold")

    print(accuracy_result * 100)


# training_dataset_dir = os.path.join(current_dir, 'train')
print('reading data')
training_dataset_dir = './train20X20'
image_data, target_data = read_training_data(training_dataset_dir)
print('reading data completed')

# the kernel can be 'linear', 'poly' or 'rbf'
# the probability was set to True so as to show
# how sure the model is of it's prediction
svc_model = SVC(kernel='linear', probability=True)

cross_validation(svc_model, 10, image_data, target_data)

print('training model')

# train the model with all the input data
svc_model.fit(image_data, target_data)


import pickle
print("model trained.saving model..")
filename = './finalized_model.sav'
pickle.dump(svc_model, open(filename, 'wb'))
print("model saved")