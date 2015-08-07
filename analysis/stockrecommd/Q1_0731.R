#Load the following five files in the beginning
REV1=read.csv("RevQGrate.csv",header=TRUE,sep=",")
EPS1=read.csv("EPSGrate.csv",header=TRUE,sep=",")
#QxDate=read.csv("QxDate0",header=FALSE,sep=",")
QxDate=read.csv("QxDate1",header=FALSE,sep=",")
#QxDate=read.csv("QxDate2",header=FALSE,sep=",")
p1=read.csv("ClosePrice1.csv",header=TRUE,sep=",")
p2=read.csv("ClosePrice2.csv",header=TRUE,sep=",")

FilterREV_Q1 = function(yy) {
	REV=REV1
	REV=REV[REV$year>(yy-2)&REV$year<(yy+1),]
	REV_1=subset(REV,REV$year==(yy-1))
	REV_2=subset(REV,REV$year==yy)
	REV_1=REV_1[REV_1$Q1Grate>0&REV_1$Q2Grate>0&REV_1$Q3Grate>0&REV_1$Q4Grate>0,]
	r1=REV_1$stockid
	REV_2=REV_2[REV_2$Q1Grate>0.3&REV_2$Q1Grate<1,]
	r2=REV_2$stockid
	r3=intersect(r1,r2)
	return(r3)
}

FilterEPS_Q1 = function(yy,rr) {
	EPS=EPS1
	EPS=EPS[EPS$year>(yy-1)&EPS$year<(yy+1)&EPS$season<2,]
#	EPS=EPS[EPS$eps1>0.5&EPS$eps2>0.5&EPS$eps_grate>0.3&EPS$ROE>(0.025*rr),]
	EPS=EPS[EPS$eps1>0.5&EPS$eps2>0.5&EPS$eps_grate>0.3&EPS$ROE>(0.075*rr),]
	e1=EPS$stockid
	return(e1)
}

