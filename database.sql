SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";
-- OMG LOL DATABASE STRUCTUREEEEESSS :D
-- --------------------------------------------------------

-- --------------------------------------------------------

--
-- Table structure for table `alias`
--

CREATE TABLE IF NOT EXISTS `alias` (
  `Name` varchar(64) NOT NULL,
  `IP` varchar(64) NOT NULL,
  `Time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `Logins` int(11) NOT NULL,
  KEY `Name` (`Name`),
  KEY `IP` (`IP`)
) DEFAULT CHARSET=latin1;

-- --------------------------------------------------------
--
-- Table structure for table `j2bans`
--

CREATE TABLE IF NOT EXISTS `j2bans` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(64) CHARACTER SET latin1 COLLATE latin1_general_ci NOT NULL,
  `reason` text NOT NULL,
  `admin` varchar(64) NOT NULL,
  `unbantime` bigint(20) DEFAULT '0',
  `timeofban` bigint(20) DEFAULT '0',
  `unbanned` tinyint(1) DEFAULT '0',
  `x` double NOT NULL,
  `y` double NOT NULL,
  `z` double NOT NULL,
  `pitch` float NOT NULL,
  `yaw` float NOT NULL,
  `world` varchar(32) NOT NULL,
  `server` tinyint(4) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) DEFAULT CHARSET=latin1 AUTO_INCREMENT=9 ;

-- --------------------------------------------------------

--
-- Table structure for table `reports`
--

CREATE TABLE IF NOT EXISTS `reports` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `user` varchar(32) CHARACTER SET latin1 COLLATE latin1_general_ci NOT NULL,
  `message` text COLLATE utf8_bin NOT NULL,
  `closed` tinyint(1) NOT NULL DEFAULT '0',
  `admin` varchar(32) CHARACTER SET latin1 COLLATE latin1_general_ci NOT NULL DEFAULT '""',
  `reason` text COLLATE utf8_bin,
  `world` varchar(32) COLLATE utf8_bin NOT NULL,
  `x` double NOT NULL,
  `y` double NOT NULL,
  `z` double NOT NULL,
  `pitch` float NOT NULL,
  `yaw` float NOT NULL,
  `server` tinyint(1) NOT NULL,
  `time` int(32) NOT NULL,
  PRIMARY KEY (`id`)
) DEFAULT CHARSET=utf8 COLLATE=utf8_bin AUTO_INCREMENT=7 ;

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE IF NOT EXISTS `users` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(60) NOT NULL,
  `group` varchar(16) NOT NULL,
  `flags` varchar(26) NOT NULL,
  `color` int(2) NOT NULL DEFAULT '10',
  `IRChost` varchar(80) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) DEFAULT CHARSET=latin1 AUTO_INCREMENT=5 ;

-- --------------------------------------------------------

--
-- Table structure for table `perms`
--

CREATE TABLE IF NOT EXISTS `perms` (
`server_id` tinyint(4) NOT NULL,
  `permission` varchar(255) NOT NULL,
  `value` bit(1) NOT NULL DEFAULT b'1',
  `flag` char(1) NOT NULL,
  PRIMARY KEY (`server_id`),
  KEY `permission` (`permission`,`flag`,`server_id`)
) DEFAULT CHARSET=latin1;

-- --------------------------------------------------------


--
-- Table structure for table `groups`
--

CREATE TABLE IF NOT EXISTS `groups` (
  `name` varchar(64) NOT NULL,
  `flags` varchar(64) NOT NULL,
  `server_id` int(1) NOT NULL
) DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `teleport`
--

CREATE TABLE IF NOT EXISTS `teleport` (
  `server_id` tinyint(1) NOT NULL,
  `warp_name` varchar(32) NOT NULL,
  `owner` varchar(16) NOT NULL,
  `world` varchar(32) NOT NULL,
  `x` double NOT NULL,
  `y` double NOT NULL,
  `z` double NOT NULL,
  `pitch` float NOT NULL,
  `yaw` float NOT NULL
) DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `notes`
--

CREATE TABLE IF NOT EXISTS `notes` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `from` varchar(16) NOT NULL,
  `to` varchar(16) NOT NULL,
  `message` text NOT NULL,
  `time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `adminBusiness` bit(1) NOT NULL,
  `received` bit(1) NOT NULL DEFAULT b'0',
  PRIMARY KEY (`id`)
) DEFAULT CHARSET=latin1 AUTO_INCREMENT=8 ;
