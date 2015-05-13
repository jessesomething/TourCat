package com.jessespalding;

import se.walkercrou.places.GooglePlaces;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.sql.Date;
import java.util.*;

/**
 * Created by Jesse on 4/23/2015.
 */
public class TourCatGUI extends JFrame implements WindowFocusListener{
    // GUI components
    private JPanel rootPanel;
    private JButton previousEventButton;
    private JLabel venueDateLabel;
    private JButton nextVenueButton;
    private JLabel venueNameLabel;
    private JLabel venueCityLabel;
    private JLabel soldTicketsLabel;
    private JButton directionsButton;
    private JButton addNewShowButton;
    private JComboBox merchComboBox;
    private JButton sellButton;
    private JTextField quantityTextField;
    private JButton stockButton;
    private JButton editMerchButton;
    private JLabel nextShowVenueLabel;
    private JLabel nextShowDateLabel;
    private JButton refreshButton;
    private JComboBox sizeComboBox;
    private JComboBox kindsComboBox;
    private JLabel itemPriceLabel;


    // Show controller to be changed with next/prev show arrow buttons
    // to call different Show object models
    private int showId = 0;

    // Boolean checks to see if any shows had been made or it's the first run
    public static boolean firstRun = true;
    public static boolean noShows = true;

    // listmodel for Shows objects
    static DefaultListModel<Show> showsModel = new DefaultListModel<Show>();

    // driver address string elements
    private static String driver = "org.apache.derby.jdbc.EmbeddedDriver";
    private static String protocol = "jdbc:derby:";
    private static String dbName = "SalesDB";

    // default db user name and pw
    private static final String USER = "username";
    private static final String PASS = "password";

    // url to be used in directions link constructor
    private String directionsUrl = "";

    private static Integer merchId = 0;

    java.sql.Date timeNow = new Date(Calendar.getInstance().getTimeInMillis());

    // Hashmaps for size and qty references
    private HashMap<String, String> merchSizeList = new HashMap<String, String>();
    private HashMap<String, Integer> merchQtyList = new HashMap<String, Integer>();

    public TourCatGUI() throws IOException {
        super("TourCat Shows");
        setContentPane(rootPanel);
        directionsButton.setVisible(false);
        pack();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
        // todo make size more dynamic based on maximum field size
        setSize(800, 400);
        // refreshes inventory and merchandise fields
        refreshInv();
        refreshMerch();

        // Window listener refreshes page when reactivated
        this.addWindowListener(new WindowAdapter() {
                                   public void windowActivated(WindowEvent e) {
                                       refreshMain();
                                       displayShow(showId);
                                   }
                               });


        addNewShowButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // opens NewShow GUI
                    NewShowGUI newShowGUI = new NewShowGUI();
                } catch (IOException ioe) {
                    System.out.println("IO Exception Error");
                }
            }
        });

        editMerchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // opens New Merch GUI
                    MerchGUI merchGui = new MerchGUI();
                } catch (IOException ioe) {
                    System.out.println("IO Exception Error");
                }
            }
        });

//        while (noShows) {
//            // do nothing
//            if (noShows = false) {
//                displayShow(showId);
//                break;
//            }
//        }

        refreshButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                refreshMain();
                refreshInv();
            }
        });

        nextVenueButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    setShowId(showId + 1);
                    displayShow(showId);
                } catch (IndexOutOfBoundsException iob) {
                    System.out.println("No upcoming shows");
                }

            }
        });


        previousEventButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    setShowId(showId - 1);
                    displayShow(showId);
                } catch (IndexOutOfBoundsException ioe) {
                    System.out.println("No previous shows'");
                }
            }
        });
        merchComboBox.addItemListener(new ItemListener() {
            @Override
            //todo
            public void itemStateChanged(ItemEvent e) {
                sizeComboBox.removeAllItems();
                refreshMerch();
            }
        });



        rootPanel.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                super.focusGained(e);
                sizeComboBox.removeAllItems();
                refreshMain();
                refreshInv();
            }
        });

        directionsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Opens directions with constructed url
                openDirections(directionsUrl);
            }
        });
        kindsComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent event) {
                // Kind of wonky but had to use so it would refresh labels
                // todo make better
                if (event.getStateChange() == ItemEvent.SELECTED) {
                    refreshPriceQty();
                }
            }
        });
        sellButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeInv();
            }
        });


        // todo doesn't change price and quantity
        sizeComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshPriceQty();
            }
        });
    }

    private void createUIComponents() {
    }

    // todo Use database to display show dates on main GUI
