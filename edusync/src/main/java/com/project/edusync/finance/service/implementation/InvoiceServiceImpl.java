package com.project.edusync.finance.service.implementation;

import com.project.edusync.common.exception.finance.InvoiceNotFoundException;
import com.project.edusync.common.exception.finance.StudentFeeMapNotFoundException;
import com.project.edusync.common.exception.finance.StudentNotFoundException;
import com.project.edusync.finance.dto.invoice.InvoiceResponseDTO;
import com.project.edusync.finance.mapper.InvoiceMapper;
import com.project.edusync.finance.model.entity.*;
import com.project.edusync.finance.model.enums.InvoiceStatus;
import com.project.edusync.finance.repository.*;
import com.project.edusync.finance.service.InvoiceService;
import com.project.edusync.uis.model.entity.Student;
import com.project.edusync.uis.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final StudentRepository studentRepository;
    private final StudentFeeMapRepository studentFeeMapRepository;
    private final FeeParticularRepository feeParticularRepository;
    private final InvoiceMapper invoiceMapper;
    // We don't need InvoiceLineItemRepository, as it will be saved by cascade.

    @Override
    @Transactional
    public InvoiceResponseDTO generateSingleInvoice(Long studentId) {
        // 1. Find the Student
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new StudentNotFoundException("Student not found with Student ID: " + studentId));

        // 2. Find the Student's Fee Map
        StudentFeeMap feeMap = studentFeeMapRepository.findByStudent_Id(studentId)
                .orElseThrow(() -> new StudentFeeMapNotFoundException("Student not Found with Student ID: " + studentId));

        // 3. Get the assigned Fee Structure
        FeeStructure feeStructure = feeMap.getFeeStructure();

        // 4. Get all particulars (line items) for that structure
        List<FeeParticular> particulars = feeParticularRepository.findByFeeStructure_Id(feeStructure.getId());

        // 5. Create the new Invoice
        Invoice invoice = new Invoice();
        invoice.setStudent(student);
        invoice.setIssueDate(LocalDate.now());
        invoice.setDueDate(LocalDate.now().plusDays(30)); // Default due date
        invoice.setStatus(InvoiceStatus.PENDING);
        invoice.setInvoiceNumber(generateInvoiceNumber());

        BigDecimal totalAmount = BigDecimal.ZERO;

        // 6. Create InvoiceLineItems from the particulars
        for (FeeParticular particular : particulars) {
            // TODO: Add logic here to check if this particular is due
            // (e.g., is it ONE_TIME and not yet invoiced? Is it MONTHLY?)
            // For now, we will add all particulars.

            InvoiceLineItem lineItem = new InvoiceLineItem();
            lineItem.setDescription(particular.getName());
            lineItem.setAmount(particular.getAmount());

            // Add the line item to the invoice (this sets the bidirectional link)
            invoice.addLineItem(lineItem);

            totalAmount = totalAmount.add(particular.getAmount());
        }

        invoice.setTotalAmount(totalAmount);

        // 7. Save the invoice (and its line items via CascadeType.ALL)
        Invoice savedInvoice = invoiceRepository.save(invoice);

        // 8. Map to DTO and return
        return invoiceMapper.toDto(savedInvoice);
    }

    @Override
    @Transactional
    public Page<InvoiceResponseDTO> getAllInvoices(Pageable pageable) {
        Page<Invoice> invoicePage = invoiceRepository.findAll(pageable);
        return invoicePage.map(invoiceMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public InvoiceResponseDTO getInvoiceById(Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new InvoiceNotFoundException("Invoice not found with invoice Id:" + invoiceId));
        return invoiceMapper.toDto(invoice);
    }

    // --- Private Helper Methods ---

    /**
     * Generates a unique invoice number.
     * TODO: This should be a robust, sequence-based generator.
     */
    private String generateInvoiceNumber() {
        // Simple, non-production-ready generator
        return "INV-" + System.currentTimeMillis();
    }
}
