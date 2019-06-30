package com.mgage.httputil;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;


/**
 * Hello world!
 *
 */
public class App 
{
	public static String CONST_charset_utf ="UTF-8";
	public static String CONST_charset ="charset";
	public static String CONST_contype ="Content-Type";
	public static String CONST_contype_appxurlen ="application/x-www-form-urlencoded";
	public static String CONST_data_to_post ="data_to_post";

	
    public static void main( String[] args ) throws Exception
    {
		  HashMap<String, String> headermap = new HashMap<String, String>() ;
		  HashMap<String, String> postMap = new HashMap<String, String>() ;
	  	   headermap = new HashMap<String, String>() ;
	  	   
	  	   hitresponsys();
	  	   if(true)
	  		   return;
	  	   
	  	   
	  	   
	  	   
	  	 //params.put("authEnabled", "true");
		//	params.put("Authorization", "Basic Ymhvb3Q6S2M4THdyMmVnSExOUFNtQzkyRkpjSkRDdGtHR3dlUGtaTmNZZ1dqZ2NmZ2k0NjM4");
	   	  headermap.put(CONST_charset,
	   				CONST_charset_utf);
	   	  
    	HTTPRequestUtility http_Req = new HTTPRequestUtility() ;
		//  HTTPResult res = http_Req.doGetRequest("http://206.183.111.29/newtimessms.asp?mobile=919591984660&sendermobile=919535758006&sdate=04%2F18%2F17+12%3A26%3A03+PM&message=FAGE+KTUZ4S7RMP",
				//  headermap,
			//	  "http://206.183.111.29/newtimessms.asp");
    	
    	
    	//String url_ticket = "sid=6013121220574808000&dest=918197434872&stime=2017-05-12+12%3A20%3A57&reason=DELIVRD&status=001&dtime=2017-05-12+12%3A21%3A07";
    	postMap.put("stime", "2011-04-21");
    	postMap.put("sid", "21232324243432424234");
    	postMap.put("status", "001");
    	System.setProperty("jsse.enableSNIExtension", "false");
    	String url3 ="https://sandbox-ngasce.cs5.force.com/services/apexrest/connecttoenquiry?mobileNumber=919004465749";
    	
    	 String url2 ="https://www.buyalenovo.com/admin/rest/service/voiceCallResponse?CID=8217092717034982660&DS=917760413211&ST=SUCCESS&DL=1-1&CD=16&TM=2017-09-27%2017:03:49";
    	//String url2 = "https://upi.obcindia.co.in/upi/updateMobSMSReq?message=testing&mobile=919000000001";
    	
    	  HTTPResult res = http_Req.doGetRequest(url3, headermap, "sandbox-ngasce.cs5.force.com");//("http://localhost:8080/adapter/1001/POST?",
    				// headermap,postMap);
    				
    	
		 // HTTPResult res = http_Req.doPostRequest("http://localhost:8080/adapter/1001/POST?",
		 //headermap,postMap);
		  
		  //  "https://35.154.55.196:443/");
		  
    	
    //	https://staging.litmusworld.com/rateus/api/mgage/sms_status?sid=6013121220574808000&dest=918197434872&stime=2017-05-12+12%3A20%3A57&reason=DELIVRD&status=001&dtime=2017-05-12+12%3A21%3A07
    		

    	
		//HTTPResult res = http_Req.doGetRequest("https://www.workmarket.com/api/v1/authorization/request", null, "https://www.workmarket.com/api/v1/authorization/request");
		System.out.println(res.status);
		System.out.println(res.result);
		
		if(true)
			return;
		
    	
    	SimpleDateFormat specificFormat = new SimpleDateFormat("YYYY-MM-DDHH:MM:SS.sss");
    	
    	Date dt = Calendar.getInstance().getTime();
    	String tm = URLEncoder.encode(specificFormat.format(dt),"UTF-8");
		String baseurl ="sdsdsdR#&";
		if(baseurl.endsWith("&"))
		{
			baseurl = baseurl.substring(0,baseurl.length()-1);
			System.out.println(baseurl);
			
		}
		
		 http_Req = new HTTPRequestUtility() ;
		 //HTTPResult res =  http_Req.doGet("https://upi.csb.co.in/UPI/SMSRequest?msg=UPI", null,param);
		//String surl = "https://bhoot:Kc8Lwr2egHLNPSmC92FJcJDCtkGGwePkZNcYgWjgcfgi4638@connekt-sandbox.flipkart.net/v1/sms/callbacks/flipkart/unicel?";
		
		//String surl = "https://bhoot:Kc8Lwr2egHLNPSmC92FJcJDCtkGGwePkZNcYgWjgcfgi4638@connekt-sandbox.flipkart.net/v1/sms/callbacks/flipkart/unicel?";
		
		String surl = "https://115.112.85.96:9002/axisWebReceiver.php?FROM=919000000002&transid=121308030000009143&circle=Karnataka&operator=Airtel&channelNm=SMA&TO=08049336262";
		
		HashMap<String,String> params = new HashMap<String,String>();
//		String surl=	"https://bhoot:Kc8Lwr2egHLNPSmC92FJcJDCtkGGwePkZNcYgWjgcfgi4638@connekt-sandbox.flipkart.net/v1/sms/callbacks/flipkart/unicel?";
		
		params.put("sid", "6012141417248461500");
		params.put("stime", "2017-01-14 14:17:24");
		params.put("reason", "DELIVRD");
		params.put("cust_mid", "d12468b4ac3c473b9bc16699c955c389");
		params.put("dtime", "2017-01-14 14:17:26");
		params.put("circle", "Chennai");
		params.put("operator", "Vodafone");
		params.put("status", "001");
		params.put("dest", "919884307168");
		 headermap = new HashMap<String, String>() ;
	  	   headermap = new HashMap<String, String>() ;
	  	 //params.put("authEnabled", "true");
		//	params.put("Authorization", "Basic Ymhvb3Q6S2M4THdyMmVnSExOUFNtQzkyRkpjSkRDdGtHR3dlUGtaTmNZZ1dqZ2NmZ2k0NjM4");
	   	  headermap.put(CONST_charset,
	   				CONST_charset_utf);
	 		
		  //HTTPResult res = http_Req.doGetRequest(surl,headermap,"https://115.112.85.96:9002/axisWebReceiver.php");

		
    	//String url ="https://ba96qjptne.execute-api.us-east-1.amazonaws.com/dev/sms-gateway/receive-response?message=KAP%20*%20this%20is%20a%20test%20message.&from=9190000";
    	//String url ="https://ba96qjptne.execute-api.us-east-1.amazonaws.com/dev/sms-gateway/receive-response?message=KAP%20*%20this%20is%20a%20test%20message.&from=919500070292";
    	String url // ="https://fms.lntinfotech.com/ReceiveSMS/ReceiveSMSPS?who=919980524000&datetime=09%2F29%2F16+02%3A47%3A30+AM&what=Test2&operator=operator&circle=circle";
    			  //="https://fms.lntinfotech.com/ReceiveSMS/ReceiveSMSPS?who=919980524000&datetime="+tm+"&what=Test&operator=operator&circle=circle";
//
    	//= "https://pingupi.axisbank.co.in/AxisUPIMerchantToken/SMSACTIVATION/smsactivationSDK?mobileNo=919920060192&verifyVal=AXISPAYSDKACT+gRaob2ggJgXkCY7Xeklukc2WiQoEN3xkdoHdJm4lT9Su443470&verifyType=SMSACT";
    	="http://125.21.191.11:83/SmsDeliveryReportGet.aspx?";
    	
    	
    	  HTTPClientFactory.initUtilityConfigurations() ;

    	  HashMap<String, String> param = new HashMap<String, String>() ;
    	  /*param.put("sid", "6010251142070791301");
    	  param.put("dest", "919999371632");
    	  param.put("stime", "2016-10-25 11:42:07");
    	  param.put("reason", "DELIVRD");
    	  param.put("status", "001");
    	  param.put("dtime", "2016-10-25 11:43:35");
    	 */
    	  //headermap.put("content_type", "application/json") ;
    	  //headermap.put("charset", "ISO-8859-1");
    	  headermap.put(CONST_charset,
  				CONST_charset_utf);
    	  headermap.put(CONST_charset,
  				CONST_charset_utf);
    	
    	  
    	  
    	//  for(int i = 0; i< 3 ; i++)
    	  //{
     // HTTPResult res =  http_Req.doPostRequest("http://192.168.1.240/USSD360/ussd360.html", headermap, "{'sender':'test_sender3','keyword':'test_keyw'"+i+ ",'campaign_id':15}");
   //   excutePost("http://192.168.1.240/USSD360/ussd360.html","{'sender':'test_sender3','keyword':'test_ke'"+i+ ",'campaign_id':15}");
      
    	  System.out.println("Calling URL "+url);
    //	  HTTPResult res =  http_Req.doGet(url, headermap,param);
      System.out.println(res.status); 
     System.out.println(res.result);
    	  
    	  
    }
    
    
    private static void hitresponsys() {
		// TODO Auto-generated method stub
    
    	HashMap<String,String> headermap = new HashMap<String,String>();
//		String surl=	"https://bhoot:Kc8Lwr2egHLNPSmC92FJcJDCtkGGwePkZNcYgWjgcfgi4638@connekt-sandbox.flipkart.net/v1/sms/callbacks/flipkart/unicel?";
		String body = "message_id=18100931553402575&inbound_address=918424056226&device_address=919611699292&message_orig=SRKARIX&carrier=Vodafone";
		String baseURL = "https://sms.qa1.responsys.net/smslistener/sl/v1/mo/oracle/velti/dc2";
		
		HTTPRequestUtility req = new HTTPRequestUtility();
		//headermap.put("content-type", "x-www-form-urlencoded");
		headermap.put("content-type", "text/plain; charset=UTF-8");
		headermap.put("charset", "UTF-8");	
		
		System.out.println("Hitting URL :"+  baseURL);
		System.out.println("Body  :"+  body);

		HTTPResult res = req.doPostRequest(baseURL, headermap, ticketToMap(body));
		
		System.out.println("Result Posting BodyParams  Is :"+ res.status +":"+res.result);

		 // res = req.doPostRequest(baseURL, headermap,  body);
		
		//System.out.println("Result Posting Body Is :"+ res.status +":"+res.result);
		
		 headermap.remove("content-type");
		headermap.put("charset", "UTF-8");	
		
		  res = req.doPostRequest(baseURL, headermap, ticketToMap(body));
		
		//System.out.println("11Result Posting BodyParams  Is :"+ res.status +":"+res.result);

		//  res = req.doPostRequest(baseURL, headermap,  body);
		
		System.out.println("12Result Posting Body Is :"+ res.status +":"+res.result);
		
		 
	}
    
    
	
