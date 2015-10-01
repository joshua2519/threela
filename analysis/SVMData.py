
# coding: utf-8

# In[ ]:

import mysql.connector,sys,traceback
from datetime import date,timedelta
mysqlconfig={'user':'threela', 'password':'123456','host':'127.0.0.1','database':'threela'}

# confirm if the date is open
def checkIsOpen(date):
    try:
        cnx = mysql.connector.connect(**mysqlconfig)
        cursor = cnx.cursor()
        time_query = ("SELECT TWSEOPEN FROM threela.time where Date=%s")
        time_data=(date,)
        cursor.execute(time_query,time_data)
        isOpen= cursor.fetchone()
        #print isOpen
        return isOpen[0]
    except:
        #print "Unexpected error:",sys.exc_info()[0],sys.exc_info()[1],sys.exc_info()[2]
        traceback.print_exc()
    finally:
        cursor.close()
        cnx.close()
        
#get correct open date
def getCorrectOpenDate():
    startDate=[(1,10),(2,10),(3,10),(3,31),(4,10),(5,10),(5,15),(6,10),(7,10),(8,10),(8,14),(9,10),(10,10),(11,10),(11,14),(12,10)]
    years=range(2005,2016)
    correctDate=[]

    for y in years:
        for d in startDate:
            checkdate=date(y,d[0],d[1])
            isOpen=checkIsOpen(checkdate)
            if isOpen==1:
                #print checkdate.ctime() + " is open!"
                correctDate.append(checkdate)
            else:
                n=1
                while(checkIsOpen(checkdate+timedelta(n))==0):
                    #print checkIsOpen(checkdate+timedelta(n))
                    n+=1
                #print (checkdate+timedelta(n)).ctime() + " is open!"
                correctDate.append(checkdate+timedelta(n))

            #print checkdate
    return correctDate

#get correct open date with season
def getCorrectOpenDateSeason():
    startDate=[(3,31),(5,15),(8,14),(11,14)]
    years=range(2015,2016)
    correctDate=[]

    for y in years:
        for d in startDate:
            checkdate=date(y,d[0],d[1])
            isOpen=checkIsOpen(checkdate)
            if isOpen==1:
                #print checkdate.ctime() + " is open!"
                correctDate.append(checkdate)
            else:
                n=1
                while(checkIsOpen(checkdate+timedelta(n))==0):
                    #print checkIsOpen(checkdate+timedelta(n))
                    n+=1
                #print (checkdate+timedelta(n)).ctime() + " is open!"
                correctDate.append(checkdate+timedelta(n))

            #print checkdate
    return correctDate

def getStartEndList(OpenDate):
    #1Month,3Month,6Month
    AllCloseDates=[]
    CloseDateDiff=[30,90,180]
    for diff in CloseDateDiff:        
        for d in OpenDate:
            dateitem={}
            EndDate=d+timedelta(diff)
            isOpen=checkIsOpen(EndDate)
            if isOpen==True:
                #print checkdate.ctime() + " is open!"
                #CloseDates.append(OneMon)
                dateitem['diff']=diff
                dateitem['start']=d
                dateitem['end']=EndDate
            else:
                n=1
                while(checkIsOpen(EndDate+timedelta(n))==0):
                    n+=1
                #print (checkdate+timedelta(n)).ctime() + " is open!"
                #CloseDates.append(OneMon+timedelta(n))
                dateitem['diff']=diff
                dateitem['start']=d
                dateitem['end']=EndDate+timedelta(n)
            AllCloseDates.append(dateitem)
        #AllCloseDates[diff]=CloseDates
    return AllCloseDates
    
def readComList():
    try:
        cnx = mysql.connector.connect(**mysqlconfig)
        cursor = cnx.cursor()
        time_query = ("SELECT stockid FROM threela.company order by stockid")        
        cursor.execute(time_query)
        comList= cursor.fetchall()
        return comList
    except:
        #print "Unexpected error:",sys.exc_info()[0],sys.exc_info()[1],sys.exc_info()[2]
        traceback.print_exc()
    finally:
        cursor.close()
        cnx.close() 


opendate=getCorrectOpenDateSeason()
datelist=getStartEndList(opendate)
comlist=readComList()
print len(comlist)
print len(datelist)
for d in datelist:
    print d



    


# In[ ]:

