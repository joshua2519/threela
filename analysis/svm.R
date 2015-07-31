mydb = dbConnect(MySQL(), user='threela', password='123456', dbname='threela', host='127.0.0.1')
#dbListTables(mydb)
#all training data
rs= dbSendQuery(mydb, "select * from threela.fundmentalraw where timeid < 20101231")
trainingdata=fetch(rs, n=-1)
trainingdata$StockId = factor(trainingdata$StockId)
trainingdata$Diff = factor(trainingdata$Diff)
trainingdata$growClass = factor(trainingdata$growClass)
trainingdata$TimeId= as.Date(as.character(trainingdata$TimeId),format="%Y%m%d")

#diff=30day
rs= dbSendQuery(mydb, "select * from threela.fundmentalraw where timeid < 20101231 and diff=30")
train.30=fetch(rs, n=-1)
train.30$StockId = factor(train.30$StockId)
train.30$Diff = factor(train.30$Diff)
train.30$growClass = factor(train.30$growClass)
train.30$TimeId= as.Date(as.character(train.30$TimeId),format="%Y%m%d")

#diff=90day
rs= dbSendQuery(mydb, "select * from threela.fundmentalraw where timeid < 20101231 and diff=90")
train.90=fetch(rs, n=-1)
train.90$StockId = factor(train.90$StockId)
train.90$Diff = factor(train.90$Diff)
train.90$growClass = factor(train.90$growClass)
train.90$TimeId= as.Date(as.character(train.90$TimeId),format="%Y%m%d")

#rpart test
train=train.90
train.subset=subset(train,select=c(YieldRate,PE,PBR,EPS,DebtRatio,ROE,MonthRate,YearRate,growClass))
stocktree.rp=rpart(growClass ~.,data=train1.subset)
plot(stocktree.rp, margin=0.1)
text(stocktree.rp,all=TRUE,use.n=TRUE)

#ctree