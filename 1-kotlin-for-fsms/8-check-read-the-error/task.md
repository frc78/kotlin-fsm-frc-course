# Check: Read the Error

Errors are not failures. They are the tools telling you exactly where to
look. This course shows you two kinds.

## A compiler error

The compiler checks your code before it runs. In the IDE, the bad line is
underlined in red. Hover over it, or look in the **Build** panel, to read
the message. A teammate writes this line:

```kotlin
val batteryVolts: Double = "12.6"
```

The compiler reports:

```text
Initializer type mismatch: expected 'Double', actual 'String'.
```

## A failed check

When the compiler is happy, the **Check** button runs the hidden tests.
A failed test prints a line like this in the **Check** panel:

```
#educational_plugin FAILED + expected:<12.6> but was:<0.0>
```

The `expected` part is what the test wanted. The `but was` part is what
your code gave.

## The question

Read both messages above. Which statement explains what is wrong in each
case?
