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
import com.example.quizcards.repository.IAppUserRepository;
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
        Long setId = test.getSetId();
        ArrayList<UserProgress> progressesUpdate = new ArrayList<>();
        progressesUpdate.ensureCapacity(500);
        List<IProgressDTO> currentProgresses = progressRepo.findUserProgressBySetIdAndUserId(
                setId, test.getUserId()
        );
        Map<String, IProgressDTO> mapProgresses = currentProgresses.stream()
                .collect(Collectors.toMap(p -> p.getUserId().toString() + p.getCardId().toString(),
                        progressDTO -> progressDTO));
        Long numQuestionsTrue = updateProcessAndCountTrueAnswers(mapProgresses, test.getQuestions(),
                progressesUpdate, test.getUserId());
        Query query = new Query(Criteria.where("testId").is(test.getTestId()));
        Update update = new Update().set("isEnded", true).set("numQuestionsTrue", numQuestionsTrue);
        mongoTemplate.updateFirst(query, update, TestData.class);
        progressRepo.saveAll(progressesUpdate);
    }

    private Long updateProcessAndCountTrueAnswers(Map<String, IProgressDTO> progressInSet,
                                                  List<IQuestion> questions,
                                                  List<UserProgress> progresses,
                                                  Long userId) {
        String userIdStr = String.valueOf(userId);
        long numQuestionsTrue = 0L;
        for (IQuestion question : questions) {
            Boolean isAnswerTrue = null;
            if (question instanceof MCQuestion mc) {
                isAnswerTrue = mc.getAnswerTrue();
            } else if (question instanceof ESQuestion es) {
                isAnswerTrue = es.getAnswerTrue();
            }
            numQuestionsTrue += Boolean.TRUE.equals(isAnswerTrue) ? 1 : 0;
            IProgressDTO progress = progressInSet.get(userIdStr + question.getCardId().toString());
            UserProgress up = UserProgress.builder()
                    .progressId(progress != null ? progress.getProgressId() : null)
                    .appUser(AppUser.builder().userId(userId).build())
                    .flashcard(Flashcard.builder().cardId(question.getCardId()).build())
                    .progressType(Boolean.TRUE.equals(isAnswerTrue))
                    .isAttention(progress != null ? progress.getIsAttention() : false)
                    .build();
            progresses.add(up);
        }
        return numQuestionsTrue;
    }
}
