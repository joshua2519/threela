
# coding: utf-8



import os,shutil

from bs4 import BeautifulSoup
path=r'E:\season\html'
dstpath=r'E:\season\html\nodata'
files=os.listdir(path)
for f in files:
    orifile=os.path.join(path,f)
    dstfile=''
    html=''
    if os.path.isfile(orifile):
        with open(orifile) as d:
            html=d.read()
        soup=BeautifulSoup(html)
        center=soup.select('body')        
        if len(center)>0:
            if center[0].text.strip()==u'資料庫中查無需求資料':
                dstfile=os.path.join(dstpath,f)
                print orifile + '->'+dstfile
                shutil.move(orifile,dstfile)


# In[ ]:



