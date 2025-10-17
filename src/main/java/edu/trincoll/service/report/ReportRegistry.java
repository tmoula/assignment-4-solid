package edu.trincoll.service.report;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ReportRegistry {
    private final Map<String, ReportGenerator> byType;

    public ReportRegistry(java.util.List<ReportGenerator> generators) {
        this.byType = generators.stream()
                .collect(Collectors.toMap(g -> g.getType().toLowerCase(), g -> g));
    }

    public ReportGenerator get(String type) {
        ReportGenerator gen = byType.get(type.toLowerCase());
        if (gen == null) throw new IllegalArgumentException("Invalid report type");
        return gen;
    }
}
