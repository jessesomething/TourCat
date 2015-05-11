package com.jessespalding;

import javax.swing.*;
import java.awt.event.*;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jesse on 5/3/2015.
 */
public class MerchGUI extends JFrame {
    private static String driver = "org.apache.derby.jdbc.EmbeddedDriver";
    private static String protocol = "jdbc:derby:";
    private static String dbName = "SalesDB";

    private static final String USER = "username";
    private static final String PASS = "password";

    private JPanel merchPanel;
    private JComboBox merchTypeComboBox;
    private JComboBox merchKindComboBox;
    private JTextField merchDescTextField;
    private JButton saveAndExitButton;
    private JButton saveAndAddButton;
    private JComboBox merchSizeComboBox;
    private JTextField merchQtyTextField;
    private JLabel sizeLabel;
    private JTextField merchPriceTextField;
    private JLabel otherKindLabel;
    private JTextField otherKindTextField;

    public MerchGUI() throws IOException {
        super("Add/Edit Merchandise");
        setContentPane(merchPanel);
        pack();
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setVisible(true);


        final String[] merchTypes = new String[] {"Music", "Apparel", "Art/Media", "Other"};
        final String[] merchMusic = new String[] {"Vinyl LP", "Vinyl EP", "7in Vinyl", "CD", "Cassette", "Other"};
        final String[] merchApparel = new String[] {"T-Shirt", "Long Sleeve", "Zip-up Hoodie", "Sweatshirt", "Accessory", "Jewelry", "Other"};
        final String[] merchArt = new String[] {"Poster", "Original Print", "Painting", "Drawing", "Book", "DVD", "VHS Tape", "Other"};
//        final String[] merchOther = new String[] {"Add Item"};

        final List<String> merchOtherList = new ArrayList<String>();
        merchOtherList.add("Add Item");

        final List<String> merchSizesList = new ArrayList<String>();
        merchSizesList.add("None");
        merchSizesList.add("Add Size");
        merchSizesList.add("X-Small");
        merchSizesList.add("Small");
        merchSizesList.add("Medium");
        merchSizesList.add("Large");
        merchSizesList.add("X-Large");


//        final String[] merchSize = new String[] {"None", "X-Small", "Small", "Medium", "Large", "X-Large"};

        final String addItem = "";

        for (int i = 0; i < merchTypes.length; i++) {
            merchTypeComboBox.addItem(merchTypes[i]);
        }

        for (int i = 0; i < merchMusic.length; i++) {
            merchKindComboBox.addItem(merchMusic[i]);
        }

        otherKindLabel.setVisible(false);
        otherKindTextField.setVisible(false);

        merchSizeComboBox.setVisible(false);
        sizeLabel.setVisible(false);

        merchTypeComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (merchKindComboBox.getItemCount() > 0) {
                    merchKindComboBox.removeAllItems();
                }
                final String merchTypeSelected = merchTypeComboBox.getSelectedItem().toString();
                if (merchTypeSelected.toString().equals("Music")) {
                    for (int i = 0; i < merchMusic.length; i++) {
                        merchKindComboBox.addItem(merchMusic[i]);
                    }
                    revalidate();
                    repaint();
                }
                if (merchTypeSelected.toString().equals("Apparel")) {
                    for (int i = 0; i < merchApparel.length; i++) {
                        merchKindComboBox.addItem(merchApparel[i]);
                    }
                }
                if (merchTypeSelected.toString().equals("Art/Media")) {
                    for (int i = 0; i < merchArt.length; i++) {
                        merchKindComboBox.addItem(merchArt[i]);
                    }
                    revalidate();
                    repaint();
                }
                if (merchTypeSelected.toString().equals("Other")) {
//                    merchKindComboBox.removeAllItems();
                    for (int i = 0; i < merchOtherList.size(); i++) {
                        merchKindComboBox.addItem(merchOtherList.get(i));
                    }
                    otherKindLabel.setVisible(true);
                    otherKindTextField.setVisible(true);
                    revalidate();
                    repaint();
                }
                revalidate();
                repaint();
            }
        });

        merchKindComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                final String merchTypeSelected = merchTypeComboBox.getSelectedItem().toString();
                if (merchKindComboBox.getItemCount() > 0) {
                    merchSizeComboBox.removeAllItems();
                    final String merchKindSelected = merchKindComboBox.getSelectedItem().toString();
                    if (merchTypeSelected.equals("Other") || merchKindSelected.equals("T-Shirt") ||
                            merchKindSelected.equals("Zip-up Hoodie") || merchKindSelected.equals("Sweatshirt") ||
                            merchKindSelected.equals("Long Sleeve") || merchKindSelected.equals("Other")) {
                        for (int i = 0; i < merchSizesList.size(); i++) {
                            merchSizeComboBox.addItem(merchSizesList.get(i).toString());
                        }
                        merchSizeComboBox.setVisible(true);
                        sizeLabel.setVisible(true);
                        revalidate();
                        repaint();
                    } else if (merchTypeSelected.equals("Other") && !merchKindSelected.equals("Add Item")) {
                        otherKindLabel.setVisible(false);
                        otherKindTextField.setVisible(false);
                    } else if (merchKindSelected.equals("Add Item")) {
                        otherKindLabel.setVisible(true);
                        otherKindTextField.setVisible(true);
                    } else {
                        merchSizeComboBox.setVisible(false);
                        sizeLabel.setVisible(false);
                    }
                }
            }
        });
        revalidate();
        repaint();

        saveAndExitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String merchType = merchTypeComboBox.getSelectedItem().toString();
                String merchKind = merchKindComboBox.getSelectedItem().toString();
                String merchKindText = otherKindTextField.getText();
                if (merchType.equals("Other") && !merchOtherList.contains(merchKindText)) {
                    merchOtherList.add(merchKindText);
                    merchKindComboBox.addItem(merchKindText);
                }
                String merchDesc = merchDescTextField.getText();
                String merchPriceStr = merchPriceTextField.getText();
                double merchPrice = Double.parseDouble(merchPriceStr);
                String merchQtyStr = merchQtyTextField.getText();
                int merchQty = Integer.parseInt(merchQtyStr);

                System.out.println(merchType + merchKind + merchDesc);

                Statement statement = null;
                Connection conn = null;
                ResultSet rs = null;
                PreparedStatement psInsert = null;

                try {
                    Class.forName(driver);
                    conn = DriverManager.getConnection(protocol + dbName + ";create=true;", USER, PASS);
                    statement = conn.createStatement();

//                    try {
//                        String createTableSQL = "CREATE TABLE Sales (SaleTypes VARCHAR(15)";
//                        statement.executeUpdate(createTableSQL);
//                    } catch (SQLException se) {
//                        System.out.println("Error creating table");
//                        se.printStackTrace();
//                    }

                    try {
                        String createMerchTable = "CREATE TABLE Merchandise (Types VARCHAR(15), Kinds VARCHAR(15), " +
                                "Sizes VARCHAR(8), Description VARCHAR(64), Price DOUBLE, Quantity int)";
                        statement.executeUpdate(createMerchTable);
                        System.out.println("Created Merch Table");
                    } catch (SQLException se) {
                        System.out.println("Merchandise table already exists");
                    }

                    try {
                        String createSizeTable = "CREATE TABLE Sizes (Sizes VARCHAR(15))";
                        statement.executeUpdate(createSizeTable);
                        System.out.println("Created Sizes Table");
                    } catch (SQLException se) {
                        System.out.println("Sizes table already exists");
                    }

                    String merchInsert = "INSERT INTO Merchandise VALUES ( ? , ? , ? , ? , ?, ? )";
                    psInsert = conn.prepareStatement(merchInsert);

                    psInsert.setString(1, merchType);
                    psInsert.setString(2, merchKind);
                    try {
                        String merchSizeStr = merchSizeComboBox.getSelectedItem().toString();
                        psInsert.setString(3, merchSizeStr);
                    } catch (NullPointerException npe) {
                        String merchSizeStr = "";
                        psInsert.setString(3, merchSizeStr);
                    }
                    psInsert.setString(4, merchDesc);
                    psInsert.setDouble(5, merchPrice);
                    psInsert.setInt(6, merchQty);

                    psInsert.executeUpdate();


                    System.out.println("Sales Items in Database: \n");
                    String fetchMerch = "SELECT * FROM Merchandise";
                    rs = statement.executeQuery(fetchMerch);

                    while (rs.next()) {
                        String types = rs.getString("Types");
                        String kinds = rs.getString("Kinds");
                        String sizes = rs.getString("Sizes");
                        String desc = rs.getString("Description");
                        int qty = rs.getInt("Quantity");
                        System.out.println("Type: " + types + "\nKind: " + kinds + "\nSize: " +
                                sizes + "\nDesc: " + desc + "\nPrice: $" + merchPrice + "\nQuantity: " + qty);
                    }

                    String sizeInsert = "INSERT INTO Sizes VALUES ( ? )";
                    psInsert = conn.prepareStatement(sizeInsert);

                    String fetchSizes = "SELECT Sizes FROM Merchandise";
                    rs = statement.executeQuery(fetchSizes);

                    System.out.println("Sizes in the database: \n");

                    while (rs.next()) {
                        String sizes = rs.getString("Sizes");
                        System.out.println("Size: " + sizes);
                    }

                    rs.close();

                    conn.close();

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
                    dispose();
                }
            }
        });
    }
}