def getIndexGrowRate(after,before):
    try:
        cnx = mysql.connector.connect(**mysqlconfig)
        cursor = cnx.cursor()
        index_query = ("SELECT  (a.ClosePrice-b.ClosePrice)/b.ClosePrice*100                         FROM threela.`index` as a                         join threela.`index` as b                         on a.timeid=%s and b.timeid=%s")
        index_data=(after,before)
        cursor.execute(index_query,index_data)
        index=cursor.fetchall()
        if len(index):
            
            return index[0][0]
        else:
            return None
        
    except:
        #print "Unexpected error:",sys.exc_info()[0],sys.exc_info()[1],sys.exc_info()[2]
        traceback.print_exc()
    finally:
        cursor.close()
        cnx.close()
        
def getStockGrowRate(stockid,after,before):
    try:
        cnx = mysql.connector.connect(**mysqlconfig)
        cursor = cnx.cursor()
        stock_query = ("SELECT (a.closeprice-b.closeprice)/b.closeprice *100 as priceRate                         FROM threela.trading as a                         join threela.trading as b                         on a.Timeid=%s and b.timeid=%s                         and a.stockid=%s and b.stockid=%s")
        stock_data=(after,before,stockid,stockid)
        cursor.execute(stock_query,stock_data)
        stockRate=cursor.fetchall()
        if len(stockRate):            
            return stockRate[0][0]
        else:
            return None
    except:
        #print "Unexpected error:",sys.exc_info()[0],sys.exc_info()[1],sys.exc_info()[2]
        traceback.print_exc()
    finally:
        cursor.close()
        cnx.close()


# In[ ]:


#insert historical data

