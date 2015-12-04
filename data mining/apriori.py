'''
Created by imcczy on 2015/12/3
'''

import sys

def load_data_set():
    return [[1, 2, 5], [2, 4], [2, 3], [1,2,4],[1,3],[2,3],[1,3],[1,2,3,5],[1,2,3]]
#候选集C1
def create_c1(dataSet):
    C1 = []
    for transaction in dataSet:
        for item in transaction:
            if not [item] in C1:
                C1.append([item])

    C1.sort()
    return map(frozenset, C1)
#选出符合最小支持度的项集
def scanD(D, Ck, minSupport):
    ssCnt = {}
    for tid in D:
        for can in Ck:
            if can.issubset(tid):
                if not ssCnt.has_key(can): ssCnt[can]=1
                else: ssCnt[can] += 1
    numItems = float(len(D))
    retList = []
    supportData = {}
    for key in ssCnt:
        support = ssCnt[key]/numItems
        if support >= minSupport:
            retList.insert(0,key)
        supportData[key] = support
    return retList, supportData
#根据L生成C
def apriori_gen(Lk, k):
    retList = []
    lenLk = len(Lk)
    for i in range(lenLk):
        for j in range(i+1, lenLk):
            L1 = list(Lk[i])[:k-2]; L2 = list(Lk[j])[:k-2]
            L1.sort(); L2.sort()
            if L1==L2:
                retList.append(Lk[i] | Lk[j])
    return retList
#调用apriori_gen和scanD
def apriori(dataSet, minSupport = 0.5):
    C1 = create_c1(dataSet)
    D = map(set, dataSet)
    L1, supportData = scanD(D, C1, minSupport)
    L = [L1]
    k = 2
    while (len(L[k-2]) > 0):
        Ck = apriori_gen(L[k-2], k)
        Lk, supK = scanD(D, Ck, minSupport)
        supportData.update(supK)
        L.append(Lk)
        k += 1
    return L, supportData

if __name__ == "__main__":
    data = load_data_set()
    L,support = apriori(data,0.22)
    for set in L:
    	for item in set:
    		print item
    	print '\n'
    