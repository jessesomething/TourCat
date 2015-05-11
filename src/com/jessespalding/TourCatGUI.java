package com.jessespalding;

import se.walkercrou.places.GooglePlaces;
import se.walkercrou.places.Place;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

/**
 * Created by Jesse on 4/23/2015.
 */
public class TourCatGUI extends JFrame{
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
    static JFrame newEventFrame;

    private JList<Show> showJList;

    private int showId = 0;

    public static boolean firstRun = true;
    public static boolean noShows = true;

    public static void setFirstRun(boolean firstRun) {
        TourCatGUI.firstRun = firstRun;
    }

    static DefaultListModel<Show> showsModel = new DefaultListModel<Show>();

    GooglePlaces client = new GooglePlaces("AIzaSyDkzkGyuOsBH7f0zszPFz2htLciSoc0Yjs");

    private static String driver = "org.apache.derby.jdbc.EmbeddedDriver";
    private static String protocol = "jdbc:derby:";
    private static String dbName = "SalesDB";

    private static final String USER = "username";
    private static final String PASS = "password";

    public static Integer getMerchId() {
        return merchId;
    }

    public static void setMerchId(Integer merchId) {
        TourCatGUI.merchId = merchId;
    }

    private static Integer merchId = 0;

    private HashMap<String, String> merchList = new HashMap<String, String>();

    public TourCatGUI() throws IOException {
        super("TourCat Shows");
        setContentPane(rootPanel);
        pack();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);

        Statement statement = null;
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement psInsert = null;

        try {
            Class.forName(driver);
            conn = DriverManager.getConnection(protocol + dbName + ";create=true;", USER, PASS);
            statement = conn.createStatement();

            String fetchMerch = "SELECT * FROM Merchandise";
            rs = statement.executeQuery(fetchMerch);

            while (rs.next()) {
                String kinds = rs.getString("Kinds");
                String desc = rs.getString("Description");
                String size = rs.getString("Sizes");
                String price = rs.getString("Price");
                String priceShort = "";
                for (int i = 0; i < price.length(); i++) {
                    String letter = price.valueOf(i);
                    if (letter.equals(".")){
                    } else {
                        priceShort = priceShort + letter;
                    }
                }
                System.out.println(priceShort);
                int qty = rs.getInt("Quantity");
                merchComboBox.addItem(kinds + " - " + desc + " @ $" + price);
                sizeComboBox.addItem(size);
            }

        } catch (ClassNotFoundException cnf) {
            System.out.println("Class not found");
        } catch (SQLException se) {
            System.out.println("SQL Exception erro");
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
                if (psInsert != null) {
                    psInsert.close();
                    System.out.println("Prepared statement closed");
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

        addNewShowButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
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
                    MerchGUI merchGui = new MerchGUI();
                } catch (IOException ioe) {
                    System.out.println("IO Exception Error");
                }
            }
        });

        while (noShows) {
            // do nothing
            if (noShows = false) {
                displayShow(showId);
                break;
            }
        }

        refreshButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                refreshMain();
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

            }
        });

        rootPanel.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                super.focusGained(e);
                refreshMain();
            }
        });
    }

    private void createUIComponents() {
    }

    public void displayShow(int showId) {
        String date = showsModel.getElementAt(showId).getShowDate().toString();
        final String venueName = showsModel.getElementAt(showId).getVenueName().toString();
        final String venueAddress = showsModel.getElementAt(showId).getStreetName().toString();
        final String venueCity = showsModel.getElementAt(showId).getCityName().toString();
        final String venueState = showsModel.getElementAt(showId).getStateName().toString();
        int venueSoldInt = showsModel.getElementAt(showId).getSoldTickets();
        String venueSold = Integer.toString(venueSoldInt);
        TourCatGUI.this.venueDateLabel.setText(date);
        TourCatGUI.this.venueNameLabel.setText(venueName);
        TourCatGUI.this.venueCityLabel.setText(venueCity + ", " + venueState);
        TourCatGUI.this.soldTicketsLabel.setText("Tickets sold: " + venueSold);

        try {
            String nextDate = showsModel.getElementAt(showId+1).getShowDate().toString();
            String nextVenueName = showsModel.getElementAt(showId+1).getVenueName().toString();
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

        directionsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openDirections("https://maps.google.com?saddr=Current+Location&daddr=" + searchAddress(venueAddress, venueCity, venueState) +
                        "&type=establishment" + "&name=" + plusSeparate(venueName) + "&key=" + "AIzaSyDkzkGyuOsBH7f0zszPFz2htLciSoc0Yjs");
                System.out.println("https://maps.google.com?saddr=Current+Location&daddr=" + searchAddress(venueAddress, venueCity, venueState) +
                        "&type=establishment" + "&name=" + plusSeparate(venueName) + "&key=" + "AIzaSyDkzkGyuOsBH7f0zszPFz2htLciSoc0Yjs");
            }
        });
    }

    public void refreshMain() {
        if (noShows) {
            System.out.println("No shows in database");
        }

        try {
            String date = showsModel.getElementAt(showId).getShowDate().toString();
            final String venueName = showsModel.getElementAt(showId).getVenueName().toString();
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
            directionsButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    openDirections("https://maps.google.com?saddr=Current+Location&daddr=" + searchAddress(venueAddress, venueCity, venueState) +
                            "&type=establishment" + "&name=" + plusSeparate(venueName) + "&key=" + "AIzaSyDkzkGyuOsBH7f0zszPFz2htLciSoc0Yjs");
//                            openDirections("https://maps.google.com?saddr=Current+Location&daddr=" +
//                                    "&type=establishment" + "&name=" + plusSeparate(venueName) + "&key=" + "AIzaSyDkzkGyuOsBH7f0zszPFz2htLciSoc0Yjs");
                    System.out.println("https://maps.google.com?saddr=Current+Location&daddr=" + searchAddress(venueAddress, venueCity, venueState) +
                            "&type=establishment" + "&name=" + plusSeparate(venueName) + "&key=" + "AIzaSyDkzkGyuOsBH7f0zszPFz2htLciSoc0Yjs");
                }
            });
        } catch (IndexOutOfBoundsException iob) {
            System.out.println("No shows to display");
        }
    }

    public static String searchAddress(String street, String city, String state) {
        String streetPlus = street.replace(' ', '+');
        String cityPlus = city.replace(' ', '+');
        String statePlus = state.replace(' ', '+');
        String venueFullAddress = streetPlus + "+" + cityPlus + "+" + statePlus;
        return venueFullAddress;
    }

    public static String plusSeparate(String phrase) {
        String phrasePlus = phrase.replace(' ', '+');
        return phrasePlus;
    }

    public static void openDirections(String url) {
        try {
            Desktop.getDesktop().browse(new URL(url).toURI());
        } catch (Exception e) {
            e.printStackTrace();
        }
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

}
