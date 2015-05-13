package com.jessespalding;

import se.walkercrou.places.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.sql.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.Date;
import java.util.List;

/**
 * Created by Jesse on 4/28/2015.
 */
public class NewShowGUI extends JFrame {
    private JPanel newShowPanel;
    private JLabel dateLabel;
    private JTextField dateTextField;
    private JTextField venueTextField;
    private JLabel venueLabel;
    private JTextField soldTicketsTextField;
    private JTextField addressTextField;
    private JTextField cityTextField;
    private JComboBox stateComboBox;
    private JButton saveAndAddButton;
    private JButton saveAndExitButton;
    private JTextField ticketFeeTextField;
    private JTextField merchFeeTextField;
    private JTextField otherFeeTextField;
    private JTextField contactTextField;
    private JComboBox timeComboBox;
    private JTextField priceTextField;
    private JLabel timeLabel;
    private JLabel addressLabel;
    private JLabel cityLabel;
    private JLabel stateLabel;
    private JLabel contactLabel;
    private JLabel priceLabel;
    private JLabel soldLabel;
    private JLabel ticketFeeLabel;
    private JLabel merchFeeLabel;
    private JLabel otherFeeLabel;
    private JLabel missingLabel;
    private JButton suggestionsButton;
    private JTextField stateSuggestTextField;
    private JLabel stateSuggestLabel;
    private JCheckBox outsideUSCheckBox;
    private JTextField provinceTextField;
    private JTextField countryTextField;
    private JButton startOverButton;
    private JButton nextSuggestionButton;

    DateFormat format = new SimpleDateFormat("MM/dd/yyyyh:mma", Locale.ENGLISH);

    String dateString = "";
    String time = "";
    String venue = "";
    String address = "";
    String city = "";
    String state = "";
    String contact = "";
    String stateString = "";
    String stateSuggestString = "";
    String country = "";
    String soldString = "";
    String priceString = "";
    String ticketFeeString = "";
    String merchFeeString = "";
    String otherFeeString = "";
    int price = 0;
    int sold = 0;
    int ticketFee = 0;
    int merchFee = 0;
    int otherFee = 0;

    private int suggestId = 0;

    private boolean saved = false;
    private boolean dbSaved = false;

    private static String driver = "org.apache.derby.jdbc.EmbeddedDriver";
    private static String protocol = "jdbc:derby:";
    private static String dbName = "SalesDB";

    private static final String USER = "username";
    private static final String PASS = "password";

    public NewShowGUI() throws IOException {
        super("TourCat Shows");
        setContentPane(newShowPanel);
        pack();
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setVisible(true);

        missingLabel.setVisible(false);
        missingLabel.setText("* Required fields");
        missingLabel.setForeground(Color.red);

        stateSuggestTextField.setVisible(false);
        stateSuggestLabel.setVisible(false);

        nextSuggestionButton.setVisible(false);

        final LinkedList<Place> venuePlaces = new LinkedList<Place>();

        String[] states = new String[] {"","Alabama", "Alaska", "Arizona", "Arkansas", "California", "Colorado", "Connecticut", "Delaware",
                "District of Columbia", "Florida", "Georgia", "Hawaii", "Idaho", "Illinois", "Indiana", "Iowa",
                "Kansas", "Kentucky", "Louisiana", "Maine", "Maryland", "Massachusetts", "Michigan", "Minnesota",
                "Mississippi", "Missouri", "Montana", "Nebraska", "Nevada", "New Hampshire", "New Jersey", "New Mexico",
                "New York", "North Carolina", "North Dakota", "Ohio", "Oklahoma", "Oregon", "Pennsylvania", "Rhode Island",
                "South Carolina", "South Dakota", "Tennessee", "Texas", "Utah", "Vermont", "Virginia", "Washington",
                "West Virginia", "Wisconsin", "Wyoming" };

        String[] times = new String[] {"","6:00AM", "6:30AM", "7:00AM", "7:30AM", "8:00AM", "8:30AM", "9:00AM", "9:30AM", "10:00AM", "10:30AM", "11:00AM", "11:30AM",
                "12:00PM", "12:30PM", "1:00PM", "1:30PM", "2:00PM", "2:30PM", "3:00PM", "3:30PM", "4:00PM", "4:30PM", "5:00PM", "5:30PM", "6:00PM", "6:30PM",
                "7:00PM", "7:30PM", "8:00PM", "8:30PM", "9:00PM", "9:30PM", "10:00PM", "10:30PM", "11:00PM", "11:30PM", "12:00AM (next day)", "12:30AM (next day)",
                "1:00AM (next day)", "1:30AM (next day)", "2:00AM (next day)", "2:30AM (next day)", "3:00AM (next day)", "3:30AM (next day)", "4:00AM (next day)",
                "4:30AM (next day)", "5:00AM (next day)", "5:30AM (next day)"};


        for (int i = 0; i < states.length; i++) {
            stateComboBox.addItem(states[i]);
        }

        for (int i = 0; i < times.length; i++) {
            timeComboBox.addItem(times[i]);
        }

        startOverButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetFields(venuePlaces);
            }
        });

        nextSuggestionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (suggestId < venuePlaces.size()) {
                    setSuggestId(getSuggestId()+1);
                    suggestPlace(venuePlaces.get(getSuggestId()));
                } else {
                    nextSuggestionButton.setVisible(false);
                }
            }
        });

        suggestionsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                venue = venueTextField.getText();
                address = addressTextField.getText();
                city = cityTextField.getText();
                state = stateComboBox.getSelectedItem().toString();

                GooglePlaces client = new GooglePlaces("AIzaSyDkzkGyuOsBH7f0zszPFz2htLciSoc0Yjs");

                String venueFull = venue + ", " + address + ", " + city + ", " + state;
                System.out.println(venueFull);
                List<Prediction> places = client.getPlacePredictions(venueFull, 2, Param.name("types").value("establishment"));

                venuePlaces.clear();

                for (Prediction place : places) {
                    System.out.println(places);
                    Place newPlace = place.getPlace();
                    venuePlaces.add(newPlace);
                    Place venuePlaceDetails = venuePlaces.peek().getDetails();
                    System.out.println(venuePlaceDetails.getName());
                }

                suggestPlace(venuePlaces.get(suggestId));

                if (suggestId < venuePlaces.size() + 1) {
                    nextSuggestionButton.setVisible(true);
                } else {
                    nextSuggestionButton.setVisible(false);
                }
            }
        });

        saveAndAddButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveVenue();
                resetFields(venuePlaces);
            }
        });

        saveAndExitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveVenue();
