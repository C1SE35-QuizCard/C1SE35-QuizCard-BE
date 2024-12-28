package com.example.quizcards.service.impl;

import com.example.quizcards.dto.IProgressDTO;
import com.example.quizcards.entities.AppUser;
import com.example.quizcards.entities.Flashcard;
import com.example.quizcards.entities.TestDataPackage.ESQuestion;
import com.example.quizcards.entities.TestDataPackage.IQuestion;
import com.example.quizcards.entities.TestDataPackage.MCQuestion;
import com.example.quizcards.entities.TestDataPackage.TestData;
import com.example.quizcards.entities.UserProgress;
import com.example.quizcards.helpers.TestHelpers.TestSocketSession;
import com.example.quizcards.repository.ITestDataMongoDbRepo;
import com.example.quizcards.repository.IUserProgressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TestSubmitServiceImpl {
    @Autowired
    private ITestDataMongoDbRepo mongoDbRepo;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private IUserProgressRepository progressRepo;

    public void submitTest(Long testId) {
        TestData test = mongoDbRepo.findByTestId(testId);
        if (test == null || test.getIsEnded()) {
            return;
        }
        long numQuestionsTrue = 0;
        Long setId = test.getSetId();
        List<IProgressDTO> progressInSetInUsers = progressRepo.findUserProgressBySetIdAndUserId(
                setId, test.getUserId()
        );
        Map<Long, IProgressDTO> progressInSet = progressInSetInUsers.stream()
                .collect(Collectors.toMap(p -> p.getUserId() + p.getCardId(), progressDTO -> progressDTO));
        ArrayList<UserProgress> listProgressUpdates = new ArrayList<>();
        listProgressUpdates.ensureCapacity(progressInSet.size());

        for (IQuestion question : test.getQuestions()) {
            Boolean isAnswerTrue = null;
            if (question instanceof MCQuestion mc) {
                isAnswerTrue = mc.getAnswerTrue();
            } else if (question instanceof ESQuestion es) {
                isAnswerTrue = es.getAnswerTrue();
            }
            numQuestionsTrue += Boolean.TRUE.equals(isAnswerTrue) ? 1 : 0;
            IProgressDTO progress = progressInSet.get(test.getUserId() + question.getCardId());
            UserProgress up = UserProgress.builder()
                    .progressId(progress != null ? progress.getProgressId() : null)
                    .appUser(AppUser.builder().userId(test.getUserId()).build())
                    .flashcard(Flashcard.builder().cardId(question.getCardId()).build())
                    .progressType(Boolean.TRUE.equals(isAnswerTrue))
                    .isAttention(progress != null ? progress.getIsAttention() : false)
                    .build();
            listProgressUpdates.add(up);
        }
        Query query = new Query(Criteria.where("testId").is(test.getTestId()));
        Update update = new Update().set("isEnded", true).set("numQuestionsTrue", numQuestionsTrue);
        mongoTemplate.updateFirst(query, update, TestData.class);
        progressRepo.saveAll(listProgressUpdates);
        TestSocketSession.shutdownTest(test.getTestId());
    }
//    private void cham() {
//        Map<BaiTest, Map<UserId, DapAn>> set = ... // cac dap an cua nguoi dung
//        Long idDung = -1;
//        MCQuestion mc;
//        for (MCQuestion.Answer answer : mc.getAnswerList()) {
//            if (answer.isTrue()) {
//                idDung = answer.getId();
//                break;
//            }
//        }
//        set.stream().filter(daAn.getId() == idDung) // trn tất c nguười dùng trả lời câu hoỏi , chọn ra những người
//                // luu cac tt vo db
//
//    }
}
