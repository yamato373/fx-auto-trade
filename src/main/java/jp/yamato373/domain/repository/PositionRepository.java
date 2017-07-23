package jp.yamato373.domain.repository;

import java.math.BigDecimal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import jp.yamato373.domain.model.entry.Position;

@Repository
public interface PositionRepository extends JpaRepository<Position, BigDecimal> {

	@Query("SELECT MIN(p.trapPx) FROM Position p")
	BigDecimal findMinFirstPx();

	Position findByAskClOrdId(Integer clOrdId);

	Position findByBidClOrdId(Integer clOrdId);
}
