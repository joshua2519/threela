import java.io.*;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.math.BigDecimal;
public class chipsFileToDB {    	
	static void updateToDaily(int timeId){
		/*
		StringBuilder sql = new StringBuilder(
				"INSERT INTO daily (StockId, BrokerId, TimeId, Buy, Sell , BuyPrice, SellPrice, Source) \n" +
				" SELECT  a.stockid, a.brokerid , " + timeId + " as TimeId, sum(a.buy) as buy , sum(a.buy*a.buyprice)/sum(a.buy) as  buyprice , aa.sell, aa.sellprice , 1 as source "+
				" FROM daily_tmp as a  "+
				" LEFT JOIN ( "+
				" 	 SELECT stockid , brokerid ,buy, buyprice, sum(sell)as sell , sum(sell * sellprice ) /sum(sell) as sellprice "+
				" 	 FROM daily_tmp   \n"+
				//" 	 WHERE  sell > 0 and length(StockId) = 4 and TimeId = " + timeId +
				" 	 WHERE  sell > 0 and TimeId = " + timeId +
				" 	 GROUP BY stockid , brokerid "+
				"  ) as aa "+
				" ON a.stockid = aa.stockid and a.brokerid = aa.brokerid \n"+
				//" WHERE  a.buy > 0 and length(a.StockId) = 4 and TimeId = " + timeId +
				" WHERE  a.buy > 0 and a.TimeId = " + timeId +
				" GROUP BY a.stockid , a.brokerid "+
				
				" UNION \n"+
				" SELECT b.stockid , b.brokerid , " + timeId + " as TimeId ,bb.buy, bb.buyprice, sum(b.Sell)as sell , sum(b.Sell*b.SellPrice ) /sum(b.Sell) as sellprice , 1 as source "+
				" FROM daily_tmp as b  "+
				" LEFT JOIN ( \n"+
				" 	SELECT stockid, brokerid , sum(buy) as buy , sum(buy*buyprice)/sum(buy) as  buyprice , sell, sellprice  "+
				" 	FROM daily_tmp   \n"+
				//"     WHERE  a.buy > 0 and length(StockId) = 4 and TimeId = " + timeId +
				"     WHERE  a.buy > 0  and TimeId = " + timeId +
				" 	GROUP BY a.stockid , a.brokerid "+
				" ) as bb "+
				" on aa.stockid = b.stockid and aa.brokerid = b.brokerid \n"+
				//" WHERE  b.Sell > 0 and length(b.StockId) = 4 AND TimeId = " + timeId +
				" WHERE  b.Sell > 0 AND TimeId = " + timeId +
				" GROUP BY b.stockid , b.brokerid "
		);
		*/
		StringBuilder sql = new StringBuilder(
			"INSERT INTO daily (StockId, BrokerId, TimeId, Buy, Sell , BuyPrice, SellPrice, Source) \n" +
			" SELECT  StockId , BrokerId , " + timeId + " as TimeId ,  "+  
			" sum(Buy) as Buy , sum(Sell) as  Sell , "+  
			" ROUND(sum(Buy  * BuyPrice)  / sum(Buy),2)  as BuyPrice ,  "+  
			" ROUND(sum(Sell * SellPrice) / sum(Sell),2) as SellPrice , 1 as source "+  
			" FROM daily_tmp  \n"+  
			" WHERE TimeId = " + timeId +
			" GROUP BY StockId, BrokerId "
		);
		
		System.out.println("insert sql:"+sql);
		
		try{
			Class.forName("com.mysql.jdbc.Driver");
			String connUrl = "jdbc:mysql://127.0.0.1:3306/threela?useUnicode=true&characterEncoding=UTF8";
			Connection conn = DriverManager.getConnection(connUrl, "threela", "123456");
			Statement sm = conn.createStatement();
			sm.executeUpdate("USE threela;");
			sm.executeUpdate(sql.toString());
			System.out.println("sql: TRUNCATE TABLE threela.daily_tmp");
			sm.executeUpdate("TRUNCATE TABLE threela.daily_tmp ");
			sm.close();
		}catch(SQLException e){
			System.out.println("insert daily sql:"+ sql.toString());
			e.printStackTrace();				
		}catch(Exception e){
			System.out.println("insert daily sql2:"+ sql.toString());
			e.printStackTrace();
		}finally{	
			try {			
				for (int i=0;i<2;i++){
					Thread.sleep(100);
					java.awt.Toolkit.getDefaultToolkit().beep(); 
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("End !!");
	}
	
	// 取得上市資料
	static StringBuilder getBSValue(String stockId, int timeId,String str){
		//System.out.println("str:"+str);
		String []tokens = str.split(","); // 放 每一行用 逗號分解後所取得的資料
		StringBuilder sql = new  StringBuilder(""); // 要回傳的 sql 資料
		//String sqlInsert = "INSERT INTO daily_tmp(id,StockId, BrokerId,TimeId, Buy, Sell, BuyPrice, SellPrice , Source) VALUES ";
		
		int id = Integer.parseInt(tokens[0].trim() );   // 序號
		String brokerId = tokens[1].trim().substring(0,4);//券商代碼
		double price = Double.parseDouble(tokens[2].trim().replace(",", "") );   // 價格
		price = new BigDecimal(price)
        			.setScale(2, BigDecimal.ROUND_HALF_UP)
        			.doubleValue();

		long buy = Long.parseLong( Math.round( Double.parseDouble(tokens[3].trim()  ) /1000 ) + ""); //買進數量
		long sell = Long.parseLong( Math.round( Double.parseDouble(tokens[4].trim()  ) /1000 ) + "");//賣出數量

		int id2 = 0;
		String brokerId2 = null;
		double price2 = 0;
		long buy2 = 0;
		long sell2 = 0;

		if (buy > 0 && sell > 0){
			sql.append(	 String.format("(%d,'%s','%s',%d, %d , %d , %f , %f , 1) ", 
						 id,stockId, brokerId, timeId, buy, sell , price , price)	);
		}else if (buy > 0){
			//id, stockId, brokerId, timeId, buy, sell , buyPrice, sellPrice
			sql.append( String.format("(%d,'%s','%s',%d, %d , %s , %f , %d , 1) ", 
						id,stockId, brokerId, timeId, buy, "NULL", price , 0) );
		}else if (sell > 0){
			sql.append( String.format("(%d,'%s','%s', %d , %s , %d , %d , %f , 1)", 
						id,stockId, brokerId, timeId, "NULL", sell , 0 , price) );
		}

		if (tokens[6] != null && tokens[6].length() > 0 ){
			id2 = Integer.parseInt(tokens[6].trim() );   // 序號
			brokerId2 = tokens[7].trim().substring(0, 4);//券商代碼
			price2 = Double.parseDouble( tokens[8].trim() );   // 價格
			buy2 = Long.parseLong( Math.round( Double.parseDouble(tokens[9].trim()  ) /1000 ) +"" ); //買進數量
			sell2 = Long.parseLong( Math.round( Double.parseDouble(tokens[10].trim()  ) /1000 ) + "");//賣出數量
			//System.out.println("buy2:"+buy2 + " sell2:"+sell2);
			price2 = new BigDecimal(price2)
						 .setScale(2, BigDecimal.ROUND_HALF_UP)
						 .doubleValue();
			
			if (sql.length() > 10 && (buy2 > 0 || sell2 > 0)  ){
				sql.append(" , ");
			}
			
			if (buy2 > 0 && sell2 > 0){
				sql.append( 
					  String.format("(%d,'%s','%s',%d, %d , %d , %f , %f , 1); ", 
					  id2,stockId, brokerId2, timeId, buy2, sell2 , price2 , price2) );
			}else if (buy2 > 0){
				//id, stockId, brokerId, timeId, buy, sell , buyPrice, sellPrice
				sql.append(
					  String.format("(%d,'%s','%s',%d, %d , %s , %f , %d , 1); ", 
					  id2,stockId, brokerId2, timeId, buy2, "NULL", price2 , 0) );
			}else if (sell2 > 0){
				sql.append(
					  String.format("(%d,'%s','%s', %d , %s , %d , %d , %f , 1);", 
					  id2,stockId, brokerId2, timeId, "NULL", sell2 , 0 , price2) );
			}
		}
		//System.out.println(id + " "+ brokerId + " "+ price + " " +  buy + " " +sell + "::" +  id2 + " "+ brokerId2 + " "+ price2 + " " +  buy2 +" "+ sell2 );
		//System.out.println("sql:"+sql);
		return sql;
	}
	
	// 取得上櫃每一筆的資料
	static StringBuilder getOTCRow(String stockId, int timeId, String  tokens){
		StringBuilder sql= new StringBuilder("");	// 放 sql 語法
		String buySell = "";  //放買或賣的數量
		String []rows = tokens.split("\",\"");          // 用逗號分隔完的資料
		int buy = 0; // 買的數量
		int sell = 0; // 賣的數量
		/*
		for (int i=0;i< rows.length;i++){
			System.out.println("rows["+i+"]"+rows[i]);
		}
		*/

		if (rows[4] != null && rows[4].length() > 1) rows[4] = rows[4].trim().replace("\"", "");
		if (rows[3].trim().length() > 1){
			buy = Integer.parseInt( Math.round (Float.parseFloat(  rows[3].trim().replace("," , "")   ) /1000 )+"" ) ; // 取出買進張數，作四捨五入
		}else{ buy = 0;
		}
		if (rows[4].length() > 1){
			sell = Integer.parseInt(  Math.round ( Float.parseFloat( rows[4].replace(",", "")  ) / 1000 ) + ""); // 取出賣出張數，作四捨五入
		}else{ sell = 0;
		}
		
		// INSERT INTO daily_tmp(id,StockId, BrokerId,TimeId, Buy, Sell, BuyPrice, SellPrice , Source) VALUES 
		if ( buy > 0 && sell > 0 ){
			sql.append( String.format("( %s,'%s','%s',%d, %d , %s , %s , %s , 1)", 
			    rows[0].trim().replace("\"", ""),stockId, rows[1].subSequence(0, 4), timeId, buy, sell, rows[2].trim().replace(",", "") , rows[2].trim().replace(",", "") ) 
			);
		}else if (buy > 0){
			sql.append( String.format("( %s,'%s','%s',%d, %s , %s , %s , %d , 1)", 
				rows[0].trim().replace("\"", ""),stockId, rows[1].subSequence(0, 4), timeId, buy , "NULL", rows[2].trim().replace(",", "") , 0) 
			);
		}else if (sell > 0){
			sql.append( String.format("(%s,'%s','%s',%d, %s , %s , %d , %s , 1)", 
				rows[0].trim().replace("\"", ""),stockId, rows[1].subSequence(0, 4), timeId, "NULL", sell , 0 , rows[2].trim().replace(",", "") ) 
			);
		}
		
/*		
		if (rows[rows.length-1].length() == 1 && rows[rows.length-1].equals("0")){
			//資料為買進
			for (int i=3;i<rows.length-2;i++){
				buySell = buySell + rows[i].trim();
			}
			if ( Integer.parseInt( rows[rows.length-2].trim() ) > 499 ){
				if (buySell.length() == 0) buySell = "0";
				buySell =  (Integer.parseInt( buySell ) + 1)+"";
			}
			if ( buySell.length() == 0) buySell = "0";
			if (Integer.parseInt(buySell) > 0 ){
				sql.append( String.format("( %s,'%s','%s',%d, %s , %s , %s , %d , 1)", 
				    rows[0].trim(),stockId, rows[1].subSequence(0, 4), timeId, buySell, "NULL", rows[2].trim() , 0) );
			}
		}else{
			//資料為賣出
			for (int i=4;i<rows.length-1;i++){
				buySell = buySell + (rows[i]);
			}
			//System.out.println("rows[rows.length-1]:"+rows[rows.length-1]);
			if (Integer.parseInt(rows[rows.length-1].trim() ) > 499 ){
				if (buySell.length() == 0) buySell = "0";
				buySell =  (Integer.parseInt( buySell ) + 1)+"";
			}
			if (buySell.length() == 0 ) buySell = "0";
			
			if (Integer.parseInt(buySell) > 0 ){
				sql.append( String.format("(%s,'%s','%s',%d, %s , %s , %d , %s , 1)", 
				   rows[0].trim(),stockId, rows[1].subSequence(0, 4), timeId, "NULL", buySell , 0 , rows[2].trim() ) );}			
			}
*/
		return sql;
	}
	// 取得上櫃資料
	static StringBuilder getOTCValue(String stockId, int timeId, String str){
		//System.out.println("str:"+str);
		StringBuilder sql = new StringBuilder(""); // 放 sql 語法
		StringBuilder sqlTmp = new StringBuilder(""); // 暫時放 sql 語法
		String []tokens = str.split(",,"); // 用 ",," 將資料分成二筆資料

		sqlTmp.append(getOTCRow(stockId, timeId, tokens[0] ) );
		if (sqlTmp.length() > 10)
			sql.append(sqlTmp );
		sqlTmp.delete(0, sqlTmp.length());
		
		if (tokens.length > 1 ){
			sqlTmp.append(getOTCRow(stockId, timeId, tokens[1] ) );
			if (sql.length() > 10  && sqlTmp.length() > 10 ) sql.append(" , ");
			if (sqlTmp.length() > 10) sql.append(sqlTmp);
			
		}		
		return sql;
	}
	static void readFile(int timeId,String files){
		String str;		// 放每一行資料
		String stockId = null; // 股票代碼
		File file = new File(files); // 取得的檔案名稱
		System.out.println("file:"+ file);
		StringBuilder sql = new StringBuilder(""); // 放 sql 語法
		StringBuilder tmpSql = new StringBuilder(""); // sql 回傳回來的value 值
		int index = 0; // 找到的位置
		int index2 = 0; // 第2個找到的位置
		int count = 0; // 記錄每一檔案現在處理到那一行
		int isOTC = 0; //是否為 上櫃公司    0->上市  1->上櫃

		try{
			FileReader ir = new FileReader(file);
			BufferedReader in = new BufferedReader(ir);
			StringBuilder sb = new StringBuilder(300);
			Class.forName("com.mysql.jdbc.Driver");
			String connUrl = "jdbc:mysql://127.0.0.1:3306/threela?useUnicode=true&characterEncoding=UTF8";
			Connection conn = DriverManager.getConnection(connUrl, "threela", "123456");
			Statement sm = conn.createStatement();
			sm.executeUpdate("USE threela;");
			 
			while((str = in.readLine()) != null){
				str = new String(str.getBytes("big5"),"utf-8");
				
				//System.out.println("sql insert:"+sql);
				//str = new String(str.getBytes("utf-8"));
				if (count == 1){
					index = str.indexOf("\"");
					index2 = str.indexOf("\"", index+1);
					if (index > 0) { 
						isOTC = 0; //上市
						stockId = str.substring(index+1,index2 );
						if (stockId.length() > 4 && 
							stockId.substring(0,1) == "0" && 
							Integer.parseInt(  stockId.subSequence(1,2)+"" )  >= 1  ) 
							return;  // 若不是股票，或是ETF ，而且 權證，就不放入資料庫 // ??? 2015/9/15 之後可以拿掉
					}else{
						isOTC = 1; // 上櫃
						if (str.indexOf(",") > 0){
							String [] tmp = str.split(",");
							stockId = tmp[1].trim();
						}else{
							System.out.println("error stockId:"+ stockId);
							try {			
								for (int i=0;i<2;i++){
									Thread.sleep(100);
									java.awt.Toolkit.getDefaultToolkit().beep(); 
								}
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
					//System.out.println("isOTC:"+isOTC+ "  stockId:"+stockId);
				}else if (count >= 3){
					sql = new StringBuilder("INSERT INTO daily_tmp(id,StockId, BrokerId,TimeId, Buy, Sell, BuyPrice, SellPrice , Source) VALUES ");
					if (isOTC == 0){
						//sql = getBSValue(stockId,timeId, str);
						tmpSql.append(  getBSValue(stockId,timeId, str) );
						if (tmpSql.length() > 10){ 
							sql.append( tmpSql );  // 表示有回傳資料 ，就加到 sql 裡
							tmpSql.delete(0, tmpSql.length() );
						}else {					   continue; 
						}
					}else{
						//str = str.replace("\"", "");
						tmpSql.append( getOTCValue(stockId,timeId, str)  );
						
						if (tmpSql.length() > 10) { 
							sql.append(tmpSql);
							tmpSql.delete(0, tmpSql.length() );
						}else{						continue;							
						}
					}
					if (count == 3) 
						System.out.println("sql:"+sql);

					try{
						sm.executeUpdate(sql.toString());
						sql.delete(0, sql.length());
					}catch(SQLException e){
						System.out.println(stockId + " error sql:"+  sql.toString());
						e.printStackTrace();			
					}catch(Exception e){
						System.out.println("error Exception sql:"+ sql.toString());
						e.printStackTrace();
					}
				}
				count++;
			}
			System.out.print(sb);
			in.close();
			ir.close();
			sm.close();
			conn.close();
		}catch(IOException e ){
			System.out.println("IO Exception");
			e.printStackTrace();
		}catch(ClassNotFoundException e){
			e.printStackTrace();
		}catch (Exception e){
			System.out.println("Other exception !!");
			e.printStackTrace();
		}finally{
			
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// file to daily_tmp and daily
		FilenameFilter textFilter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				String lowercaseName = name.toLowerCase();
				if (lowercaseName.endsWith(".csv")) {
					return true;
				} else {
					return false;
				}
			}
		};
		// 檔案資料會轉給 資料表  daily_tmp 和 daily
		int timeId = 20151005;  // 在此修改日期 // 
		File dir = new File("C:\\daily\\raw\\"+timeId);
		if (!dir.isDirectory()){
			System.err.println("directory not find!!");
			System.exit(0);
		}
		StringBuilder sb = new StringBuilder(300);
		File [] contents = dir.listFiles(textFilter );

		for (int i=0;i<contents.length;i++){
			readFile(timeId, contents[i].toString());

			//sb.append(contents[i]);
			//sb.append("\n");
			//break;
		}

		//System.out.println(sb);
		updateToDaily(timeId);
		System.out.println("End !!");
	}

}
