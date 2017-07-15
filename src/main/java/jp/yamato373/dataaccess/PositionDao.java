package jp.yamato373.dataaccess;

import java.math.BigDecimal;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import jp.yamato373.cache.PositionCache;
import jp.yamato373.order.model.OrderResult;
import jp.yamato373.trade.model.Position;
import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class PositionDao {

	@Autowired
	PositionCache positionCache;

	public Map<BigDecimal, Position> getAll(){
		return positionCache.getAll();
	}

	public void add(OrderResult orderResult){
		if (positionCache.getAll().containsKey(orderResult.getPrice())){
			log.warn("すでにポジションを持っています。ClOrdId:" + orderResult.getClOrdId());
			return;
		}
		positionCache.add(new Position(
				orderResult.getPrice(),
				orderResult.getLastPx(),
				orderResult.getLastQty(),
				orderResult.getClOrdId(),
				null));
		log.info("ポジションの追加をしました。trapPx:" + orderResult.getPrice() );
	}

	public void remove(OrderResult orderResult){
		Position position = positionCache.removeByClOrderId(orderResult.getClOrdId());
		if (position != null){
			log.info("ポジションの削除をしました。trapPx:" + position.getTrapPx());
		}else{
			log.warn("ポジションの削除を試みましたが既にありませんでした。orderResult:" + orderResult);
		}
	}

	public void setBidOrder(BigDecimal trapPx, String bidClOrderId) {
		Position position =  positionCache.get(trapPx);
		position.setBidClOrderId(bidClOrderId);
		positionCache.add(position);
	}
}
