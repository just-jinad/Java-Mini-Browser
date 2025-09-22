
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.swing.JEditorPane;


public class BrowserLogic {
    // JEditorPane contentPane;

    // public BrowserLogic(JEditorPane contentPane){
    //     this.contentPane = contentPane;
    // }

        
       public void loadPage(JEditorPane contentPane,  String userUrl) {
        try {
            if (!userUrl.startsWith("https://") && !userUrl.startsWith("http://")) {
                userUrl = "https://" + userUrl;
            }

            // statusLabel.setText("Loading...");

            // Check if URL is reachable before loading
            int responseCode = checkURLStatus(userUrl);
            if (responseCode == 200) {
                contentPane.setPage(userUrl);
                // addToHistory(userUrl);
                // statusLabel.setText("Loaded Successfully");
            } else {
                contentPane.setText("<h3>Error: Unable to load page (HTTP " + responseCode + ")</h3>");
                // statusLabel.setText("Error: " + responseCode);
            }
        } catch (IOException ex) {
            contentPane.setText("<h3>Error loading page: " + ex.getMessage() + "</h3>");
            // statusLabel.setText("Failed to Load");
        }
    }

    private int checkURLStatus(String urlString) throws IOException {
        try {
            URL userUrl = new URL(urlString);
            System.out.println("this is from the logic ");
            HttpURLConnection connection = (HttpURLConnection) userUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000); // 5 seconds timeout
            connection.setReadTimeout(5000);
            return connection.getResponseCode();
        } catch (IOException e) {
            return -1; // Failed to connect
        }
    }



}
