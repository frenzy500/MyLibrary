package com.example.mylibrary;

public class Book {
    public String title;
    public String author;
    public int pages;
    public int pagesRead;

    public Book() {
    }

    public Book(String title, String author, int pages, int pagesRead) {
        this.title = title;
        this.author = author;
        this.pages = pages;
        this.pagesRead = pagesRead;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public int getPagesRead() {
        return pagesRead;
    }

    public void setPagesRead(int pagesRead) {
        this.pagesRead = pagesRead;
    }
}
