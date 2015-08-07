#Load the following five files in the beginning
REV1=read.csv("RevQGrate.csv",header=TRUE,sep=",")
EPS1=read.csv("EPSGrate.csv",header=TRUE,sep=",")
#QxDate=read.csv("QxDate0",header=FALSE,sep=",")
QxDate=read.csv("QxDate1",header=FALSE,sep=",")
#QxDate=read.csv("QxDate2",header=FALSE,sep=",")
p1=read.csv("ClosePrice1.csv",header=TRUE,sep=",")
p2=read.csv("ClosePrice2.csv",header=TRUE,sep=",")

FilterREV_Q3 = function(yy) {
	REV=REV1
	REV=REV[REV$year>(yy-1)&REV$year<(yy+1)&REV$Q3Grate>0.3&REV$Q3Grate<1,]
#	REV=REV[REV$Q1Grate>0.2&REV$Q1Grate<1&REV$Q2Grate>0.2&REV$Q2Grate<1,]
#	REV=REV[REV$Q1Grate>0.25&REV$Q1Grate<1&REV$Q2Grate>0.25&REV$Q2Grate<1,]
	REV=REV[REV$Q1Grate>0.3&REV$Q1Grate<1&REV$Q2Grate>0.3&REV$Q2Grate<1,]
	r1=REV$stockid
	return(r1)
}
FilterEPS_Q3 = function(yy,rr) {
	EPS=EPS1
	EPS=EPS[EPS$year>(yy-1)&EPS$year<(yy+1)&EPS$season>2&EPS$season<4,]
#	EPS=EPS[EPS$eps1>0.5&EPS$eps2>0.5&EPS$eps_grate>0.3&EPS$ROE>(0.025*rr),]
#	EPS=EPS[EPS$eps1>0.5&EPS$eps2>0.5&EPS$eps_grate>0.3&EPS$ROE>(0.05*rr),]
	EPS=EPS[EPS$eps1>0.5&EPS$eps2>0.5&EPS$eps_grate>0.3&EPS$ROE>(0.075*rr),]
	e1=EPS$stockid
	return(e1)
}

#Parse Q3 some conditions and suggest some stocks
p1stk=subset(p2,TimeId==QxDate$V2[(14-7)*6+(3-1)*2+1])$StockId
p2stk=subset(p2,TimeId==QxDate$V2[(14-7)*6+(3-1)*2+2])$StockId
pstk=intersect(p1stk,p2stk)
Res14_Q3=intersect(FilterREV_Q3(2014),FilterEPS_Q3(2014,1))
Res14_Q3=intersect(Res14_Q3,pstk)
price1=subset(p2,StockId==100&TimeId==QxDate$V2[(14-7)*6+(3-1)*2+1])$ClosePrice
price_1=as.numeric(as.character(price1))
price2=subset(p2,StockId==100&TimeId==QxDate$V2[(14-7)*6+(3-1)*2+2])$ClosePrice
price_2=as.numeric(as.character(price2))
i_yROI=round((price_2-price_1)/price_1+1,digits=4)
#2014Q3-ROI
ROI=0
for (i in 1:length(Res14_Q3))
{
  price1=subset(p2,StockId==Res14_Q3[i]&TimeId==QxDate$V2[(14-7)*6+(3-1)*2+1])$ClosePrice
  price_1=as.numeric(as.character(price1))
  price2=subset(p2,StockId==Res14_Q3[i]&TimeId==QxDate$V2[(14-7)*6+(3-1)*2+2])$ClosePrice
  price_2=as.numeric(as.character(price2))
  if (length(price_1)==0) {
      ROI[i]=99 }
  else {
      if (length(price_2)==0)
          ROI[i]=99 
      else
      	  ROI[i]=(price_2-price_1)/price_1*100 }
  if (i==1)
  	y_ROI=ROI[i]/100+1
  else
  	y_ROI=y_ROI*(ROI[i]/100+1)  	
}
y_ROI=round(y_ROI^(1/length(Res14_Q3)),digits=4)
ROI=round(ROI,digits=2)
x14=rbind(Res14_Q3,ROI)
x14=t(x14)
x14=cbind(2014,x14,y_ROI,i_yROI)
#write.csv(x14,"2014Q3stk.csv",row.names=FALSE)