////    public void currentVenue() {
////        Statement statement = null;
////        Connection conn = null;
////        ResultSet rs = null;
////        try {
////            Class.forName(driver);
////            conn = DriverManager.getConnection(protocol + dbName + ";create=true;", USER, PASS);
////            statement = conn.createStatement();
//////            "CREATE TABLE Shows (ShowDate DATE, VenueName VARCHAR(64), Street VARCHAR(64),\" +\n" +
//////                    "                        \"City VARCHAR(64), State VARCHAR(64), Country VARCHAR(64), SoldTickets INT, \" +\n" +
//////                    "                        \"Price INT, TicketFee INT, MerchFee INT, OtherFee INT"
////            String fetchMerch = "SELECT * FROM Shows";
//////                    "WHERE S.Date >= '" + timeNow + "' )";
////            rs = statement.executeQuery(fetchMerch);
////
////            while (rs.next()) {
////
////                try {
//////                    String showDate = rs.getString("ShowDate");
////                    Date showDate = rs.getDate("ShowDate");
////                    String showDateStr = showDate.toString();
////                    Date
////                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
////                    DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
//////                    String showDateStr = df.parse(showDateStr);
////                    venueDateLabel.setText(showDateStr);
////                    String venueName = rs.getString("VenueName");
////                    String city = rs.getString("City");
////                    String state = rs.getString("State");
////                    String sold = rs.getString("SoldTickets");
////                    venueDateLabel.setText(showDateStr);
////                    venueNameLabel.setText(venueName);
////                    venueCityLabel.setText(city + ", " + state);
////                    soldTicketsLabel.setText(sold);
////                } catch (ParseException pe) {
////                    pe.printStackTrace();
////                }
////
////            }
////
////        } catch (ClassNotFoundException cnf) {
////            System.out.println("Class not found");
////        } catch (SQLException se) {
////            System.out.println("No show inventory exists");
////        } finally {
////            try {
////                if (rs != null) {
////                    rs.close();
////                    System.out.println("Result set is closed");
////                }
////            } catch (SQLException se) {
////                se.printStackTrace();
////            }
////
////            try {
////                if (statement != null) {
////                    statement.close();
////                    System.out.println("Statement closed");
////                }
////            } catch (SQLException se) {
////                se.printStackTrace();
////            }
////
////            try {
////                if (psInsert != null) {
////                    psInsert.close();
////                    System.out.println("Prepared statement closed");
////                }
////            } catch (SQLException se) {
////                se.printStackTrace();
////            }
//
//            try {
//                // If connection is null and finished, gives message
//                if (conn != null) {
//                    conn.close();
//                    System.out.println("Database connection is closed");
//                }
//            } catch (SQLException se) {
//                se.printStackTrace();
//            }
//
//        }
//    }

