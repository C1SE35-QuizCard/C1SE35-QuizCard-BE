package com.example.quizcards.service.implement;

import com.example.quizcards.dto.IDeadlineReminderDTO;
import com.example.quizcards.dto.request.DeadlineReminderCreationRequest;
import com.example.quizcards.repository.ICollectionRepository;
import com.example.quizcards.repository.IDeadlineReminderRepository;
import com.example.quizcards.service.IDeadlineReminderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class DeadlineReminderServiceImpl implements IDeadlineReminderService {

    @Autowired
    private IDeadlineReminderRepository deadlineReminderRepository;

    public IDeadlineReminderDTO getDeadlineReminderById(int id){
        return deadlineReminderRepository.findDeadlineReminderById(id);
    }

    public List<IDeadlineReminderDTO> getDeadlineReminderByUserId(Long userId){
        return deadlineReminderRepository.findDeadlineReminderByUserId(userId);
    }

    public List<IDeadlineReminderDTO> getDeadlineReminderBySetId(int setId){
        return deadlineReminderRepository.findDeadlineReminderBySetId(setId);
    }

    public void addDeadlineReminder(Timestamp reminderTime, Long userId, int setId){
        deadlineReminderRepository.createDeadlineReminder(reminderTime, userId, setId);
    }
    public void deleteDeadlineReminder(int deadlineRemindersId){
        deadlineReminderRepository.deleteDeadlineReminder(deadlineRemindersId);
    }
    public void updateDeadlineReminder(DeadlineReminderCreationRequest request){
        deadlineReminderRepository.updateDeadlineReminder(request.getDeadlineRemindersId(), request.getReminderTime(), request.getUserId(), request.getSetId());
    }

    public boolean existsByUserIdAndSetId(Long userId, int setId){
        return deadlineReminderRepository.existsByUserIdAndSetId(userId, setId) != 0;
    }

    public boolean existsByUserIdAndSetIdAndNotId(Long userId, int setId, int deadlineRemindersId) {
        return deadlineReminderRepository.existsByUserIdAndSetIdAndNotId(userId, setId, deadlineRemindersId) > 0;
    }
}
