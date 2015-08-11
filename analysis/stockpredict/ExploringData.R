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

train.sea.30=train.sea.30[train.sea.30$YieldRate<50 & train.sea.30$PE<50 & train.sea.30$ROE>-50 & train.sea.30$ROE<100 & train.sea.30$MonthRate<100 & train.sea.30$MonthRate > -50 & train.sea.30$YearRate >-100 & train.sea.30$YearRate<100,]
train.sea.30$growClass=droplevels(train.sea.30$growClass)
train.sea.30$growClass[train.sea.30$growRate >= 0]='good'
train.sea.30$growClass[train.sea.30$growRate < 0]='bad'
train.sea.30$growClass=factor(train.sea.30$growClass)

train.sea.90=train.sea.90[train.sea.90$YieldRate<50 & train.sea.90$PE<50 & train.sea.90$ROE>-50 & train.sea.90$ROE<100 & train.sea.90$MonthRate<100 & train.sea.90$MonthRate > -50 & train.sea.90$YearRate >-100 & train.sea.90$YearRate<100,]
train.sea.90$growClass=droplevels(train.sea.90$growClass)
train.sea.90$growClass[train.sea.90$growRate >= 0]='good'
train.sea.90$growClass[train.sea.90$growRate < 0]='bad'
train.sea.90$growClass=factor(train.sea.90$growClass)

train.sea.180=train.sea.180[train.sea.180$YieldRate<50 & train.sea.180$PE<50 & train.sea.180$ROE>-50 & train.sea.180$ROE<100 & train.sea.180$MonthRate<100 & train.sea.180$MonthRate > -50 & train.sea.180$YearRate >-100 & train.sea.180$YearRate<100,]
train.sea.180$growClass=droplevels(train.sea.180$growClass)
train.sea.180$growClass[train.sea.180$growRate >= 0]='good'
train.sea.180$growClass[train.sea.180$growRate < 0]='bad'
train.sea.180$growClass=factor(train.sea.180$growClass)



plot(train.sea.30$growRate,train.sea.30$EPS)
plot(train.sea.30$growRate,train.sea.30$ROE)
plot(train.sea.30$growRate,train.sea.30$MonthRate)
plot(train.sea.30$growRate,train.sea.30$YearRate)

###scatterplot
qplot (YieldRate, PE, data = train.sea.30, colour = growClass)
qplot (YieldRate, PBR, data = train.sea.30, colour = growClass)
qplot (YieldRate, EPS, data = train.sea.30, colour = growClass)
qplot (YieldRate, DebtRatio, data = train.sea.30, colour = growClass)
qplot (YieldRate, ROE, data = train.sea.30, colour = growClass)
qplot (YieldRate, MonthRate, data = train.sea.30, colour = growClass)
qplot (YieldRate, YearRate, data = train.sea.30, colour = growClass)
qplot (PE, PBR, data = train.sea.30, colour = growClass)
qplot (PE, EPS, data = train.sea.30, colour = growClass)
qplot (PE, DebtRatio, data = train.sea.30, colour = growClass)
qplot (PE, ROE, data = train.sea.30, colour = growClass)
qplot (PE, MonthRate, data = train.sea.30, colour = growClass)
qplot (PE, YearRate, data = train.sea.30, colour = growClass)
qplot (PBR, EPS, data = train.sea.30, colour = growClass)
qplot (PBR, DebtRatio, data = train.sea.30, colour = growClass)
qplot (PBR, ROE, data = train.sea.30, colour = growClass)
qplot (PBR, MonthRate, data = train.sea.30, colour = growClass)
qplot (PBR, YearRate, data = train.sea.30, colour = growClass)
qplot (EPS, DebtRatio, data = train.sea.30, colour = growClass)
qplot (EPS, ROE, data = train.sea.30, colour = growClass)
qplot (EPS, MonthRate, data = train.sea.30, colour = growClass)
qplot (EPS, YearRate, data = train.sea.30, colour = growClass)
qplot (DebtRatio, ROE, data = train.sea.30, colour = growClass)
qplot (DebtRatio, MonthRate, data = train.sea.30, colour = growClass)
qplot (DebtRatio, YearRate, data = train.sea.30, colour = growClass)
qplot (ROE, MonthRate, data = train.sea.30, colour = growClass)
qplot (ROE, YearRate, data = train.sea.30, colour = growClass)
qplot (MonthRate, YearRate, data = train.sea.30, colour = growClass)

#variables ~ growRate
qplot ( YieldRate,growRate, data = train.sea.30, colour = growClass)
qplot ( PE,growRate, data = train.sea.30, colour = growClass)
qplot ( PBR,growRate, data = train.sea.30, colour = growClass)
qplot ( EPS,growRate, data = train.sea.30, colour = growClass)
qplot ( DebtRatio,growRate, data = train.sea.30, colour = growClass)
qplot ( ROE,growRate, data = train.sea.30, colour = growClass)
qplot ( MonthRate,growRate, data = train.sea.30, colour = growClass)
qplot ( YearRate,growRate, data = train.sea.30, colour = growClass)

qplot ( YieldRate,growRate, data = train.sea.90, colour = growClass)
qplot ( PE,growRate, data = train.sea.90, colour = growClass)
qplot ( PBR,growRate, data = train.sea.90, colour = growClass)
qplot ( EPS,growRate, data = train.sea.90, colour = growClass)
qplot ( DebtRatio,growRate, data = train.sea.90, colour = growClass)
qplot ( ROE,growRate, data = train.sea.90, colour = growClass)
qplot ( MonthRate,growRate, data = train.sea.90, colour = growClass)
qplot ( YearRate,growRate, data = train.sea.90, colour = growClass)


qplot ( YieldRate,growRate, data = train.sea.180, colour = growClass)
qplot ( PE,growRate, data = train.sea.180, colour = growClass)
qplot ( PBR,growRate, data = train.sea.180, colour = growClass)
qplot ( EPS,growRate, data = train.sea.180, colour = growClass)
qplot ( DebtRatio,growRate, data = train.sea.180, colour = growClass)
qplot ( ROE,growRate, data = train.sea.180, colour = growClass)
qplot ( MonthRate,growRate, data = train.sea.180, colour = growClass)
qplot ( YearRate,growRate, data = train.sea.180, colour = growClass)

#rminer
#30days
model=fit(growClass ~YieldRate+PE+PBR+EPS+DebtRatio+ROE+MonthRate+YearRate,train.sea.filter.30,model='svm')
VariableImportance=Importance(model,train.sea.filter.30,method='MSA')



#cor-variance
summary(train.180)
train.180=train.180[train.180$YieldRate<50 & train.180$PE<50 & train.180$ROE>-50 & train.180$ROE<100 & train.180$MonthRate<100 & train.180$MonthRate > -50 & train.180$YearRate >-100 & train.180$YearRate<100,]
train.180=subset(train.180,select=c(YieldRate,PE,PBR,EPS,DebtRatio,ROE,MonthRate,YearRate,growRate))
cor(train.180,method = c("pearson", "kendall", "spearman"))

summary(train.sea.180)
train.sea.180=train.sea.180[train.sea.180$YieldRate<50 & train.sea.180$PE<50 & train.sea.180$ROE>-50 & train.sea.180$ROE<100 & train.sea.180$MonthRate<100 & train.sea.180$MonthRate > -50 & train.sea.180$YearRate >-100 & train.sea.180$YearRate<100,]
train.sea.180=subset(train.sea.180,select=c(YieldRate,PE,PBR,EPS,DebtRatio,ROE,MonthRate,YearRate,growRate))
cor(train.sea.180,method = c("spearman"))

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
