import sys
import httplib
import os
import pickle
import time
import pdb



def pickleAndWait(file_name, wait_time):
    pickleFile = open(file_name, 'wb')
    pickle.dump(searchResults, pickleFile)
    pickleFile.close()
    print "Sleeping at", time.strftime('%X %x %Z')
    time.sleep(wait_time)
    print "Awake and continue at", time.strftime('%X %x %Z')


###############################################################################
waitAfter = 50 #http request this many then pickle and wait

searchResults = dict()

peptideList = []
pf = open(sys.argv[1])
for line in pf.readlines():
    peptideList.append(line.strip())
pf.close()

num =  0
for pep in peptideList:
    num += 1
    searchResults[pep] = []
    conn = httplib.HTTPConnection("www.pep2pro.org")
    getValue = "/UniRef100-IL/" + pep + ".txt"
    conn.request("GET", getValue)
    response = conn.getresponse()
    if response.status == 200:
        data = response.read().strip()
        peps = data.split('\n') #there is one line for each returned value.
        searchResults[pep] = dict()
        for v in peps:
            values = v.split('\t')
            searchResults[pep][values[0]] = ''   #['uniprotID'].append(values[0])
            print "P+", num
    else:
        print "ERROR:", response.status, "for sequence", pep
    
    conn.close()

    if num % waitAfter == 0:
        pickleAndWait('pep2proInterim.pkl', 5)

#save our work to this point
pickleAndWait('pep2proInterim.pkl', 0)

num = 0
for k,v in searchResults.iteritems():
    for id in v:
        num += 1
        conn = httplib.HTTPConnection("www.uniprot.org")
        getValue = "/uniref/"+id+".tab"
        conn.request("GET", getValue)
        response = conn.getresponse()
        if response.status == 200:
            searchResults[k][id] = response.read()
            print 'Up+', num
        else:
            print "ERROR:", response.status, "for id", id
        conn.close()

        if num % waitAfter == 0:
            pickleAndWait('resultsInterim.pkl', 0)

#save our work to this point
pickleAndWait('resultsInterim.pkl', 0)


###############################################################################
answer = open(sys.argv[2], 'w')
answer.write("Sequence\tUniRefID\tMember\tEntry name\tStatus\tProtein names\tOrganism\tComponent clusters\tLength")
answer.write(os.linesep)
for k,v in searchResults.iteritems():
    for k2,v2 in v.iteritems():
        vals = v2.split('\n')
        theLine =  k+'\t'+k2+'\t'+vals[1]
        answer.write(theLine)
        answer.write(os.linesep)
answer.close()
