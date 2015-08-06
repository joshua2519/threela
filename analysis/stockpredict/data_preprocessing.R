mydb = dbConnect(MySQL(), user='threela', password='123456', dbname='threela', host='127.0.0.1')
#dbListTables(mydb)
#all training data
"
rs= dbSendQuery(mydb, 'select * from threela.fundmentalraw where timeid < 20101231')
train.all=fetch(rs, n=-1)
train.all$StockId = factor(train.all$StockId)
train.all$Diff = factor(train.all$Diff)
train.all$growClass = factor(train.all$growClass)
train.all$TimeId= as.Date(as.character(train.all$TimeId),format='%Y%m%d')
save(train.all,file='train.all.Rdata')

#raw data diff=30day
rs= dbSendQuery(mydb, 'select * from threela.fundmentalraw where timeid < 20101231 and diff=30')
train.30=fetch(rs, n=-1)
train.30$StockId = factor(train.30$StockId)
train.30$Diff = factor(train.30$Diff)
train.30$growClass = factor(train.30$growClass)
train.30$TimeId= as.Date(as.character(train.30$TimeId),format='%Y%m%d')

#raw data diff=90day
rs= dbSendQuery(mydb, 'select * from threela.fundmentalraw where timeid < 20101231 and diff=90')
train.90=fetch(rs, n=-1)
train.90$StockId = factor(train.90$StockId)
train.90$Diff = factor(train.90$Diff)
train.90$growClass = factor(train.90$growClass)
train.90$TimeId= as.Date(as.character(train.90$TimeId),format='%Y%m%d')

#raw data diff=180day
rs= dbSendQuery(mydb, 'select * from threela.fundmentalraw where timeid < 20101231 and diff=180')
train.180=fetch(rs, n=-1)
train.180$StockId = factor(train.180$StockId)
train.180$Diff = factor(train.180$Diff)
train.180$growClass = factor(train.180$growClass)
train.180$TimeId= as.Date(as.character(train.180$TimeId),format='%Y%m%d')

#validate data 30
rs= dbSendQuery(mydb, 'select * from threela.fundmentalraw where timeid > 20101231 and timeid< 20121231 and diff=30')
vd.30=fetch(rs, n=-1)
vd.30$StockId = factor(vd.30$StockId)
vd.30$Diff = factor(vd.30$Diff)
vd.30$growClass = factor(vd.30$growClass)
vd.30$TimeId= as.Date(as.character(vd.30$TimeId),format='%Y%m%d')

#validate data 90
rs= dbSendQuery(mydb, 'select * from threela.fundmentalraw where timeid > 20101231 and timeid< 20121231 and diff=90')
vd.90=fetch(rs, n=-1)
vd.90$StockId = factor(vd.90$StockId)
vd.90$Diff = factor(vd.90$Diff)
vd.90$growClass = factor(vd.90$growClass)
vd.90$TimeId= as.Date(as.character(vd.90$TimeId),format='%Y%m%d')

#validate data 180
rs= dbSendQuery(mydb, 'select * from threela.fundmentalraw where timeid > 20101231 and timeid< 20121231 and diff=180')
vd.180=fetch(rs, n=-1)
vd.180$StockId = factor(vd.180$StockId)
vd.180$Diff = factor(vd.180$Diff)
vd.180$growClass = factor(vd.180$growClass)
vd.180$TimeId= as.Date(as.character(vd.180$TimeId),format='%Y%m%d')


###preprocessing of validation data

vd.30.attr=subset(vd.30,select=c(YieldRate,PE,PBR,EPS,DebtRatio,ROE,MonthRate,YearRate))
vd.30.class=vd.30$growClass

vd.90.attr=subset(vd.90,select=c(YieldRate,PE,PBR,EPS,DebtRatio,ROE,MonthRate,YearRate))
vd.90.class=vd.90$growClass

vd.180.attr=subset(vd.180,select=c(YieldRate,PE,PBR,EPS,DebtRatio,ROE,MonthRate,YearRate))
vd.180.class=vd.180$growClass
"

#Export data after season report
#training data for all
rs= dbSendQuery(mydb, 'select * from threela.fundmentalrawseason where timeid < 20101231')
train.sea.all=fetch(rs, n=-1)
train.sea.all$StockId = factor(train.sea.all$StockId)
train.sea.all$Diff = factor(train.sea.all$Diff)
train.sea.all$growClass = factor(train.sea.all$growClass)
train.sea.all$TimeId= as.Date(as.character(train.sea.all$TimeId),format='%Y%m%d')
save(train.sea.all,file='train.sea.all.Rdata')

