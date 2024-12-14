# Use Case Module
- Contains the concrete implementations of the use case interfaces from port.input.usecase.
- These implementations handle the coordination of domain logic, error mapping, and any application-specific workflows.
- Use Case Implementations:
  - These go in the application.usecase (or application.service) layer.
  - They implement the contracts defined in port.input.usecase.
  - They coordinate business logic by invoking domain services, orchestrating workflows, handling errors, etc.
  - If needed, they can use @Transactional to handle persistence boundaries.