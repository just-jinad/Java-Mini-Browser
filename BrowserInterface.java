import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

public class BrowserInterface extends JFrame {

    protected JButton forwardBtn, backBtn, goBtn, reloadBtn, homeBtn;
    protected JEditorPane contentPane;
    protected JTextField addressBar;
    protected JScrollPane scrollPane;
    protected JMenuBar menuBar;
    protected JLabel statusLabel;
    protected JMenu bMenu;
    String userUrl, userURL;
    BrowserLogic lg = new BrowserLogic();
    ArrayList<String> history = new ArrayList<>();
    protected int currentIndex = -1;
    String homePage = "https://example.com";

    public BrowserInterface() {
        uiInit();
        layoutSetup();
        setTitle("Web Browser");
        setSize(900, 900);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        setLocationRelativeTo(null);
    }

    public void uiInit() {
        System.out.println("this is when the page loads: " + history.size());
        backBtn = new JButton("⬅️");
        forwardBtn = new JButton("➡️");
        goBtn = new JButton("Go");
        reloadBtn = new JButton("🔄️");
        homeBtn = new JButton("🏠");
        statusLabel = new JLabel("Ready");

        addressBar = new JTextField(80);

        contentPane = new JEditorPane();
        contentPane.setContentType("text/html");
        contentPane.setEditable(false);

        scrollPane = new JScrollPane(contentPane);

        menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        forwardBtn.setEnabled(false);
        backBtn.setEnabled(false);
        homeBtn.setToolTipText("home");
        reloadBtn.setToolTipText("refresh");
        goBtn.setToolTipText("go");

        // Search key logics
        goBtn.addActionListener(e -> {
            System.out.println("I dy work");
            userUrl = addressBar.getText();
            lg.loadPage(contentPane, userUrl, statusLabel);
            System.out.println(userUrl);
        });
        // The ActionListener interface in Java is designed to handle action events
        // generically.
        addressBar.addActionListener(e -> {
            String userURL = addressBar.getText();

            lg.loadPage(contentPane, userURL, statusLabel);
            if (statusLabel.getText().equals("Page Loaded!")) {
                history.add(userURL);
                currentIndex = history.size() - 1;
                navButton();
            }
        });

        backLogic();
        homePage();
        forwardLogic();

    }

    public void homePage() {
        addressBar.setText(homePage);
        lg.loadPage(contentPane, homePage, statusLabel);
        history.add(homePage);
        System.out.println("This was consoled from the homepage: " + history.size());
        currentIndex = history.size() - 1;
        System.out.println("this is the index from the homepage: " + currentIndex);

    }

    public void backLogic() {
        backBtn.addActionListener(e -> {
            if (currentIndex > 0) {
                currentIndex--;
                String backward = history.get(currentIndex);
                lg.loadPage(contentPane, backward, statusLabel);
                addressBar.setText(backward);
                navButton();
            } else if (history.size() == 0) {
                backBtn.setEnabled(false);
            }
        });
    }

    public void forwardLogic() {
        forwardBtn.addActionListener(e -> {
            if (currentIndex < history.size() - 1) {
                currentIndex++;
                String forward = history.get(currentIndex);
                lg.loadPage(contentPane, forward, statusLabel);
                addressBar.setText(forward);
                navButton();
            }
        });
    }

    public void navButton() {
        if (currentIndex > 0) {
            backBtn.setEnabled(true);
        } else {
            backBtn.setEnabled(false);
        }

        forwardBtn.setEnabled(currentIndex < history.size() - 1);
    }

    public void layoutSetup() {
        setLayout(new BorderLayout());

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.add(backBtn);
        panel.add(forwardBtn);
        panel.add(reloadBtn);
        panel.add(homeBtn);
        panel.add(new JLabel("URL"));
        panel.add(addressBar);
        panel.add(goBtn);

        add(panel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);

    }

    public static void main(String[] args) {
        new BrowserInterface();
    }
}