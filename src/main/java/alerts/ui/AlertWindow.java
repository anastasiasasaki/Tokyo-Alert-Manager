package alerts.ui;

import alerts.DeliveryChannel;
import alerts.NotificationService;
import alerts.SelectionMode;
import alerts.model.DeliveryRecord;
import alerts.model.SampleData;
import alerts.model.Subscriber;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;

/** Supplied interface. Students work on the backend, not this class. */
public final class AlertWindow extends JFrame {
    private final NotificationService service = new NotificationService();
    private final List<Subscriber> subscribers = new ArrayList<>(SampleData.subscribers());
    private final JComboBox<String> wardBox =
        new JComboBox<>(SampleData.wards().toArray(String[]::new));
    private final JComboBox<SelectionMode> policyBox = new JComboBox<>(SelectionMode.values());
    private final JComboBox<DeliveryChannel> channelBox = new JComboBox<>(DeliveryChannel.values());
    private final JTextArea messageBox = new JTextArea("Practice notice.", 2, 30);
    private final JLabel status = new JLabel("Ready");
    private final JLabel selectedCount = new JLabel("No preview yet");
    private final JLabel subscriberCount = new JLabel();
    private final DefaultTableModel recipientModel = tableModel("ID", "Name", "Home ward", "Follows");
    private final DefaultTableModel subscriberModel = tableModel("ID", "Name", "Home ward", "Follows");
    private final DefaultTableModel deliveryModel = tableModel("Method", "Recipient ID", "Ward", "Message");
    private final JTable recipientTable = makeTable(recipientModel, "recipientTable");
    private final JTable subscriberTable = makeTable(subscriberModel, "subscriberTable");
    private final JTable deliveryTable = makeTable(deliveryModel, "deliveryTable");
    private final JButton duplicateButton = button("Duplicate row", "duplicateButton", e -> duplicateSelected());
    private final JButton removeButton = button("Remove", "removeButton", e -> removeSelected());
    private final JTabbedPane tabs = new JTabbedPane();
    private int nextId = 5;

