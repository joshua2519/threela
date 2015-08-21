import java.io.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
public class GetDay {
	static boolean insFast = true; // 是否是在下午4點之後，才跑程式
	static int index1 = 1; //找資料開始的位置。第1個 "的位置 
	static int index2 = 0; //找資料結束的位置。第2個 "的位置
	//static String startStr = "[&quot;";
	static String startStr = "[\"";
	static String quot = "\"";
	static HashMap hm = new HashMap();     //放股票代碼的開高低收 價格資料，好讓 價格和 三大法人可以一起處理
	
    //Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

	public static boolean isNumeric(String str)	{
		Pattern pattern = Pattern.compile("-?[0-9.,]*");
		Matcher isNum = pattern.matcher(str);
		if( !isNum.matches() ) 	{
			return false;
		}
		return true;
	}
	//跳過一個資料 
	//回傳  這筆資料結束位置 skip(String 資料, int 上一筆資料結束位置)
	public static int skip(String data,int index){
		//int index2 = 0;
		index1 = data.indexOf(quot , index+1);
		index2 = data.indexOf(quot , index1+1);
		return index2; // 回傳這筆資料結束位置
	}
	//取得id資料
	//回傳 字串 getID(String 資料 , int 上一筆資料結束位置)
	public static String getID(String data, int index){
		String id = null;

		index1 = data.indexOf(startStr, index);
		if (index1 < 0)
			return "";

		index2 = data.indexOf(quot, index1+4);		
		id = data.substring(index1 + startStr.length() , index2);
		//System.out.print(" ind1:"+index1 + " ind2:"+index2 + " id:"+id);

		return id;
	}
	
	//取得下一筆數量
	//float nextVolume(String 要找的資料 , int 上一筆資料結束位置)
	public static long nextVolume(String data, int index){
		//String quot = "&quot;";
		//String quot = "\"";
		Long tmpLong = 0L;
		index1 = data.indexOf(quot, index+quot.length());
		index2 = data.indexOf(quot, index1+quot.length());
		String tmp = data.substring(index1+quot.length(), index2).replaceAll(",", "");
		//System.out.println("len:"+tmp.length()+" tmp:"+tmp);
		if (tmp.length() > 1)	tmp = tmp.substring(1);
		
		if (isNumeric(tmp)){
			tmpLong = Long.parseLong(tmp);
			//System.out.println("tmpLong:"+ tmpLong);
			if (tmpLong > 0){
				tmpLong = (tmpLong + 500) / 1000;
			}else{
				tmpLong = (tmpLong - 500) / 1000;
			}
			return tmpLong ;
		}
		return 0;
	}
	
	//取得下一筆價格
	//float nextPrice(String 要找的資料 , int 上一筆資料結束位置)
	public static float nextPrice(String data, int index){
		//String quot = "&quot;";
		index1 = data.indexOf(quot, index+quot.length());
		index2 = data.indexOf(quot, index1+quot.length());
		String tmp = data.substring(index1+quot.length(), index2);
		//float price = Float.parseFloat(tmp);
		if (isNumeric(tmp)){
			return Float.parseFloat(tmp);
		}
		return 0;
	}
	
	//取得下一筆價格
	//float nextPrice(String 要找的資料 , int 上一筆資料結束位置)
	public static int nextPrice2(String data, int index){
		String startStr = "<td align='right'>"; //要找的開始位置資料
		String endStr = "</td>";  //要找的結束位置資料
		int num1 = 0;    //買進筆數
		int num2 = 0;    //賣出筆數
		
		index1 = data.indexOf(startStr, index);
		index2 = data.indexOf(endStr, index1+startStr.length());
		num1 = Integer.parseInt(data.substring(index1 + startStr.length(), index2 ).replaceAll(",", "") );
		index1 = data.indexOf(startStr, index2);
		index2 = data.indexOf(endStr, index1+startStr.length()); 
		num2 = Integer.parseInt(data.substring(index1 + startStr.length(), index2 ).replaceAll(",", "") );
		if (num1 > num2){ 	num1 = num1 + 500;
		}else{ 				num2 = num2 + 500;
		}
		return (num1 - num2) / 1000;		
	}
	