// method used to refresh page according to showsModel selected by arrow buttons
    public void displayShow(int showId) {
        // Reads Show model details
        String date = showsModel.getElementAt(showId).getShowDate().toString();
        final String venueNameSimple = showsModel.getElementAt(showId).getSimpleVenueName().toString();
        final String venueName = showsModel.getElementAt(showId).getVenueName().toString();
        final String venueAddress = showsModel.getElementAt(showId).getStreetName().toString();
        final String venueCity = showsModel.getElementAt(showId).getCityName().toString();
        final String venueState = showsModel.getElementAt(showId).getStateName().toString();
        int venueSoldInt = showsModel.getElementAt(showId).getSoldTickets();
        String venueSold = Integer.toString(venueSoldInt);
        // Sets the labels according to show details
        TourCatGUI.this.venueDateLabel.setText(date);
        TourCatGUI.this.venueNameLabel.setText(venueName);
        TourCatGUI.this.venueCityLabel.setText(venueCity + ", " + venueState);
        TourCatGUI.this.soldTicketsLabel.setText("Tickets sold: " + venueSold);

        // Assigns url for directions button link
        directionsUrl = "https://maps.google.com?saddr=Current+Location&daddr=" + searchAddress(venueAddress, venueCity, venueState) +
                "&type=establishment" + "&name=" + plusSeparate(venueNameSimple) + "&key=" + "AIzaSyDkzkGyuOsBH7f0zszPFz2htLciSoc0Yjs";

        directionsButton.setVisible(true);

        // trys to set next show labels if there are any
        try {
            String nextDate = showsModel.getElementAt(showId+1).getShowDate().toString();
            String nextVenueName = showsModel.getElementAt(showId+1).getVenueName();
            TourCatGUI.this.nextShowDateLabel.setText(nextDate);
            TourCatGUI.this.nextShowVenueLabel.setText(nextVenueName);
        } catch (IndexOutOfBoundsException iob) {
            System.out.println("No more shows do display");
            String nextDate = "None Scheduled";
            String nextVenueName = "";
            TourCatGUI.this.nextShowDateLabel.setText(nextDate);
            TourCatGUI.this.nextShowVenueLabel.setText(nextVenueName);
        }

        revalidate();
        repaint();
    }

    public void refreshInv() {
        // Refreshes merch fields
        // Somewhat repeats the same things as refreshMerch()
        // todo clean up and consolidate refreshInv and refreshMerch
        Statement statement = null;
        Connection conn = null;
        ResultSet rs = null;
        try {
            // Removals all the combobox items and sets fields to default
            merchComboBox.removeAllItems();
            kindsComboBox.removeAllItems();
            sizeComboBox.removeAllItems();
            quantityTextField.removeAll();
            itemPriceLabel.setText("");
            Class.forName(driver);
            conn = DriverManager.getConnection(protocol + dbName + ";create=true;", USER, PASS);
            statement = conn.createStatement();
            String fetchMerch = "SELECT * FROM Merchandise";
            rs = statement.executeQuery(fetchMerch);

            while (rs.next()) {
                // todo remove some unneeded variables or consolidate with refreshMerch
                String nameFound = "";
                String name = rs.getString("ItemName");
                String kinds = rs.getString("Kinds");
                String size = rs.getString("Sizes");
                String price = rs.getString("Price");
                int qty = rs.getInt("Quantity");
                boolean found = false;
                for (int i = 0; i < merchComboBox.getItemCount(); i++) {
                    if (merchComboBox.getItemAt(i).toString().equals(name)) {
                        found = true;
                    }
                }
                if (!found) {
                    merchComboBox.addItem(name);
                }
//                merchComboBox.addItem(name);

//                if (!size.equals("")) {
//                    sizeComboBox.addItem(size);
//                } else {
//
//                }
            }

        } catch (ClassNotFoundException cnf) {
            System.out.println("Class not found");
        } catch (SQLException se) {
            System.out.println("No inventory exists");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                    System.out.println("Result set is closed");
                }
            } catch (SQLException se) {
                se.printStackTrace();
            }

            try {
                if (statement != null) {
                    statement.close();
                    System.out.println("Statement closed");
                }
            } catch (SQLException se) {
                se.printStackTrace();
            }
