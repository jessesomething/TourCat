package com.jessespalding;

import se.walkercrou.places.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
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
    private JButton searchForSuggestionsButton;
    private JTextField stateSuggestTextField;
    private JLabel stateSuggestLabel;

    private boolean missingField = false;

    private int suggestId = 0;
    private boolean isCorrect = false;

    public boolean isSaved() {
        return saved;
    }

    public void setSaved(boolean saved) {
        this.saved = saved;
    }

    private boolean saved = false;


    public NewShowGUI() throws IOException {
        super("TourCat Shows");
        setContentPane(newShowPanel);
        pack();
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setVisible(true);

        missingLabel.setVisible(false);
        missingLabel.setText("* Required fields");
        missingLabel.setForeground(Color.red);

        stateSuggestTextField.setVisible(false);
        stateSuggestLabel.setVisible(false);

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

        saveAndExitButton.addActionListener(new ActionListener() {
//            @Override
            public void actionPerformed(ActionEvent e) {

                String dateString = dateTextField.getText();
                String time = timeComboBox.getSelectedItem().toString();
                String venueString = venueTextField.getText();
                String addressString = addressTextField.getText();
                String cityString = cityTextField.getText();
                String state = stateComboBox.getSelectedItem().toString();
                String contact = contactTextField.getText();
                String priceString = priceTextField.getText();

                try {
                    DateFormat format = new SimpleDateFormat("mm/dd/yyyy", Locale.ENGLISH);
                    String venue = upperCase(venueString);
                    String address = tryString(addressString);
                    String city = upperCase(cityString);
                    int price = tryParse(priceString);
                    String soldString = soldTicketsTextField.getText();
                    int sold = tryParse(soldString);
                    String ticketFeeString = ticketFeeTextField.getText();
                    int ticketFee = tryParse(ticketFeeString);
                    String merchFeeString = merchFeeTextField.getText();
                    int merchFee = tryParse(merchFeeString);
                    String otherFeeString = otherFeeTextField.getText();
                    int otherFee = tryParse(otherFeeString);

                    GooglePlaces client = new GooglePlaces("AIzaSyDkzkGyuOsBH7f0zszPFz2htLciSoc0Yjs");

                    String venueFull = venue + ", " + address + ", " + city + ", " + state;
                    System.out.println(venueFull);
                    List<Prediction> places = client.getQueryPredictions(venueFull, 2, Param.name("types").value("establishment"));

                    LinkedList<Place> venuePlaces = new LinkedList<Place>();

                    for (Prediction place : places) {
                        Place newPlace = place.getPlace();
                        if (newPlace.getName().contains(venue)) {
                            venuePlaces.add(newPlace);
                        }
                    }

                    suggestPlace(venuePlaces.get(suggestId));

//                    for (Place result : venuePlaces) {
//                        if (venuePlaces != null) {
//                            Place venuePlaceDetails = result.getDetails(); // sends a GET request for more details
//                            // Just an example of the amount of information at your disposal:
//                            venueTextField.setText(venuePlaceDetails.getPlaceId());
//                            addressTextField.setText(getAddressPart(venuePlaceDetails.getAddress(), 0));
//                            cityTextField.setText(getAddressPart(venuePlaceDetails.getAddress(), 1));
//                            stateComboBox.setVisible(false);
//                            stateSuggestionTextField.setText(getAddressPart(venuePlaceDetails.getAddress(), 2));
//                            venueTextField.setText(venuePlaceDetails.getName());
//                            System.out.println("Name: " + venuePlaceDetails.getName());
//                            System.out.println("Type: " + venuePlaceDetails.getTypes());
//                            System.out.println("International Phone: " + venuePlaceDetails.getInternationalPhoneNumber());
//                            System.out.println("Website: " + venuePlaceDetails.getWebsite());
//                            System.out.println("Always Opened: " + venuePlaceDetails.isAlwaysOpened());
//                            System.out.println("Status: " + venuePlaceDetails.getStatus());
//                            System.out.println("Google Place URL: " + venuePlaceDetails.getGoogleUrl());
//                            System.out.println("Price: " + venuePlaceDetails.getPrice());
//                            System.out.println("Address: " + venuePlaceDetails.getAddress());
//                            System.out.println("Vicinity: " + venuePlaceDetails.getVicinity());
//                            System.out.println("Reviews: " + venuePlaceDetails.getReviews().size());
//                            System.out.println("Hours:\n " + venuePlaceDetails.getHours());
//                        }
//                    }

                    searchForSuggestionsButton.setText("Next Result");
                    saveAndAddButton.setText("Start Over");
                    saveAndExitButton.setText("This is correct");

//                    if (isSaved()) {
//                    while (!isSaved()) {
//                        if (suggestId == venuePlaces.size()) {
//                            searchForSuggestionsButton.setText("No more results");
//                        }
//                        // Next suggestion button
//
//                    }
//                    searchForSuggestionsButton.addActionListener(new ActionListener() {
//                        @Override
//                        public void actionPerformed(ActionEvent e) {
//                            if (suggestId < venuePlaces.size()) {
//                                setSuggestId(suggestId++);
//                                suggestPlace(venuePlaces.get(suggestId));
//                            } else {
//                                searchForSuggestionsButton.setText("Don't you read?");
//                                while (true) {
//                                    searchForSuggestionsButton.addMouseListener(new MouseAdapter() {
//                                        @Override
//                                        public void mouseReleased(MouseEvent e) {
//                                            super.mouseReleased(e);
//                                            searchForSuggestionsButton.setText("No more results");
//                                        }
//                                    });
//                                    break;
//                                }
//                            }
//                        }
//                    });
//
//                    // Start over button
//                    saveAndAddButton.addActionListener(new ActionListener() {
//                        @Override
//                        public void actionPerformed(ActionEvent e) {
//                            saveAndAddButton.setText("Save and Add Another");
//                            searchForSuggestionsButton.setText("Search for Suggestions");
//                            saveAndExitButton.setText("Save and Exit");
//
//                            dateTextField.setText("");
//                            timeComboBox.setSelectedItem("");
//                            venueTextField.setText("");
//                            addressTextField.setText("");
//                            cityTextField.setText("");
//                            stateComboBox.setSelectedItem("");
//                            contactTextField.setText("");
//                            priceTextField.setText("");
//                            soldTicketsTextField.setText("");
//                            ticketFeeTextField.setText("");
//                            merchFeeTextField.setText("");
//                            otherFeeTextField.setText("");
//                        }
//                    });
//                    // Correct and exit button
//                    saveAndExitButton.addActionListener(new ActionListener() {
//                        @Override
//                        public void actionPerformed(ActionEvent e) {
//                            setSaved(true);
//                        }
//                    });
//
                    try {
                        if (state.isEmpty()) {
                            state = removeSpace(stateSuggestTextField.getText(), 0);
                        }
                        Date date = format.parse(dateString);
                        Show newShow = new Show(date, time, venue, address, city, state,
                                contact, sold, price, ticketFee, merchFee, otherFee);
                        addShow(TourCatGUI.showsModel, newShow);
                        TourCatGUI.setNoShows(false);
                    } catch (ParseException pe) {
                        System.out.println("Error parsing date");
                    }
                    TourCatGUI.setFirstRun(false);
//                        dispose();
//                    }

                } catch (ArrayIndexOutOfBoundsException aiob) {
                    String[] reqFields = {dateString, time, venueString, cityString, state};
                    LinkedList<JLabel> reqLabels = new LinkedList<JLabel>();
                    reqLabels.add(dateLabel);
                    reqLabels.add(timeLabel);
                    reqLabels.add(venueLabel);
                    reqLabels.add(cityLabel);

                    for (int i = 0; i < reqFields.length; i++) {
                        if (reqFields[i].isEmpty() || reqFields[i].equals("")) {
                            missingLabel.setVisible(true);
                            missingField = true;
                            reqLabels.get(i).setForeground(Color.green);
                        }
                    }

                    for (int i = 0; i < reqFields.length; i++) {
                        if (reqFields[i].isEmpty() || reqFields[i].equals("")) {
                            missingLabel.setVisible(true);
                            missingField = true;
                            reqLabels.get(i).setForeground(Color.red);
                        }
                    }
                    aiob.printStackTrace();
//                    if (dateString.isEmpty()) {
//                        dateLabel.setForeground(Color.red);
//                        System.out.println("No date entered");
//                    } else if (time.isEmpty()) {
//                        timeLabel.setForeground(Color.red);
//                        System.out.println("No time entered");
//                    } else if (venueString.isEmpty()) {
//                        venueLabel.setForeground(Color.red);
//                        System.out.println("No venue entered");
//                    } else if (cityString.isEmpty()) {
//                        cityLabel.setForeground(Color.red);
//                        System.out.println("No city entered");
//                    } else if (state.isEmpty()) {
//                        stateLabel.setForeground(Color.red);
//                        System.out.println("No state entered");
//                    }
                    System.out.println("Didn't enter something");
                }

            }
        });


    }

    public void suggestPlace(Place venuePlaces) {
        Place venuePlaceDetails = venuePlaces.getDetails(); // sends a GET request for more details
        // Just an example of the amount of information at your disposal:
        venueTextField.setText(venuePlaceDetails.getName());
        addressTextField.setText(getAddressPart(venuePlaceDetails.getAddress(), 0));
        cityTextField.setText(getAddressPart(venuePlaceDetails.getAddress(), 1));
        stateComboBox.setVisible(false);
        stateLabel.setVisible(false);
        stateSuggestTextField.setText(removeSpace(venuePlaceDetails.getAddress(), 2));
        stateSuggestTextField.setVisible(true);
        stateSuggestLabel.setVisible(true);
    }

    private static void addShow(DefaultListModel<Show> shows, Show newShow) {
        shows.addElement(newShow);
    }

    private static String getAddressPart(String address, int element) {
        List<String> elementList = Arrays.asList(address.split(", "));
        String elementItem = elementList.get(element);
        System.out.println(elementItem);
        return elementItem;
    }

    private static String removeSpace(String phrase, int element) {
        List<String> wordList = Arrays.asList(phrase.split(", "));
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

    private static String upperCase(String words) {
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
