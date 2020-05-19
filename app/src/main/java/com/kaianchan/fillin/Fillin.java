package com.kaianchan.fillin;

public class Fillin {
    String fillinQu;
    String fillinAns;
    int fillinChapter;

    public Fillin(String fillinQu, String fillinAns, int fillinChapter) {
        this.fillinQu = fillinQu;
        this.fillinAns = fillinAns;
        this.fillinChapter = fillinChapter;
    }

    public String getFillinQu() {
        return fillinQu;
    }

    public void setFillinQu(String fillinQu) {
        this.fillinQu = fillinQu;
    }

    public String getFillinAns() {
        return fillinAns;
    }

    public void setFillinAns(String fillinAns) {
        this.fillinAns = fillinAns;
    }

    public int getFillinChapter() {
        return fillinChapter;
    }

    public void setFillinChapter(int fillinChapter) {
        this.fillinChapter = fillinChapter;
    }
}
