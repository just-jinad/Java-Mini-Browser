
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.swing.JEditorPane;
import javax.swing.JLabel;

public class BrowserLogic {

    public void loadPage(JEditorPane contentPane, String userUrl, JLabel statusLabel) {
        try {
            if (!userUrl.startsWith("https://") && !userUrl.startsWith("http://")) {
                userUrl = "https://" + userUrl;
            }

            int responseCode = checkURLStatus(userUrl);
            if (responseCode == 200) {
                contentPane.setPage(userUrl);
                statusLabel.setText("Page Loaded!");
            } else {
                contentPane.setText("<h3>Error: Unable to load page (HTTP " + responseCode + ")</h3>");
                statusLabel.setText("Unable to Load page...");
            }
        } catch (IOException ex) {
            contentPane.setText("<h3>Error loading page: " + ex.getMessage() + "</h3>");
            statusLabel.setText("An error occurred: " + ex.getMessage());
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