//
//            try {
//                if (psInsert != null) {
//                    psInsert.close();
//                    System.out.println("Prepared statement closed");
//                }
//            } catch (SQLException se) {
//                se.printStackTrace();
//            }

            try {
                // If connection is null and finished, gives message
                if (conn != null) {
                    conn.close();
                    System.out.println("Database connection is closed");
                }
            } catch (SQLException se) {
                se.printStackTrace();
            }

        }
    }

    // Used when removing or selling merchandise
    // todo get the quantity to update correctly and add remove button for lost items
    public void removeInv() {
        Statement statement = null;
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement psInsert = null;
        try {
            // Reads selected combobox items
            String merch = merchComboBox.getSelectedItem().toString();
            String kind = kindsComboBox.getSelectedItem().toString();
            String size = sizeComboBox.getSelectedItem().toString();
            int qty = Integer.valueOf(quantityTextField.getText());

            Class.forName(driver);
            conn = DriverManager.getConnection(protocol + dbName + ";create=true;", USER, PASS);
            statement = conn.createStatement();
            // Query statement to find items with the selected name, kind, and size
            String fetchMerch = "SELECT * FROM Merchandise WHERE ItemName = '" + merch +
                    "' AND Kinds = '" + kind + "'" + " AND Sizes = '" + size + "'";
            rs = statement.executeQuery(fetchMerch);

            // Updates database which meets criteria
            // todo make it specifically remove system column id
            while (rs.next()) {
                String updateMerch = "UPDATE Merchandise SET Quantity = Quantity - ? WHERE ItemName = '" + merch +
                        "' AND Kinds = '" + kind + "'" + " AND Sizes = '" + size + "'";
                psInsert = conn.prepareStatement(updateMerch);
                psInsert.setInt(1, qty);
            }
            psInsert.executeUpdate();
            refreshInv();

        } catch (ClassNotFoundException cnf) {
            System.out.println("Class not found");
        } catch (SQLException se) {
            System.out.println("No inventory exists");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                    System.out.println("Result set is closed");
                }
            } catch (SQLException se) {
                se.printStackTrace();
            }

            try {
                if (statement != null) {
                    statement.close();
                    System.out.println("Statement closed");
                }
            } catch (SQLException se) {
                se.printStackTrace();
            }
