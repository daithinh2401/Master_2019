# -*- coding: utf-8 -*-

import pandas as pd
import matplotlib.pyplot as plt
from math import sqrt
# define functions
# ******************* NO NEED TO CHANGE *******************
# import data from csv dataset
df = pd.read_csv("C-Lab.csv")
# prints a sample of data from the dataframe
print(df.head())
day1 = list(df.loc[df['timestamp'].map(lambda x: x.startswith('02-01-15'))]['energy'])
day2 = list(df.loc[df['timestamp'].map(lambda x: x.startswith('03-01-15'))]['energy'])
plt.plot(day1, label='Day 1')
plt.plot(day2, label='Day 2')
plt.title("Energy consumption")
plt.xlabel("15-minute interval")
plt.ylabel("Energy (wh)")
plt.legend(loc='upper left')
plt.show()
# *********************************************************


def euclidean_distance(x, y):
    euclidean_distance = 0
    for index in range(0,len(x)):    
        euclidean_distance += (float(x[index])-float(y[index]))**2
    euclidean_distance = sqrt(euclidean_distance)
    return euclidean_distance


def manhattan_distance(x,y): 
    manhattan_distance = 0
    for index in range(0,len(x)):   
        manhattan_distance += abs(x[index]-y[index])
    return manhattan_distance


def minkowski_distance(x,y, n): 
    minkowski_distance = 0
    for index in range(0,len(x)): 
        minkowski_distance += abs(x[index]-y[index])**n
    minkowski_distance = minkowski_distance**(1/n)
    return minkowski_distance


def cosine_similarity(x,y): 
    mau_trai = 0
    mau_phai = 0
    tu = 0
    
    for index in range(0,len(x)):   
        mau_trai += x[index]**2
        mau_phai += y[index]**2
        tu += x[index]*y[index]
    
    return tu / (sqrt(mau_trai) * sqrt(mau_phai))


def jaccard_similarity(x,y):
    intersection_cardinality = len(set.intersection(*[set(x), set(y)]))
    union_cardinality = len(set.union(*[set(x), set(y)]))
    return intersection_cardinality/float(union_cardinality)


# call functions
print(euclidean_distance(day1,day2))
print(manhattan_distance(day1,day2))
print(minkowski_distance(day1,day2,3))
print(cosine_similarity(day1,day2))
print(jaccard_similarity(day1,day2))