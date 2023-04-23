-- MySQL Script generated by MySQL Workbench
-- Wed Feb 22 10:17:31 2023
-- Model: New Model    Version: 1.0
-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------

USE `rummydb` ;

-- -----------------------------------------------------
-- Table `mydb`.`players`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `rummydb`.`players` (
  `id` INT NOT NULL,
  `name` VARCHAR(45) NULL,
  `password` VARCHAR(250) NOT NULL,
  `salt` VARCHAR(250) NOT NULL,
  `online` TINYINT NULL,
  `generalScore` INT NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC) VISIBLE)
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `mydb`.`games`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `rummydb`.`games` (
  `id` INT NOT NULL,
  `active` INT NOT NULL,
  `player1` INT NOT NULL,
  `player2` INT NULL,
  `name` VARCHAR(45) NULL,
  `winner` TINYINT(1) NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC) VISIBLE,
  INDEX `player1_idx` (`player1` ASC) VISIBLE,
  INDEX `player2_idx` (`player2` ASC) VISIBLE,
  CONSTRAINT `player1`
    FOREIGN KEY (`player1`)
    REFERENCES `rummydb`.`players` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `player2`
    FOREIGN KEY (`player2`)
    REFERENCES `rummydb`.`players` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
