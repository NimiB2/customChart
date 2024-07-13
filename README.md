```markdown
# Custom Table View Library

**Welcome** to the Custom Table View Library for Android! This library allows you to create and manage customizable tables in your Android applications with ease. Below, you'll find detailed information on how to use this library, along with examples and images to showcase its features.

## Features

- **Customizable Rows and Columns**: Add, remove, and customize rows and columns easily.
- **Row Numbering**: Enable or disable row numbering with a customizable header text.
- **Cell Customization**: Change the background color, text color, text size, and text style of individual cells.
- **Dynamic Headers**: Add and manage header rows with customizable styles.
- **Responsive Design**: Automatically adjust column widths to fit the content.

## Installation

To use this library, add the following dependency to your `build.gradle` file:

```gradle
dependencies {
    implementation 'com.example:customtableview:1.0.0'
}
```

## Usage

### Basic Setup

First, add the `CustomTableView` to your layout XML file:

```xml
<dev.nimrod.customchart.CustomTableView
    android:id="@+id/custom_table_view"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content" />
```

### Initialize in Your Activity/Fragment

```java
CustomTableView customTableView = findViewById(R.id.custom_table_view);

// Set table title
customTableView.setTitle("Sample Table");

// Add header row
customTableView.addHeaderRow(new String[]{"Header 1", "Header 2", "Header 3", "Header 4"});

// Add rows
customTableView.addRow(new String[]{"Row 1, Col 1", "Row 1, Col 2", "Row 1, Col 3", "Row 1, Col 4"});
customTableView.addRow(new String[]{"Row 2, Col 1", "Row 2, Col 2", "Row 2, Col 3", "Row 2, Col 4"});
// Add more rows as needed
```

### Customizing Cells

```java
// Change cell background color
customTableView.setCellColor(1, 2, Color.RED);

// Change cell text color
customTableView.setCellTextColor(1, 2, Color.WHITE);

// Change cell text size
customTableView.setCellTextSize(1, 2, 20);

// Change cell text style
customTableView.setCellTextStyle(1, 2, Typeface.BOLD);
```


### Advanced Features

```java
// Enable row numbering
customTableView.enableRowNumbering();

// Change table title
customTableView.setTitle("Advanced Table");

// Add a new header row
customTableView.addHeaderRow(new String[]{"New Header 1", "New Header 2", "New Header 3", "New Header 4"});

// Add a new row
customTableView.addRow(new String[]{"New Row 1, Col 1", "New Row 1, Col 2", "New Row 1, Col 3", "New Row 1, Col 4"});

// Add a new column with header
customTableView.addColumnWithHeader(new String[]{"Col 5", "Value 5", "Value 5", "Value 5", "Value 5", "Value 5", "Value 5"}, "Header 5");
```

## Example Screenshots

### Regular Table
A basic table with 6 rows and 4 columns.


![צילום מסך 2024-07-12 165924](https://github.com/user-attachments/assets/877a5f9d-cb37-4931-9f95-cbba0f35ebbc)

### Adding Colors and Sizes
Customizing cell background colors, text colors, and text sizes.


![צילום מסך 2024-07-12 170355](https://github.com/user-attachments/assets/8bb845b5-2469-4983-9e0b-bd6dc7fb5392)

### Advanced Table
Changing the title, adding headers, a new row, a new column, and enabling numbering.


![צילום מסך 2024-07-12 170445](https://github.com/user-attachments/assets/edda2252-67df-431d-b222-7bbccc683e90)


## Contribution

Contributions are welcome! Please submit pull requests or open issues to help improve the library.