#Parse Q3 some conditions and suggest some stocks
p1stk=subset(p2,TimeId==QxDate$V2[(13-7)*6+(3-1)*2+1])$StockId
p2stk=subset(p2,TimeId==QxDate$V2[(13-7)*6+(3-1)*2+2])$StockId
pstk=intersect(p1stk,p2stk)
Res13_Q3=intersect(FilterREV_Q3(2013),FilterEPS_Q3(2013,1))
Res13_Q3=intersect(Res13_Q3,pstk)
price1=subset(p2,StockId==100&TimeId==QxDate$V2[(13-7)*6+(3-1)*2+1])$ClosePrice
price_1=as.numeric(as.character(price1))
price2=subset(p2,StockId==100&TimeId==QxDate$V2[(13-7)*6+(3-1)*2+2])$ClosePrice
price_2=as.numeric(as.character(price2))
i_yROI=round((price_2-price_1)/price_1+1,digits=4)
#2013Q3-ROI
ROI=0
for (i in 1:length(Res13_Q3))
{
  price1=subset(p2,StockId==Res13_Q3[i]&TimeId==QxDate$V2[(13-7)*6+(3-1)*2+1])$ClosePrice
  price_1=as.numeric(as.character(price1))
  price2=subset(p2,StockId==Res13_Q3[i]&TimeId==QxDate$V2[(13-7)*6+(3-1)*2+2])$ClosePrice
  price_2=as.numeric(as.character(price2))
  if (length(price_1)==0) {
      ROI[i]=99 }
  else {
      if (length(price_2)==0)
          ROI[i]=99 
      else
      	  ROI[i]=(price_2-price_1)/price_1*100 }
  if (i==1)
  	y_ROI=ROI[i]/100+1
  else
  	y_ROI=y_ROI*(ROI[i]/100+1)  	
}
y_ROI=round(y_ROI^(1/length(Res13_Q3)),digits=4)
ROI=round(ROI,digits=2)
x13=rbind(Res13_Q3,ROI)
x13=t(x13)
x13=cbind(2013,x13,y_ROI,i_yROI)
#write.csv(x13,"2013Q3stk.csv",row.names=FALSE)

#Parse Q3 some conditions and suggest some stocks
p1stk=subset(p2,TimeId==QxDate$V2[(12-7)*6+(3-1)*2+1])$StockId
p2stk=subset(p2,TimeId==QxDate$V2[(12-7)*6+(3-1)*2+2])$StockId
pstk=intersect(p1stk,p2stk)
Res12_Q3=intersect(FilterREV_Q3(2012),FilterEPS_Q3(2012,1))
Res12_Q3=intersect(Res12_Q3,pstk)
price1=subset(p2,StockId==100&TimeId==QxDate$V2[(12-7)*6+(3-1)*2+1])$ClosePrice
price_1=as.numeric(as.character(price1))
price2=subset(p2,StockId==100&TimeId==QxDate$V2[(12-7)*6+(3-1)*2+2])$ClosePrice
price_2=as.numeric(as.character(price2))
i_yROI=round((price_2-price_1)/price_1+1,digits=4)
#2012Q3-ROI
ROI=0
for (i in 1:length(Res12_Q3))
{
  price1=subset(p2,StockId==Res12_Q3[i]&TimeId==QxDate$V2[(12-7)*6+(3-1)*2+1])$ClosePrice
  price_1=as.numeric(as.character(price1))
  price2=subset(p2,StockId==Res12_Q3[i]&TimeId==QxDate$V2[(12-7)*6+(3-1)*2+2])$ClosePrice
  price_2=as.numeric(as.character(price2))
  if (length(price_1)==0) {
      ROI[i]=99 }
  else {
      if (length(price_2)==0)
          ROI[i]=99 
      else
      	  ROI[i]=(price_2-price_1)/price_1*100 }
  if (i==1)
  	y_ROI=ROI[i]/100+1
  else
  	y_ROI=y_ROI*(ROI[i]/100+1)  	
}
y_ROI=round(y_ROI^(1/length(Res12_Q3)),digits=4)
ROI=round(ROI,digits=2)
x12=rbind(Res12_Q3,ROI)
x12=t(x12)
x12=cbind(2012,x12,y_ROI,i_yROI)
#write.csv(x12,"2012Q3stk.csv",row.names=FALSE)