	public static String encode(String val)
	{
		try{
			return URLEncoder.encode(val,"UTF-8");
			}catch (Exception e) {
				// TODO: handle exception
			}
		
		return "";
			
	}
	
	public static String decode(String val)
	{
		try{
			return URLDecoder.decode(val,"UTF-8");
			}catch (Exception e) {
				// TODO: handle exception
			}
		
		return "";
			
	}
	
    
    public static  HashMap<String,String> ticketToMap(String ticket)
	{
		HashMap<String,String> map = new HashMap<String,String>();
		
		if(ticket == null)
			return map;
		
		String tockens[] = ticket.split("&");
		if(tockens != null && tockens.length > 0)
		{
			for(int i = 0; i< tockens.length ; i++)
			{
				String subtockens[] = tockens[i].split("=");
				
				try{
				String key = decode(subtockens[0]);
				if(subtockens.length == 1)
				{
					String value ="";
					map.put(key, value);
				}
				else
				{
				String value = decode(subtockens[1]);
				map.put(key, value);
				}
				
				}catch(Exception e){}
				
			}
		}
		else
		{
			String subtockens[] = ticket.split("=");
			try{
			String key = decode(subtockens[0]);
			if(subtockens.length == 1)
			{
				String value ="";
				map.put(key, value);
			}
			else
			{
			String value = decode(subtockens[1]);
			map.put(key, value);
			}
			}
			catch(Exception e){}
			
		}
		
		return map;
		

	}
    
    
    


	public static String excutePost(String targetURL, String urlParameters)
    {
      URL url;
      HttpURLConnection connection = null;  
      try {
        //Create connection
        url = new URL(targetURL);
        connection = (HttpURLConnection)url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", 
             "application/x-www-form-urlencoded");
  			
        connection.setRequestProperty("Content-Length", "" + 
                 Integer.toString(urlParameters.getBytes().length));
        connection.setRequestProperty("Content-Language", "en-US");  
  			
        connection.setUseCaches (false);
        connection.setDoInput(true);
        connection.setDoOutput(true);

        //Send request
        DataOutputStream wr = new DataOutputStream (
                    connection.getOutputStream ());
        wr.writeBytes (urlParameters);
        wr.flush ();
        wr.close ();

        //GET RESPONSE	
        InputStream is = connection.getInputStream();
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));
        String line;
        StringBuffer response = new StringBuffer(); 
        while((line = rd.readLine()) != null) {
          response.append(line);
          response.append('\r');
        }
        rd.close();
        return response.toString();

      } catch (Exception e) {

        e.printStackTrace();
        return null;

      } finally {

        if(connection != null) {
          connection.disconnect(); 
        }
      }
    }
    
}
