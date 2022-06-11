package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV3;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;


import java.sql.SQLException;

/**
 * transaction - Transaction Manager
 */
@Slf4j
@RequiredArgsConstructor
public class MemberServiceV3_1 {

    //private final DataSource dataSource; //dataSource를 직접 사용하면 jdbc에 직접적으로 의존적임.
    private final PlatformTransactionManager transactionManager;
    private final MemberRepositoryV3 memberRepository;

    public void accountTransfer(String fromId, String toId, int money) throws SQLException {
       //transaction 시작
        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());


        try{

            //비즈니스 로직
           bizLogic(fromId, toId, money);

           transactionManager.commit(status); //성공시 commit

        }catch (Exception e){
           transactionManager.rollback(status); // 실패시 롤백
            throw new IllegalStateException(e);
        }//release 해줄 필요 없음. 해줌.


    }

    private void bizLogic(String fromId, String toId, int money) throws SQLException {
        //비즈니스 로직
        Member fromMember = memberRepository.findById(fromId);
        Member toMember = memberRepository.findById(toId);

        memberRepository.update(fromId, fromMember.getMoney() - money);
        validation(toMember);
        memberRepository.update(toId, toMember.getMoney()+ money);
    }

    private void validation(Member toMember) {
        if (toMember.getMemberId().equals("ex")){
            throw new IllegalStateException("이체중 예외 발생");
        }
    }


}



