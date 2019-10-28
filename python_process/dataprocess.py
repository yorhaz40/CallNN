
import re
import json
import random


def cleanCodebyline(code):
    # num = code.split("\t")[0]
    # code = code.split("\t")[1].strip()

    pattern = re.compile('// .*?\s')
    code = re.sub(pattern, "", code)
    pattern = re.compile('/\*.*?\*/')
    code = re.sub(pattern, "", code)
    pattern = re.compile('([^a-zA-Z0-9\s])')
    code = re.sub(pattern, lambda m: " " + m.group(1) + " ", code)
    pattern = re.compile('[0-9]')
    code = re.sub(pattern, " _NUM ", code)
    pattern = re.compile('\".*\"')
    code = re.sub(pattern, " _STR ", code)
    pattern = re.compile("public|private|protect")
    c = re.search(pattern, code)
    if (not (c is None)):
        code = code[c.start():]
    pattern = re.compile('\t')
    code = re.sub(pattern, " ", code)
    pattern = re.compile(r'\s+')
    code = re.sub(pattern, " ", code)
    return  code



def cleanCommentbyline(comment):
    # try to choose the first sentence of comment.
    # num = comment.split("\t")[0]
    # comment = comment.split("\t")[1]

    # clean the @param and @return words.
    pattern = re.compile("@param.*\*")
    comment = re.sub(pattern, "", comment)
    pattern = re.compile("@return.*\*")
    comment = re.sub(pattern, "", comment)



    # 删除(e.g.) i.e
    pattern = re.compile("\(e\.g\..* \)")
    comment = re.sub(pattern, "", comment)
    pattern = re.compile("i\.e.* ")
    comment = re.sub(pattern, "", comment)

    #get the first sentence with split from "."
    comment = comment.split(". ")[0] + "."
    comment = comment.replace("/", "")
    comment = comment.replace("*", "")


    # 将{@link *} 用*代替，如果有#则取#后第一个单词
    pattern = re.compile("({@link)(.*?)(})")
    comment = re.sub(pattern, lambda m: " " + m.group(2) + " ", comment)
    pattern = re.compile("(#)(.*?)(\(.*?\))")
    comment = re.sub(pattern, lambda m: " " + m.group(2) + " ", comment)
    pattern = re.compile("(#)(.*?)(\s.*?)")
    comment = re.sub(pattern, lambda m: " " + m.group(2) + " ", comment)
    # 将{@code *} 用*代替
    pattern = re.compile("({@code)(.*?)(})")
    comment = re.sub(pattern, lambda m: " " + m.group(2) + " ", comment)

    #将<*>x<*>用x代替(注意匹配最里层),然后再删除所有<*>
    pattern = re.compile("(<.*?>)(.*?)(<.*?>)")
    comment = re.sub(pattern, lambda m: " " + m.group(2) + " ", comment)
    pattern = re.compile("<.*?>")
    comment = re.sub(pattern, "", comment)

    #将{*} 删去 将所有@后的内容删去
    pattern = re.compile("{.*?}")
    comment = re.sub(pattern, "", comment)
    pattern = re.compile("@.*")
    comment = re.sub(pattern, "", comment)

    # 将(*) 删去 将所有@后的内容删去
    pattern = re.compile("\(.*?\)")
    comment = re.sub(pattern, "", comment)
    #comment must be longer than 3 words.
    pattern = re.compile('\s+')
    comment = re.sub(pattern, " ", comment)
    comment = comment.strip()
    c = comment.split(" ")
    if (len(c) < 4):
        comment = ""
    # chinese character filter.

    # comment.encode('utf-8').decode()
    hanzi = re.match(u"[\u4e00-\u9fa5]+", comment)

    if hanzi != None:
        print(comment)
        comment = ""

    pattern = re.compile('[^a-zA-Z0-9\s\[\].,\"\'\-]')
    luanma = re.match(pattern,comment)
    if luanma != None:
        print(comment)
        comment = ""

    pattern = re.compile('([^a-zA-Z0-9\s])')
    comment = re.sub(pattern, lambda m: " " + m.group(1) + " ", comment)
    return comment




def test(path):
    file = open(path)
    count = 0
    for line in file.readlines():
        comment = cleanCommentbyline(line)
        # comment = cleanCodebyline(line)
        print(comment)
        count += 1
        if count == 100:
            break


def jsonWrite(code, comment, seq):
    codefile = open(code)
    commentfile = open(comment)
