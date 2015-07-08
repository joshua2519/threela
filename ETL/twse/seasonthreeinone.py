
# coding: utf-8

# In[ ]:


import requests,time
import json,csv,os.path,sys,random

yearlist=range(94,102)
comlist=[]
with open('allComList.csv', 'rb') as csvfile:
    reader = csv.reader(csvfile)
    for row in reader:
        comlist.append(row[0])

rs=requests.session()
main=rs.get('http://mops.twse.com.tw/mops/web/t05st29')

for y in yearlist:
    for com in comlist:
        payload={
    'encodeURIComponent':'1',
    'step':'1',
    'firstin':'1',
    'off':'1',
    'keyword4':'',
    'code1':'',
    'TYPEK2':'',
    'checkbtn':'',
    'queryName':'co_id',
    't05st29_c_ifrs':'N',
    't05st30_c_ifrs':'N',
    'TYPEK':'all',
    'isnew':'false',
    'co_id':com,
    'year':str(y)}
        filename='html/'+str(y)+'-'+com+'.txt'
        if(os.path.isfile(filename)):
            #print filename+"is exist"
            continue
        try:
            res=rs.post('http://mops.twse.com.tw/mops/web/ajax_t05st29',data = payload,timeout=10)
            #print res.encoding
            with open(filename,'w') as source:
                source.write(res.text.encode('ISO-8859-1'))
                #print str(y)+'-'+com+'.txt' + ' export!'
            time.sleep(random.randrange(3,5))
        except:
            print filename +" error!"
            continue

            


# In[ ]:



