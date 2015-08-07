#Q1 Result
Q1=read.csv("Q1stk.csv",header=TRUE,sep=",")
Q1$season=1
colnames(Q1)=c('Year','StockId','individual_ROI','y_ROI','i_yROI','season')
Q3=read.csv("Q3stk.csv",header=TRUE,sep=",")
Q3$season=3
colnames(Q3)=c('Year','StockId','individual_ROI','y_ROI','i_yROI','season')

data=rbind(Q1,Q3)

mydb = dbConnect(MySQL(), user='threela', password='123456', dbname='threela', host='127.0.0.1')
#dbWriteTable(mydb, value =data, name = "StockRecomd", append = TRUE ,overwrite=FALSE, row.names=FALSE) 

f=function(row){
  sql=sprintf("INSERT INTO stockrecomd
  (Year, StockId, individual_ROI,y_ROI,i_yROI,season)
  VALUES(%s,%s,%s,%s,%s,%s);",row[1],row[2],row[3],row[4],row[5],row[6])  
  dbSendQuery(mydb,sql)
}

apply(data,1,f)

