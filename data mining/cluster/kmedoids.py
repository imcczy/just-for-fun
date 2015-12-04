import pam
from PIL import Image
import sys

def loadDataSet(PicName):
    im = Image.open(PicName)
    width = im.size[0]
    height = im.size[1]
    dataList = []
    for h in xrange(0,height):
        for w in xrange(0,width):
            pixel = im.getpixel((w, h))[:3]
            dataList.append(pixel)
    return width,height,dataList

def createImage(width,height,color,best_medoids,NewPic):
    newimg = Image.new("RGB",(width,height))
    n = width*height
    data =[(0,0,0)]*n
    for m in best_medoids:
        for item in best_medoids[m]:
            data[item] = color[m]
    newimg.putdata(data)
    newimg.transpose(Image.FLIP_LEFT_RIGHT)
    newimg.save(NewPic)