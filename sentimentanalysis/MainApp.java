package sentimentanalysis;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.*;

/**
  Main Application for Assignment 5
  @author vangelis
  @author jelena
  @author junye
 */
public class MainApp {

    //Thread Pool for main
    private final static int threadCount = 10;
    static List<AbstractTweet> sublists;


    // Used to read from System's standard input
    //private static final Scanner CONSOLEINPUT = new Scanner(System.in);
    private static final TweetHandler HANDLER = new TweetHandler();

    //Log
    static protected final Logger log = Logger.getLogger("SentimentAnalysis");

    /**
     * Main method demonstrates how to use Stanford NLP library classifier.
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {

        final int threadCount = 10;
        final ExecutorService exService = Executors.newFixedThreadPool(threadCount);

        FileHandler fh;
        try {
            // This block configure the logger with handler and formatter
            fh = new FileHandler("SentimentAnalysis.%u.%g.log");
            log.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);

        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        log.setLevel(Level.INFO);

        // Load the database first.
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
                HANDLER.loadSerialDB();
            }
        });
    }


    //Components for the layout
    static private final JPanel topPanel = new JPanel();
    static private final JPanel bottomPanel = new JPanel();;
    static private final JLabel commandLabel = new JLabel("Please select the command",JLabel.RIGHT);
    static private final JComboBox comboBox = new JComboBox();
    static private final JButton tempListButton = new JButton("Show TempList");
    static private final JButton databaseButton = new JButton("Show Database");
    static private final JButton saveButton = new JButton("Save Database");
    //Output area. Set as global to be edit in different methods.
    static protected final JTextArea outputArea = new JTextArea();
    static private final JScrollPane outputScrollPane = new JScrollPane(outputArea);
    //width and height of the monitor
    private static int width = Toolkit.getDefaultToolkit().getScreenSize().width;
    private static int height = Toolkit.getDefaultToolkit().getScreenSize().height;
    //width and height of the window (JFrame)
    private static int windowsWidth = 800;
    private static int windowsHeight = 600;

    /**
     * Initialize the JFrame and JPanels, and show them.
     * Also set the location to the middle of the monitor.
     */
    private static void createAndShowGUI() {

        createTopPanel();
        createBottomPanel();

        topPanel.getIgnoreRepaint();
        JPanel panelContainer = new JPanel();
        panelContainer.setLayout(new GridLayout(2,0));
        panelContainer.add(topPanel);
        panelContainer.add(bottomPanel);

        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("SentimentAnalysis");

        // Save when quit.
        //frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {

            public void windowClosing(WindowEvent e) {
                log.info("Closing window.");
                outputArea.append("Closing window. Database will be saved.\n");
                super.windowClosing(e);
                log.info("Saving database.");
                HANDLER.saveSerialDB();
                log.info("System shutdown.");
                System.exit(0);
            }

        });
        panelContainer.setOpaque(true);
        frame.setBounds((width - windowsWidth) / 2,
                (height - windowsHeight) / 2, windowsWidth, windowsHeight);
        frame.setContentPane(panelContainer);

        frame.setVisible(true);


    }

    /**
     * This method initialize the top panel, which is the commands using a ComboBox
     */
    private static void createTopPanel() {
        comboBox.addItem("Please select...");
        comboBox.addItem(" 1. Load new tweet text file.");
        comboBox.addItem(" 2. Classify tweets using NLP library and report accuracy.");
        comboBox.addItem(" 3. Manually change tweet class label.");
        comboBox.addItem(" 4. Add new tweets to database.");
        comboBox.addItem(" 5. Delete tweet from database (given its id).");
        comboBox.addItem(" 6. Search tweets by user, date, flag, or a matching substring.");
        comboBox.addItem(" 0. Exit program.");
        comboBox.setSelectedIndex(0);

        comboBox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                log.info("Command chosen, Item = " + e.getItem());
                log.info("StateChange = " + e.getStateChange());
                if (e.getStateChange() == 1) {
                    if (e.getItem().equals("Please select...")) {
                        outputArea.setText("");
                        outputArea.append(HANDLER.getTweetDB().size() + " records in database.\n");
                        outputArea.append(HANDLER.getTweetBuffer().size()+" records in temp list.\n");
                        outputArea.append("Please select a command to continue.\n");
                        topPanel.removeAll();
                        topPanel.add(commandLabel);
                        topPanel.add(comboBox);
                        //Keep the comboBox at the first line.
                        topPanel.add(new JLabel());
                        topPanel.add(new JLabel());
                        topPanel.add(new JLabel());
                        topPanel.add(new JLabel());
                        topPanel.add(new JLabel());
                        topPanel.add(new JLabel());
                        topPanel.add(new JLabel());
                        topPanel.add(new JLabel());

                        topPanel.add(tempListButton);
                        topPanel.add(new JLabel());
                        topPanel.add(databaseButton);
                        topPanel.add(saveButton);
                        topPanel.updateUI();
                    } else if (e.getItem().equals(" 1. Load new tweet text file.")) {
                        loadTweets();
                    } else if (e.getItem().equals(" 2. Classify tweets using NLP library and report accuracy.")) {
                        classifyTweets();
                    } else if (e.getItem().equals(" 3. Manually change tweet class label.")) {
                        changeTweet();
                    } else if (e.getItem().equals(" 4. Add new tweets to database.")) {
                        addTweets();
                    } else if (e.getItem().equals(" 5. Delete tweet from database (given its id).")) {
                        deleteTweet();
                    } else if (e.getItem().equals(" 6. Search tweets by user, date, flag, or a matching substring.")) {
                        searchTweet();
                    } else if (e.getItem().equals(" 0. Exit program.")) {
                        exit();
                    }
                }

            }
        });

        tempListButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.info("tempList button clicked.");
                Runnable myRunnable = new Runnable() {

                    public void run() {
                        printJTable(HANDLER.getTweetBuffer());
                    }
                };

                Thread thread = new Thread(myRunnable);
                thread.start();
            }

        });

        databaseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.info("database button clicked.");
                Runnable myRunnable = new Runnable() {

                    public void run() {
                        printJTable(HANDLER.getTweetDB());
                    }
                };

                Thread thread = new Thread(myRunnable);
                thread.start();
            }

        });

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.info("Save button clicked.");
                Runnable myRunnable = new Runnable() {

                    public void run() {
                        HANDLER.saveSerialDB();
                        outputArea.append("Database saved.\n");

                    }
                };

                Thread thread = new Thread(myRunnable);
                thread.start();
            }

        });

        GridLayout topPanelGridLayout = new GridLayout(0,2,10,10);

        topPanel.setLayout(topPanelGridLayout);
        topPanel.add(commandLabel);
        topPanel.add(comboBox);
        //Keep the comboBox at the first line.
        topPanel.add(new JLabel());
        topPanel.add(new JLabel());
        topPanel.add(new JLabel());
        topPanel.add(new JLabel());
        topPanel.add(new JLabel());
        topPanel.add(new JLabel());
        topPanel.add(new JLabel());
        topPanel.add(new JLabel());

        topPanel.add(tempListButton);
        topPanel.add(new JLabel());
        topPanel.add(databaseButton);
        topPanel.add(saveButton);
        topPanel.updateUI();
    }

    /**
     * This method initialize the bottom panel, which is the output area.
     * Just a TextArea that not editable.
     */
    private static void createBottomPanel() {

        final Font fontCourier = new Font("Courier", Font.PLAIN, 18);
        DefaultCaret caret = (DefaultCaret)outputArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        outputArea.setFont(fontCourier);

        outputArea.setText("Welcome to Sentiment Analysis System.\n");
        outputArea.setEditable(false);

        final Border border = BorderFactory.createLineBorder(Color.BLACK);
        outputArea.setBorder(BorderFactory.createCompoundBorder(border,
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        bottomPanel.setBorder(BorderFactory.createCompoundBorder(border,
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        outputScrollPane.createVerticalScrollBar();
        outputScrollPane.createHorizontalScrollBar();
        bottomPanel.setLayout(new GridLayout(1,0));
        bottomPanel.add(outputScrollPane);
    }

    /**
     * Method 1: load new tweet text file.
     *
     */
    public static void loadTweets() {

        outputArea.setText("");
        outputArea.append(HANDLER.getTweetDB().size() + " records in database.\n");
        outputArea.append(HANDLER.getTweetBuffer().size()+" records in temp list.\n");
        outputArea.append("Command 1\n");
        outputArea.append("Please input the path to tweet csv file.\n");
        outputArea.append("(Example: Data\\testdata.manual.2009.06.14.csv)\n");

        topPanel.removeAll();
        topPanel.add(commandLabel);
        topPanel.add(comboBox);

        final JLabel pathLabel = new JLabel("CSV file path:",JLabel.RIGHT);
        final JTextField pathInput = new JTextField("");

        final JButton confirmButton = new JButton("Confirm");

        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.info("Confirm button clicked. (Command 1)");
                Runnable myRunnable = new Runnable() {

                    public void run() {
                        String path = pathInput.getText();
                        HANDLER.setTweetBuffer(HANDLER.loadTweetsFromText(path));
                    }
                };

                Thread thread = new Thread(myRunnable);
                thread.start();
            }

        });

        topPanel.add(pathLabel);
        topPanel.add(pathInput);
        topPanel.add(new JLabel());
        topPanel.add(new JLabel());
        topPanel.add(new JLabel());
        topPanel.add(new JLabel());
        topPanel.add(new JLabel());
        topPanel.add(new JLabel());

        topPanel.add(tempListButton);
        topPanel.add(confirmButton);
        topPanel.add(databaseButton);
        topPanel.add(saveButton);
        topPanel.updateUI();
    }

    /**
     * Method 2: classify tweets using NLP library and report accuracy.
     *
     */
    public static void classifyTweets() {

        outputArea.setText("");
        outputArea.append(HANDLER.getTweetDB().size() + " records in database.\n");
        outputArea.append(HANDLER.getTweetBuffer().size()+" records in temp list.\n");
        outputArea.append("Command 2\n");
        outputArea.append("Classification may take a while.\n");
        outputArea.append("Click Confirm when ready.\n");

        topPanel.removeAll();
        topPanel.add(commandLabel);
        topPanel.add(comboBox);

        final JButton confirmButton = new JButton("Confirm");

        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.info("Confirm button clicked. (Command 2)");

                ExecutorService threadPool = Executors.newFixedThreadPool(threadCount);
                if (HANDLER.getTweetBuffer().isEmpty()) {
                    outputArea.append("Tweet buffer is empty. Nothing to classify.\n");
                    return;
                } else {
                    outputArea.append("Classification processing. Please wait for the result.\n");
                }

                for (AbstractTweet t : HANDLER.getTweetBuffer()) {
                    Runnable myThread = new ThreadPoolClassify(t);
                    threadPool.execute(myThread);
                }

                threadPool.shutdown();
                outputArea.append("Done.\n");
                outputArea.append("Correct classified tweets: " + ThreadPoolClassify.countCorrect + "\n");
                outputArea.append("Incorrect classified tweets: " + ThreadPoolClassify.countWrong + "\n");
                double correctRate = (double) ThreadPoolClassify.countCorrect / ((double) ThreadPoolClassify.countWrong + (double) ThreadPoolClassify.countCorrect) * 100;
                outputArea.append("Correct prediction rate: " + String.format("%.2f", correctRate) + "%\n");
            }
        });


        topPanel.add(new JLabel());
        topPanel.add(new JLabel());
        topPanel.add(new JLabel());
        topPanel.add(new JLabel());
        topPanel.add(new JLabel());
        topPanel.add(new JLabel());
        topPanel.add(new JLabel());
        topPanel.add(new JLabel());

        topPanel.add(tempListButton);
        topPanel.add(confirmButton);
        topPanel.add(databaseButton);
        topPanel.add(saveButton);
        topPanel.updateUI();
    }

    /**
     * Method 3: manually change tweet class label.
     *
     */
    static int newPolarity = 0;
    public static void changeTweet() {

        outputArea.setText("");
        outputArea.append(HANDLER.getTweetDB().size() + " records in database.\n");
        outputArea.append(HANDLER.getTweetBuffer().size()+" records in temp list.\n");
        outputArea.append("Command 3\n");
        outputArea.append("Please input the ID of tweet to change.\n");

        topPanel.removeAll();
        topPanel.add(commandLabel);
        topPanel.add(comboBox);

        final JLabel idLabel = new JLabel("Tweet ID:",JLabel.RIGHT);
        final JTextField idInput = new JTextField("");

        final JLabel polarityLabel = new JLabel("New polarity:",JLabel.RIGHT);
        final JComboBox polarityComboBox = new JComboBox();
        polarityComboBox.addItem("Please select...");
        polarityComboBox.addItem("Positive");
        polarityComboBox.addItem("Neutral");
        polarityComboBox.addItem("Negative");

        polarityComboBox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                log.info("Polarity chosen, Item = " + e.getItem());
                log.info("StateChange = " + e.getStateChange());
                if (e.getStateChange() == 1) {
                    if (e.getItem().equals("Please select...")) {
                        outputArea.append("Please select a new polarity.\n");
                    } else if (e.getItem().equals("Positive")) {
                        newPolarity = 4;
                    } else if (e.getItem().equals("Neutral")) {
                        newPolarity = 2;
                    } else if (e.getItem().equals("Negative")) {
                        newPolarity = 0;
                    }
                }
            }
        });

        final JButton confirmButton = new JButton("Confirm");

        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.info("Confirm button clicked. (Command 3)");
                Runnable myRunnable = new Runnable() {

                    public void run() {

                        int targetId = Integer.parseInt(idInput.getText());

                        int targetIndex = -1;
                        for (AbstractTweet t : HANDLER.getTweetBuffer()) {
                            if (t.getId() == targetId) {
                                targetIndex = HANDLER.getTweetBuffer().indexOf(t);
                            }
                        }
                        outputArea.append("\nTweet ID: " + targetId + ".\n");
                        if (targetIndex == -1) {
                            outputArea.append("Tweet not found in temp list.\n");
                            return;
                        } else {
                            outputArea.append("Previous polarity: " + HANDLER.getTweetBuffer().get(targetIndex).getPredictedPolarity() + "\n");
                            HANDLER.getTweetBuffer().get(targetIndex).setPredictedPolarity(newPolarity);
                            outputArea.append("Predicted polarity updated to " + newPolarity + ".\n");
                        }
                    }
                };

                Thread thread = new Thread(myRunnable);
                thread.start();
            }

        });
        topPanel.add(idLabel);
        topPanel.add(idInput);
        topPanel.add(polarityLabel);
        topPanel.add(polarityComboBox);
        topPanel.add(new JLabel());
        topPanel.add(new JLabel());
        topPanel.add(new JLabel());
        topPanel.add(new JLabel());

        topPanel.add(tempListButton);
        topPanel.add(confirmButton);
        topPanel.add(databaseButton);
        topPanel.add(saveButton);
        topPanel.updateUI();
    }

    /**
     * Method 4: add new tweets to database.
     *
     */
    public static void addTweets() {
        outputArea.setText("");
        outputArea.append(HANDLER.getTweetDB().size() + " records in database.\n");
        outputArea.append(HANDLER.getTweetBuffer().size()+" records in temp list.\n");
        outputArea.append("Command 4\n");
        outputArea.append("Click Confirm when ready.\n");

        // Add all tweets in memory into database list
        topPanel.removeAll();
        topPanel.add(commandLabel);
        topPanel.add(comboBox);

        final JButton confirmButton = new JButton("Confirm");
        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.info("Confirm button clicked. (Command 4)");
                Runnable myRunnable = new Runnable() {

                    public void run() {
                        HANDLER.addTweetsToDB(HANDLER.getTweetBuffer());
                        try {
                            Thread.sleep(4000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        outputArea.append(HANDLER.getTweetDB().size() + " records in database.\n");
                        outputArea.append(HANDLER.getTweetBuffer().size()+" records in temp list.\n");
                        outputArea.append("Tweet buffer is now empty.\n");

                    }
                };

                Thread thread = new Thread(myRunnable);
                thread.start();
            }
        });


        topPanel.add(new JLabel());
        topPanel.add(new JLabel());
        topPanel.add(new JLabel());
        topPanel.add(new JLabel());
        topPanel.add(new JLabel());
        topPanel.add(new JLabel());
        topPanel.add(new JLabel());
        topPanel.add(new JLabel());

        topPanel.add(tempListButton);
        topPanel.add(confirmButton);
        topPanel.add(databaseButton);
        topPanel.add(saveButton);
        topPanel.updateUI();
    }

    /**
     * Method 5: delete tweet from database (given its id).
     *
     */
    public static void deleteTweet() {

        outputArea.setText("");
        outputArea.append(HANDLER.getTweetDB().size() + " records in database.\n");
        outputArea.append(HANDLER.getTweetBuffer().size()+" records in temp list.\n");
        outputArea.append("Command 5\n");
        outputArea.append("Please input the ID of tweet to delete.\n");

        topPanel.removeAll();
        topPanel.add(commandLabel);
        topPanel.add(comboBox);

        final JLabel idLabel = new JLabel("Tweet ID:",JLabel.RIGHT);
        final JTextField idInput = new JTextField("");

        final JButton confirmButton = new JButton("Confirm");

        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.info("Confirm button clicked. (Command 5)");
                Runnable myRunnable = new Runnable() {

                    public void run() {
                        int inputId = Integer.parseInt(idInput.getText());

                        HANDLER.deleteTweet(inputId);
                    }
                };

                Thread thread = new Thread(myRunnable);
                thread.start();
            }

        });
        topPanel.add(idLabel);
        topPanel.add(idInput);
        topPanel.add(new JLabel());
        topPanel.add(new JLabel());
        topPanel.add(new JLabel());
        topPanel.add(new JLabel());
        topPanel.add(new JLabel());
        topPanel.add(new JLabel());

        topPanel.add(tempListButton);
        topPanel.add(confirmButton);
        topPanel.add(databaseButton);
        topPanel.add(saveButton);
        topPanel.updateUI();

    }

    /**
     * Method 6: search tweets by user, date, flag, or a matching substring.
     @return list of tweets that match the search
     */
    static List<AbstractTweet> resultList = new ArrayList<AbstractTweet>();
    public static void searchTweet() {
        outputArea.setText("");
        outputArea.append(HANDLER.getTweetDB().size() + " records in database.\n");
        outputArea.append(HANDLER.getTweetBuffer().size()+" records in temp list.\n");
        outputArea.append("Command 6\n");
        outputArea.append("Please input information and click.\n");

        topPanel.removeAll();
        topPanel.add(commandLabel);
        topPanel.add(comboBox);

        final JLabel textLabel = new JLabel("Input:", JLabel.RIGHT);
        final JTextField textInput = new JTextField("");


        final JRadioButton userButton = new JRadioButton("User", true);
        userButton.setHorizontalAlignment(SwingConstants.RIGHT);
        final JRadioButton dateButton = new JRadioButton("Date");
        dateButton.setHorizontalAlignment(SwingConstants.RIGHT);
        final JRadioButton flagButton = new JRadioButton("Flag");
        flagButton.setHorizontalAlignment(SwingConstants.RIGHT);
        final JRadioButton substringButton = new JRadioButton("Substring");
        substringButton.setHorizontalAlignment(SwingConstants.RIGHT);

        final ButtonGroup group = new ButtonGroup();
        group.add(userButton);
        group.add(dateButton);
        group.add(flagButton);
        group.add(substringButton);


        final JButton confirmButton = new JButton("Confirm");

        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.info("Confirm button clicked. (Command 6)");
                Runnable myRunnable = new Runnable() {

                    public void run() {
                        resultList = new ArrayList<AbstractTweet>();
                        if (userButton.isSelected()) {
                            resultList.addAll(HANDLER.searchByUser(textInput.getText()));
                        }

                        SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
                        if (dateButton.isSelected()){
                            try {
                                Date date = formatter.parse(textInput.getText());
                                resultList.addAll(HANDLER.searchByDate(date));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }

                        if (flagButton.isSelected()) {
                            resultList.addAll(HANDLER.searchByFlag(textInput.getText()));
                        }

                        if (substringButton.isSelected()) {
                            resultList.addAll(HANDLER.searchBySubstring(textInput.getText()));
                        }

                        if (resultList.isEmpty()) {
                            outputArea.append("0 result found.\n");
                        } else {
                            outputArea.append(resultList.size() + " result found.\n");
                            //printList(resultList);
                            printJTable(resultList);
                        }
                    }
                };

                Thread thread = new Thread(myRunnable);
                thread.start();
            }

        });

        GridLayout optionPanelGridLayout = new GridLayout(1,2,0,0);
        JPanel optionPanel1 = new JPanel();
        JPanel optionPanel2 = new JPanel();
        optionPanel1.setLayout(optionPanelGridLayout);
        optionPanel2.setLayout(optionPanelGridLayout);
        optionPanel1.add(userButton);
        optionPanel1.add(dateButton);
        optionPanel2.add(flagButton);
        optionPanel2.add(substringButton);

        topPanel.add(optionPanel1);
        topPanel.add(optionPanel2);
        topPanel.add(textLabel);
        topPanel.add(textInput);
        topPanel.add(new JLabel());
        topPanel.add(new JLabel());
        topPanel.add(new JLabel());
        topPanel.add(new JLabel());

        topPanel.add(tempListButton);
        topPanel.add(confirmButton);
        topPanel.add(databaseButton);
        topPanel.add(saveButton);
        topPanel.updateUI();
    }

    /**
     * Method 0: save and quit.
     */
    public static void exit() {

        outputArea.setText("");
        outputArea.append(HANDLER.getTweetDB().size() + " records in database.\n");
        outputArea.append(HANDLER.getTweetBuffer().size()+" records in temp list.\n");
        outputArea.append("Command 0\n");
        outputArea.append("Please click Confirm to save and exit the system.\n");

        topPanel.removeAll();
        topPanel.add(commandLabel);
        topPanel.add(comboBox);

        final JButton confirmButton = new JButton("Confirm");
        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.info("Confirm button clicked. (Command 0)");
                Runnable myRunnable = new Runnable() {

                    public void run() {
                        HANDLER.saveSerialDB();
                        outputArea.append("Database saved. System will be closed in 4 seconds.\n");
                        outputArea.append("Thank you for using!\n");

                        log.info("Exit the database. (Command 0)");
                        try {
                            Thread.sleep(4000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        log.info("System shutdown.");
                        System.exit(0);
                    }
                };

                Thread thread = new Thread(myRunnable);
                thread.start();
            }

        });

        topPanel.add(new JLabel());
        topPanel.add(new JLabel());
        topPanel.add(new JLabel());
        topPanel.add(new JLabel());
        topPanel.add(new JLabel());
        topPanel.add(new JLabel());
        topPanel.add(new JLabel());
        topPanel.add(new JLabel());

        topPanel.add(tempListButton);
        topPanel.add(confirmButton);
        topPanel.add(databaseButton);
        topPanel.add(saveButton);
        topPanel.updateUI();
        topPanel.updateUI();
    }

    /**
     * Print out the formatted table for list
     @param target_List
     */
    public static void printList(List<AbstractTweet> target_List) {
        String line = "----------------------------------------------------------------------------------"
                + "----------------------------------------------------------------------------------";
        String information = "| ";
        information = information + String.format("%6s", "Target") + " | ";
        information = information + String.format("%6s", "Class") + " | ";
        information = information + String.format("%6s", "ID") + " | ";
        information = information + String.format("%30s", "Date") + " | ";
        information = information + String.format("%10s", "Flag") + " | ";
        information = information + String.format("%15s", "User") + " | ";
        information = information + String.format("%70s", "Text") + " |";

        System.out.println(line);
        System.out.println(information);
        System.out.println(line);
        for (AbstractTweet t : target_List) {
            System.out.println(t);
            System.out.println(line);
        }
    }

    /**
     * Print out the formatted JTable for list
     @param target_List
     */
    public static void printJTable(List<AbstractTweet> target_List) {
        // Create columns names
        String columnNames[] = {"Target", "Class", "ID", "Date", "Flag", "User", "Text"};
        // Create some data
        String dataValues[][]= new String[target_List.size()][7];
        for(int i = 0; i < target_List.size(); i++) {
            dataValues[i][0] = String.valueOf(target_List.get(i).getTarget());
            dataValues[i][1] = String.valueOf(target_List.get(i).getPredictedPolarity());
            dataValues[i][2] = String.valueOf(target_List.get(i).getId());
            dataValues[i][3] = target_List.get(i).getDate().toString();
            dataValues[i][4] = target_List.get(i).getFlag();
            dataValues[i][5] = target_List.get(i).getUser();
            dataValues[i][6] = target_List.get(i).getText();
        }
        // Create a new table instance
        JTable table = new JTable(dataValues, columnNames) {
            public boolean isCellEditable(int row, int column){
                return false;
            }
        };
        table.setFillsViewportHeight(true);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);

        // Add the table to a scrolling pane
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.createVerticalScrollBar();
        scrollPane.createHorizontalScrollBar();
        scrollPane.createVerticalScrollBar();
        scrollPane.createHorizontalScrollBar();
        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame resultFrame = new JFrame("Search Result: Tweets");
        resultFrame.setBounds((width - windowsWidth) / 4,
                (height - windowsHeight) / 4, windowsWidth, windowsHeight/2);
        resultFrame.setContentPane(scrollPane);
        resultFrame.setVisible(true);
    }
}
