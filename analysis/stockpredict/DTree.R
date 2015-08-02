#rpart test
train=train.90
train.subset=subset(train,select=c(YieldRate,PE,PBR,EPS,DebtRatio,ROE,MonthRate,YearRate,growClass))
stocktree.rp=rpart(growClass ~.,data=train1.subset)
plot(stocktree.rp, margin=0.1)
text(stocktree.rp,all=TRUE,use.n=TRUE)

#ctree