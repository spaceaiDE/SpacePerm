package de.spaceai.spaceperms.util;

import lombok.Getter;

@Getter
public enum MESSAGES {

    PREFIX("§bSpacePerms §8× §7");

    private String text;

    MESSAGES(String text) {
        this.text = text;
    }

}
