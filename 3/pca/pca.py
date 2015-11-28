#! /usr/bin/env python
'''
Created on Nov 27, 2015

@author: imcczy
'''

import os, sys
import numpy as np
import PIL.Image as Image

def read_images(path, sz=None):
    c = 0
    X,lable = [], []
    for dirname, dirnames, filenames in os.walk(path):
        for subdirname in dirnames:
            subject_path = os.path.join(dirname, subdirname)
            for filename in os.listdir(subject_path):
                try:
                    im = Image.open(os.path.join(subject_path, filename))
                    im = im.convert("L")
                    
                    if (sz is not None):
                        im = im.resize(sz, Image.ANTIALIAS)
                    X.append(np.asarray(im, dtype=np.uint8))
                    lable.append(c)
                except IOError:
                    print "I/O error({0}): {1}".format(errno, strerror)
                except:
                    print "Unexpected error:", sys.exc_info()[0]
                    raise
            c = c+1
    return [X,lable]

def asRowMatrix(X):
    if len(X) == 0:
        return np.array([])
    mat = np.empty((0, X[0].size), dtype=X[0].dtype)
    for row in X:
        mat = np.vstack((mat, np.asarray(row).reshape(1,-1)))
    return mat

def asColumnMatrix(X):
    if len(X) == 0:
        return np.array([])
    mat = np.empty((X[0].size, 0), dtype=X[0].dtype)
    for col in X:
        mat = np.hstack((mat, np.asarray(col).reshape(-1,1)))
    return mat

def project(W, X, mean=None):
    if mean is None:
        return np.dot(X,W)
    return np.dot(X - mean, W)

def reconstruct(W, Y, mean=None):
    if mean is None:
        return np.dot(Y,W.T)
    return np.dot(Y, W.T) + mean

def compute(eigenvalues):
    a_sum = 0;
    sum = eigenvalues.sum()
    for x in xrange(0,eigenvalues.shape[0]):
        a_sum = a_sum + eigenvalues[x]
        if a_sum / float(sum) >= 0.9:
            break
    return x

def pca(X, num_components=0):
    [n,d] = X.shape
    if (num_components <= 0) or (num_components>n):
        num_components = n
    mean = X.mean(axis=0)
    X = X - mean
    if n>d:
        C = np.dot(X.T,X)
        [eigenvalues,eigenvectors] = np.linalg.eigh(C)
        eigenvectors = np.dot(X,eigenvectors)
        for i in xrange(n):
            eigenvectors[:,i] = eigenvectors[:,i]/np.linalg.norm(eigenvectors[:,i])
    else:
        C = np.dot(X,X.T)
        [eigenvalues,eigenvectors] = np.linalg.eigh(C)
        eigenvectors = np.dot(X.T,eigenvectors)
        for i in xrange(n):
            eigenvectors[:,i] = eigenvectors[:,i]/np.linalg.norm(eigenvectors[:,i])

    idx = np.argsort(-eigenvalues)
    eigenvalues = eigenvalues[idx]
    eigenvectors = eigenvectors[:,idx]

    eigenvalues = eigenvalues[0:num_components].copy()
    eigenvectors = eigenvectors[:,0:num_components].copy()
    return [eigenvalues, eigenvectors, mean]

if __name__ == "__main__":
	if len(sys.argv) != 2:
        print "USAGE: pac.py </path/to/images>"
        sys.exit()
    [X,lable] = read_images(sys.argv[1])
    X = asRowMatrix(X)
    [e_val, e_vec, m] = pca(X)
    e_num = compute(e_val)
    W = e_vec[:,0:e_num]
    out  = open('test','w')
    for i in xrange(0,X.shape[0]):
        new_x = project(W,X[i])
        for item in new_x:
            out.write('%s,' %item)
        out.write('%s\n' %lable[i])
    out.close