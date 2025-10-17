package edu.trincoll.service;

import edu.trincoll.repository.MemberRepository;
import edu.trincoll.service.report.MembersReportGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MembersReportGeneratorTest {

    @Mock
    MemberRepository memberRepository;

    @Test
    void countsMembers() {
        when(memberRepository.count()).thenReturn(3L);
        var gen = new MembersReportGenerator(memberRepository);
        assertEquals("Total members: 3", gen.generateReport());
    }
}