	public static int nextTotal3(String data, int index){
		String startStr = "<td align='right'>";  //取得數字參考字元
		String endStr = "</td>";			 //結束字元參考字元
		int num1 = 0;						 //取得的數字
		
		index1 = data.indexOf(startStr, index);
		index2 = data.indexOf(endStr, index1+startStr.length());
		num1 = Integer.parseInt(data.substring(index1 + startStr.length(), index2 ).replaceAll(",", "") );
		//System.out.println("index1: "+index1 + "  index2:"+index2);
		if (num1 > 0) num1 = num1 + 500;
		else		  num1 = num1 - 500;
		return num1 / 1000;
	}

	public static int nextNum(String data, int index){
		//String quot = "&quot;";
		index1 = data.indexOf(quot, index+quot.length());
		index2 = data.indexOf(quot, index1+quot.length());
		String tmp = data.substring(index1+quot.length(), index2);
		tmp = tmp.replaceAll(",", "");
		//float price = Float.parseFloat(tmp);
		if (isNumeric(tmp)) {
			return Integer.parseInt(tmp)/1000;
		}
		return 0;
	}
	
	//了解傳入的股票代碼，是否為電子業的代碼，若是的話，回傳1	
	public static int isElect(String id){
		int elect[] = {23,24,49,52,53,54,61,62,80,81,82}; // 電子業代碼
		int elect1 = 30; //設定電子業代碼 開始的位置
		int elect2 = 37; //設定電子業代碼 結束的位置
		int classify = 0;
		int flag = 0;
       	classify = Integer.parseInt( id.substring(0, 2) );
    	if (classify >= elect1  && classify <= elect2  ){
    		flag = 1;
    	}else{
    		for (int i =0 ;i< elect.length;i++){
    			if (classify == elect[i]){
    				flag = 1;
    				break;
    			}
    		}
    	}
    	return flag;
	}
	//上市資料成交價格
	public static void procTWSE(Statement sm , URL url, String date  ) throws IOException {		
		int count = 0;		// 計算取得幾筆資料
		int num = 0;		// 成交量

		int classify = 0;   //放股票代碼的前2個數字，以分辦是否為電子業
		int flag = 0;    //辦別是否為電子業的 1為電子業
		
		float open = 0.0f; // 開盤價
	    float high = 0.0f; // 最高價
		float low = 0.0f;  // 最低價
	    float close = 0.0f;// 收盤價
		float PERate = 0.0f;// 本益比
	    int Volume = 0;	   // 成交量 
		String str = "";   // 放從網路取回的資料
		String sql = ""; // sql  語法
		String tmpStr = ""; //String 型態暫存檔
        String id = null; // 股票代碼
        String strYearMon = ""; // 西元年、月  
		
        Document doc=null; // 放網頁取回來的資料_ Document 格式
        doc = Jsoup.parse(url, 3000);
        Element table0 = doc.select("center").get(0);
        Iterator <Element> item = table0.select("td").iterator();

        System.out.println("-- Start -------------");

        while(item.hasNext() )	{
        	count++;
            str = item.next().text().trim().replaceAll(",", "");
            if (count == 1  && !isNumeric(str)  ){
            	count = 0;
            	continue;
            }
            //System.out.println(count+" str:"+str );
    		//if (str.length() > 0) System.out.println(count+" str:"+str  +":" + (int) (str).charAt(0) );
            
            if (str.length() >= 1 && (int)str.charAt(0) == 45) continue;   

            switch(count % 16){
                case 1:
                	id = str;  
                	flag = isElect(id);                	                	
                	break;
                case 3:
                	num = Integer.parseInt(str) / 1000;
                	break;
                case 6:
                	open = Float.parseFloat(str);
                	break;
                case 7:
                	high = Float.parseFloat(str);
                	break;
                case 8:
                	low = Float.parseFloat(str);
                	break;
                case 9:
                	close = Float.parseFloat(str);
                	break;
                case 0:
                	PERate = Float.parseFloat(str);
                	if (flag == 1){
                		sql = "INSERT INTO priceElect ";
                	}else{
                		sql = "INSERT INTO priceBase ";
                	}
                	if (num == 0) continue;
                	if (insFast == true){
                		tmpStr = (String) hm.get(id);
                		if (tmpStr == null){tmpStr = "0,0,0,0";}
                		sql = sql + " (id, [date], [open], high, low, [close], Volume , PERate , ForeignTrading , TrustTrading , DealersTrading , Total3) VALUES ('" + 
                            	id + "' , " + Integer.parseInt(date ) +" , " + open + ", " + high + ", " + low + " , " + close + ", " + num + ", " + PERate + "," + tmpStr + " )";
                	}else {
                		sql = sql + " (id, [date], [open], high, low, [close], Volume , PERate ) VALUES ('" + 
                            	id + "' , " + Integer.parseInt(date ) +" , " + open + ", " + high + ", " + low + " , " + close + ", " + num + ", " + PERate +  " )";
                	}

                	if (id.length() < 5) {
                    	if (count / 16 ==  1)	System.out.println("sql: " + count + sql);
                    	if (id.equals("1723"))	System.out.println("sql: " + count + sql);

                    	try {	                			
                			sm.executeUpdate(sql);	                        
	                    } catch ( SQLException e ) {
	                    	e.printStackTrace();
	                    } catch(Exception e){
	                    	e.printStackTrace();
	                    }

                	}

                	open = 0; high = 0; low = 0; close = 0; //將 開、高、低、收 的價格清除
                	break;
            }
       
            //System.out.println("td str:"+count+"  "+str);
        } //end while(item.hasNext() )	{
	}
	
