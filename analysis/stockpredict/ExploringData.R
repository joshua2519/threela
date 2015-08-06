#Exploring Data

summary(train.sea.30)
qplot(data = train.sea.30, x = train.sea.30$YieldRate,binwidth=1) + ylab("YielRate")
qplot(data = train.sea.30, x = train.sea.30$PE,binwidth=10) + ylab("PE")
qplot(data = train.sea.30, x = train.sea.30$PBR,binwidth=0.5) + ylab("PBR")
qplot(data = train.sea.30, x = train.sea.30$EPS,binwidth=0.1) + ylab("EPS")
qplot(data = train.sea.30, x = train.sea.30$DebtRatio,binwidth=1) + ylab("DebtRatio")
qplot(data = train.sea.30, x = train.sea.30$ROE,binwidth=10) + ylab("ROE")
qplot(data = train.sea.30, x = train.sea.30$MonthRate,binwidth=100) + ylab("MonthRate")
qplot(data = train.sea.30, x = train.sea.30$YearRate,binwidth=100) + ylab("YearRate")

#rminer
#30days
model=fit(growClass ~YieldRate+PE+PBR+EPS+DebtRatio+ROE+MonthRate+YearRate,train.sea.filter.30,model='svm')
VariableImportance=Importance(model,train.sea.filter.30,method='MSA')
VariableImportance

#correlations and corvariance
train.sea.180.attr=subset(train.sea.180,select=c(YieldRate,PE,PBR,EPS,DebtRatio,ROE,MonthRate,YearRate,growRate))
train.sea.180.attr=train.sea.180.attr[train.sea.180.attr$YieldRate<50 & train.sea.180.attr$PE<50 & train.sea.180.attr$ROE>-50 & train.sea.180.attr$ROE<100 & train.sea.180.attr$MonthRate<100 & train.sea.180.attr$MonthRate > -50 & train.sea.30$YearRate >-100 & train.sea.180.attr$YearRate<100,]
cor(train.sea.180.attr)
cov(train.sea.180.attr)
train.sea.90.attr=subset(train.sea.90,select=c(YieldRate,PE,PBR,EPS,DebtRatio,ROE,MonthRate,YearRate,growRate))
train.sea.90.attr=train.sea.90.attr[train.sea.90.attr$YieldRate<50 & train.sea.90.attr$PE<50 & train.sea.90.attr$ROE>-50 & train.sea.90.attr$ROE<100 & train.sea.90.attr$MonthRate<100 & train.sea.90.attr$MonthRate > -50 & train.sea.30$YearRate >-100 & train.sea.90.attr$YearRate<100,]
cor(train.sea.90.attr)
cov(train.sea.90.attr)
train.sea.30.attr=subset(train.sea.30,select=c(YieldRate,PE,PBR,EPS,DebtRatio,ROE,MonthRate,YearRate,growRate))
train.sea.30.attr=train.sea.30.attr[train.sea.30.attr$YieldRate<50 & train.sea.30.attr$PE<50 & train.sea.30.attr$ROE>-50 & train.sea.30.attr$ROE<100 & train.sea.30.attr$MonthRate<100 & train.sea.30.attr$MonthRate > -50 & train.sea.30$YearRate >-100 & train.sea.30.attr$YearRate<100,]
cor(train.sea.30.attr)
cov(train.sea.30.attr)

train.all.attr=subset(train.all,select=c(YieldRate,PE,PBR,EPS,DebtRatio,ROE,MonthRate,YearRate,growRate))
train.all.attr=train.all.attr[train.all.attr$YieldRate<50 & train.all.attr$PE<50 & train.all.attr$ROE>-50 & train.all.attr$ROE<100 & train.all.attr$MonthRate<100 & train.all.attr$MonthRate > -50 & train.all$YearRate >-100 & train.all.attr$YearRate<100,]
cor(train.all.attr)
cov(train.all.attr)
