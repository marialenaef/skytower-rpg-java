# SkyTower: Turn-Based Combat & RPG Engine in Java

A text-driven, modular turn-based RPG combat system developed in **Java**. The project focuses on clean Object-Oriented Programming (OOP) design patterns, robust entity state management, and extensible turn-based combat mechanics.

---

## 🎮 Core Gameplay Mechanics
- **Turn-Based Combat Loop:** Structured turn cycle managing actions between player and adversaries.
- **Combat Calculus & Attributes:** Dynamic damage scaling, critical hit chances, defense mitigation, and resource consumption.
- **Character Hierarchy:** Extensible entity architecture modeling player archetypes, monster variants, and scaling enemy behaviors.
- **State & Action Validation:** Turn execution guardrails preventing invalid moves and managing status transitions.

---

## 🏛️ OOP Architecture & Design Patterns
- **Inheritance & Polymorphism:** Abstract base entity classes extended by specialized concrete classes  for shared mechanics.
- **Encapsulation:** Strict access modifiers protecting internal entity attributes, utilizing validated getters/setters for state mutations.
- **Separation of Concerns:** Clear demarcation between combat orchestration logic, entity models, and CLI user interactions.

---

## 🛠️ Tech Stack & Requirements
- **Language:** Java (JDK 17+)
- **Build / Execution:** Standard Java Compiler (`javac`) & JVM

---

## 🚀 How to Build & Run

### 1. Clone the repository
```bash
git clone [https://github.com/marialenaef/skytower-rpg-java.git](https://github.com/marialenaef/skytower-rpg-java.git)
cd skytower-rpg-java
