package org.dnd.timeet.agenda.domain;

public enum AgendaAction {
    START, PAUSE, RESUME, END, MODIFY;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
