//Taha
package edu.trincoll.service.report;

import edu.trincoll.repository.MemberRepository;
import org.springframework.stereotype.Component;

@Component
public class MembersReportGenerator implements ReportGenerator {
    private final MemberRepository memberRepository;

    public MembersReportGenerator(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override public String getType() { return "members"; }

    @Override
    public String generateReport() {
        long totalMembers = memberRepository.count();
        return "Total members: " + totalMembers;
    }
}
