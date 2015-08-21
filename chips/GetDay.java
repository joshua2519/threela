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
	static boolean insFast = true; // �O�_�O�b�U��4�I����A�~�]�{��
	static int index1 = 1; //���ƶ}�l����m�C��1�� "����m 
	static int index2 = 0; //���Ƶ�������m�C��2�� "����m
	//static String startStr = "[&quot;";
	static String startStr = "[\"";
	static String quot = "\"";
	static HashMap hm = new HashMap();     //��Ѳ��N�X���}���C�� �����ơA�n�� ����M �T�j�k�H�i�H�@�_�B�z
	
    //Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

	public static boolean isNumeric(String str)	{
		Pattern pattern = Pattern.compile("-?[0-9.,]*");
		Matcher isNum = pattern.matcher(str);
		if( !isNum.matches() ) 	{
			return false;
		}
		return true;
	}
	//���L�@�Ӹ�� 
	//�^��  �o����Ƶ�����m skip(String ���, int �W�@����Ƶ�����m)
	public static int skip(String data,int index){
		//int index2 = 0;
		index1 = data.indexOf(quot , index+1);
		index2 = data.indexOf(quot , index1+1);
		return index2; // �^�ǳo����Ƶ�����m
	}
	//���oid���
	//�^�� �r�� getID(String ��� , int �W�@����Ƶ�����m)
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
	
	//���o�U�@���ƶq
	//float nextVolume(String �n�䪺��� , int �W�@����Ƶ�����m)
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
	
	//���o�U�@������
	//float nextPrice(String �n�䪺��� , int �W�@����Ƶ�����m)
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
	
	//���o�U�@������
	//float nextPrice(String �n�䪺��� , int �W�@����Ƶ�����m)
	public static int nextPrice2(String data, int index){
		String startStr = "<td align='right'>"; //�n�䪺�}�l��m���
		String endStr = "</td>";  //�n�䪺������m���
		int num1 = 0;    //�R�i����
		int num2 = 0;    //��X����
		
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
		String startStr = "<td align='right'>";  //���o�Ʀr�ѦҦr��
		String endStr = "</td>";			 //�����r���ѦҦr��
		int num1 = 0;						 //���o���Ʀr
		
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
	
	//�F�ѶǤJ���Ѳ��N�X�A�O�_���q�l�~���N�X�A�Y�O���ܡA�^��1	
	public static int isElect(String id){
		int elect[] = {23,24,49,52,53,54,61,62,80,81,82}; // �q�l�~�N�X
		int elect1 = 30; //�]�w�q�l�~�N�X �}�l����m
		int elect2 = 37; //�]�w�q�l�~�N�X ��������m
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
	//�W����Ʀ������
	public static void procTWSE(Statement sm , URL url, String date  ) throws IOException {		
		int count = 0;		// �p����o�X�����
		int num = 0;		// ����q

		int classify = 0;   //��Ѳ��N�X���e2�ӼƦr�A�H����O�_���q�l�~
		int flag = 0;    //��O�O�_���q�l�~�� 1���q�l�~
		
		float open = 0.0f; // �}�L��
	    float high = 0.0f; // �̰���
		float low = 0.0f;  // �̧C��
	    float close = 0.0f;// ���L��
		float PERate = 0.0f;// ���q��
	    int Volume = 0;	   // ����q 
		String str = "";   // ��q�������^�����
		String sql = ""; // sql  �y�k
		String tmpStr = ""; //String ���A�Ȧs��
        String id = null; // �Ѳ��N�X
        String strYearMon = ""; // �褸�~�B��  
		
        Document doc=null; // ��������^�Ӫ����_ Document �榡
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

                	open = 0; high = 0; low = 0; close = 0; //�N �}�B���B�C�B�� ������M��
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
     
          // Ū�J����(�줸��y)
          BufferedInputStream bis = new BufferedInputStream(pageUrl.openStream());
          BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream("c:\\URL1.txt", false));
          
          System.out.println("read1() running " );
          while ((count = bis.read(chunk, 0, chunksize)) != -1) {
              bos.write(chunk, 0, count); // �g�J�ɮ�
          }
          bos.close();
          bis.close();

        System.out.println("Done");   
       }catch (IOException e) {
           e.printStackTrace();
       }
    }
	// �W���T�j�k�H��Ƽg�J
	public static void procTotal3(Statement sm, String data , String date){
		String startStr1 = "<td align='center'>"; //���o�Ѳ��N�X�ѦҦr��
		String startStr2 = "<td align='right'>";  //���o�Ʀr�ѦҦr��
		String endStr = "</td>";			 //�����r���ѦҦr��
		String id = ""; // ���o�Ѳ��N �X
		String sql = ""; //�� sql �y�k
		long num1 = 0; //�T�j�k�H�R�i 
		long num2 = 0; //�T�j�k�H��X
		long foreign = 0; //�~��R��W
		long trust = 0; //��H�R��W
		long dealers = 0; //����ӶR��W
		long dealers2 = 0;//����ӶR��W_���I ��2014/12/01 �}�l
		long total3 = 0;  //�T�j�k�H�X�p
		int count = 0;
		int flag = 0; // �O���O�_���q�l�~
		while (index1 > 0){
			//���o�Ѳ��N�X
			//count ++ ;
			index1 = data.indexOf(startStr1, index2);
			//�׶}��Ʒ|���Ф@��Ū��
			if (index2 > index1){
				break;
			}
			index2 = data.indexOf(endStr, index1+startStr1.length());
			id = data.substring(index1 + startStr1.length(), index2);			 
			//System.out.println("len:"+id.length() + " id:"+id);

			foreign = nextPrice2(data, index2);	//�~��		
			//�N���O�Ѳ��N�X�� �v�Ҹ������B�z
			if (id.length() != 4){
				//��b�o�̪���]�A�]�����o id���r�ꬰ <td align='center'>�A�Ө��o�~�ꪺ�r�ꬰ <td align='right'>�A���O�����٦� ���q�W�١A�r�ꬰ <td align='center'> �A�M id�@�ˡA
				//�Y��b�~��W���A�|�ܦ��U�@��id�A�|���o�W�@�Ӥ��q�� �W��
				continue;
			}			
			trust = nextPrice2(data, index2);   //��H
			dealers = nextPrice2(data, index2); //�����
			dealers2 = nextPrice2(data, index2); //�����_���I
			dealers = dealers + dealers2; 
			total3 = nextTotal3(data, index2);	 //�T�j�k�H�R��W

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
					if (count == 10) 		System.out.println("SQL �W���T�j�k�H: "+ sql);
	            	if (id.equals("1301")) 	System.out.println("SQL �W���T�j�k�H: "+ sql);

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
	
	//�W���T�j�k�H
	public static void procTWSETrading(Statement sm, URL url, String date) throws IOException{
		String str = ""; //����^�����
		String urlStr = ""; // ���}
	
		Document doc = null; // ��q�������^�����
		
		urlStr = url+"";
        doc = Jsoup.parse(url,43000);
        //doc = Jsoup.connect(str).get();
        //read1(str);

        System.out.println("read2() running");
        int count = 0;	 //�p����^�ĴX�C���
        String sql = ""; //  SQL �y�k

        try {
            URL url_address = new URL( urlStr );
            
            // Ū�J����(�r����y)            
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
	
	//�W�d���_�}���C�� �������
	public static  void procGTSM( Statement sm , URL url , String date ) {
        //url = new URL("http://www.gretai.org.tw/web/stock/aftertrading/otc_quotes_no1430/stk_wn1430_result.php?l=zh-tw&d="+ROCYear+"/"+strMon+"/"+strDay+"&sect=AL&_=1416481259788" );
        String sql= "";  // �� sql �y�k
        String tmpStr = ""; //�Ȧs String �榡���ɮ�
        String data; // ��������^�Ӫ����_ String �榡 
        Document doc=null; // ��������^�Ӫ����_ Document �榡
        //String connectionUrl  ="jdbc:sqlserver://localhost:1433;databaseName=Stock;user=finance101;password=101finance";

        index2 = 0; //���ޭ�_������m
        
        String id = null; // �Ѳ��N�X
		
		float open = 0.0f; // �}�L��
	    float high = 0.0f; // �̰���
		float low = 0.0f;  // �̧C��
	    float close = 0.0f;// ���L��
	    int Volume = 0;	   // ����q 
	    int count = 0;	   // �p��w�g���o�X�Ӥ��q���
	    System.out.println("�W�d�}�l");

		try {
			doc = Jsoup.parse(url, 3000);
	        data =  doc.toString();
	        //System.out.println("data: "+data);
	        //System.out.println("index1: " + index1);
	        while (index1 > 0) {
	        	id = getID(data,index1+1);
	        	//System.out.print(" count:"+ count + "id: "+ id);
	        	//�䤣��ID(�Ѳ��N�X)�ɡA���}
	        	if (id.length() < 1) 
	        		break;
	        	//��Ѳ��N�X>4�H�W�ɡA���B�z
	        	if (id.length() > 4) {
	        		continue;
	        	}
	        	count ++;
	        	System.out.print(" count:"+ count + "id: "+ id);
	        	//if (Integer.parseInt(id) > 2000 ) return;
	        	index2 = skip(data, index2); 	  //���� �W�١A���B�z
	        	close = nextPrice(data, index2);  // ���L��
	        	index2 = skip(data, index2);	  //�������^�A���B�z
   		
	        	open = nextPrice(data, index2);   // �}�L��
	        	high = nextPrice(data , index2);  // �̰���
	        	low = nextPrice(data, index2);    //���L��
	        	Volume = nextNum(data, index2)  ; //����q
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
	        	if (insFast == true) { // �]������@�w�|����ơA�ҥH�� INSERT
	        		tmpStr = (String) hm.get(id);
	        		if (tmpStr == null) { tmpStr = "0,0,0,0";}
	        		sql = "INSERT INTO priceGTSM (ID , date , [open] , [high] , [low] , [close] , Volume , ForeignTrading , TrustTrading , DealersTrading , Total3 ) " +
	  	        		  " VALUES ( " + id +"," + date + sql + "," +tmpStr +")";

	        	}else{  // �]�� ����|������A�ҥH�� Insert
	        		sql = "INSERT INTO priceGTSM (ID , date , [open] , [high] , [low] , [close] , Volume  ) " +
		  	        	  " VALUES ( " + id +"," + date + sql + ")";
	        	}

	        	if (count == 1 )		   System.out.println("sql �W�d����: "+ sql);
	        	if (id.equals("6180") )    System.out.println("sql �W�d����: "+ sql);

	        	System.out.println("sql: "+ sql);

	        	try {
	        		sm.executeUpdate(sql);
	        	} catch(Exception e){
	        		e.printStackTrace();
	        	}

	        	open = 0; high = 0; low = 0; close = 0;Volume = 0; //�N �}�B���B�C�B�� ������M��
	        }   // while (index1 > 0) {
	        index1 = 1; index2 = 0;

		} catch (IOException  e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (Exception e){
			e.printStackTrace();
		}        
	}
	
	//�W�d�T�j�k�H���
	public static  void procGTSMTrading( Statement sm , URL url , String date ) {
        //url = new URL("http://www.gretai.org.tw/web/stock/aftertrading/otc_quotes_no1430/stk_wn1430_result.php?l=zh-tw&d="+ROCYear+"/"+strMon+"/"+strDay+"&sect=AL&_=1416481259788" );
        String sql= ""; //�� SQL �y�k
        String data; // ��������^�Ӫ����_ String �榡 
        Document doc=null; // ��������^�Ӫ����_ Document �榡
        //String connectionUrl  ="jdbc:sqlserver://localhost:1433;databaseName=Stock;user=finance101;password=101finance";

        index1 = 1; //���ޭ�_�}�l��m
        index2 = 0; //���ޭ�_������m
        
        String id = null; // �Ѳ��N�X        
		long foreign = 0; //�~��R��W
		long trust = 0; //��H�R��W
		long dealers = 0; //����ӶR��W
		long total = 0;   // �`�p
		
		int count = 0 ;// �p�⦳�X�����q���

		try {
			doc = Jsoup.parse(url, 3000);
	        //System.out.println("---------------------------------");	        

	        data =  doc.toString();
	        //System.out.println("index1: " + index1);
	        //System.out.println("data: "+data);
	        while (index1 > 0) {
	        	id = getID(data,index1+1);
	        	//�䤣��ID(�Ѳ��N�X)�ɡA���}
	        	if (id.length() < 1) 
	        		break;
	        	id = id.substring(0, id.length() -1);
	        	//��Ѳ��N�X>4�H�W�ɡA���B�z
	        	if (id.length() > 4) {
	        		continue;
	        	}
	        	count ++;
	        	index2 = skip(data, index2); 	  //���� �W�١A���B�z
	        	index2 = skip(data, index2); 	  //���� �~��R�ѡA���B�z
	        	index2 = skip(data, index2); 	  //���� �~���ѡA���B�z
	        	foreign = nextVolume(data, index2); //�~��R���
	        	index2 = skip(data, index2); 	  //���� ��H�R�ѡA���B�z
	        	index2 = skip(data, index2); 	  //���� ��H��ѡA���B�z
	        	trust = nextVolume(data, index2); //��H�R���
	        	if (Integer.parseInt(date)  > 20070420  && Integer.parseInt(date) < 20141201 ) index2 = skip(data, index2);//��������ӶR�ѡA���B�z
	        	if (Integer.parseInt(date)  > 20070420  && Integer.parseInt(date) < 20141201 ) index2 = skip(data, index2);//��������ӽ�ѡA���B�z
	        	dealers = nextVolume(data, index2); //����ӶR���	
	        	total = foreign + trust + dealers ; //�X�p
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

		        	if (count == 1) 	  System.out.println("sql �W�d�T�j�k�H: "+ sql);
		        	if(id.equals("6180")) System.out.println("sql �W�d�T�j�k�H: "+ sql);
	        	}


	        	foreign = 0; trust = 0; dealers = 0; total = 0; //�N�ƶq�M��
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
	 	// ����B�z
	     Date now = new Date();
		 String strYearMon = ""; // �褸�~�B��
		 String strMon = ""; // ��
		 String strDay = ""; // ��
		 int ROCYear = 0; // ����~
		 int year = 0;    //�褸�~
		 
		 //==�榡��
		 SimpleDateFormat nowdate = new java.text.SimpleDateFormat("yyyyMMdd");		 
		 nowdate.setTimeZone(TimeZone.getTimeZone("GMT+8")); //==GMT�зǮɶ�����[�K�p��		 
		 String date = nowdate.format(new java.util.Date()); //==���o�ثe�ɶ� // �褸�~�B��B��
		 int mon[]= {0,31,29,31,30,31,30,31,31,30,31,30,31};
		 
		 year = Integer.parseInt( date.substring(0, 4) ) ;
		 strMon = date.substring(4, 6) ;
		 strDay = date.substring(6, 8) ;
		 strYearMon = year+strMon;
		 ROCYear = year-1911;
		 
		 int count=0;
		 int num = 0; 		//����q
		 String sql = "";//��sql �y�k
		 String str = "";
		 URL url = new URL("http://www.twse.com.tw/ch/trading/exchange/MI_INDEX/genpage/Report"+strYearMon+"/A11220141031ALLBUT0999_1.php?select2=ALLBUT0999&chk_date=103/10/31");
		 //ResultSet rs = null;
		 String id = "";   //�Ѳ��N��

		 float open = 0.0f; // �}�L��
		 float high = 0.0f; // �̰���
		 float low = 0.0f;  // �̧C��
		 float close = 0.0f;// ���L��
		 float PERate = 0.0f;// ���q��
		 
         //�s��Ʈw		 
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
			System.out.println("JDBC�S���X�ʵ{��" + e1.getMessage());			
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
        					//�Y�O���ܦh�Ѫ���ƭn����A�]���u����@��INSERT �A�Ӥ����� UPDATE �A�|����֡A
        					//���O��Ƹ̡A�@�w�|��������A���O�T�j�k�H���@�w�|�R�A�ҥH�N�|�ܦ�
        					//�Y�n����ܦh�Ѫ���ơA�N�|������T�j�k�H�R��W�A�A�������A
        					//�o�˴N�@�w�|�����
			            	//�W�� �������
			            	//http://www.twse.com.tw/ch/trading/exchange/MI_INDEX/genpage/Report201411/A11220141118ALLBUT0999_1.php?select2=ALLBUT0999&chk_date=103/11/18
				            url = new URL("http://www.twse.com.tw/ch/trading/exchange/MI_INDEX/genpage/Report" + strYearMon + "/A112" + date + "ALLBUT0999_1.php?select2=ALLBUT0999&chk_date=" + ROCDate );
				            System.out.println("url�W������: "+url);
				            procTWSE(sm, url, date);	

				            //�W�d �������
				            //http://www.gretai.org.tw/web/stock/aftertrading/otc_quotes_no1430/stk_wn1430_result.php?l=zh-tw&d=103/11/18&sect=AL&_=1416481259788
				            url = new URL("http://www.gretai.org.tw/web/stock/aftertrading/otc_quotes_no1430/stk_wn1430_result.php?l=zh-tw&d=" + ROCDate + "&sect=AL&_=1416481259788" );
			                System.out.println("url�W�d����: "+url);
			                procGTSM(sm, url , date);        
			            }

			            //�W���T�j�k�H�R��W
			            url = new URL("http://www.twse.com.tw/ch/trading/fund/T86/T86.php?input_date=" + ROCDate + "&select2=ALL");
			            //System.out.println("url�W���T�k�H: "+url);
			            //procTWSETrading(sm, url, date);
			            
			            //�W�d�T�j�k�H�R��W
			            // 2007.4.21 ~ 
			            if (Integer.parseInt(date) >= 20070421 && Integer.parseInt(date) < 20141201){
			            // http://www.gretai.org.tw/web/stock/3insti/daily_trade/3itrade_result.php?l=zh-tw&t=D&d=103/03/05&_=1419934412424
			            	url = new URL("http://www.gretai.org.tw/web/stock/3insti/daily_trade/3itrade_result.php?l=zh-tw&t=D&d=" + ROCDate + "&_=1419934412424");			            
			            }else if (Integer.parseInt(date ) >= 20141201){
			            	// 2014.12.1 ~
			            	// http://www.gretai.org.tw/web/stock/3insti/daily_trade/3itrade_hedge_result.php?l=zh-tw&t=D&d=103/12/29&_=1419863488111
			            	url = new URL("http://www.gretai.org.tw/web/stock/3insti/daily_trade/3itrade_hedge_result.php?l=zh-tw&t=D&d="+ROCDate+"&_=1419863488111");
			            }
			            System.out.println("url�W�d�T�k�H: "+url);
			            //procGTSMTrading(sm, url, date);

			            if (insFast == true){
			            	//�W�� �������
			            	//http://www.twse.com.tw/ch/trading/exchange/MI_INDEX/genpage/Report201411/A11220141118ALLBUT0999_1.php?select2=ALLBUT0999&chk_date=103/11/18
				            url = new URL("http://www.twse.com.tw/ch/trading/exchange/MI_INDEX/genpage/Report" + strYearMon + "/A112" + date + "ALLBUT0999_1.php?select2=ALLBUT0999&chk_date=" + ROCDate );
				            //System.out.println("url�W������: "+url);
				            //procTWSE(sm, url, date);	

				            //�W�d �������
				            //http://www.gretai.org.tw/web/stock/aftertrading/otc_quotes_no1430/stk_wn1430_result.php?l=zh-tw&d=103/11/18&sect=AL&_=1416481259788
				            url = new URL("http://www.gretai.org.tw/web/stock/aftertrading/otc_quotes_no1430/stk_wn1430_result.php?l=zh-tw&d=" + ROCDate + "&sect=AL&_=1416481259788" );
				            System.out.println("url�W�d����: "+url);
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
