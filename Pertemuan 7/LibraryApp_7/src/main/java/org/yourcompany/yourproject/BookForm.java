package org.yourcompany.yourproject;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class BookForm extends JFrame {
    private JTable table;
    private DefaultTableModel tableModel;
    private final String[] columnNames = {"ID", "Title", "Author"};
    private JTextField titleField;
    private JTextField authorField;

    public BookForm() {
        setTitle("Book Manager GUI");
        setSize(700, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel(new GridLayout(2, 1));

        JPanel inputPanel = new JPanel(new FlowLayout());
        titleField = new JTextField(15);
        authorField = new JTextField(15);
        inputPanel.add(new JLabel("Title:"));
        inputPanel.add(titleField);
        inputPanel.add(new JLabel("Author:"));
        inputPanel.add(authorField);

        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add Book");
        JButton refreshButton = new JButton("Refresh");

        addButton.addActionListener(e -> addBookViaAPI());
        refreshButton.addActionListener(e -> loadDataFromAPI());

        buttonPanel.add(addButton);
        buttonPanel.add(refreshButton);

        controlPanel.add(inputPanel);
        controlPanel.add(buttonPanel);

        add(controlPanel, BorderLayout.SOUTH);

        loadDataFromAPI();
    }

    private void addBookViaAPI() {
        String title = titleField.getText().trim();
        String author = authorField.getText().trim();

        if (title.isEmpty() || author.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Judul dan Penulis harus diisi!");
            return;
        }

        try {
            URL url = new URL("http://localhost:4567/api/books");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String jsonBody = new Gson().toJson(new Book(0, title, author));
            try (OutputStream os = conn.getOutputStream()) {
                os.write(jsonBody.getBytes());
                os.flush();
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == 200 || responseCode == 201) {
                JOptionPane.showMessageDialog(this, "Buku berhasil ditambahkan!");
                titleField.setText("");
                authorField.setText("");
                loadDataFromAPI();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menambahkan buku. Code: " + responseCode);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error:\n" + e.getMessage());
        }
    }

    private void loadDataFromAPI() {
        try {
            URL url = new URL("http://localhost:4567/api/books");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String json = in.lines().collect(Collectors.joining());
            in.close();

            List<Book> books = new Gson().fromJson(json, new TypeToken<List<Book>>() {}.getType());

            tableModel.setRowCount(0);
            for (Book book : books) {
                Object[] row = { book.getId(), book.getTitle(), book.getAuthor() };
                tableModel.addRow(row);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal mengambil data:\n" + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            BookForm gui = new BookForm();
            gui.setVisible(true);
        });
    }
}