//                saveShowDb();
                if (saved) {

                    dispose();
                }
            }
        });

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                JFrame frame = (JFrame) e.getSource();

                int result = JOptionPane.showConfirmDialog(frame,
                        "Close without saving?",
                        "Exit Application",
                        JOptionPane.YES_NO_OPTION);

                if (result == JOptionPane.YES_OPTION)
                    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            }
        });
    }

    public void suggestPlace(Place venuePlaces) {
        Place venuePlaceDetails = venuePlaces.getDetails(); // sends a GET request for more details
        // Just an example of the amount of information at your disposal:
        venueTextField.setText(venuePlaceDetails.getName());
        addressTextField.setText(getAddressPart(venuePlaceDetails.getAddress(), 0));
        cityTextField.setText(getAddressPart(venuePlaceDetails.getAddress(), 1));
        countryTextField.setText(getAddressPart(venuePlaceDetails.getAddress(), 2));
        stateComboBox.setVisible(false);
        stateLabel.setVisible(false);
        String stateZipStr = getAddressPart(venuePlaceDetails.getAddress(), 2);
        stateSuggestTextField.setText(getSpaceSplit(stateZipStr, 0));
        stateSuggestTextField.setVisible(true);
        stateSuggestLabel.setVisible(true);
    }

    public void saveVenue() {
        try {
            dateString = dateTextField.getText();
            time = timeComboBox.getSelectedItem().toString();
            contact = contactTextField.getText();
            venue = upperCase(venueTextField.getText());
            address = upperCase(addressTextField.getText());
            city = upperCase(cityTextField.getText());

            if (stateSuggestTextField.equals(null)){
                state = stateComboBox.getSelectedItem().toString();
            } else {
                state = stateSuggestTextField.getText();
            }

            priceString = priceTextField.getText();
            if (!priceString.equals("")) {
                price = Integer.parseInt(priceString);
            }
            ticketFeeString = ticketFeeTextField.getText();
            if (!ticketFeeString.equals("")) {
                ticketFee = Integer.parseInt(ticketFeeString);
            }
            merchFeeString = merchFeeTextField.getText();
            if (!merchFeeString.equals("")) {
                merchFee = Integer.parseInt(merchFeeString);
            }
            otherFeeString = otherFeeTextField.getText();
            if (!otherFeeString.equals("")) {
                otherFee = Integer.parseInt(otherFeeString);
            }

            try {

                try {
                    Date date = format.parse(dateString + time);
                    Show newShow = new Show(date, time, venue, address, city, state,
                            contact, sold, price, ticketFee, merchFee, otherFee);
                    addShow(TourCatGUI.showsModel, newShow);
                    TourCatGUI.setNoShows(false);
                    saved = true;
                } catch (ParseException pe) {
                    missingField();
                    System.out.println("Error parsing date");
                    pe.printStackTrace();
                }
                TourCatGUI.setFirstRun(false);

            } catch (ArrayIndexOutOfBoundsException aiob) {
                aiob.printStackTrace();
                System.out.println("Didn't enter something");
            }
        } catch (ArrayIndexOutOfBoundsException aiob) {
            missingField();
        }

    }