# apifile = open(api)
    seqfile = open(seq)
    file = open("./data.json", "w")
    for cd, cmt, seq in zip(codefile.readlines(), commentfile.readlines(),  seqfile.readlines()):
        num = cd.split("\t")[0]
        seq = seq.split("\t")[1].strip()

        if(seq ==""):
            continue
        cd = cleanCodebyline(cd)
        cmt = cleanCommentbyline(cmt)
        if cmt == "":
            continue
        new_dict = {"id":num, "code":cd, "comment":cmt, "seq":seq}
        json_str = json.dumps(new_dict)
        file.write(json_str+"\n")
    file.close()
    codefile.close()
    commentfile.close()
    seqfile.close()



def randomTest(filepath):
    file = open(filepath)
    dicts = []
    for line in file.readlines():
        dict = json.loads(line)
        dicts.append(dict)
    file.close()

    random.shuffle(dicts)

    count = 0
    for dict in dicts:
        id = dict["id"]
        code = dict["code"]
        comment = dict["comment"]
        api = dict["api"]
        seq = dict["seq"]
        print("id: " + id)
        # print("code: "+ code)
        print("comment:"+comment)
        # print("api: " + api)
        # print("seq: " + seq)
        count += 1
        if(count == 100):
            break



def datawrite(data, type):
    fcode = open(type + ".token.code.clean", "w")
    fcomment = open(type + ".token.nl.clean", "w")
    fapi = open(type + ".token.api.clean", "w")
    fseq = open(type + ".token.seq.clean", "w")

    for dict in data:
        id = dict["id"]
        code = dict["code"]
        comment = dict["comment"]
        api = dict["api"]
        seq = dict["seq"]
        fcode.write(id + "\t" + code + "\n")
        fcomment.write(id + "\t" + comment + "\n")
        fapi.write(id + "\t" + api + "\n")
        fseq.write(id + "\t" + seq + "\n")
    fcode.close()
    fcomment.close()
    fapi.close()
    fseq.close()

def dividetest_valid(path):
    file = open(path)
    dicts = []
    count = 0
    for line in file.readlines():
        count += 1
        dict = json.loads(line)
        dicts.append(dict)
    file.close()
    random.shuffle(dicts)

    v_count = int(count/10)
    test = dicts[0:v_count]
    valid = dicts[v_count:2*v_count]
    train = dicts[2*v_count:]
    print("dict: %d, test: %d, valid: %d, train: %d"%(len(dicts), len(test), len(valid), len(train)))
    datawrite(test, "test")
    datawrite(valid,"valid")
    datawrite(train, "train")






def generate(list, length):
    dict = {}
    for line in list:
        line = line.strip("\n")
        line = line.split(" ")
        i = 1
        while(i<len(line)):
            if line[i] in dict:
                dict[line[i]] += 1
            else:
                dict[line[i]] = 1
            i += 1
    d = sorted(dict.items(), key=lambda d: d[1] , reverse=True)
    if length != 0:
        return d[0:length]
    else:
        return d

def write(d, file):
    f2 = open(file, "w")
    i = 0
    while(i<len(d)):
        f2.write(d[i][0]+"\n")
        i += 1
    f2.close()


def havealook(path = "/home/bohong/文档/seqmodel/TL-CodeSum/data/train/old/train.token.nl"):
    f = open(path,"r")
    dict={}
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
    f.close()
    print(len(d))


def vocabGenerate(path):
    file = open(path,"r")
    code = []
    comment = []
    api = []
    seq = []
    for line in file.readlines():
        dict = json.loads(line)
        code.append(dict["code"])
        comment.append(dict["comment"])
        api.append(dict["api"])
        seq.append(dict["seq"])
    file.close()
    vcode = generate(code,50000)
    vcomment = generate(comment,30000)
    vapi = generate(api, 0)
    vseq = generate(seq, 50000)
    print("vcode: %d, vcomment: %d, vapi: %d, vseq: %d"%(len(vcode), len(vcomment), len(vapi), len(vseq)))
    write(vcode, "vocab.code")
    write(vcomment, "vocab.nl")
    write(vapi,"vocab.api")
    write(vseq,"vocab.seq")



def find(path, pat, type):
    f = open(path)
    for line in f.readlines():
        line = json.loads(line)
        if(re.match(pat, line[type])!=None ):
            print(line[type])



def fliter(data, num):
    file = open(data)
    count = 0
    for line in file.readlines():
        line = json.loads(line)

        if(len(line['api'].split(" ")) > num):
            count += 1
    print(count)



