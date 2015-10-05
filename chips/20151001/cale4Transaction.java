import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.*;

public class cale4Transaction {
	static int count=0;
	//將數字前面補滿0回傳
	public static String zfill(int num,int fill){
		String tmp="";
		tmp = "0000000000"+num;
		tmp = tmp.substring(tmp.length()-fill, tmp.length() );	
		return tmp;
	}
	public static void tmpSql(){
		/*
		StringBuilder tmpSql = new StringBuilder(
				" SELECT up.StockId, up.Cate, up.FindStartTimeId, startDay.TimeID, startDay.OpenPrice , "+
				" up.FindEndtimeId, endDay.TimeId, endDay.OpenPrice,   "+
				" ((startDay.OpenPrice - endDay.OpenPrice) / endDay.OpenPrice) as ROI "+
				" from threela.transaction  AS UP "+
				" JOIN ( "+
				" 	SELECT StockId, TimeId, OpenPrice FROM THREELA.trading  "+
				" 	WHERE StockId = 2227 and TimeId > 20140806 and TimeId < 20140906  "+
				" 	ORDER BY TimeId LIMIT 0,1 "+
				"  ) as startDay "+
				" ON up.StockId = startDay.StockId "+
				" JOIN( "+
				" 	SELECT StockId, TimeId, OpenPrice FROM THREELA.trading  "+
				" 	WHERE StockId = 2227 and TimeId > 20140919 and TimeId < 20141006  "+
				" 	ORDER BY TimeId LIMIT 0,1 "+
				" ) as endDay "+
				" ON up.StockId = endDay.StockId "+
				" WHERE UP.FindStartTimeID = 20140806 and up.StockId = 2227 and up.Cate = 21 "	
		);
*/		
	}
	
