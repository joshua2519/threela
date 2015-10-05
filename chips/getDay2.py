# -*- coding: utf-8 -*-
import requests
import sys
from bs4 import BeautifulSoup
#import dateutil ??? 需要打開
d = {}
# 將民國年轉西元年
count = 0
def ROCDateToADDate(year):
    if (len(str(year)) < 4):
        year = str(int(year) + 1911)
    elif (len(str(year)) < 8):
        year = str(int(year) + 19110000)
    #print year
    return year
#將 西元年轉民國年
def ADDateToROCDate(year):
    if (len(str(year)) <= 4):
        year = int(year) - 1911
    elif(len(str(year)) <=6 ):
        year = int(year)-191100
    elif(len(str(year)) <=8 ):
        year = int(year) - 19110000
    return str(year)

def connect(dbname,password,host="localhost",user="root",port=3306):
    import mysql.connector as mc
    db = mc.connect(host=host,user=user,passwd=password,db=dbname,charset="utf8")
    return db
def insert_trading(db,StockId=None,TimeId=None, OpenPrice=None, HighPrice=None, LowPrice=None, ClosePrice=None,Volume=None, pe=None):
    cur = db.cursor()   
    #print str(StockId) + " : " + str(TimeId ) + " : " + str( OpenPrice) + " : " + str( HighPrice) + " : " + str(LowPrice) + " : " + str(ClosePrice ) + " : " + str(Volume ) + " : " + str( pe)    
    if (float(Volume) == 0.0 or Volume == 'None' or HighPrice == 'None'):
        data = "'%s',%s,%s,%s,%s,%s,%s,%s"%(StockId,TimeId,'NULL','NULL','NULL','NULL',Volume,pe)
    else:
        data = "'%s', %s , %s , %s ,%s, %s , %s , %s"%(StockId,TimeId, OpenPrice , HighPrice , LowPrice,ClosePrice,Volume,pe)
    sql = "insert into threela.trading(StockId,TimeId,OpenPrice,HighPrice,LowPrice,ClosePrice,Volume,pe) values(%s);"%(data)

    try:
        cur.execute(sql)
        db.commit()
    except:
        print "error sql: " + sql
        print "Unexpected error:", sys.exc_info()[0]

def close(db):
    db.close()

