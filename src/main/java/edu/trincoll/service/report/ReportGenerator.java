// Taha

package edu.trincoll.service.report;

public interface ReportGenerator {
    String getType();       // e.g., "overdue", "available", "members"
    String generateReport();
}