//    public void saveShowDb() {
//        dateString = dateTextField.getText();
//        try {
//            Date date = format.parse(dateString + time);
//        } catch (ParseException pe) {
//            System.out.println("Error parsing date");
//        }
//        time = timeComboBox.getSelectedItem().toString();
//        contact = contactTextField.getText();
//        venue = upperCase(venueTextField.getText());
//        address = upperCase(addressTextField.getText());
//        city = upperCase(cityTextField.getText());
//
//        if (stateSuggestTextField.equals(null)){
//            state = stateComboBox.getSelectedItem().toString();
//        } else {
//            state = stateSuggestTextField.getText();
//        }
//
//        if (!countryTextField.equals("")) {
//            country = countryTextField.getText().toString();
//        } else {
//            country = "";
//        }
//
//        soldString = soldTicketsTextField.getText();
//        if (!soldString.equals("")) {
//            sold = Integer.parseInt(soldString);
//        } else {
//            price = 0;
//        }
//
//        priceString = priceTextField.getText();
//        if (!priceString.equals("")) {
//            price = Integer.parseInt(priceString);
//        } else {
//            price = 0;
//        }
//        ticketFeeString = ticketFeeTextField.getText();
//        if (!ticketFeeString.equals("")) {
//            ticketFee = Integer.parseInt(ticketFeeString);
//        }
//        merchFeeString = merchFeeTextField.getText();
//        if (!merchFeeString.equals("")) {
//            merchFee = Integer.parseInt(merchFeeString);
//        }
//        otherFeeString = otherFeeTextField.getText();
//        if (!otherFeeString.equals("")) {
//            otherFee = Integer.parseInt(otherFeeString);
//        }
//
//        Statement statement = null;
//        Connection conn = null;
//        ResultSet rs = null;
//        PreparedStatement psInsert = null;
//
//        try {
//            java.sql.Date date = new java.sql.Date(format.getCalendar().getTimeInMillis());
//            Class.forName(driver);
//            conn = DriverManager.getConnection(protocol + dbName + ";create=true;", USER, PASS);
//            statement = conn.createStatement();
//
//            try {
//                String createShowsTable = "CREATE TABLE Shows (ShowDate DATE, VenueName VARCHAR(64), Street VARCHAR(64)," +
//                        "City VARCHAR(64), State VARCHAR(64), Country VARCHAR(64), SoldTickets INT, " +
//                        "Price INT, TicketFee INT, MerchFee INT, OtherFee INT)";
//                statement.executeUpdate(createShowsTable);
//                System.out.println("Created Shows Table");
//            } catch (SQLException se) {
//                System.out.println("Shows table already exists");
//            }
//
//            String showInsert = "INSERT INTO Shows VALUES ( ? , ? , ? , ? ,? , ? , ? , ? , ?, ? , ? )";
//            psInsert = conn.prepareStatement(showInsert);
//
//            psInsert.setDate(1, date);
//            psInsert.setString(2, venue);
//            psInsert.setString(3, address);
//            psInsert.setString(4, city);
//            psInsert.setString(5, state);
//            psInsert.setString(6, country);
//            psInsert.setInt(7, sold);
//            psInsert.setInt(8, price);
//            psInsert.setInt(9, ticketFee);
//            psInsert.setInt(10, merchFee);
//            psInsert.setInt(11, otherFee);
//
//            psInsert.executeUpdate();
//
//
//            System.out.println("Shows in Database: \n");
//            String fetchMerch = "SELECT * FROM Shows";
//            rs = statement.executeQuery(fetchMerch);
//
//            while (rs.next()) {
//                String dateStr = rs.getString("ShowDate");
//                String nameStr = rs.getString("VenueName");
//                String streetStr = rs.getString("Street");
//                String cityStr = rs.getString("City");
//                String stateStr = rs.getString("State");
//                String countryStr = rs.getString("Country");
//                String soldStr = String.valueOf(sold);
//                String priceStr = String.valueOf(price);
//                String ticketFeeStr = String.valueOf(ticketFee);
//                String merchFeeStr = String.valueOf(merchFee);
//                String otherFeeStr = String.valueOf(otherFee);
//                System.out.println("Date: " + dateStr + "\nName: " + nameStr + "\nStreet: " + streetStr + "\nCity: " +
//                        cityStr + "\nState " + stateStr + "\nCountry: " + countryStr + "\nSold: " + soldStr + "\nPrice: " +
//                        priceStr + "\nTicket Fee: " + ticketFeeStr + "\nMerch Fee: " + merchFeeStr + "\nOther Fee: " + otherFeeStr);
//            }
//
//            conn.close();
//
//            psInsert.close();
//
//            dbSaved = true;
//
//            if (conn.isClosed()) {
//                System.out.println("conn closed");
//            }
//
//        } catch (ClassNotFoundException cnf) {
//            System.out.println("Class not found");
//        } catch (SQLException se) {
//            System.out.println("SQL Exception erro");
//            se.printStackTrace();
//        } finally {
//            try {
//                if (rs != null) {
//                    rs.close();
//                    System.out.println("Result set is closed");
//                }
//            } catch (SQLException se) {
//                se.printStackTrace();
//            }
//
//            try {
//                if (statement != null) {
//                    statement.close();
//                    System.out.println("Statement closed");
//                }
//            } catch (SQLException se) {
//                se.printStackTrace();
//            }
//
//            try {
//                if (psInsert != null) {
//                    psInsert.close();
//                    System.out.println("Prepared statement closed");
//                }
//            } catch (SQLException se) {
//                se.printStackTrace();
//            }
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
//            dispose();
//        }
//    }

    public void createShowTable() {

    }

    public void resetFields(LinkedList<Place> venuePlaces) {
        stateComboBox.setVisible(true);
        stateLabel.setVisible(true);
        stateSuggestTextField.setVisible(false);
        stateSuggestLabel.setVisible(false);

        suggestionsButton.setText("Search for Suggestions");
        saveAndAddButton.setText("Save and Add Another");
        saveAndExitButton.setText("Save and Exit");

        stateSuggestTextField.setText("");
        dateTextField.setText("");
        timeComboBox.setSelectedItem("");
        venueTextField.setText("");
        addressTextField.setText("");
        cityTextField.setText("");
        stateComboBox.setSelectedItem("");
        contactTextField.setText("");
        priceTextField.setText("");
        soldTicketsTextField.setText("");
        ticketFeeTextField.setText("");
        merchFeeTextField.setText("");
        otherFeeTextField.setText("");

        venuePlaces.clear();
    }

    private static void addShow(DefaultListModel<Show> shows, Show newShow) {
        shows.addElement(newShow);
    }

    public void missingField() {
        String[] reqFields = {dateString, time, venue, address, city, state};
        LinkedList<JLabel> reqLabels = new LinkedList<JLabel>();
        reqLabels.add(dateLabel);
        reqLabels.add(timeLabel);
        reqLabels.add(venueLabel);
        reqLabels.add(addressLabel);
        reqLabels.add(cityLabel);
        reqLabels.add(stateLabel);

        for (int i = 0; i < reqFields.length; i++) {
            if (reqFields[i].isEmpty() || reqFields[i].equals("")) {
                missingLabel.setVisible(true);
                reqLabels.get(i).setForeground(Color.red);
            }
        }
    }

    private static String getAddressPart(String address, int element) {
        List<String> elementList = Arrays.asList(address.split(", "));
        String elementItem = elementList.get(element);
        System.out.println(elementItem);
        return elementItem;
    }

    private static String getSpaceSplit(String phrase, int element) {
        List<String> wordList = Arrays.asList(phrase.split(" "));
        String wordItem = wordList.get(element);
        return wordItem;
    }

    public static Integer tryParse(String text) {
        try {
            return new Integer(text);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public static String tryString(String text) {
        try {
            return new String (text);
        } catch (ArrayIndexOutOfBoundsException aiob) {
            return "";
        }
    }

    public static String upperCase(String words) {
        StringBuffer res = new StringBuffer();

        String[] strArr = words.split(" ");
        for (String str : strArr) {
            char[] stringArray = str.trim().toCharArray();
            stringArray[0] = Character.toUpperCase(stringArray[0]);
            str = new String(stringArray);

            res.append(str).append(" ");
        }

        return res.toString().trim();
    }

    public int getSuggestId() {
        return suggestId;
    }

    public void setSuggestId(int suggestId) {
        this.suggestId = suggestId;
    }
}