cnx=None
cursor=None
try:
    cnx = mysql.connector.connect(**mysqlconfig)
    cursor = cnx.cursor()
    #outfile = open('svmdata.txt','a')
    for ditem in datelist:
        diff=ditem['diff']
        start=ditem['start']
        end=ditem['end']
        year=start.year
        mon=start.month
        day=start.day
        raws=[]
        #add_raw = ("INSERT INTO `threela`.`fundmentalraw` "
        #        "(`StockId`,`TimeId`,`Diff`,`YieldRate`,`PE`,`PBR`,`EPS`,`DebtRatio`,`ROE`,`MonthRate`,`YearRate`,`growRate`,`growClass`) "
        #        "VALUES (%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s)")
        add_raw = ("INSERT INTO `threela`.`FundmentalRawSeason` "
                "(`StockId`,`TimeId`,`Diff`,`YieldRate`,`PE`,`PBR`,`EPS`,`DebtRatio`,`ROE`,`MonthRate`,`YearRate`,`growRate`,`growClass`) "
                "VALUES (%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s)")
        for com in comlist:
            stockid=com[0]
            YR_PE_Query=("SELECT  d.stockid AS stockid,                         t.season AS season,                         d.closeprice AS ClosePrice,                         d.YieldRate AS YR,                         d.PE AS PE                         FROM                         `trading` AS d                         JOIN `time` AS t ON d.TimeId = t.TimeId                         where d.stockid=%s and t.Date=%s and d.YieldRate is not null and d.PE is not null")
            YR_PE_Data=(com[0],start)
            #print YR_PE_Data
            cursor.execute(YR_PE_Query,YR_PE_Data)
            YR_PE=cursor.fetchall()
            if len(YR_PE)>0:
                season=YR_PE[0][1]
                closeprice=YR_PE[0][2]
                YR=YR_PE[0][3]
                PE=YR_PE[0][4]
                    
                season_query=("SELECT BookValue,EPS,DebtRatio,ROE                                 FROM threela.season                                 where stockid=%s and Year=%s and Season=%s and                                 bookvalue >0 and bookvalue is not null and                                 EPS is not null and DebtRatio is not null and ROE is not null")
                querySeason=season
                queryYear=year
                if date(year,1,1)<=start < date(year,3,31):
                    queryYear=year-1
                    querySeason=3                       
                elif date(year,3,31)<=start <date(year,5,15):
                    queryYear=year-1
                    querySeason=4                       
                elif date(year,5,15)<=start <date(year,8,14):
                    if season==2:
                        querySeason=season-1
                    else:
                        querySeason=season-2
                elif date(year,8,14)<=start <date(year,11,14):
                    if season==3:
                        querySeason=season-1
                    else:
                        querySeason=season-2
                else:
                    querySeason=season-1
                season_data=(stockid,queryYear,querySeason)
                cursor.execute(season_query,season_data)
                SeasonReport= cursor.fetchall()
                if len(SeasonReport) > 0:
                    bookvalue=SeasonReport[0][0]
                    EPS=SeasonReport[0][1]
                    DebtRatio=SeasonReport[0][2]
                    ROE=SeasonReport[0][3]
                    month_query=('SELECT avg(RevMonthGrowthRate),avg(RevYearGrowthRate)                                     FROM threela.month                                     WHERE STOCKID=%s AND                                     ((year=%s and month=%s) or (year=%s and month=%s) or (year=%s and month=%s))                                     group by STOCKID                                     having avg(RevMonthGrowthRate) is not null and avg(RevYearGrowthRate) is not null')
                    #1 month before start date
                    start_1m=start-timedelta(31)
                    #2 month befroe start date
                    start_2m=start-timedelta(62)
                    #3 month before start date
                    start_3m=start-timedelta(93)
                    
                    year_m1=start_1m.year
                    mon_m1=start_1m.month
                    year_m2=start_2m.year
                    mon_m2=start_2m.month
                    year_m3=start_3m.year
                    mon_m3=start_3m.month
                    
                    month_data=(stockid,year_m1,mon_m1,year_m2,mon_m2,year_m3,mon_m3)
                    cursor.execute(month_query,month_data)
                    MonthReport=cursor.fetchall()
                    if len(MonthReport) > 0:
                        MonthRate=MonthReport[0][0]
                        YearRate=MonthReport[0][1]
                        IndexRate=getIndexGrowRate(end.strftime("%Y%m%d"),start.strftime("%Y%m%d"))
                        StockRate=getStockGrowRate(stockid,end.strftime("%Y%m%d"),start.strftime("%Y%m%d"))
                        if IndexRate !=None and StockRate !=None:
                            growRate=StockRate-IndexRate
                            growClass=''
                            if growRate< -15:
                                growClass='worse'
                            elif -15<= growRate <-5:
                                growClass='bad'
                            elif -5<= growRate <=5:
                                growClass='none'
                            elif 5<growRate<=15:
                                growClass='good'
                            elif growRate>15:
                                growClass='great'
                
                            
                            raw_data=(stockid,start.strftime("%Y%m%d"),diff,YR,PE,closeprice/bookvalue,EPS,DebtRatio,ROE,MonthRate,YearRate,growRate,growClass)
                            raws.append(raw_data)
        
        cursor.executemany(add_raw,raws)
        cnx.commit()
                            #outfile.write(stockid+'\t'+str(year)+'\t'+str(season)+'\t'+str(mon)+'\t'+str(day)+'\t'\
                            #          +str(closeprice)+'\t'+str(YR)+'\t'+str(PE)+'\t'+str(closeprice/bookvalue)+'\t'\
                            #         +str(EPS)+'\t'+str(DebtRatio)+"\t"+str(ROE)+"\t"+str(MonthRate)+"\t"+str(YearRate)+'\t'\
                            #         +str(IndexRate)+'\t'+str(StockRate)+'\t'+str(growRate)+'\t'+str(growClass)+"\n")
                    
except:
    #print "Unexpected error:",sys.exc_info()[0],sys.exc_info()[1],sys.exc_info()[2]
    traceback.print_exc()
finally:    
    if cursor:
        cursor.close()
    if cnx:
        cnx.close()
#    if outfile:
#        outfile.close()


# In[ ]:

