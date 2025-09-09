package com.vardhan.healthcare.repo;
import com.vardhan.healthcare.model.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReportRepo extends JpaRepository<Report, Long> {
  List<Report> findByPatientId(Long patientId);
}