def getIndexWeb(conn, adYearMonth , DBEndADDate):
    cur = conn.cursor()
    DBEndROCDate = int( ADDateToROCDate(DBEndADDate) ) # 將資料庫裡，最後存放的日期，轉成西元年
    rs = requests.session();
    # 取得 交易金額
    web = "http://www.twse.com.tw/ch/trading/exchange/FMTQIK/genpage/Report%s/%s_F3_1_2.php"%(adYearMonth, adYearMonth)
    res = rs.post(web); # 取回資料
    soup = BeautifulSoup(res.text)
    tables = soup.select('table')[7:8] # 取出 table 資料 ，從0開始
    #print tables
    for table in tables:
        trs = table.select('tr')[2:] # 取 第2筆之後的 <tr> 資料
        for tr in trs:
            count = 0
            #print "count"
            for td in tr:
                if (count == 0):
                    dataDate =  int(td.text.strip().replace("/",""))
                    if (DBEndROCDate < dataDate):
                        print dataDate
                    else:
                        break
                if (count == 2):
                    #print " " + td.text.strip().replace(",","") ,
                    indexValue = round( (int(td.text.strip().replace(",","") ) / 100000000.0 ),2)
                    #print indexValue
                    d[dataDate] = indexValue
                    break
                count += 1
    
    # 取得成交金額 開、高、低、收 的資料
    ROCWebYear = ADDateToROCDate(  (adYearMonth/100)  )
    month = str(adYearMonth)[4:6]
    web = 'http://www.twse.com.tw/ch/trading/indices/MI_5MINS_HIST/MI_5MINS_HIST.php?myear=%s&mmon=%s'%(ROCWebYear, month)  
    print web
    res = rs.post(web); # 取回資料
    soup = BeautifulSoup(res.text)
    tables = soup.select('table')[7:8] # 取出 table 資料 ，從0開始
    #print tables
    for table in tables:
        trs = table.select('tr')[2:]
        #print trs
        for tr in trs:
            #tds = tr.select('tr')
            count = 0
            for td in tr:
                if (count == 0):
                    dataDate = int( td.text.replace("/","") )
                    #print dataDate , " ", DBEndROCDate
                    # 若 web 上現在看到的日期，比資料庫裡的日期還要小，表示這資料，已經存放在資料庫裡了，所以這筆資料不需要了
                    if (dataDate <= DBEndROCDate):
                        break
                elif (count == 1):
                    openPrice = td.text.strip().replace(",","")
                elif (count == 2):
                    highPrice = td.text.strip().replace(",","")
                elif (count == 3):
                    lowPrice = td.text.strip().replace(",","")
                else:
                    closePrice = td.text.strip().replace(",","")
                    if dataDate not in d:
                        print "Wait !! No volume !!"
                        return "false"

                    indexValue = d[dataDate]
                    print indexValue
                    dataDate = ROCDateToADDate( dataDate )
                    sql = "INSERT INTO threela.INDEX " \
                         "(StockId, TimeId, OpenPrice , HighPrice, LowPrice, ClosePrice, TAPI) Values " \
                         "(100, %s, %s, %s, %s, %s, %s)"%(dataDate , openPrice, highPrice, lowPrice,closePrice , indexValue)
                    print "sql2 大盤: ", 
                    print sql                    

                    try:
                        cur.execute(sql)
                        conn.commit()
                    except:
                        print "error sql: " + sql
                        print "Unexpected error:", sys.exc_info()[0]
                        
                count += 1
    return

def getIndex(conn, year , month, yearNow, monthNow ,DBEndDate):
    #print "BeginDate:" + year + " " + month + "    EndDate:" + yearNow + " " + monthNow

    beginYearMon = year * 100 + month
    endYearMon = yearNow * 100 + monthNow
    print "大盤日期"
    
    for yy in range(year, yearNow+1):
        for mm in range(1, 13):
            yearMon = yy * 100 + mm
            if ( yearMon >= beginYearMon and yearMon <= endYearMon ):
                isRun = getIndexWeb(conn, yearMon, DBEndDate )
                if (isRun is 'false'):
                    return 'false'
                #break
            elif (yearMon <= beginYearMon ):
                continue
            else:
                break
    return            
 
def getPrice(td=None):
    if (td == 'None'):
        return 'None'
    else:  
        return str(round(float(td),2))

# 上櫃
def getOTC(conn, adYear, monthDay):
    cur = conn.cursor()
    rocYear = int(adYear) - 1911
    date = '%s/%s'%(rocYear, monthDay)
    rs = requests.session();
    # http://www.tpex.org.tw/web/stock/aftertrading/daily_close_quotes/stk_quote_result.php?l=zh-tw&d=104/09/08&_=1441900868250
    url = 'http://www.tpex.org.tw/web/stock/aftertrading/daily_close_quotes/stk_quote_result.php?l=zh-tw&_=1441900868250&d=%s'%(date)
    print url

    adDate = ROCDateToADDate(rocYear) + monthDay.replace('/',"")
    
    res = rs.post(url); # 取回資料
    soup = BeautifulSoup(res.text) #將網頁抓下來的資料轉換成文字檔    
    #print soup.text
    #print '-------------------------------------'    
    data = soup.text.split('aaData":[')[1]
    rows = data.split('],[')
    count =0
    for row in rows:
        row=row.replace("[","").replace("]","").replace("}","") 
        #print row
        columns = row.split('","')
        columns[0] = columns[0].replace('"',"")
        
        if (len(columns[0]) != 4 ):
            continue

        if (columns[2].strip() == '---'):
            sql = "INSERT INTO threela.trading (StockId, TimeId, OpenPrice, HighPrice, LowPrice , ClosePrice , Volume) "\
                  " Values ('%s', %s , NULL,NULL ,NULL,NULL,NULL)"%(columns[0] , adDate)

        else:
            volume = int( round( float(columns[8].replace(",","") ) / 1000.0 ,0)   )
            sql = "INSERT INTO threela.trading " \
                  "(StockId, TimeId, OpenPrice, HighPrice, LowPrice , ClosePrice , Volume) Values " \
                  " ('%s', %s , %s, %s, %s, %s, %d) " %   \
                  (columns[0] , adDate, columns[4] , columns[5], columns[6], columns[2],  volume  ) 
        
        if (count % 200 == 0):
            print "sql4 上櫃: ",
            print sql
        count += 1

        try:
            cur.execute(sql)
            conn.commit()
        except:
            print "error sql: " + sql
            print "Unexpected error:", sys.exc_info()[0]

