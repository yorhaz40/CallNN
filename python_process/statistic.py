import  json
def code_statistic(filepath):
    file = open(filepath)
    count = 0
    count_100 = 0
    count_150 = 0
    count_200 = 0
    other = 0
    total = 0
    for line in file.readlines():
        line = json.loads(line)
        line = line['code']
        linelist = line.split(" ")
        length = len(linelist)
        count += 1
        total += length
        if(length<100):
            count_100 += 1
        elif(length>=100 and length <150):
            count_150 += 1
        elif(length>=150 and length<200):
            count_200 += 1
        else:
            other += 1

    avg = total/count
    print("code:\n")
    print("count: %d, length<100: %d, length<150: %d lenght<200: %d length>200 %d \n avg_lenght: %d"%(count,count_100,count_150,count_200,other,avg))


def comment_statistic(filepath):
    file = open(filepath)
    count = 0
    count_10 = 0
    count_50 = 0
    count_100 = 0
    other = 0
    total = 0
    for line in file.readlines():
        line = json.loads(line)
        line = line['comment']
        linelist = line.split(" ")
        length = len(linelist)
        count += 1
        total += length
        if(length<10):
            count_10 += 1
        elif(length>=10 and length <50):
            count_50 += 1
        elif(length>=50 and length<100):
            count_100 += 1
        else:
            other += 1

    avg = total/count
    print("comment:\n")
    print("count: %d, length<10: %d, length<50: %d lenght<100: %d length>200 %d \n avg_lenght: %d"%(count,count_10,count_50,count_100,other,avg))



def seq_statistic(filepath):
    file = open(filepath)
    count = 0
    count_10 = 0
    count_50 = 0
    count_100 = 0
    other = 0
    total = 0
    for line in file.readlines():
        line = json.loads(line)
        line = line['seq']
        linelist = line.split(" ")
        length = len(linelist)
        count += 1
        total += length
        if(length<10):
            count_10 += 1
        elif(length>=10 and length <50):
            count_50 += 1
        elif(length>=50 and length<100):
            count_100 += 1
        else:
            other += 1

    avg = total/count
    print("seq:\n")
    print("count: %d, length<10: %d, length<50: %d lenght<100: %d length>200 %d \n avg_lenght: %d"%(count,count_10,count_50,count_100,other,avg))

def lookjs(num, path = "tuple.json"):
    f = open(path)
    for line in f.readlines():
        line = json.loads(line)
        id = line['id']
        #print(id)
        if id == num:
            print(line)




if __name__ == '__main__':
    codepath = "data.json"
    commentpath ="data.json"
    seq = "data.json"
    code_statistic(codepath)
    comment_statistic(commentpath)
    seq_statistic(seq)
    # file = "/home/bohong/文档/jtags/src/jtags/output_fixseq/tuple.json"
    # lookjs(556625-1,file)