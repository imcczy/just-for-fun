'''
Created on Feb 13, 2014

@author: sushant
'''

from cluster import *
import matplotlib.pyplot as plt
from mpl_toolkits.mplot3d import Axes3D

class dbscanner:
    
    dataSet = []
    count = 0
    visited = []
    member = []
    Clusters = []
    
    
    def dbscan(self,D,eps,MinPts):
        self.dataSet = D
        print len(self.dataSet) 
        C = -1
        Noise = cluster('Noise')
        fig = plt.figure()
        ax = plt.axes(projection='3d')
        #sum = 0
        #sum1 =0 

        for point in D:
            #sum1 = sum1 +1
            if point not in self.visited:
                self.visited.append(point)
                NeighbourPoints = self.regionQuery(point,eps)
                
                if len(NeighbourPoints) < MinPts:
                    Noise.addPoint(point)
                else:
                    name = 'Cluster'+str(self.count)
                    C = cluster(name)
                    self.count+=1
                    self.expandCluster(point,NeighbourPoints,C,eps,MinPts)
                    C.printPoints()
                    #sum = sum+C.printPoints()
                    ax.plot(C.getX(),C.getY(),C.getZ(),'o',label=name)
        if len(Noise.getPoints())!=0:
            #sum = sum+Noise.printPoints()
            ax.plot(Noise.getX(),Noise.getY(),Noise.getZ(),'o',label='Noise')
        #print sum,sum1
        plt.legend(loc='lower left')
        plt.show()

                    
                    
            
    
    def expandCluster(self,point,NeighbourPoints,C,eps,MinPts):
        
        C.addPoint(point)
        
        for p in NeighbourPoints:
            if p not in self.visited:
                self.visited.append(p)
                np = self.regionQuery(p,eps)
                if len(np) >= MinPts:
                    for n in np:
                        if n not in NeighbourPoints:
                            NeighbourPoints.append(n)
                    
            for c in self.Clusters:
                if not c.has(p):
                    if not C.has(p):
                        C.addPoint(p)
                        
            if len(self.Clusters) == 0:
                if not C.has(p):
                    C.addPoint(p)
                        
        self.Clusters.append(C)

    def regionQuery(self,P,eps):
        result = []
        for d in self.dataSet:
            if distEclud(d,P)<=eps:
                result.append(d)
        return result

def distEclud(vecA, vecB):
        dist = 0
        for i in range(len(vecA)):
            dist += (vecA[i] - vecB[i])**2
        return(dist**0.5)