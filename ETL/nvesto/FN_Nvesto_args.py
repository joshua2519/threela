
# coding: utf-8

# In[ ]:

import requests,time
import json,csv,os.path,sys
 #下面三個要更改
csrf = sys.argv[2]
secretToken = sys.argv[3]

cookie = '_gat=1; PHPSESSID=mlj8um978k8o4ek74332oqif52; 2bd8f60281051886518bc3f76c3bc3b8=5d32d32f10c525d14bb381658b97c2caa402e483a%3A4%3A%7Bi%3A0%3Bs%3A5%3A%2237616%22%3Bi%3A1%3Bs%3A9%3A%22%E7%93%9C%E7%93%9C%E7%93%9C%22%3Bi%3A2%3Bi%3A2592000%3Bi%3A3%3Ba%3A1%3A%7Bs%3A13%3A%22lastLoginTime%22%3Bi%3A1435159722%3B%7D%7D; _ga=GA1.2.1489814873.1435159714'


dateList=sys.argv[1]
payload = {
'LoginForm[email]': 'zero11911@gmail.com',
'LoginForm[password]':'123456', 
'LoginForm[rememberMe]':'0', 
'LoginForm[rememberMe]':'1', 
'ajax':'login-form'
}

rs = requests.session()
res  = rs.post('https://www.nvesto.com/user/login', data = payload)
res.encoding='utf-8'
#print res.text.encode('utf-8')
 

 
header = {
'Content-Type':'application/x-www-form-urlencoded; charset=UTF-8',
'Cookie':cookie,
'Host':'www.nvesto.com',
'Origin':'https://www.nvesto.com',
'Referer':'https://www.nvesto.com/tpe/2498/majorForce',
'User-Agent':'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2357.81 Safari/537.36',
'X-Requested-With':'XMLHttpRequest'
}

#dateList.reverse()
date=dateList
if not os.path.exists('Nevsto_stock/'+date):
    os.makedirs('Nevsto_stock/'+date)
print 'start: '+date
   
with open('allComList.csv', 'rb') as csvfile:
    reader = csv.reader(csvfile)
    for row in reader:            
        sid= row[0]
        name=row[1]
        #print row[0],row[1]
        txtname = sid+'.txt'
        filename='Nevsto_stock/'+date+'/'+sid+'.txt'
        #print os.path.isfile(filename)
        if(os.path.isfile(filename)):
            print txtname+"is exist"
            continue
        print "start "+filename
        payload2 = {
            'fromdate':date,
            'todate':date,
            'view':'detail',
             'csrf':csrf,
            'secretToken':secretToken
            }
        res2 = rs.post('https://www.nvesto.com/tpe/'+sid+'/majorforce/ajaxGetData', data = payload2, headers = header)
        res2.encoding='utf-8'
        #print res2.text
        jd = json.loads(res2.text)
        fid = open('Nevsto_stock/'+date+'/'+sid+'.txt','w')
            
        fid.write(res2.text)
        fid.close()
        time.sleep(1)
            
print "END"            




