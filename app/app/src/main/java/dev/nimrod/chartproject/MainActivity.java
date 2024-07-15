package dev.nimrod.chartproject;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import dev.nimrod.customchart.CustomTableView;


public class MainActivity extends AppCompatActivity {
    private CustomTableView customTableView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        customTableView = findViewById(R.id.custom_table_view);


        // Part 1: Regular Table
        setupRegularTable();
        // Pause for taking screenshot here using your device's screenshot functionality

        // Part 2: Adding Colors and Sizes
//        setupColorsAndSizes();
        // Pause for taking screenshot here using your device's screenshot functionality

        // Part 3: Change Title, Headers, Adding Row, Column, and Numbering
        setupAdvancedFeatures();
        // Pause for taking screenshot here using your device's screenshot functionality
    }

    private void setupRegularTable() {
        customTableView.setTitle("Regular Table");
//        customTableView.addHeaderRow(new String[]{"Header 1", "Header 2", "Header 3", "Header 4"});
        for (int i = 1; i <= 6; i++) {
            customTableView.addRow(new String[]{
                    "Row " + i + ", Col 1", "Row " + i + ", Col 2", "Row " + i + ", Col 3", "Row " + i + ", Col 4"
            });
        }
    }

    private void setupColorsAndSizes() {
        customTableView.setCellColor(1, 2, Color.RED);
        customTableView.setCellTextColor(1, 2, Color.WHITE);
        customTableView.setCellTextSize(1, 2, 20);
        customTableView.setCellTextStyle(1, 2, Typeface.BOLD);

        customTableView.setCellColor(2, 3, Color.GREEN);
        customTableView.setCellTextColor(2, 3, Color.BLACK);
        customTableView.setCellTextSize(2, 3, 20);
        customTableView.setCellTextStyle(2, 3, Typeface.BOLD_ITALIC);

        customTableView.setColumnColor(1,Color.YELLOW);
        customTableView.setRowColor(4,Color.LTGRAY);
        customTableView.setCellTextStyle(2, 0, Typeface.BOLD);
        customTableView.setCellTextSize(2, 0, 18);
        customTableView.setCellTextStyle(3, 0, Typeface.ITALIC);
        customTableView.setCellTextSize(3, 0, 16);
    }

    private void setupAdvancedFeatures() {
        customTableView.setTitle("Advanced Table");
        customTableView.addHeaderRow(new String[]{"New Header 1", "New Header 2", "New Header 3", "New Header 4"});

        customTableView.addRow(new String[]{"New Row 1, Col 1", "New Row 1, Col 2", "New Row 1, Col 3", "New Row 1, Col 4"});

        customTableView.addColumnWithHeader(new String[]{"Col 5", "Value 5", "Value 5", "Value 5", "Value 5", "Value 5", "Value 5"}, "Header 5");

        customTableView.enableRowNumbering();
    }


}
