# CustomTableView-Android
CustomTableView is an Android library designed to simplify the creation and management of dynamic table views. It provides a comprehensive set of features for handling rows, columns, cell styling, sorting, and filtering, making it ideal for displaying complex tabular data in Android applications.

<p align="center" style="display: flex; align-items: flex-start; justify-content: center;">
  <img src="https://github.com/user-attachments/assets/af4f120f-8ebc-4b7c-a1e6-fc798e3cd5eb" alt="Table Overview" height="300">
  <img src="https://github.com/user-attachments/assets/f80f7ecd-e137-459c-b277-e5587156cc73" alt="Menu Open" height="200" style="margin-left: 20px;">
</p>

## Advanced Features

- **Undo/Redo**: Use `undoButton` and `redoButton` for state management.

<br><br>

- **Swipe Actions**: Swipe left to highlight a row, swipe right to delete.
  
<img src="https://github.com/user-attachments/assets/ac13fec8-645b-4e81-bd17-9ae3c90b8f4b" width="200" alt="Swiping GIF">

<br><br>

- **Drag and Drop**: Long press and drag to reorder rows.

  
<img src="https://github.com/user-attachments/assets/81010c59-8b4b-4be8-946a-9d64c8c9692d" width="200" alt="Drag and Drop GIF">

<br><br>

- **Sorting**: Easily sort rows by clicking on column headers.
  <img src="https://github.com/user-attachments/assets/6218ab81-f9a0-4002-aeff-d43cdc4ba5f2" width="200" alt="Sorting GIF">

<br><br>

- **Filtering**: Quickly filter rows based on specific criteria.


<img src="https://github.com/user-attachments/assets/544cd49d-a967-4989-9c44-e36f6eb98b92" width="200" alt="Filtering GIF">




## Setup
Step 1. Add it in your root build.gradle at the end of repositories:
```gradle
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
```

Step 2. Add the dependency:

```gradle
dependencies {
    implementation 'com.github.NimiB2:customChart:1.0.2'
}
```

## Usage

###### Basic Table Setup:
```java
CustomTableView customTableView = findViewById(R.id.custom_table_view);
customTableView.setTitle("My Table");
customTableView.addRow(new String[]{"Cell 1", "Cell 2", "Cell 3"});
```

###### Adding a Header:
```java
customTableView.setHasHeader(true);
customTableView.addHeaderRow(new String[]{"Column 1", "Column 2", "Column 3"});
```

###### Customizing Cells:
```java
customTableView.setCellColor(1, 2, Color.YELLOW);
customTableView.setCellTextColor(0, 1, Color.BLUE);
customTableView.setCellTypeface(2, 0, Typeface.DEFAULT_BOLD);
```

###### Dynamic Column Management:
```java
// Add a new column
customTableView.addColumn(new String[]{"New Column Header", "Value 1", "Value 2", "Value 3"});

// Remove a column
customTableView.removeColumn(2);  // Removes the third column (index 2)
```

## Customization

Customize the appearance of your table by modifying the XML layouts and drawables provided in the library.

```java
customTableView.setColumnColor(1, Color.LTGRAY);
customTableView.setRowColor(2, Color.CYAN);
customTableView.setNumberingHeaderText("No.");
```

## What's New
1.0.2:
1. Initial release with core functionality
2. Support for cell customization
3. Implemented undo/redo functionality
4. Added filtering capabilities
5. Introduced swipe and drag-drop actions

## License

    Copyright 2024 Nimrod Bar

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

## Credits

Icons used in the library:
- Undo, Redo, and Menu icons: Material Design icons by Google
