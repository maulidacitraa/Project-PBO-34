-- phpMyAdmin SQL Dump
-- Cute Todo List Database Schema 🌸💖
-- Updated with completed field and important field

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";

-- Database: `todolist`

-- --------------------------------------------------------

-- Table structure for table `users`

CREATE TABLE IF NOT EXISTS `users` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(100) NOT NULL,
  `password` varchar(255) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

-- Table structure for table `todo`

CREATE TABLE IF NOT EXISTS `todo` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `title` varchar(200) NOT NULL,
  `description` text DEFAULT NULL,
  `date` datetime NOT NULL,
  `priority` enum('LOW','MEDIUM','HIGH') NOT NULL,
  `type` enum('TASK','ACTIVITY','EVENT') NOT NULL,
  `completed` tinyint(1) NOT NULL DEFAULT 0,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  KEY `idx_date` (`date`),
  KEY `idx_completed` (`completed`),
  CONSTRAINT `todo_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

-- Table structure for table `todo_activity`

CREATE TABLE IF NOT EXISTS `todo_activity` (
  `id` int(11) NOT NULL,
  `location` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `todo_activity_ibfk_1` FOREIGN KEY (`id`) REFERENCES `todo` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

-- Table structure for table `todo_event`

CREATE TABLE IF NOT EXISTS `todo_event` (
  `id` int(11) NOT NULL,
  `duration_hours` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `todo_event_ibfk_1` FOREIGN KEY (`id`) REFERENCES `todo` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

-- Table structure for table `todo_task`

CREATE TABLE IF NOT EXISTS `todo_task` (
  `id` int(11) NOT NULL,
  `important` tinyint(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  CONSTRAINT `todo_task_ibfk_1` FOREIGN KEY (`id`) REFERENCES `todo` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

-- Sample data (optional)

-- Insert sample user
INSERT INTO `users` (`username`, `password`) VALUES
('demo', 'demo123'),
('cutie', 'pink123');

-- Insert sample todos for demo user (user_id = 1)
INSERT INTO `todo` (`user_id`, `title`, `description`, `date`, `priority`, `type`, `completed`) VALUES
(1, 'Morning Workout', 'Do 30 minutes of yoga', '2024-12-10 07:00:00', 'HIGH', 'ACTIVITY', 0),
(1, 'Team Meeting', 'Discuss Q4 project goals', '2024-12-10 10:00:00', 'MEDIUM', 'EVENT', 0),
(1, 'Buy Groceries', 'Get fruits, vegetables, and milk', '2024-12-09 18:00:00', 'MEDIUM', 'TASK', 0),
(1, 'Read Book', 'Finish chapter 5 of novel', '2024-12-11 20:00:00', 'LOW', 'TASK', 0),
(1, 'Coffee with Friends', 'Meet at Starbucks downtown', '2024-12-12 15:00:00', 'LOW', 'ACTIVITY', 0);

-- Insert child table data
INSERT INTO `todo_activity` (`id`, `location`) VALUES
(1, 'Home Gym'),
(5, 'Starbucks Downtown');

INSERT INTO `todo_event` (`id`, `duration_hours`) VALUES
(2, 2);

INSERT INTO `todo_task` (`id`, `important`) VALUES
(3, 0),
(4, 1);

COMMIT;

-- ✨💖 Database schema ready! Happy organizing! 🌸💕