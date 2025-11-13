import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

/**
 * Full single-file Swing app: London Musical Ticket System
 * - No package declaration
 * - Splash screen (plain) shown for 2 seconds
 * - Welcome main window with buttons
 * - In-memory sample data (4 musicals) with multiple shows
 * - 100 seats per show (S1..S100)
 * - Ticket types: Adult=50, Senior=40, Student=35
 * - Book tickets with multi-seat selection, preview and confirm
 * - Receipts saved as plain text files in receipts/
 *
 * Compile:
 * javac src/Main.java
 * Run:
 * java -cp src Main
 */
public class Main {
    private JFrame frame;
    private final List<Musical> musicals = new ArrayList<>();
    private final Map<String, Integer> ticketPrices = new HashMap<>();

    public Main() {
        // Sample Musical Show Data Seedings
        initSampleData();
        initTicketPrices();

        // Function that builds main GUI
        buildMainUI();
    }

    private void initTicketPrices() {
        ticketPrices.put("Adult", 50);
        ticketPrices.put("Senior", 40);
        ticketPrices.put("Student", 35);
    }

    private void initSampleData() {
        LocalDate start = LocalDate.now().plusDays(1);

        Musical m1 = new Musical("The Lion King", "A Disney classic - family musical.", "assets/lionKing.png");
        m1.addShow(new Show(start.plusDays(1), LocalTime.of(19, 0)));
        m1.addShow(new Show(start.plusDays(4), LocalTime.of(14, 30)));
        m1.addShow(new Show(start.plusDays(9), LocalTime.of(20, 0)));

        Musical m2 = new Musical("Frozen", "A magical musical for children.", "assets/frozen.jpeg");
        m2.addShow(new Show(start.plusDays(2), LocalTime.of(13, 0)));
        m2.addShow(new Show(start.plusDays(6), LocalTime.of(19, 30)));
        m2.addShow(new Show(start.plusDays(12), LocalTime.of(18, 0)));

        Musical m3 = new Musical("Les Misérables", "Epic tale of revolution & love.", "assets/les.png");
        m3.addShow(new Show(start.plusDays(3), LocalTime.of(19, 30)));
        m3.addShow(new Show(start.plusDays(10), LocalTime.of(19, 30)));

        Musical m4 = new Musical("Phantom of the Opera", "Haunting romance and mystery.", "assets/pha.jpg");
        m4.addShow(new Show(start.plusDays(5), LocalTime.of(19, 0)));
        m4.addShow(new Show(start.plusDays(11), LocalTime.of(14, 0)));
        m4.addShow(new Show(start.plusDays(17), LocalTime.of(20, 0)));

        musicals.add(m1);
        musicals.add(m2);
        musicals.add(m3);
        musicals.add(m4);
    }

    private void buildMainUI() {
        frame = new JFrame("London Musical Tickets");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(920, 540);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        // Top title
        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(new EmptyBorder(12, 12, 12, 12));
        JLabel title = new JLabel("Welcome to London Musical Tickets", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        header.add(title, BorderLayout.CENTER);
        frame.add(header, BorderLayout.NORTH);

        // Buttons on left
        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setBorder(new EmptyBorder(10, 10, 10, 10));
        JButton btnList = new JButton("Musical List");
        JButton btnSchedule = new JButton("Show Schedule");
        JButton btnBook = new JButton("Book Tickets");
        JButton btnExit = new JButton("Exit");

        Dimension btnSize = new Dimension(160, 40);
        for (JButton b : Arrays.asList(btnList, btnSchedule, btnBook, btnExit)) {
            b.setMaximumSize(btnSize);
            b.setAlignmentX(Component.CENTER_ALIGNMENT);
            left.add(b);
            left.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        frame.add(left, BorderLayout.WEST);

        // Center info area
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        JTextArea infoArea = new JTextArea();
        infoArea.setEditable(false);
        infoArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        infoArea.setText(
                "Welcome! Use the buttons on the left to:\n\n - View musicals\n - Check schedules\n - Book tickets\n\nReceipts are saved in the 'receipts' folder.");

        mainPanel.add(infoArea);

        JScrollPane centerScroll = new JScrollPane(mainPanel);
        centerScroll.setBorder(new EmptyBorder(10, 10, 10, 10));
        frame.add(centerScroll, BorderLayout.CENTER);

        // Attach actions
        btnList.addActionListener(e -> showMusicalList(mainPanel));
        btnSchedule.addActionListener(e -> showScheduleDialog());
        btnBook.addActionListener(e -> bookTicketsDialog());
        btnExit.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(frame, "Exit application?", "Confirm",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION)
                frame.dispose();
        });

        frame.setVisible(true);
    }

