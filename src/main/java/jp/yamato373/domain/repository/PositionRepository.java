package jp.yamato373.domain.repository;

import java.math.BigDecimal;
import java.util.Map;

import jp.yamato373.domain.model.OrderResult;
import jp.yamato373.domain.model.Position;

public interface PositionRepository {

	void add(OrderResult orderResult);

	void remove(OrderResult orderResult);

	void setBidOrder(BigDecimal trapPx, String bidClOrderId);

	Map<BigDecimal, Position> getAll();

}
