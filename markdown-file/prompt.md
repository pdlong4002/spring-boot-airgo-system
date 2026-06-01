# Project Documentation & Interview Preparation Assistant

Analyze the entire project source code, folder structure, dependencies, Docker configuration, CI/CD configuration, README, and all project files.

Your tasks are to generate three files:

* README.md
* tech.md
* interview.md

---

# 1. Rewrite README.md

Rewrite the README professionally.

Requirements:

* Improve formatting and readability.
* Add suitable icons/emojis where appropriate.
* Explain:

  * Project overview
  * Features
  * Architecture
  * Tech stack
  * Installation
  * Running locally
  * Deployment
  * API documentation
* Use clean and recruiter-friendly language.
* Generate architecture diagrams using Markdown if useful.

---

# 2. Generate tech.md

Create a detailed learning document for every technology used in this project.

Do NOT simply list technologies.

For each technology, generate a complete study guide.

Examples of technologies that may appear:

* Java
* Spring Boot
* Spring Security
* Spring Cloud
* Eureka Server
* API Gateway
* OpenFeign
* Resilience4j
* Kafka
* Redis
* MySQL
* Docker
* Docker Compose
* Kubernetes
* JWT
* Maven
* JPA / Hibernate
* Microservices Architecture
* REST API
* CI/CD
* GitHub Actions

For EACH technology include:

## What is it?

Explain the technology simply.

## Why is it used?

Explain its purpose.

## Why was it used in THIS project?

Explain specifically based on the codebase.

## Core Concepts

Explain all important concepts.

## Common Interview Questions

Provide 10–20 interview questions.

## Sample Answers

Provide strong sample answers.

## Common Mistakes

Things beginners often misunderstand.

## Best Practices

Industry recommendations.

## Real Usage In This Project

Show where and how the technology is used inside the project.

The goal is that a developer can learn and revise every technology used in the project from this single file.

---

# 3. Generate interview.md

Create a complete interview preparation document.

Imagine you are a senior backend engineer interviewing the project owner.

Generate questions from multiple levels:

## HR Questions

Questions about the project motivation.

## Project Introduction Questions

How to introduce the project.

## Architecture Questions

Questions about system design.

## Microservices Questions

Questions related to service communication.

## Spring Boot Questions

Questions related to implementation.

## Database Questions

Questions related to MySQL and JPA.

## Redis Questions

Questions related to caching.

## Kafka Questions

Questions related to messaging.

## Docker Questions

Questions related to containerization.

## Security Questions

Questions related to JWT, authentication, authorization.

## Performance Questions

Questions related to optimization.

## Scalability Questions

Questions related to scaling the system.

## Production Questions

Questions about real-world deployment.

## Trade-off Questions

Questions such as:

* Why Kafka instead of RabbitMQ?
* Why Redis?
* Why Microservices instead of Monolith?
* Why FeignClient instead of RestTemplate?
* Why API Gateway?
* Why Eureka?

For EVERY question:

* Explain why the interviewer asks it.
* Provide a strong answer.
* Provide possible follow-up questions.

---

# Additional Requirement

Create a final section:

# Defending This Project In An Interview

Generate:

* Top 50 most likely interview questions.
* Strong sample answers.
* Weak answers to avoid.
* Tips for explaining architectural decisions.

The final output should help the project owner confidently explain and defend every technical decision used in the project.
