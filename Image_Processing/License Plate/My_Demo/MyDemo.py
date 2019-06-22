import cv2
import numpy as np
from skimage import measure
from skimage.measure import regionprops
import pickle
import imutils
import collections


# Load model for detection
filename = './finalized_model.sav'
model = pickle.load(open(filename, 'rb'))

def preprocessing(imagePath):

        # Step 1a: input an image
        image = cv2.imread(imagePath)

        # Step 1b: Transform to grayscale
        image_gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)

        cv2.imshow('image_gray',image_gray)

        # Step 1c: Remove noise by Bilateral Filter
        blue_image = cv2.bilateralFilter(image_gray,9,75,75)

        cv2.imshow('bilateralFilter',blue_image)

        # Step 1d: Equalize Histogram
        equalize_image = cv2.equalizeHist(blue_image)

        cv2.imshow('equalization histogram',equalize_image)

        # Step 1e: Morphological Operations
        kernel = cv2.getStructuringElement(cv2.MORPH_RECT,(5,5))
        morph_image = cv2.morphologyEx(equalize_image,cv2.MORPH_OPEN,kernel,iterations=20)
        sub_morp_image = cv2.subtract(equalize_image,morph_image)
        ret,thresh_image = cv2.threshold(sub_morp_image,0,255,cv2.THRESH_OTSU)

        cv2.imwrite("output/thresh_image.png", thresh_image)

        return image, thresh_image


def detectPlates(image, thresh_image):
        plates_array = []

        # Step 2a: Get the connection region
        label_image = measure.label(input=thresh_image,neighbors=8)

        # Setp 2b: Gets maximum width, height and minimum width, height that a license plate can be
        plate_dimensions = (0.01*label_image.shape[0], 0.05*label_image.shape[0], 0.015*label_image.shape[1], 0.04*label_image.shape[1])
        min_height, max_height, min_width, max_width = plate_dimensions

        plate_dimensions2 = (0.08*label_image.shape[0], 0.4*label_image.shape[0], 0.15*label_image.shape[1], 0.4*label_image.shape[1])
        min_height2, max_height2, min_width2, max_width2 = plate_dimensions2

        # Step 2c: Detect plate in image
        for region in regionprops(label_image):
                y0, x0, y1, x1 = region.bbox

                region_height = y1 - y0
                region_width = x1 - x0

                # Draw a rectangle in region that can be a plate, the add to plates_array
                if region_height >= min_height and region_height <= max_height and region_width >= min_width and region_width <= max_width and region_width > region_height:
                        plates_array.append(thresh_image[y0:y1, x0:x1])
                        cv2.rectangle(image, (x0, y0), (x1, y1), (0,255,0), 2)

                elif region_height >= min_height2 and region_height <= max_height2 and region_width >= min_width2 and region_width <= max_width2 and region_width >= region_height:
                    plates_array.append(thresh_image[y0:y1, x0:x1])
                    cv2.rectangle(image, (x0, y0), (x1, y1), (0, 255, 0), 2)

        return image, plates_array


def segmentCharacters(plates_array):
        have_character = False
        license_plate = None
        characters = {}
        for plate in plates_array:
                if (have_character == False):
                        license_plate = np.invert(plate)
                        labelled_plate = measure.label(license_plate)

                        character_dimensions = (0.35 * license_plate.shape[0], 0.60 * license_plate.shape[0], 0.05 * license_plate.shape[1], 0.15 * license_plate.shape[1])
                        min_height, max_height, min_width, max_width = character_dimensions

                        for regions in regionprops(labelled_plate):
                                y0, x0, y1, x1 = regions.bbox
                                region_height = y1 - y0
                                region_width = x1 - x0

                                if region_height > min_height and region_height < max_height and region_width > min_width and region_width < max_width:
                                        roi = license_plate[y0:y1, x0:x1]

                                        # resize the characters to 20X20
                                        resized_char = cv2.resize(roi, (20, 20))

                                        # append image to characters array
                                        characters[x0] = resized_char
                                        have_character = True

        characters = collections.OrderedDict(sorted(characters.items()))
        return license_plate, characters


def detectCharacters(characters):
        classification_result = []
        for k, each_character in characters.items():
            # converts it to a 1D array
            each_character = each_character.reshape(1, -1);
            result = model.predict(each_character)
            classification_result.append(result)

        plate_string = ''
        for eachPredict in classification_result:
            plate_string += eachPredict[0]


        return plate_string


def doDetect(isVideo, path):
    # Step 1: Preprocessing
    image, thresh_image = preprocessing(path)

    # Step 2: Detect plates region in image
    image, plates_array = detectPlates(image, thresh_image)

    # Step 3: Segment characters
    license_plate, characters = segmentCharacters(plates_array)

    # Step 4: Detect characters
    result = detectCharacters(characters)
    font = cv2.FONT_HERSHEY_SIMPLEX
    cv2.putText(image, result, (50, image.shape[0] - 100), font, 4, (0, 255, 0), 2, cv2.LINE_AA)

    if result == "":
            print("None")
    else:
        print(result)

    if isVideo == False:
        cv2.imshow("thresh_image", thresh_image)
        cv2.imshow("image", image)
        cv2.imshow("license_plate", license_plate)

        count = 0
        for k, each_character in characters.items():
                cv2.imshow("Characters " + str(count), each_character)
                count += 1

        cv2.waitKey(0)

    return image

def main(isVideo):
    if isVideo == True:
        filename = './video12.mp4'
        # cap = cv2.VideoCapture(filename)
        cap = cv2.VideoCapture(0)

        count = 0
        while cap.isOpened():
            ret,frame = cap.read()
            if ret == True:
                # cv2.imshow('window-name',frame)
                # frame = imutils.rotate(frame, 270)
                cv2.imwrite("output/img.png", frame)
                img = doDetect(True, "output/img.png")
                cv2.imshow('window-name', img)

                if cv2.waitKey(10) & 0xFF == ord('q'):
                    break
            else:
                break
        cap.release()
        cv2.destroyAllWindows()

    else:
        doDetect(False, "IMG/7.jpg")


main(False)