	public static void  updateTransaction(Statement smUpdate, String stockId, int cate, int findStartTimeId, int findEndtimeId , boolean isAfter17 ){
		String monthStr = null; 
		int month = 0;
		int endTime = 0;//// 取一個月內的值 by 發現要買進或作空的時間點 (第一個時間)
		int endtime2 =  0;// 取一個月內的值 by 發現要買賣出或回補的時間點 (第二個時間) 
		if (findStartTimeId != 0){
			monthStr = findStartTimeId+"";
			monthStr = monthStr.substring(4,6);
			month = Integer.parseInt(monthStr);
			endTime = (month == 12) ?   (findStartTimeId +8900) : (findStartTimeId +100 ) ;
			//System.out.println("in month:"+month);
			//System.out.println("startTime:"+findStartTimeId + "  endTime:"+endTime);
		}		
		
		if ( findEndtimeId != 0 ){
			monthStr = findEndtimeId + "";
			monthStr = monthStr.substring(4,6);
			month = Integer.parseInt(monthStr);			
			endtime2 = (month == 12) ?   (findEndtimeId +8900) : (findEndtimeId +200 ) ;
			System.out.println("endTime:"+findEndtimeId);		
		}

		count++;
		StringBuilder sql = new StringBuilder();

		String ROIStr = (cate== 11)?
			"((endDay.OpenPrice   - startDay.OpenPrice) / startDay.OpenPrice) *100 " :	
			"((startDay.OpenPrice - endDay.OpenPrice  ) / endDay.OpenPrice  ) *100 " ;
		if (findEndtimeId == 0) {
			sql = new StringBuilder(
				String.format(
					" UPDATE threela.transaction  AS UP "+
					" JOIN ( "+
					" 	SELECT StockId, TimeId, OpenPrice FROM THREELA.trading  "+
					" 	WHERE StockId = '%s' COLLATE utf8_unicode_ci and TimeId > %d and TimeId < %d  "+
					" 	ORDER BY TimeId LIMIT 0,1 "+
					" ) as startDay "+
					" ON up.StockId = startDay.StockId "+					
					" SET  "+
					" up.StartTimeId =  startDay.TimeID, "+
					" up.StartPrice = startDay.OpenPrice "+
					" WHERE UP.FindStartTimeID = %d and up.StockId = '%s' COLLATE utf8_unicode_ci  and up.Cate = %d ",
					stockId ,  findStartTimeId ,  endTime,
					findStartTimeId , stockId , cate )
			);
		}else if (findStartTimeId == 0){
			ROIStr = (cate== 11)?
					"((endDay.OpenPrice - up.StartPrice    ) / up.StartPrice    ) *100 " :	
					"((up.StartPrice    - endDay.OpenPrice ) / endDay.OpenPrice ) *100 " ;
			
			sql = new StringBuilder(
				String.format(
					" UPDATE threela.transaction  AS UP "+
					" JOIN( "+
					" 	SELECT StockId, TimeId, OpenPrice FROM THREELA.trading  "+
					" 	WHERE StockId = '%s' COLLATE utf8_unicode_ci  and TimeId > %d and TimeId < %d "+
					" 	ORDER BY TimeId LIMIT 0,1 "+
					" ) as endDay "+
					" ON up.StockId = endDay.StockId "+				
					" SET  "+
					" up.EndTimeId = endDay.TimeId, "+
					" up.EndPrice = endDay.OpenPrice, "+
					" up.ROI = %s "+
					" WHERE UP.FindEndTimeID = %d and up.StockId = '%s' COLLATE utf8_unicode_ci  and up.Cate = %d ",
					stockId ,  findEndtimeId ,  endtime2,
					ROIStr,
					findEndtimeId , stockId , cate 
				)
			);
			
		}else{
			sql = new StringBuilder(
				String.format(
					" UPDATE threela.transaction  AS UP "+
					" JOIN ( "+
					" 	SELECT StockId, TimeId, OpenPrice FROM THREELA.trading  "+
					" 	WHERE StockId = '%s' COLLATE utf8_unicode_ci  and TimeId > %d and TimeId < %d  "+
					" 	ORDER BY TimeId LIMIT 0,1 "+
					" ) as startDay "+
					" ON up.StockId = startDay.StockId "+
					" JOIN( "+
					" 	SELECT StockId, TimeId, OpenPrice FROM THREELA.trading  "+
					" 	WHERE StockId = '%s' COLLATE utf8_unicode_ci  and TimeId > %d and TimeId < %d "+
					" 	ORDER BY TimeId LIMIT 0,1 "+
					" ) as endDay "+
					" ON up.StockId = endDay.StockId "+	
					
					" SET  "+
					" up.StartTimeId =  startDay.TimeID, "+
					" up.StartPrice = startDay.OpenPrice, "+
					" up.EndTimeId = endDay.TimeId, "+
					" up.EndPrice = endDay.OpenPrice, "+
					" up.ROI = %s "+
					" WHERE UP.FindStartTimeID = %d and up.StockId = '%s' COLLATE utf8_unicode_ci and up.Cate = %d ",
					stockId ,  findStartTimeId ,  endTime,
					stockId ,  findEndtimeId   ,  endtime2,
					ROIStr,
					findStartTimeId , stockId , cate )
				);			
		}
		//System.out.println("count:"+count+"  sql:"+sql);

		if (count % 50 ==0){System.out.println("count:"+count+"  sql"+sql);}

		try{
			smUpdate.execute(sql.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			System.out.println("sql:"+sql);
		}

	}
	public static void main(String[] args) {
		// 當這程式有執行到2014年12月的資料時，需要補下的 sql 語法
		// 因為下面期間，沒有籌碼資料
		/*
		 delete from transaction
		 where FindStartTimeId <= 20141210 and FindEndTimeId >= 20150115  */
		String connectionUrl = null;
		Connection conn = null;
		Statement sm = null;
		Statement smUpdate = null;
		Calendar today = Calendar.getInstance();
		int year = today.get(Calendar.YEAR); //西元年
		int month = today.get(Calendar.MONTH) +1;//月
		int day = today.get(Calendar.DATE); //日
		int hour = today.get(Calendar.HOUR_OF_DAY);// 時
		//int minute = today.get(Calendar.MINUTE);	//分
		int date = Integer.parseInt(year +zfill(month,2) + zfill(day,2) );
		String stockId = "" ;			 // 股票代碼
		int cate = 0;			 // 種類 (買進或作空)
		int findStartTimeId = 0; // 放從 sql 取出 找到 發現要買進或作空的股票一開始的時間
		int findEndTimeId = 0;   // 放從 sql 取出 找到 發現要賣出或回補的股票
		boolean isAfter17 = false;		 // 看是否在17點之後，因為籌碼抓完，再作一些中間的處理，最快要17點之後，才會有資料
		//但是程式中，還沒有處理。執行這個程式時，若時間在17:00~23:59 要處理當天的， 00:00 ~ 16:59 只處理到前一天
		
		if (hour >=17) isAfter17 = true;
		
		System.out.println("date: "+ date );
		try{
			Class.forName("com.mysql.jdbc.Driver");
			String connUrl = "jdbc:mysql://127.0.0.1:3306/threela?useUnicode=true&characterEncoding=UTF8";
			conn = DriverManager.getConnection(connUrl, "threela", "123456");
			sm = conn.createStatement();
			smUpdate = conn.createStatement();
			StringBuilder sqlStr = new StringBuilder( 
				" SELECT StockId, Cate, FindStartTimeID, FindEndTimeId "+
				" FROM threela.transaction "+
				" WHERE StartTimeId is NULL and EndTimeId is NULL and FindStartTimeID > 0 and FindEndTimeId > 0");
			System.out.println("sql:"+sqlStr);
			ResultSet rs = sm.executeQuery(sqlStr.toString());	
			while (rs.next()){
				stockId = rs.getString("StockId").toString() ;
				cate = Integer.parseInt( rs.getString("Cate"));
				findStartTimeId = Integer.parseInt( rs.getString("FindStartTimeID"));
				findEndTimeId = Integer.parseInt( rs.getString("FindEndTimeId"));
				updateTransaction(smUpdate , stockId, cate, findStartTimeId, findEndTimeId , isAfter17 );
				//break;
			}
			
			sqlStr = new StringBuilder(
				" SELECT StockId, Cate, FindStartTimeID, FindEndTimeId "+
				" FROM threela.transaction "+
				" WHERE StartTimeId is NULL and FindStartTimeID > 0 and FindEndTimeId is null");
			System.out.println("sql:"+sqlStr);
			rs = sm.executeQuery(sqlStr.toString());	
			while (rs.next()){				
				stockId = rs.getString("StockId").toString();
				cate = Integer.parseInt( rs.getString("Cate"));
				findStartTimeId = Integer.parseInt( rs.getString("FindStartTimeID"));
				findEndTimeId = 0;
				updateTransaction(smUpdate , stockId, cate, findStartTimeId, findEndTimeId , isAfter17 );
				//break;
			}
			sqlStr = new StringBuilder(
					" SELECT StockId, Cate, FindStartTimeID, FindEndTimeId "+
					" FROM threela.transaction "+
					" WHERE FindEndTimeId > 0 and StartTimeID > 0 and EndTimeId is null");
				System.out.println("sql:"+sqlStr);
				rs = sm.executeQuery(sqlStr.toString());	
				while (rs.next()){				
					stockId = rs.getString("StockId").toString();
					cate = Integer.parseInt( rs.getString("Cate"));
					findStartTimeId = 0; // 控制不處理 開始買進，或是作空的資料
					findEndTimeId = Integer.parseInt( rs.getString("FindEndTimeId")) ;
					updateTransaction(smUpdate , stockId, cate, findStartTimeId, findEndTimeId , isAfter17 );
					//break;
				}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			if (sm != null || smUpdate != null){
				try{
					sm.close();
					smUpdate.close();
					conn.close();
				} catch (Exception e) {
					e.printStackTrace();
				}				
			}
		}	// end try	
		System.out.println("end !!");
	}   // end main

}
