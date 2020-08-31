CREATE DATABASE demo CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `demo`;

DROP TABLE IF EXISTS `favourite`;
DROP TABLE IF EXISTS `purchase_history`;
DROP TABLE IF EXISTS `app_user`;
DROP TABLE IF EXISTS `menu`;
DROP TABLE IF EXISTS `open_hours`;
DROP TABLE IF EXISTS `restaurant`;

CREATE TABLE `restaurant` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(128) NOT NULL,
  `cash_balance` decimal(12, 2),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE TABLE `open_hours` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `restaurant_id` int(11) NOT NULL,
  `day_of_week` int(11),
  `open_time` int(11),
  `closed_time` int(11),
  `open_period` int(11),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE TABLE `menu` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `restaurant_id` int(11) NOT NULL,
  `dish_name` varchar(500) NOT NULL,
  `price` decimal(12, 2),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE TABLE `app_user` (
  `id` int(11) NOT NULL,
  `name` varchar(128) NOT NULL,
  `cash_balance` decimal(12, 2),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE TABLE `purchase_history` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11),
  `restaurant_id` int(11),
  `restaurant_name` varchar(128),
  `menu_id` int(11),
  `dish_name` varchar(500),
  `transaction_amount` decimal(12, 2),
  `transaction_date` timestamp NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE TABLE `favourite` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11),
  `restaurant_id` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;