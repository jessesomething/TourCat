package com.jessespalding;

import se.walkercrou.places.GooglePlaces;
import se.walkercrou.places.Place;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

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


    public TourCatGUI() throws IOException {
        super("TourCat Shows");
        setContentPane(rootPanel);
        pack();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);

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
        });

        GooglePlaces places = new GooglePlaces("AIzaSyDkzkGyuOsBH7f0zszPFz2htLciSoc0Yjs");

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
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
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
            iob.printStackTrace();
            System.out.println("No more shows do display");
            String nextDate = "None Scheduled";
            String nextVenueName = "";
            TourCatGUI.this.nextShowDateLabel.setText(nextDate);
            TourCatGUI.this.nextShowVenueLabel.setText(nextVenueName);
        }

        revalidate();
        repaint();


        // url address creator
//        String placeUrl = "https://maps.googleapis.com/maps/api/place/textsearch/json?query=" + plusSeparate(venueName) +
//                "+" + searchAddress(venueName, venueAddress, venueCity, venueState) + "&type=establishment" +
//                "&key=" + "AIzaSyDkzkGyuOsBH7f0zszPFz2htLciSoc0Yjs";
//        System.out.println(placeUrl);



//        String streetPlus = venueAddress.replace(' ', '+');
//        String cityPlus = venueCity.replace(' ', '+');
//        String statePlus = venueState.replace(' ', '+');
//        String venueFullAddress = streetPlus + "+" + cityPlus + "+" + statePlus;
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
