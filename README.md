# Human Activity Recognition (HAR)

This project aims to recognize three activities (walking, sitting and standing) using smartphone accelerometer sensor data.

To run this projects there are some pre-requisites that need to be installed in the machine

## Machine Learning Requirements
 Python
 Anaconda Navigator
 Numpy
 Pandas
 Tensorflow v1.13.1

## Android Requirements
 Android Studio 3.4.1
 JDK 9 

## Data Collection
For data collection application run this https://github.com/hasantarek2002/HAR/tree/master/DataCollector android project in android studio. it will generate an apk file on the device that will collect accelerometer sensor data at a rate of 50 Hz.

## Machine Learning
The code for model build and evaluation is in this link https://github.com/hasantarek2002/HAR/tree/master/ML%20Code. After this the Random forest model is selected and using weka this model is converted for android application by running  https://github.com/hasantarek2002/HAR/blob/master/WekaTest/src/RFSaveLoadModelThreeActivity.java file. 

## Activity REcognition
For Activity Recognition application run this https://github.com/hasantarek2002/HAR/tree/master/ActivityRecognition android project in android studio. it will generate an apk file on the device taht will recognize these three activities.

## APK File
1. Data collector application canbe dowloaded from this link https://github.com/hasantarek2002/HAR/blob/master/APK%20File/Data%20collector/app-release.apk
2. Activity Recognition application canbe dowloaded from this link https://github.com/hasantarek2002/HAR/blob/master/APK%20File/Activity%20Recognition/app-release.apk
