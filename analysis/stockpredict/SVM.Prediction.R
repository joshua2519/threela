mydb = dbConnect(MySQL(), user='threela', password='123456', dbname='threela', host='127.0.0.1')
#SVM prediction
#raw data diff=30day
rs= dbSendQuery(mydb, 'select * from threela.fundmentalrawseason where timeid > 20150501 and diff=30')
data.sea=fetch(rs, n=-1)
#data.sea$StockId = factor(data.sea.30$StockId)
#data.sea$Diff = factor(data.sea.30$Diff)
#data.sea.30$growClass = factor(data.sea.30$growClass)
#data.sea.30$TimeId= as.Date(as.character(train.sea.30$TimeId),format='%Y%m%d')
data.sea.mysql=data.sea[, which(names(data.sea) %in% c("stockid","timeid"))]
data.sea=data.sea[, -which(names(data.sea) %in% c("stockid","timeid","diff","growClass","growRate"))]



##predict data
predict.data.sea.30=predict(model.sea.filter.30.type.tune,data.sea)
predict.data.sea.90=predict(model.sea.filter.90.type.tune,data.sea)
predict.data.sea.180=predict(model.sea.filter.180.type.tune,data.sea)

##combinedata

data.sea.mysql=cbind(data.sea.mysql$StockId,data.sea.mysql$TimeId,predict.data.sea.30,predict.data.sea.90,predict.data.sea.180)

###inser to mysql 

f=function(row){
  sql=sprintf("INSERT INTO StockPredict
  (StockId, TimeId, PredictMon1,PredictMon2,PredictMon3)
  VALUES(%s,%s,%s,%s,%s);",row[1],row[2],row[3],row[4],row[5])  
  dbSendQuery(mydb,sql)
}

apply(data.sea.mysql,1,f)




