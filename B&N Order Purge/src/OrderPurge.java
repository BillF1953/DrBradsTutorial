import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import org.json.JSONArray;

/*********
 * 
 * @author Kevin
 * To Do: Create way to pass in user/password and start integer via command line
 * Git Version
 */

public class OrderPurge {
	
	// need properties for report ID, service URL, user, password
	
	public static void main(String[] args) throws UnirestException {

		// call the following in a loop increasing by increments of 10K (the
		// report limit)
		JSONArray rows = getReportRows(73681, 73700); // start at 73681

		for (int i = 0; i < rows.length(); i++) {
			JSONArray row = rows.getJSONArray(i);

			Integer ID = Integer.valueOf((String) row.get(0));
			String CreatedDate = (String) row.get(1);
			String UpdatedDate = (String) row.get(2);
			Double DaysOld = Double.valueOf((String) row.get(3));
			Double DaysSinceUpdate = Double.valueOf((String) row.get(4));
			Integer IncidentCount = Integer.valueOf((String) row.get(5));
			
			if (DaysOld > -61) {
				// System.out.println(DaysOld);
				System.exit(0);
			}
			
			if (DaysSinceUpdate < -60 && IncidentCount < 1) {
				System.out.println("Deleting order " + ID);
				// System.out.println(DaysSinceUpdate);
				// deleteOrder(ID); // uncomment to do a delete
			} else {
				System.out.println(rows.get(i));
			}
		}

	}

	private static JSONArray getReportRows(int start, int end) throws UnirestException {

		HttpResponse<JsonNode> response = Unirest
				.post("https://barnesandnoble--tst2.custhelp.com/services/rest/connect/v1.3/analyticsReportResults")
				.header("accept", "application/json")
				.header("authorization", "Basic *************************")
				.header("cache-control", "no-cache")
				.body("{\r\n    " 
						+ "\"id\": 102134,\r\n        " 
						+ "\"filters\":\r\n            "
						+ "[\r\n                " 
						+ "{\r\n                    " 
						+ "\"name\": \"Order ID List\","
						+ "\r\n                    \"values\": [" 
						+ "\r\n                            \"" + start + "\","
						+ "\r\n                            \"" + end + "\"" 
						+ "\r\n                    ]"
						+ "\r\n                }" 
						+ "\r\n            ]" 
						+ "\r\n" 
						+ "}\r\n")
				.asJson();

		JSONArray rows = response.getBody().getObject().getJSONArray("rows");
		
		System.out.println("Headers\n------------------\n" +
		response.getHeaders());
		System.out.println("\n\nBody\n------------------\n" +
		response.getBody());

		return rows;
	}

	private static Boolean deleteOrder(int orderid) throws UnirestException {

		HttpResponse<String> response = Unirest
				.delete("https://barnesandnoble--tst2.custhelp.com/services/rest/connect/v1.3/ExtOrders.Orders/"+orderid+"")
				.header("accept", "application/json")
				.header("authorization", "Basic *****************")
				.header("cache-control", "no-cache").header("postman-token", "fdf346d1-fdb8-6ab2-a2aa-398f9bb9da68")
				.asString();
		if (response.getStatus() == 200) {
			return true;
		} else {
			return false;
		}
	}
}