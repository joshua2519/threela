-- MySQL Script generated by MySQL Workbench
-- 06/25/15 14:39:08
-- Model: New Model    Version: 1.0
-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

-- -----------------------------------------------------
-- Schema threela
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema threela
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `threela` DEFAULT CHARACTER SET utf8 COLLATE utf8_unicode_ci ;
USE `threela` ;

-- -----------------------------------------------------
-- Table `threela`.`company` 公司基本資料
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `threela`.`company` (
  `StockId` VARCHAR(10) NOT NULL COMMENT '股票代號',
  `ComName` NVARCHAR(100) NOT NULL COMMENT '公司名稱',
  `Address` NVARCHAR(255) NULL COMMENT '地址',
  `OpenDate` Date NULL COMMENT '成立日期',
  `ListDate` Date NULL COMMENT '上市日期',
  `Capital`  bigint NULL COMMENT '實收資本額',
  `Chairman` NVARCHAR(50) NULL COMMENT '董事長',
  `GM` NVARCHAR(50) NULL COMMENT '總經理',
  `Lat` DECIMAL(10,6) NULL COMMENT '緯度',
  `Lon` DECIMAL(10,6) NULL COMMENT '經度',
  `geom` GEOMETRY NULL COMMENT 'location',
  `CountyId` INT NULL COMMENT '縣市代號',
  `TownId` INT NULL COMMENT '鄉鎮市區代號',
  PRIMARY KEY (`StockId`)  COMMENT '股票代號')
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `threela`.`trading` 每日成交資料
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `threela`.`trading` (
  `StockId` VARCHAR(10) NOT NULL COMMENT '股票代號',
  `TimeId` INT NOT NULL COMMENT '交易日期',
  `OpenPrice` DOUBLE NULL COMMENT '開盤價格',
  `ClosePrice` DOUBLE NULL COMMENT '收盤價格',
  `HighPrice` DOUBLE NULL COMMENT '最高價格',
  `LowPrice` DOUBLE NULL COMMENT '最低價格',
  `Volume` INT NULL COMMENT '成交量(股數)',
  `YieldRate` DOUBLE NULL COMMENT '殖利率',
  `PE` DOUBLE NULL COMMENT '本益比',
  PRIMARY KEY (`StockId`,`TimeId`)  COMMENT '')
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `threela`.`season` 每季資料
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `threela`.`season` (  
  `StockId` VARCHAR(10) NOT NULL COMMENT '',
  `Year` INT NOT NULL COMMENT '',
  `Season` INT NOT NULL COMMENT '1,2,3,4',
  `BookValue` DOUBLE NULL COMMENT '股票淨值',
  `EPS` DOUBLE NULL COMMENT '每股盈餘',
  `DebtRatio` DOUBLE NULL COMMENT '負債比例',
  PRIMARY KEY (`StockId`,`Year`,`Season`)  COMMENT '')
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `threela`.`month` 每月資料
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `threela`.`month` (  
  `StockId` VARCHAR(10) NOT NULL COMMENT '股票代號',
  `Year` INT NOT NULL COMMENT '年(西元)',
  `Month` INT NOT NULL COMMENT '1-12',
  `MonthRev` INT NULL COMMENT '月營收(千元)',
  `RevGrowthRate` DOUBLE NULL COMMENT '營收成長率(年)',
  PRIMARY KEY (`StockId`,`Year`,`Month`)  COMMENT '')
ENGINE = InnoDB
CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `threela`.`brokers` 券商公司基本資料
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `threela`.`brokers` (
  `BrokerId` VARCHAR(10) BINARY NOT NULL COMMENT '券商公司代號',
  `Name` NVARCHAR(100) NOT NULL COMMENT '券商名稱',
  `Address` NVARCHAR(255) NULL COMMENT '地址',
  `Phone` VARCHAR(50) NULL COMMENT '電話',  
  `OpenDate` date NULL COMMENT '開業日期',
  `CountyId` INT NULL COMMENT '縣市代號',
  `TownId` INT NULL COMMENT '鄉鎮市區代號',
  `Lat` DECIMAL(10,6) NULL COMMENT '緯度',
  `Lon` DECIMAL(10,6) NULL COMMENT '經度',
  `HeadId` VARCHAR(10) NOT NULL COMMENT '總公司Id',
  `geom` GEOMETRY NULL COMMENT '位置',
  `Type` INT NULL COMMENT '類型1:公股銀行2:外資3.國內自營商',
  `comments` nvarchar(255) NULL COMMENT '備註',
  PRIMARY KEY (`BrokerId`)  COMMENT '')
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `threela`.`headbrokers`
-- -----------------------------------------------------
-- CREATE TABLE IF NOT EXISTS `threela`.`headbrokers` (
--  `HeadId` VARCHAR(10) BINARY NOT NULL COMMENT '',
--  `Name` NVARCHAR(100) NULL COMMENT '',
--  `OpenDate` DATE NULL COMMENT '',
-- `Address` NVARCHAR(255) NULL COMMENT '',
--  `FullTitle` NVARCHAR(100) NULL COMMENT '',
--  `Phone` VARCHAR(50) NULL COMMENT '',
--  PRIMARY KEY (`HeadId`)  COMMENT '')
-- ENGINE = InnoDB
-- DEFAULT CHARACTER SET = utf8
-- COLLATE = utf8_unicode_ci;