#Parse Q3 some conditions and suggest some stocks
p1stk=subset(p2,TimeId==QxDate$V2[(11-7)*6+(3-1)*2+1])$StockId
p2stk=subset(p2,TimeId==QxDate$V2[(11-7)*6+(3-1)*2+2])$StockId
pstk=intersect(p1stk,p2stk)
Res11_Q3=intersect(FilterREV_Q3(2011),FilterEPS_Q3(2011,1))
Res11_Q3=intersect(Res11_Q3,pstk)
price1=subset(p2,StockId==100&TimeId==QxDate$V2[(11-7)*6+(3-1)*2+1])$ClosePrice
price_1=as.numeric(as.character(price1))
price2=subset(p2,StockId==100&TimeId==QxDate$V2[(11-7)*6+(3-1)*2+2])$ClosePrice
price_2=as.numeric(as.character(price2))
i_yROI=round((price_2-price_1)/price_1+1,digits=4)
#2011Q3-ROI
ROI=0
for (i in 1:length(Res11_Q3))
{
  price1=subset(p2,StockId==Res11_Q3[i]&TimeId==QxDate$V2[(11-7)*6+(3-1)*2+1])$ClosePrice
  price_1=as.numeric(as.character(price1))
  price2=subset(p2,StockId==Res11_Q3[i]&TimeId==QxDate$V2[(11-7)*6+(3-1)*2+2])$ClosePrice
  price_2=as.numeric(as.character(price2))
  if (length(price_1)==0) {
      ROI[i]=99 }
  else {
      if (length(price_2)==0)
          ROI[i]=99 
      else
      	  ROI[i]=(price_2-price_1)/price_1*100 }
  if (i==1)
  	y_ROI=ROI[i]/100+1
  else
  	y_ROI=y_ROI*(ROI[i]/100+1)  	
}
y_ROI=round(y_ROI^(1/length(Res11_Q3)),digits=4)
ROI=round(ROI,digits=2)
x11=rbind(Res11_Q3,ROI)
x11=t(x11)
x11=cbind(2011,x11,y_ROI,i_yROI)
#write.csv(x11,"2011Q3stk.csv",row.names=FALSE)

#Parse Q3 some conditions and suggest some stocks
p1stk=subset(p1,TimeId==QxDate$V2[(10-7)*6+(3-1)*2+1])$StockId
#p2stk=subset(p1,TimeId==QxDate$V2[(10-7)*6+(3-1)*2+2])$StockId
p2stk=subset(p2,TimeId==QxDate$V2[(10-7)*6+(3-1)*2+2])$StockId
pstk=intersect(p1stk,p2stk)
Res10_Q3=intersect(FilterREV_Q3(2010),FilterEPS_Q3(2010,1))
Res10_Q3=intersect(Res10_Q3,pstk)
price1=subset(p1,StockId==100&TimeId==QxDate$V2[(10-7)*6+(3-1)*2+1])$ClosePrice
price_1=as.numeric(as.character(price1))
price2=subset(p2,StockId==100&TimeId==QxDate$V2[(10-7)*6+(3-1)*2+2])$ClosePrice
price_2=as.numeric(as.character(price2))
i_yROI=round((price_2-price_1)/price_1+1,digits=4)
#2010Q3-ROI
ROI=0
for (i in 1:length(Res10_Q3))
{
  price1=subset(p1,StockId==Res10_Q3[i]&TimeId==QxDate$V2[(10-7)*6+(3-1)*2+1])$ClosePrice
  price_1=as.numeric(as.character(price1))
#  price2=subset(p1,StockId==Res10_Q3[i]&TimeId==QxDate$V2[(10-7)*6+(3-1)*2+2])$ClosePrice
  price2=subset(p2,StockId==Res10_Q3[i]&TimeId==QxDate$V2[(10-7)*6+(3-1)*2+2])$ClosePrice
  price_2=as.numeric(as.character(price2))
  if (length(price_1)==0) {
      ROI[i]=99 }
  else {
      if (length(price_2)==0)
          ROI[i]=99 
      else
      	  ROI[i]=(price_2-price_1)/price_1*100 }
  if (i==1)
  	y_ROI=ROI[i]/100+1
  else
  	y_ROI=y_ROI*(ROI[i]/100+1)  	
}
y_ROI=round(y_ROI^(1/length(Res10_Q3)),digits=4)
ROI=round(ROI,digits=2)
x10=rbind(Res10_Q3,ROI)
x10=t(x10)
x10=cbind(2010,x10,y_ROI,i_yROI)
#write.csv(x10,"2010Q3stk.csv",row.names=FALSE)

