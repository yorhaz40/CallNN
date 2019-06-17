from nltk.metrics import *

def getContent(line):
    l = line.split("\t")
    return l[1]


def getPrecision(origin, generate):
    forigin = open(origin)
    fgene = open(generate)
    score = 0
    count = 0
    for fo, fg in zip(forigin.readlines(), fgene.readlines()):
        reference = set(getContent(fo).split())
        test = set(fg.split())
        count = count + 1
        if(len(test)==0):
            continue
        score = score + precision(reference,test)
    return score/count


def getRecall(origin, generate):
    forigin = open(origin)
    fgene = open(generate)
    score = 0
    count = 0
    for fo, fg in zip(forigin.readlines(), fgene.readlines()):
        reference = set(getContent(fo).split())
        test = set(fg.split())
        count = count + 1
        if(len(test)==0):
            continue
        score = score + recall(reference,test)

    return score/count

def getFscore(origin, generate):
    forigin = open(origin)
    fgene = open(generate)
    score = 0
    count = 0
    for fo, fg in zip(forigin.readlines(), fgene.readlines()):
        reference = set(getContent(fo).split())
        test = set(fg.split())
        count = count + 1
        if(len(test)==0):
            continue
        score = score + f_measure(reference,test)

    return score/count



if __name__ == "__main__":
    origin = "/home/bohong/文档/seqmodel/TL-CodeSum/data/valid/back/valid.token.nl"

    generate1 = "/home/bohong/文档/seqmodel/TL-CodeSum/data/model/529code2nl/eval/valid.108000.out"
    generate2 = "/home/bohong/文档/seqmodel/TL-CodeSum/data/model/529api/eval/valid.128000.out"
    generate3 = "/home/bohong/文档/seqmodel/TL-CodeSum/data/model/529call/eval/valid.116000.out"
    print("-----------precision-------------")
    print(getPrecision(origin,generate1))
    print(getPrecision(origin, generate2))
    print(getPrecision(origin, generate3))
    print("-----------recall----------------")
    print(getRecall(origin,generate1))
    print(getRecall(origin, generate2))
    print(getRecall(origin, generate3))
    print("-----------f_score---------------")
    print(getFscore(origin,generate1))
    print(getFscore(origin, generate2))
    print(getFscore(origin, generate3))