	public static void read1( String strURL ) {
        int chunksize = 4096000;
      byte[] chunk = new byte[chunksize];
      int count;
      try  {    
          URL pageUrl = new URL(strURL );
     
          // 讀入網頁(位元串流)
          BufferedInputStream bis = new BufferedInputStream(pageUrl.openStream());
          BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream("c:\\URL1.txt", false));
          
          System.out.println("read1() running " );
          while ((count = bis.read(chunk, 0, chunksize)) != -1) {
              bos.write(chunk, 0, count); // 寫入檔案
          }
          bos.close();
          bis.close();

        System.out.println("Done");   
       }catch (IOException e) {
           e.printStackTrace();
       }
    }
	// 上市三大法人資料寫入
	public static void procTotal3(Statement sm, String data , String date){
		String startStr1 = "<td align='center'>"; //取得股票代碼參考字元
		String startStr2 = "<td align='right'>";  //取得數字參考字元
		String endStr = "</td>";			 //結束字元參考字元
		String id = ""; // 取得股票代 碼
		String sql = ""; //放 sql 語法
		long num1 = 0; //三大法人買進 
		long num2 = 0; //三大法人賣出
		long foreign = 0; //外資買賣超
		long trust = 0; //投信買賣超
		long dealers = 0; //自營商買賣超
		long dealers2 = 0;//自營商買賣超_避險 自2014/12/01 開始
		long total3 = 0;  //三大法人合計
		int count = 0;
		int flag = 0; // 記錄是否為電子業
		while (index1 > 0){
			//取得股票代碼
			//count ++ ;
			index1 = data.indexOf(startStr1, index2);
			//避開資料會重覆一直讀取
			if (index2 > index1){
				break;
			}
			index2 = data.indexOf(endStr, index1+startStr1.length());
			id = data.substring(index1 + startStr1.length(), index2);			 
			//System.out.println("len:"+id.length() + " id:"+id);

			foreign = nextPrice2(data, index2);	//外資		
			//將不是股票代碼的 權證跳掉不處理
			if (id.length() != 4){
				//放在這裡的原因，因為取得 id的字串為 <td align='center'>，而取得外資的字串為 <td align='right'>，但是中間還有 公司名稱，字串為 <td align='center'> ，和 id一樣，
				//若放在外資上面，會變成下一筆id，會取得上一個公司的 名稱
				continue;
			}			
			trust = nextPrice2(data, index2);   //投信
			dealers = nextPrice2(data, index2); //自營商
			dealers2 = nextPrice2(data, index2); //自營商_避險
			dealers = dealers + dealers2; 
			total3 = nextTotal3(data, index2);	 //三大法人買賣超

			if (id.length() == 4){
				count ++;
				flag = isElect(id);
				if (insFast == true) {
					hm.put(id, foreign + "," + trust + "," + dealers + "," + total3);
				} else{
					if (flag == 1){
	            		sql = "UPDATE  priceElect ";
	            	}else{
	            		sql = "UPDATE  priceBase ";
	            	}
	            	sql = sql + "SET ForeignTrading = " + foreign + " , TrustTrading = " + trust + " , DealersTrading = " + dealers + " , Total3 = " + total3 +
	            		  " WHERE id = " + id + " AND DATE = " + date;
					if (count == 10) 		System.out.println("SQL 上市三大法人: "+ sql);
	            	if (id.equals("1301")) 	System.out.println("SQL 上市三大法人: "+ sql);

	            	try {	                			
	        			sm.executeUpdate(sql);
	                } catch ( SQLException e ) {
	                	e.printStackTrace();
	                } catch(Exception e){
	                	e.printStackTrace();
	                }

				}
				
			}else if (foreign != 0 || trust != 0){
				//System.out.println("---- id:"+ id + "  foreign:"+foreign + "  trust:"+ trust + "  dealers:" + dealers + "  total3:" + total3 + "  index1:" + index1 + "  index2:" + index2);
			}
		}   // end while (index1 > 0){
		index1 = 1; index2 = 0;
	}
	
	//上市三大法人
	public static void procTWSETrading(Statement sm, URL url, String date) throws IOException{
		String str = ""; //放取回的資料
		String urlStr = ""; // 網址
	
		Document doc = null; // 放從網站取回的資料
		
		urlStr = url+"";
        doc = Jsoup.parse(url,43000);
        //doc = Jsoup.connect(str).get();
        //read1(str);

        System.out.println("read2() running");
        int count = 0;	 //計算取回第幾列資料
        String sql = ""; //  SQL 語法

        try {
            URL url_address = new URL( urlStr );
            
            // 讀入網頁(字元串流)            
            BufferedReader br = new BufferedReader(new InputStreamReader(url_address.openStream(), "Big5")); //UTF-8
            //BufferedReader br = new BufferedReader(new InputStreamReader(url_address.openStream(), "Big5"));
            //BufferedWriter bw = new BufferedWriter(new FileWriter("URL2.txt", false));    
            String oneLine = null ;
            
            while ((oneLine = br.readLine()) != null) {
            	count ++;
                //bw.write(oneLine);
            	if (count > 500 && count < 520 && oneLine.length() > 5000)
            		System.out.println(count + "len:"+ oneLine.length());
            	if (oneLine.length() > 100000){
            		//System.out.println(count + " len:"+oneLine.length() + ": "+ oneLine);
            		procTotal3(sm, oneLine, date);
            		index1 = 1; index2 = 0;
            		break;
            	}
            	if (count > 600){
            		break;
            	}
            	
            }
            //bw.close();
            br.close();
            
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
 
        
        System.out.println("Done");
        doc = null;
        System.gc();
	}
	
	//上櫃資料_開高低收 成交價格
	public static  void procGTSM( Statement sm , URL url , String date ) {
        //url = new URL("http://www.gretai.org.tw/web/stock/aftertrading/otc_quotes_no1430/stk_wn1430_result.php?l=zh-tw&d="+ROCYear+"/"+strMon+"/"+strDay+"&sect=AL&_=1416481259788" );
        String sql= "";  // 放 sql 語法
        String tmpStr = ""; //暫存 String 格式的檔案
        String data; // 放網頁取回來的資料_ String 格式 
        Document doc=null; // 放網頁取回來的資料_ Document 格式
        //String connectionUrl  ="jdbc:sqlserver://localhost:1433;databaseName=Stock;user=finance101;password=101finance";

        index2 = 0; //索引值_結束位置
        
        String id = null; // 股票代碼
		
		float open = 0.0f; // 開盤價
	    float high = 0.0f; // 最高價
		float low = 0.0f;  // 最低價
	    float close = 0.0f;// 收盤價
	    int Volume = 0;	   // 成交量 
	    int count = 0;	   // 計算已經取得幾個公司資料
	    System.out.println("上櫃開始");

		try {
			doc = Jsoup.parse(url, 3000);
	        data =  doc.toString();
	        //System.out.println("data: "+data);
	        //System.out.println("index1: " + index1);
	        while (index1 > 0) {
	        	id = getID(data,index1+1);
	        	//System.out.print(" count:"+ count + "id: "+ id);
	        	//找不到ID(股票代碼)時，離開
	        	if (id.length() < 1) 
	        		break;
	        	//當股票代碼>4以上時，不處理
	        	if (id.length() > 4) {
	        		continue;
	        	}
	        	count ++;
	        	System.out.print(" count:"+ count + "id: "+ id);
	        	//if (Integer.parseInt(id) > 2000 ) return;
	        	index2 = skip(data, index2); 	  //跳掉 名稱，不處理
	        	close = nextPrice(data, index2);  // 收盤價
	        	index2 = skip(data, index2);	  //跳掉漲跌，不處理
   		
	        	open = nextPrice(data, index2);   // 開盤價
	        	high = nextPrice(data , index2);  // 最高價
	        	low = nextPrice(data, index2);    //收盤價
	        	Volume = nextNum(data, index2)  ; //成交量
	        	sql = "";
	        	if (open > 0) sql = sql + " , " + open;
	        	else		  sql = sql + " , null";
	        	if (high > 0) sql = sql + " , " + high;
	        	else		  sql = sql + " , null";
	        	if (low > 0)  sql = sql + " , " + low;
	        	else		  sql = sql + " , null";
	        	if (close > 0)sql = sql + " , " + close;
	        	else		  sql = sql + " , null";
	        	sql = sql + " , " + Volume ;
	        	System.out.println("Volume:"+Volume+"  sql: "+ sql);
	        	if (Volume == 0) continue;
	        	if (insFast == true) { // 因為價格一定會有資料，所以用 INSERT
	        		tmpStr = (String) hm.get(id);
	        		if (tmpStr == null) { tmpStr = "0,0,0,0";}
	        		sql = "INSERT INTO priceGTSM (ID , date , [open] , [high] , [low] , [close] , Volume , ForeignTrading , TrustTrading , DealersTrading , Total3 ) " +
	  	        		  " VALUES ( " + id +"," + date + sql + "," +tmpStr +")";

	        	}else{  // 因為 價格會先執行，所以用 Insert
	        		sql = "INSERT INTO priceGTSM (ID , date , [open] , [high] , [low] , [close] , Volume  ) " +
		  	        	  " VALUES ( " + id +"," + date + sql + ")";
	        	}

	        	if (count == 1 )		   System.out.println("sql 上櫃價格: "+ sql);
	        	if (id.equals("6180") )    System.out.println("sql 上櫃價格: "+ sql);

	        	System.out.println("sql: "+ sql);

	        	try {
	        		sm.executeUpdate(sql);
	        	} catch(Exception e){
	        		e.printStackTrace();
	        	}

	        	open = 0; high = 0; low = 0; close = 0;Volume = 0; //將 開、高、低、收 的價格清除
	        }   // while (index1 > 0) {
	        index1 = 1; index2 = 0;

		} catch (IOException  e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (Exception e){
			e.printStackTrace();
		}        
	}
	
	//上櫃三大法人交易
	public static  void procGTSMTrading( Statement sm , URL url , String date ) {
        //url = new URL("http://www.gretai.org.tw/web/stock/aftertrading/otc_quotes_no1430/stk_wn1430_result.php?l=zh-tw&d="+ROCYear+"/"+strMon+"/"+strDay+"&sect=AL&_=1416481259788" );
        String sql= ""; //放 SQL 語法
        String data; // 放網頁取回來的資料_ String 格式 
        Document doc=null; // 放網頁取回來的資料_ Document 格式
        //String connectionUrl  ="jdbc:sqlserver://localhost:1433;databaseName=Stock;user=finance101;password=101finance";

        index1 = 1; //索引值_開始位置
        index2 = 0; //索引值_結束位置
        
        String id = null; // 股票代碼        
		long foreign = 0; //外資買賣超
		long trust = 0; //投信買賣超
		long dealers = 0; //自營商買賣超
		long total = 0;   // 總計
		
		int count = 0 ;// 計算有幾筆公司資料

		try {
			doc = Jsoup.parse(url, 3000);
	        //System.out.println("---------------------------------");	        

	        data =  doc.toString();
	        //System.out.println("index1: " + index1);
	        //System.out.println("data: "+data);
	        while (index1 > 0) {
	        	id = getID(data,index1+1);
	        	//找不到ID(股票代碼)時，離開
	        	if (id.length() < 1) 
	        		break;
	        	id = id.substring(0, id.length() -1);
	        	//當股票代碼>4以上時，不處理
	        	if (id.length() > 4) {
	        		continue;
	        	}
	        	count ++;
	        	index2 = skip(data, index2); 	  //跳掉 名稱，不處理
	        	index2 = skip(data, index2); 	  //跳掉 外資買股，不處理
	        	index2 = skip(data, index2); 	  //跳掉 外資賣股，不處理
	        	foreign = nextVolume(data, index2); //外資買賣股
	        	index2 = skip(data, index2); 	  //跳掉 投信買股，不處理
	        	index2 = skip(data, index2); 	  //跳掉 投信賣股，不處理
	        	trust = nextVolume(data, index2); //投信買賣股
	        	if (Integer.parseInt(date)  > 20070420  && Integer.parseInt(date) < 20141201 ) index2 = skip(data, index2);//跳掉自營商買股，不處理
	        	if (Integer.parseInt(date)  > 20070420  && Integer.parseInt(date) < 20141201 ) index2 = skip(data, index2);//跳掉自營商賣股，不處理
	        	dealers = nextVolume(data, index2); //自營商買賣股	
	        	total = foreign + trust + dealers ; //合計
	        	if (insFast == true) {
	        		hm.put(id, foreign + "," + trust + "," + dealers + "," + total );	        		
	        	}else{
	        		sql = "UPDATE  priceGTSM SET ForeignTrading = "+foreign+" , TrustTrading = "+trust +" , DealersTrading = " + dealers +", Total3 = "+total +
			        		  " WHERE id = "+id+" AND DATE = " + date;
		        	try {
		        		sm.executeUpdate(sql);
		        	} catch(Exception e){
		        		e.printStackTrace();
		        	}

		        	if (count == 1) 	  System.out.println("sql 上櫃三大法人: "+ sql);
		        	if(id.equals("6180")) System.out.println("sql 上櫃三大法人: "+ sql);
	        	}


	        	foreign = 0; trust = 0; dealers = 0; total = 0; //將數量清除
	        } 	// end while (index1 > 0) {
	        index1 = 1; index2 = 0;
		} catch (IOException e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (Exception e){
			e.printStackTrace();
		} 
		doc = null;
	}

	public static void main(String[] args) throws IOException, SQLException {
		// TODO Auto-generated method stub
	 	// 日期處理
	     Date now = new Date();
		 String strYearMon = ""; // 西元年、月
		 String strMon = ""; // 月
		 String strDay = ""; // 日
		 int ROCYear = 0; // 民國年
		 int year = 0;    //西元年
		 
		 //==格式化
		 SimpleDateFormat nowdate = new java.text.SimpleDateFormat("yyyyMMdd");		 
		 nowdate.setTimeZone(TimeZone.getTimeZone("GMT+8")); //==GMT標準時間往後加八小時		 
		 String date = nowdate.format(new java.util.Date()); //==取得目前時間 // 西元年、月、日
		 int mon[]= {0,31,29,31,30,31,30,31,31,30,31,30,31};
		 
		 year = Integer.parseInt( date.substring(0, 4) ) ;
		 strMon = date.substring(4, 6) ;
		 strDay = date.substring(6, 8) ;
		 strYearMon = year+strMon;
		 ROCYear = year-1911;
		 
		 int count=0;
		 int num = 0; 		//成交量
		 String sql = "";//放sql 語法
		 String str = "";
		 URL url = new URL("http://www.twse.com.tw/ch/trading/exchange/MI_INDEX/genpage/Report"+strYearMon+"/A11220141031ALLBUT0999_1.php?select2=ALLBUT0999&chk_date=103/10/31");
		 //ResultSet rs = null;
		 String id = "";   //股票代號

		 float open = 0.0f; // 開盤價
		 float high = 0.0f; // 最高價
		 float low = 0.0f;  // 最低價
		 float close = 0.0f;// 收盤價
		 float PERate = 0.0f;// 本益比
		 
         //連資料庫		 
		 String connectionUrl = null;
		 Connection cn;
		 Statement sm = null;		 
        try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
	        connectionUrl  ="jdbc:sqlserver://localhost:1433;databaseName=Stock;user=finance101;password=101finance";

			cn = DriverManager.getConnection(connectionUrl);
			sm = cn.createStatement();
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			System.out.println("JDBC沒有驅動程式" + e1.getMessage());			
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();		
	    } catch(Exception e){
	        System.out.println( e.toString() );
	    }
		 
         for (int i = 2015;i<= 2015;i++){
         	for (int j = 1;j<=1;j++){
         		//for (int k = 1;k<= mon[j] ; k++){
         		for (int k =12;k<= 12 ; k++){
         			count = 0;
        			year = i;
        			strMon = "0"+j;
        			strMon = strMon.substring(strMon.length()-2,strMon.length());
        			strDay = "0" + k;
        			//strDay = "19";
        			strDay = strDay.substring(strDay.length()-2 , strDay.length());
        			strYearMon = year + strMon;
        			ROCYear = year - 1911;
        			date = strYearMon + strDay;
        			String ROCDate = ROCYear +"/" + strMon + "/" + strDay;
        			System.out.println("date:" + date);
		 
        			try{
        				if (insFast == false){
        					//若是有很多天的資料要執行，因為只執行一次INSERT ，而不執行 UPDATE ，會比較快，
        					//但是資料裡，一定會有成交價，但是三大法人不一定會買，所以就會變成
        					//若要執行很多天的資料，就會先執行三大法人買賣超，再執行價格，
        					//這樣就一定會有資料
			            	//上市 成交價格
			            	//http://www.twse.com.tw/ch/trading/exchange/MI_INDEX/genpage/Report201411/A11220141118ALLBUT0999_1.php?select2=ALLBUT0999&chk_date=103/11/18
				            url = new URL("http://www.twse.com.tw/ch/trading/exchange/MI_INDEX/genpage/Report" + strYearMon + "/A112" + date + "ALLBUT0999_1.php?select2=ALLBUT0999&chk_date=" + ROCDate );
				            System.out.println("url上市價格: "+url);
				            procTWSE(sm, url, date);	

				            //上櫃 成交價格
				            //http://www.gretai.org.tw/web/stock/aftertrading/otc_quotes_no1430/stk_wn1430_result.php?l=zh-tw&d=103/11/18&sect=AL&_=1416481259788
				            url = new URL("http://www.gretai.org.tw/web/stock/aftertrading/otc_quotes_no1430/stk_wn1430_result.php?l=zh-tw&d=" + ROCDate + "&sect=AL&_=1416481259788" );
			                System.out.println("url上櫃價格: "+url);
			                procGTSM(sm, url , date);        
			            }

			            //上市三大法人買賣超
			            url = new URL("http://www.twse.com.tw/ch/trading/fund/T86/T86.php?input_date=" + ROCDate + "&select2=ALL");
			            //System.out.println("url上市三法人: "+url);
			            //procTWSETrading(sm, url, date);
			            
			            //上櫃三大法人買賣超
			            // 2007.4.21 ~ 
			            if (Integer.parseInt(date) >= 20070421 && Integer.parseInt(date) < 20141201){
			            // http://www.gretai.org.tw/web/stock/3insti/daily_trade/3itrade_result.php?l=zh-tw&t=D&d=103/03/05&_=1419934412424
			            	url = new URL("http://www.gretai.org.tw/web/stock/3insti/daily_trade/3itrade_result.php?l=zh-tw&t=D&d=" + ROCDate + "&_=1419934412424");			            
			            }else if (Integer.parseInt(date ) >= 20141201){
			            	// 2014.12.1 ~
			            	// http://www.gretai.org.tw/web/stock/3insti/daily_trade/3itrade_hedge_result.php?l=zh-tw&t=D&d=103/12/29&_=1419863488111
			            	url = new URL("http://www.gretai.org.tw/web/stock/3insti/daily_trade/3itrade_hedge_result.php?l=zh-tw&t=D&d="+ROCDate+"&_=1419863488111");
			            }
			            System.out.println("url上櫃三法人: "+url);
			            //procGTSMTrading(sm, url, date);

			            if (insFast == true){
			            	//上市 成交價格
			            	//http://www.twse.com.tw/ch/trading/exchange/MI_INDEX/genpage/Report201411/A11220141118ALLBUT0999_1.php?select2=ALLBUT0999&chk_date=103/11/18
				            url = new URL("http://www.twse.com.tw/ch/trading/exchange/MI_INDEX/genpage/Report" + strYearMon + "/A112" + date + "ALLBUT0999_1.php?select2=ALLBUT0999&chk_date=" + ROCDate );
				            //System.out.println("url上市價格: "+url);
				            //procTWSE(sm, url, date);	

				            //上櫃 成交價格
				            //http://www.gretai.org.tw/web/stock/aftertrading/otc_quotes_no1430/stk_wn1430_result.php?l=zh-tw&d=103/11/18&sect=AL&_=1416481259788
				            url = new URL("http://www.gretai.org.tw/web/stock/aftertrading/otc_quotes_no1430/stk_wn1430_result.php?l=zh-tw&d=" + ROCDate + "&sect=AL&_=1416481259788" );
				            System.out.println("url上櫃價格: "+url);
			                procGTSM(sm, url , date);        
			            }

			            System.out.println("--------------------------------------");
			            hm.clear();
			        } catch(Exception e){
			            System.out.println( e.toString() );
			        }
		 
        		} // end for (int k = 1;k< mon[j] ; k++){
        	}
        } // end for (int i = 2005;i< 2014;i++){
        sm.close();
        hm = null;
        //cn.close();
	}

}
