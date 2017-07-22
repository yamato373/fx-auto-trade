--
CREATE DATABASE autotrade CHARACTER SET utf8;

--
USE autotrade;

--
CREATE TABLE `order_result` (
  `cl_ord_id` VARCHAR(10) NOT NULL,
  `status` VARCHAR(6) NOT NULL,
  `symbol` VARCHAR(7) NOT NULL,
  `side` VARCHAR(3) NOT NULL,
  `order_time` DATETIME NOT NULL,
  `order_qty` DECIMAL(11,3),
  `price` DECIMAL(9,5),
  `order_id` VARCHAR(45) DEFAULT NULL,
  `exec_id` VARCHAR(45) DEFAULT NULL,
  `exec_time` DATETIME DEFAULT NULL,
  `last_qty` DECIMAL(11,3),
  `last_px` DECIMAL(9,5),
  `rej_reason` VARCHAR(5) DEFAULT NULL,
  PRIMARY KEY (`cl_ord_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
CREATE TABLE `position` (
  `trap_px` DECIMAL(5,2) NOT NULL,
  `ask_cl_ord_id` VARCHAR(10) NOT NULL,
  `bid_cl_ord_id` VARCHAR(10) DEFAULT NULL,
  `buying_flg` BIT(1) NOT NULL,
  PRIMARY KEY (`trap_px`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
CREATE TABLE `position_history` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `trap_px` DECIMAL(5,2) NOT NULL,
  `ask_cl_ord_id` VARCHAR(10) NOT NULL,
  `bid_cl_ord_id` VARCHAR(10) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 付けない
ALTER TABLE position ADD FOREIGN KEY(ask_cl_ord_id) REFERENCES order_result(cl_ord_id);
ALTER TABLE position ADD FOREIGN KEY(bid_cl_ord_id) REFERENCES order_result(cl_ord_id);

--
DELETE FROM autotrade.order_result;

--
DROP TABLE order_result;