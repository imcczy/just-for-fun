# -*- coding: utf-8 -*- 
#加性零均值高斯噪声
#code:myhaspl@myhaspl.com
import cv2
import numpy as np

fn="test2.bmp"
myimg=cv2.imread(fn)
img=myimg

param=30
#灰阶范围
grayscale=256
w=img.shape[1]
h=img.shape[0]
newimg=np.zeros((h,w,3),np.uint8)

for x in xrange(0,h):
    for y in xrange(0,w,2):
        r1=np.random.random_sample()
        r2=np.random.random_sample()
        z1=param*np.cos(2*np.pi*r2)*np.sqrt((-2)*np.log(r1))
        z2=param*np.sin(2*np.pi*r2)*np.sqrt((-2)*np.log(r1))
        newimg[x,y,0]=fxy_val_0
        newimg[x,y,1]=fxy_val_1
        newimg[x,y,2]=fxy_val_2
        newimg[x,y+1,0]=fxy1_val_0
        newimg[x,y+1,1]=fxy1_val_1
        newimg[x,y+1,2]=fxy1_val_2

cv2.imshow('preview',newimg)
cv2.waitKey()
cv2.destroyAllWindows()