# 上市
def getTran(conn, adYear , monthDay):
    rocYear = int(adYear) - 1911
    rs = requests.session();
    ROCdate = '%s/%s'%(rocYear,monthDay)
    padload = {
    'download':'',
    'selectType':'ALLBUT0999'
    }
    padload['qdate'] = ROCdate
    #print "ROCdate:" + str(ROCdate)
    #print padload
    
    res = rs.post('http://www.twse.com.tw/ch/trading/exchange/MI_INDEX/MI_INDEX.php',data=padload); # 取回資料
    soup = BeautifulSoup(res.text) #將網頁抓下來的資料轉換成文字檔
    #print soup.text
    tables = soup.select('table')[1:2] # 取出第1筆 table ，從0開始
    #print tables[0]
    tdCount = 0 #記錄現在在處理那一個欄位
    StockId = ""       # 取出證券代號
    Volume = 0       # 成交股數釋
    pe  = 0             # 本益比
    OpenPrice = 0 #開盤價
    HighPrice = 0  #最高價
    LowPrice = 0   # 最低價
    ClosePrice = 0 #收盤價
    count = 0 # 計算已經處理的筆數
    #print tables
    ROCdateInt = ROCdate.replace("/", "")
    ADDateStr = ROCDateToADDate(ROCdateInt)
    
    for table in tables:
        trs = table.select('tr')[2:] # 取 第2筆之後的 <tr> 資料
        #print trs    
        # 取出每一行資料
        for tr in trs:
            tds= tr.select('td')
            tdCount = 0  # 設定每一列的第1個欄位為0的位置
            #取出每一個欄位
            for td in tds:
                td = td.text.strip().replace(",","")
                if (tdCount == 0):    # 證券代號
                    if ( td != 'NULL' and len(td) > 2 and td[0:1] == "0" and int(td[1:2]) > 0 ): # 將 01~09開頭的，都跳掉不處理
                        break
                    #if (len(td) != 4):
                    #    break;
                    StockId = td

                if (tdCount != 0 and td == '--'): # 將沒有資料的值，轉成 None
                    td = 'None'
                if (tdCount ==  2):  # 成交股數，變成交張數
                    if (td == 'None' or int(td) < 500):
                        Volume = '0'
                        break
                    else:
                        Volume = str(  int(round((float(td) / 1000),0)   )  )
                    #print Volume
                    #print round((float(td) / 1000))
                if (tdCount == 5):       # 開盤價
                    #print type(td)
                    #print repr(td)
                    OpenPrice=getPrice(td)
                if (tdCount == 6):     # 最高價
                    HighPrice=getPrice(td)
                if (tdCount == 7):     #最低價
                    LowPrice = getPrice(td)
                if (tdCount == 8):    # 收盤價
                    ClosePrice = getPrice(td)
                if (tdCount == 15): # 本益比
                    count = count+1
                    pe=getPrice(td)
                    if (count % 200 == 0):
                        print "sql3 上市: " ,
                        sql = "INSERT INTO threela.trading "\
                        "(StockId, TimeId , OpenPrice , HighPrice, LowPrice, ClosePrice, Volume , PE) VALUES "\
                        " (%s, %s ,%s , %s , %s , %s , %s , %s) " \
                        %(StockId , ADDateStr ,OpenPrice,HighPrice , LowPrice , ClosePrice, Volume , pe  )

                        print sql
                    insert_trading(conn,StockId,ADDateStr, OpenPrice, HighPrice, LowPrice, ClosePrice,Volume, pe)
                tdCount += 1
                #if (count > 100):
                #    break
        break;
