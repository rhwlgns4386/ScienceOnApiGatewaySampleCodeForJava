package Search;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class SearchEducation {

    public static final  String target="KACADEMY";
    public static final String clientID="Your ClientId";
    private static final String accessToken="Your AccessToken";

    public static void main(String[] args){

        /**
         * 검색어를 인코딩 합니다.
         * 검색어 필드에 관한것은 ScienceOn Api GateWay를 참고 해주세요.
         */
        String query= URLEncoder.encode("{\"KW\":\"무료\"}");

        /** 검색할 쿼리를 입력하여 교육 검색 api에 request를 요청하고 response를 받는다. */
        String response= SearchEducation.getSearchResults(query);

        System.out.println(response);
    }

    /**
     * @brief 각각의 검색 api를 사용하기 위한 함수
     * @return String:요청을 받은 xml값
     * @param query:url인코딩된 검색 데이터 입력
     */
    public static String getSearchResults(String query){
        String target_URL="https://apigateway.kisti.re.kr/openapicall.do?" +
                "client_id=" +clientID+
                "&token=" +accessToken+
                "&version=1.0" +
                "&action=search" +
                "&target=" +target+
                "&searchQuery="+query;

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