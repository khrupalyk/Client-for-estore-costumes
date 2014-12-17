/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import classesdb.*;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.*;

/**
 *
 * @author insane
 */
public class MainFrame extends JFrame {

    private DataInputStream in = null;
    private DataOutputStream out = null;
    private Socket socket;
    private int role;
    private int shopId;
    private JTable table = null;
    private int countCurrentItem = 0;
    private Product currProduct;
    private double priceOrder = 0;
    private JLabel priceOrderLabel = new JLabel();

    public MainFrame(Socket socket, DataInputStream in, DataOutputStream out) throws Exception {
        this.socket = socket;
        this.in = in;
        this.out = out;

        setSize(850, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        role = in.readInt();

        createInterface();
        setVisible(true);
    }

    private void createInterface() throws Exception {
        JScrollPane scrollPane = null;
        DefaultTableModel model;
        JPanel topPanel = new JPanel(new FlowLayout());
        JPanel centerPanel = new JPanel();
        JPanel endPanel = null;

        topPanel.setPreferredSize(new Dimension(240, 100));

        File fileCfg = new File("settings.cfg");
        if (fileCfg.exists()) {
            BufferedReader reader = new BufferedReader(new FileReader(fileCfg));
            shopId = Integer.parseInt(reader.readLine());
            reader.close();
        } else {
            shopId = 1;
            fileCfg.createNewFile();
            FileWriter writer = new FileWriter(fileCfg);
            writer.write("1");
            writer.close();
        }
//        out.writeInt(shopId);

        switch (role) {
            case Constants.ADMINISTRATOR:
                JButton addProduct = new JButton("Додати товар");
                JButton findProduct = new JButton("Знайти товар");
                JButton addCategory = new JButton("Додати/Видалити тип");
                JButton shangeStatus = new JButton("Змінити статус");
                JButton deleteOrder = new JButton("Видалити замовлення");
                JButton showItemOrders = new JButton("Елементи замовлення");

                shangeStatus.setEnabled(false);
                deleteOrder.setEnabled(false);
                showItemOrders.setEnabled(false);

                addProduct.setPreferredSize(new Dimension(200, 20));
                findProduct.setPreferredSize(new Dimension(200, 20));
                addCategory.setPreferredSize(new Dimension(200, 20));
                shangeStatus.setPreferredSize(new Dimension(200, 20));
                deleteOrder.setPreferredSize(new Dimension(200, 20));
                showItemOrders.setPreferredSize(new Dimension(200, 20));

                showItemOrders.addActionListener((action) -> {
                    JDialog dialog = new JDialog();
                    dialog.setLayout(new FlowLayout());
                    dialog.setSize(400, 300);

                    Object[] awd = new Object[]{"№", "Тип", "Колір", "Ціна", "Розмір", "Кількість"};

                    DefaultTableModel dmodel = new DefaultTableModel(awd, 0);
                    try {
                        out.writeInt(Constants.GET_ITEM_ORDERS_BY_ORDER);
                        out.writeInt((Integer) table.getValueAt(table.getSelectedRow(), 0));
                        ObjectInputStream inObject = new ObjectInputStream(in);
                        ItemOrder orde = null;

                        while ((orde = (ItemOrder) inObject.readObject()) != null) {
                            dmodel.addRow(new Object[]{orde.getItemOrderId(), orde.getProduct().getType().getName(),
                                orde.getProduct().getColor(), orde.getProduct().getPrice(), orde.getProduct().getSize(),
                                orde.getCount()});
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    JTable itemOrderTable = new JTable(dmodel);
                    JScrollPane spane = new JScrollPane(itemOrderTable);
                    spane.setPreferredSize(new Dimension(390, 300));

                    dialog.add(spane);
                    dialog.setVisible(true);
                });

                Object objects[] = {"№", "Оформив", "Замовив", "Статус"};
                model = new DefaultTableModel(objects, 0);
                shangeStatus.addActionListener((action) -> {
                    JDialog dialog = new JDialog();
                    dialog.setSize(200, 300);
                    dialog.setLayout(new FlowLayout(FlowLayout.LEFT));
                    JButton save = new JButton("save");
                    JComboBox<String> statuses = new JComboBox<>(new String[]{"New", "Hangings"});
                    save.addActionListener((action2) -> {
                        int index = table.getSelectedRow();
                        try {
                            out.writeInt(Constants.UPDATE_ORDER_STATUS);
                            out.writeInt((Integer) table.getValueAt(index, 0));
                            out.writeUTF((String) statuses.getSelectedItem());

                            DefaultTableModel dm = (DefaultTableModel) table.getModel();
                            int rowCount = dm.getRowCount();
                            for (int i = rowCount - 1; i >= 0; i--) {
                                dm.removeRow(i);
                            }
                            out.writeInt(Constants.GET_ALL_ORDER);
                            ObjectInputStream inObject = new ObjectInputStream(in);
                            Order orde = null;

                            while ((orde = (Order) inObject.readObject()) != null) {
                                Object[] orderInfo = new Object[4];
                                orderInfo[0] = orde.getOrderId();
                                orderInfo[1] = orde.getUser().getFirstName() + " " + orde.getUser().getLastName();
                                orderInfo[2] = orde.getBuyer().getFirstName() + " " + orde.getBuyer().getLastName();
                                orderInfo[3] = orde.getStatus();
                                model.addRow(orderInfo);
                            }
                            dialog.dispose();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        } catch (ClassNotFoundException ex) {
                            ex.printStackTrace();
                        }
                    });

                    dialog.add(statuses);
                    dialog.add(save);
                    dialog.setVisible(true);
                });

                addCategory.addActionListener((action) -> {
                    try {
                        addCategory();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });

                findProduct.addActionListener((action) -> {
                    try {
                        findProduct();
                    } catch (Exception ex) {
                        ex.printStackTrace();

                    }
                });

                addProduct.addActionListener((action) -> {
                    try {
                        addProduct();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });

                deleteOrder.addActionListener((action) -> {
                    try {
                        out.writeInt(Constants.DELETE_ORDER);
                        out.writeInt((Integer) table.getValueAt(table.getSelectedRow(), 0));
                        ((DefaultTableModel) table.getModel()).removeRow(table.getSelectedRow());
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                });

                out.writeInt(Constants.GET_ALL_ORDER);
                ObjectInputStream inObject = new ObjectInputStream(in);
                Order orde = null;
                while ((orde = (Order) inObject.readObject()) != null) {
                    Object[] orderInfo = new Object[4];
                    orderInfo[0] = orde.getOrderId();
                    orderInfo[1] = orde.getUser().getFirstName() + " " + orde.getUser().getLastName();
                    orderInfo[2] = orde.getBuyer().getFirstName() + " " + orde.getBuyer().getLastName();
                    orderInfo[3] = orde.getStatus();
                    model.addRow(orderInfo);
                }

                table = new JTable(model);
                scrollPane = new JScrollPane(table);
                scrollPane.setPreferredSize(new Dimension(600, 350));

                table.getSelectionModel().addListSelectionListener((event) -> {
                    shangeStatus.setEnabled(true);
                    deleteOrder.setEnabled(true);
                    showItemOrders.setEnabled(true);
                });

                centerPanel.add(scrollPane);
                topPanel.add(addProduct);
                topPanel.add(addCategory);
                topPanel.add(findProduct);
                topPanel.add(shangeStatus);
                topPanel.add(deleteOrder);
                topPanel.add(showItemOrders);
                break;
            case Constants.CASHIER:
                findProduct = new JButton("Знайти товар");
                JButton dropItemOrder = new JButton("Видалити товар");
                JButton order = new JButton("Оформити замовлення");

                dropItemOrder.setEnabled(false);

                dropItemOrder.setPreferredSize(new Dimension(200, 20));
                findProduct.setPreferredSize(new Dimension(200, 20));
                order.setPreferredSize(new Dimension(200, 20));

                dropItemOrder.addActionListener((action) -> {
                    updateOrderPrice(-((Integer) table.getValueAt(table.getSelectedRow(), 5)));
                    ((DefaultTableModel) table.getModel()).removeRow(table.getSelectedRow());
                });

                findProduct.addActionListener((action) -> {
                    try {
                        findProduct();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });

                Object[] obj = {"#", "Тип", "Колір", "Розмір", "Кількість", "Ціна", "Магазин"};
                model = new DefaultTableModel(obj, 0) {
                    @Override
                    public boolean isCellEditable(int rowIndex, int columnIndex) {
                        return true;
                    }
                };

                order.addActionListener((action) -> {
                    addBuyers();
                });

                table = new JTable(model);

                table.getSelectionModel().addListSelectionListener((event) -> {
                    dropItemOrder.setEnabled(true);
                });

                scrollPane = new JScrollPane(table);
                scrollPane.setPreferredSize(new Dimension(600, 350));

                priceOrderLabel.setText("Разом: " + priceOrder);
                centerPanel.add(scrollPane);
                centerPanel.add(priceOrderLabel);
                topPanel.add(findProduct);
                topPanel.add(dropItemOrder);
                topPanel.add(order);
                break;
            case Constants.DIRECTOR:
                JButton addUser = new JButton("Додати користувача");
                JButton dropUser = new JButton("Видалити користувача");
                JButton changeUserRole = new JButton("Змінити роль");
                JButton addStore = new JButton("Додати магазин");
                JButton changeStore = new JButton("Змінити магазин");

                dropUser.setEnabled(false);
                changeUserRole.setEnabled(false);

                addUser.setPreferredSize(new Dimension(200, 20));
                dropUser.setPreferredSize(new Dimension(200, 20));
                changeUserRole.setPreferredSize(new Dimension(200, 20));
                addStore.setPreferredSize(new Dimension(200, 20));
                changeStore.setPreferredSize(new Dimension(200, 20));

                addStore.addActionListener((action) -> {
                    JDialog dialog = new JDialog();
                    dialog.setSize(200, 300);
                    dialog.setLayout(new FlowLayout(FlowLayout.LEFT));

                    JTextField address = new JTextField(15);
                    JTextField phone = new JTextField(15);
                    JButton add = new JButton("додати");

                    add.addActionListener((action2) -> {
                        try {
                            out.writeInt(Constants.ADD_SHOP);
                            out.writeUTF(address.getText());
                            out.writeUTF(phone.getText());
                            dialog.dispose();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });

                    dialog.add(new JLabel("Адреса:"));
                    dialog.add(address);
                    dialog.add(new JLabel("Телефон:"));
                    dialog.add(phone);
                    dialog.add(add);

                    dialog.setVisible(true);
                });

                changeStore.addActionListener((action) -> {
                    JDialog dialog = new JDialog();
                    dialog.setSize(200, 300);
                    dialog.setLayout(new FlowLayout(FlowLayout.LEFT));
                    JComboBox<String> stores = new JComboBox<>();
                    JButton change = new JButton("Оновити");
                    try {
                        out.writeInt(Constants.GET_ALL_SHOPS);
                        ObjectInputStream inObject1 = new ObjectInputStream(in);
                        Store store = null;
                        ArrayList<Store> arr = new ArrayList<>();
                        while ((store = (Store) inObject1.readObject()) != null) {
                            arr.add(store);
                            stores.addItem(store.getAddress());
                        }
                        File fileCfg1 = new File("settings.cfg");
                        change.addActionListener((action2) -> {
                            for (Store arr1 : arr) {
                                if (arr1.getAddress().equals((String) stores.getSelectedItem())) {
                                    try {

                                        fileCfg.createNewFile();
                                        FileWriter writer = new FileWriter(fileCfg);
                                        writer.write(arr1.getStoreId() + "");
                                        writer.close();

                                        JOptionPane.showMessageDialog(null, "Please, restart system!");
                                        dialog.dispose();
                                        break;
                                    } catch (Exception e) {
                                    }
                                    return;
                                }
                            }
                        });
                    } catch (IOException | ClassNotFoundException ex) {
                        ex.printStackTrace();
                    }

                    dialog.add(stores);
                    dialog.add(change);
                    dialog.setVisible(true);
                });

                Object[] obj1 = {"№", "Логін", "Імя", "Фамілія", "Роль користувача", "Кілк. оф. замовлень"};
                model = new DefaultTableModel(obj1, 0) {
                    @Override
                    public boolean isCellEditable(int rowIndex, int columnIndex) {
                        return false;
                    }
                };

                out.writeInt(Constants.GET_USERS_LIST);
                inObject = new ObjectInputStream(in);
                User user = null;
                while ((user = (User) inObject.readObject()) != null) {
                    model.addRow(user.getInfo());
                }

                JTable table1 = new JTable(model);
                table = table1;

                table1.getSelectionModel().addListSelectionListener((ListSelectionEvent event) -> {
                    dropUser.setEnabled(true);
                    changeUserRole.setEnabled(true);
                });

                dropUser.addActionListener((action) -> {
                    try {
                        int index = table1.getSelectedRow();
                        out.writeInt(Constants.DROP_USER_BY_ID);
                        out.writeInt((Integer) table1.getValueAt(index, 0));
                        ((DefaultTableModel) table1.getModel()).removeRow(index);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }

                });

                addUser.addActionListener((action) -> {
                    addUser();
                });

                changeUserRole.addActionListener((action) -> {
                    changeUserRole();
                });

                scrollPane = new JScrollPane(table1);
                scrollPane.setPreferredSize(new Dimension(600, 350));
                topPanel.add(addUser);
                topPanel.add(dropUser);
                topPanel.add(changeUserRole);

                topPanel.add(addStore);
                topPanel.add(changeStore);
                centerPanel.add(scrollPane);
                break;
        }
        JButton listStore = new JButton("Список магазинів");
        listStore.setPreferredSize(new Dimension(200, 20));

        listStore.addActionListener((action) -> {
            JButton dropStore;
            JDialog dialog = new JDialog();
            dialog.setSize(310, 320);
            dialog.setLayout(new FlowLayout());

            DefaultTableModel modelStores = new DefaultTableModel(new Object[]{"№", "Адреса", "Телефон"}, 0);
            try {
                out.writeInt(Constants.GET_ALL_SHOPS);
                ObjectInputStream inObject1 = new ObjectInputStream(in);
                Store store = null;
                while ((store = (Store) inObject1.readObject()) != null) {
                    modelStores.addRow(new Object[]{store.getStoreId(), store.getAddress(), store.getPhone()});
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
            JTable tableStores = new JTable(modelStores);
            JScrollPane paneForStores = new JScrollPane(tableStores);
            paneForStores.setPreferredSize(new Dimension(300, 250));

            dialog.add(paneForStores);
            if (role == Constants.DIRECTOR) {
                dropStore = new JButton("Видалити магазин");
                dropStore.setEnabled(false);
                tableStores.getSelectionModel().addListSelectionListener((event) -> {
                    dropStore.setEnabled(true);
                });

                dropStore.addActionListener((action2) -> {
                    int index = tableStores.getSelectedRow();
                    try {
                        out.writeInt(Constants.DROP_STORE);
                        out.writeInt((Integer) tableStores.getValueAt(index, 0));

                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    } finally {
                        dialog.dispose();
                        dropStore.setEnabled(false);
                    }
                });
                dialog.add(dropStore);
            }
            dialog.setVisible(true);
        });
        topPanel.add(listStore);
        add(topPanel, BorderLayout.WEST);
        add(centerPanel, BorderLayout.CENTER);

    }

    private void updateOrderPrice(double newPrice) {
        priceOrder = priceOrder + newPrice;
        priceOrderLabel.setText("Разом: " + priceOrder);
    }

    public void addUser() {
        JDialog dialog = new JDialog(this, "Новий користувач", true);
        dialog.setSize(400, 330);
        dialog.setLayout(new FlowLayout(FlowLayout.LEFT));

        JButton addButton = new JButton("Додати");
        JTextField login = new JTextField(15);
        JTextField fname = new JTextField(15);
        JTextField lname = new JTextField(15);
        JPasswordField password = new JPasswordField(15);
        JComboBox<String> roles = new JComboBox<>(new String[]{"Administrator", "Director", "Cashier"});
        JComboBox<String> stores = new JComboBox<>();
        roles.setPreferredSize(new Dimension(170,24));
        stores.setPreferredSize(new Dimension(170,24));
        addButton.setPreferredSize(new Dimension(170,24));
        
        ArrayList<Store> arr = new ArrayList<>();
        try {
            out.writeInt(Constants.GET_ALL_SHOPS);
            ObjectInputStream inObject1 = new ObjectInputStream(in);
            Store store = null;

            while ((store = (Store) inObject1.readObject()) != null) {
                arr.add(store);
                stores.addItem(store.getAddress());
            }
        } catch (Exception e) {
e.printStackTrace();
        }

        addButton.addActionListener((action) -> {
            try {
                out.writeInt(Constants.ADD_USER);
                User user = new User();
                user.setFirstName(fname.getText());
                user.setLastName(lname.getText());
                user.setLogin(login.getText());
                user.setPassword(password.getText());
                String str = (String) roles.getSelectedItem();
                user.setRole(user.getIntRole(str));

                for (Store arr1 : arr) {
                    if (arr1.getAddress().equals((String) stores.getSelectedItem())) {
                        user.setStoreId(arr1.getStoreId());
                    }
                }

                ObjectOutputStream outObject = new ObjectOutputStream(out);
                outObject.writeObject(user);
//                out.writeInt(Constants.GET_USERS_LIST);
                DefaultTableModel model = (DefaultTableModel) table.getModel();
                model.addRow(user.getInfo());
                dialog.dispose();
                JOptionPane.showMessageDialog(null, "Користувача усіпшно додано!", "Повідомлення", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        });

        GridLayout layout = new GridLayout(6, 2,5,5);
        JPanel mainPanel = new JPanel(layout);
        mainPanel.setBorder(BorderFactory.createTitledBorder("Введіть дані"));
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        mainPanel.setPreferredSize(new Dimension(380,200));
        leftPanel.setPreferredSize(new Dimension(180,200));
        rightPanel.setPreferredSize(new Dimension(180,200));
        
        mainPanel.add(new JLabel("Логін:"));
        mainPanel.add(login);
        mainPanel.add(new JLabel("Пароль:"));
        mainPanel.add(password);
        mainPanel.add(new JLabel("Імя:"));
        mainPanel.add(fname);
        mainPanel.add(new JLabel("Фамілія:"));
        mainPanel.add(lname);
        mainPanel.add(new JLabel("Тип облікового запису:"));
        mainPanel.add(roles);
        mainPanel.add(new JLabel("Магазин:"));

        mainPanel.add(stores);
        
        
//        mainPanel.add(rightPanel,BorderLayout.EAST);
//        mainPanel.add(leftPanel,BorderLayout.WEST);
        
        dialog.add(mainPanel);
        dialog.add(addButton);
        dialog.setVisible(true);
    }

    private void changeUserRole() {
        JDialog dialog = new JDialog(this, "", true);
        dialog.setSize(200, 300);
        dialog.setLayout(new FlowLayout(FlowLayout.LEFT));

        JButton save = new JButton("зберегти");
        JComboBox<String> roles = new JComboBox<>(new String[]{"Administrator", "Director", "Cashier"});
        dialog.add(roles);
        dialog.add(save);

        save.addActionListener((action) -> {
            try {
                out.writeInt(Constants.UPDATE_USER_ROLE);
                int index = table.getSelectedRow();
                int id = (Integer) table.getValueAt(index, 0);

                User u = new User();
                out.writeInt(u.getIntRole((String) roles.getSelectedItem()));
                out.writeInt(id);
                table.setValueAt(roles.getSelectedItem(), index, 4);
                dialog.dispose();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        });

        dialog.setVisible(true);
    }

    private void addCategory() throws Exception {
        JDialog dialog = new JDialog(this, "Додати категорію", true);
        dialog.setSize(200, 415);
        dialog.setLayout(new FlowLayout(FlowLayout.LEFT));

        JButton save = new JButton("Додати");
        JButton drop = new JButton("Видалити");
        drop.setEnabled(false);

        save.setPreferredSize(new Dimension(167, 20));
        drop.setPreferredSize(new Dimension(167, 20));

        JList<String> listCategory;
        JTextField nameType = new JTextField(15);
        JTextField discount = new JTextField(15);
        DefaultListModel listModel = new DefaultListModel();
        ArrayList<String> list = new ArrayList<>();
        ArrayList<classesdb.Type> typesArr = getTypes();
        typesArr.stream().map((typesArr1) -> {
            if (typesArr1.getParentId() == 0) {
                list.add(typesArr1.getName());
            }
            return typesArr1;
        }).forEach((typesArr1) -> {
            listModel.addElement(typesArr1.getName());
        });
        listCategory = new JList<>(listModel);

        drop.addActionListener((action) -> {
            try {
                out.writeInt(Constants.DROP_TYPE);
                out.writeUTF(listCategory.getSelectedValue());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            dialog.dispose();
        });

        listCategory.addListSelectionListener((laction) -> {
            drop.setEnabled(true);
        });

        String a[] = new String[list.size()];
        list.add(0, "none");
        JComboBox<String> types = new JComboBox<>(list.toArray(a));
        types.setPreferredSize(new Dimension(167, 20));

        save.addActionListener((action) -> {
            String newTypeStr = (String) types.getSelectedItem();
            classesdb.Type newType = new classesdb.Type();
            newType.setName(nameType.getText());
            newType.setDiscount(Integer.parseInt(discount.getText()));
            if (newTypeStr.equals("none")) {
                newType.setParentId(-1);
            } else {
                for (classesdb.Type a1 : typesArr) {
                    if (a1.getName().equals(newTypeStr)) {
                        newType.setParentId(a1.getTypeId());
                        break;
                    }
                }
            }
            ObjectOutputStream outObject;
            try {
                out.writeInt(Constants.ADD_TYPE_CATEGORY);
                outObject = new ObjectOutputStream(out);
                outObject.writeObject(newType);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            dialog.dispose();

        });

        JScrollPane spane = new JScrollPane(listCategory);
        spane.setPreferredSize(new Dimension(167, 200));

        dialog.add(spane);
        dialog.add(drop);
        dialog.add(new JLabel("Назва:            "));
        dialog.add(nameType);
        dialog.add(new JLabel("Знижка:"));
        dialog.add(discount);
        dialog.add(types);
        dialog.add(save);

        dialog.setVisible(true);
    }

    private ArrayList<classesdb.Type> getTypes() throws Exception {
        out.writeInt(Constants.GET_TYPE_CATEGORY);
        classesdb.Type type = null;
        ObjectInputStream inObject = new ObjectInputStream(in);
        ArrayList<classesdb.Type> typesArr = new ArrayList<>();
        while ((type = (classesdb.Type) inObject.readObject()) != null) {
            typesArr.add(type);
        }

        return typesArr;
    }

    private void addProduct() throws Exception {
        JDialog dialog = new JDialog(this, "Додати товар", true);
        dialog.setSize(200, 300);
        dialog.setLayout(new FlowLayout(FlowLayout.LEFT));

        JTextField color = new JTextField(15);
        JTextField size = new JTextField(15);
        JTextField price = new JTextField(15);
        JTextField count = new JTextField(15);
        JButton save = new JButton("зберегти");

        ArrayList<String> list = new ArrayList<>();
        ArrayList<classesdb.Type> typesArr = getTypes();
        for (classesdb.Type typesArr1 : typesArr) {
            if (typesArr1.getParentId() == 0) {
                list.add(typesArr1.getName());
            }
        }
        String a[] = new String[list.size()];
        JComboBox<String> types = new JComboBox<>(list.toArray(a));
        JComboBox<String> pypes = new JComboBox<>();

        types.setPreferredSize(new Dimension(170, 20));
        pypes.setPreferredSize(new Dimension(170, 20));
        pypes.setModel(getModel(types));
        types.addActionListener((action2) -> {
            try {
                pypes.setModel(getModel(types));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        save.setPreferredSize(new Dimension(170, 20));
        save.addActionListener((action) -> {
            Product product = new Product();
            product.setColor(color.getText());
            product.setPrice(Integer.parseInt(price.getText()));
            product.setSize(Integer.parseInt(size.getText()));
            ProductInShop pis = new ProductInShop();
            pis.setCount(Integer.parseInt(count.getText()));
            Store store = new Store();
            store.setStoreId(shopId);
            pis.setStore(store);
            product.getShops().add(pis);
            classesdb.Type t = new classesdb.Type();
            for (classesdb.Type a1 : typesArr) {
                if (a1.getName().equals((String) pypes.getSelectedItem())) {
                    t = a1;
                    break;
                }
            }
            product.setType(t);
            try {
                out.writeInt(Constants.ADD_PRODUCT);
                ObjectOutputStream outObject = new ObjectOutputStream(out);
                outObject.writeObject(product);
                dialog.dispose();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        dialog.add(new JLabel("Колір:"));
        dialog.add(color);
        dialog.add(new JLabel("Розмір:"));
        dialog.add(size);
        dialog.add(new JLabel("Ціна:"));
        dialog.add(price);
        dialog.add(new JLabel("Кількість:"));
        dialog.add(count);
        dialog.add(new JLabel("Тип:"));
        dialog.add(types);
        dialog.add(pypes);
        dialog.add(save);

        dialog.setVisible(true);
    }

    private DefaultComboBoxModel<String> getModel(JComboBox<String> types) throws Exception {
        ArrayList<classesdb.Type> typesArr1 = getTypes();
        DefaultComboBoxModel<String> modelList = new DefaultComboBoxModel<>();
        typesArr1.stream().forEach((typesArr11) -> {
            if (typesArr11.getName().equals((String) types.getSelectedItem())) {
                for (int i = 0; i < typesArr1.size(); i++) {
                    if (typesArr1.get(i).getParentId() == typesArr11.getTypeId()) {
                        modelList.addElement(typesArr1.get(i).getName());
                    }
                }
            }

        });
        return modelList;
    }

    private void findProduct() throws Exception {
        JDialog dialog = new JDialog();
        dialog.setSize(630, 400);
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel centerPanel = new JPanel();
        JPanel endPanel = new JPanel(new FlowLayout());

        JButton find = new JButton("знайти");
        JTextField price = new JTextField(15);
        JTextField size = new JTextField(15);

        ArrayList<String> list = new ArrayList<>();
        list.add("ALL");
        ArrayList<classesdb.Type> typesArr = getTypes();
        typesArr.stream().forEach((typesArr1) -> {
            if (typesArr1.getParentId() == 0) {
                list.add(typesArr1.getName());
            }
        });
        String a[] = new String[list.size()];
        JComboBox<String> maintypes = new JComboBox<>(list.toArray(a));
        JComboBox<String> pypes = new JComboBox<>();

        maintypes.addActionListener((action2) -> {
            try {
                ArrayList<classesdb.Type> typesArr1 = getTypes();
                DefaultComboBoxModel<String> modelList = new DefaultComboBoxModel<>();
                typesArr1.stream().forEach((typesArr11) -> {
                    if (typesArr11.getName().equals((String) maintypes.getSelectedItem())) {
                        for (int i = 0; i < typesArr1.size(); i++) {
                            if (typesArr1.get(i).getParentId() == typesArr11.getTypeId()) {
                                modelList.addElement(typesArr1.get(i).getName());
                            }
                        }
                    }

                });
                if (modelList.getSize() == 0) {
                    pypes.setEnabled(false);
                } else {
                    pypes.setEnabled(true);
                }

                pypes.setModel(modelList);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        JComboBox<String> colors = new JComboBox<>();

        maintypes.setPreferredSize(new Dimension(170, 20));
        pypes.setPreferredSize(new Dimension(170, 20));

        colors.setPreferredSize(new Dimension(170, 20));
        colors.addItem("ALL");
        topPanel.setPreferredSize(new Dimension(200, 50));
        endPanel.setPreferredSize(new Dimension(200, 50));
        centerPanel.setPreferredSize(new Dimension(200, 400));

        Object[] obj = {"№", "Тип", "Колір", "Ціна", "Розмір", "Магазин"};
        DefaultTableModel model = new DefaultTableModel(obj, 0) {
            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false;
            }
        };

        out.writeInt(Constants.GET_ALL_PRODUCT);
        ObjectInputStream inObject = new ObjectInputStream(in);
        ArrayList<Product> products = new ArrayList<>();
        Product product = null;
        while ((product = (Product) inObject.readObject()) != null) {
            products.add(product);
        }

        Set<String> colorsSet = new TreeSet<>();
        for (Product p : products) {
            p.getShops().stream().filter((s) -> (s.getStore().getStoreId() == shopId)).map((s) -> {
                Vector v = p.getInfo();
                v.add(s.getStore().getAddress());
                return v;
            }).forEach((v) -> {
                model.addRow(v);

            });
            colorsSet.add(p.getColor());

        }
        for (String colorsSet1 : colorsSet) {
            colors.addItem(colorsSet1);
        }
        JTable tableProduct = new JTable(model);
        JScrollPane pane = new JScrollPane(tableProduct);
        pane.setPreferredSize(new Dimension(400, 300));

        final JButton delete = new JButton("Видалити із магазину");
        final JButton deleteAll = new JButton("Видалити з усіх магазинів");
        final JButton update = new JButton("Оновити");
        final JButton addToOrder = new JButton("Додати до замовлення");

        delete.setEnabled(false);
        deleteAll.setEnabled(false);
        update.setEnabled(false);
        addToOrder.setEnabled(false);

        tableProduct.getSelectionModel().addListSelectionListener((event) -> {
            delete.setEnabled(true);
            deleteAll.setEnabled(true);
            update.setEnabled(true);
            addToOrder.setEnabled(true);
        });

        delete.addActionListener((action) -> {
            try {
                out.writeInt(Constants.DROP_PRODUCT_FROM_SHOP);
                int index = tableProduct.getSelectedRow();
                int id = (Integer) tableProduct.getValueAt(index, 0);
                out.writeInt(id);
                ((DefaultTableModel) tableProduct.getModel()).removeRow(index);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        deleteAll.addActionListener((action) -> {
            try {
                out.writeInt(Constants.DROP_PRODUCT_BY_ID);
                int index = tableProduct.getSelectedRow();
                int id = (Integer) tableProduct.getValueAt(index, 0);
                out.writeInt(id);
                ((DefaultTableModel) tableProduct.getModel()).removeRow(index);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        find.addActionListener((action) -> {
            try {

                String sizeS = size.getText();
                String priceS = price.getText();

                String type = (String) maintypes.getSelectedItem();
                String color = (String) colors.getSelectedItem();
                int sizeI = (sizeS.trim().equals("")) ? -1 : Integer.valueOf(sizeS);
                int priceI = (priceS.trim().equals("")) ? -1 : Integer.valueOf(priceS);

                Set<Product> result = new TreeSet<>((Product o1, Product o2) -> {
                    for (ProductInShop p : o1.getShops()) {
                        if (p.getStore().getStoreId() == shopId) {
                            return -1;
                        }
                    }
                    return 1;
                });
                if (!type.equals("ALL")) {
                    type = (String) pypes.getSelectedItem();
                }
                for (Product product1 : products) {
                    if ((type.equals("ALL") && color.equals("ALL")) && (sizeI == -1 && priceI == -1)) {
                        result.add(product1);
                    } else if (((product1.getColor().equals(color) && type.equals("ALL"))
                            || (product1.getColor().equals(color) && product1.getType().getName().equals(type))
                            || (color.equals("ALL") && product1.getType().getName().equals(type))) && (sizeI == -1 && priceI == -1)) {
                        result.add(product1);
                    } else if ((product1.getColor().equals(color) && type.equals("ALL"))
                            || (product1.getColor().equals(color) && product1.getType().getName().equals(type))
                            || (color.equals("ALL") && product1.getType().getName().equals(type))
                            && (sizeI != -1 || priceI != -1)) {
                        if (priceI != -1 && sizeI == -1) {
                            if (product1.getPrice() == priceI) {
                                result.add(product1);
                            }
                        } else if (priceI == -1 && sizeI != -1) {
                            if (product1.getSize() == sizeI) {
                                result.add(product1);
                            }
                        } else if (sizeI != -1 && priceI != -1) {
                            if (product1.getPrice() == priceI && product1.getSize() == sizeI) {
                                result.add(product1);
                            }
                        }

                    } else if ((type.equals("ALL") && color.equals("ALL")) && (sizeI != -1 || priceI != -1)) {
                        if (priceI != -1 && sizeI == -1) {
                            if (product1.getPrice() == priceI) {
                                result.add(product1);
                            }
                        } else if (priceI == -1 && sizeI != -1) {
                            if (product1.getSize() == sizeI) {
                                result.add(product1);
                            }
                        } else if (sizeI != -1 && priceI != -1) {
                            if (product1.getPrice() == priceI && product1.getSize() == sizeI) {
                                result.add(product1);
                            }
                        }
                    }

                }
                int rowCount = model.getRowCount();
                for (int i = 0; i < rowCount; i++) {
                    model.removeRow(0);
                }
                result.stream().forEach((result1) -> {
                    Vector v = result1.getInfo();
                    v.add("null");
                    for (ProductInShop s : result1.getShops()) {
                        v.set(v.size() - 1, s.getStore().getAddress());
                        model.addRow(v);
                    }
                });
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        update.addActionListener((action) -> {
            try {
                updateProduct(tableProduct, products);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        addToOrder.addActionListener((action) -> {
            JDialog dialogAddToOrder = new JDialog();
            dialogAddToOrder.setLayout(new FlowLayout());
            dialogAddToOrder.setSize(300, 200);
            JTextField count = new JTextField(10);
            JButton add = new JButton("додати");

            add.addActionListener((awd) -> {
                countCurrentItem = Integer.valueOf(count.getText());
                int index = tableProduct.getSelectedRow();
                ((DefaultTableModel) table.getModel()).addRow(new Object[]{tableProduct.getValueAt(index, 0), tableProduct.getValueAt(index, 1), tableProduct.getValueAt(index, 2),
                    tableProduct.getValueAt(index, 4), countCurrentItem, ((Integer) tableProduct.getValueAt(index, 3)) * countCurrentItem,
                    tableProduct.getValueAt(index, 5)});
                updateOrderPrice((Integer) tableProduct.getValueAt(index, 3) * countCurrentItem);
                dialogAddToOrder.dispose();
            });

            dialogAddToOrder.add(new JLabel("Кількість товару:"));
            dialogAddToOrder.add(count);
            dialogAddToOrder.add(add);
            dialogAddToOrder.setVisible(true);
        });

        switch (role) {
            case Constants.ADMINISTRATOR:
                endPanel.add(delete);
                endPanel.add(deleteAll);
                endPanel.add(update);
                break;
            case Constants.CASHIER:
                endPanel.add(addToOrder);
                break;
        }

        topPanel.add(new JLabel("Тип:"));
        topPanel.add(maintypes);
        topPanel.add(new JLabel("Модель:"));
        topPanel.add(pypes);
        topPanel.add(new JLabel("\nКолір:      "));
        topPanel.add(colors);
        topPanel.add(new JLabel("Ціна:"));
        topPanel.add(price);
        topPanel.add(new JLabel("Розмір:"));
        topPanel.add(size);
        topPanel.add(find);
        centerPanel.add(pane);
        dialog.add(topPanel, BorderLayout.WEST);
        dialog.add(centerPanel, BorderLayout.CENTER);
        dialog.add(endPanel, BorderLayout.PAGE_END);
        dialog.setVisible(true);
    }

    private void updateProduct(JTable table, ArrayList<Product> products) throws Exception {
        JDialog dialog = new JDialog(this, "Оновити товар", true);
        dialog.setSize(200, 300);
        dialog.setLayout(new FlowLayout(FlowLayout.LEFT));
        int index = table.getSelectedRow();
//        JTextField name = new JTextField(15);
        JTextField color = new JTextField(15);
        color.setText((String) table.getValueAt(index, 2));
        JTextField size = new JTextField(15);
        size.setText(String.valueOf(table.getValueAt(index, 4)));
        JTextField price = new JTextField(15);
        price.setText(String.valueOf(table.getValueAt(index, 3)));
        JTextField count = new JTextField(15);
        int pId = 0;
        for (Product product : products) {
            if (product.getProductid() == (Integer) (table.getValueAt(index, 0))) {
                count.setText("" + getCountProductInCurrentStore(product));
                pId = product.getProductid();
                break;
            }
        }
        color.setText((String) table.getValueAt(index, 2));
        JButton save = new JButton("зберегти");

        ArrayList<String> list = new ArrayList<>();
        ArrayList<classesdb.Type> typesArr = getTypes();
        for (classesdb.Type typesArr1 : typesArr) {
            if (typesArr1.getParentId() == 0) {
                list.add(typesArr1.getName());
            }
        }
        String a[] = new String[list.size()];

        JComboBox<String> types = new JComboBox<>(list.toArray(a));
        JComboBox<String> pypes = new JComboBox<>();

        types.setPreferredSize(new Dimension(170, 20));
        pypes.setPreferredSize(new Dimension(170, 20));
        pypes.setModel(getModel(types));
        types.addActionListener((action2) -> {
            try {
                pypes.setModel(getModel(types));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        pypes.setSelectedItem((String) table.getValueAt(index, 1));

        ArrayList<classesdb.Type> typesArr1 = getTypes();
        typesArr1.stream().forEach((typesArr11) -> {
            if (typesArr11.getName().equals((String) pypes.getSelectedItem())) {
                for (int i = 0; i < typesArr1.size(); i++) {
                    if (typesArr1.get(i).getParentId() == typesArr11.getTypeId()) {
                        types.setSelectedItem((String) typesArr11.getName());
                        break;
                    }
                }
            }

        });

        int aa = pId;
        save.addActionListener((action) -> {

            Product product = new Product();
            product.setProductid(aa);
            product.setColor(color.getText());
            product.setPrice(Integer.parseInt(price.getText()));
            product.setSize(Integer.parseInt(size.getText()));
            ProductInShop pis = new ProductInShop();
            pis.setCount(Integer.parseInt(count.getText()));
            Store store = new Store();
            store.setStoreId(shopId);
            pis.setStore(store);
            product.getShops().add(pis);
            classesdb.Type t = new classesdb.Type();
            for (classesdb.Type a1 : typesArr) {
                if (a1.getName().equals((String) pypes.getSelectedItem())) {
                    t = a1;
                    break;
                }
            }
            product.setType(t);
            try {
                out.writeInt(Constants.UPDATE_PRODUCT);
                ObjectOutputStream outObject = new ObjectOutputStream(out);
                outObject.writeObject(product);
                dialog.dispose();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        dialog.add(new JLabel("Колір:"));
        dialog.add(color);
        dialog.add(new JLabel("Розмір:"));
        dialog.add(size);
        dialog.add(new JLabel("Ціна:"));
        dialog.add(price);
        dialog.add(new JLabel("Кількість:"));
        dialog.add(count);
        dialog.add(new JLabel("Тип:"));
        dialog.add(types);
        dialog.add(new JLabel("Модель:"));
        dialog.add(pypes);
        dialog.add(save);

        dialog.setVisible(true);
    }

    private int getCountProductInCurrentStore(Product p) {
        for (ProductInShop pis : p.getShops()) {
            if (pis.getStore().getStoreId() == shopId) {
                return pis.getCount();
            }
        }
        return -1;
    }

    private void addBuyers() {
        try {
            JDialog dialog = new JDialog();
            dialog.setSize(300, 450);
            dialog.setLayout(new FlowLayout(FlowLayout.LEFT));

            JTextField fname = new JTextField(15);
            JTextField lname = new JTextField(15);
            JTextField phone = new JTextField(15);
            JButton addBuyer = new JButton("Додати покупця");
            JButton order = new JButton("Оформити замовлення");
            addBuyer.setPreferredSize(new Dimension(170,24));
            order.setPreferredSize(new Dimension(170,24));

            addBuyer.addActionListener((action) -> {
                try {
                    Buyers user = new Buyers(0, fname.getText(), lname.getText(), phone.getText());
                    out.writeInt(Constants.ADD_BUYERS);
                    ObjectOutputStream outObject = new ObjectOutputStream(out);
                    outObject.writeObject(user);
                    dialog.dispose();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });

            Object[] arr = {"№", "Імя", "Фамілія", "Телефон", "Знижка"};
            JComboBox<String> statuses = new JComboBox<>(new String[]{"New", "Hangings"});
            statuses.setPreferredSize(new Dimension(170,24));
            DefaultTableModel model = new DefaultTableModel(arr, 0);

            out.writeInt(Constants.GET_ALL_BUYERS);
            ObjectInputStream inObject = new ObjectInputStream(in);
            Buyers user = null;
            while ((user = (Buyers) inObject.readObject()) != null) {
                model.addRow(user.getInfo());
            }

            JTable tableInBuyers = new JTable(model);

            JScrollPane scrollPane = new JScrollPane(tableInBuyers);
            scrollPane.setPreferredSize(new Dimension(300, 200));

            order.addActionListener((action) -> {
                double sum = 0;
//                kug
                Order ord = new Order();
                int index = tableInBuyers.getSelectedRow();
                Buyers buyer = new Buyers((Integer) tableInBuyers.getValueAt(index, 0),
                        (String) tableInBuyers.getValueAt(index, 1),
                        (String) tableInBuyers.getValueAt(index, 2),
                        (String) tableInBuyers.getValueAt(index, 3));
                buyer.setDiscount((Integer) tableInBuyers.getValueAt(index, 4));
                ord.setBuyer(buyer);
                ord.setStatus((String) statuses.getSelectedItem());
//                ord.setUser(shopId);
                int countRow = this.table.getRowCount();
                for (int i = 0; i < countRow; i++) {

                    Product p = new Product();
                    p.setProductid((Integer) table.getValueAt(i, 0));
                    ItemOrder item = new ItemOrder(0,
                            (Integer) table.getValueAt(i, 4),
                            p, 0);
                    System.err.println("Count: " + item.getCount() + "\n" + p.getPrice());
                    sum += (Integer) table.getValueAt(i, 5);
                    ord.getItemOrders().add(item);
                }
                try {
                    out.writeInt(Constants.ADD_ORDER);
                    ObjectOutputStream outObject = new ObjectOutputStream(out);
                    outObject.writeObject(ord);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                JOptionPane.showMessageDialog(null, "Загальна вартість: " + sum
                        + " грн.\nЗі знижкою: " + (buyer.getDiscount() != 0 ? sum - (buyer.getDiscount() * sum) / 100 : sum) + " грн.");
                dialog.dispose();
            });

            dialog.add(scrollPane);
            dialog.add(new JLabel("Імя:                                   "));
            dialog.add(fname);
            dialog.add(new JLabel("Фамілія:                          "));
            dialog.add(lname);
            dialog.add(new JLabel("Телефон:                               "));
            dialog.add(phone);
            dialog.add(addBuyer);
            dialog.add(statuses);
            dialog.add(order);
            dialog.setVisible(true);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
