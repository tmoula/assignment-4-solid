package edu.trincoll.service;

import com.trincoll.library.model.Member;
import com.trincoll.library.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public Optional<Member> findById(Long memberId) {
        return memberRepository.findById(memberId);
    }

    public Optional<Member> findByEmail(String email) {
        return memberRepository.findByEmail(email);
    }

    @Transactional
    public Member incrementCheckoutCount(Member member) {
        if (member == null) throw new IllegalArgumentException("member cannot be null");
        member.setBooksCheckedOut(member.getBooksCheckedOut() + 1);
        return memberRepository.save(member);
    }

    @Transactional
    public Member decrementCheckoutCount(Member member) {
        if (member == null) throw new IllegalArgumentException("member cannot be null");
        int current = Math.max(0, member.getBooksCheckedOut() - 1);
        member.setBooksCheckedOut(current);
        return memberRepository.save(member);
    }

    /**
     * Validates the member (simple example). Extend with business rules as needed.
     */
    public boolean isValid(Member member) {
        if (member == null) return false;
        if (member.getId() == null) return false;
        if (member.getStatus() == null) return false;
        // add more validation rules as necessary
        return true;
    }

    @Transactional
    public Member save(Member member) {
        return memberRepository.save(member);
    }
}