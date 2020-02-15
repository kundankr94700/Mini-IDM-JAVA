import chrriis.dj.nativeswing.swtimpl.NativeInterface;
import chrriis.dj.nativeswing.swtimpl.components.JWebBrowser;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.text.DefaultEditorKit;
import java.awt.*;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;

public class Downloader_Application{
    public static void main(String[] args)

    {
        C ob=new C();

    }}

class C extends JFrame implements Observer
{
    JButton b1,b3,pauseButton, resumeButton,cancelButton, clearButton,addButton,closeButton,backButton;
    JLabel l2;
    JTable table;
    JTextField t1;
    DownloadsTableModel tableModel;
    JPanel wbPanel;
    Download selectedDownload;
    boolean clearing;
    JPanel buttonsPanel,addPanel,downloadsPanel;
    JWebBrowser wb;
    public C ()
    {
        System.out.println("hell");
        NativeInterface.open();
        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run() {

                addButton  = new JButton("Download",new ImageIcon("C:\\Users\\Kundan Kumar\\IdeaProjects\\JAVA Part 2\\Downloader\\download.png"));
                tableModel = new DownloadsTableModel();
                table = new JTable(tableModel);
                pauseButton = new JButton("Pause",new ImageIcon("C:\\Users\\Kundan Kumar\\IdeaProjects\\JAVA Part 2\\Downloader\\pause.png"));
                resumeButton = new JButton("Resume",new ImageIcon("C:\\Users\\Kundan Kumar\\IdeaProjects\\JAVA Part 2\\Downloader\\resume.png"));
                clearButton = new JButton("Clear",new ImageIcon("C:\\Users\\Kundan Kumar\\IdeaProjects\\JAVA Part 2\\Downloader\\delete.png"));
                cancelButton = new JButton("Cancel",new ImageIcon("C:\\Users\\Kundan Kumar\\IdeaProjects\\JAVA Part 2\\Downloader\\cancel.png"));
                closeButton = new JButton("Close",new ImageIcon("C:\\Users\\Kundan Kumar\\IdeaProjects\\JAVA Part 2\\Downloader\\delete.png"));
                backButton = new JButton("Progress",new ImageIcon("C:\\Users\\Kundan Kumar\\IdeaProjects\\JAVA Part 2\\Downloader\\download.png"));
                buttonsPanel = new JPanel();
                downloadsPanel = new JPanel();
                b1=new JButton("Video Download",new ImageIcon("C:\\Users\\Kundan Kumar\\IdeaProjects\\JAVA Part 2\\Downloader\\youtube.png"));
                b3=new JButton("URL Download",new ImageIcon("C:\\Users\\Kundan Kumar\\IdeaProjects\\JAVA Part 2\\Downloader\\download.png"));
                t1 =new JTextField(100);

                table.getSelectionModel().addListSelectionListener((ListSelectionEvent e) ->{
                    if (selectedDownload != null)
                        selectedDownload.deleteObserver(C.this);

                    if (!clearing) {
                        selectedDownload =
                                tableModel.getDownload(table.getSelectedRow());
                        selectedDownload.addObserver(C.this);
                        updateButtons();
                    }
                });

                table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

                addButton.addActionListener((e)-> {
                    t1.paste();
                    URL verifiedUrl = verifyUrl(t1.getText());
                    if (verifiedUrl != null) {

                        JFrame f1=new JFrame();
                        f1.add(downloadsPanel);
                        f1.setSize(500,600);
                        f1.setVisible(true);
                        f1.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                        tableModel.addDownload(new Download(verifiedUrl));
                        t1.setText("");
                    } else {
                        JOptionPane.showMessageDialog(C.this,
                                "Invalid Download URL", "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }  });

                clearButton.addActionListener((e) ->{
                    clearing = true;
                    tableModel.clearDownload(table.getSelectedRow());
                    clearing = false;
                    selectedDownload = null;
                    updateButtons();        });

                cancelButton.addActionListener((e) ->{selectedDownload.cancel();        updateButtons();  });

                pauseButton.addActionListener((e)-> { selectedDownload.pause();         updateButtons();  });

                resumeButton.addActionListener((e)-> {selectedDownload.resume();        updateButtons();  });

                closeButton.addActionListener((e)-> {dispose();  });

                backButton.addActionListener((e)-> {
                    JFrame f1=new JFrame();
                    f1.add(downloadsPanel);
                    f1.setSize(500,600);
                    f1.setVisible(true);
                    f1.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                });

                ProgressRenderer renderer = new ProgressRenderer(0, 100);
                renderer.setStringPainted(true);
                table.setDefaultRenderer(JProgressBar.class, renderer);
                table.setRowHeight((int) renderer.getPreferredSize().getHeight());

                downloadsPanel.setBorder(BorderFactory.createTitledBorder("Downloads"));
                downloadsPanel.setLayout(new BorderLayout());
                downloadsPanel.add(new JScrollPane(table),BorderLayout.CENTER);

                add(addButton);
                add(pauseButton);
                add(resumeButton);
                add(cancelButton);
                add(clearButton);
                add(closeButton);
                add(backButton);


                pauseButton.setEnabled(false);
                clearButton.setEnabled(false);
                cancelButton.setEnabled(false);
                resumeButton.setEnabled(false);

                pauseButton.setBounds(820,140,130,30);
                clearButton.setBounds(820,180,130,30);
                cancelButton.setBounds(820,220,130,30);
                resumeButton.setBounds(820,260,130,30);
                backButton.setBounds(820,300,130,30);
                closeButton.setBounds(820,340,130,30);
                addButton.setBounds(820,90,130,40);

                add(buttonsPanel);
                setTitle("Download Manager");
                setSize(640, 480);
                setResizable(false);
                setLocation(new Point(500,100));
                setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);




                setTitle("Mini Internet Download Manager                                                                                                         @Kundan Kumar");

                t1.setBounds(70,420,340,30);
                b1.setBounds(420,420,180,30);
                b3.setBounds(630,420,170,30);


                l2=new JLabel("URL");
                l2.setBounds(30,410,270,40);
                b1.addActionListener((e)->
                {   t1.paste();
                    URL verifiedUrl = verifyUrl(t1.getText());
                    if (verifiedUrl != null) {
                        new Thread(new V1_Download(verifiedUrl)).start();
                        t1.setText("");
                    } else {
                        JOptionPane.showMessageDialog(new JFrame(),"Invalid Download URL", "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }  });

                b3.addActionListener((e)->
                {   t1.paste();
                    URL verifiedUrl = verifyUrl(t1.getText());
                    if (verifiedUrl != null) {
                        new Thread(new V1_Download(verifiedUrl)).start();
                        wb.navigate(verifiedUrl.toString());

                        t1.setText("");
                    } else {
                        JOptionPane.showMessageDialog(new JFrame(),"Invalid Download URL", "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }  });
                add(b1);
                add(l2);
                add(t1);
                add(b3);
                setLayout(null);
                setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                setVisible(true);
                setSize(1000,500);
                setResizable(false);
                setLocation(new Point(500,10));
                wbPanel=new JPanel(new BorderLayout());
                wb=new JWebBrowser();
                wbPanel.add(wb,BorderLayout.CENTER);
                wb.setBarsVisible(false);
                wb.navigate("https://www.google.com/");
                add(wbPanel);
                wbPanel.setBounds(10,10,1000,500);

                JPopupMenu menu = new JPopupMenu();
                Action cut = new DefaultEditorKit.CutAction();
                cut.putValue(Action.NAME, "Cut");
                cut.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control X"));
                menu.add( cut );

                Action copy = new DefaultEditorKit.CopyAction();
                copy.putValue(Action.NAME, "Copy");
                copy.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control C"));

                menu.add( copy );

                Action paste = new DefaultEditorKit.PasteAction();
                paste.putValue(Action.NAME, "Paste");
                paste.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control V"));
                menu.add( paste );
                t1.setComponentPopupMenu(menu);

            }
        });
        NativeInterface.runEventPump();
        Runtime.getRuntime().addShutdownHook(new Thread((new Runnable() {
            @Override
            public void run() {
                NativeInterface.close();
            }
        })));



    }
    private URL verifyUrl(String url) {
        if (!url.toLowerCase().startsWith("https://")  )
            return null;
        URL verifiedUrl = null;
        try {
            verifiedUrl = new URL(url);
        } catch (Exception e) {
            return null;
        }
        if (verifiedUrl.getFile().length() < 2)
            return null;

        return verifiedUrl;
    }
    private void updateButtons() {
        if (selectedDownload != null) {
            int status = selectedDownload.getStatus();
            switch (status) {
                case Download.DOWNLOADING:
                    pauseButton.setEnabled(true);
                    resumeButton.setEnabled(false);
                    cancelButton.setEnabled(true);
                    clearButton.setEnabled(false);
                    break;
                case Download.PAUSED:
                    pauseButton.setEnabled(false);
                    resumeButton.setEnabled(true);
                    cancelButton.setEnabled(true);
                    clearButton.setEnabled(false);
                    break;
                case Download.ERROR:
                    pauseButton.setEnabled(false);
                    resumeButton.setEnabled(true);
                    cancelButton.setEnabled(false);
                    clearButton.setEnabled(true);
                    break;
                default: // COMPLETE or CANCELLED
                    pauseButton.setEnabled(false);
                    resumeButton.setEnabled(false);
                    cancelButton.setEnabled(false);
                    clearButton.setEnabled(true);
            }
        } else {

            pauseButton.setEnabled(false);
            resumeButton.setEnabled(false);
            cancelButton.setEnabled(false);
            clearButton.setEnabled(false);
        }
    }


    public void update(Observable o, Object arg) {
        if (selectedDownload != null && selectedDownload.equals(o))
            updateButtons();
    }

}

class V1_Download  implements Runnable {

    URL url;
    public  V1_Download(URL url)

    {
        this.url=url;
        System.out.println(this.url);
    }

    @Override
    public void run() {
        String url=this.url.toString();
        String download_path="D:\\youtube";

        String[] command =
                {
                        "cmd",
                };
        Process p;
        try {
            p = Runtime.getRuntime().exec(command);
            new Thread(new video21(p.getErrorStream(), System.err)).start();
            new Thread(new video21(p.getInputStream(), System.out)).start();
            PrintWriter stdin = new PrintWriter(p.getOutputStream());
            System.out.println(p.getOutputStream());
            stdin.println("cd \""+download_path+"\"");
            stdin.println(download_path+"\\youtube-dl "+url+ " --write-all-thumbnails");
            stdin.close();
            p.waitFor();
            JFrame f1=new JFrame();
            JLabel l1=new JLabel("Download Complete");
            f1.add(l1);
            f1.setDefaultCloseOperation(f1.DISPOSE_ON_CLOSE);
            f1.setVisible(true);
            f1.setSize(120,50);
            l1.setBounds(40,10,40,20);
            l1.setLayout(null);
            f1.setResizable(false);
            f1.setLocation(new Point(900,500));

        } catch (Exception EE) {
            EE.printStackTrace();
        }


    }
}

class video21 implements Runnable
{
    public video21(InputStream istrm, OutputStream ostrm) {
        istrm_ = istrm;
        ostrm_ = ostrm;
    }
    public void run() {
        try
        {
            final byte[] buffer = new byte[1024];
            for (int length = 0; (length = istrm_.read(buffer)) != -1; )
            {
                ostrm_.write(buffer, 0, length);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    private final OutputStream ostrm_;
    private final InputStream istrm_;
}
