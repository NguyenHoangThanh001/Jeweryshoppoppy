# Jeweryshoppoppy Introduction and Installation Guide
## Introduction
JeweryShopPoppy is a comprehensive e-commerce web application specializing in jewelry and gold products in the Vietnamese market. The system allows customers to conveniently browse, customize, and purchase various jewelry items online, while
ensuring fair price by calculating the cost based on size and material of the products. In addition, the system also integrates an internal staff management module designed to support business operations. This module includes employee management, work shift scheduling and attendance support.

## Functions
-Manage jewelry Product with dynamic price calculation.
-Manage shopping cart and customer order.
-Manage staff account and shift scheduling.
-Online payment gate with VNPay

## Setting up the project locally
-Before starting, make sure to install Git and Microsoft SQL Server, Docker Desktop. Installing Visual Studio Code and any Java IDE (e.g IntelliJ) is optional or as an alternative option. 
-First, clone the repository to the local machine.
-Then, run the create database script in MySQL studio 

### Docker method
-Note: There is a file called "DOCKER_SETUP.md" that tell the detail of deploying the project by using Docker

1. Fill in the value or secret in .env, you can also create a new .env if needed
2. Configure database source, username and password in application.properties
3. Run "docker compose up --build" in the root of the project
4. Afterward, there should be three localhost project running

### Alternative method
1. Install and Open Visual Studio Code and any Java IDE
2. Open front end file in visual studio code and back end folder in any Java IDE
3. Run the project

## Credit
-This project is a teamwork effort of five members, these will be included as git username

+NguyenHoangThanh001: Team Lead, UI/UX Designer, Admin dashboard

+Ghost-3107: Order System

+ultrakhaicraft: Product Management System and Authentication

+MotUika: Staff and Shift Management System

+soilangtu: UI/UX implementation and Payment Gate
