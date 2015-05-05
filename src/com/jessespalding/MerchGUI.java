package com.jessespalding;

import javax.swing.*;
import java.awt.event.*;
import java.io.IOException;
import java.sql.*;

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

    public MerchGUI() throws IOException {
        super("Add/Edit Merchandise");
        setContentPane(merchPanel);
        pack();
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setVisible(true);


        String[] merchTypes = new String[] {"Music", "Apparel", "Art/Media", "Other"};
        final String[] merchMusic = new String[] {"Vinyl LP", "Vinyl EP", "7in Vinyl", "CD", "Cassette", "Other"};
        final String[] merchApparel = new String[] {"T-Shirt", "Long Sleeve", "Zip-up Hoodie", "Sweatshirt", "Accessory", "Jewelry", "Other"};
        final String[] merchArt = new String[] {"Poster", "Original Print", "Painting", "Drawing", "Book", "DVD", "VHS Tape", "Other"};
        final String[] merchOther = new String[] {""};

        final String[] merchSize = new String[] {"X-Small", "Small", "Medium", "Large", "X-Large"};

        for (int i = 0; i < merchTypes.length; i++) {
            merchTypeComboBox.addItem(merchTypes[i]);
        }

        for (int i = 0; i < merchMusic.length; i++) {
            merchKindComboBox.addItem(merchMusic[i]);
        }

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
                    for (int i = 0; i < merchOther.length; i++) {
                        merchKindComboBox.addItem(merchOther[i]);
                    }
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
                if (merchKindComboBox.getItemCount() > 0) {
                    merchSizeComboBox.removeAllItems();
                    final String merchKindSelected = merchKindComboBox.getSelectedItem().toString();
                    if (merchKindSelected.equals("T-Shirt") || merchKindSelected.equals("Zip-up Hoodie") ||
                            merchKindSelected.equals("Sweatshirt") || merchKindSelected.equals("Long Sleeve") ||
                            merchKindSelected.equals("Other")) {
                        for (int i = 0; i < merchSize.length; i++) {
                            merchSizeComboBox.addItem(merchSize[i]);
                        }
                        merchSizeComboBox.setVisible(true);
                        sizeLabel.setVisible(true);
                        revalidate();
                        repaint();
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
                String merchDesc = merchDescTextField.getText();
                String merchPriceStr = merchPriceTextField.getText();
                double merchPrice = Double.parseDouble(merchPriceStr);
                String merchQtyStr = merchQtyTextField.getText();
                int merchQty = Integer.parseInt(merchQtyStr);

                System.out.println(merchType + merchKind + merchDesc);

                Statement statement = null;
                Connection conn= null;
                ResultSet rs = null;
                PreparedStatement psInsert = null;

                try {
                    Class.forName(driver);
                    conn = DriverManager.getConnection(protocol + dbName + ";create=true", USER, PASS);
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
                    } catch (SQLException se) {
                        System.out.println("Error creating Merchandise table");
                        se.printStackTrace();
                    }

                    String prepareInsert = "INSERT INTO Merchandise VALUES ( ? , ? , ? , ? , ?, ? )";
                    psInsert = conn.prepareStatement(prepareInsert);

                    psInsert.setString(1, merchType);
                    psInsert.setString(2, merchKind);
                    try {
                        String merchSizeStr = merchSizeComboBox.getSelectedItem().toString();
                        psInsert.setString(3, merchSizeStr);
                    } catch (NullPointerException npe) {
                        String merchSizeStr = "";
                        psInsert.setString(3, merchSizeStr);
                    }

//                    psInsert.setString(3, merchSizeStr);
                    psInsert.setString(4, merchDesc);
                    psInsert.setDouble(5, merchPrice);
                    psInsert.setInt(6, merchQty);

                    psInsert.executeUpdate();
                    psInsert.closeOnCompletion();

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
                        sizes + "\nDesc: " + merchDesc + "\nPrice: $" + merchPrice + "\nQuantity: " + qty);
                    }

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

                }
            }
        });
    }
}
