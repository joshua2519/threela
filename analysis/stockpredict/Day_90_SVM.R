#decrease growclass
train.sea.90$growClass=droplevels(train.sea.90$growClass)
train.sea.90$growClass[train.sea.90$growRate >= 0]='good'
train.sea.90$growClass[train.sea.90$growRate < 0]='bad'
train.sea.90$growClass=factor(train.sea.90$growClass)

#filter data
train.sea.filter.90=train.sea.90[train.sea.90$YieldRate<50 & train.sea.90$PE<50 & train.sea.90$ROE>-50 & train.sea.90$ROE<100 & train.sea.90$MonthRate<100 & train.sea.90$MonthRate > -50 & train.sea.90$YearRate >-100 & train.sea.90$YearRate<100,]

#sample filtered training dataset
"
train.sea.filter.90.sample=rbind(train.sea.filter.90[train.sea.filter.90$growClass=='bad',][sample(nrow(train.sea.filter.90[train.sea.filter.90$growClass=='bad',]),4900),],
train.sea.filter.90[train.sea.filter.90$growClass=='good',][sample(nrow(train.sea.filter.90[train.sea.filter.90$growClass=='good',]),4900),])
"

#validation data
vd.sea.90$growClass=droplevels(vd.sea.90$growClass)
vd.sea.90$growClass[vd.sea.90$growRate >= 0]='good'
vd.sea.90$growClass[vd.sea.90$growRate < 0]='bad'
vd.sea.90$growClass=factor(vd.sea.90$growClass)

vd.sea.90.attr=subset(vd.sea.90,select=c(YieldRate,PE,PBR,EPS,DebtRatio,ROE,MonthRate,YearRate))
vd.sea.90.class=vd.sea.90$growClass


#training model--default
model.sea.filter.90=svm(growClass ~YieldRate+PE+PBR+EPS+DebtRatio+ROE+MonthRate+YearRate,data=train.sea.filter.90)

##predict model
predict.sea.filter.90=predict(model.sea.filter.90,vd.sea.90.attr)


##check accuracy
table(predict.sea.filter.90,vd.sea.90.class)
confusionMatrix(table(predict.sea.filter.90,vd.sea.90.class))


#training model--with tunning

model.sea.filter.90.tune=svm(growClass ~YieldRate+PE+PBR+EPS+DebtRatio+ROE+MonthRate+YearRate,data=train.sea.filter.90,cost=16,gamma=0.0625)

##predict model
predict.sea.filter.90.tune=predict(model.sea.filter.90.tune,vd.sea.90.attr)

##check accuracy
table(predict.sea.filter.90.tune,vd.sea.90.class)
confusionMatrix(table(predict.sea.filter.90.tune,vd.sea.90.class))



#training model--with type-2

model.sea.filter.90.type=svm(growClass ~YieldRate+PE+PBR+EPS+DebtRatio+ROE+MonthRate+YearRate,data=train.sea.filter.90,type='nu-classification')

##predict model
predict.sea.filter.90.type=predict(model.sea.filter.90.type,vd.sea.90.attr)

##check accuracy
table(predict.sea.filter.90.type,vd.sea.90.class)
confusionMatrix(table(predict.sea.filter.90.type,vd.sea.90.class))


#training model--with type-2 and tune

model.sea.filter.90.type.tune=svm(growClass ~YieldRate+PE+PBR+EPS+DebtRatio+ROE+MonthRate+YearRate,data=train.sea.filter.90,type='nu-classification',cost=16,gamma=0.0625)

##predict model
predict.sea.filter.90.type.tune=predict(model.sea.filter.90.type.tune,vd.sea.90.attr)

##check accuracy
table(predict.sea.filter.90.type.tune,vd.sea.90.class)
confusionMatrix(table(predict.sea.filter.90.type.tune,vd.sea.90.class))


#plot model
plot(model.sea.filter.90, train.sea.filter.90, YieldRate ~ PE,color.palette = terrain.colors)
plot(model.sea.filter.90, train.sea.filter.90, YieldRate ~ PBR,color.palette = terrain.colors)
plot(model.sea.filter.90, train.sea.filter.90, YieldRate ~ EPS,color.palette = terrain.colors)
plot(model.sea.filter.90, train.sea.filter.90, YieldRate ~ ROE,color.palette = terrain.colors)








#plot model