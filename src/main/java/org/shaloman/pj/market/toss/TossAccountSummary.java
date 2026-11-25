package org.shaloman.pj.market.toss;

/** 계좌 목록 내부 모델입니다. 화면에 필요한 accountSeq와 유형만 전달하고 계좌번호는 뒤 4자리만 보입니다. */
public record TossAccountSummary(String accountSeq, String accountType, String maskedAccountNo) {}
