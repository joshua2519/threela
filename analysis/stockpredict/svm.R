#svm model
#create training dataset
train.30.attr=subset(train.30,select=c(YieldRate,PE,PBR,EPS,DebtRatio,ROE,MonthRate,YearRate,growClass))
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
#create training dataset
train.sea.30.attr=subset(train.sea.30,select=c(YieldRate,PE,PBR,EPS,DebtRatio,ROE,MonthRate,YearRate,growClass))
train.sea.90.attr=subset(train.sea.90,select=c(YieldRate,PE,PBR,EPS,DebtRatio,ROE,MonthRate,YearRate,growClass))
train.sea.180.attr=subset(train.sea.180,select=c(YieldRate,PE,PBR,EPS,DebtRatio,ROE,MonthRate,YearRate,growClass))

#training model
model.sea.30=svm(growClass ~ .,data=train.sea.30.attr)
model.sea.90=svm(growClass ~ .,data=train.sea.90.attr)
model.sea.180=svm(growClass ~ .,data=train.sea.180.attr)

#prediction with validation data
predict.sea.30=predict(model.sea.30,vd.sea.30.attr)
predict.sea.90=predict(model.sea.90,vd.sea.90.attr)
predict.sea.180=predict(model.sea.180,vd.sea.180.attr)

#check accuracy
table(predict.sea.30,vd.sea.30.class)
table(predict.sea.90,vd.sea.90.class)
table(predict.sea.180,vd.sea.180.class)
