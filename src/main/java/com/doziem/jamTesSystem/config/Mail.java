package com.doziem.jamTesSystem.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Mail {

    private String from;
    private String[] to;
    private String subject;
    private String link;
    private String buttonTest;
    private String name;


    public Mail(String from, String[] to, String subject, String link ) {
        this.from = from;
        this.to = to;
        this.subject = subject;
        this.link = link;
    }

    public Mail(String from, String[] to, String subject) {
        this.from = from;
        this.to = to;
        this.subject = subject;
    }


    public Mail(String from, String[] to, String subject, String link, String buttonTest) {
        this.from = from;
        this.to = to;
        this.subject = subject;
        this.link = link;
        this.buttonTest = buttonTest;

    }
}
