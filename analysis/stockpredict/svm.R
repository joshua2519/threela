#svm model

#30days
##create training dataset
train.30.attr=subset(train.30,select=c(YieldRate,PE,PBR,EPS,DebtRatio,ROE,MonthRate,YearRate,growClass))
##sample training dataset
train.30.attr.sample.bad=train.30.attr[train.30.attr$growClass=='bad',][sample(nrow(train.30.attr[train.30.attr$growClass=='bad',]),3900),]
train.30.attr.sample.good=train.30.attr[train.30.attr$growClass=='good',][sample(nrow(train.30.attr[train.30.attr$growClass=='good',]),3900),]
train.30.attr.sample.great=train.30.attr[train.30.attr$growClass=='great',][sample(nrow(train.30.attr[train.30.attr$growClass=='great',]),3900),]
train.30.attr.sample.none=train.30.attr[train.30.attr$growClass=='none',][sample(nrow(train.30.attr[train.30.attr$growClass=='none',]),3900),]
train.30.attr.sample.worse=train.30.attr[train.30.attr$growClass=='worse',][sample(nrow(train.30.attr[train.30.attr$growClass=='worse',]),3900),]
train.30.attr.sample=rbind(train.30.attr.sample.bad,
                               train.30.attr.sample.good,
                               train.30.attr.sample.great,
                               train.30.attr.sample.none,
                               train.30.attr.sample.worse)
##training model
model.sample.30=svm(growClass ~ .,data=train.30.attr.sample)
##svm tune


##prediction with validation data
predict.sample.30=predict(model.sample.30,vd.30.attr)
##check accuracy
table(predict.sample.30,vd.30.class)
confusionMatrix(table(predict.sample.30,vd.30.class))


#create training dataset

train.90.attr=subset(train.90,select=c(YieldRate,PE,PBR,EPS,DebtRatio,ROE,MonthRate,YearRate,growClass))
train.180.attr=subset(train.180,select=c(YieldRate,PE,PBR,EPS,DebtRatio,ROE,MonthRate,YearRate,growClass))

#training model
model.30=svm(growClass ~ .,data=train.30.attr)
model.90=svm(growClass ~ .,data=train.90.attr)
model.180=svm(growClass ~ .,data=train.180.attr)

#prediction with validation data
predict.30=predict(model.30,vd.30.attr)
predict.90=predict(model.90,vd.90.attr)
predict.180=predict(model.180,vd.180.attr)

#check accuracy
table(predict.30,vd.30.class)
table(predict.90,vd.90.class)
table(predict.180,vd.180.class)

#Season dataset
##30 days
###create training dataset
#train.sea.30.attr=subset(train.sea.30,select=c(YieldRate,PE,PBR,EPS,DebtRatio,ROE,MonthRate,YearRate,growClass))
###sample training dataset
#train.sea.30.attr.sample.bad=train.sea.30.attr[train.sea.30.attr$growClass=='bad',][sample(nrow(train.sea.30.attr[train.sea.30.attr$growClass=='bad',]),900),]
#train.sea.30.attr.sample.good=train.sea.30.attr[train.sea.30.attr$growClass=='good',][sample(nrow(train.sea.30.attr[train.sea.30.attr$growClass=='good',]),900),]
#train.sea.30.attr.sample.great=train.sea.30.attr[train.sea.30.attr$growClass=='great',][sample(nrow(train.sea.30.attr[train.sea.30.attr$growClass=='great',]),900),]
#train.sea.30.attr.sample.none=train.sea.30.attr[train.sea.30.attr$growClass=='none',][sample(nrow(train.sea.30.attr[train.sea.30.attr$growClass=='none',]),900),]
#train.sea.30.attr.sample.worse=train.sea.30.attr[train.sea.30.attr$growClass=='worse',][sample(nrow(train.sea.30.attr[train.sea.30.attr$growClass=='worse',]),900),]
#train.sea.30.attr.sample=rbind(train.sea.30.attr.sample.bad,
#                               train.sea.30.attr.sample.good,
#                               train.sea.30.attr.sample.great,
#                               train.sea.30.attr.sample.none,
#                               train.sea.30.attr.sample.worse)
###filter data
train.sea.filter.30=train.sea.30[train.sea.30$YieldRate<50 & train.sea.30$PE<50 & train.sea.30$ROE>-50 & train.sea.30$ROE<100 & train.sea.30$MonthRate<100 & train.sea.30$MonthRate > -50 & train.sea.30$YearRate >-100 & train.sea.30$YearRate<100,]

###sample filtered training dataset

