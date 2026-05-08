# Hello, Kotlin

Welcome! This first task introduces the Kotlin features you'll use in every
later task: `val` / `var`, functions, `object` singletons, and string templates.

## Concepts

- **`val`** declares a read-only property. Once set, it can't be reassigned.
- **`var`** declares a mutable property. It *can* be reassigned later.
- **`fun`** declares a function. Single-expression functions can use `=` instead
  of `{ return ... }`.
- **`object`** declares a singleton — there's exactly one instance, accessible
  by its name. (FRC subsystems are usually written this way: there's only one
  intake, only one elevator.)
- **String templates** let you embed values in strings: `"Score: $score"` or
  `"Hello, ${player.name}!"`.

## Your task

Open `src/RobotIntro.kt`. The `RobotIntro` object has four members:

1. `teamName` — set the empty string to your FRC team's name (any non-empty
   string works).
2. `matchScore` — already initialized to `0`. Notice it's a `var`, so it can
   change during a match.
3. `greeting()` — return `"Hello from <teamName>!"` using a string template.
4. `describeScore()` — return `"Score: <matchScore>"` using a string template.

When the tests pass, you've got Kotlin's basic shapes down.

## Hints

- Inside a single-expression function: `fun foo(): String = "hi"`
- A string template inside an object's own method can reference the object's
  property directly: `"Hello from $teamName!"`
