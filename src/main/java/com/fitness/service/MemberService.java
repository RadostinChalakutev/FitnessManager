package com.fitness.service;

import com.fitness.database.MemberRepository;
import com.fitness.model.Member;
import com.fitness.model.Subscription;

import java.time.LocalDate;

public class MemberService {

    private final MemberRepository memberRepository =
            new MemberRepository();

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

        memberRepository.save(
                member,
                subscription.getId()
        );
    }
}