#Parse Q3 some conditions and suggest some stocks
p1stk=subset(p1,TimeId==QxDate$V2[(9-7)*6+(3-1)*2+1])$StockId
p2stk=subset(p1,TimeId==QxDate$V2[(9-7)*6+(3-1)*2+2])$StockId
pstk=intersect(p1stk,p2stk)
Res09_Q3=intersect(FilterREV_Q3(2009),FilterEPS_Q3(2009,1))
Res09_Q3=intersect(Res09_Q3,pstk)
price1=subset(p1,StockId==100&TimeId==QxDate$V2[(9-7)*6+(3-1)*2+1])$ClosePrice
price_1=as.numeric(as.character(price1))
price2=subset(p1,StockId==100&TimeId==QxDate$V2[(9-7)*6+(3-1)*2+2])$ClosePrice
price_2=as.numeric(as.character(price2))
i_yROI=round((price_2-price_1)/price_1+1,digits=4)
#2009Q3-ROI
ROI=0
for (i in 1:length(Res09_Q3))
{
  price1=subset(p1,StockId==Res09_Q3[i]&TimeId==QxDate$V2[(9-7)*6+(3-1)*2+1])$ClosePrice
  price_1=as.numeric(as.character(price1))
  price2=subset(p1,StockId==Res09_Q3[i]&TimeId==QxDate$V2[(9-7)*6+(3-1)*2+2])$ClosePrice
  price_2=as.numeric(as.character(price2))
  if (length(price_1)==0) {
      ROI[i]=99 }
  else {
      if (length(price_2)==0)
          ROI[i]=99 
      else
      	  ROI[i]=(price_2-price_1)/price_1*100 }
  if (i==1)
  	y_ROI=ROI[i]/100+1
  else
  	y_ROI=y_ROI*(ROI[i]/100+1)  	
}
y_ROI=round(y_ROI^(1/length(Res09_Q3)),digits=4)
ROI=round(ROI,digits=2)
x09=rbind(Res09_Q3,ROI)
x09=t(x09)
x09=cbind(2009,x09,y_ROI,i_yROI)
#write.csv(x09,"2009Q3stk.csv",row.names=FALSE)

#Parse Q3 some conditions and suggest some stocks
p1stk=subset(p1,TimeId==QxDate$V2[(8-7)*6+(3-1)*2+1])$StockId
p2stk=subset(p1,TimeId==QxDate$V2[(8-7)*6+(3-1)*2+2])$StockId
pstk=intersect(p1stk,p2stk)
Res08_Q3=intersect(FilterREV_Q3(2008),FilterEPS_Q3(2008,1))
Res08_Q3=intersect(Res08_Q3,pstk)
price1=subset(p1,StockId==100&TimeId==QxDate$V2[(8-7)*6+(3-1)*2+1])$ClosePrice
price_1=as.numeric(as.character(price1))
price2=subset(p1,StockId==100&TimeId==QxDate$V2[(8-7)*6+(3-1)*2+2])$ClosePrice
price_2=as.numeric(as.character(price2))
i_yROI=round((price_2-price_1)/price_1+1,digits=4)
#2008Q3-ROI
ROI=0
for (i in 1:length(Res08_Q3))
{
  price1=subset(p1,StockId==Res08_Q3[i]&TimeId==QxDate$V2[(8-7)*6+(3-1)*2+1])$ClosePrice
  price_1=as.numeric(as.character(price1))
  price2=subset(p1,StockId==Res08_Q3[i]&TimeId==QxDate$V2[(8-7)*6+(3-1)*2+2])$ClosePrice
  price_2=as.numeric(as.character(price2))
  if (length(price_1)==0) {
      ROI[i]=99 }
  else {
      if (length(price_2)==0)
          ROI[i]=99 
      else
      	  ROI[i]=(price_2-price_1)/price_1*100 }
  if (i==1)
  	y_ROI=ROI[i]/100+1
  else
  	y_ROI=y_ROI*(ROI[i]/100+1)  	
}
y_ROI=round(y_ROI^(1/length(Res08_Q3)),digits=4)
ROI=round(ROI,digits=2)
x08=rbind(Res08_Q3,ROI)
x08=t(x08)
x08=cbind(2008,x08,y_ROI,i_yROI)
#write.csv(x08,"2008Q3stk.csv",row.names=FALSE)

