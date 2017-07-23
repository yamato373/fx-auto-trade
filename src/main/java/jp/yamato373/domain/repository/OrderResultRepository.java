package jp.yamato373.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import jp.yamato373.domain.model.entry.OrderResult;

@Repository
public interface OrderResultRepository extends JpaRepository<OrderResult, Integer> {
}