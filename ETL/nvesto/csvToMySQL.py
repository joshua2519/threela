import requests,time,MySQLdb
import json,csv,os.path,sys
date = sys.argv[1]
conn = MySQLdb.connect(host='10.120.31.4',user='threela',passwd='123456')
curs = conn.cursor()
conn.select_db('threela')
parent_path = os.path.abspath('\Users\BigData\Desktop\stockdog\\'+str(date))
print parent_path
#print "The dir is : %s"%os.listdir(parent_path)[5]
#print len(os.listdir(parent_path)) #--307 len(os.listdir(parent_path))
gua=[]
for i in range(0,len(os.listdir(parent_path))):
    filename = os.listdir(parent_path)[i]
    fid = open('/Users/BigData/Desktop/stockdog/'+str(date)+'/'+filename,'rb')
    reader = csv.reader(fid)
    stock_id = filename[-44:-40]
    #print stock_id
    
    rows = [row for row in reader]
    if len(rows) >2:
    #for row in reader:
            #print row[0][-5:-1],row[1]
        for j in range(1,len(rows)-1):
            broker_id = rows[j][0][-5:-1]
            buy = rows[j][1]
            sell = rows[j][2]
            buy_price = rows[j][4]
            sell_price = rows[j][5]
            #print broker_id,date,buy,sell,buy_price,sell_price
            tuple1 =(stock_id,broker_id,date,buy,sell,buy_price,sell_price)
            gua.append(tuple1)
    else:
        pass
#print gua

sql="insert ignore into guaguatest(StockId,BrokerId,TimeId,Buy,Sell,BuyPrice,SellPrice) values(%s,%s,%s,%s,%s,%s,%s)" 
curs.executemany(sql,gua)
conn.commit()
print "finished"+'\t'+str(date)+" ok,ok...ThX God"


#count = curs.execute('select * from daily')
#result = curs.fetchall()
#print "Finall %s totall: " % count


curs.close()
conn.close()