#Parse Q3 some conditions and suggest some stocks
p1stk=subset(p1,TimeId==QxDate$V2[(7-7)*6+(3-1)*2+1])$StockId
p2stk=subset(p1,TimeId==QxDate$V2[(7-7)*6+(3-1)*2+2])$StockId
pstk=intersect(p1stk,p2stk)
Res07_Q3=intersect(FilterREV_Q3(2007),FilterEPS_Q3(2007,1))
Res07_Q3=intersect(Res07_Q3,pstk)
price1=subset(p1,StockId==100&TimeId==QxDate$V2[(7-7)*6+(3-1)*2+1])$ClosePrice
price_1=as.numeric(as.character(price1))
price2=subset(p1,StockId==100&TimeId==QxDate$V2[(7-7)*6+(3-1)*2+2])$ClosePrice
price_2=as.numeric(as.character(price2))
i_yROI=round((price_2-price_1)/price_1+1,digits=4)
#2007Q3-ROI
ROI=0
for (i in 1:length(Res07_Q3))
{
  price1=subset(p1,StockId==Res07_Q3[i]&TimeId==QxDate$V2[(7-7)*6+(3-1)*2+1])$ClosePrice
  price_1=as.numeric(as.character(price1))
  price2=subset(p1,StockId==Res07_Q3[i]&TimeId==QxDate$V2[(7-7)*6+(3-1)*2+2])$ClosePrice
  price_2=as.numeric(as.character(price2))
  if (length(price_1)==0) {
      ROI[i]=99 }
  else {
      if (length(price_2)==0)
          ROI[i]=99 
      else
      	  ROI[i]=(price_2-price_1)/price_1*100 }
  if (i==1)
  	y_ROI=ROI[i]/100+1
  else
  	y_ROI=y_ROI*(ROI[i]/100+1)  	
}
y_ROI=round(y_ROI^(1/length(Res07_Q3)),digits=4)
ROI=round(ROI,digits=2)
x07=rbind(Res07_Q3,ROI)
x07=t(x07)
x07=cbind(2007,x07,y_ROI,i_yROI)
#write.csv(x,"2007Q3stk.csv",row.names=FALSE)
x=rbind(x14,x13,x12,x11,x10,x09,x08,x07)
y1=x14[1,4]*x13[1,4]*x12[1,4]*x11[1,4]*x10[1,4]*x09[1,4]*x08[1,4]*x07[1,4]
y1=round(y1^(1/8),digits=4)
y2=x14[1,5]*x13[1,5]*x12[1,5]*x11[1,5]*x10[1,5]*x09[1,5]*x08[1,5]*x07[1,5]
y2=round(y2^(1/8),digits=4)
yq3=y1-y2
colnames(x)<-c("Year","StockNo","individual_ROI","y_ROI","i_yROI")
write.csv(x,"Q3stk.csv",row.names=FALSE)
xy=subset(x,select=c(Year,y_ROI,i_yROI))
xy=unique(xy)
xy=data.frame(xy)

#Show index/year ROI trend line
ggplot(xy, aes(Year)) + 
geom_point(aes(y=y_ROI),shape=17,size=5,colour="red") + 
geom_point(aes(y=i_yROI),shape=17,size=5,colour="blue") +
geom_line(aes(y = y_ROI, colour = "y_ROI")) + 
geom_line(aes(y = i_yROI, colour = "i_yROI")) +
geom_text(aes(y=y_ROI,label=y_ROI)) +
geom_text(aes(y=i_yROI,label=i_yROI))+
scale_color_manual(values = c("blue", "red"))