train.sea.filter.30.bad=train.sea.filter.30[train.sea.filter.30$growClass=='bad',][sample(nrow(train.sea.filter.30[train.sea.filter.30$growClass=='bad',]),700),]
train.sea.filter.30.good=train.sea.filter.30[train.sea.filter.30$growClass=='good',][sample(nrow(train.sea.filter.30[train.sea.filter.30$growClass=='good',]),700),]
train.sea.filter.30.great=train.sea.filter.30[train.sea.filter.30$growClass=='great',][sample(nrow(train.sea.filter.30[train.sea.filter.30$growClass=='great',]),700),]
train.sea.filter.30.none=train.sea.filter.30[train.sea.filter.30$growClass=='none',][sample(nrow(train.sea.filter.30[train.sea.filter.30$growClass=='none',]),700),]
train.sea.filter.30.worse=train.sea.filter.30[train.sea.filter.30$growClass=='worse',][sample(nrow(train.sea.filter.30[train.sea.filter.30$growClass=='worse',]),700),]
train.sea.filter.30.sample=rbind(train.sea.filter.30.bad,
                                 train.sea.filter.30.good,
                                 train.sea.filter.30.great,
                                 train.sea.filter.30.none,
                                 train.sea.filter.30.worse)

###training model
model.sea.30=svm(growClass ~YieldRate+PE+PBR+EPS+DebtRatio+ROE+MonthRate+YearRate,data=train.sea.30)
model.sea.sample.30=svm(growClass ~ .,data=train.sea.30.attr.sample)
model.sea.filter.sample.30=svm(growClass ~YieldRate+PE+PBR+EPS+DebtRatio+ROE+MonthRate+YearRate,data=train.sea.filter.30.sample,cost=1,gamma=0.0625)
model.sea.filter.30=svm(growClass ~YieldRate+PE+PBR+EPS+DebtRatio+ROE+MonthRate+YearRate,data=train.sea.filter.30)
model.sea.filter.30=svm(growClass ~YieldRate+PE+PBR+EPS+DebtRatio+ROE+MonthRate+YearRate,data=train.sea.filter.30,cost=16,gamma=0.0625)


## svm tune
obj <- tune.svm(growClass~YieldRate+PE+PBR+EPS+DebtRatio+ROE+MonthRate+YearRate, data = train.sea.filter.30, sampling = "fix", gamma = 2^c(-8,-4,0,4,8), cost = 2^c(-8,-4,-2,0,2,4,8))
plot(obj, transform.x = log2, transform.y = log2)
plot(obj, type = "perspective", theta = 120, phi = 45)

###prediction with validation data
#predict.sea.30=predict(model.sea.30,vd.sea.30.attr)
#predict.sea.sample.30=predict(model.sea.sample.30,vd.sea.30.attr)
#predict.sea.filter.sample.30=predict(model.sea.filter.sample.30,vd.sea.30.attr)
predict.sea.filter.30=predict(model.sea.filter.30,vd.sea.30.attr)
###plot model
plot(model.sea.sample.30, train.sea.30.attr.sample, YieldRate ~ PE,color.palette = terrain.colors)
plot(model.sea.filter.sample.30, train.sea.filter.30.sample, YieldRate ~ PE,color.palette = terrain.colors)
plot(model.sea.filter.30, train.sea.filter.30, YieldRate ~ PE,color.palette = terrain.colors)

###check accuracy
#table(predict.sea.30,vd.sea.30.class)
#confusionMatrix(table(predict.sea.30,vd.sea.30.class))

#table(predict.sea.sample.30,vd.sea.30.class)
#confusionMatrix(table(predict.sea.sample.30,vd.sea.30.class))

#table(predict.sea.filter.sample.30,vd.sea.30.class)
#confusionMatrix(table(predict.sea.filter.sample.30,vd.sea.30.class))

table(predict.sea.filter.30,vd.sea.30.class)
confusionMatrix(table(predict.sea.filter.30,vd.sea.30.class))

train.sea.90.attr=subset(train.sea.90,select=c(YieldRate,PE,PBR,EPS,DebtRatio,ROE,MonthRate,YearRate,growClass))
train.sea.180.attr=subset(train.sea.180,select=c(YieldRate,PE,PBR,EPS,DebtRatio,ROE,MonthRate,YearRate,growClass))



model.sea.90=svm(growClass ~ .,data=train.sea.90.attr)
model.sea.180=svm(growClass ~ .,data=train.sea.180.attr)


predict.sea.90=predict(model.sea.90,vd.sea.90.attr)
predict.sea.180=predict(model.sea.180,vd.sea.180.attr)


table(predict.sea.90,vd.sea.90.class)
table(predict.sea.180,vd.sea.180.class)
