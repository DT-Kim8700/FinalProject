package com.eunzzi.p0617;

public class DTOBoard {

    private int boardNumber;
    private String name, boardTitle;

    public DTOBoard(int boardNumber, String name, String boardTitle) {
        this.boardNumber = boardNumber;
        this.name = name;
        this.boardTitle = boardTitle;
    }

    public int getBoardNumber() {
        return boardNumber;
    }

    public String getName() {
        return name;
    }

    public String getBoardTitle() {
        return boardTitle;
    }

    public void setName(String name) {
        this.name = name;
    }
}