def getEndDate(conn,dataBase):
    cur = conn.cursor()    
    sql = "SELECT MAX(TimeId) FROM threela.trading WHERE StockId = ( SELECT StockId FROM ETF50 LIMIT 0,1 ) " #???換成 統一公司 
    #print sql

    try:  
        cur.execute(sql)
        date = cur.fetchone()[0]
        print "DB End date: ",
        print date
    except:
        print "Unexpected error:", sys.exc_info()[0]

    return date
   
#-- main ----------------------------------------------------------------------
import datetime
today = datetime.date.today()  # 取得今天日期
yearNow = today.year           # 年
monthNow = today.month         # 月
dayNow = today.day             # 日 
dataBase = "use threela; "     # 資料庫名稱

try:
    conn=connect(dbname="threela",password="123456",host="127.0.0.1",user="threela",port=3306) # 連資料庫
    DBEndDate = getEndDate(conn,dataBase) # 找資料庫裡，最後一天有交易的記錄
    #DBEndDate = '20150101' # 修改這個日期，就可以執行 某一段期間的日期，這個變數是開始日期
    DBdateStr = str(DBEndDate)              
    DByear = int(DBdateStr[0:4])  # 放在資料庫的，最新的 年
    DBmonth = int(DBdateStr[4:6]) # 放在資料庫裡，最新的 月
    DBday = int(DBdateStr[6:8])   # 放在資料庫裡，最新的 日
    
    d1 = datetime.datetime(DByear, DBmonth, DBday)
    d2 = datetime.datetime(yearNow,monthNow,dayNow)
    dayDiff = (d2-d1).days
    
    # 找日期相差幾天 
    print "today :",
    print yearNow, monthNow, dayNow
    
    # == 大盤 ===================================
    isRun=getIndex(conn, DByear, DBmonth, yearNow, monthNow , DBEndDate )
    if (isRun is 'false' ):
        sys.exit()
    #getIndex(conn, DByear, DBmonth, yearNow, monthNow , DBEndDate ) # 取得大盤資料    
    #== 處理 上市、上櫃股票 =====================
    #print "begin 上市、上櫃"
    dates = []
    
    for i in range(dayDiff - 1 , -1 , -1):
        #print i ,
        dates.append(today - datetime.timedelta(days = (i)))
    #print
    #print "begin date: " ,
    #print dates[0]

    for i in dates:
        date = str(i).replace("-", "/")
        adYear = date[0:4]
        monthDay = date[5:]
        print i
        
        getTran(conn, adYear, monthDay) # 取每天上市的交易資料， 傳 connect 和 日期資料
        getOTC(conn, adYear, monthDay) # 取得每天上櫃交易資料
        # 下面2個網站，若要取得交易當天的資料時，若都有當天資料，再執行，程式就不會錯
        # http://www.tpex.org.tw/web/stock/aftertrading/daily_close_quotes/stk_quote.php?l=zh-tw
        # http://www.twse.com.tw/ch/trading/exchange/FMTQIK/genpage/Report201510/201510_F3_1_2.php
        #break
    #print dates
    print 'end!!'
 
except:
    print "Unexpected error:", sys.exc_info()[0]
finally:
    close(conn)
                