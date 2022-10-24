package org.example.web.dto;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

public class BookSizeToRemove {

    @NotNull
    @Positive
    @Digits(integer = 4, fraction = 0)
    private Integer size;

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }
}
