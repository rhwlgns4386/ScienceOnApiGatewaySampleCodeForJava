package Search;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SearchDDC {

    private static final String target="DDC";
    private static final String clientID="Your ClientId";
    private static final String accessToken="Your AccessToken";

    public static void main(String[] arg){

        /** 검색할 주제어를 입력하여 주제분류 api에 request를 요청하고 response를 받는다. */
        String response=SearchDDC.getSearchResults("science");

        System.out.println(response);
    }
    /**
     * @brief 주제어 api를 사용하기위한 함수
     * @return String:요청을 받은 xml값
     * @param subject:주제어
     */
    public static String getSearchResults(String subject){
        String target_URL="https://apigateway.kisti.re.kr/openapicall.do?" +
                "client_id=" +clientID+
                "&token=" +accessToken+
                "&version=1.0" +
                "&action=search" +
                "&target=" +target+
                "&subject="+subject;

        String response=getResponse(target_URL);
        return response;
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