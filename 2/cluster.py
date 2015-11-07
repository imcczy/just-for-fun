'''
Created on Nov 7, 2015

@author: imcczy
'''

class cluster:
    
    pList = []
    X = []
    Y = []
    Z = []
    name = ""
    
    def __init__(self,name):
        self.name = name
        self.pList = []
        self.X = []
        self.Y = []
        self.Z = []
    
    def addPoint(self,point):
        self.pList.append(point)
        self.X.append(point[0])
        self.Y.append(point[1])
        self.Z.append(point[2])

    def getPoints(self):
        return self.pList
    
    def erase(self):
        self.pList = []
    
    def getX(self):
        return self.X
    
    def getY(self):
        return self.Y
    
    def getZ(self):
        return self.Z

    def has(self,point):
        
        if point in self.pList:
            return True
        
        return False
        
    def printPoints(self):
        print self.name+' Points:'
        print '-----------------'
        print self.pList
        print len(self.pList)
        print '-----------------'
        return len(self.pList)
    
        
        
        