# insert prediction data
cnx=None
cursor=None
try:
    cnx = mysql.connector.connect(**mysqlconfig)
    cursor = cnx.cursor()
    #outfile = open('svmdata.txt','a')
    for ditem in datelist:
        diff=ditem['diff']
        start=ditem['start']
        end=ditem['end']
        year=start.year
        mon=start.month
        day=start.day
        raws=[]
        #add_raw = ("INSERT INTO `threela`.`fundmentalraw` "
        #        "(`StockId`,`TimeId`,`Diff`,`YieldRate`,`PE`,`PBR`,`EPS`,`DebtRatio`,`ROE`,`MonthRate`,`YearRate`,`growRate`,`growClass`) "
        #        "VALUES (%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s)")
        add_raw = ("INSERT ignore INTO `threela`.`FundmentalRawSeason` "
                "(`StockId`,`TimeId`,`Diff`,`YieldRate`,`PE`,`PBR`,`EPS`,`DebtRatio`,`ROE`,`MonthRate`,`YearRate`) "
                "VALUES (%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s)")
        for com in comlist:
            stockid=com[0]
            YR_PE_Query=("SELECT  d.stockid AS stockid,                         t.season AS season,                         d.closeprice AS ClosePrice,                         d.YieldRate AS YR,                         d.PE AS PE                         FROM                         `trading` AS d                         JOIN `time` AS t ON d.TimeId = t.TimeId                         where d.stockid=%s and t.Date=%s and d.YieldRate is not null and d.PE is not null")
            YR_PE_Data=(com[0],start)
            #print YR_PE_Data
            cursor.execute(YR_PE_Query,YR_PE_Data)
            YR_PE=cursor.fetchall()
            if len(YR_PE)>0:
                season=YR_PE[0][1]
                closeprice=YR_PE[0][2]
                YR=YR_PE[0][3]
                PE=YR_PE[0][4]
                    
                season_query=("SELECT BookValue,EPS,DebtRatio,ROE                                 FROM threela.season                                 where stockid=%s and Year=%s and Season=%s and                                 bookvalue >0 and bookvalue is not null and                                 EPS is not null and DebtRatio is not null and ROE is not null")
                querySeason=season
                queryYear=year
                if date(year,1,1)<=start < date(year,3,31):
                    queryYear=year-1
                    querySeason=3                       
                elif date(year,3,31)<=start <date(year,5,15):
                    queryYear=year-1
                    querySeason=4                       
                elif date(year,5,15)<=start <date(year,8,14):
                    if season==2:
                        querySeason=season-1
                    else:
                        querySeason=season-2
                elif date(year,8,14)<=start <date(year,11,14):
                    if season==3:
                        querySeason=season-1
                    else:
                        querySeason=season-2
                else:
                    querySeason=season-1
                season_data=(stockid,queryYear,querySeason)
                cursor.execute(season_query,season_data)
                SeasonReport= cursor.fetchall()
                if len(SeasonReport) > 0:
                    bookvalue=SeasonReport[0][0]
                    EPS=SeasonReport[0][1]
                    DebtRatio=SeasonReport[0][2]
                    ROE=SeasonReport[0][3]
                    month_query=('SELECT avg(RevMonthGrowthRate),avg(RevYearGrowthRate)                                     FROM threela.month                                     WHERE STOCKID=%s AND                                     ((year=%s and month=%s) or (year=%s and month=%s) or (year=%s and month=%s))                                     group by STOCKID                                     having avg(RevMonthGrowthRate) is not null and avg(RevYearGrowthRate) is not null')
                    #1 month before start date
                    start_1m=start-timedelta(31)
                    #2 month befroe start date
                    start_2m=start-timedelta(62)
                    #3 month before start date
                    start_3m=start-timedelta(93)
                    
                    year_m1=start_1m.year
                    mon_m1=start_1m.month
                    year_m2=start_2m.year
                    mon_m2=start_2m.month
                    year_m3=start_3m.year
                    mon_m3=start_3m.month
                    
                    month_data=(stockid,year_m1,mon_m1,year_m2,mon_m2,year_m3,mon_m3)
                    cursor.execute(month_query,month_data)
                    MonthReport=cursor.fetchall()
                    if len(MonthReport) > 0:
                        MonthRate=MonthReport[0][0]
                        YearRate=MonthReport[0][1]
                               
                            
                        raw_data=(stockid,start.strftime("%Y%m%d"),diff,YR,PE,closeprice/bookvalue,EPS,DebtRatio,ROE,MonthRate,YearRate)
                        raws.append(raw_data)
        
        cursor.executemany(add_raw,raws)
        cnx.commit()
                            #outfile.write(stockid+'\t'+str(year)+'\t'+str(season)+'\t'+str(mon)+'\t'+str(day)+'\t'\
                            #          +str(closeprice)+'\t'+str(YR)+'\t'+str(PE)+'\t'+str(closeprice/bookvalue)+'\t'\
                            #         +str(EPS)+'\t'+str(DebtRatio)+"\t"+str(ROE)+"\t"+str(MonthRate)+"\t"+str(YearRate)+'\t'\
                            #         +str(IndexRate)+'\t'+str(StockRate)+'\t'+str(growRate)+'\t'+str(growClass)+"\n")
                    
except:
    #print "Unexpected error:",sys.exc_info()[0],sys.exc_info()[1],sys.exc_info()[2]
    traceback.print_exc()
finally:    
    if cursor:
        cursor.close()
    if cnx:
        cnx.close()


# In[ ]:



