#decrease growclass
train.sea.180$growClass=droplevels(train.sea.180$growClass)
train.sea.180$growClass[train.sea.180$growRate >= 0]='good'
train.sea.180$growClass[train.sea.180$growRate < 0]='bad'
train.sea.180$growClass=factor(train.sea.180$growClass)

#filter data
train.sea.filter.180=train.sea.180[train.sea.180$YieldRate<50 & train.sea.180$PE<50 & train.sea.180$ROE>-50 & train.sea.180$ROE<100 & train.sea.180$MonthRate<100 & train.sea.180$MonthRate > -50 & train.sea.180$YearRate >-100 & train.sea.180$YearRate<100,]

#sample filtered training dataset
"
train.sea.filter.180.sample=rbind(train.sea.filter.180[train.sea.filter.180$growClass=='bad',][sample(nrow(train.sea.filter.180[train.sea.filter.180$growClass=='bad',]),41800),],
train.sea.filter.180[train.sea.filter.180$growClass=='good',][sample(nrow(train.sea.filter.180[train.sea.filter.180$growClass=='good',]),41800),])
"

#validation data
vd.sea.180$growClass=droplevels(vd.sea.180$growClass)
vd.sea.180$growClass[vd.sea.180$growRate >= 0]='good'
vd.sea.180$growClass[vd.sea.180$growRate < 0]='bad'
vd.sea.180$growClass=factor(vd.sea.180$growClass)

vd.sea.180.attr=subset(vd.sea.180,select=c(YieldRate,PE,PBR,EPS,DebtRatio,ROE,MonthRate,YearRate))
vd.sea.180.class=vd.sea.180$growClass


#training model--default
ctree.train.sea.180=ctree(growClass ~YieldRate+PE+PBR+EPS+DebtRatio+ROE+MonthRate+YearRate,data=train.sea.filter.180)
plot(ctree.train.sea.180)

##predict model
predict.ctree.sea.filter.180=predict(ctree.train.sea.180,vd.sea.180.attr)

##check accuracy
table(predict.ctree.sea.filter.180,vd.sea.180.class)
confusionMatrix(table(predict.ctree.sea.filter.180,vd.sea.180.class))

##rocr
predict.ctree.sea.filter.180=predict(ctree.train.sea.180,vd.sea.180,type='prob')
odd <- function(x) x%%2 != 0 
even <- function(x) x%%2 == 0 
pred.to.roc=unlist(predict.ctree.sea.filter.180)[even(1:length(unlist(predict.ctree.sea.filter.180)))]
pred.rocr <- prediction(pred.to.roc, vd.sea.180.class)
perf.tpr.rocr <- performance(pred.rocr,'tpr','fpr')
plot(perf.tpr.rocr, colorize=T,main=paste("AUC:",(perf.rocr@y.values)))