#Parse Q1 some conditions and suggest some stocks
m=read.csv("month.csv",header=TRUE,sep=",")
EPS=EPS1
REV=REV1
REV_1=REV[REV$year>2013&REV$year<2015,]
REV_1=REV_1[REV_1$Q1Grate>0&REV_1$Q2Grate>0&REV_1$Q3Grate>0&REV_1$Q4Grate>0,]
r1=REV_1$stockid
REV_2=m[m$Year>2014&m$Month<4&m$Month>2&m$RevYearCumulativeGRate>30&m$RevYearCumulativeGRate<100,]
r2=REV_2$StockId
r3=intersect(r1,r2)
EPS=EPS[EPS$year>2014&EPS$year<2016&EPS$season<2,]
EPS=EPS[EPS$eps1>0.5&EPS$eps2>0.5&EPS$eps_grate>0.3&EPS$ROE>0.025,]
e1=EPS$stockid
p1stk=subset(p2,TimeId==QxDate$V2[(15-7)*6+(3-3)*2+1])$StockId
p2stk=subset(p2,TimeId==QxDate$V2[(15-7)*6+(3-3)*2+2])$StockId
pstk=intersect(p1stk,p2stk)
Res15_Q1=intersect(FilterEPS_Q1(2015,1),r3)
Res15_Q1=intersect(Res15_Q1,pstk)
price1=subset(p2,StockId==100&TimeId==QxDate$V2[(15-7)*6+(3-3)*2+1])$ClosePrice
price_1=as.numeric(as.character(price1))
price2=subset(p2,StockId==100&TimeId==QxDate$V2[(15-7)*6+(3-3)*2+2])$ClosePrice
price_2=as.numeric(as.character(price2))
i_yROI=round((price_2-price_1)/price_1+1,digits=4)
#2015Q1-ROI
ROI=0
for (i in 1:length(Res15_Q1))
{
  price1=subset(p2,StockId==Res15_Q1[i]&TimeId==QxDate$V2[(15-7)*6+(3-3)*2+1])$ClosePrice
  price_1=as.numeric(as.character(price1))
  price2=subset(p2,StockId==Res15_Q1[i]&TimeId==QxDate$V2[(15-7)*6+(3-3)*2+2])$ClosePrice
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
y_ROI=round(y_ROI^(1/length(Res15_Q1)),digits=4)
ROI=round(ROI,digits=2)
x15=rbind(Res15_Q1,ROI)
x15=t(x15)
x15=cbind(2015,x15,y_ROI,i_yROI)
#write.csv(x15,"2015Q1stk.csv",row.names=FALSE)

#Parse Q1 some conditions and suggest some stocks
p1stk=subset(p2,TimeId==QxDate$V2[(14-7)*6+(3-3)*2+1])$StockId
p2stk=subset(p2,TimeId==QxDate$V2[(14-7)*6+(3-3)*2+2])$StockId
pstk=intersect(p1stk,p2stk)
Res14_Q1=intersect(FilterREV_Q1(2014),FilterEPS_Q1(2014,1))
Res14_Q1=intersect(Res14_Q1,pstk)
price1=subset(p2,StockId==100&TimeId==QxDate$V2[(14-7)*6+(3-3)*2+1])$ClosePrice
price_1=as.numeric(as.character(price1))
price2=subset(p2,StockId==100&TimeId==QxDate$V2[(14-7)*6+(3-3)*2+2])$ClosePrice
price_2=as.numeric(as.character(price2))
i_yROI=round((price_2-price_1)/price_1+1,digits=4)
#2014Q1-ROI
ROI=0
for (i in 1:length(Res14_Q1))
{
  price1=subset(p2,StockId==Res14_Q1[i]&TimeId==QxDate$V2[(14-7)*6+(3-3)*2+1])$ClosePrice
  price_1=as.numeric(as.character(price1))
  price2=subset(p2,StockId==Res14_Q1[i]&TimeId==QxDate$V2[(14-7)*6+(3-3)*2+2])$ClosePrice
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
y_ROI=round(y_ROI^(1/length(Res14_Q1)),digits=4)
ROI=round(ROI,digits=2)
x14=rbind(Res14_Q1,ROI)
x14=t(x14)
x14=cbind(2014,x14,y_ROI,i_yROI)
#write.csv(x14,"2014Q1stk.csv",row.names=FALSE)

#Parse Q1 some conditions and suggest some stocks
p1stk=subset(p2,TimeId==QxDate$V2[(13-7)*6+(3-3)*2+1])$StockId
p2stk=subset(p2,TimeId==QxDate$V2[(13-7)*6+(3-3)*2+2])$StockId
pstk=intersect(p1stk,p2stk)
Res13_Q1=intersect(FilterREV_Q1(2013),FilterEPS_Q1(2013,1))
Res13_Q1=intersect(Res13_Q1,pstk)
price1=subset(p2,StockId==100&TimeId==QxDate$V2[(13-7)*6+(3-3)*2+1])$ClosePrice
price_1=as.numeric(as.character(price1))
price2=subset(p2,StockId==100&TimeId==QxDate$V2[(13-7)*6+(3-3)*2+2])$ClosePrice
price_2=as.numeric(as.character(price2))
i_yROI=round((price_2-price_1)/price_1+1,digits=4)
#2013Q1-ROI
ROI=0
for (i in 1:length(Res13_Q1))
{
  price1=subset(p2,StockId==Res13_Q1[i]&TimeId==QxDate$V2[(13-7)*6+(3-3)*2+1])$ClosePrice
  price_1=as.numeric(as.character(price1))
  price2=subset(p2,StockId==Res13_Q1[i]&TimeId==QxDate$V2[(13-7)*6+(3-3)*2+2])$ClosePrice
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
y_ROI=round(y_ROI^(1/length(Res13_Q1)),digits=4)
ROI=round(ROI,digits=2)
x13=rbind(Res13_Q1,ROI)
x13=t(x13)
x13=cbind(2013,x13,y_ROI,i_yROI)
#write.csv(x13,"2013Q1stk.csv",row.names=FALSE)

#Parse Q1 some conditions and suggest some stocks
p1stk=subset(p2,TimeId==QxDate$V2[(12-7)*6+(3-3)*2+1])$StockId
p2stk=subset(p2,TimeId==QxDate$V2[(12-7)*6+(3-3)*2+2])$StockId
pstk=intersect(p1stk,p2stk)
Res12_Q1=intersect(FilterREV_Q1(2012),FilterEPS_Q1(2012,1))
Res12_Q1=intersect(Res12_Q1,pstk)
price1=subset(p2,StockId==100&TimeId==QxDate$V2[(12-7)*6+(3-3)*2+1])$ClosePrice
price_1=as.numeric(as.character(price1))
price2=subset(p2,StockId==100&TimeId==QxDate$V2[(12-7)*6+(3-3)*2+2])$ClosePrice
price_2=as.numeric(as.character(price2))
i_yROI=round((price_2-price_1)/price_1+1,digits=4)
#2012Q2-ROI
ROI=0
for (i in 1:length(Res12_Q1))
{
  price1=subset(p2,StockId==Res12_Q1[i]&TimeId==QxDate$V2[(12-7)*6+(3-3)*2+1])$ClosePrice
  price_1=as.numeric(as.character(price1))
  price2=subset(p2,StockId==Res12_Q1[i]&TimeId==QxDate$V2[(12-7)*6+(3-3)*2+2])$ClosePrice
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
y_ROI=round(y_ROI^(1/length(Res12_Q1)),digits=4)
ROI=round(ROI,digits=2)
x12=rbind(Res12_Q1,ROI)
x12=t(x12)
x12=cbind(2012,x12,y_ROI,i_yROI)
#write.csv(x12,"2012Q1stk.csv",row.names=FALSE)

#Parse Q1 some conditions and suggest some stocks
p1stk=subset(p2,TimeId==QxDate$V2[(11-7)*6+(3-3)*2+1])$StockId
p2stk=subset(p2,TimeId==QxDate$V2[(11-7)*6+(3-3)*2+2])$StockId
pstk=intersect(p1stk,p2stk)
Res11_Q1=intersect(FilterREV_Q1(2011),FilterEPS_Q1(2011,1))
Res11_Q1=intersect(Res11_Q1,pstk)
price1=subset(p2,StockId==100&TimeId==QxDate$V2[(11-7)*6+(3-3)*2+1])$ClosePrice
price_1=as.numeric(as.character(price1))
price2=subset(p2,StockId==100&TimeId==QxDate$V2[(11-7)*6+(3-3)*2+2])$ClosePrice
price_2=as.numeric(as.character(price2))
i_yROI=round((price_2-price_1)/price_1+1,digits=4)
#2011Q2-ROI
ROI=0
for (i in 1:length(Res11_Q1))
{
  price1=subset(p2,StockId==Res11_Q1[i]&TimeId==QxDate$V2[(11-7)*6+(3-3)*2+1])$ClosePrice
  price_1=as.numeric(as.character(price1))
  price2=subset(p2,StockId==Res11_Q1[i]&TimeId==QxDate$V2[(11-7)*6+(3-3)*2+2])$ClosePrice
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
y_ROI=round(y_ROI^(1/length(Res11_Q1)),digits=4)
ROI=round(ROI,digits=2)
x11=rbind(Res11_Q1,ROI)
x11=t(x11)
x11=cbind(2011,x11,y_ROI,i_yROI)
#write.csv(x11,"2011Q1stk.csv",row.names=FALSE)

#Parse Q1 some conditions and suggest some stocks
p1stk=subset(p1,TimeId==QxDate$V2[(10-7)*6+(3-3)*2+1])$StockId
p2stk=subset(p1,TimeId==QxDate$V2[(10-7)*6+(3-3)*2+2])$StockId
#p2stk=subset(p2,TimeId==QxDate$V2[(10-7)*6+(3-3)*2+2])$StockId
pstk=intersect(p1stk,p2stk)
Res10_Q1=intersect(FilterREV_Q1(2010),FilterEPS_Q1(2010,1))
Res10_Q1=intersect(Res10_Q1,pstk)
price1=subset(p1,StockId==100&TimeId==QxDate$V2[(10-7)*6+(3-3)*2+1])$ClosePrice
price_1=as.numeric(as.character(price1))
price2=subset(p1,StockId==100&TimeId==QxDate$V2[(10-7)*6+(3-3)*2+2])$ClosePrice
price_2=as.numeric(as.character(price2))
i_yROI=round((price_2-price_1)/price_1+1,digits=4)
#2010Q1-ROI
ROI=0
for (i in 1:length(Res10_Q1))
{
  price1=subset(p1,StockId==Res10_Q1[i]&TimeId==QxDate$V2[(10-7)*6+(3-3)*2+1])$ClosePrice
  price_1=as.numeric(as.character(price1))
  price2=subset(p1,StockId==Res10_Q1[i]&TimeId==QxDate$V2[(10-7)*6+(3-3)*2+2])$ClosePrice
#  price2=subset(p2,StockId==Res10_Q1[i]&TimeId==QxDate$V2[(10-7)*6+(3-3)*2+2])$ClosePrice
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
y_ROI=round(y_ROI^(1/length(Res10_Q1)),digits=4)
ROI=round(ROI,digits=2)
x10=rbind(Res10_Q1,ROI)
x10=t(x10)
x10=cbind(2010,x10,y_ROI,i_yROI)
#write.csv(x10,"2010Q1stk.csv",row.names=FALSE)

#Parse Q1 some conditions and suggest some stocks
p1stk=subset(p1,TimeId==QxDate$V2[(9-7)*6+(3-3)*2+1])$StockId
p2stk=subset(p1,TimeId==QxDate$V2[(9-7)*6+(3-3)*2+2])$StockId
pstk=intersect(p1stk,p2stk)
Res09_Q1=intersect(FilterREV_Q1(2009),FilterEPS_Q1(2009,1))
Res09_Q1=intersect(Res09_Q1,pstk)
price1=subset(p1,StockId==100&TimeId==QxDate$V2[(9-7)*6+(3-3)*2+1])$ClosePrice
price_1=as.numeric(as.character(price1))
price2=subset(p1,StockId==100&TimeId==QxDate$V2[(9-7)*6+(3-3)*2+2])$ClosePrice
price_2=as.numeric(as.character(price2))
i_yROI=round((price_2-price_1)/price_1+1,digits=4)
#2009Q1-ROI
ROI=0
for (i in 1:length(Res09_Q1))
{
  price1=subset(p1,StockId==Res09_Q1[i]&TimeId==QxDate$V2[(9-7)*6+(3-3)*2+1])$ClosePrice
  price_1=as.numeric(as.character(price1))
  price2=subset(p1,StockId==Res09_Q1[i]&TimeId==QxDate$V2[(9-7)*6+(3-3)*2+2])$ClosePrice
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
y_ROI=round(y_ROI^(1/length(Res09_Q1)),digits=4)
ROI=round(ROI,digits=2)
x09=rbind(Res09_Q1,ROI)
x09=t(x09)
x09=cbind(2009,x09,y_ROI,i_yROI)
#write.csv(x09,"2009Q1stk.csv",row.names=FALSE)

#Parse Q1 some conditions and suggest some stocks
p1stk=subset(p1,TimeId==QxDate$V2[(8-7)*6+(3-3)*2+1])$StockId
p2stk=subset(p1,TimeId==QxDate$V2[(8-7)*6+(3-3)*2+2])$StockId
pstk=intersect(p1stk,p2stk)
Res08_Q1=intersect(FilterREV_Q1(2008),FilterEPS_Q1(2008,1))
Res08_Q1=intersect(Res08_Q1,pstk)
price1=subset(p1,StockId==100&TimeId==QxDate$V2[(8-7)*6+(3-3)*2+1])$ClosePrice
price_1=as.numeric(as.character(price1))
price2=subset(p1,StockId==100&TimeId==QxDate$V2[(8-7)*6+(3-3)*2+2])$ClosePrice
price_2=as.numeric(as.character(price2))
i_yROI=round((price_2-price_1)/price_1+1,digits=4)
#2008Q1-ROI
ROI=0
for (i in 1:length(Res08_Q1))
{
  price1=subset(p1,StockId==Res08_Q1[i]&TimeId==QxDate$V2[(8-7)*6+(3-3)*2+1])$ClosePrice
  price_1=as.numeric(as.character(price1))
  price2=subset(p1,StockId==Res08_Q1[i]&TimeId==QxDate$V2[(8-7)*6+(3-3)*2+2])$ClosePrice
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
y_ROI=round(y_ROI^(1/length(Res08_Q1)),digits=4)
ROI=round(ROI,digits=2)
x08=rbind(Res08_Q1,ROI)
x08=t(x08)
x08=cbind(2008,x08,y_ROI,i_yROI)
#write.csv(x08,"2008Q1stk.csv",row.names=FALSE)

#Parse Q1 some conditions and suggest some stocks
p1stk=subset(p1,TimeId==QxDate$V2[(7-7)*6+(3-3)*2+1])$StockId
p2stk=subset(p1,TimeId==QxDate$V2[(7-7)*6+(3-3)*2+2])$StockId
pstk=intersect(p1stk,p2stk)
Res07_Q1=intersect(FilterREV_Q1(2007),FilterEPS_Q1(2007,1))
Res07_Q1=intersect(Res07_Q1,pstk)
price1=subset(p1,StockId==100&TimeId==QxDate$V2[(7-7)*6+(3-3)*2+1])$ClosePrice
price_1=as.numeric(as.character(price1))
price2=subset(p1,StockId==100&TimeId==QxDate$V2[(7-7)*6+(3-3)*2+2])$ClosePrice
price_2=as.numeric(as.character(price2))
i_yROI=round((price_2-price_1)/price_1+1,digits=4)
#2007Q1-ROI
ROI=0
for (i in 1:length(Res07_Q1))
{
  price1=subset(p1,StockId==Res07_Q1[i]&TimeId==QxDate$V2[(7-7)*6+(3-3)*2+1])$ClosePrice
  price_1=as.numeric(as.character(price1))
  price2=subset(p1,StockId==Res07_Q1[i]&TimeId==QxDate$V2[(7-7)*6+(3-3)*2+2])$ClosePrice
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
y_ROI=round(y_ROI^(1/length(Res07_Q1)),digits=4)
ROI=round(ROI,digits=2)
x07=rbind(Res07_Q1,ROI)
x07=t(x07)
x07=cbind(2007,x07,y_ROI,i_yROI)
#write.csv(x,"2007Q1stk.csv",row.names=FALSE)
x=rbind(x15,x14,x13,x12,x11,x10,x09,x08)
y1=x15[1,4]*x14[1,4]*x13[1,4]*x12[1,4]*x11[1,4]*x10[1,4]*x09[1,4]*x08[1,4]
y1=round(y1^(1/8),digits=4)
y2=x15[1,5]*x14[1,5]*x13[1,5]*x12[1,5]*x11[1,5]*x10[1,5]*x09[1,5]*x08[1,5]
y2=round(y2^(1/8),digits=4)
yq1=y1-y2
colnames(x)<-c("Year","StockNo","individual_ROI","y_ROI","i_yROI")
write.csv(x,"Q1stk.csv",row.names=FALSE)
xy=subset(x,select=c(Year,y_ROI,i_yROI))
xy=unique(xy)
xy=data.frame(xy)

ggplot(xy, aes(Year)) + 
geom_point(aes(y=y_ROI),shape=17,size=5,colour="red") + 
geom_point(aes(y=i_yROI),shape=17,size=5,colour="blue") +
geom_line(aes(y = y_ROI, colour = "y_ROI")) + 
geom_line(aes(y = i_yROI, colour = "i_yROI")) +
geom_text(aes(y=y_ROI,label=y_ROI)) +
geom_text(aes(y=i_yROI,label=i_yROI))+
scale_color_manual(values = c("blue", "red"))

