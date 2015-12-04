'''
Created on Nov 7, 2015

@author: imcczy
'''

import numpy as np
import matplotlib.pyplot as plt
from mpl_toolkits.mplot3d import Axes3D
from dbscanner import *
import time

def genDate():
    radius_1 = 20*np.random.random(size=5000)
    radius_2 = 20*np.random.random(size=5000) + 50

    theta1 = np.random.random(size=5000) * np.pi * 2
    psi1 = np.random.random(size=5000) * np.pi

    theta2 = np.random.random(size=5000) * np.pi * 2
    psi2 = np.random.random(size=5000) * np.pi

    x1 = radius_1 * np.sin(psi1) * np.cos(theta1)
    x2 = radius_2 * np.sin(psi2) * np.cos(theta2)

    y1 = radius_1 * np.sin(psi1) * np.sin(theta1)
    y2 = radius_2 * np.sin(psi2) * np.sin(theta2)

    z1 = radius_1 * np.cos(psi1)
    z2 = radius_2 * np.cos(psi2)
    t1 = np.mat([x1,y1,z1]).T.tolist()
    t2 = np.mat([x2,y2,z2]).T.tolist()
    data = t1+t2
    fig = plt.figure()
    ax = plt.axes(projection='3d')
    ax.set_title('initial data')
    ax.plot(x1,y1,z1,'.',label='X1')
    ax.plot(x2,y2,z2,'.',label='X2')
    plt.legend(loc='lower left')
    plt.savefig('initial.png')
    return data

if __name__ == '__main__':
    start = time.time()
    data = genDate()
    dbc = dbscanner()
    dbc.dbscan(data,10,4)
    end = time.time()
    print 'running time =' , end - start