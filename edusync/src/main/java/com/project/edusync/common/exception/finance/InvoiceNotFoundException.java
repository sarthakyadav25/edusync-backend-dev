package com.project.edusync.common.exception.finance;

import com.project.edusync.common.exception.EdusyncException;

public class InvoiceNotFoundException extends EdusyncException {
    public InvoiceNotFoundException(String message) {
        super(message);
    }
}
