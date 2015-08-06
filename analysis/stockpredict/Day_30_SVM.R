#decrease growclass
train.sea.30$growClass=droplevels(train.sea.30$growClass)
train.sea.30$growClass[train.sea.30$growRate >= 0]='good'
train.sea.30$growClass[train.sea.30$growRate < 0]='bad'
train.sea.30$growClass=factor(train.sea.30$growClass)

#filter data
train.sea.filter.30=train.sea.30[train.sea.30$YieldRate<50 & train.sea.30$PE<50 & train.sea.30$ROE>-50 & train.sea.30$ROE<100 & train.sea.30$MonthRate<100 & train.sea.30$MonthRate > -50 & train.sea.30$YearRate >-100 & train.sea.30$YearRate<100,]

#sample filtered training dataset
"
train.sea.filter.30.sample=rbind(train.sea.filter.30[train.sea.filter.30$growClass=='bad',][sample(nrow(train.sea.filter.30[train.sea.filter.30$growClass=='bad',]),4900),],
                                 train.sea.filter.30[train.sea.filter.30$growClass=='good',][sample(nrow(train.sea.filter.30[train.sea.filter.30$growClass=='good',]),4900),])
"

#validation data
vd.sea.30$growClass=droplevels(vd.sea.30$growClass)
vd.sea.30$growClass[vd.sea.30$growRate >= 0]='good'
vd.sea.30$growClass[vd.sea.30$growRate < 0]='bad'
vd.sea.30$growClass=factor(vd.sea.30$growClass)

vd.sea.30.attr=subset(vd.sea.30,select=c(YieldRate,PE,PBR,EPS,DebtRatio,ROE,MonthRate,YearRate))
vd.sea.30.class=vd.sea.30$growClass


#training model--default
model.sea.filter.30=svm(growClass ~YieldRate+PE+PBR+EPS+DebtRatio+ROE+MonthRate+YearRate,data=train.sea.filter.30)

##predict model
predict.sea.filter.30=predict(model.sea.filter.30,vd.sea.30.attr)


##check accuracy
table(predict.sea.filter.30,vd.sea.30.class)
confusionMatrix(table(predict.sea.filter.30,vd.sea.30.class))


#training model--with tunning

model.sea.filter.30.tune=svm(growClass ~YieldRate+PE+PBR+EPS+DebtRatio+ROE+MonthRate+YearRate,data=train.sea.filter.30,cost=16,gamma=0.0625)

##predict model
predict.sea.filter.30.tune=predict(model.sea.filter.30.tune,vd.sea.30.attr)

##check accuracy
table(predict.sea.filter.30.tune,vd.sea.30.class)
confusionMatrix(table(predict.sea.filter.30.tune,vd.sea.30.class))



#training model--with type-2

model.sea.filter.30.type=svm(growClass ~YieldRate+PE+PBR+EPS+DebtRatio+ROE+MonthRate+YearRate,data=train.sea.filter.30,type='nu-classification')

##predict model
predict.sea.filter.30.type=predict(model.sea.filter.30.type,vd.sea.30.attr)

##check accuracy
table(predict.sea.filter.30.type,vd.sea.30.class)
confusionMatrix(table(predict.sea.filter.30.type,vd.sea.30.class))


#training model--with type-2 and tune

model.sea.filter.30.type.tune=svm(growClass ~YieldRate+PE+PBR+EPS+DebtRatio+ROE+MonthRate+YearRate,data=train.sea.filter.30,type='nu-classification',cost=16,gamma=0.0625)

##predict model
predict.sea.filter.30.type.tune=predict(model.sea.filter.30.type.tune,vd.sea.30.attr)

##check accuracy
table(predict.sea.filter.30.type.tune,vd.sea.30.class)
confusionMatrix(table(predict.sea.filter.30.type.tune,vd.sea.30.class))


#plot model
plot(model.sea.filter.30, train.sea.filter.30, YieldRate ~ PE,color.palette = terrain.colors)
plot(model.sea.filter.30, train.sea.filter.30, YieldRate ~ PBR,color.palette = terrain.colors)
plot(model.sea.filter.30, train.sea.filter.30, YieldRate ~ EPS,color.palette = terrain.colors)
plot(model.sea.filter.30, train.sea.filter.30, YieldRate ~ ROE,color.palette = terrain.colors)








#plot model