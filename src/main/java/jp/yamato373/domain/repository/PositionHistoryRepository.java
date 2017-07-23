package jp.yamato373.domain.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jp.yamato373.domain.model.entry.PositionHistory;

@Repository
public interface PositionHistoryRepository  extends JpaRepository<PositionHistory, Integer> {

	@Query("SELECT p FROM PositionHistory p WHERE :from <= p.settlTime AND p.settlTime < :to")
	List<PositionHistory> findBySettlTime(@Param("from") Date from, @Param("to") Date to);
}