def fiveEncoder(path, out_name, write_num = 5):
    # find the right number from tuple.json according to train/valid/test.token.code.clean
    # write the related_codes into five code files.

    # build a dict to link num and related_codes
    js = open("tuple.json")
    r_codes ={}
    for line in js:
        line = json.loads(line)
        r_codes[line["id"]] = line["related_codes"]
    js.close()

    # prepare five files to write
    files = []
    for i in range(write_num):
        file = open(out_name+".token.code"+str(i), "w")
        files.append(file)

    # open the source file
    source = open(path)
    for line in source:
        num = line.split("\t")[0]
        if num in r_codes.keys():
            related_codes = r_codes[num]
        else:
            related_codes = []
        while(len(related_codes) < write_num):
            related_codes.append("")
        for i in range(write_num):
            # clean codes before write
            res = cleanCodebyline(related_codes[i])
            res = num + "\t" + res + "\n"
            files[i].write(res)

    # close file
    for f in files:
        f.close()



def divideJson(path):

    f = open(path)
    dicts = []
    for line in f:
        line = json.loads(line)
        comment = cleanCommentbyline(line['comment'])
        if comment == "":
            continue
        dicts.append(line)
    random.shuffle(dicts)
    test = open(path+".test","w")
    valid = open(path+".valid","w")
    train = open(path+".train","w")

    v_count = int(len(dicts)/10)
    tes = dicts[0:v_count]
    v = dicts[v_count:2*v_count]
    tra = dicts[2*v_count:]

    for t_ in tes:
        test.write(json.dumps(t_)+"\n")
    for v_ in v:
        valid.write(json.dumps(v_)+"\n")
    for t_ in tra:
        train.write(json.dumps(t_)+"\n")
    f.close()
    test.close()
    valid.close()
    train.close()




def cleanJson(jspath, outpath):
    f = open(jspath)
    out = open(outpath, "w")
    for line in f.readlines():
        line = json.loads(line)
        line['code'] = cleanCodebyline(line['code'])
        r_codes = []
        for c in line['related_codes']:
            r_codes.append(cleanCodebyline(c))
        line['related_codes'] = r_codes
        line['comment'] = cleanCommentbyline(line['comment'])
        out.write(json.dumps(line)+"\n")
    f.close()
    out.close()



def multi_encoders(jspath, out_name, write_num=5):
    # divide_valid_test

    # for train/valid/test generate 5 files

    js = open(jspath)

    commentfile = open(out_name+'.token.nl', "w")
    codefile = open(out_name + ".token.code", "w")
    files = []
    for i in range(write_num):
        file = open(out_name+".token.code"+str(i), "w")
        files.append(file)
    for line in js.readlines():
        line = json.loads(line)
        num = line['id']
        comment = line['comment']
        code = line['code']
        related_codes = line['related_codes']
        while (len(related_codes) < write_num):
            related_codes.append("")
        for i in range(write_num):
            # clean codes before write
            res = cleanCodebyline(related_codes[i])
            res = str(num) + "\t" + res + "\n"
            files[i].write(res)

        codefile.write(str(num) + "\t" + cleanCodebyline(code) + "\n")
        commentfile.write(str(num)+ "\t" + cleanCommentbyline(comment) + "\n")
    # close file
    for f in files:
        f.close()
    codefile.close()
    commentfile.close()
    js.close()




def codeVocab(path):
    f = open(path)
    code = []
    for line in f.readlines():
        line = json.loads(line)
        code.append(line['code'])
        code.extend(line['related_codes'])

    vcode = generate(code, 50000)
    print("vcode: %d" % (len(vcode)))
    write(vcode, path+"vocab.code")

def nlVocab(path):
    f = open(path)
    comment = []
    for line in f.readlines():
        line = json.loads(line)
        comment.append(line['comment'])
    vcomment = generate(comment, 0)
    print("vcomment: %d" % (len(vcomment)))
    write(vcomment, path+"vocab.nl")

def lookjs(num, path = "tuple.json"):
    f = open(path)
    for line in f.readlines():
        line = json.loads(line)
        id = line['id']
        if str(id) == num:
            print(line)

if __name__ == "__main__":
    code = "code.data"
    comment  = "comment.data"
    #api = "api.data"
    seq = "seq.data"
    # test("test.output")
    jsonWrite(code,comment,seq)
    js = "data.json"
    # fliter(js,3)
    # randomTest(js)
    dividetest_valid(js)
    vocabGenerate(js)
    # find("data.json", "", "comment")
    #havealook()
#368516


    # infile = "./fiveencoder/tuple.json"
    # outfile = "./fiveencoder/train"
    # divideJson("./fiveencoder/tuple.json")
    # cleanfile = "./fiveencoder/tuple.clean"

    # cleanJson(infile,cleanfile)
    # codeVocab(cleanfile)
    # nlVocab(cleanfile)
    # multi_encoders(infile,outfile)
    # fiveEncoder(infile,outfile)
#    lookjs("358280")


