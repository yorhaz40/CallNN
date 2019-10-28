import  re
import random


def callnn(path,output, layer = 1):
    # layer control the least call dependency layer number
    file = open(path)
    out  = open(output, "w")
    for seq in file.readlines():
        #seq = "174	isOption:isLongOption,isShortOption, isLongOption:startsWith,substring,indexOf,getMatchingOptions,isEmpty,length,getLongPrefix,  getMatchingOptions:startsWith,keySet,singletonList,stripLeadingHyphens,contains,add,   getLongPrefix:substring,hasLongOption,length,stripLeadingHyphens,  hasLongOption:containsKey,stripLeadingHyphens,   stripLeadingHyphens:startsWith,substring,     isShortOption:startsWith,substring,indexOf,hasShortOption,length,"
        seq = seq.strip()
        num = seq.split("\t")[0]
        content = seq.split("\t")[1]
        pattern = re.compile('\s+')
        content = re.sub(pattern, " ", content)
        calls = content.split(" ")
        caller_callees = {}
        for call in calls:
            caller = call.split(":")[0]
            callees = call.split(":")[1].split(",")
            caller_callees[caller] = callees
        if(len(caller_callees)>10):
            continue
            # res = num + "\t" + "" + "\n"
        if(len(caller_callees) < layer):
            continue
        else:
            methodname = list(caller_callees.keys())[0]
            circlecheck = []
            circlecheck.append(methodname)
            res = methodname + " " + fillInBlanks(caller_callees, caller_callees[methodname],circlecheck)
            res = num + "\t" + res + "\n"
        print(num)
        out.write(res)
    file.close()
    out.close()



def fillInBlanks(caller_callees, callees, circlecheck):
    res = "("
    for callee in callees:
        if callee in caller_callees:

            if callee not in circlecheck:
                circlecheck.append(callee)
                res = res + " " + callee + " " + fillInBlanks(caller_callees,caller_callees[callee],circlecheck)
            else:

                res = res + " " + callee

        else:
            res = res +" " + callee
    circlecheck.pop()
    pattern = re.compile('\s+')
    res = re.sub(pattern, " ", res)
    res = res + " " + ")"
    return res


def makedict(file):
    mydict = {}
    for line in file.readlines():
        num = line.split("\t")[0]
        content = line.split("\t")[1]
        mydict[num]=content
    return mydict



def unify(source, target, output):
    sfile = open(source)
    tfile = open(target)
    ofile = open(output,"w")
    sdict = makedict(sfile)
    tdict = makedict(tfile)
    for num in list(sdict.keys()):
        #num = str(int(num)+1)
        if num in tdict.keys():
            res = num + "\t" + tdict[num]
            ofile.write(res)
    sfile.close()
    tfile.close()
    ofile.close()




def getContent(line):
    l = line.split("\t")
    return l[1]




def divtrain_valid_test(code, comment, seq, api):
    code1 = open(code, "r")
    comment1 = open(comment, "r")
    seq1 = open(seq, "r")
    api1 = open(api, "r")
    res = []
    for (cd, cm, sq, ap) in zip(code1.readlines(), comment1.readlines(), seq1.readlines(), api1.readlines()):
        unit = {}
        unit['code'] = getContent(cd)
        unit['comment'] = getContent(cm)
        unit['seq'] = getContent(sq)
        unit['api'] = getContent(ap)
        #unit['num'] = getNum(cd)
        #if(len(unit['seq'].split(" "))>3):
        res.append(unit)

    code1.close()
    comment1.close()
    seq1.close()
    api1.close()
    random.shuffle(res)
    codetrain = open("train." + code, "w")
    commenttrain = open("train." + comment, "w")
    seqtrain = open("train." + seq, "w")
    apitrain = open("train." + api, "w")

    codetest = open("test." + code, "w")
    commenttest = open("test." + comment, "w")
    seqtest = open("test." + seq, "w")
    apitest = open("test." + api, "w")

    codevalid = open("valid." + code, "w")
    commentvalid = open("valid." + comment, "w")
    seqvalid = open("valid." + seq, "w")
    apivalid = open("valid." + api, "w")

    count = 0
    for l in res:
        count += 1
        if(count<=13000):
            codetest.write(str(count) + "\t" + l['code'])
            commenttest.write(str(count) + "\t" + l['comment'])
            seqtest.write(str(count) + "\t" + l['seq'])
            apitest.write(str(count) + "\t" + l['api'])

        elif(count>13000 and count<26000):
            codevalid.write(str(count) + "\t" + l['code'])
            commentvalid.write(str(count) + "\t" + l['comment'])
            seqvalid.write(str(count) + "\t" + l['seq'])
            apivalid.write(str(count) + "\t" + l['api'])
        else:
            codetrain.write(str(count) + "\t" + l['code'])
            commenttrain.write(str(count) + "\t" + l['comment'])
            seqtrain.write(str(count) + "\t" + l['seq'])
            apitrain.write(str(count) + "\t" + l['api'])
    codetrain.close()
    commenttrain.close()
    seqtrain.close()
    apitrain.close()

    codetest.close()
    commenttest.close()
    seqtest.close()
    apitest.close()

    codevalid.close()
    commentvalid.close()
    seqvalid.close()
    apivalid.close()


def vocabGenerate(path):
    f = open(path,"r")
    dict={}
    pattern = re.compile('(.*token\.)(.*)(\.clean)')
    p = re.match(pattern,path).group(2)
    for line in f.readlines():
        line = line.strip("\n")
        line = line.split(" ")
        i = 1
        while(i<len(line)):
            if line[i] in dict:
                dict[line[i]] += 1
            else:
                dict[line[i]] = 1
            i += 1
    d = sorted(dict.items(), key=lambda d: d[1],reverse=True)

    f2 = open("vocab."+p,"w")
    i = 0
    while(i<len(d)):
        f2.write(d[i][0]+"\n")
        i += 1
    print()
    f2.close()
    f.close()



if __name__ == "__main__":
    code ="token.code.clean"
    comment = "token.nl.clean"
    api = "token.api.clean"
    seq = "token.seq.clean"
    # divtrain_valid_test(code, comment, seq, api)
    # vocabGenerate(code)
    # vocabGenerate(comment)
    # vocabGenerate(api)
    # vocabGenerate(seq)
    # source = "./single_api_new_backup/token.api.clean"
    # target = "./token.nl"
    # output = "./old_large_call_unify/token.nl.clean"
    # unify(source,target,output)

    callnn("seq.data", "formatseq.data")
    # path = "seq.data"
    # output = "callnn.data"
    # callnn(path,output)

    # seq = "174	isOption:isLongOption,isShortOption, isLongOption:startsWith,substring,indexOf,getMatchingOptions,isEmpty,length,getLongPrefix,  getMatchingOptions:startsWith,keySet,singletonList,stripLeadingHyphens,contains,add,   getLongPrefix:substring,hasLongOption,length,stripLeadingHyphens,  hasLongOption:containsKey,stripLeadingHyphens,   stripLeadingHyphens:startsWith,substring,     isShortOption:startsWith,substring,indexOf,hasShortOption,length,"
    # seq = seq.strip()
    # num = seq.split("\t")[0]
    # content = seq.split("\t")[1]
    # pattern = re.compile('\s+')
    # content = re.sub(pattern, " ", content)
    # calls = content.split(" ")
    # caller_callee = {}
    # for call in calls:
    #     caller = call.split(":")[0]
    #     callees = call.split(":")[1].split(",")
    #     caller_callee[caller] = callees
    # print()

