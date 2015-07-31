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

#diff=180day
rs= dbSendQuery(mydb, "select * from threela.fundmentalraw where timeid < 20101231 and diff=180")
train.180=fetch(rs, n=-1)
train.180$StockId = factor(train.180$StockId)
train.180$Diff = factor(train.180$Diff)
train.180$growClass = factor(train.180$growClass)
train.180$TimeId= as.Date(as.character(train.180$TimeId),format="%Y%m%d")

#validate data 30
rs= dbSendQuery(mydb, "select * from threela.fundmentalraw where timeid > 20101231 and timeid< 20121231 and diff=30")
vd.30=fetch(rs, n=-1)
vd.30$StockId = factor(vd.30$StockId)
vd.30$Diff = factor(vd.30$Diff)
vd.30$growClass = factor(vd.30$growClass)
vd.30$TimeId= as.Date(as.character(vd.30$TimeId),format="%Y%m%d")

#validate data 90
rs= dbSendQuery(mydb, "select * from threela.fundmentalraw where timeid > 20101231 and timeid< 20121231 and diff=90")
vd.90=fetch(rs, n=-1)
vd.90$StockId = factor(vd.90$StockId)
vd.90$Diff = factor(vd.90$Diff)
vd.90$growClass = factor(vd.90$growClass)
vd.90$TimeId= as.Date(as.character(vd.90$TimeId),format="%Y%m%d")

#validate data 180
rs= dbSendQuery(mydb, "select * from threela.fundmentalraw where timeid > 20101231 and timeid< 20121231 and diff=180")
vd.180=fetch(rs, n=-1)
vd.180$StockId = factor(vd.180$StockId)
vd.180$Diff = factor(vd.180$Diff)
vd.180$growClass = factor(vd.180$growClass)
vd.180$TimeId= as.Date(as.character(vd.180$TimeId),format="%Y%m%d")

#rpart test
train=train.90
train.subset=subset(train,select=c(YieldRate,PE,PBR,EPS,DebtRatio,ROE,MonthRate,YearRate,growClass))
stocktree.rp=rpart(growClass ~.,data=train1.subset)
plot(stocktree.rp, margin=0.1)
text(stocktree.rp,all=TRUE,use.n=TRUE)

#ctree