    public AlertWindow() {
        super("Tokyo Alert Manager");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(850, 600));
        JPanel content = new JPanel(new BorderLayout(0, 10));
        content.setBorder(BorderFactory.createEmptyBorder(12, 12, 10, 12));
        tabs.setName("mainTabs");
        tabs.addTab("Notice", buildNoticePage());
        tabs.addTab("Subscribers", buildSubscribersPage());
        tabs.addTab("Delivery results", buildDeliveryPage());
        content.add(tabs, BorderLayout.CENTER);
        status.setName("statusLabel");
        content.add(status, BorderLayout.SOUTH);
        setContentPane(content);
        refreshSubscribers();
        Rectangle screen = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        setSize(Math.min(1020, screen.width - 32), Math.min(690, screen.height - 48));
        setLocationRelativeTo(null);
    }

    private JPanel buildNoticePage() {
        JPanel page = pagePanel();
        JPanel top = new JPanel(new BorderLayout(0, 12));
        JPanel controls = new JPanel(new GridBagLayout());
        wardBox.setName("wardBox");
        policyBox.setName("policyBox");
        channelBox.setName("channelBox");
        wardBox.setEditable(false);
        wardBox.setPreferredSize(new Dimension(200, 30));
        policyBox.setPreferredSize(new Dimension(260, 30));
        channelBox.setPreferredSize(new Dimension(150, 30));
        policyBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean selected, boolean focused) {
                super.getListCellRendererComponent(list, value, index, selected, focused);
                if (value instanceof SelectionMode mode) setText(policyLabel(mode));
                return this;
            }
        });
        channelBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean selected, boolean focused) {
                super.getListCellRendererComponent(list, value, index, selected, focused);
                if (value instanceof DeliveryChannel channel) {
                    setText(channel == DeliveryChannel.EMAIL ? "Email" : "App");
                }
                return this;
            }
        });
        addChoice(controls, 0, "Issuing ward", wardBox);
        addChoice(controls, 1, "Recipient rule", policyBox);
        addChoice(controls, 2, "Delivery method", channelBox);
        top.add(controls, BorderLayout.NORTH);

        JPanel messagePanel = new JPanel(new BorderLayout(0, 5));
        JLabel messageLabel = new JLabel("Message");
        messageLabel.setLabelFor(messageBox);
        messageBox.setName("messageBox");
        messageBox.setLineWrap(true);
        messageBox.setWrapStyleWord(true);
        messageBox.setMargin(new Insets(6, 6, 6, 6));
        messagePanel.add(messageLabel, BorderLayout.NORTH);
        messagePanel.add(new JScrollPane(messageBox), BorderLayout.CENTER);
        top.add(messagePanel, BorderLayout.CENTER);
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        actions.add(button("Preview recipients", "previewButton", e -> preview()));
        actions.add(button("Simulate delivery", "sendButton", e -> sendNotice()));
        top.add(actions, BorderLayout.SOUTH);
        page.add(top, BorderLayout.NORTH);

        JPanel results = new JPanel(new BorderLayout(0, 8));
        JPanel heading = new JPanel(new BorderLayout());
        heading.add(new JLabel("Selected recipients"), BorderLayout.WEST);
        selectedCount.setName("selectedCount");
        heading.add(selectedCount, BorderLayout.EAST);
        results.add(heading, BorderLayout.NORTH);
        results.add(new JScrollPane(recipientTable), BorderLayout.CENTER);
        setSubscriberColumnWidths(recipientTable);
        page.add(results, BorderLayout.CENTER);

        wardBox.addActionListener(e -> resetSelectionDisplay());
        policyBox.addActionListener(e -> resetSelectionDisplay());
        channelBox.addActionListener(e -> resetDeliveryDisplay());
        messageBox.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { resetDeliveryDisplay(); }
            public void removeUpdate(DocumentEvent e) { resetDeliveryDisplay(); }
            public void changedUpdate(DocumentEvent e) { resetDeliveryDisplay(); }
        });
        return page;
    }

    private JPanel buildSubscribersPage() {
        JPanel page = pagePanel();
        JPanel toolbar = new JPanel(new BorderLayout());
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        actions.add(button("Add subscriber", "addButton", e -> addSubscriber()));
        actions.add(duplicateButton);
        actions.add(removeButton);
        duplicateButton.setToolTipText("Copy the selected row with the same ID.");
        toolbar.add(actions, BorderLayout.WEST);
        toolbar.add(button("Restore sample data", "restoreButton", e -> {
            subscribers.clear();
            subscribers.addAll(SampleData.subscribers());
            nextId = 5;
            refreshSubscribers();
            resetSelectionDisplay();
        }), BorderLayout.EAST);
        page.add(toolbar, BorderLayout.NORTH);
        page.add(new JScrollPane(subscriberTable), BorderLayout.CENTER);
        page.add(subscriberCount, BorderLayout.SOUTH);
        setSubscriberColumnWidths(subscriberTable);
        subscriberTable.getSelectionModel().addListSelectionListener(e -> {
            boolean selected = subscriberTable.getSelectedRow() >= 0;
            duplicateButton.setEnabled(selected);
            removeButton.setEnabled(selected);
        });
        duplicateButton.setEnabled(false);
        removeButton.setEnabled(false);
        return page;
    }

    private JPanel buildDeliveryPage() {
        JPanel page = pagePanel();
        deliveryTable.getColumnModel().getColumn(0).setPreferredWidth(90);
        deliveryTable.getColumnModel().getColumn(1).setPreferredWidth(110);
        deliveryTable.getColumnModel().getColumn(2).setPreferredWidth(180);
        deliveryTable.getColumnModel().getColumn(3).setPreferredWidth(440);
        page.add(new JScrollPane(deliveryTable), BorderLayout.CENTER);
        page.add(new JLabel("Simulation only. No messages are sent."), BorderLayout.SOUTH);
        return page;
    }

    private void applyChoices() {
        service.setSelectionMode((SelectionMode) policyBox.getSelectedItem());
        service.setDeliveryChannel((DeliveryChannel) channelBox.getSelectedItem());
    }

    private void preview() {
        try {
            applyChoices();
            Set<String> ids = service.selectRecipients(new ArrayList<>(subscribers),
                (String) wardBox.getSelectedItem());
            renderRecipients(ids);
            setStatus(ids.isEmpty() ? "No matching subscribers." : "Preview complete");
        } catch (RuntimeException ex) {
            resetSelectionDisplay();
            showError(ex);
        }
    }

    private void sendNotice() {
        deliveryModel.setRowCount(0);
        if (messageBox.getText().isBlank()) {
            setStatus("Enter a message.");
            messageBox.requestFocusInWindow();
            return;
        }
        try {
            applyChoices();
            List<DeliveryRecord> records = service.sendNotice(new ArrayList<>(subscribers),
                (String) wardBox.getSelectedItem(), messageBox.getText());
            Set<String> ids = new LinkedHashSet<>();
            for (DeliveryRecord record : records) {
                ids.add(record.getSubscriberId());
                deliveryModel.addRow(new Object[]{record.getChannel().name(), record.getSubscriberId(),
                    record.getIssuingWard(), record.getMessage()});
            }
            renderRecipients(ids);
            tabs.setSelectedIndex(2);
            setStatus(records.isEmpty() ? "No matching subscribers. No deliveries created."
                : records.size() + (records.size() == 1 ? " simulated delivery" : " simulated deliveries"));
        } catch (RuntimeException ex) {
            showError(ex);
        }
    }

    private void renderRecipients(Set<String> ids) {
        recipientModel.setRowCount(0);
        Map<String, Subscriber> byId = new LinkedHashMap<>();
        for (Subscriber subscriber : subscribers) byId.putIfAbsent(subscriber.getId(), subscriber);
        for (String id : ids) {
            Subscriber subscriber = byId.get(id);
            recipientModel.addRow(subscriber == null
                ? new Object[]{id, "", "", ""} : subscriberRow(subscriber));
        }
        selectedCount.setText(ids.size() + " selected");
    }

    private void resetSelectionDisplay() {
        recipientModel.setRowCount(0);
        selectedCount.setText("No preview yet");
        resetDeliveryDisplay();
    }

    private void resetDeliveryDisplay() {
        deliveryModel.setRowCount(0);
        setStatus("Ready");
    }

    private void showError(RuntimeException ex) {
        String message = ex.getMessage() == null ? ex.getClass().getSimpleName() : ex.getMessage();
        if (ex instanceof UnsupportedOperationException) {
            setStatus(message);
        } else {
            setStatus("Unable to complete the request.");
            JOptionPane.showMessageDialog(this, message, "Request failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void setStatus(String message) {
        status.setText(message);
    }

    private void refreshSubscribers() {
        subscriberModel.setRowCount(0);
        for (Subscriber subscriber : subscribers) subscriberModel.addRow(subscriberRow(subscriber));
        subscriberCount.setText(subscribers.size() + " rows");
    }

    private void addSubscriber() {
        JTextField name = new JTextField(20);
        name.setName("newSubscriberName");
        JComboBox<String> home = new JComboBox<>(SampleData.wards().toArray(String[]::new));
        home.setName("newSubscriberHome");
        JPanel follows = new JPanel(new java.awt.GridLayout(0, 2, 8, 3));
        List<JCheckBox> choices = new ArrayList<>();
        for (String ward : SampleData.wards()) {
            JCheckBox box = new JCheckBox(ward);
            box.setName("follow-" + ward);
            choices.add(box);
            follows.add(box);
        }
        JPanel form = new JPanel(new GridBagLayout());
        addFormRow(form, 0, "Name", name);
        addFormRow(form, 1, "Home ward", home);
        addFormRow(form, 2, "Follows", follows);
        while (JOptionPane.showConfirmDialog(this, form, "Add subscriber",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION) {
            try {
                Set<String> wards = new LinkedHashSet<>();
                for (JCheckBox box : choices) if (box.isSelected()) wards.add(box.getText());
                subscribers.add(new Subscriber(String.format("S%02d", nextId),
                    name.getText().trim(), (String) home.getSelectedItem(), wards));
                nextId++;
                refreshSubscribers();
                resetSelectionDisplay();
                return;
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Check subscriber details",
                    JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    private void duplicateSelected() {
        int row = subscriberTable.getSelectedRow();
        if (row < 0) return;
        subscribers.add(subscribers.get(subscriberTable.convertRowIndexToModel(row)));
        refreshSubscribers();
        resetSelectionDisplay();
    }

    private void removeSelected() {
        int row = subscriberTable.getSelectedRow();
        if (row < 0) return;
        subscribers.remove(subscriberTable.convertRowIndexToModel(row));
        refreshSubscribers();
        resetSelectionDisplay();
    }

    private static Object[] subscriberRow(Subscriber subscriber) {
        return new Object[]{subscriber.getId(), subscriber.getName(), subscriber.getHomeWard(),
            subscriber.getFollowedWards().isEmpty() ? "None" : String.join(", ", subscriber.getFollowedWards())};
    }

    private static String policyLabel(SelectionMode mode) {
        return switch (mode) {
            case HOME_ONLY -> "Home only";
            case HOME_OR_FOLLOWED -> "Home or followed";
            case FOLLOWED_ONLY -> "Followed only";
            case NON_RESIDENT_FOLLOWER -> "Nonresident followers";
        };
    }

    private static JPanel pagePanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 16));
        panel.setBorder(BorderFactory.createEmptyBorder(16, 14, 14, 14));
        return panel;
    }

    private static JButton button(String text, String name, ActionListener action) {
        JButton button = new JButton(text);
        button.setName(name);
        button.addActionListener(action);
        return button;
    }

    private static DefaultTableModel tableModel(String... columns) {
        return new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
    }

    private static JTable makeTable(DefaultTableModel model, String name) {
        JTable table = new JTable(model);
        table.setName(name);
        DefaultTableCellRenderer plainText = new DefaultTableCellRenderer();
        plainText.putClientProperty("html.disable", Boolean.TRUE);
        table.setDefaultRenderer(Object.class, plainText);
        table.setRowHeight(29);
        table.setFillsViewportHeight(true);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(false);
        table.setAutoCreateRowSorter(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setReorderingAllowed(false);
        table.getAccessibleContext().setAccessibleName(name);
        return table;
    }

    private static void setSubscriberColumnWidths(JTable table) {
        int[] widths = {75, 180, 230, 310};
        for (int i = 0; i < widths.length; i++) table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
    }

    private static void addChoice(JPanel panel, int column, String text, JComponent input) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = column; c.gridy = 0;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(0, 0, 5, column == 2 ? 0 : 12);
        JLabel label = new JLabel(text);
        label.setLabelFor(input);
        panel.add(label, c);
        c.gridy = 1; c.insets.bottom = 0;
        panel.add(input, c);
    }

    private static void addFormRow(JPanel panel, int row, String text, JComponent input) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0; c.gridy = row;
        c.anchor = GridBagConstraints.LINE_START;
        c.insets = new Insets(6, 0, 6, 12);
        JLabel label = new JLabel(text);
        label.setLabelFor(input);
        panel.add(label, c);
        c.gridx = 1; c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets.right = 0;
        panel.add(input, c);
    }
}
