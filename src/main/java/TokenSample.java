
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;



public class TokenSample {

    /**============================ 사용자 정보 입력 ==========================================*/
    //맥주소 입력해주세요.
    public static final String MAC_address = "Your Mac Address";
    //발급받은 client_id를 입력해주세요.
    public static final String clientID = "Your clientId";
    //API Gateway로부터 발급받은 인증키를 입력해주세요.
    public static final String key = "Your key";
    //refresh Token을 입력해주세요.(Access Token 재발급시 입력)
    private static String refreshToken = "Your RefreshToken";
    //accessToken을 입력해주세요(데이터요청시 이용)
    private static String accessToken = "Your AccessToken";
    /**====================================== ==========================================*/
    //iv의 값은 변하지 않습니다.
    public static final String iv = "jvHJ1EFA0IXBrxxz";

    public static void main(String[] args) throws Exception {

        /** AES256암호화를 위해 iv값으로 각각의 키와 iv값으로 암호화를 한다.*/
        AES256Util aes256Util=new AES256Util(){

            public String encrypt(String ciphertext,String passphrase) throws Exception {
                IvParameterSpec ivSpec = new IvParameterSpec(iv.getBytes());
                SecretKeySpec skeySpec = new SecretKeySpec(passphrase.getBytes("UTF-8"), "AES");
                Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
                cipher.init(Cipher.ENCRYPT_MODE, skeySpec, ivSpec);
                byte[] encrypted_bytes = cipher.doFinal(ciphertext.getBytes());
                String encrypted_str  = Base64.getUrlEncoder().encodeToString(encrypted_bytes);
                return encrypted_str;
            }
        };

        /**
         * 최초 token요청을 합니다.
         * RefreshToken과 AccessToken을 발급 받습니다.
         */
        String tokenResponse=TokenSample.createToken(aes256Util);
        System.out.println(tokenResponse);

        /** 데이터를 요청하고 그에 맞는 데이터를 받습니다.
         * 하단의 코드는 예제코드이며 자세한 내용은 ScienceOn ApiGateWay를 참조해주세요.
         */
        String query=URLEncoder.encode("{\"BI\":\"코로나\"}");
        String target_URL="https://apigateway.kisti.re.kr/openapicall.do?" +
                "client_id=" +clientID+
                "&token=" +accessToken+
                "&version=1.0" +
                "&action=search" +
                "&target=ARTI" +
                "&searchQuery="+query;

        String response=getResponse(target_URL);
        System.out.println(response);


        /** 에러를 검출하기위해 xml데이터의 statusCode의 값을 추출 */
        DocumentBuilderFactory factory=DocumentBuilderFactory.newInstance();
        DocumentBuilder builder=factory.newDocumentBuilder();
        Document document=builder.parse(new InputSource(new StringReader(response)));
        NodeList nodelist=document.getElementsByTagName("statusCode");
        Node textNode=nodelist.item(0).getChildNodes().item(0);

        /**
         * AccessToken만료시 AccessToken를 재발급합니다.
         * AccessToken요청시 E4106번이 떨어지면 RefreshToken 만료된 것 입니다.
         */
        if(textNode.getNodeValue().equals("401")){
            if(response.contains("E4103")){
                System.out.println("AccessToken이 만료 되었습니다.");
                tokenResponse=TokenSample.getAccessToken();
                if(tokenResponse.contains("E4106")){
                    System.out.println("RefreshToken이 만료 되었습니다.");
                    TokenSample.createToken(aes256Util);
                }
            }
        }
    }

    /**
     * 1) 최초 토큰발급 요청인 경우, RefreshToken 값이 만료(2주 기한)되어 신규로 AccessToken, RefreshToken 둘 다 전체 토큰발급이 필요한 경우
     * 2) API Gateway 신청시 제출한 맥주소 값, 발급받은 클라이어트 ID 값이 필요함
     * 3) 정상적으로 토큰발급이 완료되면, AccessToken, RefreshToken 값을 저장한 이 후에 이 값을 사용해야 함
     */

    public static String createToken(AES256Util aes256Util) throws Exception{
        Date date_now = new Date(System.currentTimeMillis());
        SimpleDateFormat fourteen_format = new SimpleDateFormat("yyyyMMddHHmmss");
        String time = fourteen_format.format(date_now);

        String encrypted_txt = "";

        JSONObject plain_txt = new JSONObject();
        plain_txt.put("mac_address", MAC_address);
        plain_txt.put("datetime", time);

        try {
            encrypted_txt = URLEncoder.encode(aes256Util.encrypt(plain_txt.toString(),key),"UTF-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }

        String target_URL = "https://apigateway.kisti.re.kr/tokenrequest.do?accounts="+encrypted_txt+"&client_id="+clientID;
        String response = getResponse(target_URL);
        JSONObject jsonObject = new JSONObject(response);
        refreshToken=jsonObject.get("refresh_token").toString();
        accessToken=jsonObject.get("access_token").toString();
        return jsonObject.toString();

    }

    /**
     * 1) AccessToken 값이 만료(2시간 기한)되어 신규로 AccessToken 발급이 필요한 경우
     * 2) 최초 토큰발급 또는 전체 토큰발급 받을 때 받은 RefreshToken 값을 가지고 URL 호출함
     * 3) 정상적으로 토큰발급이 완료되면, AccessToken 값을 저장한 이 후에 이 값을 사용해야 함
     * 4) 토큰발급이 안되면 신규로 전체 토큰발급 진행을 해야 함
     */
    public static String getAccessToken(){
        String target_URL = "https://apigateway.kisti.re.kr/tokenrequest.do?client_id="+clientID+"&refreshToken="+refreshToken;
        String response = getResponse(target_URL);

        JSONObject jsonObject = new JSONObject(response);
        if(!jsonObject.has("errorCode")){
            accessToken=jsonObject.get("access_token").toString();
        }
        return jsonObject.toString();
    }

    /**
     * @brief 서버로 request요청을 보내고 그에 맞는 response를 받는
     * @return String:요청을 받은 xml or json값
     * @param target_URL:요청을 보낼 url
     */
    private static String getResponse(String target_URL) {
        try {
            URL url = new URL(target_URL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET"); // optional default is GET
            int responseCode=con.getResponseCode();
            BufferedReader in;
            if(responseCode!=HttpURLConnection.HTTP_OK){
                in = new BufferedReader(new InputStreamReader(con.getErrorStream()));
            }
            else{
                in= new BufferedReader(new InputStreamReader(con.getInputStream()));
            }
            String inputLine;
            StringBuffer response= new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }

            in.close();
            return  response.toString();
        } catch (IOException e) {
            return e.getMessage();
        }
    }
}