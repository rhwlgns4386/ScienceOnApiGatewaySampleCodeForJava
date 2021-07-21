
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;



public class ThesisTOC {

    private static final String target="VOLUME";
    private static final String clientID="Your ClientId";
    private static final String accessToken="Your AccessToken";

    public static void main(String[] arg) throws Exception{

        String cn="NJOU00023797";
        String volno="3";

        /** cn과 volno를 입력받아 kisti의 권호정보를 반환 받습니다. */
        String response=ThesisTOC.getTOC(cn,volno);

        System.out.println(response);
    }

    /**
     * @brief 권호 TOC API를 사용하기 위한 함수
     * @return String:요청을 받은 xml값
     * @param cn:선택한 저널CN를 입력
     * @param volno:선택한 권호번호를 입력
     */
    private static String getTOC(String cn,String volno) {

        String target_URL="https://apigateway.kisti.re.kr/openapicall.do?" +
                "client_id=" +clientID+
                "&token="+accessToken+
                "&version=1.0" +
                "&action=toc" +
                "&target=" +target+
                "&cn="+cn+
                "&volno="+volno;


        /** api요청을 보냅니다. */
        String response = getResponse(target_URL);

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