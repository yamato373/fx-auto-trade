package jp.yamato373.domain.repository;

import jp.yamato373.domain.model.Rate;
import jp.yamato373.domain.model.Rate.Entry;
import jp.yamato373.uitl.FxEnums.Side;

public interface RateRepository {

	void set(String symbol, Rate rate);

	Rate get(String symbol);

	void clear(String symbol);

	void clearAll();

	Entry getEntry(String symbol, Side side);

}
