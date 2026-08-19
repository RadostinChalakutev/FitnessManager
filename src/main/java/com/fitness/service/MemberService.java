package com.fitness.service;

import com.fitness.database.MemberRepository;
import com.fitness.model.Member;
import com.fitness.model.Subscription;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.Base64;

public class MemberService {

    private final MemberRepository memberRepository =
            new MemberRepository();

    private final SecureRandom secureRandom =
            new SecureRandom();
    private final EmailService emailService =
            new EmailService();

    public void saveMember(
            String firstName,
            String lastName,
            String phone,
            String egn,
            String email,
            Subscription subscription,
            LocalDate startDate,
            String paymentMethod) {

        LocalDate endDate =
                startDate.plusMonths(
                        subscription.getDurationMonths()
                );

        Member member = new Member(
                firstName,
                lastName,
                phone,
                egn,
                email,
                subscription.getName(),
                startDate,
                endDate,
                paymentMethod,
                subscription.getPrice()
        );

        String verificationToken =
                generateVerificationToken();

        member.setVerificationToken(
                verificationToken
        );

        memberRepository.save(
                member,
                subscription.getId()
        );
        emailService.sendVerificationEmail(
                member.getEmail(),
                member.getFirstName(),
                member.getVerificationToken()
        );
    }

    public void updateMember(Member member) {

        memberRepository.updatePersonalData(member);
    }

    private String generateVerificationToken() {

        byte[] randomBytes = new byte[32];

        secureRandom.nextBytes(randomBytes);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(randomBytes);
    }
}