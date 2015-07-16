
# coding: utf-8

# In[ ]:

#import list company
import os,csv,codecs,sys
import mysql.connector
#list company
comlist=[]
with open('E:/daily/listcompany.csv') as csvfile:
    reader=csv.reader(csvfile)
    for row in reader:
        comlist.append(row[0])
rawdatapath='E:/daily/raw/'
#folders=os.listdir(rawdatapath)
folders=['20150708','20150709','20150713','20150714']
for folder in folders:
    datefolder=os.path.join(rawdatapath,folder)
   
    for stockid in comlist:
        stockfile=os.path.join(datefolder,stockid+'.csv')
        if(os.path.exists(stockfile)):
            records=[]
            try:
                #read data from csv file
                with open(stockfile,'r') as dailyfile:
                    reader =csv.reader(dailyfile)
                    headline=reader.next()[0]
                    #print headline.decode('big5')
                    if headline=='查無資料':
                        print stockfile +u"=查無資料"
                        continue
                    reader.next()
                    reader.next()
                    
                    for row in reader:
                        record1={'id':'','broker':'','price':'','buy':'','sell':''}
                        record2={'id':'','broker':'','price':'','buy':'','sell':''}
                        #data=row.decode(code).split(',')
                        record1['id']=row[0]
                        record1['broker']=row[1][:4]
                        record1['price']=float(row[2])
                        record1['buy']=int(row[3].replace(',',''))
                        record1['sell']=int(row[4].replace(',',''))
                        records.append(record1)
                        #print record1               
                        if len(row)>5 and len(row[6])>0:
                            record2['id']=row[6]
                            record2['broker']=row[7][:4]
                            record2['price']=float(row[8])
                            record2['buy']=int(row[9].replace(',',''))
                            record2['sell']=int(row[10].replace(',',''))
                            records.append(record2)
                            #print record2
                
                #insert to DB
                cnx = mysql.connector.connect(user='threela', password='123456',
                                          host='127.0.0.1',
                                          database='threela')
                cursor = cnx.cursor()
                add_daily= ("INSERT INTO `threela`.`daily_tmp` "
                           "(StockId,BrokerId,TimeId,Buy,Sell,BuyPrice,SellPrice) "
                           "VALUES (%s,%s,%s,%s,%s,%s,%s)")
                data=[]
                for record in records:
                    brokerid=record['broker']
                    TimeId=folder
                    Buy=int(record['buy']/1000)
                    Sell=int(record['sell']/1000)
                    buyprice=0
                    sellprice=0
                    if record['buy'] >0:
                            buyprice=record['price']
                    if record['sell'] >0:
                            sellprice=record['price']
                    data_daily=(stockid,brokerid,TimeId,Buy,Sell,buyprice,sellprice)
                    data.append(data_daily)
        
                cursor.executemany(add_daily, data)
                cnx.commit()
            except:
                print "Unexpected error:", stockfile,sys.exc_info()[0],sys.exc_info()[1]

