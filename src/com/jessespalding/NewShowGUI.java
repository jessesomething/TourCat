package com.jessespalding;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Jesse on 4/28/2015.
 */
public class NewShowGUI extends JFrame {
    private JPanel newShowPanel;
    private JLabel dateField;
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

    public NewShowGUI() throws IOException {
        super("TourCat Shows");
        setContentPane(newShowPanel);
        pack();
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setVisible(true);

        String[] states = new String[] {"Alabama", "Alaska", "Arizona", "Arkansas", "California", "Colorado", "Connecticut", "Delaware",
                "District of Columbia", "Florida", "Georgia", "Hawaii", "Idaho", "Illinois", "Indiana", "Iowa",
                "Kansas", "Kentucky", "Louisiana", "Maine", "Maryland", "Massachusetts", "Michigan", "Minnesota",
                "Mississippi", "Missouri", "Montana", "Nebraska", "Nevada", "New Hampshire", "New Jersey", "New Mexico",
                "New York", "North Carolina", "North Dakota", "Ohio", "Oklahoma", "Oregon", "Pennsylvania", "Rhode Island",
                "South Carolina", "South Dakota", "Tennessee", "Texas", "Utah", "Vermont", "Virginia", "Washington",
                "West Virginia", "Wisconsin", "Wyoming" };

        String[] times = new String[] {"6:00AM", "6:30AM", "7:00AM", "7:30AM", "8:00AM", "8:30AM", "9:00AM", "9:30AM", "10:00AM", "10:30AM", "11:00AM", "11:30AM",
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
            @Override
            public void actionPerformed(ActionEvent e) {
                String dateString = dateTextField.getText();
                DateFormat format = new SimpleDateFormat("mm/dd/yyyy", Locale.ENGLISH);
                String time = timeComboBox.getSelectedItem().toString();
                String venue = upperCase(venueTextField.getText());
                String address = upperCase(addressTextField.getText());
                String city = upperCase(cityTextField.getText());
                String state = stateComboBox.getSelectedItem().toString();
                String contact = contactTextField.getText();
                String priceString = priceTextField.getText();
                int price = Integer.parseInt(priceString);
                String soldString = soldTicketsTextField.getText();
                int sold = Integer.parseInt(soldString);
                String ticketFeeString = ticketFeeTextField.getText();
                int ticketFee = Integer.parseInt(ticketFeeString);
                String merchFeeString = merchFeeTextField.getText();
                int merchFee = Integer.parseInt(merchFeeString);
                String otherFeeString = otherFeeTextField.getText();
                int otherFee = Integer.parseInt(otherFeeString);

                try {
                    Date date = format.parse(dateString);
                    Show newShow = new Show(date, time, venue, address, city, state,
                            contact, sold, price, ticketFee, merchFee, otherFee);
                    addShow(TourCatGUI.showsModel, newShow);
                    TourCatGUI.setNoShows(false);
                } catch (ParseException pe) {
                    System.out.println("Error parsing date");
                }
                TourCatGUI.setFirstRun(false);
                dispose();
            }
        });
    }

    private static void addShow(DefaultListModel<Show> shows, Show newShow) {
        shows.addElement(newShow);
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
}