#raw data diff=30day
rs= dbSendQuery(mydb, 'select * from threela.fundmentalrawseason where timeid < 20101231 and diff=30')
train.sea.30=fetch(rs, n=-1)
train.sea.30$StockId = factor(train.sea.30$StockId)
train.sea.30$Diff = factor(train.sea.30$Diff)
train.sea.30$growClass = factor(train.sea.30$growClass)
train.sea.30$TimeId= as.Date(as.character(train.sea.30$TimeId),format='%Y%m%d')
save(train.sea.30,file='train.sea.30.Rdata')

#raw data diff=90day
rs= dbSendQuery(mydb, 'select * from threela.fundmentalrawseason where timeid < 20101231 and diff=90')
train.sea.90=fetch(rs, n=-1)
train.sea.90$StockId = factor(train.sea.90$StockId)
train.sea.90$Diff = factor(train.sea.90$Diff)
train.sea.90$growClass = factor(train.sea.90$growClass)
train.sea.90$TimeId= as.Date(as.character(train.sea.90$TimeId),format='%Y%m%d')
save(train.sea.90,file='train.sea.90.Rdata')

#raw data diff=180day
rs= dbSendQuery(mydb, 'select * from threela.fundmentalrawseason where timeid < 20101231 and diff=180')
train.sea.180=fetch(rs, n=-1)
train.sea.180$StockId = factor(train.sea.180$StockId)
train.sea.180$Diff = factor(train.sea.180$Diff)
train.sea.180$growClass = factor(train.sea.180$growClass)
train.sea.180$TimeId= as.Date(as.character(train.sea.180$TimeId),format='%Y%m%d')
save(train.sea.180,file='train.sea.180.Rdata')


##decrease growclass
train.sea.30$growClass=droplevels(train.sea.30$growClass)
train.sea.30$growClass[train.sea.30$growRate > 0]='good'
train.sea.30$growClass[train.sea.30$growRate < 0]='bad'
train.sea.30$growClass=factor(train.sea.30$growClass)


#validate data 30
rs= dbSendQuery(mydb, 'select * from threela.fundmentalrawseason where timeid > 20101231 and timeid< 20121231 and diff=30')
vd.sea.30=fetch(rs, n=-1)
vd.sea.30$StockId = factor(vd.sea.30$StockId)
vd.sea.30$Diff = factor(vd.sea.30$Diff)
vd.sea.30$growClass = factor(vd.sea.30$growClass)
vd.sea.30$TimeId= as.Date(as.character(vd.sea.30$TimeId),format='%Y%m%d')

save(vd.sea.30,file='vd.sea.30.Rdata')

#validate data 90
rs= dbSendQuery(mydb, 'select * from threela.fundmentalrawseason where timeid > 20101231 and timeid< 20121231 and diff=90')
vd.sea.90=fetch(rs, n=-1)
vd.sea.90$StockId = factor(vd.sea.90$StockId)
vd.sea.90$Diff = factor(vd.sea.90$Diff)
vd.sea.90$growClass = factor(vd.sea.90$growClass)
vd.sea.90$TimeId= as.Date(as.character(vd.sea.90$TimeId),format='%Y%m%d')
vd.sea.90.attr=subset(vd.sea.90,select=c(YieldRate,PE,PBR,EPS,DebtRatio,ROE,MonthRate,YearRate))
vd.sea.90.class=vd.sea.90$growClass
save(vd.sea.90,file='vd.sea.90.Rdata')

#validate data 180
rs= dbSendQuery(mydb, 'select * from threela.fundmentalrawseason where timeid > 20101231 and timeid< 20121231 and diff=180')
vd.sea.180=fetch(rs, n=-1)
vd.sea.180$StockId = factor(vd.sea.180$StockId)
vd.sea.180$Diff = factor(vd.sea.180$Diff)
vd.sea.180$growClass = factor(vd.sea.180$growClass)
vd.sea.180$TimeId= as.Date(as.character(vd.sea.180$TimeId),format='%Y%m%d')
vd.sea.180.attr=subset(vd.sea.180,select=c(YieldRate,PE,PBR,EPS,DebtRatio,ROE,MonthRate,YearRate))
vd.sea.180.class=vd.sea.180$growClass
save(vd.sea.180,file='vd.sea.180.Rdata')

#generate validation attributre and class data
vd.sea.30$growClass=droplevels(vd.sea.30$growClass)
vd.sea.30$growClass[vd.sea.30$growRate > 0]='good'
vd.sea.30$growClass[vd.sea.30$growRate < 0]='bad'
vd.sea.30$growClass=factor(vd.sea.30$growClass)
vd.sea.30.attr=subset(vd.sea.30,select=c(YieldRate,PE,PBR,EPS,DebtRatio,ROE,MonthRate,YearRate))
vd.sea.30.class=vd.sea.30$growClass