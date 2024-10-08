package dev.nimrod.customchart.Util;

import dev.nimrod.customchart.CustomTableView;

public class TableViewCaretaker {
    private TableViewMemento memento;

    public void saveState(CustomTableView tableView) {
        if (tableView != null) {
            memento = tableView.saveToMemento();
        }
    }

    public void restoreState(CustomTableView tableView) {
        if (memento != null && tableView != null) {
            tableView.restoreFromMemento(memento);
        }
    }

    public TableViewMemento getMemento() {
        return memento;
    }

    public void clearMemento() {
        memento = null;
    }

    public boolean hasSavedState() {
        return memento != null;
    }
}