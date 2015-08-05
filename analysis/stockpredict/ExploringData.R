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
