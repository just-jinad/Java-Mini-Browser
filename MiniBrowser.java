// MiniBrowser.java - Main class
import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Stack;

public class MiniBrowser extends JFrame implements ActionListener {
    
    // GUI Components
    private JTextField addressBar;
    private JButton backButton, forwardButton, goButton, homeButton, bookmarkButton;
    private JEditorPane contentPane;
    private JScrollPane scrollPane;
    private JLabel statusLabel;
    private JMenuBar menuBar;
    private JMenu bookmarkMenu;
    
    // Navigation Management
    private Stack<String> history;
    private int historyIndex;
    private ArrayList<String> bookmarks;
    
    // Constants
    private static final String HOME_URL = "https://example.com";
    private static final String BOOKMARKS_FILE = "bookmarks.txt";
    
    public MiniBrowser() {
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        loadBookmarks();
        
        setTitle("Java Mini Browser");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Initialize navigation
        history = new Stack<>();
        historyIndex = -1;
        
        // Load home page
        navigateToURL(HOME_URL);
    }
    
    private void initializeComponents() {
        // Address bar and navigation buttons
        addressBar = new JTextField(50);
        backButton = new JButton("←");
        forwardButton = new JButton("→");
        goButton = new JButton("Go");
        homeButton = new JButton("🏠");
        bookmarkButton = new JButton("⭐");
        
        // Content display
        contentPane = new JEditorPane();
        contentPane.setContentType("text/html");
        contentPane.setEditable(false);
        scrollPane = new JScrollPane(contentPane);
        
        // Status bar
        statusLabel = new JLabel("Ready");
        statusLabel.setBorder(BorderFactory.createLoweredBevelBorder());
        
        // Menu bar
        menuBar = new JMenuBar();
        bookmarkMenu = new JMenu("Bookmarks");
        menuBar.add(bookmarkMenu);
        setJMenuBar(menuBar);
        
        // Initialize data structures
        bookmarks = new ArrayList<>();
        
        // Set button properties
        backButton.setEnabled(false);
        forwardButton.setEnabled(false);
        backButton.setToolTipText("Go Back");
        forwardButton.setToolTipText("Go Forward");
        homeButton.setToolTipText("Home");
        bookmarkButton.setToolTipText("Add Bookmark");
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Navigation panel
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        navPanel.add(backButton);
        navPanel.add(forwardButton);
        navPanel.add(homeButton);
        navPanel.add(new JLabel("URL:"));
        navPanel.add(addressBar);
        navPanel.add(goButton);
        navPanel.add(bookmarkButton);
        
        // Add components to main frame
        add(navPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);
    }
    