//
//            try {
//                if (psInsert != null) {
//                    psInsert.close();
//                    System.out.println("Prepared statement closed");
//                }
//            } catch (SQLException se) {
//                se.printStackTrace();
//            }

            try {
                // If connection is null and finished, gives message
                if (conn != null) {
                    conn.close();
                    System.out.println("Database connection is closed");
                }
            } catch (SQLException se) {
                se.printStackTrace();
            }

        }
    }

    // Does much of what refreshInv does but better
    // todo consolidate refreshInv into this one
    public void refreshMerch() {
        Statement statement = null;
        Connection conn = null;
        ResultSet rs = null;
//        PreparedStatement psInsert = null;

        try {
            Class.forName(driver);
            conn = DriverManager.getConnection(protocol + dbName + ";create=true;", USER, PASS);
            statement = conn.createStatement();
            sizeComboBox.removeAllItems();
            kindsComboBox.removeAllItems();
            itemPriceLabel.removeAll();
            quantityTextField.removeAll();
            String merchSelect = "";
            String sizeSelect = "";
            if (merchComboBox.getSelectedIndex() == -1) {
            } else {
                merchSelect = merchComboBox.getSelectedItem().toString();
            }
            if (sizeComboBox.getSelectedIndex() == -1) {

            } else {
                sizeSelect  = sizeComboBox.getSelectedItem().toString();
            }
//            String kind = "";
//            if (kindsComboBox.getSelectedItem().toString().equals("")) {
//                // do nothing
//            } else {
//                kind = kindsComboBox.getSelectedItem().toString();
//            }
//            String fetchItem = "SELECT Sizes,Kinds,Price FROM Merchandise WHERE ItemName = '" + merch + "' AND Kinds = '" + kind + "'";

            String fetchItem = "SELECT Sizes,Kinds,Price FROM Merchandise WHERE ItemName = '" + merchSelect +
                    "'";
            rs = statement.executeQuery(fetchItem);


            // Reads each query item and updates corresponding fields
            // todo needs cleaning up
            while (rs.next()) {
                boolean kindFound = false;
                boolean sizeFound = false;
                String kind = rs.getString("Kinds");
                String price = rs.getString("Price");
                String size = rs.getString("Sizes");
                for (int i = 0; i < kindsComboBox.getItemCount(); i++) {
                    if (kindsComboBox.getItemAt(i).toString().equals(kind)){
                        kindFound = true;
                    }
                }
                if (!kindFound) {
                    kindsComboBox.addItem(kind);
                }
                for (int i = 0; i < sizeComboBox.getItemCount(); i++) {
                    try {
                        if (kindsComboBox.getItemAt(i).toString().equals(size)) {
                            sizeFound = true;
                        }
                    } catch (NullPointerException npe) {
                        System.out.println("No kind selected");
                    }

                }
                if (!size.equals(itemPriceLabel.toString())){
                    itemPriceLabel.setText(price);
                }
            }

        } catch (ClassNotFoundException cnf) {
            System.out.println("Class not found");
        } catch (SQLException se) {
            System.out.println("No inventory exists");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                    System.out.println("Result set is closed");
                }
            } catch (SQLException se) {
                se.printStackTrace();
            }

            try {
                if (statement != null) {
                    statement.close();
                    System.out.println("Statement closed");
                }
            } catch (SQLException se) {
                se.printStackTrace();
            }

            try {
                // If connection is null and finished, gives message
                if (conn != null) {
                    conn.close();
                    System.out.println("Database connection is closed");
                }
            } catch (SQLException se) {
                se.printStackTrace();
            }

        }
    }

    // This again does a lot with refreshing price and quantity according to
    // what is specifically selected -- doesn't work well with others
    // todo get rid of this and put functions into refreshMerch
    public void refreshPriceQty() {
        Statement statement = null;
        Connection conn = null;
        ResultSet rs = null;
//        PreparedStatement psInsert = null;

        try {
            System.out.println("refreshing price and qty");
            Class.forName(driver);
            conn = DriverManager.getConnection(protocol + dbName + ";create=true;", USER, PASS);
            statement = conn.createStatement();
            String merch = "";
            if (merchComboBox.getSelectedIndex() == -1) {
                // do nothing
            } else {
                merch = merchComboBox.getSelectedItem().toString();
            }
            String kind = kindsComboBox.getSelectedItem().toString();
            sizeComboBox.removeAllItems();


            String fetchItem = "SELECT ItemName, Sizes,Kinds,Price,Quantity FROM Merchandise WHERE ItemName = '"
                    + merch + "' AND Kinds = '" + kind + "'";
            rs = statement.executeQuery(fetchItem);


            while (rs.next()) {
                boolean foundSize = false;
                String name = rs.getString("ItemName");
                String kinds = rs.getString("Kinds");
                String price = rs.getString("Price");
                String sizes = rs.getString("Sizes");
                String qty = String.valueOf(rs.getInt("Quantity"));
                if (!qty.equals(null)) {
                    quantityTextField.setText(qty);
                } else {
                    quantityTextField.setText("0");
                }
                if (name.equals(merch) && kinds.equals(kind)){
                    itemPriceLabel.setText(price);
                    quantityTextField.setText(qty);
                }
                for (int i = 0; i < sizeComboBox.getItemCount(); i++) {
                    if (sizeComboBox.getItemAt(i).equals(sizes)){
                        foundSize = true;
                    }
                }
                if (kinds.equals(kind) && !foundSize) {
                    sizeComboBox.addItem(sizes);
                }

                System.out.println(name + " " + price + " " + qty);
            }

        } catch (ClassNotFoundException cnf) {
            System.out.println("Class not found");
        } catch (SQLException se) {
            System.out.println("SQL Exception Error");
            se.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                    System.out.println("Result set is closed");
                }
            } catch (SQLException se) {
                se.printStackTrace();
            }

            try {
                if (statement != null) {
                    statement.close();
                    System.out.println("Statement closed");
                }
            } catch (SQLException se) {
                se.printStackTrace();
            }

            try {
                // If connection is null and finished, gives message
                if (conn != null) {
                    conn.close();
                    System.out.println("Database connection is closed");
                }
            } catch (SQLException se) {
                se.printStackTrace();
            }

        }
    }

    // Main GUI refresh method
    // Used with refresh button
    // todo consolidate with displayShow method
    public void refreshMain() {
        if (noShows) {
            System.out.println("No shows in database");
        }
        try {
            String date = showsModel.getElementAt(showId).getShowDate().toString();
            final String venueNameSimple = showsModel.getElementAt(showId).getSimpleVenueName().toString();
            final String venueName = showsModel.getElementAt(showId).getVenueName();
            final String venueAddress = showsModel.getElementAt(showId).getStreetName().toString();
            final String venueCity = showsModel.getElementAt(showId).getCityName().toString();
            final String venueState = showsModel.getElementAt(showId).getStateName().toString();
            int venueSoldInt = showsModel.getElementAt(showId).getSoldTickets();
            String venueSold = Integer.toString(venueSoldInt);
            venueDateLabel.setText(date);
            venueNameLabel.setText(venueName);
            venueCityLabel.setText(venueCity + ", " + venueState);
            soldTicketsLabel.setText("Tickets sold: " + venueSold);
            revalidate();
            repaint();
            directionsUrl = "https://maps.google.com?saddr=Current+Location&daddr=" + searchAddress(venueAddress, venueCity, venueState) +
                    "&type=establishment" + "&name=" + plusSeparate(venueNameSimple) + "&key=" + "AIzaSyDkzkGyuOsBH7f0zszPFz2htLciSoc0Yjs";
            directionsButton.setVisible(true);
        } catch (IndexOutOfBoundsException iob) {
            System.out.println("No shows to display");
        }
    }



    public void windowActivated(WindowEvent e) {
        refreshMain();
    }

    // Reformats the received address data from GooglePlace API
    // and converts each space into a plus for URL
    public static String searchAddress(String street, String city, String state) {
        String streetPlus = street.replace(' ', '+');
        String cityPlus = city.replace(' ', '+');
        String statePlus = state.replace(' ', '+');
        String venueFullAddress = streetPlus + "+" + cityPlus + "+" + statePlus;
        return venueFullAddress;
    }

    // Adds pluses to a single string
    public static String plusSeparate(String phrase) {
        String phrasePlus = phrase.replace(' ', '+');
        return phrasePlus;
    }

    // Directions link opens a new window with provided url
    public static void openDirections(String url) {
        try {
            Desktop.getDesktop().browse(new URL(url).toURI());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void windowGainedFocus(WindowEvent e) {
        // Can't get this to function correctly,
        // so won't refresh when adding a new show automatically
        refreshInv();
        refreshMain();
    }

    public void windowLostFocus(WindowEvent e) {

    }

    public static boolean isNoShows() {
        return noShows;
    }

    public static void setNoShows(boolean noShows) {
        TourCatGUI.noShows = noShows;
    }

    public int getShowId() {
        return showId;
    }

    public void setShowId(int showId) {
        this.showId = showId;
    }

    // first run boolean to be used in the future
    public static void setFirstRun(boolean firstRun) {
        TourCatGUI.firstRun = firstRun;
    }

    public static Integer getMerchId() {
        return merchId;
    }

    public static void setMerchId(Integer merchId) {
        TourCatGUI.merchId = merchId;
    }


}
