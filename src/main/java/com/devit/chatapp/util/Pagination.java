package com.devit.chatapp.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Pagination {
    private int page;
    private int size;
    private boolean firstPage;
    private boolean lastPage;
    private int numberOfElements;
}
