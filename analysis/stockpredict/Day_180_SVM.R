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
model.sea.filter.180=svm(growClass ~YieldRate+PE+PBR+EPS+DebtRatio+ROE+MonthRate+YearRate,data=train.sea.filter.180)

##predict model
predict.sea.filter.180=predict(model.sea.filter.180,vd.sea.180.attr)


##check accuracy
table(predict.sea.filter.180,vd.sea.180.class)
confusionMatrix(table(predict.sea.filter.180,vd.sea.180.class))


#training model--with tunning

model.sea.filter.180.tune=svm(growClass ~YieldRate+PE+PBR+EPS+DebtRatio+ROE+MonthRate+YearRate,data=train.sea.filter.180,cost=16,gamma=0.0625)

##predict model
predict.sea.filter.180.tune=predict(model.sea.filter.180.tune,vd.sea.180.attr)

##check accuracy
table(predict.sea.filter.180.tune,vd.sea.180.class)
confusionMatrix(table(predict.sea.filter.180.tune,vd.sea.180.class))



#training model--with type-2

model.sea.filter.180.type=svm(growClass ~YieldRate+PE+PBR+EPS+DebtRatio+ROE+MonthRate+YearRate,data=train.sea.filter.180,type='nu-classification')

##predict model
predict.sea.filter.180.type=predict(model.sea.filter.180.type,vd.sea.180.attr)

##check accuracy
table(predict.sea.filter.180.type,vd.sea.180.class)
confusionMatrix(table(predict.sea.filter.180.type,vd.sea.180.class))


#training model--with type-2 and tune

model.sea.filter.180.type.tune=svm(growClass ~YieldRate+PE+PBR+EPS+DebtRatio+ROE+MonthRate+YearRate,data=train.sea.filter.180,type='nu-classification',cost=16,gamma=0.0625)

##predict model
predict.sea.filter.180.type.tune=predict(model.sea.filter.180.type.tune,vd.sea.180.attr)

##check accuracy
table(predict.sea.filter.180.type.tune,vd.sea.180.class)
confusionMatrix(table(predict.sea.filter.180.type.tune,vd.sea.180.class))


#plot model
plot(model.sea.filter.180, train.sea.filter.180, YieldRate ~ PE,color.palette = terrain.colors)
plot(model.sea.filter.180, train.sea.filter.180, YieldRate ~ PBR,color.palette = terrain.colors)
plot(model.sea.filter.180, train.sea.filter.180, YieldRate ~ EPS,color.palette = terrain.colors)
plot(model.sea.filter.180, train.sea.filter.180, YieldRate ~ ROE,color.palette = terrain.colors)








#plot model