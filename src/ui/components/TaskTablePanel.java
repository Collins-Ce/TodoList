package ui.components;

import model.Task;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class TaskTablePanel extends JPanel {

    private JTable taskTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;

    private JTextField searchField;
    private JComboBox<String> statusFilter;
    private JComboBox<String> priorityFilter;

    public TaskTablePanel() {

        setLayout(new BorderLayout(10, 10));

        createTable();
        createFilters();
    }

    // CREATE TABLE
    private void createTable() {

        String[] columns = {
            "Task ID",
            "Title",
            "Description",
            "Priority",
            "Due Date",
            "Status"
        };

        tableModel = new DefaultTableModel(columns, 0) {

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        taskTable = new JTable(tableModel);

        taskTable.setRowHeight(32);

        taskTable.setSelectionMode(
            ListSelectionModel.SINGLE_SELECTION
        );

        taskTable.getTableHeader().setFont(
            new Font("Arial", Font.BOLD, 13)
        );

        sorter = new TableRowSorter<>(tableModel);

        taskTable.setRowSorter(sorter);

        taskTable.setDefaultRenderer(
            Object.class,
            new TaskCellRenderer()
        );

        JScrollPane scrollPane = new JScrollPane(taskTable);

        add(scrollPane, BorderLayout.CENTER);
    }

    // CREATE SEARCH AND FILTERS
    private void createFilters() {

        JPanel filterPanel = new JPanel(
            new FlowLayout(FlowLayout.LEFT, 10, 10)
        );

        searchField = new JTextField(18);

        statusFilter = new JComboBox<>(
            new String[] {
                "All Status",
                "PENDING",
                "COMPLETED"
            }
        );

        priorityFilter = new JComboBox<>(
            new String[] {
                "All Priorities",
                "LOW",
                "MEDIUM",
                "HIGH"
            }
        );

        filterPanel.add(new JLabel("Search:"));
        filterPanel.add(searchField);

        filterPanel.add(new JLabel("Status:"));
        filterPanel.add(statusFilter);

        filterPanel.add(new JLabel("Priority:"));
        filterPanel.add(priorityFilter);

        add(filterPanel, BorderLayout.NORTH);

        searchField.getDocument().addDocumentListener(
            new javax.swing.event.DocumentListener() {

                public void insertUpdate(
                        javax.swing.event.DocumentEvent e) {
                    applyFilters();
                }

                public void removeUpdate(
                        javax.swing.event.DocumentEvent e) {
                    applyFilters();
                }

                public void changedUpdate(
                        javax.swing.event.DocumentEvent e) {
                    applyFilters();
                }
            }
        );

        statusFilter.addActionListener(e -> applyFilters());

        priorityFilter.addActionListener(e -> applyFilters());
    }

    // LOAD TASKS INTO TABLE
    public void setTasks(List<Task> tasks) {

        tableModel.setRowCount(0);

        for (Task task : tasks) {

            tableModel.addRow(new Object[] {

                task.getTaskId(),
                task.getTitle(),
                task.getDescription(),
                task.getPriority(),

                task.getDueDate() == null
                    ? "Not set"
                    : task.getDueDate().toString(),

                task.getStatus()
            });
        }

        applyFilters();
    }

    // SEARCH AND FILTER
    private void applyFilters() {

        String search = searchField.getText().trim();

        String selectedStatus =
            (String) statusFilter.getSelectedItem();

        String selectedPriority =
            (String) priorityFilter.getSelectedItem();

        RowFilter<DefaultTableModel, Integer> filter =
            new RowFilter<DefaultTableModel, Integer>() {

                @Override
                public boolean include(
                        Entry<? extends DefaultTableModel,
                        ? extends Integer> entry) {

                    String title = String.valueOf(
                        entry.getValue(1)
                    );

                    String description = String.valueOf(
                        entry.getValue(2)
                    );

                    String priority = String.valueOf(
                        entry.getValue(3)
                    );

                    String status = String.valueOf(
                        entry.getValue(5)
                    );

                    boolean searchMatches =
                        search.isEmpty() ||
                        title.toLowerCase().contains(
                            search.toLowerCase()
                        ) ||
                        description.toLowerCase().contains(
                            search.toLowerCase()
                        );

                    boolean statusMatches =
                        "All Status".equals(selectedStatus) ||
                        status.equals(selectedStatus);

                    boolean priorityMatches =
                        "All Priorities".equals(selectedPriority) ||
                        priority.equals(selectedPriority);

                    return searchMatches &&
                           statusMatches &&
                           priorityMatches;
                }
            };

        sorter.setRowFilter(filter);
    }

    // GET SELECTED TASK ID
    public int getSelectedTaskId() {

        int selectedRow = taskTable.getSelectedRow();

        if (selectedRow == -1) {
            return -1;
        }

        int modelRow = taskTable.convertRowIndexToModel(
            selectedRow
        );

        return (int) tableModel.getValueAt(modelRow, 0);
    }

    // CUSTOM TABLE RENDERER
    private class TaskCellRenderer
            extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column) {

            Component component =
                super.getTableCellRendererComponent(
                    table,
                    value,
                    isSelected,
                    hasFocus,
                    row,
                    column
                );

            if (isSelected) {
                component.setBackground(
                    table.getSelectionBackground()
                );

                component.setForeground(
                    table.getSelectionForeground()
                );

                return component;
            }

            int modelRow = table.convertRowIndexToModel(row);

            String priority = String.valueOf(
                tableModel.getValueAt(modelRow, 3)
            );

            String dueDate = String.valueOf(
                tableModel.getValueAt(modelRow, 4)
            );

            String status = String.valueOf(
                tableModel.getValueAt(modelRow, 5)
            );

            component.setBackground(Color.WHITE);
            component.setForeground(Color.DARK_GRAY);

            // COMPLETED TASK
            if ("COMPLETED".equals(status)) {

                component.setBackground(
                    new Color(232, 245, 233)
                );
            }

            // OVERDUE TASK
            else if (!"Not set".equals(dueDate)) {

                try {

                    LocalDate date = LocalDate.parse(dueDate);

                    if (date.isBefore(LocalDate.now())) {

                        component.setBackground(
                            new Color(255, 225, 225)
                        );

                        component.setForeground(
                            new Color(160, 30, 30)
                        );
                    }

                } catch (Exception e) {
                    // Ignore invalid date values
                }
            }

            // PRIORITY COLORS
            if (column == 3) {

                setHorizontalAlignment(SwingConstants.CENTER);

                switch (priority) {

                    case "HIGH":
                        component.setBackground(
                            new Color(255, 205, 210)
                        );
                        component.setForeground(
                            new Color(183, 28, 28)
                        );
                        break;

                    case "MEDIUM":
                        component.setBackground(
                            new Color(255, 224, 178)
                        );
                        component.setForeground(
                            new Color(230, 100, 0)
                        );
                        break;

                    case "LOW":
                        component.setBackground(
                            new Color(200, 230, 201)
                        );
                        component.setForeground(
                            new Color(27, 94, 32)
                        );
                        break;
                }

                setFont(new Font("Arial", Font.BOLD, 12));

            } else {

                setHorizontalAlignment(SwingConstants.LEFT);

                setFont(new Font("Arial", Font.PLAIN, 12));
            }

            return component;
        }
    }
}