-- -----------------------------------------------------
-- Table `threela`.`region` 行政區代號
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `threela`.`region` (
	`TownId` INT NOT NULL COMMENT '鄉鎮市區代號',
    `TownName` nvarchar(50) NOT NULL COMMENT '鄉鎮市區名稱',
    `CountyId` INT NOT NULL COMMENT '縣市代號',
    `CountyName` nvarchar(50) COMMENT '縣市名稱',
     PRIMARY KEY (`TownId`)  COMMENT '')
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;

-- -----------------------------------------------------
-- Table `threela`.`daily`  每日分點交易資料
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `threela`.`daily` (  
  `StockId` VARCHAR(10) NOT NULL COMMENT '股票代號',
  `BrokerId` VARCHAR(10) BINARY NOT NULL COMMENT '券商公司代號',
  `TimeId` int NOT NULL COMMENT '日期代號',
  `Buy` INT NULL COMMENT '買入張數',
  `Sell` INT NULL COMMENT '賣出張數',
  `BuyPrice` DOUBLE NULL COMMENT '買入平均價格',
  `SellPrice` DOUBLE NULL COMMENT '賣出平均價格',
  `Source` int NULL COMMENT '資料來源',
  PRIMARY KEY (`StockId`,`BrokerId`,`TimeId`)  COMMENT '')
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;

CREATE TABLE IF NOT EXISTS `threela`.`daily_tmp` (
  `Id` bigint not null  AUTO_INCREMENT comment '',
  `StockId` VARCHAR(10) NOT NULL COMMENT '股票代號',
  `BrokerId` VARCHAR(10) BINARY NOT NULL COMMENT '券商公司代號',
  `TimeId` int NOT NULL COMMENT '日期代號',
  `Buy` INT NULL COMMENT '買入張數',
  `Sell` INT NULL COMMENT '賣出張數',
  `BuyPrice` DOUBLE NULL COMMENT '買入平均價格',
  `SellPrice` DOUBLE NULL COMMENT '賣出平均價格',
  PRIMARY KEY (`Id`)  COMMENT '')
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;

-- 日期表格

CREATE table if not exists `threela`.`Time` (
	`TimeId` INT NOT NULL COMMENT '日期代號',
    `Year` INT Not NULL COMMENT '年',
    `Season` INT Not NULL COMMENT '季',
	`Month` INT Not NULL COMMENT '月',
    `Day` INT NOT NULL COMMENT '日',
    `Week` INT NOT NULL COMMENT '星期',
    `WeekOfYear` INT NOT NULL COMMENT '每年的第幾周',
    `WeekofMonth` INT NOT NULL COMMENT '每月的第幾周',
    `Date` Date not null COMMENT '',
    `TWSEOPEN` boolean not null COMMENT '是否開市',
    PRIMARY KEY (`TimeId`)  COMMENT '')
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;

-- 每日加權指數

CREATE table if not exists `threela`.`index` (
	`TimeId` int NOT NULL COMMENT '日期代號',
    `TAIEX` double Not NULL COMMENT '上市指數',
    `TPEX` double Not NULL COMMENT '上櫃指數',
    PRIMARY KEY (`TimeId`)  COMMENT ''	
)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;

-- 公司所屬產業類

CREATE table if not exists `threela`.`comindustry` (
	`StockId` varchar(10) NOT NULL COMMENT '公司代號',
    `SubIndustry` varchar(10) Not NULL COMMENT '產業鏈代號',    
    PRIMARY KEY (`StockId`,`SubIndustry`)  COMMENT ''	
)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;

-- 產業鏈代號

CREATE table if not exists `threela`.`IndustryType` (
	`MainIndustry` varchar(10) NOT NULL COMMENT '主產業代號',
    `SubIndustry` varchar(10) Not NULL COMMENT '產業鏈代號',    
    PRIMARY KEY (`MainIndustry`,`SubIndustry`)  COMMENT ''	
)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;

-- 期貨交易資料

CREATE table if not exists `threela`.`FuturesTrading` (
	`TimeId` INT NOT NULL COMMENT '日期代號',
    `MonthBuyer` INT Not NULL COMMENT '買方-特定法人合計',
	`MonthSeller` INT Not NULL COMMENT '賣方-特定法人合計',
    `TotalBuyer` INT NOT NULL COMMENT '買方-所有契約-特定法人合計',
    `TotalSeller` INT NOT NULL COMMENT '賣方-所有契約-特定法人合計',
    `TotalOpenPosition` INT NOT NULL COMMENT '全市場未沖銷部位數',
    PRIMARY KEY (`TimeId`)  COMMENT '')
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
