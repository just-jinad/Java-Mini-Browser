import java.awt.BorderLayout;
import java.awt.FlowLayout;

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
    // protected JLabel statusLabel;
    protected JMenu bMenu;
    String userUrl;
    
    public BrowserInterface(){
        uiInit();
        layoutSetup();
        setTitle("Web Browser");
        setSize(900, 900);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        setLocationRelativeTo(null);
    }
    
    public void uiInit(){
        BrowserLogic lg = new BrowserLogic();
      forwardBtn = new JButton("⬅️");
      backBtn = new JButton("➡️");
      goBtn = new JButton("Go");
      reloadBtn = new JButton("🔄️");
      homeBtn = new JButton("🏠");

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

      goBtn.addActionListener(e->{
        System.out.println("I dy work");
        userUrl = addressBar.getText();
        lg.loadPage(contentPane, userUrl);   
        System.out.println(userUrl);
    });

    }

    public void layoutSetup(){
        setLayout(new BorderLayout());

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.add(forwardBtn);
        panel.add(backBtn);
        panel.add(new JLabel("URL"));
        panel.add(addressBar);
        panel.add(goBtn);
        panel.add(homeBtn);
        panel.add(reloadBtn);

        add(panel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        // add(statusLabel, BorderLayout.NORTH);

    }

    public static void main(String[] args){
        new BrowserInterface();
    }
}