    private void showMusicalList(JPanel mainPanel) {
        mainPanel.removeAll(); // Clear previous content
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS)); // Stack items vertically
        mainPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
        mainPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        for (Musical m : musicals) {
            JPanel musicalPanel = new JPanel(new BorderLayout());
            musicalPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            musicalPanel.setMaximumSize(new Dimension(600, 150));
            Border lineBorder = BorderFactory.createLineBorder(java.awt.Color.gray, 1);
            Border innerMargin = BorderFactory.createEmptyBorder(10, 10, 10, 10);
            Border outerMargin = BorderFactory.createEmptyBorder(10, 10, 5, 0);

            Border innerCombined = BorderFactory.createCompoundBorder(lineBorder, innerMargin);
            Border fullBorder = BorderFactory.createCompoundBorder(outerMargin, innerCombined);

            musicalPanel.setBorder(fullBorder);

            // This is for spacing between the shows.
            JPanel emptyPanel = new JPanel(new BorderLayout());
            emptyPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

            // Image
            ImageIcon icon = new ImageIcon(m.getImagePath());
            Image scaledImg = icon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
            JLabel imgLabel = new JLabel(new ImageIcon(scaledImg));
            imgLabel.setPreferredSize(new Dimension(120, 120)); // Reserve space to prevent resizing issues

            // Text info
            JTextArea infoArea = new JTextArea();
            infoArea.setEditable(false);
            infoArea.setText(
                    "Name: " + m.name + "\n" +
                            "Description: " + m.description + "\n" +
                            "Shows Available: " + m.shows.size());
            infoArea.setFont(new Font("Arial", Font.PLAIN, 14));
            infoArea.setWrapStyleWord(true);
            infoArea.setLineWrap(true);
            infoArea.setBackground(mainPanel.getBackground());
            infoArea.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

            musicalPanel.add(imgLabel, BorderLayout.WEST);
            musicalPanel.add(infoArea, BorderLayout.CENTER);

            // mainPanel.add(emptyPanel);
            mainPanel.add(musicalPanel);
        }

        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private void showScheduleDialog() {
        JDialog dlg = new JDialog(frame, "Show Schedule", true);
        dlg.setSize(700, 420);
        dlg.setLocationRelativeTo(frame);
        dlg.setLayout(new BorderLayout(10, 10));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        top.add(new JLabel("Select Musical:"));
        JComboBox<String> cbMusicals = new JComboBox<>();
        for (Musical m : musicals)
            cbMusicals.addItem(m.name);
        top.add(cbMusicals);

        dlg.add(top, BorderLayout.NORTH);

        DefaultListModel<String> listModel = new DefaultListModel<>();
        JList<String> list = new JList<>(listModel);
        list.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane listScroll = new JScrollPane(list);
        dlg.add(listScroll, BorderLayout.CENTER);

        cbMusicals.addActionListener(e -> refreshShowList(cbMusicals.getSelectedIndex(), listModel));
        refreshShowList(0, listModel);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton viewSeats = new JButton("View Seats");
        JButton close = new JButton("Close");
        bottom.add(viewSeats);
        bottom.add(close);
        dlg.add(bottom, BorderLayout.SOUTH);

        viewSeats.addActionListener(e -> {
            int mi = cbMusicals.getSelectedIndex();
            int sel = list.getSelectedIndex();
            if (mi < 0 || sel < 0) {
                JOptionPane.showMessageDialog(dlg, "Please select a show from the list.", "Validation",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            Show s = musicals.get(mi).shows.get(sel);
            showSeatsDialog(musicals.get(mi), s);
        });

        close.addActionListener(e -> dlg.dispose());
        dlg.setVisible(true);
    }

    private void refreshShowList(int musicalIndex, DefaultListModel<String> listModel) {
        listModel.clear();
        if (musicalIndex < 0)
            return;
        Musical m = musicals.get(musicalIndex);
        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd MMM uuuu");
        DateTimeFormatter tf = DateTimeFormatter.ofPattern("HH:mm");
        for (Show s : m.shows) {
            listModel.addElement(
                    String.format("%s   %s   Available: %d", s.date.format(df), s.time.format(tf), s.availableSeats()));
        }
    }

    private void showSeatsDialog(Musical musical, Show show) {
        JDialog dlg = new JDialog(frame, "Seats for " + musical.name + " - " + show.date + " " + show.time, true);
        dlg.setSize(600, 420);
        dlg.setLocationRelativeTo(frame);
        dlg.setLayout(new BorderLayout(10, 10));

        DefaultListModel<String> seatModel = new DefaultListModel<>();
        for (int i = 1; i <= Show.SEAT_CAPACITY; i++)
            if (!show.isBooked(i))
                seatModel.addElement("S" + i);

        JList<String> seatList = new JList<>(seatModel);
        seatList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane scroll = new JScrollPane(seatList);
        dlg.add(scroll, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton close = new JButton("Close");
        bottom.add(close);
        dlg.add(bottom, BorderLayout.SOUTH);

        close.addActionListener(e -> dlg.dispose());
        dlg.setVisible(true);
    }

    private void bookTicketsDialog() {
        JDialog dlg = new JDialog(frame, "Book Tickets", true);
        dlg.setSize(980, 620);
        dlg.setLocationRelativeTo(frame);
        dlg.setLayout(new BorderLayout(10, 10));

        // Top: musical + show selection
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        top.add(new JLabel("Select Musical:"));
        JComboBox<String> cbMusicals = new JComboBox<>();
        for (Musical m : musicals)
            cbMusicals.addItem(m.name);
        top.add(cbMusicals);

        top.add(new JLabel("Select Show:"));
        JComboBox<String> cbShows = new JComboBox<>();
        top.add(cbShows);

        dlg.add(top, BorderLayout.NORTH);

        // Center left: seats list (multi-select)
        DefaultListModel<String> seatModel = new DefaultListModel<>();
        JList<String> seatList = new JList<>(seatModel);
        seatList.setVisibleRowCount(15);
        seatList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane seatScroll = new JScrollPane(seatList);

        // Center right: ticket counts and summary
        JPanel ticketPanel = new JPanel(new GridBagLayout());
        ticketPanel.setBorder(BorderFactory.createTitledBorder("Ticket types (must equal number of seats selected)"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        ticketPanel.add(new JLabel("Adult ($50):"), gbc);
        gbc.gridx = 1;
        JSpinner spAdult = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));
        ticketPanel.add(spAdult, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        ticketPanel.add(new JLabel("Senior ($40):"), gbc);
        gbc.gridx = 1;
        JSpinner spSenior = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));
        ticketPanel.add(spSenior, gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        ticketPanel.add(new JLabel("Student ($35):"), gbc);
        gbc.gridx = 1;
        JSpinner spStudent = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));
        ticketPanel.add(spStudent, gbc);

        JTextArea summary = new JTextArea(12, 36);
        summary.setEditable(false);
        summary.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane summaryScroll = new JScrollPane(summary);

        JPanel right = new JPanel(new BorderLayout(8, 8));
        right.add(ticketPanel, BorderLayout.NORTH);
        right.add(summaryScroll, BorderLayout.CENTER);

        // Buttons
        JButton btnRefreshSeats = new JButton("Refresh Seats");
        JButton btnPreview = new JButton("Preview Order");
        JButton btnConfirm = new JButton("Confirm Purchase");
        JButton btnCancel = new JButton("Cancel");

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        actions.add(btnRefreshSeats);
        actions.add(btnPreview);
        actions.add(btnConfirm);
        actions.add(btnCancel);

        // Compose center
        JPanel center = new JPanel(new GridLayout(1, 2, 10, 10));
        center.add(seatScroll);
        center.add(right);
        dlg.add(center, BorderLayout.CENTER);
        dlg.add(actions, BorderLayout.SOUTH);

        // Populate shows when musical selected
        cbMusicals.addActionListener(e -> {
            int mi = cbMusicals.getSelectedIndex();
            cbShows.removeAllItems();
            if (mi >= 0) {
                Musical m = musicals.get(mi);
                for (Show s : m.shows) {
                    cbShows.addItem(s.date.format(DateTimeFormatter.ofPattern("dd MMM uuuu")) + "  "
                            + s.time.format(DateTimeFormatter.ofPattern("HH:mm")));
                }
            }
            refreshSeatsForSelection(cbMusicals.getSelectedIndex(), cbShows.getSelectedIndex(), seatModel);
        });

        cbShows.addActionListener(
                e -> refreshSeatsForSelection(cbMusicals.getSelectedIndex(), cbShows.getSelectedIndex(), seatModel));

        btnRefreshSeats.addActionListener(
                e -> refreshSeatsForSelection(cbMusicals.getSelectedIndex(), cbShows.getSelectedIndex(), seatModel));

        btnPreview.addActionListener(e -> {
            int mi = cbMusicals.getSelectedIndex();
            int si = cbShows.getSelectedIndex();
            if (mi < 0 || si < 0) {
                JOptionPane.showMessageDialog(dlg, "Please select musical and show.", "Validation",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            List<String> selectedSeats = seatList.getSelectedValuesList();
            if (selectedSeats.isEmpty()) {
                JOptionPane.showMessageDialog(dlg, "Please select at least one seat.", "Validation",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            int adults = (int) spAdult.getValue();
            int seniors = (int) spSenior.getValue();
            int students = (int) spStudent.getValue();
            int totalCount = adults + seniors + students;
            if (totalCount != selectedSeats.size()) {
                JOptionPane.showMessageDialog(dlg,
                        "Number of ticket types must equal number of seats selected.\nSelected seats: "
                                + selectedSeats.size() + ", ticket count: " + totalCount,
                        "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // assign types in order
            List<String> types = new ArrayList<>();
            for (int i = 0; i < adults; i++)
                types.add("Adult");
            for (int i = 0; i < seniors; i++)
                types.add("Senior");
            for (int i = 0; i < students; i++)
                types.add("Student");

            StringBuilder sb = new StringBuilder();
            sb.append("Musical: ").append(musicals.get(mi).name).append("\n");
            sb.append("Show: ").append(musicals.get(mi).shows.get(si).date).append(" ")
                    .append(musicals.get(mi).shows.get(si).time).append("\n\n");
            sb.append(String.format("%-8s %-10s %-8s\n", "Seat", "Type", "Price"));
            sb.append("--------------------------------\n");
            int total = 0;
            for (int k = 0; k < selectedSeats.size(); k++) {
                String seat = selectedSeats.get(k);
                String type = types.get(k);
                int price = ticketPrices.get(type);
                total += price;
                sb.append(String.format("%-8s %-10s $%-8d\n", seat, type, price));
            }
            sb.append("\nTotal: $").append(total).append("\n");
            summary.setText(sb.toString());
        });

        btnConfirm.addActionListener(e -> {
            int mi = cbMusicals.getSelectedIndex();
            int si = cbShows.getSelectedIndex();
            if (mi < 0 || si < 0) {
                JOptionPane.showMessageDialog(dlg, "Please select musical and show.", "Validation",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            List<String> selectedSeats = seatList.getSelectedValuesList();
            if (selectedSeats.isEmpty()) {
                JOptionPane.showMessageDialog(dlg, "Please select seats to book.", "Validation",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            int adults = (int) spAdult.getValue();
            int seniors = (int) spSenior.getValue();
            int students = (int) spStudent.getValue();
            int totalCount = adults + seniors + students;
            if (totalCount != selectedSeats.size()) {
                JOptionPane.showMessageDialog(dlg,
                        "Number of ticket types must equal number of seats selected.\nSelected seats: "
                                + selectedSeats.size() + ", ticket count: " + totalCount,
                        "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Show s = musicals.get(mi).shows.get(si);
            // concurrency check: ensure all still available
            for (String seatStr : selectedSeats) {
                int seatId = Integer.parseInt(seatStr.substring(1)); // "S12" -> 12
                if (s.isBooked(seatId)) {
                    JOptionPane.showMessageDialog(dlg, "Seat " + seatId + " was just booked. Please refresh seats.",
                            "Seat unavailable", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            // assign types in order
            List<String> types = new ArrayList<>();
            for (int i = 0; i < adults; i++)
                types.add("Adult");
            for (int i = 0; i < seniors; i++)
                types.add("Senior");
            for (int i = 0; i < students; i++)
                types.add("Student");

            Map<Integer, String> seatTypeMap = new LinkedHashMap<>();
            int idx = 0;
            int total = 0;
            for (String seatStr : selectedSeats) {
                int seatId = Integer.parseInt(seatStr.substring(1));
                String type = types.get(idx++);
                seatTypeMap.put(seatId, type);
                total += ticketPrices.get(type);
            }

            // book seats
            for (Integer seatId : seatTypeMap.keySet())
                s.bookSeat(seatId);

            // save receipt
            Order order = new Order(UUID.randomUUID().toString(), musicals.get(mi).name, s.date, s.time, seatTypeMap,
                    total);
            try {
                String path = saveReceipt(order);
                JOptionPane.showMessageDialog(dlg, "Purchase confirmed! Receipt saved to:\n" + path, "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(dlg, "Failed to save receipt: " + ex.getMessage(), "IO Error",
                        JOptionPane.ERROR_MESSAGE);
            }

            // refresh seats and clear fields
            refreshSeatsForSelection(mi, si, seatModel);
            spAdult.setValue(0);
            spSenior.setValue(0);
            spStudent.setValue(0);
            summary.setText("");
        });

        btnCancel.addActionListener(e -> dlg.dispose());

        // initialize selection
        if (cbMusicals.getItemCount() > 0) {
            cbMusicals.setSelectedIndex(0);
            cbMusicals.dispatchEvent(new ActionEvent(cbMusicals, ActionEvent.ACTION_PERFORMED, ""));
        }

        dlg.setVisible(true);
    }

    private void refreshSeatsForSelection(int musicalIndex, int showIndex, DefaultListModel<String> seatModel) {
        seatModel.clear();
        if (musicalIndex < 0 || showIndex < 0)
            return;
        Musical m = musicals.get(musicalIndex);
        if (showIndex >= m.shows.size())
            return;
        Show s = m.shows.get(showIndex);
        for (int i = 1; i <= Show.SEAT_CAPACITY; i++) {
            if (!s.isBooked(i))
                seatModel.addElement("S" + i);
        }
    }

    private String saveReceipt(Order order) throws IOException {
        Path receiptsDir = Paths.get("receipts");
        if (!Files.exists(receiptsDir))
            Files.createDirectories(receiptsDir);
        String timestamp = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss").format(LocalDateTime.now());
        String filename = "receipt_" + timestamp + "_" + order.id.substring(0, 8) + ".txt";
        Path file = receiptsDir.resolve(filename);
        try (BufferedWriter w = Files.newBufferedWriter(file)) {
            w.write("London Musical Tickets - Receipt");
            w.newLine();
            w.write("Order ID: " + order.id);
            w.newLine();
            w.write("Musical: " + order.musicalName);
            w.newLine();
            w.write("Show: " + order.showDate + " " + order.showTime);
            w.newLine();
            w.write("----------------------------------------");
            w.newLine();
            w.write(String.format("%-8s %-10s %-8s", "Seat", "Type", "Price"));
            w.newLine();
            w.write("----------------------------------------");
            w.newLine();
            for (Map.Entry<Integer, String> e : order.seatType.entrySet()) {
                int price = ticketPrices.get(e.getValue());
                w.write(String.format("%-8d %-10s $%d", e.getKey(), e.getValue(), price));
                w.newLine();
            }
            w.write("----------------------------------------");
            w.newLine();
            w.write("Total: $" + order.total);
            w.newLine();
            w.write("Thank you for your purchase!");
            w.newLine();
        }
        return file.toAbsolutePath().toString();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::new);
    }
}

/* ---------------- Helper classes ---------------- */

class Musical {
    final String name;
    final String description;
    final List<Show> shows = new ArrayList<>();
    final String imagePath;

    public Musical(String name, String description, String imagePath) {
        this.name = name;
        this.description = description;
        this.imagePath = imagePath;
    }

    public void addShow(Show s) {
        shows.add(s);
    }

    public String getImagePath() {
        return this.imagePath;
    }
}

class Show {
    public static final int SEAT_CAPACITY = 100;
    final LocalDate date;
    final LocalTime time;
    private final Set<Integer> booked = new HashSet<>();

    public Show(LocalDate date, LocalTime time) {
        this.date = date;
        this.time = time;
    }

    public boolean isBooked(int seatId) {
        return booked.contains(seatId);
    }

    public void bookSeat(int seatId) {
        if (seatId >= 1 && seatId <= SEAT_CAPACITY)
            booked.add(seatId);
    }

    public int availableSeats() {
        return SEAT_CAPACITY - booked.size();
    }
}

class Order {
    final String id;
    final String musicalName;
    final LocalDate showDate;
    final LocalTime showTime;
    final LinkedHashMap<Integer, String> seatType;
    final int total;

    public Order(String id, String musicalName, LocalDate showDate, LocalTime showTime, Map<Integer, String> seatType,
            int total) {
        this.id = id;
        this.musicalName = musicalName;
        this.showDate = showDate;
        this.showTime = showTime;
        this.seatType = new LinkedHashMap<>(seatType);
        this.total = total;
    }
}
