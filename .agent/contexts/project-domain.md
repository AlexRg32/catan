# Project Domain Context

> **Note to the Developer**: This file is the primary source of truth for the AI agents to understand what your application is about. You must describe your core business rules, entities, and flow here. The agents (`@backend`, `@frontend`, `@architect`) are pre-configured to build a **Spring Boot + React** application, but they rely on this file to know *what* to build.

## Project Name

[Insert your project name here, e.g., Acme E-Commerce]

## Description

[A one-sentence description of the project, e.g., A platform for users to buy and sell premium mechanical keyboards.]

## Core Entities

[List the main objects in your system]

- **User**: [Description, e.g., Can register, login, and have roles like BUYER or SELLER.]
- **Product**: [Description, e.g., Created by sellers, has a price, description, and images.]
- **Order**: [Description, e.g., Connects a User with multiple Products, tracking payment state.]

## Key Business Rules

[List the absolutely non-negotiable rules of your domain]

1. E.g., A user cannot buy their own products.
2. E.g., Products must have at least one image before being published.
3. E.g., Stock must be reduced transactionally upon order placement.

## User Roles & Permissions

- **Admin**: [What they can do]
- **Standard User**: [What they can do]
- **Guest**: [What they can see without logging in]

## Important Technical Decisions (Specific to this project)

[List any deviation from the standard Spring Boot / React setup, or specific integrations like Stripe for payments, AWS S3 for images, etc.]

- Payment Gateway: [e.g., Stripe]
- Email Provider: [e.g., SendGrid]