    private void setupEventHandlers() {
        // Button action listeners
        backButton.addActionListener(this);
        forwardButton.addActionListener(this);
        goButton.addActionListener(this);
        homeButton.addActionListener(this);
        bookmarkButton.addActionListener(this);
        
        // Address bar enter key
        addressBar.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    navigateToURL(addressBar.getText());
                }
            }
        });
        
        // Hyperlink listener for content pane
        contentPane.addHyperlinkListener(new HyperlinkListener() {
            @Override
            public void hyperlinkUpdate(HyperlinkEvent e) {
                if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    navigateToURL(e.getURL().toString());
                }
            }
        });
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        
        if (source == goButton) {
            navigateToURL(addressBar.getText());
        } else if (source == backButton) {
            goBack();
        } else if (source == forwardButton) {
            goForward();
        } else if (source == homeButton) {
            navigateToURL(HOME_URL);
        } else if (source == bookmarkButton) {
            addBookmark();
        }
    }
    
    private void navigateToURL(String  urlString) {
        if (urlString == null || urlString.trim().isEmpty()) {
            showStatus("Please enter a URL");
            return;
        }
        
        // Normalize URL
      String normalizedUrl = urlString.trim();
        if (!urlString.startsWith("http://") && !urlString.startsWith("https://")) {
            normalizedUrl = "https://" + urlString;
        }
        
        // Update address bar
        addressBar.setText(normalizedUrl);
        
        // Load content in background thread
        SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                showStatus("Loading: " + urlString);
                return fetchWebContent(urlString);
            }
            
            @Override
            protected void done() {
                try {
                    String content = get();
                    contentPane.setText(content);
                    contentPane.setCaretPosition(0);
                    
                    // Update history
                    updateHistory(urlString);
                    showStatus("Page loaded successfully");
                    
                } catch (Exception e) {
                    showError("Failed to load page: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }
    
    private String fetchWebContent(String urlString) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        
        // Set request properties
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(10000); // 10 seconds
        connection.setReadTimeout(10000);
        connection.setRequestProperty("User-Agent", 
            "Mozilla/5.0 (JavaMiniBrowser/1.0)");
        
        // Check response code
        int responseCode = connection.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new IOException("HTTP Error: " + responseCode + " " + 
                connection.getResponseMessage());
        }
        
        // Read response
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(connection.getInputStream()))) {
            
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        
        return content.toString();
    }
    
    private void updateHistory(String url) {
        // Remove any forward history if we're not at the end
        while (history.size() > historyIndex + 1) {
            history.pop();
        }
        
        // Add new URL to history
        history.push(url);
        historyIndex = history.size() - 1;
        
        // Update navigation buttons
        updateNavigationButtons();
    }
    
    private void goBack() {
        if (historyIndex > 0) {
            historyIndex--;
            String url = history.get(historyIndex);
            addressBar.setText(url);
            navigateToURLFromHistory(url);
        }
    }
    
    private void goForward() {
        if (historyIndex < history.size() - 1) {
            historyIndex++;
            String url = history.get(historyIndex);
            addressBar.setText(url);
            navigateToURLFromHistory(url);
        }
    }
    
    private void navigateToURLFromHistory(String url) {
        SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                showStatus("Loading: " + url);
                return fetchWebContent(url);
            }
            
            @Override
            protected void done() {
                try {
                    String content = get();
                    contentPane.setText(content);
                    contentPane.setCaretPosition(0);
                    updateNavigationButtons();
                    showStatus("Page loaded from history");
                } catch (Exception e) {
                    showError("Failed to load page: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }
    
    private void updateNavigationButtons() {
        backButton.setEnabled(historyIndex > 0);
        forwardButton.setEnabled(historyIndex < history.size() - 1);
    }
    
    private void addBookmark() {
        String currentURL = addressBar.getText();
        if (currentURL != null && !currentURL.trim().isEmpty() && 
            !bookmarks.contains(currentURL)) {
            
            bookmarks.add(currentURL);
            saveBookmarks();
            updateBookmarkMenu();
            showStatus("Bookmark added: " + currentURL);
        }
    }
    
    private void loadBookmarks() {
        try (BufferedReader reader = new BufferedReader(new FileReader(BOOKMARKS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                bookmarks.add(line.trim());
            }
            updateBookmarkMenu();
        } catch (FileNotFoundException e) {
            // File doesn't exist yet, that's okay
        } catch (IOException e) {
            showError("Failed to load bookmarks: " + e.getMessage());
        }
    }
    
    private void saveBookmarks() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(BOOKMARKS_FILE))) {
            for (String bookmark : bookmarks) {
                writer.println(bookmark);
            }
        } catch (IOException e) {
            showError("Failed to save bookmarks: " + e.getMessage());
        }
    }
    
    private void updateBookmarkMenu() {
        bookmarkMenu.removeAll();
        
        for (String bookmark : bookmarks) {
            JMenuItem menuItem = new JMenuItem(bookmark);
            menuItem.addActionListener(e -> navigateToURL(bookmark));
            bookmarkMenu.add(menuItem);
        }
        
        if (!bookmarks.isEmpty()) {
            bookmarkMenu.addSeparator();
        }
        
        JMenuItem clearBookmarks = new JMenuItem("Clear All Bookmarks");
        clearBookmarks.addActionListener(e -> clearAllBookmarks());
        bookmarkMenu.add(clearBookmarks);
    }
    
    private void clearAllBookmarks() {
        int result = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to clear all bookmarks?", 
            "Clear Bookmarks", 
            JOptionPane.YES_NO_OPTION);
            
        if (result == JOptionPane.YES_OPTION) {
            bookmarks.clear();
            saveBookmarks();
            updateBookmarkMenu();
            showStatus("All bookmarks cleared");
        }
    }
    
    private void showStatus(String message) {
        statusLabel.setText(message);
    }
    
    private void showError(String message) {
        statusLabel.setText("Error: " + message);
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    public static void main(String[] args) {
        // Set look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Use default look and feel
        }
        
        SwingUtilities.invokeLater(() -> {
            new MiniBrowser().setVisible(true);
        });
    }
}