# CustomTableView-Android

CustomTableView is an Android library designed to simplify the creation and management of dynamic table views. It provides a comprehensive set of features for handling rows, columns, cell styling, sorting, and filtering, making it ideal for displaying complex tabular data in Android applications.

## Screenshots

<p align="center">
    <img src="https://github.com/user-attachments/assets/4a6ed2ea-5367-4771-95e2-235ca6724591" alt="Screenshot 1" width="45%" style="margin: 10px;">
    <img src="https://github.com/user-attachments/assets/5bcb2bc4-f12b-447f-9e28-59ef1c70ac2a" alt="Screenshot 2" width="45%" style="margin: 10px;">
</p>

## Features in Action

### Sorting & Filtering
<p align="center">
    <img src="https://github.com/user-attachments/assets/b23d02a5-7cf7-48a9-85fd-85ee81b47f39.gif" alt="Sorting & Filtering" width="75%" style="margin: 10px;">
</p>

### Swipe Actions
<p align="center">
    <img src="https://github.com/user-attachments/assets/427a0728-4e0b-4597-acd7-3fcfb7efcc63.gif" alt="Swipe Actions" width="75%" style="margin: 10px;">
</p>

### Drag & Drop
<p align="center">
    <img src="https://github.com/user-attachments/assets/4a72720f-fa5b-417b-a129-854acbfe8ff7.gif" alt="Drag & Drop" width="75%" style="margin: 10px;">
</p>

## Setup

### Step 1: Add it in your root `build.gradle` at the end of repositories:
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

###### Filtering:
```java
customTableView.filterRows("searchTerm");
```

## Advanced Features

- **Undo/Redo**: Use `undoButton` and `redoButton` for state management.
- **Swipe Actions**: Swipe left to highlight a row, swipe right to delete.
- **Drag and Drop**: Long press and drag to reorder rows.
- **Dynamic Columns**: Add or remove columns dynamically.

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
