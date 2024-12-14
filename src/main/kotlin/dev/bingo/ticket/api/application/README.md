# Application Layer
The application layer, responsible for defining use case interfaces and their implementations. 
It serves as the core of the application logic, independent of infrastructure concerns.
- Contains input ports (interfaces) that define use cases, and output (interfaces).
- Contains use case implementations that fulfill these interfaces.
- Application use cases can be invoked by any input mechanism (REST, gRPC, CLI, etc.).
- Submodules representing bounded contexts or domains.