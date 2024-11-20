package com.example.quizcards.repository;

import com.example.quizcards.entities.ExamDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface IExamDetailRepository extends JpaRepository<ExamDetail, Long> {
    @Query(value = """
            select count(1)
            from exam_details e
            where e.ex_id = :id
            """, nativeQuery = true)
    Integer countExamDetailById(@Param("id") Long examDetailId);
}
