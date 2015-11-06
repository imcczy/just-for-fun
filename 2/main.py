import numpy as np
import matplotlib.pyplot as plt
from mpl_toolkits.mplot3d import Axes3D
from dbscanner import *

def genDate():
    radius_1 = 20*np.random.random(size=10000)
    radius_2 = 20*np.random.random(size=10000) + 50

    theta = np.random.random(size=10000) * np.pi * 2
    psi = np.random.random(size=10000) * np.pi

    x1 = radius_1 * np.sin(psi) * np.cos(theta)
    x2 = radius_2 * np.sin(psi) * np.cos(theta)

    y1 = radius_1 * np.sin(psi) * np.sin(theta)
    y2 = radius_2 * np.sin(psi) * np.sin(theta)

    z1 = radius_1 * np.cos(psi)
    z2 = radius_2 * np.cos(psi)
    t1 = np.mat([x1,y1,z1]).T.tolist()
    t2 = np.mat([x2,y2,z2]).T.tolist()
    data = t1+t2
    return data

if __name__ == '__main__':
    data = genDate()
    dbc= dbscanner()
    dbc.dbscan(data,30,4)