package edu.trincoll.service;

import edu.trincoll.model.Member;
import edu.trincoll.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MemberService Unit Tests")
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private MemberService memberService;

    private Member member;

    @BeforeEach
    void setup() {
        member = new Member();
        member.setId(1L);
        member.setEmail("john@example.com");
        member.setBooksCheckedOut(2);
        member.setStatus("ACTIVE");
    }

    @Test
    void testFindById() {
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        Optional<Member> result = memberService.findById(1L);
        assertThat(result).isPresent();
        verify(memberRepository).findById(1L);
    }

    @Test
    void testFindByEmail() {
        when(memberRepository.findByEmail("john@example.com")).thenReturn(Optional.of(member));
        Optional<Member> result = memberService.findByEmail("john@example.com");
        assertThat(result).isPresent();
        verify(memberRepository).findByEmail("john@example.com");
    }

    @Test
    void testIncrementCheckoutCount() {
        when(memberRepository.save(any(Member.class))).thenAnswer(inv -> inv.getArgument(0));
        Member updated = memberService.incrementCheckoutCount(member);
        assertThat(updated.getBooksCheckedOut()).isEqualTo(3);
        verify(memberRepository).save(any(Member.class));
    }

    @Test
    void testDecrementCheckoutCount() {
        when(memberRepository.save(any(Member.class))).thenAnswer(inv -> inv.getArgument(0));
        Member updated = memberService.decrementCheckoutCount(member);
        assertThat(updated.getBooksCheckedOut()).isEqualTo(1);
        verify(memberRepository).save(any(Member.class));
    }

    @Test
    void testIsValid() {
        assertThat(memberService.isValid(member)).isTrue();
        assertThat(memberService.isValid(null)).isFalse();
    }

    @Test
    void testSave() {
        when(memberRepository.save(member)).thenReturn(member);
        Member saved = memberService.save(member);
        assertThat(saved).isEqualTo(member);
        verify(memberRepository).save(member);
    }
    @Test
    void incrementCheckoutCount_null_throws() {
        assertThatThrownBy(() -> memberService.incrementCheckoutCount(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("member cannot be null");
    }

    // 2) decrement: null member → throws
    @Test
    void decrementCheckoutCount_null_throws() {
        assertThatThrownBy(() -> memberService.decrementCheckoutCount(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("member cannot be null");
    }

    // 3) decrement: at zero → stays zero (covers Math.max branch)
    @Test
    void decrementCheckoutCount_whenZero_staysZero() {
        member.setBooksCheckedOut(0);
        when(memberRepository.save(any(Member.class))).thenAnswer(inv -> inv.getArgument(0));

        Member updated = memberService.decrementCheckoutCount(member);

        assertThat(updated.getBooksCheckedOut()).isEqualTo(0);
        verify(memberRepository).save(any(Member.class));
    }



}
